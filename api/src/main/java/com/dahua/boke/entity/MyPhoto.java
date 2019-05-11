package com.dahua.boke.entity;

import java.io.Serializable;

public class MyPhoto implements Serializable{

	//个人中心的图片信息表
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; 
	private int user_id;         //对应的用户id
	private String photo;      //图片
	private String photo_test; //图片对应的文字
	private String update_time;//上传图片时间
	private String nickname;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getPhoto_test() {
		return photo_test;
	}
	public void setPhoto_test(String photo_test) {
		this.photo_test = photo_test;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	
	
	
}
