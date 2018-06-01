package com.text.user.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.text.entity.WeChat;
import com.text.user.service.WeChatService;
import com.text.util.WeChatMesUtil;
import com.text.util.SHA1;

@Service
public class WeChatServiceImpl implements WeChatService{

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

}
