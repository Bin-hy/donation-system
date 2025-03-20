<template>
  <div class="barrage-container">
    <!-- 弹幕列表模块 -->
    <div class="barrage-list">
      <!-- 查询表单 -->
      <div class="query-form-container">
        <el-form :inline="true" :model="queryForm" class="query-form">
          <el-row :gutter="20">
            <!-- 左侧表单内容 -->
            <el-col :span="18">
              <el-row :gutter="10">
                <!-- 用户账号 -->
                <el-col :span="8">
                  <el-form-item label="用户账号">
                    <el-input
                        v-model="queryForm.username"
                        placeholder="请输入用户账号"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 用户昵称 -->
                <el-col :span="8">
                  <el-form-item label="用户昵称">
                    <el-input
                        v-model="queryForm.nickname"
                        placeholder="请输入用户昵称"
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

      <!-- 弹幕表格 -->
      <el-table
          :data="paginatedBarrages"
          style="width: 100%"
          border
          :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
      >
        <!-- 用户账号列 -->
        <el-table-column prop="username" label="用户账号" width="150" />

        <!-- 用户头像列 -->
        <el-table-column label="用户头像" width="150">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" size="130" />
          </template>
        </el-table-column>

        <!-- 用户昵称列 -->
        <el-table-column prop="nickname" label="用户昵称" width="200" />

        <!-- 弹幕内容列 -->
        <el-table-column prop="content" label="弹幕内容" width="300" />

        <!-- 弹幕发送时间列 -->
        <el-table-column prop="createdAt" label="发送时间" width="200">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="default" @click="handleDeleteBarrage(row)">删除弹幕</el-button>
            <el-button
                :type="row.isBarrageBanned ? 'success' : 'warning'"
                size="default"
                @click="handleToggleBarrageBan(row)"
            >
              {{ row.isBarrageBanned ? '恢复发送弹幕' : '禁止发送弹幕' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="barrageList.length"
            :page-size="pageSize"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 50]"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

// 弹幕列表数据
const barrageList = ref([]);

// 查询表单数据
const queryForm = ref({
  username: '', // 用户账号
  nickname: '', // 用户昵称
  dateRange: [], // 时间范围
});

// 分页相关状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(10); // 每页显示条数

// 分页后的弹幕列表
const paginatedBarrages = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return barrageList.value.slice(start, end);
});

// 时间格式化函数
const formatTime = (timestamp) => {
  const date = new Date(timestamp); // 将时间戳转换为 Date 对象
  const year = date.getFullYear(); // 获取年份
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 获取月份（补零）
  const day = String(date.getDate()).padStart(2, '0'); // 获取日期（补零）
  const hours = String(date.getHours()).padStart(2, '0'); // 获取小时（补零）
  const minutes = String(date.getMinutes()).padStart(2, '0'); // 获取分钟（补零）
  const seconds = String(date.getSeconds()).padStart(2, '0'); // 获取秒数（补零）

  // 拼接成 YYYY-MM-DD HH:mm:ss 格式
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 获取弹幕数据
const fetchBarrages = async () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const params = {
        username: queryForm.value.username,
        nickname: queryForm.value.nickname,
        startDate: queryForm.value.dateRange?.[0],
        endDate: queryForm.value.dateRange?.[1],
      };

      const response = await axios.get('/admin/barrages', {
        params,
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.data.code === 200) {
        barrageList.value = response.data.data;
      } else {
        console.error('获取弹幕列表失败:', response.data.msg);
        ElMessage.error('获取弹幕列表失败');
      }
    } catch (error) {
      console.error('获取弹幕列表失败:', error);
      ElMessage.error('获取弹幕列表失败：' + error.message);
    }
  } else {
    ElMessage.error('用户未登录，请先登录');
  }
};

// 删除弹幕
const handleDeleteBarrage = async (barrage) => {
  try {
    // 确认删除操作
    await ElMessageBox.confirm('确定要删除这条弹幕吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 从 localStorage 中获取令牌
    const token = localStorage.getItem('token');
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    // 发送删除请求
    await axios.delete(`/admin/barrages/${barrage.id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    // 重新获取弹幕列表
    await fetchBarrages();
    ElMessage.success('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message);
    }
  }
};

// 切换禁止/恢复发送弹幕状态
const handleToggleBarrageBan = async (barrage) => {
  try {
    const action = barrage.isBarrageBanned ? '恢复发送弹幕' : '禁止发送弹幕';
    await ElMessageBox.confirm(`确定要${action}吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 从 localStorage 中获取令牌
    const token = localStorage.getItem('token');
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    // 发送更新请求
    await axios.post(
        `/admin/users/${barrage.userId}/toggle-barrage-ban`,
        { isBarrageBanned: !barrage.isBarrageBanned },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
    );

    // 重新获取弹幕列表
    await fetchBarrages();
    ElMessage.success(`${action}成功`);
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error( error.message);
    }
  }
};


// 处理查询
const handleQuery = () => {
  currentPage.value = 1;
  fetchBarrages(); // 根据查询条件获取数据
};

// 处理重置
const handleReset = () => {
  queryForm.value = {
    username: '',
    nickname: '',
    dateRange: [],
  };
  currentPage.value = 1;
  fetchBarrages(); // 重置后获取全部数据
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
  fetchBarrages(); // 页面加载时获取全部数据
});
</script>

<style scoped>
/* 外层容器样式 */
.barrage-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

/* 弹幕列表模块样式 */
.barrage-list {
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
</style>
