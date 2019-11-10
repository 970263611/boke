package com.boke.realm;

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class SpringHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

	@Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		Subject subject= SecurityUtils.getSubject();
        Session session=subject.getSession();
//		System.out.println("beforeHandshake");
		attributes.put("shiroSubject", subject);
		//HttpSession session = getSession(request);
		attributes.put("shiroSession", session);
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        System.out.println("afterHandshake");
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
