package com.young.cas.clientone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author Tornado Young
 * @version 2019/7/16 11:24
 */
//@Configuration
public class ViewResolverConfig {
    @Bean
    public InternalResourceViewResolver setupViewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        return resolver;

    }
}
