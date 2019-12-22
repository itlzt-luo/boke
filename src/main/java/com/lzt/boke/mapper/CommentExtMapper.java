package com.lzt.boke.mapper;

import com.lzt.boke.model.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentExtMapper {

    void incCommentCount(Comment parentComment);
}
