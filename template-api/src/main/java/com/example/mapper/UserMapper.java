package com.example.mapper;

import com.example.model.Danmu;
import com.example.model.Feedback;
import com.example.model.FinanceRecord;
import com.example.model.User;
import com.example.vo.BarrageVO;
import com.example.vo.FeedbackVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户
    User findByUsername(String username);

    // 检查用户名是否已存在
    int existsByUsername(String username);

    // 保存用户信息
    void save(User user);

    // 根据用户 ID（UUID）查找用户
    User findById(@Param("id") String id);

    // 查询用户列表（支持分页）
    List<User> findUsers(
            @Param("username") String username,
            @Param("nickname") String nickname,
            @Param("email") String email,
            @Param("phone") String phone
    );

    // 更新用户信息
    void update(User existingUser);

    // 批量删除用户
    void batchDelete(@Param("ids") List<String> ids);

    void updateUser(User user);

    // 获取弹幕列表
    List<BarrageVO> selectBarrages(
            @Param("username") String username,
            @Param("nickname") String nickname,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // 删除弹幕
    void deleteBarrageById(@Param("id") String id);


    // 根据用户 ID 查询用户信息
    User selectById(@Param("userId") String userId);

    // 更新用户信息
    void updateById(User user);

    void addBarrage(Danmu danmu);

    void insertFeedback(Feedback feedback);

    List<FeedbackVO> getFeedback(@Param("name") String name,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate
    );

    int deleteBatch(@Param("ids") List<String> ids);

    void submitDonation(FinanceRecord financeRecord);

    boolean existsByNickname(String nickname);

    @Select("SELECT * FROM users WHERE nickname = #{nickname}")
    User findByNickname(@Param("nickname") String nickname);
}
