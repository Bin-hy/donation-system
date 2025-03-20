<template>
  <div class="project-container">
    <!-- 项目评价列表模块 -->
    <div class="review-list">
      <!-- 查询表单 -->
      <div class="query-form-container">
        <el-form :inline="true" :model="queryForm" class="query-form">
          <el-row :gutter="20">
            <!-- 左侧表单内容 -->
            <el-col :span="18">
              <el-row :gutter="10">
                <!-- 评价人 -->
                <el-col :span="8">
                  <el-form-item label="评价人">
                    <el-input
                        v-model="queryForm.reviewer"
                        placeholder="请输入评价人"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 时间范围 -->
                <el-col :span="8">
                  <el-form-item label="时间范围">
                    <el-date-picker
                        v-model="queryForm.dateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="YYYY-MM-DD"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-col>

            <!-- 右侧按钮 -->
            <el-col :span="6" class="button-group">
              <el-form-item>
                <el-button type="primary" @click="handleQuery">查询</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 操作栏 -->
      <div class="action-bar">
        <!-- 删除按钮 -->
        <el-button
            type="danger"
            :disabled="selectedReviews.length === 0"
            @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>

      <!-- 评价表格 -->
      <el-table
          :data="paginatedReviews"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
          :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
      >
        <!-- 多选列 -->
        <el-table-column type="selection" width="55" />

        <!-- 用户昵称列 -->
        <el-table-column label="用户昵称" width="180">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.reviewerAvatar" />
              <span class="username">{{ row.reviewer }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 项目名称列 -->
        <el-table-column prop="projectTitle" label="项目名称" width="200" />

        <!-- 评价内容列 -->
        <el-table-column prop="review" label="评价内容" />

        <!-- 封禁状态列 -->
        <el-table-column label="封禁状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.isBanned ? 'danger' : 'success'">
              {{ row.isBanned ? '已封禁' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 操作列（固定在右侧） -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
                :type="row.isBanned ? 'success' : 'warning'"
                size="default"
                @click="handleToggleBan(row)"
            >
              {{ row.isBanned ? '解封' : '封禁' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="reviewList.length"
            :page-size="pageSize"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 50]"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 查看弹窗 -->
    <el-dialog
        v-if="currentReview"
        v-model="viewDialogVisible"
        title="评价详情"
        width="50%"
        center
    >
      <div>
        <!-- 评价人信息 -->
        <div class="info-section">
          <h2>评价人信息</h2>
          <ul class="info-list">
            <li><strong>评价人：</strong>{{ currentReview.reviewer }}</li>
            <li><strong>封禁状态：</strong>
              <el-tag :type="currentReview.isBanned ? 'danger' : 'success'">
                {{ currentReview.isBanned ? '已封禁' : '正常' }}
              </el-tag>
            </li>
          </ul>
        </div>

        <!-- 评价内容 -->
        <div class="info-section">
          <h2>评价内容</h2>
          <p>{{ currentReview.review }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import { debounce } from 'lodash';

// 评价列表数据
const reviewList = ref([]);

// 查询表单数据
const queryForm = ref({
  reviewer: '', // 评价人
  dateRange: [], // 时间范围
});

// 分页相关状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(10); // 每页显示条数

// 选中的评价
const selectedReviews = ref([]);

// 查看弹窗相关状态
const viewDialogVisible = ref(false);
const currentReview = ref(null);

// 分页后的评价列表
const paginatedReviews = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return reviewList.value.slice(start, end);
});

// 获取评价数据
const fetchReviews = async () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const params = {
        reviewer: queryForm.value.reviewer,
        startDate: queryForm.value.dateRange?.[0],
        endDate: queryForm.value.dateRange?.[1],
      };

      const response = await axios.get('/admin/project-reviews', {
        params,
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.data.code === 200) {
        reviewList.value = response.data.data;
      } else {
        console.error('获取评价列表失败:', response.data.msg);
        ElMessage.error('获取评价列表失败');
      }
    } catch (error) {
      console.error('获取评价列表失败:', error);
      ElMessage.error('获取评价列表失败：' + error.message);
    }
  } else {
    ElMessage.error('用户未登录，请先登录');
  }
};

// // 查看评价详情
// const handleView = (review) => {
//   currentReview.value = review;
//   viewDialogVisible.value = true;
// };

// 处理选中评价
const handleSelectionChange = (selection) => {
  selectedReviews.value = selection;
};

// 批量删除评价
const handleBatchDelete = async () => {
  try {
    // 确认删除操作
    await ElMessageBox.confirm('确定要删除选中的评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 获取选中的评价 ID 列表
    const selectedIds = selectedReviews.value.map(review => review.id);

    // 发送批量删除请求
    const token = localStorage.getItem('token');
    await axios.post('/admin/project-reviews/batch-delete', selectedIds, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    // 重新获取评价列表
    await fetchReviews();
    ElMessage.success('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message);
    }
  }
};

// 切换封禁状态（带防抖）
const handleToggleBan = debounce(async (review) => {
  try {
    const action = review.isBanned ? '解封' : '封禁';
    await ElMessageBox.confirm(`确定要${action}该评价吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 发送封禁/解封请求
    const token = localStorage.getItem('token');
    await axios.post(`/admin/project-reviews/${review.id}/toggle-ban`, null, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    // 重新获取评价列表
    await fetchReviews();
    ElMessage.success(`${action}成功`);
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message);
    }
  }
}, 300); // 防抖时间设置为 300ms

// 处理查询
const handleQuery = () => {
  currentPage.value = 1;
  fetchReviews(); // 根据查询条件获取数据
};

// 处理重置
const handleReset = () => {
  queryForm.value = {
    reviewer: '',
    dateRange: [],
  };
  currentPage.value = 1;
  fetchReviews(); // 重置后获取全部数据
};

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page;
};

// 组件挂载时初始化数据
onMounted(() => {
  fetchReviews(); // 页面加载时获取全部数据
});
</script>

<style scoped>
/* 外层容器样式 */
.project-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

/* 评价列表模块样式 */
.review-list {
  width: 100%;
}

/* 查询表单容器样式 */
.query-form-container {
  background-color: var(--el-bg-color-page);
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

/* 按钮组样式 */
.button-group {
  display: flex;
  justify-content: flex-end;
}

/* 操作栏样式 */
.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

/* 设置表格容器的宽度和溢出滚动 */
.el-table {
  width: 100%;
  overflow-x: auto;
}

/* 分页样式 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 用户信息样式 */
.user-info {
  display: flex;
  align-items: center;
}

.user-info .username {
  margin-left: 10px;
  font-size: 14px;
  font-weight: bold;
  color: var(--el-text-color-primary);
}

/* 信息部分样式 */
.info-section {
  margin-top: 20px;
}

.info-section h2 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

.info-list {
  list-style: none;
  padding-left: 20px;
  margin: 0;
}

.info-list li {
  margin-bottom: 8px;
  color: var(--el-text-color-regular);
}

.info-list strong {
  font-weight: bold;
  margin-right: 8px;
}
</style>
