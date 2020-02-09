package service;


import entity.User;
import entity.WeChat;

import java.util.Map;

public interface WeChatService {
	
	String Verification(WeChat weChat);

	String CreateMenu();

	User weChatLogin(String code);

	Map<String, Object> processRequest(Map<String, String> requestMap);

}
