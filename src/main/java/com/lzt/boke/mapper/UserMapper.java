package com.lzt.boke.mapper;

import com.lzt.boke.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {
    @Insert("insert into user (id,name,account_id,token,gmt_create,gmt_modified) values (1,#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    void insert(User user);
}
