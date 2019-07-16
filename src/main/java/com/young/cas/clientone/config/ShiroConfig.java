package com.young.cas.clientone.config;

import io.buji.pac4j.filter.LogoutFilter;
import io.buji.pac4j.filter.SecurityFilter;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.pac4j.core.config.Config;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Tornado Young
 * @version  2019/7/15 16:21
 */
@Configuration
public class ShiroConfig {
    //项目工程路径
    @Value("${cas.project.url}")
    private String projectUrl;
    //项目cas服务路径
    @Value("${cas.server.url}")
    private String casServerUrl;
    //客户端名称
    @Value("${cas.client-name}")
    private String clientName;

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager(Pac4jSubjectFactory subjectFactory,SessionManager sessionManager,CasRealm casRealm){
        DefaultWebSecurityManager manager=new DefaultWebSecurityManager();
        manager.setRealm(casRealm);
        manager.setSessionManager(sessionManager);
        manager.setSubjectFactory(subjectFactory);
        return manager;
    }

    @Bean
    public CasRealm casRealm(){
        CasRealm casRealm=new CasRealm();
        casRealm.setClientName(clientName);
        //不使用缓存
        casRealm.setCachingEnabled(false);
        casRealm.setAuthenticationCachingEnabled(false);
        casRealm.setAuthorizationCachingEnabled(false);
        return casRealm;
    }

    /**
     * 使用 pac4j 的 subjectFactory
     * @return
     */
    @Bean
    public Pac4jSubjectFactory subjectFactory(){
        return new Pac4jSubjectFactory();
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean bean=new FilterRegistrationBean();
        bean.setFilter(new DelegatingFilterProxy("shiroFilter"));
        bean.addInitParameter("targetFilterLifecycle","true");
        bean.setEnabled(true);
        bean.addUrlPatterns("/*");
        bean.setDispatcherTypes(DispatcherType.REQUEST,DispatcherType.FORWARD);
        return bean;
    }

    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean){
        Map<String,String> filerChainDefinationMap=new LinkedHashMap<>();
        filerChainDefinationMap.put("/", "securityFilter");
        filerChainDefinationMap.put("/clientOne/logon", "securityFilter");
        filerChainDefinationMap.put("/index", "securityFilter");
        filerChainDefinationMap.put("/callback", "callbackFilter");
//        filerChainDefinationMap.put("/clientOne/logout", "anon");
        filerChainDefinationMap.put("/clientOne/logout", "logout");
        filerChainDefinationMap.put("/**","anon");
        // filerChainDefinationMap.put("/user/edit/**", "authc,perms[user:edit]");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filerChainDefinationMap);
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean factory(DefaultWebSecurityManager securityManager, Config config){
        ShiroFilterFactoryBean shiroFilterFactoryBean=new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        loadShiroFilterChain(shiroFilterFactoryBean);
        Map<String, Filter> filters=new HashMap<>(3);
        //cas 资源认证拦截
        SecurityFilter securityFilter=new SecurityFilter();
        securityFilter.setConfig(config);
        securityFilter.setClients(clientName);
        filters.put("securityFilter",securityFilter);
        //cas 回调
        CallbackFilter callbackFilter=new CallbackFilter();
        callbackFilter.setConfig(config);
        callbackFilter.setDefaultUrl(projectUrl);
        filters.put("callbackFilter",callbackFilter);
        // 注销 拦截器
        LogoutFilter logoutFilter=new LogoutFilter();
        logoutFilter.setConfig(config);
        logoutFilter.setCentralLogout(true);
        logoutFilter.setLocalLogout(true);
        logoutFilter.setDefaultUrl(projectUrl+"/callback?client_name="+clientName);
        filters.put("logout",logoutFilter);
        shiroFilterFactoryBean.setFilters(filters);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SessionDAO sessionDAO(){
        return new MemorySessionDAO();
    }

    /**
     * 自定义cookie
     * @return
     */
    @Bean
    public SimpleCookie sessionIdCookie(){
        SimpleCookie cookie=new SimpleCookie("sid");
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        return cookie;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(SimpleCookie sessionIdCookie,SessionDAO sessionDAO){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookie(sessionIdCookie);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setGlobalSessionTimeout(30*60*1000);
        sessionManager.setSessionDAO(sessionDAO);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        return sessionManager;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor advisor=new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public FilterRegistrationBean singleSignOutFilter(){
        FilterRegistrationBean bean=new FilterRegistrationBean();
        bean.setName("singleSignOutFilter");
        SingleSignOutFilter singleSignOutFilter=new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerUrl);
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        bean.setFilter(singleSignOutFilter);
        bean.addUrlPatterns("/*");
        bean.setEnabled(true);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
