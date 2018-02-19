package com.text.entity;

public class MyPhoto {

	//个人中心的图片信息表
	
	private int id; 
	private int user_id;         //对应的用户id
	private String photo_1;      //图片1
	private String photo_1_test; //图片1对应的文字
	private String photo_2;		 //图片2
	private String photo_2_test; //图片2对应的文字
	private String photo_3; 	 //图片3
	private String photo_3_test; //图片3对应的文字
	private byte[] photo_byte_1;
	private byte[] photo_byte_2;
	private byte[] photo_byte_3;
	
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
	public String getPhoto_1() {
		return photo_1;
	}
	public void setPhoto_1(String photo_1) {
		this.photo_1 = photo_1;
	}
	public String getPhoto_1_test() {
		return photo_1_test;
	}
	public void setPhoto_1_test(String photo_1_test) {
		this.photo_1_test = photo_1_test;
	}
	public String getPhoto_2() {
		return photo_2;
	}
	public void setPhoto_2(String photo_2) {
		this.photo_2 = photo_2;
	}
	public String getPhoto_2_test() {
		return photo_2_test;
	}
	public void setPhoto_2_test(String photo_2_test) {
		this.photo_2_test = photo_2_test;
	}
	public String getPhoto_3() {
		return photo_3;
	}
	public void setPhoto_3(String photo_3) {
		this.photo_3 = photo_3;
	}
	public String getPhoto_3_test() {
		return photo_3_test;
	}
	public void setPhoto_3_test(String photo_3_test) {
		this.photo_3_test = photo_3_test;
	}
	public byte[] getPhoto_byte_1() {
		return photo_byte_1;
	}
	public void setPhoto_byte_1(byte[] photo_byte_1) {
		this.photo_byte_1 = photo_byte_1;
	}
	public byte[] getPhoto_byte_2() {
		return photo_byte_2;
	}
	public void setPhoto_byte_2(byte[] photo_byte_2) {
		this.photo_byte_2 = photo_byte_2;
	}
	public byte[] getPhoto_byte_3() {
		return photo_byte_3;
	}
	public void setPhoto_byte_3(byte[] photo_byte_3) {
		this.photo_byte_3 = photo_byte_3;
	}
	
	
}
