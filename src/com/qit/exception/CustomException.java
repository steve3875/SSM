package com.qit.exception;


/***********************       
* 项目名称：QIT-SHIRO   
* 类名称    ：CustomException   
* 类描述    ：   
* 创建作者：qit   
* 创建时间：2017年6月30日 上午8:27:30       
************************/ 
public class CustomException extends Exception {
	private String message;

	public CustomException(String message) {
		super(message);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
