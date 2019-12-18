package com.lzt.boke.mapper;


import com.lzt.boke.dto.QuestionDTO;
import com.lzt.boke.model.Question;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionExtMapper {
    int incView(Question record);

    int incCommentCount(Question record);

    List<Question> selectRelated(Question question);

    Integer countBySearch(QuestionDTO questionDTO);

    List<Question> selectBySearch(QuestionDTO questionDTO);
}