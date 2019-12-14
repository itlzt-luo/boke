package com.lzt.boke.dto;

import com.github.pagehelper.PageInfo;
import com.lzt.boke.model.Question;
import lombok.Data;

import java.util.List;

@Data
public class QuestionPageInfoDTO {

    private List<QuestionDTO> questionDTOList;

    private PageInfo<Question> pageInfo;
}
