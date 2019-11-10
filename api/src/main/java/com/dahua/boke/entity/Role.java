package com.dahua.boke.entity;

import java.io.Serializable;

public class Role implements Serializable {

	//角色表
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String roleName; //角色名字
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	
}
