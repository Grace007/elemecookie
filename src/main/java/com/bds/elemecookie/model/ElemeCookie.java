package com.bds.elemecookie.model;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.impl.NutDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Date;

/**
 * @author eli
 * @date 2018/6/14 11:03
 */
@Table("eleme_cookie_0619")
public class ElemeCookie {
    @Id
    private int id;
    @Column
    private String track_id;
    @Column
    @ColDefine(width = 512)
    private String cookie;
    @Column("user_id")
    private String USERID;
    @Column("s_id")
    private String SID;
    @Column
    @ColDefine(width = 512)
    private String ali_request_body;
    @Column
    private Date create_time;
    @Column
    private Date update_time;
    @Column
    private int status;
    @Column
    private int update_status;
    @Column
    private String node;

    public int getUpdate_status() {
        return update_status;
    }

    public void setUpdate_status(int update_status) {
        this.update_status = update_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrack_id() {
        return track_id;
    }

    public void setTrack_id(String track_id) {
        this.track_id = track_id;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getAli_request_body() {
        return ali_request_body;
    }

    public void setAli_request_body(String ali_request_body) {
        this.ali_request_body = ali_request_body;
    }

    public static void main(String[] args) {
        ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/bds/elemecookie/test/dataSource.xml");
        NutDao o2oDao = (NutDao) ctx.getBean("o2oDao");
        o2oDao.create(ElemeCookie.class,false);
    }
}
