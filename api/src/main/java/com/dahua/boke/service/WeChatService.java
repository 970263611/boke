package com.dahua.boke.service;

import java.util.Map;

import com.dahua.boke.entity.User;
import com.dahua.boke.entity.WeChat;

public interface WeChatService {
	
	String Verification(WeChat weChat);

	String CreateMenu();

	User weChatLogin(String code);

	Map<String,Object> processRequest(Map<String,String> requestMap);

}
