<template>
  <div class="finance-container">
    <!-- 查询表单 -->
    <div class="query-form-container">
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-row :gutter="20">
          <!-- 左侧表单内容 -->
          <el-col :span="18">
            <el-row :gutter="10">




              <!-- 支付方式 -->
              <el-col :span="8">
                <el-form-item label="支付方式">
                  <el-select
                      v-model="queryForm.paymentMethod"
                      placeholder="请选择支付方式"
                      clearable
                      style="width: 100px"
                  >
                    <el-option label="支付宝" :value="0" />
                    <el-option label="微信" :value="1" />
                  </el-select>
                </el-form-item>
              </el-col>


              <!-- 时间范围 -->
              <el-col :span="16">
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

              <!-- 项目名称 -->
              <el-col :span="8">
                <el-form-item label="项目名称">
                  <el-input
                      v-model="queryForm.projectName"
                      placeholder="请输入项目名称"
                      clearable
                  />
                </el-form-item>
              </el-col>

              <!-- 地址搜索 -->
              <el-col :span="16">
                <el-form-item label="地址">
                  <el-input
                      v-model="queryForm.address"
                      placeholder="请输入地址"
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
      <!-- 导出按钮 -->
      <el-button type="success" @click="handleExport">导出明细</el-button>
    </div>

    <!-- 收支明细表格 -->
    <el-table
        :data="paginatedRecords"
        style="width: 100%"
        border
        :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
    >
      <!-- 项目名称 -->
      <el-table-column label="项目名称" width="180">
        <template #default="{ row }">
          <span>{{ row.projectName }}</span>
        </template>
      </el-table-column>

      <!-- 用户信息 -->
      <el-table-column label="用户" width="180">
        <template #default="{ row }">
          <div class="user-info">
            <el-avatar :size="40" :src="row.userAvatar" />
            <span class="username">{{ row.username }}</span>
          </div>
        </template>
      </el-table-column>

      <!-- 金额 -->
      <el-table-column prop="amount" label="金额" width="120">
        <template #default="{ row }">
          <span>￥{{ row.amount.toFixed(2) }}</span>
        </template>
      </el-table-column>

      <!-- 时间 -->
      <el-table-column prop="time" label="时间" width="180">
        <template #default="{ row }">
          <span>{{ formatDate(row.time) }}</span>
        </template>
      </el-table-column>

      <!-- 分类 -->
      <el-table-column prop="category" label="捐赠类型" width="120" />

      <!-- 支付方式 -->
      <el-table-column label="支付方式" width="120">
        <template #default="{ row }">
          <el-tag :type="row.paymentMethod === 0 ? 'success' : 'primary'">
            {{ row.paymentMethod === 0 ? '支付宝' : '微信' }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 是否来自境外 -->
      <el-table-column label="是否境外" width="120">
        <template #default="{ row }">
          <el-tag :type="row.isForeign ? 'danger' : 'success'">
            {{ row.isForeign ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 地址 -->
      <el-table-column prop="address" label="地址" />
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination">
      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="financeRecords.length"
          :page-size="pageSize"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20, 50]"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import * as XLSX from 'xlsx'; // 引入 xlsx 库

// 收支明细数据
const financeRecords = ref([]);

// 查询表单数据
const queryForm = ref({
  address: '', // 地址
  dateRange: [], // 时间范围
  paymentMethod: null, // 支付方式
});

// 分页相关状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(10); // 每页显示条数

// 分页后的收支明细数据
const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return financeRecords.value.slice(start, end);
});

// 格式化日期
const formatDate = (date) => {
  return new Date(date).toLocaleString();
};

// 获取收支明细数据
const fetchFinanceRecords = async () => {
  try {
    const token = localStorage.getItem('token');
    const params = {
      projectName: queryForm.value.projectName, // 项目名称
      address: queryForm.value.address, // 地址
      startDate: queryForm.value.dateRange?.[0], // 开始日期
      endDate: queryForm.value.dateRange?.[1], // 结束日期
      paymentMethod: queryForm.value.paymentMethod, // 支付方式
    };

    const response = await axios.get('/admin/finance-records', {
      params,
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.data.code === 200) {
      financeRecords.value = response.data.data;
    } else {
      ElMessage.error('获取收支明细失败：' + response.data.msg);
    }
  } catch (error) {
    ElMessage.error('获取收支明细失败：' + error.message);
  }
};

// 处理查询
const handleQuery = () => {
  currentPage.value = 1;
  fetchFinanceRecords();
};

// 处理重置
const handleReset = () => {
  queryForm.value = {
    projectName: '', // 项目名称
    address: '', // 地址
    dateRange: [], // 时间范围
    paymentMethod: null, // 支付方式
  };
  currentPage.value = 1;
  fetchFinanceRecords();
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

// 导出 Excel 文件
const handleExport = () => {
  if (financeRecords.value.length === 0) {
    ElMessage.warning('没有数据可导出');
    return;
  }

  // 定义表头
  const headers = [
    '项目名称',
    '用户',
    '金额',
    '时间',
    '捐赠分类',
    '支付方式',
    '是否境外',
    '地址',
  ];

  // 定义数据列
  const data = financeRecords.value.map((item) => [
    item.projectName,
    item.username,
    `￥${item.amount.toFixed(2)}`,
    formatDate(item.time),
    item.category,
    item.paymentMethod === 0 ? '支付宝' : '微信',
    item.isForeign ? '是' : '否',
    item.address,
  ]);

  // 创建工作表
  const worksheet = XLSX.utils.aoa_to_sheet([headers, ...data]);
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, '收支明细');

  // 导出文件
  XLSX.writeFile(workbook, '收支明细.xlsx');
  ElMessage.success('导出成功');
};

// 组件挂载时初始化数据
onMounted(() => {
  fetchFinanceRecords();
});
</script>

<style scoped>
.finance-container {
  border: 1px solid var(--el-border-color-light);
  padding: 20px;
}
.query-form-container {

  background-color: var(--el-bg-color-page);
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}
.button-group {
  margin-top: 40px;
  display: flex;
  justify-content: flex-end;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
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

/* 操作栏样式 */
.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
</style>
