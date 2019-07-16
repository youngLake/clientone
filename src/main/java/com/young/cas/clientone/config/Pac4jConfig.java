package com.young.cas.clientone.config;

import io.buji.pac4j.context.ShiroSessionStore;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Tornado Young
 * @version 2019/7/16 8:25
 */
@Configuration
public class Pac4jConfig {
    //项目工程路径
    @Value("${cas.project.url}")
    private String projectUrl;
    //项目cas服务路径
    @Value("${cas.server.url}")
    private String casServerUrl;
    //客户端名称
    @Value("${cas.client-name}")
    private String clientName;

    @Bean("authcConfig")
    public Config  config(CasClient casClient, ShiroSessionStore shiroSessionStore){
        Config config=new Config();
        config.setSessionStore(shiroSessionStore);
        Clients clients=new Clients(casClient);
        config.setClients(clients);
        return config;

    }

    @Bean
    public ShiroSessionStore shiroSessionStore(){
        return new ShiroSessionStore();
    }

    @Bean
    public CasClient casClient(CasConfiguration casConfig){
        CasClient casClient=new CasClient(casConfig);
        casClient.setCallbackUrl(projectUrl+"/callback?client_name="+clientName);
        casClient.setName(clientName);
        return casClient;
    }

    @Bean
    public CasConfiguration casConfig(){
        final CasConfiguration casConfiguration=new CasConfiguration();
        casConfiguration.setLoginUrl(casServerUrl+"/login");
        casConfiguration.setProtocol(CasProtocol.CAS20);
        casConfiguration.setAcceptAnyProxy(true);
        casConfiguration.setPrefixUrl(casServerUrl+"/");
        return casConfiguration;
    }
}
