package com.example.careerblog.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Value("${upload.path}")
	private String uploadPath;

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {	
		registry.addResourceHandler("/img/**")								//	requested path		
				.addResourceLocations("file://" + uploadPath + "/");		//	redirect to file system
		registry.addResourceHandler("/static/**")							//	requested path		
				.addResourceLocations("classpath:/static/");				//	redirect to project folder
	
	}
	
	
}
