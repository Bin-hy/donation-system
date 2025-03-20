<template>
  <div class="user-messages-container">
    <!-- 用户消息列表模块 -->
    <div class="message-list">
      <!-- 查询表单 -->
      <div class="query-form-container">
        <el-form :inline="true" class="query-form">
          <el-row :gutter="20">
            <!-- 左侧表单内容 -->
            <el-col :span="18">
              <el-row :gutter="10">
                <!-- 反馈人 -->
                <el-col :span="8">
                  <el-form-item label="反馈人姓名">
                    <el-input
                        v-model="queryForm.name"
                        placeholder="请输入反馈人姓名"
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
            :disabled="selectedMessages.length === 0"
            @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>

      <!-- 消息表格 -->
      <div class="table-container">
        <el-table
            :data="paginatedMessages"
            style="width: 100%"
            border
            @selection-change="handleSelectionChange"
            :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
        >
          <!-- 多选列 -->
          <el-table-column type="selection" width="55" />

          <!-- 反馈人头像 -->
          <el-table-column label="反馈人头像" width="120">
            <template #default="{ row }">
              <el-avatar :src="row.avatar" :size="40" />
            </template>
          </el-table-column>

          <!-- 反馈人昵称 -->
          <el-table-column prop="nickname" label="反馈人昵称" width="150" />

          <!-- 反馈人真实姓名 -->
          <el-table-column prop="realName" label="反馈人真实姓名" width="150" />

          <!-- 反馈人电话 -->
          <el-table-column prop="phone" label="反馈人电话" width="150" />

          <!-- 发送时间 -->
          <el-table-column prop="createTime" label="发送时间" width="180" />

          <!-- 项目封面 -->
          <el-table-column label="项目封面" width="250">
            <template #default="{ row }">
              <img
                  :src="row.projectImage"
                  alt="项目封面"
                  style="width: 200px; height: 120px; object-fit: cover; border-radius: 4px;"
              />
            </template>
          </el-table-column>

          <!-- 项目名称 -->
          <el-table-column prop="projectTitle" label="项目名称" width="200" />

          <!-- 反馈内容 -->
          <el-table-column label="反馈内容" fixed="right" width="150">
            <template #default="{ row }">
              <el-button type="primary" size="default" @click="handleViewMessage(row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="messageList.length"
            :page-size="pageSize"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 50]"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 消息详情弹窗 -->
    <el-dialog
        v-model="messageDialogVisible"
        title="消息详情"
        width="30%"
        center
        style="text-align: justify;"
    >
      <div>
        <!-- 反馈内容 -->
        <div class="info-section">
          <p style="padding: 10px;border-radius: 4px;">
            {{ currentMessage.message }}
          </p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

// 消息列表数据
const messageList = ref([]);

// 查询表单数据
const queryForm = ref({
  name: '', // 反馈人
  dateRange: [], // 时间范围
});

// 分页相关状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(5); // 每页显示条数

// 选中的消息
const selectedMessages = ref([]);

// 消息详情弹窗相关状态
const messageDialogVisible = ref(false);
const currentMessage = ref(null);

// 分页后的消息列表
const paginatedMessages = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return messageList.value.slice(start, end);
});

const fetchMessageList = async () => {
  try {
    const params = {
      name: queryForm.value.name,
      startDate: queryForm.value.dateRange?.[0] ? `${queryForm.value.dateRange[0]} 00:00:00` : null,
      endDate: queryForm.value.dateRange?.[1] ? `${queryForm.value.dateRange[1]} 23:59:59` : null,
    };

    const token = localStorage.getItem('token');
    const response = await axios.get('/admin/feedback', {
      params,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    console.log('接口返回数据:', response.data); // 打印返回值
    if (response.data.code === 200) { // 判断状态码
      // 对返回的数据进行日期格式化
      messageList.value = response.data.data.map(item => {
        return {
          ...item, // 保留其他字段
          createTime: formatDateTimeFromArray(item.createTime), // 格式化 createTime 字段
          projectTitle: item.projectTitle || '无项目名称', // 默认值
          projectImage: item.projectImage || '默认封面URL', // 默认值
        };
      });
    } else {
      ElMessage.error(response.msg || '获取消息列表失败'); // 显示错误信息
    }
  } catch (error) {
    console.error('获取消息列表失败:', error);
    ElMessage.error('获取消息列表失败');
  }
};

function formatDateTimeFromArray(dateArray) {
  if (!dateArray || dateArray.length < 6) return ''; // 如果数据不合法，返回空字符串

  const [year, month, day, hour, minute, second] = dateArray;
  const date = new Date(year, month - 1, day, hour, minute, second);

  const pad = (num) => num.toString().padStart(2, '0'); // 补零函数
  const formattedDate = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
  const formattedTime = `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;

  return `${formattedDate} ${formattedTime}`;
}

// 处理选中消息
const handleSelectionChange = (selection) => {
  selectedMessages.value = selection;
};

// 批量删除
const handleBatchDelete = async () => {
  try {
    // 确认删除操作
    await ElMessageBox.confirm('确定要删除选中的消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 获取选中的消息 ID 列表
    const selectedIds = selectedMessages.value.map(message => message.id);

    // 发送批量删除请求
    const token = localStorage.getItem('token');
    const response = await axios.post('/admin/feedback/batch-delete', selectedIds, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    console.log('批量删除接口返回数据:', response.data); // 打印返回值
    if (response.code === 200) { // 判断状态码
      ElMessage.success(response.msg || '删除成功'); // 显示成功信息
      await fetchMessageList(); // 重新获取消息列表
    } else {
      ElMessage.error(response.msg || '删除失败'); // 显示错误信息
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败: ' + error.msg);
    }
  }
};

// 查看消息详情
const handleViewMessage = (message) => {
  currentMessage.value = message;
  messageDialogVisible.value = true;
};

// 处理查询
const handleQuery = () => {
  currentPage.value = 1;
  fetchMessageList(); // 根据查询条件获取数据
};

// 处理重置
const handleReset = () => {
  queryForm.value = {
    name: '',
    dateRange: [],
  };
  currentPage.value = 1;
  fetchMessageList(); // 重置后获取全部数据
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
  console.log('组件已挂载，开始获取数据'); // 调试用
  fetchMessageList(); // 页面加载时获取全部数据
});
</script>


<style scoped>
/* 外层容器样式 */
.user-messages-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

/* 消息列表模块样式 */
.message-list {
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

/* 表格容器样式 */
.table-container {
  width: 100%;
  overflow-x: auto; /* 横向滚动 */
}

/* 分页样式 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 弹窗内容样式 */
.info-section p {
  font-size: 20px;
  max-height: 300px; /* 固定高度 */
  padding: 10px;
  border-radius: 4px;
}
</style>
