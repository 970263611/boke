package com.text.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
public class MyTest {

	//用户个人中心文字信息表
	
	private int id;
	private String test_1; //用户铭言
	private String test_2; //用户格言
	private int user_id;   //关联的用户id
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTest_1() {
		return test_1;
	}
	public void setTest_1(String test_1) {
		this.test_1 = test_1;
	}
	public String getTest_2() {
		return test_2;
	}
	public void setTest_2(String test_2) {
		this.test_2 = test_2;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	
}
