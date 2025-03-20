package com.example.service.impl;

import com.example.mapper.PublicInfoMapper;
import com.example.model.PublicInfo;
import com.example.service.PublicInfoService;
import com.example.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PublicInfoServiceImpl implements PublicInfoService {

    @Autowired
    private PublicInfoMapper publicInfoMapper;

    @Override
    public Result<List<PublicInfo>> getPublicInfoList() {
        try {
            List<PublicInfo> publicInfoList = publicInfoMapper.getPublicInfoList();
            return Result.success(publicInfoList);
        } catch (Exception e) {
            return Result.error("获取公示信息列表失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> updatePublicInfo(String id, PublicInfo publicInfo) {
        try {
            publicInfo.setId(id); // 设置 ID
            publicInfoMapper.updatePublicInfo(publicInfo);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}
