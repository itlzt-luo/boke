package com.lzt.boke.dto;

import lombok.Data;

@Data
public class QuestionQueryDTO {
    private String search;
    private Integer pageNum;
    private Integer pageSize;
}
