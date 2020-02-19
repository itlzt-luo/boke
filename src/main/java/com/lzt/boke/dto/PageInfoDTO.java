package com.lzt.boke.dto;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.Collection;

@Data
public class PageInfoDTO<T> {

    private Collection<T> dataCollection;

    private PageInfo pageInfo;
}
