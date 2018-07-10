package com.bds.elemecookie.job;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author eli
 * @date 2018/6/16 13:03
 */
public class StartJob  {
    public static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    public void quartzTest1() throws Exception{
        //创建调度中心(调度器)
        Scheduler scheduler = schedulerFactory.getScheduler();
        //创建jobDeil(作业)
        ElemeCookieJob elemeCookieJob = new ElemeCookieJob();
        ElemeTokenJob elemeTokenJob =new ElemeTokenJob();
        //JobDetail的作用就是给job作业添加附加信息,比如name,group等
        JobDetail elemeTokenJobDetail = JobBuilder.newJob(elemeTokenJob.getClass()).withIdentity("elemeTokenJob").build();
        JobDetail elemeCookieJobbDetail = JobBuilder.newJob(elemeCookieJob.getClass()).withIdentity("elemeCookieJob").build();
        //创建触发器
        Trigger tokenTrigger = TriggerBuilder.newTrigger().withIdentity("elemeTokenJob","eleme").withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")).build();
        Trigger cookieTrigger = TriggerBuilder.newTrigger().withIdentity("elemeCookieJob","eleme").withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")).build();
        //向调度中心注册
        scheduler.scheduleJob(elemeTokenJobDetail,tokenTrigger);
        scheduler.scheduleJob(elemeCookieJobbDetail,cookieTrigger);
        // 启动调度
        scheduler.start();
    }
}
