package com.lzt.boke.controller;

import com.github.pagehelper.PageInfo;
import com.lzt.boke.dto.QuestionDTO;
import com.lzt.boke.dto.QuestionPageInfoDTO;
import com.lzt.boke.mapper.UserMapper;
import com.lzt.boke.model.Question;
import com.lzt.boke.model.User;
import com.lzt.boke.service.QuestionService;
import com.lzt.boke.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class IndexController {
    final Integer pageSize = 5;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;
    @GetMapping(value = "/")
    public String hello(@RequestParam(name = "pageNum",defaultValue = "1") Integer pageNum,
                        HttpServletRequest request,
                        Model model) {
        User user = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie:cookies) {
                if ("token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    user = userService.getUserByToken(token);
                    request.getSession().setAttribute("user", user);
                    break;
                }
            }
        }
        model.addAttribute("questionPageInfoDTO", questionService.list(pageNum, pageSize));
        return "index";
    }
}
