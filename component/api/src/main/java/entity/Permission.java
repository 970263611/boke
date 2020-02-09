package entity;

import java.io.Serializable;

public class Permission implements Serializable {

	//权限表
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String permissionName; //权限名字
	private int rowId;             //对应的角色id
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPermissionName() {
		return permissionName;
	}
	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	
	
}
