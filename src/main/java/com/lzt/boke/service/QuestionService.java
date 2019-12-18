package com.lzt.boke.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzt.boke.dto.QuestionDTO;
import com.lzt.boke.dto.QuestionPageInfoDTO;
import com.lzt.boke.mapper.QuestionExtMapper;
import com.lzt.boke.mapper.QuestionMapper;
import com.lzt.boke.mapper.UserMapper;
import com.lzt.boke.model.Question;
import com.lzt.boke.model.QuestionExample;
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
    private UserService userService;

    @Autowired
    private QuestionMapper questionMapper;

    /**
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public QuestionPageInfoDTO list(Integer pageNum, Integer pageSize) {

        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        } else if (pageNum > this.getPages(pageSize, new QuestionExample())) {
            pageNum = this.getPages(pageSize, new QuestionExample());
        }

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionMapper.selectByExample(new QuestionExample());
        PageInfo<Question> pageInfo = new PageInfo<>(questions, 7);
        for (Question question : questions) {
            User user = userService.getUserById(question.getCreator().longValue());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        pageInfo.setNavigateLastPage(this.getPages(pageSize, new QuestionExample()));
        pageInfo.setNavigateFirstPage(1);

        QuestionPageInfoDTO questionPageInfoDTO = new QuestionPageInfoDTO();
        questionPageInfoDTO.setPageInfo(pageInfo);
        questionPageInfoDTO.setQuestionDTOList(questionDTOList);

        return questionPageInfoDTO;
    }

    /**
     * 查找当前用户发布的所有问题
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    public QuestionPageInfoDTO list(Long id, Integer pageNum, Integer pageSize) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(id.intValue());
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        } else if (pageNum > this.getPages(pageSize,questionExample)) {
            pageNum = this.getPages(pageSize, questionExample);
        }

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionMapper.selectByExample(questionExample);
        PageInfo<Question> pageInfo = new PageInfo<>(questions, 7);
        for (Question question : questions) {
            User user = userService.getUserById(question.getCreator().longValue());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        pageInfo.setNavigateLastPage(this.getPages(pageSize, questionExample));
        pageInfo.setNavigateFirstPage(1);

        QuestionPageInfoDTO questionPageInfoDTO = new QuestionPageInfoDTO();
        questionPageInfoDTO.setPageInfo(pageInfo);
        questionPageInfoDTO.setQuestionDTOList(questionDTOList);

        return questionPageInfoDTO;
    }

    /**
     * 计算总页数
     * @param pageSize
     * @param questionExample
     * @return
     */
    public Integer getPages(int pageSize, QuestionExample questionExample) {
        int totalRecords = (int) questionMapper.countByExample(questionExample);
        int totalPages = totalRecords % pageSize == 0 ? totalRecords / pageSize : (totalRecords / pageSize + 1);
        return totalPages;
    }

    /**
     * 添加新问题
     * @param question
     */
    public void insert(Question question) {

        questionMapper.insert(question);
    }

    /**
     * 根据id查找问题
     * @param questionId
     * @return
     */
    public QuestionDTO getById(Long questionId) {
        Question question = questionMapper.selectByPrimaryKey(questionId);

        if (question == null) {
            return null;
        }
        User user = userService.getUserById(question.getCreator().longValue());

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);

        return questionDTO;
    }


}
