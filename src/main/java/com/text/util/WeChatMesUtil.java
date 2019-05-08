package com.text.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public final class WeChatMesUtil {

	public static final String Token = "";// 微信token

	public static final String EncodingAESKey = "";// 微信key

	public static final String AppID = "";// 微信开发者id

	public static final String AppSecret = "";// 微信开发者密码
	
	public static final String AppID_CS = "";// 微信开发者id
	
	public static final String AppSecret_CS = "";// 微信开发者密码
	
	public static final String Account_Number_CS = "";//测试公众号
	
	public static final String LoginURL = "http://www.loveding.top/login";
	
	public static final String SaomaURL = "http://www.loveding.top/Sweep";
	
	public static final String WeChat_Write_1_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+AppID_CS+"&redirect_uri="+LoginURL+"&response_type=code&scope=snsapi_base&state=write1#wechat_redirect";
	
	public static final String WeChat_Write_2_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+AppID_CS+"&redirect_uri="+LoginURL+"&response_type=code&scope=snsapi_base&state=write2#wechat_redirect";
	
	public static final String WeChat_Write_3_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+AppID_CS+"&redirect_uri="+LoginURL+"&response_type=code&scope=snsapi_base&state=write3#wechat_redirect";
	
	public static final String WeChat_Index_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+AppID_CS+"&redirect_uri="+LoginURL+"&response_type=code&scope=snsapi_base&state=index#wechat_redirect";
	
	public static final String WeChat_MyWorld_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+AppID_CS+"&redirect_uri="+LoginURL+"&response_type=code&scope=snsapi_base&state=myworld#wechat_redirect";
	
	public static final String WeChat_Image_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+AppID_CS+"&redirect_uri="+LoginURL+"&response_type=code&scope=snsapi_base&state=images#wechat_redirect";
	
	public static final String WeChat_Saoma_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+AppID_CS+"&redirect_uri="+SaomaURL+"&response_type=code&scope=snsapi_base&state=#wechat_redirect";
	
	/**   
     * 返回消息类型：文本   
     */    
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";    
  
    /**   
     * 返回消息类型：音乐   
     */    
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";    
  
    /**   
     * 返回消息类型：图文   
     */    
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";    
  
    /**   
     * 请求消息类型：文本   
     */    
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";    
  
    /**   
     * 请求消息类型：图片   
     */    
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";    
  
    /**   
     * 请求消息类型：链接   
     */    
    public static final String REQ_MESSAGE_TYPE_LINK = "link";    
  
    /**   
     * 请求消息类型：地理位置   
     */    
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";    
  
    /**   
     * 请求消息类型：音频   
     */    
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";    
  
    /**   
     * 请求消息类型：推送   
     */    
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";    
  
    /**   
     * 事件类型：subscribe(订阅)   
     */    
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";    

    /**   
     * 事件类型：unsubscribe(取消订阅)   
     */    
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";    
  
    /**   
     * 事件类型：CLICK(自己定义菜单点击事件)   
     */    
    public static final String EVENT_TYPE_CLICK = "CLICK";    
     //定义一个私有的静态全局变量来保存该类的唯一实例  
	/**
	 * 解析xml方法
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map parseMsgXml(HttpServletRequest request){
		// 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        // 读取输入流
        SAXReader reader = new SAXReader();
        // 从request中取得输入流
        InputStream is = null;
        try{
        	is = request.getInputStream();
        	// 得到xml根元素
        	Document doc = reader.read(is);
        	Element root = doc.getRootElement();
        	// 得到根元素的所有子节点
        	List<Element> elementList = root.elements();
        	// 遍历所有子节点
        	for (Element e : elementList){
        		map.put(e.getName(), e.getText());
        	}
        }catch(Exception e){
        	try{
        		is.close();
        	}catch(IOException e1){
        		e1.printStackTrace();
        	}
        }
        return map;
	}
	
	 /**   
     * 文本消息对象转换成xml   
     *    
     * @param textMessage 文本消息对象   
     * @return xml   
     */    
	public static void XMLprint(HttpServletResponse response,String content,String toUserName,String fromUserName,String msgType/*这里暂时都定义为文本回复形式*/){
		PrintWriter out;
		String xmlStr;
		if(content==null || toUserName==null || fromUserName==null){
			xmlStr = "success";
		}else{
			Date date=new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String createTime = formatter.format(date);
			
			Document document = DocumentHelper.createDocument();  
			Element rootElement = document.addElement("xml");
			//这里要注意，发送给微信后身份调换，要注意touser和fromuser的位置
			Element ToUserName = rootElement.addElement("ToUserName");  
			ToUserName.addCDATA(fromUserName);  
			Element FromUserName = rootElement.addElement("FromUserName");  
			FromUserName.addCDATA(toUserName);  
			Element CreateTime = rootElement.addElement("CreateTime");  
			CreateTime.addText(createTime);  
			/*这里暂时都定义为文本回复形式*/
			Element MsgType = rootElement.addElement("MsgType");  
			MsgType.addCDATA("text"); 
			Element Content = rootElement.addElement("Content");  
			Content.addCDATA(content); 
			
			xmlStr=document.getRootElement().asXML();
		}
		
		
		try {
			System.out.println(xmlStr);
			out = response.getWriter();
			out.print(xmlStr);  
			out.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    }  
}
