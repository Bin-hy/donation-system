package com.example.service.impl;

import com.example.context.BaseContext;
import com.example.dto.*;
import com.example.handler.WebSocketHandler;
import com.example.mapper.ProjectMapper;
import com.example.mapper.UserMapper;
import com.example.model.Danmu;
import com.example.model.Feedback;
import com.example.model.FinanceRecord;
import com.example.model.User;
import com.example.service.UserService;
import com.example.util.BaiduMapUtil;
import com.example.util.JwtUtil;
import com.example.util.Result;
import com.example.vo.BarrageVO;
import com.example.vo.FeedbackVO;
import com.example.vo.UserInfoVO;
import com.example.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private BaiduMapUtil baiduMapUtil;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public User login(String username, String password) throws Exception {
        // 根据用户名查找用户
        User user = userMapper.findByUsername(username);

        // 检查用户是否存在
        if (user == null) {
            throw new Exception("用户不存在");
        }

        // 检查密码是否正确
        if (!user.getPassword().equals(password)) {
            throw new Exception("密码错误");
        }

        // 检查用户是否被封禁
        if (user.getIsBanned() == 1) {
            throw new Exception("您的账号已被封禁，请联系管理员");
        }

        // 返回用户信息
        return user;
    }

    @Override
    public Result<String> register(RegisterRequest registerRequest) {
        // 1. 检查用户名是否已存在
        if (userMapper.existsByUsername(registerRequest.getUsername()) > 0) {
            return Result.error("用户名已存在");
        }

        // 2. 创建用户对象
        User user = new User();
        user.setId(UUID.randomUUID().toString()); // 生成唯一 ID
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setIsBanned(0);
        user.setIsBarrageBanned(0);

        // 3. 自动生成唯一的默认昵称（格式：用户 + 随机的4位数）
        String nickname;
        Random random = new Random();
        do {
            int randomNumber = random.nextInt(9000) + 1000; // 范围：1000 ~ 9999
            nickname = "用户" + randomNumber;
        } while (userMapper.existsByNickname(nickname)); // 检查昵称是否已存在
        user.setNickname(nickname);

        // 4. 设置默认头像和创建时间
        user.setAvatar("https://pisiyi.oss-cn-beijing.aliyuncs.com/%E7%94%A8%E6%88%B7.png"); // 设置默认头像
        user.setCreatedAt(LocalDateTime.now()); // 设置创建时间
        user.setUpdatedAt(LocalDateTime.now()); // 设置修改时间

        // 5. 保存用户信息
        userMapper.save(user);

        // 6. 返回成功响应
        return Result.success("注册成功");
    }

    @Override
    public Result<UserInfoVO> getUserInfo(String token) {
        String userId = BaseContext.getCurrentId();
        // 2. 根据用户 ID 查询用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 构造 UserInfoVO 对象
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setNickname(user.getNickname()); // 设置用户昵称
        userInfo.setAvatar(user.getAvatar());     // 设置用户头像

        // 4. 返回用户信息
        return Result.success(userInfo);
    }

    @Override
    public List<User> getUsers(String username, String nickname, String email, String phone) {
        // 2. 调用 Mapper 查询用户列表
        return userMapper.findUsers(username, nickname, email, phone);

    }

    @Override
    public void toggleBanUser(String userId) {
        // 查询用户
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 切换封禁状态
        user.setIsBanned(user.getIsBanned() == 0 ? 1 : 0);

        // 保存更新
        userMapper.updateUser(user);
    }

    @Override
    public void updateUser(User user) {
        // 1. 查询当前用户信息
        User existingUser = userMapper.findById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 只允许更新邮箱和电话
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setUpdatedAt(LocalDateTime.now()); // 更新修改时间

        // 3. 调用 Mapper 更新用户信息
        userMapper.update(existingUser);
    }

    @Override
    public void batchDeleteUsers(List<String> ids) {
        // 1. 检查 ID 列表是否为空
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("用户 ID 列表不能为空");
        }

        // 2. 调用 Mapper 批量删除用户
        userMapper.batchDelete(ids);
    }


    @Override
    public void toggleBarrageBan(String userId, boolean isBarrageBanned) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setIsBarrageBanned(isBarrageBanned ? 1 : 0);
            userMapper.updateUser(user);
        } else {
            throw new RuntimeException("用户不存在");
        }
    }

    @Override
    public List<BarrageVO> getBarrages(String username, String nickname, Date startDate, Date endDate) {
        return userMapper.selectBarrages(username, nickname, startDate, endDate);
    }

    @Override
    public void deleteBarrage(String id) {
        userMapper.deleteBarrageById(id);
    }

    @Override
    public UserProfileVO getUserProfile(String userId) {
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 转换为 VO 对象
        UserProfileVO userProfileVO = new UserProfileVO();
        userProfileVO.setNickname(user.getNickname());
        userProfileVO.setUsername(user.getUsername());
        userProfileVO.setEmail(user.getEmail());
        userProfileVO.setPhone(user.getPhone());
        userProfileVO.setAvatar(user.getAvatar());
        return userProfileVO;
    }

    @Override
    public void saveUserProfile(String userId, UserProfileDTO userProfileDTO) {
        // 查询当前用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查昵称是否唯一
        if (userProfileDTO.getNickname() != null && !userProfileDTO.getNickname().equals(user.getNickname())) {
            // 查询是否存在相同昵称的用户
            User existingUser = userMapper.findByNickname(userProfileDTO.getNickname());
            if (existingUser != null) {
                throw new RuntimeException("昵称已被占用，请使用其他昵称");
            }
        }

        // 更新用户信息
        if (userProfileDTO.getNickname() != null) {
            user.setNickname(userProfileDTO.getNickname());
        }
        if (userProfileDTO.getEmail() != null) {
            user.setEmail(userProfileDTO.getEmail());
        }
        if (userProfileDTO.getPhone() != null) {
            user.setPhone(userProfileDTO.getPhone());
        }
        if (userProfileDTO.getAvatar() != null) {
            user.setAvatar(userProfileDTO.getAvatar());
        }
        if (userProfileDTO.getPassword() != null) {
            // 更新密码
            user.setPassword(userProfileDTO.getPassword());
        }

        // 更新修改时间
        user.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        userMapper.updateById(user);
    }

    @Override
    public void addBarrage(String userId, String content) {
        // 1. 检查用户是否被禁止发送弹幕
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getIsBarrageBanned() == 1) { // 1 表示禁止发送弹幕
            throw new RuntimeException("您已被禁止发送弹幕");
        }

        // 2. 生成 UUID
        String danmuId = UUID.randomUUID().toString();

        // 3. 创建弹幕对象
        Danmu danmu = new Danmu();
        danmu.setId(danmuId);
        danmu.setUserId(userId);
        danmu.setContent(content);
        danmu.setCreatedAt(LocalDateTime.now());

        // 4. 调用数据访问层添加弹幕
        userMapper.addBarrage(danmu);
    }

    @Override
    public void submitFeedback(FeedbackDTO feedbackDTO) {
        // 获取用户反馈信息
        String userName = feedbackDTO.getName(); // 用户姓名
        String userPhone = feedbackDTO.getPhone(); // 用户电话
        String feedbackContent = feedbackDTO.getContent(); // 反馈内容

        // 组合成一段消息
        String message = "接收到 " + userName + " 用户的项目反馈内容：" + feedbackContent + "请拨打电话：" + userPhone + "，尽快与用户确认具体情况。";
        webSocketHandler.sendReplyMessage("12138",message,"Feedback");

        String userId = BaseContext.getCurrentId();
        Feedback feedback = new Feedback();
        feedback.setId(String.valueOf(UUID.randomUUID()));
        feedback.setUserId(userId);
        feedback.setProjectId(feedbackDTO.getProjectId());
        feedback.setPhone(userPhone);
        feedback.setName(userName);
        feedback.setCreateTime(LocalDateTime.now());
        feedback.setMessage(message);
        // 4. 将反馈信息存储到数据库表中，调用 UserMapper 进行插入操作
        userMapper.insertFeedback(feedback);
    }



    @Override
    public int deleteBatch(List<String> ids) {
        return userMapper.deleteBatch(ids);
    }

    @Override
    public List<FeedbackVO> getFeedbackVO(String name, LocalDateTime startDate, LocalDateTime endDate) {

        return userMapper.getFeedback(name, startDate, endDate);

    }

    @Override
    public Result<String> submitDonation(FinanceRecordDTO financeRecordDTO) {
        FinanceRecord financeRecord = new FinanceRecord();
        financeRecord.setAmount(financeRecordDTO.getAmount());
        financeRecord.setId(String.valueOf(UUID.randomUUID()));
        financeRecord.setProjectId(financeRecordDTO.getProjectId());
        financeRecord.setCategory("资金捐赠");
        financeRecord.setUserId(BaseContext.getCurrentId());
        financeRecord.setPaymentMethod(financeRecordDTO.getPaymentMethod());

        // 获取地址
        String location = baiduMapUtil.getLocationFromCoordinates(financeRecordDTO.getLatitude(), financeRecordDTO.getLongitude());
        financeRecord.setAddress(location);

        // 判断是否来自境外
        boolean isDomestic = baiduMapUtil.isDomestic(financeRecordDTO.getLatitude(), financeRecordDTO.getLongitude());
        financeRecord.setIsForeign(isDomestic ? 0 : 1); // 0-境内，1-境外
        financeRecord.setTime(LocalDateTime.now());


        // 保存到数据库
        userMapper.submitDonation(financeRecord);

        // 更新项目的已筹金额和爱心次数
        projectMapper.updateProjectAfterDonation(financeRecordDTO.getProjectId(),financeRecordDTO.getAmount());

        return Result.success("捐款提交成功！");
    }
}
