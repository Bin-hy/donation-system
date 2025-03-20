package com.example.service;

import com.example.model.PublicInfo;
import com.example.util.Result;

import java.util.List;

public interface PublicInfoService {
    Result<String> updatePublicInfo(String id, PublicInfo publicInfo);

    Result<List<PublicInfo>> getPublicInfoList();
}
