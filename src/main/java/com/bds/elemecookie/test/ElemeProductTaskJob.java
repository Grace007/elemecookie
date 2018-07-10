package com.bds.elemecookie.test;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@Table("mobile_eleme_product_task")
public class ElemeProductTaskJob {
	@Id
	private long id;
	@Column
	private long request_id;
	@Column
	private long task_id;
	@Column
	private Date create_time;
	@Column
	private String city_name;
	@Column
	private String keyword;
	@Column
	private String url;
	@Column
	private String shop_name;
	@Column
	private String shop_id;
	@Column
	private int product_number;
	@Column
	private int comment_number;
	@Column
	private String node;
	@Column
	private int level;
	@Column
	private int shop_status;
	@Column
	private int product_status;
	@Column
	private int comment_status;
	@Column
	private String project_code;
	@Column
	private String down_type;
	@Column
	private String remark;
    @Column
    private String latitude;
    @Column
    private String longitude;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRequest_id() {
		return request_id;
	}

	public void setRequest_id(long request_id) {
		this.request_id = request_id;
	}

	public long getTask_id() {
		return task_id;
	}

	public void setTask_id(long task_id) {
		this.task_id = task_id;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public String getShop_id() {
		return shop_id;
	}

	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}

	public int getProduct_number() {
		return product_number;
	}

	public void setProduct_number(int product_number) {
		this.product_number = product_number;
	}

	public int getComment_number() {
		return comment_number;
	}

	public void setComment_number(int comment_number) {
		this.comment_number = comment_number;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getShop_status() {
		return shop_status;
	}

	public void setShop_status(int shop_status) {
		this.shop_status = shop_status;
	}

	public int getProduct_status() {
		return product_status;
	}

	public void setProduct_status(int product_status) {
		this.product_status = product_status;
	}

	public int getComment_status() {
		return comment_status;
	}

	public void setComment_status(int comment_status) {
		this.comment_status = comment_status;
	}

	public String getProject_code() {
		return project_code;
	}

	public void setProject_code(String project_code) {
		this.project_code = project_code;
	}

	public String getDown_type() {
		return down_type;
	}

	public void setDown_type(String down_type) {
		this.down_type = down_type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
