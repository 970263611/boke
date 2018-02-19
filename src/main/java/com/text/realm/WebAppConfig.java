package com.text.realm;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName: WebAppConfig
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Administrator
 * @date 2017年7月11日
 * 这种办法是一个非常好的访问系统文件夹下图片的办法，只可惜没有使用好，有时间再研究
 */
//@Configuration
//public class WebAppConfig extends WebMvcConfigurerAdapter {
//
//	// 访问图片方法
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/images/**").addResourceLocations("/root/dwq/images"); 
//		registry.addResourceHandler("/images/**").addResourceLocations("file:///E:/images/");
//		super.addResourceHandlers(registry);
//	}
//	
//}
