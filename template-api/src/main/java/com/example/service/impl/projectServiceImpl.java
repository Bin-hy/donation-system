package com.example.service.impl;

import com.example.mapper.ProjectMapper;
import com.example.model.OrgInfo;
import com.example.model.Project;
import com.example.model.ProjectReview;
import com.example.service.ProjectService;
import com.example.util.Result;
import com.example.vo.FinanceRecordVO;
import com.example.vo.ProjectReviewDetailVO;
import com.example.vo.ProjectReviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class projectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;


    // 查询项目列表
    @Override
    public Result<List<Project>> getProjects(String title, String recordNumber,Integer category, Date startDate, Date endDate) {
        // 调用 Mapper 查询数据库
        List<Project> projects = projectMapper.getProjects(title, recordNumber,category, startDate, endDate);
        return Result.success(projects);
    }

    @Override
    public void addProject(Project project) {
        try {
            // 生成项目 ID（projects 表的主键）
            project.generateId();
            project.setIsRecommended(0);
            // 插入项目基本信息
            projectMapper.insertProject(project);

            // 插入关联的项目信息
            if (project.getProjectInfo() != null) {
                // 生成 project_info 表的独立 id（UUID）
                String projectInfoId = UUID.randomUUID().toString();
                project.getProjectInfo().setId(projectInfoId);

                // 设置 project_info 表的 projectId（关联 projects 表）
                project.getProjectInfo().setProjectId(project.getId());

                // 插入 project_info
                projectMapper.insertProjectInfo(project.getProjectInfo());
            }

            // 插入关联的机构信息
            if (project.getOrgInfo() != null) {
                // 生成 org_info 表的独立 id（UUID）
                String orgInfoId = UUID.randomUUID().toString();
                project.getOrgInfo().setId(orgInfoId);

                // 设置 org_info 表的 projectId（关联 projects 表）
                project.getOrgInfo().setProjectId(project.getId());

                // 插入 org_info
                projectMapper.insertOrgInfo(project.getOrgInfo());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加项目失败：" + e.getMessage());
        }
    }


    @Override
    public void updateProject(Project project) {
        // 更新项目基本信息
        projectMapper.updateProject(project);

        // 更新机构信息
        if (project.getOrgInfo() != null) {
            projectMapper.updateOrgInfo(project.getOrgInfo());
        }

        // 更新项目信息
        if (project.getProjectInfo() != null) {
            projectMapper.updateProjectInfo(project.getProjectInfo());
        }
    }

    @Override
    public void batchDeleteProjects(List<String> ids) {
        try {

            // 批量删除项目基本信息
            int projectsDeleted = projectMapper.batchDeleteProjects(ids);
            System.out.println("删除项目基本信息成功，删除数量：" + projectsDeleted);

            // 批量删除关联的机构信息
            int orgInfoDeleted = projectMapper.batchDeleteOrgInfoByProjectIds(ids);
            System.out.println("删除机构信息成功，删除数量：" + orgInfoDeleted);

            // 批量删除关联的项目信息
            int projectInfoDeleted = projectMapper.batchDeleteProjectInfoByProjectIds(ids);
            System.out.println("删除项目信息成功，删除数量：" + projectInfoDeleted);

            // 打印日志：删除完成
            System.out.println("批量删除项目完成");
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            throw new RuntimeException("批量删除失败：" + e.getMessage());
        }
    }

    // 查询数据
    @Override
    public Result<List<ProjectReviewVO>> getProjectReviews(String reviewer, Date startDate, Date endDate) {

        List<ProjectReviewVO> reviews = projectMapper.selectList(reviewer, startDate, endDate);
        return Result.success(reviews);
    }

    @Override
    public void batchDeleteProjectReviews(List<String> ids) {
        try {
            // 批量删除评价
            int reviewsDeleted = projectMapper.batchDeleteProjectReviews(ids);
            System.out.println("删除评价成功，删除数量：" + reviewsDeleted);

            // 打印日志：删除完成
            System.out.println("批量删除评价完成");
        } catch (Exception e) {
            // 打印异常堆栈信息
            e.printStackTrace();
            throw new RuntimeException("批量删除评价失败：" + e.getMessage());
        }
    }


    @Override
    public void toggleBan(String id) {
        // 查询当前评价的封禁状态
        ProjectReview review = projectMapper.selectById(id);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }

        // 切换封禁状态
        Integer isBanned = review.getIsBanned() == 0 ? 1 : 0; // 0 -> 1, 1 -> 0
        review.setIsBanned(isBanned);
        review.setUpdatedAt(LocalDateTime.now()); // 使用 LocalDateTime

        // 更新数据库
        projectMapper.updateById(review);
    }

    @Override
    public Result<List<FinanceRecordVO>> getFinanceRecords(
            String projectName, String address, Date startDate, Date endDate,
            Integer paymentMethod, String username,
            Double minAmount, Double maxAmount, String category, String donorType
    ) {
        try {
            // 直接调用 Mapper 方法，传递所有参数
            List<FinanceRecordVO> records = projectMapper.selectFinanceRecords(
                    projectName, address, startDate, endDate, paymentMethod, username,
                    minAmount, maxAmount, category, donorType
            );

            // 返回成功结果
            return Result.success(records);
        } catch (Exception e) {
            // 记录错误日志
            log.error("查询捐赠记录失败", e);
            // 返回错误结果
            return Result.error("查询捐赠记录失败：" + e.getMessage());
        }
    }
    @Override
    @Transactional
    public void updateProjectRecommendStatus(String id, Integer isRecommended) {
        try {
            int rows = projectMapper.updateProjectRecommendStatus(id, isRecommended);
            if (rows == 0) {
                throw new RuntimeException("更新失败，未找到匹配的数据");
            }
            log.info("更新成功，影响行数: {}", rows);
        } catch (Exception e) {
            // 打印异常堆栈信息
            log.error("更新失败: ", e);
            throw e; // 重新抛出异常，确保事务回滚
        }
    }

    @Override
    public List<Project> getRecommendedProjects() {
        return projectMapper.selectRecommendedProjects();
    }

    @Override
    public Project getProjectById(String id) {
        return projectMapper.getProjectById(id);
    }


    @Override
    public List<ProjectReviewDetailVO> getReviewDetailById(String id) {
        List<ProjectReviewDetailVO> projectReviewDetailVO = projectMapper.selectReviewDetailById(id);
        // 直接查询并返回 VO
        System.out.println("数据是"+projectReviewDetailVO);
        return projectReviewDetailVO;
    }

    @Override
    public void submitReview(ProjectReview projectReview) {
        // 插入数据库
        projectMapper.submitReview(projectReview);
    }



}
