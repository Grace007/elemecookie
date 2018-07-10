package com.bds.elemecookie.job;

import com.bds.base.http.Response;
import com.bds.elemecookie.model.ElemeCookieUser;
import com.bds.elemecookie.utils.ElemeHttpBase;
import com.bds.elemecookie.utils.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.impl.NutDao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author eli
 * @date 2018/6/16 12:46
 */
public class ElemeTokenJob implements Job {
    private Logger logger =LoggerFactory.getLogger(getClass());
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("进入ElemeTokenJob...");
//        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/bds/elemecookie/test/dataSource.xml");
//        NutDao tDao = (NutDao) ctx.getBean("o2oDao");
        NutDao tDao= (NutDao) SpringContextUtil.getBean("o2oDao");
        //取超时或者失效的token
        ElemeCookieUser elemeCookieUser = tDao.fetch(ElemeCookieUser.class, Cnd.where("token_status", "!=", 2));

        if (elemeCookieUser == null) {
            return;
        }
        String login_url = "http://xapi.yika66.com/User/login?uName="+elemeCookieUser.getUsername()+"&pWord="+elemeCookieUser.getPassword()+"&Developer=&code=utf8";
        String result = "";
        try {
            Response response = ElemeHttpBase.get(login_url, "utf_8");
            result = response.getResult();
            logger.info("更新Token时的result = " + result);
        }catch (Exception e){
            logger.info("更新Token时，发生异常");
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
        logger.info("更新token成功");

    }
}
