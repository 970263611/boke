package com.text.user.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.text.entity.WeChat;
import com.text.realm.RunFunction;
import com.text.realm.SerializeUtil;
import com.text.user.service.WeChatService;
import com.text.util.SHA1;
import com.text.util.WeChatMesUtil;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

@Service
public class WeChatServiceImpl implements WeChatService{
	
	private static Logger logger = LoggerFactory.getLogger(RunFunction.class);
	
	@Autowired
	private SerializeUtil redisDateSourse;

	/*
	 * (non-Javadoc)
	 * @see com.text.user.service.WeChatService#Verification(com.text.entity.WeChat)
	 * 验证微信发过来的请求
	 */
	@Override
	public String Verification(WeChat weChat){
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
	 * 微信自定义菜单创建
	 */
	@Override
	public String CreateMenu(String data) {
		String flag = "error";
		Jedis jedis = redisDateSourse.getRedis();
		String accessToken = jedis.get("AccessToken");
		if(!"none".equals(accessToken)){
			String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
			/*String data = "{\"button\": [{\"name\":\"公司介绍\", \"sub_button\": [{\"type\": \"click\",\"name\": \"公司简介\",\"key\": \"m_znq\"},{\"type\": \"click\",\"name\": \"关于我们\",\"key\": \"m_xpdz\"},{\"type\": \"click\",\"name\": \"交通方式\",\"key\": \"m_jmt\"}]},";  
	        data += "{\"name\": \"解决方案\",\"sub_button\": [{\"type\": \"click\",\"name\": \"电商解决方案\",\"key\": \"电商解决方案\"},{\"type\": \"click\",\"name\": \"HR人事管理解决方案\",\"key\": \"人事管理解决方案\"},{\"type\": \"click\",\"name\": \"物业管理方案\",\"key\": \"物业管理方案\"}]},";  
	        data += "{\"name\": \"业务领域\",\"sub_button\": [{\"type\": \"view\",\"name\": \"业务范围\",\"url\": \"http://www.haiyusoft.com\"},{\"type\": \"click\",\"name\": \"联合研发中心\",\"key\": \"m_about\"},{\"type\": \"click\",\"name\": \"我要绑定\",\"key\": \"我要绑定\"}]}]}";  */
	        
	        CloseableHttpResponse result = null;
	        try {
	        //开启http请求准备
	        CloseableHttpClient httpclient = HttpClients.createDefault();  
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
	        StringEntity stringEntity = new StringEntity(data);
	        stringEntity.setContentType("text/json");
	        stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	        httpPost.setEntity(stringEntity);
	        result = httpclient.execute(httpPost);
	        httpclient.close(); 
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	System.out.println(e.getMessage());
	        	logger.info("请求新建菜单失败");
	        }
	        //请求end
	        int statusCode = result.getStatusLine().getStatusCode();  
	        if(statusCode !=200){  
	            System.out.println("请求新建菜单失败,连接微信服务器失败");
	        }else{
	        	System.out.println("请求新建菜单成功");
	        	flag = "success";
	        }
		}else{
			logger.info("accessToken参数不正确，请重新获取");
		}
		return flag;
	}

}
