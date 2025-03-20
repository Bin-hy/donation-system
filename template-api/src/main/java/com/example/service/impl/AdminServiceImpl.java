package com.example.service.impl;

import com.example.context.BaseContext;
import com.example.mapper.AdminMapper;
import com.example.mapper.UserMapper;
import com.example.model.Admin;
import com.example.model.User;
import com.example.service.AdminService;
import com.example.util.Result;
import com.example.vo.AdminInfoVO;
import com.example.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin login(String username, String password) throws Exception {
        Admin admin = adminMapper.findByUsername(username);
        if (admin == null || !admin.getPassword().equals(password)) {
            throw new Exception("用户或密码错误");
        }

        return admin;
    }

    @Override
    public Result<AdminInfoVO> getUserInfo() {
        // 2. 根据用户 ID 查询用户信息
        Admin admin = adminMapper.findById("12138");
        if (admin == null) {
            return Result.error("用户不存在");
        }

        // 3. 构造 UserInfoVO 对象
        AdminInfoVO adminInfoVO = new AdminInfoVO();
        adminInfoVO.setNickname(admin.getNickname()); // 设置用户昵称
        adminInfoVO.setAvatarUrl(admin.getAvatarUrl());     // 设置用户头像

        // 4. 返回用户信息
        return Result.success(adminInfoVO);
    }

}
