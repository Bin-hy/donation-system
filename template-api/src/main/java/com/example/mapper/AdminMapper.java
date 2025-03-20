package com.example.mapper;


import com.example.model.Admin;
import com.example.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("SELECT * FROM `admin` WHERE `username` = #{username}")
    Admin findByUsername(@Param("username") String username);

    // 根据用户 ID（UUID）查找用户
    @Select("SELECT * FROM `admin` WHERE `id` = #{id}")
    Admin findById(@Param("id") String id);

}
