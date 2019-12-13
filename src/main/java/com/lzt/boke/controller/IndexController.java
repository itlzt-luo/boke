package com.lzt.boke.controller;

import com.lzt.boke.mapper.UserMapper;
import com.lzt.boke.model.User;
import com.lzt.boke.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;
    @GetMapping(value = "/")
    public String hello(HttpServletRequest request, Model model) {
        User user = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie:cookies) {
                if ("token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    user = userMapper.getUserByToken(token);
                    request.getSession().setAttribute("user", user);
                    break;
                }
            }
        }
        model.addAttribute("questions", questionService.setQuestionDTOList());
        return "index";
    }
}
