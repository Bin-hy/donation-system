package com.example.service;

import com.example.dto.*;
import com.example.model.Feedback;
import com.example.model.User;
import com.example.util.Result;
import com.example.vo.BarrageVO;
import com.example.vo.FeedbackVO;
import com.example.vo.UserInfoVO;
import com.example.vo.UserProfileVO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface UserService {


    Result<String> register(RegisterRequest registerRequest);

    Result<UserInfoVO> getUserInfo(String token);

    User login(String username, String password) throws Exception;

    void updateUser(User user);

    void batchDeleteUsers(List<String> ids);

    List<User> getUsers(String username, String nickname, String email, String phone);

    void toggleBanUser(String id);

    // 切换用户禁止发送弹幕状态
    void toggleBarrageBan(String userId, boolean isBarrageBanned);

    // 获取弹幕列表
    List<BarrageVO> getBarrages(String username, String nickname, Date startDate, Date endDate);

    // 删除弹幕
    void deleteBarrage(String id);

    UserProfileVO getUserProfile(String userId);

    void saveUserProfile(String userId, UserProfileDTO userProfileDTO);

    void addBarrage(String userId, String content);

    void submitFeedback(FeedbackDTO feedbackDTO);


    int deleteBatch(List<String> ids);

    List<FeedbackVO> getFeedbackVO(String name, LocalDateTime startDate, LocalDateTime endDate);

    Result<String> submitDonation(FinanceRecordDTO financeRecordDTO);
}
