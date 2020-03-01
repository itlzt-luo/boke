package com.lzt.boke.controller;

import com.lzt.boke.dto.PageInfoDTO;
import com.lzt.boke.dto.QuestionDTO;
import com.lzt.boke.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                        @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                        @RequestParam(name = "search", required = false) String search) {
        PageInfoDTO<QuestionDTO> list = questionService.list(search,pageNum, pageSize);
        model.addAttribute("questionPageInfoDTO", list);
        model.addAttribute("search", search);
        return "index";
    }

}
