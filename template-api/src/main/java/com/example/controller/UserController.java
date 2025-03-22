package com.example.controller;

import com.example.context.BaseContext;
import com.example.dto.*;
import com.example.model.*;
import com.example.service.*;
import com.example.util.JwtClaimsConstant;
import com.example.util.JwtProperties;
import com.example.util.JwtUtil;
import com.example.util.Result;
import com.example.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;


    @Autowired
    private CarouselService carouselService;

    @Autowired
    private ProjectService projectService;


    @Autowired
    private NewsService newsService;

    @Autowired
    private PublicInfoService publicInfoService;




    @PostMapping("/login")
    public Result<Map<String, String>> loginUser(@RequestBody User user) {
        try {
            // 验证用户名和密码
            User loginUser = userService.login(user.getUsername(), user.getPassword());

            // 生成 JWT 令牌
            HashMap<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.USER_ID, loginUser.getId());
            String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

            // 返回令牌和 uuid
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            result.put("isBanned", String.valueOf(loginUser.getIsBanned()));
            result.put("uuid", loginUser.getId());
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }
    @GetMapping("/verifyCode")
    public Result<VerifyCodeVO> getCaptcha() {
        return userService.generateVerificationCode();
    }

    public Result getEmailCode(String email) {

        return Result.success();
    }

    // 获取用户信息
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(@RequestHeader("Authorization") String token) {
        return userService.getUserInfo(token);
    }


    /**
     * 获取轮播图列表
     *
     * @return 轮播图列表
     */
    @GetMapping("/carousel/list")
    public Result<List<CarouselVO>> getCarouselList() {
        List<CarouselVO> carouselList = carouselService.getCarouselList();
        return Result.success(carouselList);
    }

    /**
     * 获取推荐的新闻列表
     */
    @GetMapping("/news/recommend")
    public Result<List<News>> getRecommendedNews() {
        List<News> newsList = newsService.getRecommendedNews();
        return Result.success(newsList);
    }

    /**
     * 获取推荐的慈善项目列表
     */
    @GetMapping("/projects/recommend")
    public Result<List<Project>> getRecommendedProjects() {
        List<Project> projects = projectService.getRecommendedProjects();
        return Result.success(projects);
    }

    @GetMapping("/info-disclosure/list")
    public Result<List<PublicInfo>> getPublicInfoList() {
        return publicInfoService.getPublicInfoList();
    }


    @GetMapping("/news/detail")
    public Result<News> getNewsDetail(@RequestParam String id) {
        try {
            News news = newsService.getNewsDetailById(id);
            if (news != null) {
                return Result.success(news);
            } else {
                return Result.error("新闻不存在");
            }
        } catch (Exception e) {
            return Result.error("获取新闻详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/projects")
    public Result<List<Project>> getProjects(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String recordNumber,
            @RequestParam(required = false) Integer category, // 增加 category 参数
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        return projectService.getProjects(title, recordNumber, category, startDate, endDate);
    }


    @GetMapping("/news")
    public Result<List<News>> getNews(
            @RequestParam(required = false) String title, // 新闻标题
            @RequestParam(required = false) String publisher, // 发布者
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, // 开始日期
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate // 结束日期
    ) {
        try {
            List<News> newsList = newsService.getNews(title, publisher, startDate, endDate);
            return Result.success(newsList);
        } catch (Exception e) {
            return Result.error("获取新闻列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/projects/{id}")
    public Result<Project> getProjectDetail(@PathVariable String id) {
        try {
            // 查询项目详情
            Project project = projectService.getProjectById(id);
            if (project == null) {
                return Result.error("项目未找到");
            }

            return Result.success(project);
        } catch (Exception e) {
            return Result.error("获取项目详情失败: " + e.getMessage());
        }
    }


    @GetMapping("/finance-records")
    public Result<List<FinanceRecordVO>> getFinanceRecords(
            @RequestParam(required = false) String projectName, // 项目名称
            @RequestParam(required = false) String address, // 地址
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, // 开始日期
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, // 结束日期
            @RequestParam(required = false) Integer paymentMethod, // 支付方式
            @RequestParam(required = false) String username, // 用户名
            @RequestParam(required = false) Double minAmount, // 新增：最小金额
            @RequestParam(required = false) Double maxAmount, // 新增：最大金额
            @RequestParam(required = false) String category, // 新增：交易类型
            @RequestParam(required = false) String donorType // 新增：捐赠人类型
    ) {
        return projectService.getFinanceRecords(
                projectName, address, startDate, endDate, paymentMethod, username,
                minAmount, maxAmount, category, donorType
        );
    }


    @GetMapping("/messages/{id}")
    public Result<List<ProjectReviewDetailVO>> getReviewDetailsByProjectId(@PathVariable String id) {
        List<ProjectReviewDetailVO> reviews = projectService.getReviewDetailById(id);
        if (reviews == null || reviews.isEmpty()) {
            return Result.error("评价不存在或已被封禁");
        }
        return Result.success(reviews);
    }


    @PostMapping("/project/reviews")
    public Result<String> submitReview(@RequestBody ProjectReview projectReview) {
        try {
            // 1. 从上下文工具类中获取当前用户的 userId
            String userId = BaseContext.getCurrentId();

            // 2. 设置实体类的字段
            projectReview.setId(UUID.randomUUID().toString()); // 生成唯一 ID
            projectReview.setUserId(userId); // 设置用户 ID
            projectReview.setCreatedAt(LocalDateTime.now()); // 设置创建时间
            projectReview.setUpdatedAt(LocalDateTime.now()); // 设置更新时间
            projectReview.setIsBanned(0); // 默认未封禁

            // 3. 调用 Service 层方法，插入留言数据
            projectService.submitReview(projectReview);

            // 4. 返回成功响应
            return Result.success("留言提交成功！");
        } catch (Exception e) {
            // 5. 返回失败响应
            return Result.error("留言提交失败：" + e.getMessage());
        }
    }


    @GetMapping("/profile")
    public Result<UserProfileVO> getUserProfile() {
        try {
            // 获取当前用户 ID
            String userId = BaseContext.getCurrentId();

            // 调用 Service 层获取用户信息
            UserProfileVO userProfile = userService.getUserProfile(userId);
            return Result.success(userProfile);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public Result<String> saveUserProfile(@RequestBody UserProfileDTO userProfileDTO) {
        try {
            // 获取当前用户 ID
            String userId = BaseContext.getCurrentId();

            // 调用 Service 层保存用户信息
            userService.saveUserProfile(userId, userProfileDTO);
            return Result.success("用户信息保存成功");
        } catch (Exception e) {
            return Result.error("保存用户信息失败: " + e.getMessage());
        }
    }


    // 获取弹幕列表
    @GetMapping("/barrages")
    public Result<List<BarrageVO>> getBarrages(
            @RequestParam(required = false) String username, // 用户账号
            @RequestParam(required = false) String nickname, // 用户昵称
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, // 开始日期
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate // 结束日期
    ) {
        try {
            List<BarrageVO> barrages = userService.getBarrages(username, nickname, startDate, endDate);
            return Result.success(barrages);
        } catch (Exception e) {
            return Result.error("获取弹幕列表失败：" + e.getMessage());
        }
    }


    // 添加弹幕
    @PostMapping("/barrages")
    public Result<String> addBarrages(@RequestBody Map<String, String> requestBody) {
        try {
            // 获取当前用户 ID
            String userId = BaseContext.getCurrentId();

            // 获取弹幕内容
            String content = requestBody.get("content");
            if (content == null || content.trim().isEmpty()) {
                return Result.error("弹幕内容不能为空");
            }

            // 调用服务层添加弹幕
            userService.addBarrage(userId, content);
            return Result.success("弹幕添加成功");
        } catch (Exception e) {
            return Result.error("弹幕添加失败：" + e.getMessage());
        }
    }

    // 删除弹幕
    @DeleteMapping("/barrages/{barrageId}")
    public Result<String> deleteBarrage(@PathVariable String barrageId) {
        try {
            // 调用服务层删除弹幕
            userService.deleteBarrage(barrageId);
            return Result.success("弹幕删除成功");
        } catch (Exception e) {
            return Result.error("弹幕删除失败：" + e.getMessage());
        }
    }


    @PostMapping("/feedback")
    public Result<String> submitFeedback(@RequestBody FeedbackDTO feedbackDTO) {
       userService.submitFeedback(feedbackDTO);
       return Result.success("举报提交成功");
    }


    @PostMapping("/donations")
    public Result<String> submitDonation(@RequestBody FinanceRecordDTO financeRecordDTO) {
        return userService.submitDonation(financeRecordDTO);
    }



}
