package com.example.service;

import com.example.model.Admin;
import com.example.model.User;
import com.example.util.Result;
import com.example.vo.AdminInfoVO;
import com.example.vo.UserInfoVO;

public interface AdminService {
    Admin login(String username, String password) throws Exception;

    Result<AdminInfoVO> getUserInfo();
}
