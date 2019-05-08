package com.text.realm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.alibaba.fastjson.JSON;
import com.text.user.service.UserService;

/**
 * 重写WebSocket方法
 * @author loveding
 *
 */
//javax的websocket，这里弃用了改用spring websocket   @ServerEndpoint(value = "/websocket/socketServer"/* ,configurator=GetHttpSessionConfigurator.class */)
//@Component
public class SpringWebSocketHandler extends TextWebSocketHandler {
	
	Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);
	
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
	
	@Autowired 
	private UserService userService;
	
    public  static SpringWebSocketHandler swsh ;
    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
    	swsh = this;
    	swsh.userService = this.userService;
        //初使化时将已静态化的userService实例化
    }
    
    private String loginUUID;
	
	private final static List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<WebSocketSession>());
	
    //接收文本消息，并发送出去
    @SuppressWarnings("rawtypes")
	@Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //super.handleTextMessage(session, message);
    	this.loginUUID = message.getPayload();
    	System.out.println("来自客户端的消息:" + message);
        //查询redis中键是够改变确定用户是够扫码
        HashMap map = swsh.userService.checkSweep(message.getPayload(),session);
        try {
			session.sendMessage(new TextMessage(JSON.toJSONString(map)));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    //连接建立后处理
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	sessions.add(session);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
        	session.sendMessage(new TextMessage(""));
        } catch (IOException e) {
            System.out.println("IO异常");
        }
        //处理离线消息
    }
    //抛出异常时处理
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        logger.debug("websocket chat connection closed......");
        sessions.remove(session);
    }
    //连接关闭后处理
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
        try {
			swsh.userService.delLoginUUID(loginUUID);
			logger.debug("websocket关闭，删除loginUUID成功");
		} catch (Exception e) {
			logger.debug("websocket关闭，删除loginUUID失败,键已经不存在");
			e.printStackTrace();
		}
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    
    
    /** 
     * 给某个用户发送消息 
     * 
     * @param userName 
     * @param message 
     */  
    public void sendMessage(String userName, TextMessage message) {  
        for (WebSocketSession user : sessions) {  
            if (user.getAttributes().get("WEBSOCKET_USERNAME").equals(userName)) {  
                try {  
                    if (user.isOpen()) {  
                        user.sendMessage(message);  
                    }  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                break;  
            }  
        }  
    }  
     
    /** 
     * 给所有在线用户发送消息 
     * 
     * @param message 
     */  
    public void sendMessageToUsers(TextMessage message) {  
        for (WebSocketSession user : sessions) {  
            try {  
                if (user.isOpen()) {  
                    user.sendMessage(message);  
                }  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    } 
    
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        SpringWebSocketHandler.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        SpringWebSocketHandler.onlineCount--;
    }
	
	/**  javax的websocket，这里弃用了改用spring websocket
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的SpringWebSocketHandler对象。
    private static CopyOnWriteArraySet<SpringWebSocketHandler> webSocketSet = new CopyOnWriteArraySet<SpringWebSocketHandler>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
	
	@Autowired 
	private UserService userService;
	
    public  static SpringWebSocketHandler swsh ;
    @PostConstruct //通过@PostConstruct实现初始化bean之前进行的操作
    public void init() {
    	swsh = this;
    	swsh.userService = this.userService;
        //初使化时将已静态化的userService实例化
    }
    
    private HttpSession httpSession;
    
    private String loginUUID;
    
     * 连接建立成功调用的方法
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        try {
            sendMessage("");
        } catch (IOException e) {
            System.out.println("IO异常");
        }
    }

      连接关闭调用的方法
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
        try {
			swsh.userService.delLoginUUID(loginUUID);
			logger.debug("websocket关闭，删除loginUUID成功");
		} catch (Exception e) {
			logger.debug("websocket关闭，删除loginUUID失败,键已经不存在");
			e.printStackTrace();
		}
    }

     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        this.loginUUID = message;
        //查询redis中键是够改变确定用户是够扫码
        String state = swsh.userService.checkSweep(message,httpSession);
        try {
			sendMessage(state);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 * //群发消息 for (SpringWebSocketHandler item : webSocketSet) { try {
		 * item.sendMessage(message); } catch (IOException e) { e.printStackTrace(); } }
    }

    
      发生错误时调用
     
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }


    
      群发自定义消息
     
    public static void sendInfo(String message) throws IOException {
        for (SpringWebSocketHandler item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        SpringWebSocketHandler.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        SpringWebSocketHandler.onlineCount--;
    }
    */
}

