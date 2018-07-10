package com.bds.elemecookie.test;


import com.bds.elemecookie.model.ElemeCookie;
import com.bds.elemecookie.utils.HttpClientUtils;
import com.bds.elemecookie.utils.RegexUtil;
import org.apache.http.Header;
import org.json.JSONObject;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 * @date 2018/6/14 10:31
 */
public class ElemeALiPayTest {
    @Test
    public void test01() throws Exception{
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/bds/elemecookie/dataSource.xml");
        NutDao tDao = (NutDao) ctx.getBean("o2oDao");

        List<ElemeCookie> list = tDao.query(ElemeCookie.class, Cnd.where("status", "=", 3).limit(1));

        for (int i=0;i<list.size();i++){
            ElemeCookie elemeCookie = list.get(i);
            Map<String ,String > header = new HashMap<>();
            header.put("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/16A5288q NebulaSDK/1.8.100112 Nebula PSDType(1) AlipayDefined(nt:WIFI,ws:320|504|2.0) AliApp(AP/10.1.25.370) AlipayClient/10.1.25.370 Language/zh-Hans");
            header.put("Host","h5.ele.me");
            header.put("Referer","https://h5.ele.me");
            header.put("Origin","https://h5.ele.me");
            String ali_request_body = elemeCookie.getAli_request_body();

            JSONObject ali_request_body_json = new JSONObject(ali_request_body);
            String alipay_device_id=ali_request_body_json.getString("alipay_device_id");
            String alipay_code=ali_request_body_json.getString("alipay_code");
            String auth_type=ali_request_body_json.getString("auth_type");
            Double longitude=ali_request_body_json.getDouble("longitude");
            Double latitude=ali_request_body_json.getDouble("latitude");
            String locationName=ali_request_body_json.getString("locationName");
            String geohash=ali_request_body_json.getString("geohash");
            Long timestamp=ali_request_body_json.getLong("timestamp");



            Map<String ,String > body = new HashMap<>();
            body.put("alipay_device_id",alipay_device_id);
            body.put("alipay_code",alipay_code);
            body.put("auth_type",auth_type);
            body.put("longitude",longitude.toString());
            body.put("latitude",latitude.toString());
            body.put("locationName",locationName);
            body.put("geohash",geohash);
            body.put("timestamp",timestamp.toString());


            String url = "https://h5.ele.me/restapi/eus/login/alipay_login";

            Header[] response_headers = HttpClientUtils.doPost(url,header,body,"Set-cookie");
            System.out.println("response_headers = " + response_headers);

            //返回格式
            //track_id=1528950329|0c949d0fdd64a54cfcc5eac3af92638557033d9008e0ebb506|a0dc3a2bddd6d6bdf8a46073c411a707; Path=/; Domain=ele.me; Max-Age=311040000
            //USERID=32359406; Domain=.ele.me; Max-Age=31536000; Path=/; HttpOnly
            //SID=BOZGSDpNUTaNkIz21znrQchyeFLEv9mRYUig; Domain=.ele.me; Max-Age=31536000; Path=/; HttpOnly
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

            elemeCookie.setSID(SID);
            elemeCookie.setUSERID(USERID);
            elemeCookie.setTrack_id(track_id);
            elemeCookie.setUpdate_time(new Date());
            elemeCookie.setStatus(2);
            elemeCookie.setCookie(cookie);

            tDao.update(elemeCookie);


        }




    }

}
