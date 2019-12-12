package com.lzt.boke.controller;

import com.lzt.boke.mapper.UserMapper;
import com.lzt.boke.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {

    @Autowired
    UserMapper userMapper;
    @GetMapping(value = "/")
    public String hello(HttpServletRequest request) {
        User user = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies) {
            if ("token".equals(cookie.getName())) {
                String token = cookie.getValue();
                user = userMapper.getUserByToken(token);
                request.getSession().setAttribute("user", user);
                break;
            }
        }
        return "index";
    }
}
