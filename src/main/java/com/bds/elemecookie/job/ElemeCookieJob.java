package com.bds.elemecookie.job;

import com.bds.base.http.Response;
import com.bds.elemecookie.model.ElemeCookie;
import com.bds.elemecookie.model.ElemeCookieUser;
import com.bds.elemecookie.utils.ElemeHttpBase;
import com.bds.elemecookie.utils.HttpClientUtils;
import com.bds.elemecookie.utils.RegexUtil;
import com.bds.elemecookie.utils.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.json.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.*;

/**
 * @author eli
 * @date 2018/6/16 12:48
 */
public class ElemeCookieJob implements Job {
    private Logger logger =LoggerFactory.getLogger(getClass());
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("进入ElemeCookieJob...");
//        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/bds/elemecookie/test/dataSource.xml");
//        NutDao tDao = (NutDao) ctx.getBean("o2oDao");
        NutDao tDao= (NutDao) SpringContextUtil.getBean("o2oDao");
        //查询cookie中的可用cookie数量.如果大于60,则不用
        int available_count = tDao.count(ElemeCookie.class, Cnd.where("status","=",2));
        if (available_count >=200) {
            return;
        }
        //取可以使用的token
        ElemeCookieUser elemeCookieUser = tDao.fetch(ElemeCookieUser.class, Cnd.where("token_status", "=", 2));
        int count = 1;
        List<String > phoneList = getPhoneNumber(elemeCookieUser,count,tDao);
        if(elemeCookieUser==null){
            logger.info("未获取到可用的千万卡平台token");
        }else {
            getCookie(phoneList, elemeCookieUser, tDao);
        }
    }

    /**
     * 获得phoneList
     * @param count
     * @return
     */
    public List<String> getPhoneNumber(ElemeCookieUser elemeCookieUser, int count,NutDao tdao){
        String phone_url = "http://xapi.yika66.com/User/getPhone?ItemId=56206&token="+elemeCookieUser.getToken()+"&Count="+count;
        String result = "";
        try {
            Response response = ElemeHttpBase.get(phone_url, "gbk");
            result = response.getResult();
            if (result.contains("过期")){
                elemeCookieUser.setToken_status(3);
                tdao.update(elemeCookieUser);
                logger.info("elemeCookieUser的token过期");
            }
        }catch (Exception e){
            logger.info("获取手机号发生异常");
            e.printStackTrace();
        }
//        result = 17024474305;17024474320;17024474324;
        logger.info("获取手机号 返回result = " + result);
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
            String result_json=elemeBeforeLogin(header,body);
            //result_json = {"validate_token":"095083cc359a44145234e52644c9b3d2ecdb965ab8b32878b2d119a1e2041e98"}
            logger.info("调用eleme api发送验证码 返回result_json = " + result_json);
            //重试3次还获取不到 validate_token  换号码
            int retry=0;
            while(StringUtils.isEmpty(result_json)&&retry++<3){
                logger.info("调用eleme api发送验证码 返回Null 第 "+retry+" 次重试");
                result_json=elemeBeforeLogin(header,body);
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(StringUtils.isEmpty(result_json)){
                continue;
            }
            String validate_token_value = null;
            try {
                validate_token_value = new JSONObject(result_json).getString("validate_token");
            }catch(Exception e){
                e.printStackTrace();
            }
            //095083cc359a44145234e52644c9b3d2ecdb965ab8b32878b2d119a1e2041e98
            logger.info("validate_token_value = " + validate_token_value);
            String alipay_device_id="6h5ve675nsr38cy";
            String alipay_code="049e252fb7b311e9a48f8d6636e7e9938d938f78688eeb45905cd72dd6852805";
            String auth_type="1";
            String validate_code="";
            String validate_token=validate_token_value;

            //拿到validate_token之后,进行登录操作
            //1.先拿到返回的验证码,50秒后放弃,一般30秒内取得,如果长时间没有取得可能是eleme后台封手机号了
            for (int j= 0;j<5;j++) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String sms_url = "http://xapi.yika66.com/User/getMessage?token=" + elemeCookieUser.getToken() + "&ItemId=56206&Phone=" + phone;
                String result = "";

                try {
                    Response response = ElemeHttpBase.get(sms_url, "gbk");
                    result = response.getResult();
                    logger.info("从千万卡平台获取验证码返回result = " + result);
                    if(StringUtils.isEmpty(result)) continue;
                    validate_code = RegexUtil.subString("您的验证码是","，在5分钟内有效",result);

                } catch (SocketTimeoutException e) {
                    elemeCookieUser.setToken_status(3);
                    tdao.update(elemeCookieUser);
                    logger.info("从千万卡平台获取验证码异常");
                } catch (Exception e){
                    e.printStackTrace();
                    logger.info("从千万卡平台获取验证码异常");
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

            try {
                Header[] response_headers = HttpClientUtils.doPost(login_url, header, login_body, "Set-cookie");
                logger.info("模拟eleme登录 返回请求头： " + response_headers);
                for (Header response_header : response_headers) {
                    logger.info("response_header = " + response_header.getValue());
                }
                String track_id = "";
                String USERID = "";
                String SID = "";

                for (Header response_header : response_headers) {
                    String value = response_header.getValue();
                    logger.info("value = " + value);
                    if (value.contains("track_id")) {
                        track_id = "track_id=" + RegexUtil.subString("track_id=", ";", value);
                    }
                    if (value.contains("USERID")) {
                        USERID = "USERID=" + RegexUtil.subString("USERID=", ";", value);
                    }
                    if (value.contains("SID")) {
                        SID = "SID=" + RegexUtil.subString("SID=", ";", value);
                    }
                }
                String cookie = track_id + ";" + USERID + ";" + SID;

                logger.info("cookie = " + cookie);
                logger.info("track_id = " + track_id);
                logger.info("USERID = " + USERID);
                logger.info("SID = " + SID);

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            System.out.println("开始释放"+phone+"并拉黑");
            //3.把手机号释放并拉黑
            String black_url ="http://xapi.yika66.com/User/addBlack?token="+elemeCookieUser.getToken()+"&phoneList="+56206+"-"+phone;
            String over_url ="http://xapi.yika66.com/User/releasePhone?token="+elemeCookieUser.getToken()+"&phoneList="+phone+"-"+56206;
            String result = "";
            try {
                Response response = ElemeHttpBase.get(over_url, "gbk");
                result = response.getResult();
                System.out.println("释放result = " + result);
                if (result.contains("ok")){
                    System.out.println(phone+"释放成功");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            result = "";
            try {
                Response response = ElemeHttpBase.get(black_url, "gbk");
                result = response.getResult();
                System.out.println("拉黑result = " + result);
                if (result.contains("ok")){
                    System.out.println(phone+"添加黑名单成功");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String elemeBeforeLogin(Map<String, String> header,Map<String, String> body){
        try {
            Thread.sleep(1000);
        }catch (Exception e){

        }
        String url = "https://h5.ele.me/restapi//eus/login/mobile_send_code";

        String result_json = "";
        try {
            result_json = HttpClientUtils.doPost(url, header, body);
        }catch (Exception e){
            logger.info("调用eleme api发送验证码时发生异常");
            e.printStackTrace();
        }
        return result_json;
    }
}
