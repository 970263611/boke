package com.text.user.service.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.text.entity.User;
import com.text.entity.WeChat;
import com.text.realm.RunFunction;
import com.text.realm.SerializeUtil;
import com.text.user.dao.WeChatDao;
import com.text.user.service.WeChatService;
import com.text.util.SHA1;
import com.text.util.WeChatMesUtil;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

@Service
public class WeChatServiceImpl implements WeChatService {

	private static Logger logger = LoggerFactory.getLogger(RunFunction.class);

	@Autowired
	private SerializeUtil redisDateSourse;
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired
	private WeChatDao weChatDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.text.user.service.WeChatService#Verification(com.text.entity.WeChat)
	 * 验证微信发过来的请求
	 */
	@Override
	public String Verification(WeChat weChat) {
		List<String> params = new ArrayList<String>();
		params.add(WeChatMesUtil.Token);
		params.add(weChat.getTimestamp());
		params.add(weChat.getNonce());
		// 1. 将token、timestamp、nonce三个参数进行字典序排序
		Collections.sort(params, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		// 2. 将三个参数字符串拼接成一个字符串进行sha1加密
		String temp = SHA1.encode(params.get(0) + params.get(1) + params.get(2));
		if (temp.equals(weChat.getSignature())) {
			try {
				System.out.println("成功返回 echostr：" + weChat.getEchostr());
				return weChat.getEchostr();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("失败认证");
		return null;
	}

	/**
	 * 自定义回复消息
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) {
		String respMessage = "回复您的消息是来自于大花博客服务器端，欢迎访问www.loveding.top\n由于个人公众号微信给的权限较少，请关注我的测试公众号："
				+ WeChatMesUtil.Account_Number_CS + "\n";
		Map requestMap = WeChatMesUtil.parseMsgXml(request);
		// 发送方帐号（open_id）
		String fromUserName = (String) requestMap.get("FromUserName");
		// 公众帐号
		String toUserName = (String) requestMap.get("ToUserName");
		// 消息类型
		String msgType = (String) requestMap.get("MsgType");

		// 文本消息
		if (msgType.equals(WeChatMesUtil.REQ_MESSAGE_TYPE_TEXT)) {
			String content = (String) requestMap.get("Content");
			respMessage += "您发送的是文本消息！";
			respMessage += "\n内容是    " + content;
		}
		// 图片消息
		else if (msgType.equals(WeChatMesUtil.REQ_MESSAGE_TYPE_IMAGE)) {
			respMessage += "您发送的是图片消息！";
		}
		// 地理位置消息
		else if (msgType.equals(WeChatMesUtil.REQ_MESSAGE_TYPE_LOCATION)) {
			respMessage += "您发送的是地理位置消息！";
		}
		// 链接消息
		else if (msgType.equals(WeChatMesUtil.REQ_MESSAGE_TYPE_LINK)) {
			respMessage += "您发送的是链接消息！";
		}
		// 音频消息
		else if (msgType.equals(WeChatMesUtil.REQ_MESSAGE_TYPE_VOICE)) {
			respMessage += "您发送的是音频消息！";
		} // 事件推送
		else if (msgType.equals(WeChatMesUtil.REQ_MESSAGE_TYPE_EVENT)) {
			// 事件类型
			String eventType = (String) requestMap.get("Event");
			// 订阅
			if (eventType.equals(WeChatMesUtil.EVENT_TYPE_SUBSCRIBE)) {
				respMessage = "mo-爱心 盼星星，盼月亮，你终于来鸟~\n欢迎访问大花博客公众号\n网页版请访问www.loveding.top\n由于个人公众号微信给的权限较少。\n更多功能请关注我的测试公众号："
						+ WeChatMesUtil.Account_Number_CS + "。\n关注测试公众号后将为您自动注册并登陆，同时将提供给您可以在浏览器登陆大花博客的账号和密码。";

				// 发送方帐号（open_id）
				String open_id = (String) requestMap.get("FromUserName");
				// 公众帐号
				System.out.println(open_id);
				Jedis jedis = redisDateSourse.getRedis();
				String accessToken = jedis.get("AccessToken_CS");
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpGet httpget = new HttpGet("https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + accessToken
						+ "&openid=" + open_id + "&lang=zh_CN");
				// Create a custom response handler
				ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

					public JSONObject handleResponse(final HttpResponse response)
							throws ClientProtocolException, IOException {
						int status = response.getStatusLine().getStatusCode();
						if (status >= 200 && status < 300) {
							HttpEntity entity = response.getEntity();
							if (null != entity) {
								String result = EntityUtils.toString(entity);
								// 根据字符串生成JSON对象
								JSONObject resultObj = JSONObject.fromObject(result);
								return resultObj;
							} else {
								return null;
							}
						} else {
							throw new ClientProtocolException("Unexpected response status: " + status);
						}
					}

				};
				System.out.println("请求基本信息成功");
				logger.info("请求基本信息成功");
				// 返回的json对象
				JSONObject responseBody;
				try {
					responseBody = httpclient.execute(httpget, responseHandler);
					System.out.println("responseBody=============" + responseBody.toString());
					logger.info("基本信息=============" + responseBody.toString());
					String resultMsg = "一键注册失败";
					if (null != responseBody.get("subscribe")) {
						int subscribe = (int) responseBody.get("subscribe");// 返回token
						if (0 == subscribe) {
							resultMsg = "请先关注公众号";
						} else if (1 == subscribe) {
							User u = weChatDao.getUserId(open_id);
							if (u != null) {
								resultMsg = "您已经注册过本博客系统!已为您成功登陆本博客!\n同时您每次从公众号访问大花博客时,将自动免密登陆\n访问测试公众号，带来更好体验,您将可以直接浏览博客内容！\n您的账号是:" + u.getName() + "\n您的密码是:"
										+ u.getPassword() + "\n请牢记您的账号,可在网页端直接登陆。";
							} else {
								/**
								 * 提供一键注册
								 */
								User user = new User();
								String password = getPass();
								user.setNickname((String) responseBody.get("nickname") + "_wx");
								user.setName(password);
								user.setRealname(open_id);
								user.setPassword(password);
								user.setCreateTime(new Date());
								String flag = userServiceImpl.userAdd(user);
								if ("success".equals(flag)) {
									resultMsg = "已经为您注册成功，感谢您的支持!\n您已成功登陆本博客!\n访问测试公众号，带来更好体验,您将可以直接浏览博客内容！\n您的账号是:" + password + "\n您的密码是:" + password
											+ "\n请牢记您的账号,可在网页端直接登陆。";
								} else if ("NickNamealReguster".equals(flag)) {
									resultMsg = "用户昵称重复，请更换微信昵称";
								} else if ("alReguster".equals(flag)) {
									resultMsg = "用户名重复，请更换微信昵称";
								}
							}
						}
						WeChatMesUtil.XMLprint(response, "mo-爱心 盼星星，盼月亮，你终于来鸟~\n欢迎访问大花博客公众号\n网页版请访问www.loveding.top\n"+resultMsg, toUserName, open_id, null/* 这里暂时都定义为文本回复形式 */);
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						httpclient.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}
			// 取消订阅
			else if (eventType.equals(WeChatMesUtil.EVENT_TYPE_UNSUBSCRIBE)) {
				// TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
			}
			// 自定义菜单点击事件
			else if (eventType.equals(WeChatMesUtil.EVENT_TYPE_CLICK)) {
				// 事件KEY值，与创建自定义菜单时指定的KEY值对应
				String eventKey = (String) requestMap.get("EventKey");// 这个 EventKey 就是自定义菜单的key
				if (eventKey.equals("weChatRegister")) {
					
				}
			}
		}
		System.out.println("返回消息-" + respMessage);
		WeChatMesUtil.XMLprint(response, respMessage, toUserName, fromUserName, msgType/* 这里暂时都定义为文本回复形式 */);
	}

	/**
	 * 微信自定义菜单创建
	 */
	@Override
	public String CreateMenu() {
		String flag = "error";
		Jedis jedis = redisDateSourse.getRedis();
		String accessToken = jedis.get("AccessToken_CS");// 因为个人公众号没有权限，所以这里获取测试号在redis中的accessToken
		if (!"none".equals(accessToken)) {
			String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
			String data = "{" + "\"button\": [" + "{"
					+ "\"type\": \"view\"," + "\"name\": \"主页\"," + "\"url\": \"" + WeChatMesUtil.WeChat_Index_URL
					+ "\"" + "}," + 
					/*"{" + "\"name\": \"提笔\"," + "\"sub_button\": [{" + "\"type\": \"view\","
					+ "\"name\": \"技术交流\"," + "\"url\": \"" + WeChatMesUtil.WeChat_Write_1_URL + "\"" + "}, {"
					+ "\"type\": \"view\"," + "\"name\": \"我的困惑\"," + "\"url\": \"" + WeChatMesUtil.WeChat_Write_2_URL
					+ "\"" + "}, {" + "\"type\": \"view\"," + "\"name\": \"谈谈生活\"," + "\"url\": \""
					+ WeChatMesUtil.WeChat_Write_3_URL + "\"" + "}]" + "}," */
					"{" + "\"type\": \"view\","
					+ "\"name\": \"图片\"," + "\"url\": \"" + WeChatMesUtil.WeChat_Image_URL + "\"" + "},"
					+ "{" + "\"type\": \"view\","
					+ "\"name\": \"个人\"," + "\"url\": \"" + WeChatMesUtil.WeChat_MyWorld_URL + "\"" + "}" + "]" + "}";
			CloseableHttpResponse result = null;
			JSONObject json = null;
			try {
				// 开启http请求准备
				CloseableHttpClient httpclient = HttpClients.createDefault();
				HttpPost httpPost = new HttpPost(url);

				httpPost.addHeader("Content-type", "application/json; charset=utf-8");
				httpPost.setHeader("Accept", "application/json");
				StringEntity stringEntity = new StringEntity(data, Charset.forName("UTF-8"));
				stringEntity.setContentType("text/json");
				stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8"));
				httpPost.setEntity(stringEntity);
				result = httpclient.execute(httpPost);
				String resultStr = EntityUtils.toString(result.getEntity());
				json = JSONObject.fromObject(resultStr);
				httpclient.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				logger.info("请求新建菜单失败");
			}
			// 请求end
			int statusCode = result.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				System.out.println("请求新建菜单失败,连接微信服务器失败");
				logger.info("请求新建菜单失败,连接微信服务器失败");
			} else if (!"0".equals(json.getString("errcode"))) {
				System.out.println("请求数据填写错误,错误代码" + json.getString("errcode"));
				logger.info("请求数据填写错误,错误代码" + json.getString("errcode"));
			} else {
				System.out.println("请求新建菜单成功");
				logger.info("请求新建菜单成功");
				flag = "success";
			}
		} else {
			logger.info("accessToken参数不正确，请重新获取");
		}
		return flag;
	}

	public String getPass() {
		Random r = new Random();
		String pass = Math.abs(r.nextInt()) + "";
		if (pass.length() >= 8) {
			pass = pass.substring(0, 7);
		}
		return pass;
	}

	/**
	 * 实现微信用户点击链接授权后自动登录
	 */
	@Override
	public void weChatLogin(String code) {
		System.out.println("code===================================" + code);
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(
				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WeChatMesUtil.AppID_CS + "&secret="
						+ WeChatMesUtil.AppSecret_CS + "&code=" + code + "&grant_type=authorization_code");
		// Create a custom response handler
		ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {

			public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					if (null != entity) {
						String result = EntityUtils.toString(entity);
						// 根据字符串生成JSON对象
						JSONObject resultObj = JSONObject.fromObject(result);
						return resultObj;
					} else {
						return null;
					}
				} else {
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			}

		};
		System.out.println("请求信息成功-微信自动登录");
		logger.info("请求信息成功-微信自动登录");
		// 返回的json对象
		try {
			JSONObject responseBody = httpclient.execute(httpget, responseHandler);
			System.out.println("responseBody=============" + responseBody.toString());
			logger.info("基本信息=============" + responseBody.toString());
			// 暂时只用到openid
			/*
			 * String access_token = (String) responseBody.get("access_token"); String
			 * refresh_token = (String) responseBody.get("refresh_token");
			 */
			String openid = (String) responseBody.get("openid");
			User u = weChatDao.getUserId(openid);
			// 通过shiro获取session
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession();
			SecurityUtils.getSubject().getSession().setTimeout(600000);
			// 令牌验证登陆
			subject.login(new UsernamePasswordToken(u.getName(), u.getPassword()));
			session.setAttribute("user", u);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
