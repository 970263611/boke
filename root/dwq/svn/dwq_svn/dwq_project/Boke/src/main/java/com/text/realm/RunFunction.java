package com.text.realm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.text.entity.Article;
import com.text.user.dao.UserDao;
import com.text.util.RedisUtil;
import com.text.util.WeChatMesUtil;

import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

@Component  
public class RunFunction {
	
	private static Logger logger = LoggerFactory.getLogger(RunFunction.class);
	
	@Autowired
	private SerializeUtil redisDateSourse;
	@Autowired
	private UserDao userDao;
	
	//获取微信token定时方法
	@Scheduled(cron="0 0 */2 * * ?") //每2小时执行一次  
    public void statusCheck() {      
        logger.info(new Date()+"dwq---执行---");  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        Jedis jedis = redisDateSourse.getRedis();
        try {  
            //利用get形式获得token  
            HttpGet httpget = new HttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+WeChatMesUtil.AppID+"&secret="+WeChatMesUtil.AppSecret);  
            // Create a custom response handler  
            ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {  
  
                public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {  
                    int status = response.getStatusLine().getStatusCode();  
                    if (status >= 200 && status < 300) {  
                        HttpEntity entity = response.getEntity();  
                        if(null!=entity){  
                            String result= EntityUtils.toString(entity);  
                            //根据字符串生成JSON对象  
                            JSONObject resultObj = JSONObject.fromObject(result);  
                            return resultObj;  
                        }else{  
                            return null;  
                        }  
                    } else {  
                        throw new ClientProtocolException("Unexpected response status: " + status);  
                    }  
                }
  
            };  
            //返回的json对象  
            JSONObject responseBody = httpclient.execute(httpget, responseHandler);  
            String token="";  
            if(null!=responseBody){  
                token= (String) responseBody.get("access_token");//返回token  
            }  
            //System.out.println("----------------------------------------");  
            //System.out.println("access_token:"+responseBody.get("access_token"));  
            //System.out.println("expires_in:"+responseBody.get("expires_in"));  
  
            httpclient.close();  
            jedis.set("AccessToken", token);
            System.out.println("获取AccessToken成功-------------"+ token);
            
        }catch (Exception e) {  
            e.printStackTrace();  
            jedis.set("AccessToken", "none");
        }   
        redisDateSourse.closeRedis(jedis);
    }    
	
	//获取微信测试号token定时方法
	@Scheduled(cron="0 0 */2 * * ?") //每2小时执行一次  
    public void statusCheck_CS() {      
        logger.info(new Date()+"dwq---测试号执行---");  
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        Jedis jedis = redisDateSourse.getRedis();
        try {  
            //利用get形式获得token  
            HttpGet httpget = new HttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+WeChatMesUtil.AppID_CS+"&secret="+WeChatMesUtil.AppSecret_CS);  
            // Create a custom response handler  
            ResponseHandler<JSONObject> responseHandler = new ResponseHandler<JSONObject>() {  
  
                public JSONObject handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {  
                    int status = response.getStatusLine().getStatusCode();  
                    if (status >= 200 && status < 300) {  
                        HttpEntity entity = response.getEntity();  
                        if(null!=entity){  
                            String result= EntityUtils.toString(entity);  
                            //根据字符串生成JSON对象  
                            JSONObject resultObj = JSONObject.fromObject(result);  
                            return resultObj;  
                        }else{  
                            return null;  
                        }  
                    } else {  
                        throw new ClientProtocolException("Unexpected response status: " + status);  
                    }  
                }
  
            };  
            //返回的json对象  
            JSONObject responseBody = httpclient.execute(httpget, responseHandler);  
            String token="";  
            if(null!=responseBody){  
                token= (String) responseBody.get("access_token");//返回token  
            }  
            //System.out.println("----------------------------------------");  
            //System.out.println("access_token:"+responseBody.get("access_token"));  
            //System.out.println("expires_in:"+responseBody.get("expires_in"));  
  
            httpclient.close();  
            jedis.set("AccessToken_CS", token);
            System.out.println("获取AccessToken_CS成功-------------"+ token);
            
        }catch (Exception e) {  
            e.printStackTrace();  
            jedis.set("AccessToken_CS", "none");
        }   
        redisDateSourse.closeRedis(jedis);
    }    
	
	
	//获取微信token定时方法
		@Scheduled(cron="0 0 5 * * ?") //每天早上5点执行一次  
	    public void beachSaveSee() {      
	        logger.info(new Date()+"dwq---存储文章访问量执行---");  
	        Jedis jedis = redisDateSourse.getRedis();
	        List<String> tops = jedis.lrange("top", 0, -1);
	        List<String> untops = jedis.lrange("article", 0, -1);
	        tops.addAll(untops);
	        List<Article> articles = RedisUtil.hgetArticle(tops,jedis);
	        userDao.beachSaveSee(articles);
	        System.out.println("存储文章访问量到数据库成功------");
	        redisDateSourse.closeRedis(jedis);
	    }    
}
