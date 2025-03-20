<template>
  <div class="dashboard-container">
    <!-- 统计数据卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <img src="@/assets/user-icon.png" alt="用户图标" class="stat-icon" />
            <div>
              <div class="stat-title">用户总数</div>
              <div class="stat-value">{{ stats.userCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <img src="@/assets/donation-icon.png" alt="捐款图标" class="stat-icon" />
            <div>
              <div class="stat-title">捐款总数</div>
              <div class="stat-value">¥{{ stats.donationTotal }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <img src="@/assets/barrage-icon.png" alt="弹幕图标" class="stat-icon" />
            <div>
              <div class="stat-title">弹幕总数</div>
              <div class="stat-value">{{ stats.barrageCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 双折线图和排行榜 -->
    <el-row :gutter="20" class="charts-and-ranking">
      <!-- 双折线图 -->
      <el-col :span="16">
        <el-card>
          <div class="chart-title">
            近七日趋势
            <el-button type="primary" size="small" @click="downloadChart('trendChart')" style="float: right;">
              下载图表
            </el-button>
          </div>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 排行榜 -->
      <el-col :span="8">
        <el-card>
          <div class="chart-title">捐款总数 Top5</div>
          <el-table :data="donationRanking" style="width: 100%;height: 300px">
            <el-table-column prop="userRank" label="排名" width="80" />
            <el-table-column label="用户">
              <template #default="{ row }">
                <div style="display: flex; align-items: center;">
                  <img :src="row.avatar" alt="用户头像" style="width: 40px; height: 40px; border-radius: 50%; margin-right: 10px;" />
                  <span>{{ row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="捐款金额" align="right">
              <template #default="{ row }">
                ¥{{ row.amount }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增柱状图 -->
    <el-row :gutter="20" class="charts-and-ranking">
      <el-col :span="24">
        <el-card>
          <div class="chart-title">
            近十日捐赠趋势图
            <el-button type="primary" size="small" @click="downloadChart('donationChart')" style="float: right;">
              下载图表
            </el-button>
          </div>
          <div ref="donationChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

// 统计数据
const stats = ref({
  userCount: 0,
  donationTotal: 0,
  barrageCount: 0,
});

// 近七日趋势数据
const trendData = ref({
  dates: [],
  barrages: [],
  newUsers: [],
});

// 近十日捐赠趋势数据
const donationTrendData = ref({
  dates: [],
  donations: [],
});

// 捐款排行榜数据
const donationRanking = ref([]);

// 使用 ref 绑定 DOM 元素
const trendChartRef = ref(null); // 折线图容器
const donationChartRef = ref(null); // 柱状图容器

// 获取令牌
const getToken = () => {
  return localStorage.getItem('token'); // 从本地存储获取令牌
};

// 定义接口方法
const fetchStats = async () => {
  try {
    const token = getToken();
    const response = await axios.get('/admin/dashboard/stats', {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });
    if (response.status === 200 && response.data.code === 200) {
      return response.data.data; // 返回数据部分
    } else {
      throw new Error(response.data.message || '请求失败');
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
    throw error;
  }
};

const fetchTrendData = async () => {
  try {
    const token = getToken();
    const response = await axios.get('/admin/dashboard/trend', {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });
    if (response.status === 200 && response.data.code === 200) {
      return response.data.data; // 返回数据部分
    } else {
      throw new Error(response.data.message || '请求失败');
    }
  } catch (error) {
    console.error('获取趋势数据失败:', error);
    throw error;
  }
};

const fetchDonationTrendData = async () => {
  try {
    const token = getToken();
    const response = await axios.get('/admin/dashboard/donation-trend', {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });
    if (response.status === 200 && response.data.code === 200) {
      return response.data.data; // 返回数据部分
    } else {
      throw new Error(response.data.message || '请求失败');
    }
  } catch (error) {
    console.error('获取捐赠趋势数据失败:', error);
    throw error;
  }
};

const fetchDonationRanking = async () => {
  try {
    const token = getToken();
    const response = await axios.get('/admin/dashboard/donation-ranking', {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });
    if (response.status === 200 && response.data.code === 200) {
      return response.data.data; // 返回数据部分
    } else {
      throw new Error(response.data.message || '请求失败');
    }
  } catch (error) {
    console.error('获取捐款排行榜失败:', error);
    throw error;
  }
};

// 初始化折线图
const initTrendChart = () => {
  if (trendChartRef.value) {
    const trendChart = echarts.init(trendChartRef.value);
    trendChart.setOption({
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: ['新增弹幕数', '新增用户'],
      },
      xAxis: {
        type: 'category',
        data: trendData.value.dates,
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '新增弹幕数',
          type: 'line',
          data: trendData.value.barrages,
        },
        {
          name: '新增用户',
          type: 'line',
          data: trendData.value.newUsers,
        },
      ],
    });
  }
};

// 初始化柱状图
const initDonationChart = () => {
  if (donationChartRef.value) {
    const donationChart = echarts.init(donationChartRef.value);
    donationChart.setOption({
      tooltip: {
        trigger: 'axis',
      },
      xAxis: {
        type: 'category',
        data: donationTrendData.value.dates,
      },
      yAxis: {
        type: 'value',
      },
      series: [
        {
          name: '捐赠金额',
          type: 'bar',
          data: donationTrendData.value.donations,
        },
      ],
    });
  }
};

// 下载图表
const downloadChart = (chartType) => {
  let chartInstance;
  if (chartType === 'trendChart') {
    chartInstance = echarts.getInstanceByDom(trendChartRef.value);
  } else if (chartType === 'donationChart') {
    chartInstance = echarts.getInstanceByDom(donationChartRef.value);
  }

  if (chartInstance) {
    const imgUrl = chartInstance.getDataURL({
      type: 'png',
      pixelRatio: 2,
      backgroundColor: '#fff',
    });
    const link = document.createElement('a');
    link.href = imgUrl;
    link.download = `${chartType}.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
};

// 初始化数据
const initData = async () => {
  try {
    // 获取统计数据
    stats.value = await fetchStats();

    // 获取近七日趋势数据
    const trendResponse = await fetchTrendData();
    trendData.value = trendResponse;

    // 获取近十日捐赠趋势数据
    const donationTrendResponse = await fetchDonationTrendData();
    donationTrendData.value = donationTrendResponse;

    // 获取捐款排行榜数据
    donationRanking.value = await fetchDonationRanking();

    // 初始化图表
    initTrendChart();
    initDonationChart();
  } catch (error) {
    console.error('数据加载失败:', error);
    if (error.response && error.response.status === 401) {
      // 未授权，跳转到登录页
      localStorage.removeItem('token'); // 清除本地存储的令牌
      window.location.href = '/login'; // 跳转到登录页
    }
  }
};

// 组件挂载时初始化数据
onMounted(() => {
  initData();
});
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: left;
}

.stat-content {
  margin-left: 95px;
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 70px; /* 图标宽度 */
  height: 70px; /* 图标高度 */
  margin-right: 16px; /* 图标与文字的间距 */
}

.stat-title {
  font-size: 16px;
  color: #666;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-top: 8px;
}

.charts-and-ranking {
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.chart-container {
  height: 300px;
}
</style>
