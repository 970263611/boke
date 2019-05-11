package com.dahua.boke.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class BokeUtil {
	
	/**
	 * 获取当前时间转String
	 */
	public static String getStringTime(){
		Date date = new Date();
        //设置要获取到什么样的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取String类型的时间
        String time = sdf.format(date);
        return time;
	}
	
	/**
	 * 获取当前登录ip
	 */
	public static String getIP(HttpServletRequest request){
		String ipAddress = request.getHeader("x-forwarded-for");  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getHeader("Proxy-Client-IP");  
        }  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {  
            ipAddress = request.getRemoteAddr();  
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){  
                //根据网卡取本机配置的IP  
                InetAddress inet=null;  
                try {  
                    inet = InetAddress.getLocalHost();  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
                ipAddress= inet.getHostAddress();  
            }  
        }  
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15  
            if(ipAddress.indexOf(",")>0){  
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));  
            }  
        }
        return ipAddress;
	}

	public static boolean checkNull(String str){
	    if(str==null || "".equals(str)){
	        return false;
        }
	    return true;
    }

    public static Session getSession(){
        Subject subject= SecurityUtils.getSubject();
        Session session=subject.getSession();
        return session;
    }
}
