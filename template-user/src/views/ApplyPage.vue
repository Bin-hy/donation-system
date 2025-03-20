<template>
  <div class="donation-page">
    <div class="form-container">
      <!-- 表单栏改为2xn格式 -->
      <div class="form-grid">
        <!-- 时间范围 -->
        <div class="form-group">
          <label>时间：</label>
          <el-date-picker
              v-model="queryForm.timeRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </div>

        <!-- 金额范围 -->
        <div class="form-group">
          <label>金额：</label>
          <div class="amount-range">
            <input
                type="text"
                v-model="queryForm.minAmount"
                placeholder="最小金额"
                class="amount-input"
            />
            <span>至</span>
            <input
                type="text"
                v-model="queryForm.maxAmount"
                placeholder="最大金额"
                class="amount-input"
            />
          </div>
        </div>

        <!-- 交易类型 -->
        <div class="form-group">
          <label>交易类型：</label>
          <select v-model="queryForm.transactionType">
            <option value="">全部</option>
            <option value="资金捐赠">资金捐赠</option>
            <option value="资金转账">资金转账</option>
            <option value="资金拨付">资金拨付</option>
          </select>
        </div>

        <!-- 支付方式 -->
        <div class="form-group">
          <label>支付方式：</label>
          <select v-model="queryForm.paymentMethod">
            <option value="">全部</option>
            <option value="微信支付">微信支付</option>
            <option value="支付宝">支付宝</option>
          </select>
        </div>

        <!-- 关键字搜索 -->
        <div class="form-group">
          <label>关键字：</label>
          <input
              type="text"
              v-model="queryForm.keyword"
              placeholder="捐赠人或其他关键词"
              class="keyword-input"
          />
        </div>
      </div>

      <!-- 查询和重置按钮靠右 -->
      <div class="form-actions">
        <button class="query-button" @click="handleQuery">查询</button>
        <button class="reset-button" @click="resetQuery">重置</button>
      </div>
    </div>

    <!-- 在线收入统计 -->
    <div style="display: flex">
      <div style="margin: 30px">
        在线收入
        <span style="font-size: 30px; font-weight: 400; color: #d32f2f">
          ￥{{ totalAmount.toFixed(2) }}
        </span>
        元
      </div>
      <div style="margin-top: 45px; margin-left: 500px">
        本收入情况是针对当前查询条件下所有数据的统计
      </div>
    </div>

    <!-- 捐赠列表 -->
    <div class="donation-list">
      <table>
        <thead>
        <tr>
          <th>时间</th>
          <th>项目名称</th>
          <th>捐款金额</th>
          <th>捐款人</th>
          <th>捐款类型</th>
          <th>捐款平台</th>
          <th>捐赠地址</th> <!-- 新增列 -->
        </tr>
        </thead>
        <tbody>
        <tr v-for="item in paginatedDonationList" :key="item.time">
          <td>{{ item.time }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.amount }}</td>
          <td>{{ item.target }}</td>
          <td>{{ item.category }}</td>
          <td>
            <img
                v-if="item.platform === '微信支付'"
                src="@/assets/wechat-icon.png"
                alt="微信"
                class="platform-icon"
            />
            <img
                v-else-if="item.platform === '支付宝'"
                src="@/assets/alipay-icon.png"
                alt="支付宝"
                class="platform-icon"
            />
          </td>
          <td>{{ item.address }}</td> <!-- 新增列 -->
        </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div class="pagination">
        <button
            :disabled="currentPage === 1"
            @click="currentPage = 1"
            class="pagination-button"
        >
          首页
        </button>
        <button
            :disabled="currentPage === 1"
            @click="currentPage--"
            class="pagination-button"
        >
          上一页
        </button>
        <button
            v-for="page in totalPages"
            :key="page"
            :class="{ active: currentPage === page }"
            @click="currentPage = page"
            class="pagination-button"
        >
          {{ page }}
        </button>
        <button
            :disabled="currentPage === totalPages"
            @click="currentPage++"
            class="pagination-button"
        >
          下一页
        </button>
        <button
            :disabled="currentPage === totalPages"
            @click="currentPage = totalPages"
            class="pagination-button"
        >
          尾页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

// 查询表单数据
const queryForm = ref({
  timeRange: [],
  minAmount: '',
  maxAmount: '',
  transactionType: '',
  donorType: '',
  paymentMethod: '',
  keyword: '',
});

// 分页相关
const pageSize = 20; // 每页显示 20 条
const currentPage = ref(1); // 当前页码

// 原始捐赠列表数据
const donationList = ref([]);

// 过滤后的捐赠列表
const filteredData = ref([]);

// 总金额计算
const totalAmount = computed(() => {
  return filteredData.value.reduce((sum, item) => {
    return sum + parseFloat(item.amount);
  }, 0);
});

// 分页逻辑
const paginatedDonationList = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return filteredData.value.slice(start, end);
});

// 总页数
const totalPages = computed(() => {
  return Math.ceil(filteredData.value.length / pageSize);
});

// 查询
const handleQuery = async () => {
  try {
    const token = localStorage.getItem('token');
    const params = {
      projectName: queryForm.value.keyword || null, // 项目名称
      address: queryForm.value.address || null, // 地址
      startDate: queryForm.value.timeRange[0] || null, // 开始时间
      endDate: queryForm.value.timeRange[1] || null, // 结束时间
      paymentMethod: queryForm.value.paymentMethod === ''
          ? null
          : (queryForm.value.paymentMethod === '微信支付' ? 0 : 1), // 支付方式
      username: queryForm.value.keyword || null, // 用户昵称
      minAmount: parseFloat(queryForm.value.minAmount) || null, // 最小金额
      maxAmount: parseFloat(queryForm.value.maxAmount) || null, // 最大金额
      category: queryForm.value.transactionType || null, // 交易类型
      donorType: queryForm.value.donorType || null, // 捐赠人类型
    };

    // 调用接口获取数据
    const response = await axios.get('/user/finance-records', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      params,
    });

    if (response.code === 200) {
      // 将接口返回的数据映射为前端需要的格式
      donationList.value = response.data.map((record) => ({
        time: formatTime(record.time), // 时间
        name: record.projectName, // 项目名称
        amount: record.amount, // 金额
        target: record.username, // 用户昵称
        category: record.category, // 交易类型
        paymentMethod: record.paymentMethod === 0 ? '微信支付' : '支付宝', // 支付方式
        platform: record.paymentMethod === 0 ? '微信支付' : '支付宝', // 支付平台
        address: record.address, // 捐赠地址
      }));

      // 更新过滤后的数据
      filteredData.value = [...donationList.value];
      currentPage.value = 1; // 重置到第一页
    } else {
      throw new Error(response.msg || '查询失败');
    }
  } catch (error) {
    console.error('查询失败', error);
    ElMessage.error('查询失败，请稍后重试');
  }
};

// 时间格式化函数
const formatTime = (timestamp) => {
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从 0 开始，需要加 1
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 重置
const resetQuery = async () => {
  queryForm.value = {
    timeRange: [],
    minAmount: '',
    maxAmount: '',
    transactionType: '',
    donorType: '',
    paymentMethod: '',
    keyword: '',
  };

  // 重置后重新获取数据
  await handleQuery();
  console.log('已重置查询条件');
};

// 初始化加载数据
onMounted(() => {
  handleQuery();
});
</script>

<style scoped>
.donation-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: Arial, sans-serif;
}

h1 {
  text-align: center;
  color: #d32f2f; /* 红色主题 */
  margin-bottom: 20px;
}

.form-container {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.form-group {
  display: flex;
  align-items: center;
}

.form-group label {
  width: 100px;
  font-weight: bold;
  color: #333;
}

.form-group input,
.form-group select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  flex: 1;
}

.amount-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.amount-range input {
  width: 100px;
}

.query-button,
.reset-button {
  padding: 8px 12px;
  background-color: #d32f2f; /* 红色主题 */
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-left: 10px;
  width: 80px;
}

.reset-button {
  width: 80px;
  border: none;
  background: #f90;
  color: #fff;
  border-radius: 4px;
}

.reset-button:hover {
  background-color: #999;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
}

.donation-list {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #d32f2f; /* 红色主题 */
  color: #fff;
}

.platform-icon {
  width: 24px;
  height: 24px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.pagination-button {
  padding: 8px 12px;
  margin: 0 5px;
  background-color: #fff;
  color: #000;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
}

.pagination-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination-button.active {
  background-color: #d32f2f; /* 红色主题 */
  color: #fff;
}

.pagination-button:hover:not(:disabled) {
  background-color: #d32f2f; /* 红色主题 */
  color: #fff;
}

/* 确保地址列宽度合适 */
td:nth-child(7) {
  min-width: 150px; /* 根据实际需求调整 */
}
</style>
