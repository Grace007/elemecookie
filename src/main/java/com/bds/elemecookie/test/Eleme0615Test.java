package com.bds.elemecookie.test;

import com.bds.base.http.Response;
import com.bds.elemecookie.model.ElemeCookie;
import com.bds.elemecookie.model.ElemeCookieUser;
import com.bds.elemecookie.utils.ElemeHttpBase;
import com.bds.elemecookie.utils.HttpClientUtils;
import com.bds.elemecookie.utils.RegexUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.json.JSONObject;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.*;

/**
 * @author eli
 * @date 2018/6/15 18:17
 */
public class Eleme0615Test {

    /**
     * 每10min执行一次
     */
    @Test
    public void getTokenThread(){
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/bds/elemecookie/test/dataSource.xml");
        NutDao tDao = (NutDao) ctx.getBean("o2oDao");
        //取超时或者失效的token
        ElemeCookieUser elemeCookieUser = tDao.fetch(ElemeCookieUser.class, Cnd.where("token_status", "=", 3));

        if (elemeCookieUser == null) {
            return;
        }
        String login_url = "http://xapi.yika66.com/User/login?uName="+elemeCookieUser.getUsername()+"&pWord="+elemeCookieUser.getPassword()+"&Developer=&code=utf8";
        String result = "";
        try {
            Response response = ElemeHttpBase.get(login_url, "utf_8");
            result = response.getResult();
        }catch (Exception e){
            e.printStackTrace();
        }
        //1FB25E0F2A8C7633083D9E36EFBA061D&22.01&10&30&10&1&0&100581332&普通用户&22.01
        //token =1FB25E0F2A8C7633083D9E36EFBA061D
        //09DA286F7FB5E12C083D9E36EFBA061D
        if (StringUtils.isNotEmpty(result) && result.length() >=33){
            String token = result.substring(0,result.indexOf("&"));
            elemeCookieUser.setToken(token);
            elemeCookieUser.setToken_status(2);
            elemeCookieUser.setUpdate_time(new Date());
            tDao.update(elemeCookieUser);
        }else{
            elemeCookieUser.setToken_status(5);  //设置错误状态
            elemeCookieUser.setUpdate_time(new Date());
            tDao.update(elemeCookieUser);
        }
    }

    /**
     * 每3min执行一次
     */
    @Test
    public void getCookie(){
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/bds/elemecookie/test/dataSource.xml");
        NutDao tDao = (NutDao) ctx.getBean("o2oDao");
        //查询cookie中的可用cookie数量.如果大于3,则不用
        int available_count = tDao.count(ElemeCookie.class,Cnd.where("status","=",2));
        if (available_count >3) {
            return;
        }
        //取可以使用的token
        ElemeCookieUser elemeCookieUser = tDao.fetch(ElemeCookieUser.class, Cnd.where("token_status", "=", 2));
        int count = 1;
        List<String > phoneList = getPhoneNumber(elemeCookieUser,count,tDao);
        getCookie(phoneList,elemeCookieUser,tDao);


    }


    /**
     * 获得phoneList
     * @param count
     * @return
     */
    public List<String> getPhoneNumber(ElemeCookieUser elemeCookieUser, int count,NutDao tdao){
        String phone_url = "http://xapi.yika66.com/User/getPhone?ItemId=388&token="+elemeCookieUser.getToken()+"&code=utf8&Count="+count;
        String result = "";
        try {
            Response response = ElemeHttpBase.get(phone_url, "utf_8");
            result = response.getResult();
            if (result.contains("过期")){
                elemeCookieUser.setToken_status(3);
                tdao.update(elemeCookieUser);
                System.out.println("elemeCookieUser的token过期");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        result = 17024474305;17024474320;17024474324;
        System.out.println("result = " + result);
        if (StringUtils.isNotEmpty(result)){
            List<String> phoneList = new ArrayList<>();
            String [] phoneArray = result.split(";");
            for (int i=0;i<phoneArray.length;i++){
                String phone = phoneArray[i];
                if (StringUtils.isNotEmpty(phone)){
                    phoneList.add(phone);
                }
            }
            return phoneList;
        }
        return null;
    }

    public void getCookie(List<String> phoneList,ElemeCookieUser elemeCookieUser,NutDao tdao) {

        for (int i = 0; i < phoneList.size(); i++) {

            String phone= phoneList.get(i);

            Map<String, String> header = new HashMap<>();
            header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16A5288q NebulaSDK/1.8.100112 Nebula PSDType(1) AlipayDefined(nt:WIFI,ws:320|504|2.0) AliApp(AP/10.1.25.370) AlipayClient/10.1.25.370 Language/zh-Hans");
            header.put("Host", "h5.ele.me");
            header.put("Referer", "https://h5.ele.me/login/");
            header.put("Origin", "https://h5.ele.me");
            header.put("X-Shard", "loc=117.096602,36.64513");

            String captcha_value="";
            String captcha_hash="";
            String geohash="wwe22x51ke6g";
            String latitude="36.64513";
            String longitude="117.096602";
            String timestamp= String.valueOf(new Date().getTime()/1000);
            String locationName="银座花园银座购物广场(花园店)";



            Map<String, String> body = new HashMap<>();
            body.put("mobile", phone);
            body.put("captcha_value", captcha_value);
            body.put("captcha_hash", captcha_hash);
            body.put("geohash", geohash);
            body.put("latitude", latitude);
            body.put("longitude", longitude);
            body.put("timestamp", timestamp);
            body.put("locationName", locationName);



            String url = "https://h5.ele.me/restapi//eus/login/mobile_send_code";

            String result_json = "";
            try {
                result_json = HttpClientUtils.doPost(url, header, body);
            }catch (Exception e){
                e.printStackTrace();
            }
            //result_json = {"validate_token":"095083cc359a44145234e52644c9b3d2ecdb965ab8b32878b2d119a1e2041e98"}
            System.out.println("result_json = " + result_json);
            String validate_token_value = null;
            try {
                validate_token_value = new JSONObject(result_json).getString("validate_token");
            }catch(Exception e){
                e.printStackTrace();
            }
            //095083cc359a44145234e52644c9b3d2ecdb965ab8b32878b2d119a1e2041e98
            System.out.println("validate_token_value = " + validate_token_value);
            String alipay_device_id="6h5ve675nsr38cy";
            String alipay_code="049e252fb7b311e9a48f8d6636e7e9938d938f78688eeb45905cd72dd6852805";
            String auth_type="1";
            String validate_code="";
            String validate_token=validate_token_value;

            //拿到validate_token之后,进行登录操作
            //1.先拿到返回的验证码,50秒后放弃,一般30秒内取得
            for (int j= 0;j<10;j++) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String sms_url = "http://xapi.yika66.com/User/getMessage?token=" + elemeCookieUser.getToken() + "&ItemId=388&Phone=" + phone + "&code=utf8";
                String result = "";

                try {
                    Response response = ElemeHttpBase.get(sms_url, "utf_8");
                    result = response.getResult();
                    System.out.println("result = " + result);
                    if(StringUtils.isEmpty(result)) continue;
                    validate_code = RegexUtil.subString("您的验证码是","，在5分钟内有效",result);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (StringUtils.isNotEmpty(validate_code)) break;
            }



            //2.进行登录操作
            Map<String, String> login_body = new HashMap<>();
            login_body.put("mobile", phone);
            login_body.put("geohash", geohash);
            login_body.put("latitude", latitude);
            login_body.put("longitude", longitude);
            login_body.put("timestamp", timestamp);
            login_body.put("locationName", locationName);
            login_body.put("alipay_device_id", alipay_device_id);
            login_body.put("alipay_code", alipay_code);
            login_body.put("auth_type", auth_type);
            login_body.put("validate_code", validate_code);
            login_body.put("validate_token", validate_token);


            String login_url = "https://h5.ele.me/restapi//eus/login/alipay_login/by_mobile";

            Header[] response_headers = HttpClientUtils.doPost(login_url,header,login_body,"Set-cookie");
            System.out.println("response_headers = " + response_headers);
            for (Header response_header: response_headers) {
                System.out.println("response_header = " + response_header.getValue());
            }
            String track_id="";
            String USERID="";
            String SID="";

            for (Header response_header:response_headers) {
                String value = response_header.getValue();
                System.out.println("value = " + value);
                if (value.contains("track_id")){
                    track_id ="track_id="+ RegexUtil.subString("track_id=",";",value);
                }
                if (value.contains("USERID")){
                    USERID ="USERID="+RegexUtil.subString("USERID=",";",value);
                }
                if (value.contains("SID")){
                    SID = "SID="+RegexUtil.subString("SID=",";",value);
                }
            }
            String cookie = track_id+";"+USERID+";"+SID;

            System.out.println("cookie = " + cookie);
            System.out.println("track_id = " + track_id);
            System.out.println("USERID = " + USERID);
            System.out.println("SID = " + SID);

            ElemeCookie elemeCookie = new ElemeCookie();
            elemeCookie.setSID(SID);
            elemeCookie.setUSERID(USERID);
            elemeCookie.setTrack_id(track_id);
            elemeCookie.setCreate_time(new Date());
            elemeCookie.setUpdate_time(new Date());
            elemeCookie.setStatus(2);
            elemeCookie.setCookie(cookie);
            try {
                tdao.insert(elemeCookie);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 测试发现一个手机接收4个验证码,请求就返回400状态
     */
    @Test
    public void sendCode(){
        String phone= "17024474346";
        String token = "C20D21ACEF930C86083D9E36EFBA061D";

        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16A5288q NebulaSDK/1.8.100112 Nebula PSDType(1) AlipayDefined(nt:WIFI,ws:320|504|2.0) AliApp(AP/10.1.25.370) AlipayClient/10.1.25.370 Language/zh-Hans");
        header.put("Host", "h5.ele.me");
        header.put("Referer", "https://h5.ele.me/login/");
        header.put("Origin", "https://h5.ele.me");
        header.put("X-Shard", "loc=117.096602,36.64513");

        String captcha_value="";
        String captcha_hash="";
        String geohash="wwe22x51ke6g";
        String latitude="36.64513";
        String longitude="117.096602";
        String timestamp= String.valueOf(new Date().getTime()/1000);
        String locationName="银座花园银座购物广场(花园店)";


        Map<String, String> body = new HashMap<>();
        body.put("mobile", phone);
        body.put("captcha_value", captcha_value);
        body.put("captcha_hash", captcha_hash);
        body.put("geohash", geohash);
        body.put("latitude", latitude);
        body.put("longitude", longitude);
        body.put("timestamp", timestamp);
        body.put("locationName", locationName);


        String url = "https://h5.ele.me/restapi//eus/login/mobile_send_code";

        String result_json = "";
        try {
            result_json = HttpClientUtils.doPost(url, header, body);
        }catch (Exception e){
            e.printStackTrace();
        }
        //result_json = {"validate_token":"095083cc359a44145234e52644c9b3d2ecdb965ab8b32878b2d119a1e2041e98"}
        System.out.println("result_json = " + result_json);
        String validate_token_value = null;
        try {
            validate_token_value = new JSONObject(result_json).getString("validate_token");
        }catch(Exception e){
            e.printStackTrace();
        }
        //095083cc359a44145234e52644c9b3d2ecdb965ab8b32878b2d119a1e2041e98
        System.out.println("validate_token_value = " + validate_token_value);

        String alipay_device_id="6h5ve675nsr38cy";
        String alipay_code="049e252fb7b311e9a48f8d6636e7e9938d938f78688eeb45905cd72dd6852805";
        String auth_type="1";
        String validate_code="";
        String validate_token=validate_token_value;

        //拿到validate_token之后,进行登录操作
        //1.先拿到返回的验证码,50秒后放弃,一般30秒内取得
        for (int i= 0;i<10;i++) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String sms_url = "http://xapi.yika66.com/User/getMessage?token=" + token + "&ItemId=388&Phone=" + phone + "&code=utf8";
            String result = "";

            try {
                Response response = ElemeHttpBase.get(sms_url, "utf_8");
                result = response.getResult();
                System.out.println("result = " + result);
                if(StringUtils.isEmpty(result)) continue;
                validate_code = RegexUtil.subString("您的验证码是","，在5分钟内有效",result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (StringUtils.isNotEmpty(validate_code)) break;
        }



        //2.进行登录操作
        Map<String, String> login_body = new HashMap<>();
        login_body.put("mobile", phone);
        login_body.put("geohash", geohash);
        login_body.put("latitude", latitude);
        login_body.put("longitude", longitude);
        login_body.put("timestamp", timestamp);
        login_body.put("locationName", locationName);
        login_body.put("alipay_device_id", alipay_device_id);
        login_body.put("alipay_code", alipay_code);
        login_body.put("auth_type", auth_type);
        login_body.put("validate_code", validate_code);
        login_body.put("validate_token", validate_token);


        String login_url = "https://h5.ele.me/restapi//eus/login/alipay_login/by_mobile";

        Header[] response_headers = HttpClientUtils.doPost(login_url,header,login_body,"Set-cookie");
        System.out.println("response_headers = " + response_headers);
        for (Header response_header: response_headers) {
            System.out.println("response_header = " + response_header.getValue());
        }
        String track_id="";
        String USERID="";
        String SID="";

        for (Header response_header:response_headers) {
            String value = response_header.getValue();
            System.out.println("value = " + value);
            if (value.contains("track_id")){
                track_id ="track_id="+ RegexUtil.subString("track_id=",";",value);
            }
            if (value.contains("USERID")){
                USERID ="USERID="+RegexUtil.subString("USERID=",";",value);
            }
            if (value.contains("SID")){
                SID = "SID="+RegexUtil.subString("SID=",";",value);
            }
        }
        String cookie = track_id+";"+USERID+";"+SID;

        System.out.println("cookie = " + cookie);
        System.out.println("track_id = " + track_id);
        System.out.println("USERID = " + USERID);
        System.out.println("SID = " + SID);





    }
}
