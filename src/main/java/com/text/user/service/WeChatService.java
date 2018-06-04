package com.text.user.service;

import com.text.entity.WeChat;

public interface WeChatService {
	
	String Verification(WeChat weChat);

	String CreateMenu(String data);

}
