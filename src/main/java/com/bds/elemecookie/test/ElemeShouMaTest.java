package com.bds.elemecookie.test;

import com.bds.base.http.Response;
import com.bds.elemecookie.utils.ElemeHttpBase;
import org.junit.Test;

/**
 * @author eli
 * @date 2018/6/15 14:10
 */
public class ElemeShouMaTest {

    @Test
    public void getToken(){
        String login_url = "http://xapi.yika66.com/User/login?uName=picapica&pWord=bdsbds&Developer=&code=utf8";
        String result = "";
        try {
            Response response = ElemeHttpBase.get(login_url, "utf_8");
            result = response.getResult();
        }catch (Exception e){
            e.printStackTrace();
        }
        //1FB25E0F2A8C7633083D9E36EFBA061D&22.01&10&30&10&1&0&100581332&普通用户&22.01
        //7552BB7E7DB590E9083D9E36EFBA061D

        System.out.println("result = " + result);

    }

    @Test
    public void getPhoneNumber(){
        String phone_url = "http://xapi.yika66.com/User/getPhone?ItemId=388&token=C20D21ACEF930C86083D9E36EFBA061D&code=utf8&Count=1";
        String result = "";
        try {
            Response response = ElemeHttpBase.get(phone_url, "utf_8");
            result = response.getResult();
        }catch (Exception e){
            e.printStackTrace();
        }
        //result = 17024474363;
        //17024474363
        //17024474362
        System.out.println("result = " + result);
    }

    @Test
    public void getSMS(){
//        String sms_url = "http://xapi.yika66.com/User/getMessage?token=09DA286F7FB5E12C083D9E36EFBA061D&ItemId=388&Phone=17024474362&code=utf8";
        String sms_url ="http://xapi.yika66.com/User/getMessage?token=C20D21ACEF930C86083D9E36EFBA061D&ItemId=388&Phone=17024474465&code=utf8";
        String result = "";
        try {
            Response response = ElemeHttpBase.get(sms_url, "utf_8");
            result = response.getResult();
        }catch (Exception e){
            e.printStackTrace();
        }
        //无短信
        //result = NOTION&平台全面升级，新增专属通道免手续费、指定号段、指定卡商ID、过滤号段等功能，开发用户可查看API文档使用新功能，客服QQ280625362[End]
        //result = MSG&388&17024474465&【饿了么】您的验证码是024926，在5分钟内有效。如非本人操作请忽略本短信。[End]
        System.out.println("result = " + result);

    }






    public static void main(String[] args) {


    }


}
