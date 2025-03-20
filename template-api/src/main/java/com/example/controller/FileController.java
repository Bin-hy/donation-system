package com.example.controller;


import com.example.service.FilesService;
import com.example.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileController {

    @Autowired
    private FilesService filesService;

    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 调用服务层方法处理图片上传
            String imageUrl = filesService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            return Result.error("图片上传失败");
        }
    }
}
