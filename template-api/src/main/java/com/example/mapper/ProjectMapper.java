package com.example.mapper;

import com.example.model.OrgInfo;
import com.example.model.Project;
import com.example.model.ProjectInfo;
import com.example.model.ProjectReview;
import com.example.vo.FinanceRecordVO;
import com.example.vo.ProjectReviewDetailVO;
import com.example.vo.ProjectReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ProjectMapper {

    List<Project> getProjects(
            @Param("title") String title,
            @Param("recordNumber") String recordNumber,
            @Param("category") Integer category, // 增加 category 参数
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    // 更新项目基本信息
    void updateProject(Project project);

    // 更新机构信息
    void updateOrgInfo(OrgInfo orgInfo);

    // 更新项目信息
    void updateProjectInfo(ProjectInfo projectInfo);

    // 插入项目基本信息
    void insertProject(Project project);

    // 插入机构信息
    void insertOrgInfo(OrgInfo orgInfo);

    // 插入项目信息
    void insertProjectInfo(ProjectInfo projectInfo);

    // 批量删除项目基本信息，返回删除的记录数
    int batchDeleteProjects(List<String> ids);

    // 批量删除机构信息，返回删除的记录数
    int batchDeleteOrgInfoByProjectIds(List<String> projectIds);

    // 批量删除项目信息，返回删除的记录数
    int batchDeleteProjectInfoByProjectIds(List<String> projectIds);

    List<ProjectReviewVO> selectList(
            @Param("reviewer") String reviewer,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    int batchDeleteProjectReviews(@Param("ids") List<String> ids);

    ProjectReview selectById(@Param("id") String id);

    void updateById(ProjectReview review);


    List<FinanceRecordVO> selectFinanceRecords(
                @Param("projectName") String projectName,
                @Param("address") String address,
                @Param("startDate") Date startDate,
                @Param("endDate") Date endDate,
                @Param("paymentMethod") Integer paymentMethod,
                @Param("username") String username,
                @Param("minAmount") Double minAmount,
                @Param("maxAmount") Double maxAmount,
                @Param("category") String category,
                @Param("donorType") String donorType
    );


    int updateProjectRecommendStatus(@Param("id") String id, @Param("isRecommended") Integer isRecommended);

    List<Project> selectRecommendedProjects();

    Project getProjectById(String id);

    List<ProjectReviewDetailVO> selectReviewDetailById(@Param("id") String id);

    void submitReview(ProjectReview projectReview);

    void updateProjectAfterDonation(
            @Param("projectId") String projectId,
            @Param("amount") Double amount
    );
}
