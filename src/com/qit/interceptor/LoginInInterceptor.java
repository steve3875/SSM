package com.qit.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.qit.message.SessionAttributeDictionary;
import com.qit.util.ResourcesUtil;

public class LoginInInterceptor implements HandlerInterceptor {

	/*
	 * 登陆拦截器工作原理：
	 * 1、登陆拦截器首先判断此URL是不是公开地址，如果是公开地址那么放行，如果不是那么进入到下一步骤
	 * 2、判断session中是不是已经进行了用户认证，如果已经认证那么放行，如果没有认证那么进入到下一步
	 * 3、既不是公开地址又没有经过验证，那么说明此地址是未经过认证得地址，需要进行登陆认证，跳转到登陆界面
	 * */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestUrl = request.getRequestURI();
		List<String> anonymousURLList = ResourcesUtil.gekeyList("anonymousURL");
		for (String url : anonymousURLList) {
			if (requestUrl.indexOf(url) >= 0)
				return true;
		}

		HttpSession session = request.getSession();
		if (session.getAttribute(SessionAttributeDictionary.SESSION_USER) != null) {
			return true;
		}
		request.getRequestDispatcher("/index.jsp").forward(request, response);
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
