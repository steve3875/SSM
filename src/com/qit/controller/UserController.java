package com.qit.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qit.message.SessionAttributeDictionary;
import com.qit.exception.CustomException;
import com.qit.pojo.custom.ActiveUser;
import com.qit.pojo.custom.UserCustom;
import com.qit.service.UserService;

//controller注解表示此类是controller控制类，负责处理springmvc的请求
@Controller
public class UserController {
	//此处注解autowired表示由spring自动的将bean注入到此属性中
	@Autowired
	private UserService userService;
	//Requestmapping指示了URL请求的路径是/login.action时候将会由此方法进行处理
	@RequestMapping("/login.action")
	public String login(HttpServletRequest request) throws Exception {
		// 如果登陆失败从request中获取认证异常信息，shiroLoginFailure就是shiro异常类的全限定名
		String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
		// 根据shiro返回的异常类路径判断，抛出指定异常信息
		if (exceptionClassName != null) {
			if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
				// 最终会抛给异常处理器
				throw new CustomException("账号不存在");
			} else if (IncorrectCredentialsException.class.getName().equals(exceptionClassName)) {
				throw new CustomException("用户名/密码错误");
			} else if ("RandomCodeError".equals(exceptionClassName)) {
				throw new CustomException("验证码错误 ");
			} else {
				throw new Exception();// 最终在异常处理器生成未知错误
			}
		}
		// 此方法不处理登陆成功（认证成功），shiro认证成功会自动跳转到上一个请求路径
		// 登陆失败还到login页面
		return "login";
	}

	// 登陆成功后跳转到首页
	@RequestMapping("/first.action")
	public String first(Model model) {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
			model.addAttribute(SessionAttributeDictionary.SESSION_USER, activeUser);
		}
		return "first";
	}

	@RequestMapping("/loginIndex.action")
	public String loginIndex(Model model) {

		return "login";
	}

	@RequestMapping("/queryUsers.action")
	@RequiresPermissions("user:query")
	public String queryUsers(Model model) {
		List<UserCustom> userCustomList = userService.selctUserListUserName("%a%");
		model.addAttribute("userList", userCustomList);
		return "userList";

	}

	@RequestMapping("/clearShiroEhcache.action")
	public String clearShiroEhcache() {
		userService.clearShiroEhcache();
		return "first";
	}
}
