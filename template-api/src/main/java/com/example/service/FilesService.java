package com.example.service;

import org.springframework.web.multipart.MultipartFile;

public interface FilesService {
    String uploadImage(MultipartFile file);
}
