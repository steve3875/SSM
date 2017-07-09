package com.qit.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.qit.dao.PermissionCustomMapper;
import com.qit.dao.UserMapper;
import com.qit.exception.CustomException;
import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.UserExample;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;
import com.qit.shiro.CustomRealm;
import com.qit.service.UserService;

public class UserServiceImpl implements UserService {
	//定义dao包中的UserMapper接口并且自动注入相关的bean
	@Autowired
	private UserMapper userMapper;
	//定义dao包中的PermissionCustomMapper接口并自动注入
	@Autowired
	private PermissionCustomMapper permissionCustomMapper;
	//定义shiro包中的custom包接口，此处为了清除自动登录的缓存
	@Autowired
	private CustomRealm customRealm;
	/*
	* 方法名称: authenticateUser
	* 方法描述:
	* @see com.qit.service.impl.UserService#authenticateUser(com.qit.pojo.custom.UserCustom)
	*/
	@Override
	public ActiveUser authenticateUser(UserCustom userCustom) throws Exception {
		User user = this.findUserByUserCode(userCustom.getUsercode());
		if (user == null)
			throw new CustomException("账号不存在");
		if (userCustom.getPassword().equals(user.getPassword())) {
			ActiveUser activeUser = new ActiveUser();
			activeUser.setId(user.getId());
			activeUser.setUsercode(user.getUsercode());
			activeUser.setUsername(user.getUsername());
			List<Permission> permissionList = this.findPermissionListByUserId(user.getId());
			List<Permission> menuList = this.findMenuListByUserId(user.getId());
			activeUser.setMenuList(menuList);
			activeUser.setPermissionList(permissionList);
			return activeUser;
		} else {
			throw new CustomException("用户密码错误!");
		}

	}

	/*
	* 方法名称: findUserByUserCode
	* 方法描述:
	* @see com.qit.service.impl.UserService#findUserByUserCode(java.lang.String)
	*/
	@Override
	public User findUserByUserCode(String userCode) {
		UserExample example = new UserExample();
		com.qit.pojo.UserExample.Criteria criteria = example.createCriteria();
		criteria.andUsercodeEqualTo(userCode);
		List<User> userList = userMapper.selectByExample(example);
		if (userList != null && userList.size() == 1)
			return userList.get(0);
		return null;
	}

	/*
	* 方法名称: findMenuListByUserId
	* 方法描述:
	* @see com.qit.service.impl.UserService#findMenuListByUserId(java.lang.String)
	*/
	@Override
	public List<Permission> findMenuListByUserId(String id) throws Exception {
		return permissionCustomMapper.findMenuListByUserId(id);
	}

	/*
	* 方法名称: findPermissionListByUserId
	* 方法描述:
	* @see com.qit.service.impl.UserService#findPermissionListByUserId(java.lang.String)
	*/
	@Override
	public List<Permission> findPermissionListByUserId(String id) throws Exception {
		return permissionCustomMapper.findPermissionListByUserId(id);
	}

	public List<UserCustom> selctUserListUserName(String userName) {
		UserExample example = new UserExample();
		com.qit.pojo.UserExample.Criteria criteria = example.createCriteria();
		criteria.andUsernameLike(userName);
		List<User> userList = userMapper.selectByExample(example);
		List<UserCustom> userCustomList = new ArrayList<UserCustom>();
		for (User user : userList) {
			UserCustom userCustom = new UserCustom();
			BeanUtils.copyProperties(user, userCustom);
			userCustomList.add(userCustom);
		}
		return userCustomList;
	}

	@Override
	public void clearShiroEhcache() {
		
		customRealm.clearCached();
		
	}

}
