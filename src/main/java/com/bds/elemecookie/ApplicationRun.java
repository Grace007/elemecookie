package com.bds.elemecookie;


import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author eli
 * @date 2017/9/18 15:40
 */
public class ApplicationRun {
    private static Logger logger = Logger.getLogger(ApplicationRun.class);

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new FileSystemXmlApplicationContext("classpath:dataSource.xml");


    }
}
