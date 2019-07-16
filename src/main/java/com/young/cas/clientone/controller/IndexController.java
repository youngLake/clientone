package com.young.cas.clientone.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Tornado Young
 * @version 2019/7/16 13:29
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/index")
    public String index(){
        Subject subject= SecurityUtils.getSubject();
        System.err.println(subject.isAuthenticated());
        return "index";
    }
}
