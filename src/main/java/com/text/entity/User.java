package com.text.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * 用户信息的实体类
 * 一个主键-一共四个属性
 * ID，昵称，用户名，用户密码
 * @author 丁伟强
 *
 */
@EntityScan
public class User {

	private int id;			//	ID
	private String nickname;//	昵称	
	private String name;	//	用户名
	private String password;//	密码
	private int rowId;		//  权限id
	private String realname;//  真实姓名
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}
	
}
