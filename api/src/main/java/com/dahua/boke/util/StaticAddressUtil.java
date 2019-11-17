package com.dahua.boke.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticAddressUtil {
	
	public static String yuming;
	public static String ip;
	public static String bokeUploadImgTop;
	public static String imagePort;
	public static String rocketPort;
	public static String baiduImgFolder;
	public static String bokeImgTop;
	public static String baiduImgUrl;
	public static String rocketMQTelnet;
	public static String baiduImg;

	@Value("${yuming}")
	private String static_yuming;
	@Value("${ip}")
	private String static_ip;
	@Value("${bokeUploadImgTop}")
	private String static_bokeUploadImgTop;
	@Value("${imagePort}")
	private String static_imagePort;
	@Value("${rocketPort}")
	private String static_rocketPort;
	@Value("${baiduImgFolder}")
	private String static_baiduImgFolder;

	public static String Token;// 微信token
	public static String EncodingAESKey;// 微信key
	public static String AppID;// 微信开发者id
	public static String AppSecret;// 微信开发者密码
	public static String AppID_CS;// 微信开发者id
	public static String AppSecret_CS;// 微信开发者密码
	public static String Account_Number_CS;//测试公众号

	@Value("${token}")
	private String static_Token;
	@Value("${EncodingAESKey}")
	private String static_EncodingAESKey;
	@Value("${AppID}")
	private String static_AppID;
	@Value("${AppSecret}")
	private String static_AppSecret;
	@Value("${AppID_CS}")
	private String static_AppID_CS;
	@Value("${AppSecret_CS}")
	private String static_AppSecret_CS;
	@Value("${Account_Number_CS}")
	private String static_Account_Number_CS;

	@Value("${dubbo.private.ip}")
	private String dubbo_private_ip;

	@PostConstruct
	private void init() {
		this.yuming = static_yuming;
		this.ip = static_ip;
		this.bokeUploadImgTop = static_bokeUploadImgTop;
		this.imagePort = static_imagePort;
		this.rocketPort = static_rocketPort;
		this.baiduImgFolder = static_baiduImgFolder;
		bokeImgTop = yuming + imagePort;
		baiduImgUrl = bokeImgTop + baiduImgFolder;
		rocketMQTelnet = yuming + rocketPort;
		baiduImg = bokeUploadImgTop + baiduImgFolder;
		//解决dubbo 在docker容器注入ip和端口不可以访问到的问题
		System.setProperty("DUBBO_IP_TO_REGISTRY", dubbo_private_ip);


		this.Token = static_Token;
		this.EncodingAESKey = static_EncodingAESKey;
		this.AppID = static_AppID;
		this.AppSecret = static_AppSecret;
		this.AppID_CS = static_AppID_CS;
		this.AppSecret_CS = static_AppSecret_CS;
		this.Account_Number_CS = static_Account_Number_CS;
	}





}
