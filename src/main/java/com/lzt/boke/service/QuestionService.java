package com.lzt.boke.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzt.boke.dto.PageInfoDTO;
import com.lzt.boke.dto.QuestionDTO;
import com.lzt.boke.dto.QuestionQueryDTO;
import com.lzt.boke.exception.CustomizeErrorCode;
import com.lzt.boke.exception.CustomizeException;
import com.lzt.boke.mapper.QuestionExtMapper;
import com.lzt.boke.mapper.QuestionMapper;
import com.lzt.boke.model.Question;
import com.lzt.boke.model.QuestionExample;
import com.lzt.boke.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    /**
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfoDTO<QuestionDTO> list( String search, Integer pageNum, Integer pageSize) {

        if (!StringUtils.isBlank(search)) {
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);

        int totalRecords = (int) questionExtMapper.countBySearch(questionQueryDTO);
        int totalPages = totalRecords % pageSize == 0 ? totalRecords / pageSize : (totalRecords / pageSize + 1);

        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        } else if (pageNum > totalPages) {
            pageNum = totalPages;
        }

        questionQueryDTO.setPageNum(pageNum);
        questionQueryDTO.setPageSize(pageSize);

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);

        PageInfo<Question> pageInfo = new PageInfo<>(questions, 7);
        pageInfo.setNavigateLastPage(totalPages);
        pageInfo.setNavigateFirstPage(1);

        for (Question question : questions) {
            User user = userService.getUserById(question.getCreator().longValue());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        PageInfoDTO<QuestionDTO> questionPageInfoDTO = new PageInfoDTO<>();
        questionPageInfoDTO.setPageInfo(pageInfo);
        questionPageInfoDTO.setDataCollection(questionDTOList);
        return questionPageInfoDTO;
    }

    /**
     * 查找当前用户发布的所有问题
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfoDTO<Question> list(Long id, Integer pageNum, Integer pageSize) {
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(id);
        questionExample.setOrderByClause("gmt_create desc");
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        } else if (pageNum > this.getPages(pageSize,questionExample)) {
            pageNum = this.getPages(pageSize, questionExample);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Question> questions = questionMapper.selectByExample(questionExample);

        PageInfo<Question> pageInfo = new PageInfo<>(questions, 7);

        pageInfo.setNavigateLastPage(this.getPages(pageSize, questionExample));
        pageInfo.setNavigateFirstPage(1);

        PageInfoDTO<Question> questionPageInfoDTO = new PageInfoDTO<>();
        questionPageInfoDTO.setPageInfo(pageInfo);
        questionPageInfoDTO.setDataCollection(questions);

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

        question.setViewCount(0);
        question.setLikeCount(0);
        question.setCommentCount(0);
        questionMapper.insert(question);
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        } else {
            //更新
            Question dbQuestion = questionMapper.selectByPrimaryKey(question.getId());
            if (dbQuestion == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            if (dbQuestion.getCreator().longValue() != question.getCreator().longValue()) {
                throw new CustomizeException(CustomizeErrorCode.INVALID_OPERATION);
            }

            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);

            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /**
     * 根据id查找问题
     * @param questionId
     * @return
     */
    public QuestionDTO getById(Long questionId) {
        Question question = questionMapper.selectByPrimaryKey(questionId);

        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        User user = userService.getUserById(question.getCreator().longValue());

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);

        return questionDTO;
    }

    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    /**
     * 根据问题标签查找相关问题
     */
    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        String regexpTag = queryDTO.getTag().replace(',', '|');
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);

        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
