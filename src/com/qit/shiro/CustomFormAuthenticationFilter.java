package com.qit.shiro;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import com.qit.message.SessionAttributeDictionary;

public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		// 进行验证码的校验，如果校验失败提示验证码错误，抛出异常
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession session = httpServletRequest.getSession();
		String validate_code = (String) session.getAttribute(SessionAttributeDictionary.VALIDATE_CODE);
		String reqValidateCode = httpServletRequest.getParameter(SessionAttributeDictionary.VALIDATE_CODE);
		if (validate_code != null && reqValidateCode != null) {
			if (!reqValidateCode.equalsIgnoreCase(validate_code)) {
				httpServletRequest.setAttribute("shiroLoginFailure", "RandomCodeError");
				//拒绝访问，不在验证账号和密码
				return true;
			}

		}

		return super.onAccessDenied(request, response);
	}

}
