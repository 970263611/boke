package com.dahua.boke.entity;

import java.io.Serializable;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
public class WordMessage implements Serializable{

	//留言信息表
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String create_user; //创建人
	private String create_time; //创建时间
	private String message;     //留言信息
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
