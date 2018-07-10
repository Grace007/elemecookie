package com.bds.elemecookie.test;

import com.bds.elemecookie.utils.ElemeHttpBase;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 * @date 2018/6/15 11:32
 */
public class ElemeShopCookieTest {
    public static void main(String[] args)  {
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/bds/elemecookie/dataSource.xml");
        NutDao tDao = (NutDao) ctx.getBean("o2oDao");
        List<ElemeProductTaskJob> list = new ArrayList<>();
        list = tDao.query(ElemeProductTaskJob.class, Cnd.where("node","=","test0615").and("shop_status","=",0));
        System.out.println("list.size() = " + list.size());
        int count =0;
        for (int i=0 ; i<list.size();i++){
            ElemeProductTaskJob elemeProductTaskJob = list.get(i);
            String  webUrl = "https://mainsite-restapi.ele.me/shopping/restaurant/" + elemeProductTaskJob.getShop_id()
                    + "?extras%5B%5D=activities&extras%5B%5D=albums&extras%5B%5D=license&extras%5B%5D=identification&terminal=h5";
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Referer", "https://h5.ele.me/shop/");
            headers.put("Origin", "https://h5.ele.me");
            headers.put("Host", "mainsite-restapi.ele.me");
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36");
            headers.put("cookie","ubt_ssid=x8okiiwb97ouasa5fqdkgoobcgvkxevb_2017-10-09; _utrace=6e3186bb4eee67eaa35402de155cdbcf_2017-10-09; perf_ssid=mwekmrt7p1z3go5aqm3ykdshg9oqso8p_2017-10-09; track_id=1509078980|e98388ee1d98655de074ae69384de6b3912e5a4fbd3af5fb6d|1d2cff0ee26a3dbcff4420585b6471c0; SL_GWPT_Show_Hide_tmp=1; SL_wptGlobTipTmp=1; USERID=1696896881; SID=nmaHoP5N15DhWSPn1GNStrr7Mg4xEKPFeJuw");

            String result = "";
            try {
                result = ElemeHttpBase.get(webUrl, "utf-8", headers).getResult();
            } catch (Exception e) {
                e.printStackTrace();
            }

            count++;
            if (result.length()> 101) {
                System.out.println("count:" + count + "   result = " + result.substring(1, 100));
            }else{
                System.out.println("count:" + count + "   result = " + result);
            }
            if (result.contains("UNAUTHORIZED_RESTAURANT_ERROR")){
                System.out.println("出现UNAUTHORIZED_RESTAURANT_ERROR");
                System.out.println("################count = " + count);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }





        }

    }
}
