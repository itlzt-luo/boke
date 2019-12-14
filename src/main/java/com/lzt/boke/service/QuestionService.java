package com.lzt.boke.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzt.boke.dto.QuestionDTO;
import com.lzt.boke.dto.QuestionPageInfoDTO;
import com.lzt.boke.mapper.QuestionMapper;
import com.lzt.boke.mapper.UserMapper;
import com.lzt.boke.model.Question;
import com.lzt.boke.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionMapper questionMapper;

    public QuestionPageInfoDTO setQuestionDTOListByPageHelper(int pageNum, int pageSize) {

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionMapper.list();
        PageInfo<Question> pageInfo = new PageInfo<>(questions,7);
        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        QuestionPageInfoDTO questionPageInfoDTO = new QuestionPageInfoDTO();
        questionPageInfoDTO.setPageInfo(pageInfo);
        questionPageInfoDTO.setQuestionDTOList(questionDTOList);

        return questionPageInfoDTO;
    }

    public Integer getgetPages() {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PageHelper.startPage(1, 5);
        List<Question> questions = questionMapper.list();
        PageInfo<Question> pageInfo = new PageInfo<>(questions);
        return pageInfo.getPages();
    }
   /* public List<QuestionDTO> setQuestionDTOList() {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        List<Question> questions = questionMapper.list();

        for (Question question : questions) {
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        return questionDTOList;
    }*/
}
