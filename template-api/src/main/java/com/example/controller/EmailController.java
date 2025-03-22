package com.example.controller;

import com.example.dto.EmailDTO;
import com.example.service.EmailService;
import com.example.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/email")
@CrossOrigin
public class EmailController {
    // 发送邮箱验证邮件
    @Autowired
    private EmailService emailService;
    @PostMapping("/register")
    public Result sendEmail(@RequestBody EmailDTO emailDTO) {
        try{
            emailService.sendRegistEmail(emailDTO.getEmail());
        }catch (Exception e){
            return Result.error("邮箱发送失败");
        }
        return Result.success();
    }

    @PostMapping("/findPassword")
    public Result sendFindPasswordEmail(@RequestBody EmailDTO emailDTO) {
        try{
            emailService.sendPasswordFindEmail(emailDTO.getEmail());
        }catch (Exception e){
            return Result.error("邮箱发送失败");
        }
        return Result.success();
    }


}
