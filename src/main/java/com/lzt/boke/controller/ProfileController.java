package com.lzt.boke.controller;

import com.lzt.boke.dto.QuestionPageInfoDTO;
import com.lzt.boke.model.User;
import com.lzt.boke.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/profile/{action}")
    public String progile(HttpServletRequest request,
                          @PathVariable(name = "action") String action,
                          @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                          @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                          Model model) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            QuestionPageInfoDTO questionPageInfoDTO = questionService.list(user.getId(), pageNum, pageSize);
            model.addAttribute("questionPageInfoDTO", questionPageInfoDTO);
            return "profile";
        } else {
            return "redirect:/";
        }



    }
}
