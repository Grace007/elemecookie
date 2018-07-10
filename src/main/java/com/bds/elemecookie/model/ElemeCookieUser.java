package com.bds.elemecookie.model;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.NutDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Date;

/**
 * @author eli
 * @date 2018/6/15 18:25
 */
@Table("eleme_cookie_user")
public class ElemeCookieUser {
    @Id
    private int id;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String token;
    @Column
    private int token_status;
    @Column
    private Date update_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getToken_status() {
        return token_status;
    }

    public void setToken_status(int token_status) {
        this.token_status = token_status;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public static void main(String[] args) {
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/bds/elemecookie/test/dataSource.xml");
        NutDao o2oDao = (NutDao) ctx.getBean("o2oDao");
        o2oDao.create(ElemeCookieUser.class,false);
    }
}
