package com.young.cas.clientone.config;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author Tornado Young
 * @version 2019/7/16 8:47
 */
public class CallbackFilter extends io.buji.pac4j.filter.CallbackFilter{
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        super.doFilter(servletRequest, servletResponse, filterChain);
    }
}
