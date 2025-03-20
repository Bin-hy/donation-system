package com.example.service;

import com.example.model.Project;
import com.example.model.ProjectReview;
import com.example.util.Result;
import com.example.vo.FinanceRecordVO;
import com.example.vo.ProjectReviewDetailVO;
import com.example.vo.ProjectReviewVO;

import java.util.Date;
import java.util.List;

public interface ProjectService {
    Result<List<Project>> getProjects(String title, String recordNumber,Integer category, Date startDate, Date endDate);

    void addProject(Project project);

    void updateProject(Project project);

    void batchDeleteProjects(List<String> ids);

    Result<List<ProjectReviewVO>> getProjectReviews(String reviewer, Date startDate, Date endDate);

    void batchDeleteProjectReviews(List<String> ids);

    void toggleBan(String id);


    void updateProjectRecommendStatus(String id, Integer isRecommended);

    List<Project> getRecommendedProjects();

    Project getProjectById(String id);

    List<ProjectReviewDetailVO> getReviewDetailById(String id);

    void submitReview(ProjectReview projectReview);

    Result<List<FinanceRecordVO>> getFinanceRecords(String projectName, String address, Date startDate, Date endDate, Integer paymentMethod, String username, Double minAmount, Double maxAmount, String category, String donorType);
}
