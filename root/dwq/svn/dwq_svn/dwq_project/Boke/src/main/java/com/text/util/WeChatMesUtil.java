package com.text.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public final class WeChatMesUtil {

	public static final String Token = "dwq_dhbk";// 微信token

	public static final String EncodingAESKey = "zAR6vNswpb7yLtIa2vN6cWepaIJUduy2a77LeG8NeqP";// 微信key

	public static final String AppID = "wx3652b18edba544ec";// 微信开发者id

	public static final String AppSecret = "52bb120d7252187c1771e48e4ac72497";// 微信开发者密码
	
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
	public static Map parseMsgXml(HttpServletRequest request) throws IOException, DocumentException {
		// 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();
        // 从request中取得输入流
        InputStream inputStream = request.getInputStream();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
  
        // 遍历所有子节点
        for (Element e : elementList)
            map.put(e.getName(), e.getText());
  
        // 释放资源
        inputStream.close();
        inputStream = null;
        return map;
	}
	
	 /**   
     * 文本消息对象转换成xml   
     *    
     * @param textMessage 文本消息对象   
     * @return xml   
     */    
	public static String XMLprint(String str){  
        XMLWriter xmlwriter=null;  
        try{  
            org.dom4j.Document  document =null;  
            document=DocumentHelper.parseText(str);  
            OutputFormat format=OutputFormat.createPrettyPrint();  
            StringWriter writer=new StringWriter();  
            xmlwriter=new XMLWriter(writer,format);  
            xmlwriter.write(document);  
            return writer.toString();  
        }catch(Exception e){  
            e.printStackTrace();  
            return str;  
        }finally{  
            if(xmlwriter!=null){  
                try{  
                    xmlwriter.close();  
                }catch(Exception e){  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
}
