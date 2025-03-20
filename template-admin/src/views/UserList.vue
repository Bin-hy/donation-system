<template>
  <div class="user-container">
    <!-- 用户列表模块 -->
    <div class="user-list">
      <!-- 查询表单 -->
      <div class="query-form-container">
        <el-form :inline="true" :model="queryForm" class="query-form">
          <el-row :gutter="20">
            <!-- 左侧表单内容 -->
            <el-col :span="18">
              <el-row :gutter="10">
                <!-- 账号（用户名） -->
                <el-col :span="6">
                  <el-form-item label="账号">
                    <el-input
                        v-model="queryForm.username"
                        placeholder="请输入账号"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 昵称 -->
                <el-col :span="6">
                  <el-form-item label="昵称">
                    <el-input
                        v-model="queryForm.nickname"
                        placeholder="请输入昵称"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 邮箱 -->
                <el-col :span="6">
                  <el-form-item label="邮箱">
                    <el-input
                        v-model="queryForm.email"
                        placeholder="请输入邮箱"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 电话 -->
                <el-col :span="6">
                  <el-form-item label="电话">
                    <el-input
                        v-model="queryForm.phone"
                        placeholder="请输入电话"
                        clearable
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
            :disabled="selectedUsers.length === 0"
            @click="handleBatchDelete"
        >
          一键删除
        </el-button>
      </div>

      <!-- 用户表格 -->
      <el-table
          :data="paginatedUsers"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
          :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
      >
        <!-- 多选列 -->
        <el-table-column type="selection" width="55" />

        <!-- 用户名列 -->
        <el-table-column prop="username" label="账号" width="150" />

        <!-- 昵称列 -->
        <el-table-column prop="nickname" label="昵称" width="150" />

        <!-- 头像列 -->
        <el-table-column label="头像" width="150">
          <template #default="{ row }">
            <el-image
                style="width: 50px; height: 50px; border-radius: 50%"
                :src="row.avatar"
                fit="cover"
                @click="handleAvatarClick(row.avatar)"
            />
          </template>
        </el-table-column>

        <!-- 邮箱列 -->
        <el-table-column prop="email" label="邮箱" width="200" />

        <!-- 电话列 -->
        <el-table-column prop="phone" label="电话" width="150" />

        <!-- 封禁状态列 -->
        <el-table-column prop="isBanned" label="封禁状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.isBanned ? 'danger' : 'success'">
              {{ row.isBanned ? '已封禁' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 创建时间列 -->
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <!-- 修改时间列 -->
        <el-table-column prop="updatedAt" label="修改时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>

        <!-- 操作列（固定在右侧） -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
                :type="row.isBanned ? 'success' : 'danger'"
                size="default"
                @click="handleBan(row)"
            >
              {{ row.isBanned ? '解封' : '封禁' }}
            </el-button>
            <el-button type="warning" size="default" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="userList.length"
            :page-size="pageSize"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 50]"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 图片弹窗 -->
    <el-dialog
        v-model="avatarDialogVisible"
        title="查看头像"
        width="30%"
        center
    >
      <el-image
          style="width: 100%; height: auto"
          :src="currentAvatar"
          fit="cover"
      />
    </el-dialog>

    <!-- 编辑抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        title="编辑用户"
        direction="rtl"
        size="30%"
    >
      <el-form :model="formData" label-width="120px">
        <el-form-item label="头像">
          <el-image
              style="width: 100px; height: 100px; border-radius: 50%"
              :src="formData.avatar"
              fit="cover"
          />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="formData.username" placeholder="请输入账号" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';

// 用户列表数据
const userList = ref([]);

// 查询表单数据
const queryForm = ref({
  username: '', // 账号（用户名）
  nickname: '', // 昵称
  email: '', // 邮箱
  phone: '', // 电话
});

// 分页相关状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(5); // 每页显示条数

// 选中的用户
const selectedUsers = ref([]);

// 编辑抽屉相关状态
const drawerVisible = ref(false);
const formData = ref({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
});

// 图片弹窗相关状态
const avatarDialogVisible = ref(false);
const currentAvatar = ref('');

// 分页后的用户列表
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return userList.value.slice(start, end);
});

// 时间格式化方法
const formatDate = (dateArray) => {
  if (!dateArray || !Array.isArray(dateArray) || dateArray.length !== 6) return '-'; // 如果时间数据无效，返回占位符

  // 将数组转换为 Date 对象
  const [year, month, day, hours, minutes, seconds] = dateArray;
  const date = new Date(year, month - 1, day, hours, minutes, seconds); // 注意：月份需要减 1

  // 格式化日期
  const formattedYear = date.getFullYear();
  const formattedMonth = String(date.getMonth() + 1).padStart(2, '0'); // 月份补零
  const formattedDay = String(date.getDate()).padStart(2, '0'); // 日期补零
  const formattedHours = String(date.getHours()).padStart(2, '0'); // 小时补零
  const formattedMinutes = String(date.getMinutes()).padStart(2, '0'); // 分钟补零
  const formattedSeconds = String(date.getSeconds()).padStart(2, '0'); // 秒数补零

  return `${formattedYear}-${formattedMonth}-${formattedDay} ${formattedHours}:${formattedMinutes}:${formattedSeconds}`;
};

// 点击头像事件
const handleAvatarClick = (avatarUrl) => {
  currentAvatar.value = avatarUrl;
  avatarDialogVisible.value = true;
};

// 获取用户数据
const fetchUsers = async () => {
  try {
    const token = localStorage.getItem('token'); // 从 localStorage 获取令牌
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    const params = {
      username: queryForm.value.username,
      nickname: queryForm.value.nickname,
      email: queryForm.value.email,
      phone: queryForm.value.phone,
    };

    const response = await axios.get('/admin/users', {
      params,
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });

    if (response.data.code === 200) {
      userList.value = response.data.data;
    } else {
      ElMessage.error('获取用户列表失败');
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败：' + error.message);
  }
};

// 封禁/解封用户
const handleBan = async (user) => {
  try {
    const token = localStorage.getItem('token'); // 从 localStorage 获取令牌
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    const action = user.isBanned ? '解封' : '封禁';
    await ElMessageBox.confirm(`确定要${action}用户 ${user.username} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    const response = await axios.post(`/admin/users/ban/${user.id}`, {
      isBanned: !user.isBanned,
    }, {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });

    if (response.data.code === 200) {
      ElMessage.success(`${action}成功`);
      await fetchUsers(); // 重新获取数据
    } else {
      ElMessage.error(`${action}失败`);
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(` ${error.message}`);
    }
  }
};

// 编辑用户
const handleEdit = (user) => {
  formData.value = { ...user };
  drawerVisible.value = true;
};

// 提交表单（编辑）
const handleSubmit = async () => {
  try {
    const token = localStorage.getItem('token'); // 从 localStorage 获取令牌
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    await axios.put(`/admin/users/${formData.value.id}`, formData.value, {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });

    ElMessage.success('编辑成功');
    drawerVisible.value = false;
    await fetchUsers(); // 重新获取数据
  } catch (error) {
    ElMessage.error('操作失败：' + error.message);
  }
};

// 处理选中用户
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection;
};

// 批量删除用户
const handleBatchDelete = async () => {
  try {
    const token = localStorage.getItem('token'); // 从 localStorage 获取令牌
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    await ElMessageBox.confirm('确定要删除选中的用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    const selectedIds = selectedUsers.value.map(user => user.id);
    await axios.post('/admin/users/batch-delete', selectedIds, {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });

    await fetchUsers();
    ElMessage.success('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message);
    }
  }
};

// 处理查询
const handleQuery = () => {
  currentPage.value = 1;
  fetchUsers();
};

// 处理重置
const handleReset = () => {
  queryForm.value = {
    username: '',
    nickname: '',
    email: '',
    phone: '',
  };
  currentPage.value = 1;
  fetchUsers();
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
  fetchUsers();
});
</script>

<style scoped>
/* 外层容器样式 */
.user-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

/* 用户列表模块样式 */
.user-list {
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
</style>
