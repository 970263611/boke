package com.text.user.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.DocumentException;

import com.text.entity.WeChat;

public interface WeChatService {
	
	String Verification(WeChat weChat);

	String CreateMenu(String data);

	String processRequest(HttpServletRequest request) throws IOException, DocumentException;

}
