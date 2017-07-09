package com.qit.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.qit.exception.CustomException;
import com.qit.pojo.Permission;
import com.qit.pojo.User;
import com.qit.pojo.custom.ActiveUser;
import com.qit.service.UserService;

public class CustomRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;

	// 用于授权
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 首先从凭证里获取到当前的用户名
		ActiveUser activeUser = (ActiveUser) principals.getPrimaryPrincipal();
		// 实际开发中会根据当前的用户名查找当前用户对应的角色和权限信息

		List<String> permissionList = new ArrayList<String>();
		List<Permission> permissionObjectList = null;
		try {
			permissionObjectList = userService.findPermissionListByUserId(activeUser.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (permissionObjectList != null) {
			for (Permission permission : permissionObjectList)
				permissionList.add(permission.getPercode());
		}
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addStringPermissions(permissionList);
		return simpleAuthorizationInfo;
	}

	// 用于认证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 从token中取出用户身份信息
		String userCode = (String) token.getPrincipal();
		User user = userService.findUserByUserCode(userCode);
		if (user == null)
			return null;
		List<Permission> menuList = null;
		try {
			menuList = userService.findMenuListByUserId(user.getId());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ActiveUser activeUser = new ActiveUser();
		activeUser.setUsercode(user.getUsercode());
		activeUser.setUsername(user.getUsername());
		activeUser.setId(user.getId());
		activeUser.setMenuList(menuList);
		// 如果查询寻到，返回认证信息AuthenticationInfo
		SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(activeUser, user.getPassword(),
				ByteSource.Util.bytes(user.getSalt()), this.getName());
		// 如果查询不到
		return simpleAuthenticationInfo;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		super.setName("CustomRealm");
	}
	//清除缓存
	public void clearCached() {
		PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
		super.clearCache(principals);
	}

}
