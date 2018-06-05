package com.text.user.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.text.entity.WeChat;

public interface WeChatService {
	
	String Verification(WeChat weChat);

	String CreateMenu(String data);

	void processRequest(HttpServletRequest request,HttpServletResponse response);

}
