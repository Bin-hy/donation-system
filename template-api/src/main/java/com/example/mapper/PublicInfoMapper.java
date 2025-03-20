package com.example.mapper;

import com.example.model.PublicInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PublicInfoMapper {

    List<PublicInfo> getPublicInfoList();

    void updatePublicInfo(PublicInfo publicInfo);
}
