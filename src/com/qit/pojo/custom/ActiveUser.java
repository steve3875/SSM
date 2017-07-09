package com.qit.pojo.custom;

import java.io.Serializable;
import java.util.List;

import com.qit.pojo.Permission;
import com.qit.pojo.User;

public class ActiveUser extends User implements Serializable{
	private List<Permission> permissionList;
	private List<Permission> menuList;

	public List<Permission> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(List<Permission> permissionList) {
		this.permissionList = permissionList;
	}

	public List<Permission> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<Permission> menuList) {
		this.menuList = menuList;
	}

}
