package com.qit.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.qit.message.SessionAttributeDictionary;

public class CustomExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		CustomException customException = null;
		if (ex instanceof CustomException) {
			customException = (CustomException) ex;
		} else if (ex instanceof MaxUploadSizeExceededException) {
			customException = new CustomException("数据文件过大，请重新裁剪图片，最大不可超过1KB!");
		} else {
			customException = new CustomException("未知错误!");
		}
		String message = customException.getMessage();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(SessionAttributeDictionary.ERROR_CODE, message);
		modelAndView.setViewName("error");
		return modelAndView;
	}

}
