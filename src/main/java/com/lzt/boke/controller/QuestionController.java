package com.lzt.boke.controller;

import com.lzt.boke.dto.CommentDTO;
import com.lzt.boke.dto.QuestionDTO;
import com.lzt.boke.enums.CommentTypeEnum;
import com.lzt.boke.exception.CustomizeErrorCode;
import com.lzt.boke.exception.CustomizeException;
import com.lzt.boke.model.Question;
import com.lzt.boke.service.CommentService;
import com.lzt.boke.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.standard.expression.Each;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") String id, Model model) {
        Long questionId = null;
        try {
            questionId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new CustomizeException(CustomizeErrorCode.INVALID_INPUT);
        }
        QuestionDTO questionDTO = questionService.getById(questionId);//问题
        List<QuestionDTO> relatedQuestions = questionService.selectRelated(questionDTO);//相关问题
        List<CommentDTO> comments = commentService.listByTargetId(questionId, CommentTypeEnum.QUESTION);//回复
        questionService.incView(questionId);//累加阅读数

        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("relatedQuestions", relatedQuestions);
        return "question";
    }


}
