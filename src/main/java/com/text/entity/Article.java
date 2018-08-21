package com.text.entity;

import java.io.Serializable;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
public class Article implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//文章表
	
	private int id;
	private String title;      //标题
	private String content;    //正文
	private String lead;       //导语
	private String create_user;//创建人
	private String create_time;//创建时间
	private String type;	   //文章类型---技术交流1，我的困惑2，谈谈生活3，情感交流4
	private String isdelete;   //是否删除0不删除，1删除
	private String top;        //是否置顶0不置顶，1置顶
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLead() {
		return lead;
	}
	public void setLead(String lead) {
		this.lead = lead;
	}
	public String getCreate_user() {
		return create_user;
	}
	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIsdelete() {
		return isdelete;
	}
	public void setIsdelete(String isdelete) {
		this.isdelete = isdelete;
	}
	public String getTop() {
		return top;
	}
	public void setTop(String top) {
		this.top = top;
	}
	
	
}
