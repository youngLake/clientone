package com.young.cas.clientone.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Tornado Young
 * @version 2019/7/16 9:14
 */
@Controller
@RequestMapping(value = "/clientOne")
public class LoginController {

    @RequestMapping(value = "/login")
    public String login(String username,String passwd){
        Subject subject= SecurityUtils.getSubject();
        if (subject.isAuthenticated()){
            UsernamePasswordToken token=new UsernamePasswordToken(username,passwd);
//            token.setRememberMe(true);
            try {
                subject.login(token);
            } catch (UnknownAccountException uae) {
                System.err.println("用户不存在 " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
                System.err.println("密码不正确： " + token.getPrincipal() + "!");
            } catch (LockedAccountException lae) {
                System.err.println("账号被锁定： " + token.getPrincipal() + " is locked.  ");
            } catch (AuthenticationException ae) { // ... catch more exceptions here (maybe custom ones
                // specific to your application?
                System.err.println("登录失败" + ae);
            }
        }
        return "index";
    }

    @RequestMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "login";
    }
}
