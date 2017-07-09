package com.qit.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.qit.message.SessionAttributeDictionary;
import com.qit.pojo.Permission;
import com.qit.pojo.custom.ActiveUser;
import com.qit.util.ResourcesUtil;

public class AuthencatieInterceptor implements HandlerInterceptor {
	/*
	 * 授权拦截器说明：
	 * 1、授权拦截器是在登陆拦截器放行的前提下生效的，如果登陆拦截器没有return true,那么授权拦截器不会执行
	 * 2、授权拦截器首先判断此URL是不是公开地址，如果是公开地址那么和登陆拦截器一样，放行
	 * 3、如果不是公开地址，那么判断是不是公共访问地址，如果是每个用户都可以访问的地址，那么直接放行
	 * 4、如果既不是公开地址也不是公共地址，那么从session中读取当前用户的信息，从信息中取出当前用户的权限permission
	 * 	   链表，然后将链表中的URL和当前请求的URL进行匹配，如果成功那么说明当前用户有访问此URL的权限，那么放行，如果不成功那么
	 * 	 进行到下一步。
	 * 5、既不是公开地址，也不是公共地址，并且当前用户不存在访问此URL的权限，那么跳转到拒绝页面，显示用户被拒绝
	 * */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 首先判断是不是公开地址,如果是公开地址那么
		String requestUrl = request.getRequestURI();
		List<String> anonymousURLList = ResourcesUtil.gekeyList("anonymousURL");
		for (String url : anonymousURLList) {
			if (requestUrl.indexOf(url) >= 0)
				return true;
		}
		// 判断是不是公共访问地址,如果是公开地址那么放行
		List<String> commonURLList = ResourcesUtil.gekeyList("commonURL");
		for (String url : commonURLList) {
			if (requestUrl.indexOf(url) >= 0)
				return true;
		}
		// 是否是权限范围的URL
		HttpSession session = request.getSession();
		ActiveUser activeUser = (ActiveUser) session.getAttribute(SessionAttributeDictionary.SESSION_USER);
		List<Permission> permissionList = activeUser.getPermissionList();
		for (Permission permission : permissionList) {
			if (requestUrl.indexOf(permission.getUrl()) >= 0)
				return true;
		}
		request.getRequestDispatcher("/WEB-INF/message/refuse.jsp").forward(request, response);
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
