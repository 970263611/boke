package com.text.realm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;


/**
 * 注册普通WebScoket
 */
@Configuration
//@EnableWebMvc  //这个注解会拦截静态文件，贼几把坑草
@EnableWebSocket
//@EnableWebSocketMessageBroker   spring消息队列时候用这个注解，第三个去掉
public class SpringWebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    	//允许连接的域,只能以http或https开头
        //String[] allowsOrigins = {"http://www.xxx.com"};
        registry.addHandler(logWebSocketHandler(), "/websocket/socketServer").setAllowedOrigins("*").addInterceptors(new SpringHandshakeInterceptor());//.setAllowedOrigins(allowsOrigins); // 此处与客户端的 URL 相对应
        registry.addHandler(logWebSocketHandler(), "/websocket/socketServer").setAllowedOrigins("*").addInterceptors(new SpringHandshakeInterceptor()).withSockJS();//addInterceptors将拦截器添加进来
    }

    @Bean
    public WebSocketHandler logWebSocketHandler() {
        return new SpringWebSocketHandler();
    }
    
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        //这个对象说一下，貌似只有服务器是tomcat的时候才需要配置,具体我没有研究
        return new ServerEndpointExporter();
    }

    
    /*
    @Override   spring消息队列时候用这两个方法
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        System.out.println("注册");
        registry.addEndpoint("/hello").withSockJS(); // 注册端点，和普通服务端的/log一样的
        // withSockJS()表示支持socktJS访问，在浏览器中使用
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        System.out.println("启动");
        config.enableSimpleBroker("/topic"); // 
        config.setApplicationDestinationPrefixes("/app"); // 格式前缀
    }
	*/
}
