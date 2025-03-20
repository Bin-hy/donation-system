package com.example.service.impl;

import com.example.service.FilesService;
import com.example.util.AliOSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesServiceImpl implements FilesService {

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 上传图片到阿里云 OSS
            String fileName = file.getOriginalFilename();
            String objectName = "projects/" + fileName; // 你可以根据需要修改路径
            return aliOSSUtils.upload(file.getBytes(), objectName);
        } catch (Exception e) {
            throw new RuntimeException("图片上传失败", e);
        }
    }
}
