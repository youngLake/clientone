package com.young.cas.clientone.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author Tornado Young
 * @version 2019/7/16 9:32
 */
//@Configuration
public class SpringMVCConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);
//        registry.addViewController("/").setViewName("index");
//        registry.addViewController("/clientOne/login").setViewName("index");
//        registry.addViewController("/clientOne/login").setViewName("login");
    }
}
