package com.qit.service;

import java.util.List;

import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;

public interface UserService {

	ActiveUser authenticateUser(UserCustom userCustom) throws Exception;

	User findUserByUserCode(String userCode);

	List<Permission> findMenuListByUserId(String id) throws Exception;

	List<Permission> findPermissionListByUserId(String id) throws Exception;

	List<UserCustom> selctUserListUserName(String userName);

	void clearShiroEhcache();

}