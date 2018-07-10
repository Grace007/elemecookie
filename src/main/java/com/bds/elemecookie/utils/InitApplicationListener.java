package com.bds.elemecookie.utils;

import com.bds.elemecookie.job.StartJob;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 配置应用监听器,监听容器启动和停止
 * @author eli
 * @date 2017/11/3 12:07
 */
public class InitApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = Logger.getLogger(InitApplicationListener.class);

    /**
     * 在使用传统的application.xml和project-servlet.xml配置中会出现二次调用的问题。主要原因是初始化root容器之后，会初始化project-servlet.xml对应的子容器。我们需要的是只执行一遍即可。那么上面打印父容器的代码用来进行判断排除子容器即可。在业务处理之前添加如下判断：
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null){
            logger.info("现在进入InitApplicationListener中..");
            logger.info(event.getApplicationContext().getParent());
            try {
                StartJob quartzDemoTest =new StartJob();
                quartzDemoTest.quartzTest1();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    @PostConstruct
    public void postConstruct() throws Exception{
        logger.info("postConstruct()执行....");

    }
    @PreDestroy
    public void preDestory(){
        logger.info("preDestory()执行....");
    }
}
