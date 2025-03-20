package com.example.controller;


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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;


    @Autowired
    private CarouselService carouselService;

    @Autowired
    private PublicInfoService publicInfoService;

    @Autowired
    private DashboardService dashboardService;

    @PostMapping("/login")
    public Result<Map<String, String>> loginUser(@RequestBody Admin admin) {
        try {
            // 验证用户名和密码
            Admin adminLogin = adminService.login(admin.getUsername(), admin.getPassword());

            // 生成 JWT 令牌
            HashMap<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.USER_ID, adminLogin.getId());
            String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

            // 返回令牌和 uuid
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 获取用户信息
    @GetMapping("/info")
    public Result<AdminInfoVO> getUserInfo() {
        return adminService.getUserInfo();
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


    @PostMapping("/projects")
    public Result<String> addProject(@RequestBody Project project) {
        try {
            // 调用 Service 层添加项目
            projectService.addProject(project);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    @PutMapping("/projects/{id}/recommend")
    public Result<String> updateProjectRecommendStatus(
            @PathVariable String id,
            @RequestParam Integer isRecommended
    ) {
        try {
            projectService.updateProjectRecommendStatus(id, isRecommended);
            return Result.success("更新成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage()); // 返回具体的错误信息
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }


    @PutMapping("/projects/{id}")
    public Result<String> updateProject(@PathVariable String id, @RequestBody Project project) {
        try {
            // 设置项目 ID
            project.setId(id);

            // 调用 Service 层更新项目
            projectService.updateProject(project);
            return Result.success("编辑成功");
        } catch (Exception e) {
            return Result.error("编辑失败：" + e.getMessage());
        }
    }


    @PostMapping("/projects/batch-delete")
    public Result<String> batchDeleteProjects(@RequestBody List<String> ids) {
        try {
            // 调用 Service 层批量删除项目

            projectService.batchDeleteProjects(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }


    @GetMapping("/project-reviews")
    public Result<List<ProjectReviewVO>> getProjectReviews(
            @RequestParam(required = false) String reviewer,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        return projectService.getProjectReviews(reviewer, startDate, endDate);
    }

    @PostMapping("/project-reviews/batch-delete")
    public Result<String> batchDeleteProjectReviews(@RequestBody List<String> ids) {
        try {
            // 调用 Service 层批量删除项目

            projectService.batchDeleteProjectReviews(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }


    @PostMapping("/project-reviews/{id}/toggle-ban")
    public Result<Void> toggleBan(@PathVariable String id) {
        projectService.toggleBan(id);
        return Result.success();
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


    @GetMapping("/users")
    public Result<List<User>> getUsers(
            @RequestParam(required = false) String username, // 账号（用户名）
            @RequestParam(required = false) String nickname, // 昵称
            @RequestParam(required = false) String email,    // 邮箱
            @RequestParam(required = false) String phone    // 电话
    ) {
        try {
            // 调用 Service 层查询用户列表
            List<User> userList = userService.getUsers(username, nickname, email, phone);
            return Result.success(userList);
        } catch (Exception e) {
            return Result.error("查询用户列表失败：" + e.getMessage());
        }
    }


    @PutMapping("/users/{id}")
    public Result<String> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            // 设置用户 ID
            user.setId(id);

            // 调用 Service 层更新用户信息
            userService.updateUser(user);
            return Result.success("编辑成功");
        } catch (Exception e) {
            return Result.error("编辑失败：" + e.getMessage());
        }
    }


    @PostMapping("/users/batch-delete")
    public Result<String> batchDeleteUsers(@RequestBody List<String> ids) {
        try {
            // 调用 Service 层批量删除用户
            userService.batchDeleteUsers(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    @PostMapping("/users/ban/{id}")
    public Result<String> toggleBanUser(@PathVariable String id) {
        try {
            // 调用 Service 层封禁或解封用户
            userService.toggleBanUser(id);
            return Result.success("操作成功");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
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

    @PostMapping("/news")
    public Result<String> addNews(@RequestBody News news) {
        try {
            newsService.addNews(news);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }


    @PutMapping("/news/{id}")
    public Result<String> updateNews(@PathVariable String id, @RequestBody News news) {
        try {
            news.setId(id);
            newsService.updateNews(news);
            return Result.success("编辑成功");
        } catch (Exception e) {
            return Result.error("编辑失败：" + e.getMessage());
        }
    }

    @PostMapping("/news/batch-delete")
    public Result<String> batchDeleteNews(@RequestBody List<String> ids) {
        try {
            newsService.batchDeleteNews(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }


    // 切换用户禁止发送弹幕状态
    @PostMapping("/users/{userId}/toggle-barrage-ban")
    public Result<String> toggleBarrageBan(@PathVariable String userId, @RequestBody Map<String, Boolean> request) {
        try {
            boolean isBarrageBanned = request.get("isBarrageBanned");
            userService.toggleBarrageBan(userId, isBarrageBanned);
            String action = isBarrageBanned ? "禁止发送弹幕" : "恢复发送弹幕";
            return Result.success(action + "成功");
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
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

    // 删除弹幕
    @DeleteMapping("/barrages/{id}")
    public Result<String> deleteBarrage(@PathVariable String id) {
        try {
            userService.deleteBarrage(id);
            return Result.success("删除弹幕成功");
        } catch (Exception e) {
            return Result.error("删除弹幕失败：" + e.getMessage());
        }
    }

    @PutMapping("/news/{id}/recommend")
    public Result<?> updateRecommendStatus(
            @PathVariable String id,
            @RequestBody Map<String, Integer> request
    ) {
        Integer isRecommended = request.get("isRecommended");
        if (isRecommended == null || (isRecommended != 0 && isRecommended != 1)) {
            return Result.error("参数 isRecommended 必须为 0 或 1");
        }

        int result = newsService.updateRecommendStatus(id, isRecommended);
        if (result > 0) {
            return Result.success("推荐状态更新成功");
        } else {
            return Result.error("推荐状态更新失败");
        }
    }

    /**
     * 获取轮播图列表
     */
    @GetMapping("/carousel/list")
    public Result<List<CarouselVO>> getCarouselList() {
        try {
            List<CarouselVO> carouselList = carouselService.getCarouselList();
            return Result.success(carouselList);
        } catch (Exception e) {
            return Result.error("获取轮播图列表失败：" + e.getMessage());
        }
    }

    /**
     * 添加轮播图
     */
    @PostMapping("/carousel/add")
    public Result<String> addCarousel(@RequestBody Carousel carousel) {
        try {
            System.out.println("最后的数据是"+carousel);
            carouselService.addCarousel(carousel);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * 编辑轮播图
     */
    @PutMapping("/carousel/update/{id}")
    public Result<String> updateCarousel(@PathVariable String id, @RequestBody Carousel carousel) {
        try {
            carousel.setId(id); // 设置轮播图 ID
            carouselService.updateCarousel(carousel);
            return Result.success("编辑成功");
        } catch (Exception e) {
            return Result.error("编辑失败：" + e.getMessage());
        }
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/carousel/delete/{id}")
    public Result<String> deleteCarousel(@PathVariable String id) {
        try {
            carouselService.deleteCarousel(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除轮播图
     */
    @PostMapping("/carousel/batch-delete")
    public Result<String> batchDeleteCarousels(@RequestBody List<String> ids) {
        try {
            carouselService.batchDeleteCarousels(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取公示信息列表
     */
    @GetMapping("/public-info/list")
    public Result<List<PublicInfo>> getPublicInfoList() {
        return publicInfoService.getPublicInfoList();
    }

    /**
     * 更新公示信息
     */
    @PutMapping("/public-info/update/{id}")
    public Result<String> updatePublicInfo(@PathVariable String id, @RequestBody PublicInfo publicInfo) {
        return publicInfoService.updatePublicInfo(id, publicInfo);
    }


    @GetMapping("/feedback")
    public Result<List<FeedbackVO>> getMessages(
            @RequestParam(required = false) String name, // 反馈人姓名
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate, // 开始日期
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate // 结束日期
    ) {
        try {
            List<FeedbackVO> messages = userService.getFeedbackVO(name, startDate, endDate);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error("获取消息列表失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/feedback/batch-delete")
    public Result<String> deleteBatch(@RequestBody List<String> ids) {
        try {
            int deletedCount = userService.deleteBatch(ids);
            if (deletedCount > 0) {
                return Result.success("成功删除 " + deletedCount + " 条消息");
            } else {
                return Result.error("未找到匹配的消息");
            }
        } catch (Exception e) {
            return Result.error("批量删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/dashboard/stats")
    public Result<DashboardStatsVO> getDashboardStats() {
        try {
            DashboardStatsVO stats = dashboardService.getDashboardStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard/trend")
    public Result<DashboardTrendVO> getDashboardTrend() {
        try {
            DashboardTrendVO trendData = dashboardService.getDashboardTrend();
            return Result.success(trendData);
        } catch (Exception e) {
            return Result.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard/donation-trend")
    public Result<DashboardDonationTrendVO> getDashboardDonationTrend() {
        try {
            DashboardDonationTrendVO donationTrendData = dashboardService.getDashboardDonationTrend();
            return Result.success(donationTrendData);
        } catch (Exception e) {
            return Result.error("获取捐赠趋势数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard/donation-ranking")
    public Result<List<DonationRankingVO>> getDonationRanking() {
        try {
            List<DonationRankingVO> donationRanking = dashboardService.getDonationRanking();
            return Result.success(donationRanking);
        } catch (Exception e) {
            return Result.error("获取捐款排行榜失败: " + e.getMessage());
        }
    }
}
