package com.young.cas.clientone.config;

import io.buji.pac4j.context.ShiroSessionStore;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
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
        //CAS30协议可以接受CAS server返回的attitudes
        casConfiguration.setProtocol(CasProtocol.CAS30);
        //如果用CAS20需要修改CAS server的 templates/protocol/2.0/casServiceValidationSuccess.html 文件 （相较于3.0）
        //CAS20在网上搜索的设置html的都无法获取attitudes 有可能是使用了shro的代理filter而没用Cas20ProxyReceivingTicketValidationFilter导致的
        // 待后续有空研究 先使用默认的CAS30
        /*
        3.0
        <cas:attributes th:if="${not #lists.isEmpty(formattedAttributes)}">
            <div th:each="attr : ${formattedAttributes}" th:remove="tag">
                <div th:utext="${attr}" th:remove="tag"/>
            </div>
        </cas:attributes>
        2.0
        <cas th:if="${not #lists.isEmpty(assertion.chainedAuthentications[assertion.chainedAuthentications.size()-1].principal.attributes)}">
            <div th:each="attr : ${assertion.chainedAuthentications[assertion.chainedAuthentications.size()-1].principal.attributes}" th:remove="tag">
                <div th:utext="|<cas:${attr.key}>${#strings.replace(#strings.replace(attr.value,'[',''),']','')}</cas:${attr.key}>|" th:remove="tag"></div>
            </div>
        </cas>
        */
        //casConfiguration.setProtocol(CasProtocol.CAS20);
//        casConfiguration.setDefaultTicketValidator(Cas20ProxyReceivingTicketValidationFilter);
        casConfiguration.setAcceptAnyProxy(true);
        casConfiguration.setPrefixUrl(casServerUrl+"/");
        return casConfiguration;
    }
}
