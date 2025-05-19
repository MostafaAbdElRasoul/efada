package com.efada.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//http://localhost:9090/files/profile_1_img.PNG

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
	
	@Value("${attachment.path}")
	private String attachmentsPath;

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/files/**")
            .addResourceLocations("file:"+attachmentsPath+"/");
    }
}
