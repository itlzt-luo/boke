package com.lzt.boke.controller;

import com.lzt.boke.dto.QuestionDTO;
import com.lzt.boke.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") String id, Model model) {
        Long questionId = null;
        questionId = Long.parseLong(id);
        QuestionDTO questionDTO = questionService.getById(questionId);

        //累加阅读数
        questionService.incView(questionId);
        model.addAttribute("question", questionDTO);
        //model.addAttribute("comments", comments);
        //model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }


}
