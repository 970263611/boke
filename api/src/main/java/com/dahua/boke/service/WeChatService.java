package com.dahua.boke.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dahua.boke.entity.User;
import com.dahua.boke.entity.WeChat;

public interface WeChatService {
	
	String Verification(WeChat weChat);

	String CreateMenu();

	User processRequest(HttpServletRequest request,HttpServletResponse response);

	User weChatLogin(String code);

}
