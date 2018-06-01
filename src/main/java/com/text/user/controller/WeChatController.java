package com.text.user.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.text.entity.WeChat;
import com.text.realm.RunFunction;
import com.text.user.service.WeChatService;

@Controller("wechat")
public class WeChatController {
	
	private static Logger logger = LoggerFactory.getLogger(RunFunction.class);
	
	@Autowired
	private WeChatService weChatService;

	/*
	 * 获取微信发过来的数据，解析
	 */
	@GetMapping("getMes")
	@ResponseBody
	public String getWeChat(HttpServletRequest request){
		/*boolean isGet = request.getMethod().toLowerCase().equals("get");  */
		String signature = request.getParameter("signature");  
		String timestamp = request.getParameter("timestamp");  
		String nonce = request.getParameter("nonce");  
		String echostr = request.getParameter("echostr");  
		logger.info("signature - "+signature+" timestamp - "+timestamp+" nonce - "+nonce+" echostr - "+echostr);
		return weChatService.Verification(new WeChat(signature,timestamp,nonce,echostr));
	} 
}
