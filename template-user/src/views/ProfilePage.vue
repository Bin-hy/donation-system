<template>
  <div class="profile-page">
    <div class="container">
      <div class="header">
        <h1 style="color: #CC0000">个人中心</h1>
      </div>
      <div class="content">
        <div class="sidebar">
          <ul>
            <li :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">
              <img v-if="activeTab === 'info'" src="@/assets/people.png" class="sidebar-icon">
              <img v-else src="@/assets/people1.png" class="sidebar-icon">
              我的信息
            </li>
            <li :class="{ active: activeTab === 'avatar' }" @click="activeTab = 'avatar'">
              <img v-if="activeTab === 'avatar'" src="@/assets/avatar1.png" class="sidebar-icon">
              <img v-else src="@/assets/avatar.png" class="sidebar-icon">
              我的头像
            </li>
            <li :class="{ active: activeTab === 'password' }" @click="activeTab = 'password'">
              <img v-if="activeTab === 'password'" src="@/assets/password2.png" class="sidebar-icon">
              <img v-else src="@/assets/password1.png" class="sidebar-icon">
              修改密码
            </li>
            <li :class="{ active: activeTab === 'donation' }" @click="activeTab = 'donation'">
              <img v-if="activeTab === 'donation'" src="@/assets/donation.png" class="sidebar-icon">
              <img v-else src="@/assets/donation1.png" class="sidebar-icon">
              我的捐赠
            </li>
            <li :class="{ active: activeTab === 'myBarrages' }" @click="activeTab = 'myBarrages'">
              <img v-if="activeTab === 'myBarrages'" src="@/assets/barrage1.png" class="sidebar-icon">
              <img v-else src="@/assets/barrage.png" class="sidebar-icon">
              我的弹幕
            </li>
          </ul>
        </div>
        <div class="main-content">
          <!-- 我的信息 -->
          <div v-if="activeTab === 'info'" class="tab-content">
            <h2 class="tab-title">
              <img src="@/assets/people1.png" class="title-icon">
              我的信息
            </h2>
            <el-form :model="form" label-width="120px" class="xingXi">
              <el-form-item label="昵称">
                <el-input v-model="form.nickname" placeholder="请输入昵称"></el-input>
              </el-form-item>
              <el-form-item label="账号">
                <el-input v-model="form.username" disabled placeholder="请输入账号"></el-input>
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
              </el-form-item>
              <el-form-item label="电话">
                <el-input v-model="form.phone" placeholder="请输入电话"></el-input>
              </el-form-item>
            </el-form>
            <el-button type="primary" class="save-button" @click="saveInfo">保存</el-button>
          </div>

          <!-- 我的头像 -->
          <div v-if="activeTab === 'avatar'" class="tab-content">
            <h2 class="tab-title">
              <img src="@/assets/avatar.png" class="title-icon">
              我的头像
            </h2>
            <div class="avatar-content">
              <label for="file_input" class="first-change-lb">
                <img src="//s1.hdslb.com/bfs/static/jinkela/account/assets/local_icon.png" class="first-change-lb_img">
                <span>选择本地图片</span>
              </label>
              <input type="file" id="file_input" ref="avatarInput" style="display: none;" @change="handleAvatarUpload" accept="image/*">
              <div class="border-line"></div>
              <div class="avatar-preview">
                <img :src="avatarUrl" alt="当前头像" class="avatar-image">
              </div>
            </div>
            <p class="descript">请选择图片上传：大小150 * 150像素支持JPG、PNG等格式，图片需小于2M</p>
            <el-button type="primary" class="save-buttonn" @click="saveAvatar">保存</el-button>
          </div>

          <!-- 修改密码 -->
          <div v-if="activeTab === 'password'" class="tab-content">
            <h2 class="tab-title">
              <img src="@/assets/password1.png" class="title-icon">
              修改密码
            </h2>
            <el-form :model="form" label-width="120px" class="xingXi">
              <el-form-item label="密码">
                <el-input v-model="form.password" type="password" placeholder="请输入新的密码" show-password></el-input>
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input v-model="form.confirmPassword" type="password" placeholder="请确认密码" show-password></el-input>
              </el-form-item>
            </el-form>
            <el-button type="primary" class="save-button" @click="savePassword">保存</el-button>
          </div>

          <!-- 我的捐赠 -->
          <div v-if="activeTab === 'donation'" class="tab-content">
            <h2 class="tab-title">
              <img src="@/assets/donation1.png" class="title-icon">
              我的捐赠
            </h2>
            <div class="donation-content">
              <!-- 总金额显示 -->
              <div class="total-amount">
                总金额：<span class="amount-value">￥{{ totalDonationAmount }}</span>
              </div>
              <el-table :data="paginatedDonationList" style="width: 100%">
                <el-table-column prop="date" label="捐赠日期" width="180"></el-table-column>
                <el-table-column prop="amount" label="捐赠金额" width="180">
                  <template #default="{ row }">
                    ￥{{ row.amount }} <!-- 金额前加人民币符号 -->
                  </template>
                </el-table-column>
                <el-table-column prop="paymentMethod" label="捐赠方式"></el-table-column>
              </el-table>
              <!-- 分页控件 -->
              <div class="pagination">
                <button
                    v-for="page in totalDonationPages"
                    :key="page"
                    :class="{ active: currentPage === page }"
                    @click="currentPage = page"
                >
                  {{ page }}
                </button>
              </div>
            </div>
          </div>

          <!-- 我的弹幕 -->
          <div v-if="activeTab === 'myBarrages'" class="tab-content">
            <h2 class="tab-title">
              <img src="@/assets/barrage.png" class="title-icon">
              我的弹幕
            </h2>
            <div class="barrage-content">
              <el-table :data="paginatedMyBarrages" style="width: 100%">
                <el-table-column prop="content" label="弹幕内容"></el-table-column>
                <el-table-column prop="createdAt" label="发送时间" width="180"></el-table-column>
                <el-table-column label="操作" width="120">
                  <template #default="{ row }">
                    <el-button type="danger" size="small" @click="deleteBarrage(row.id)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <!-- 分页控件 -->
              <div class="pagination">
                <button
                    v-for="page in totalBarragePages"
                    :key="page"
                    :class="{ active: currentPage === page }"
                    @click="currentPage = page"
                >
                  {{ page }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

// eslint-disable-next-line no-unused-vars
const router = useRouter();

// 响应式数据
const activeTab = ref('info');
const form = ref({
  nickname: '',
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
});
const avatarUrl = ref('https://via.placeholder.com/150'); // 默认头像URL
const avatarInput = ref(null);

// 分页相关数据
const currentPage = ref(1); // 当前页码
const pageSize = ref(8); // 每页显示的数量

// 捐赠记录
const donationList = ref([]);

// 弹幕记录
const myBarrages = ref([]);

// 计算分页后的捐赠数据
const paginatedDonationList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return donationList.value.slice(start, end);
});

// 计算分页后的弹幕数据
const paginatedMyBarrages = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return myBarrages.value.slice(start, end);
});

// 计算捐赠总页数
const totalDonationPages = computed(() => {
  return Math.ceil(donationList.value.length / pageSize.value);
});

// 计算弹幕总页数
const totalBarragePages = computed(() => {
  return Math.ceil(myBarrages.value.length / pageSize.value);
});

// 计算捐赠总金额
const totalDonationAmount = computed(() => {
  return donationList.value.reduce((total, record) => total + parseFloat(record.amount), 0).toFixed(2);
});

// 获取用户信息
const fetchUserProfile = async () => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/user/profile', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const user = response.data;
    form.value.nickname = user.nickname;
    form.value.username = user.username;
    form.value.email = user.email;
    form.value.phone = user.phone;
    avatarUrl.value = user.avatar;
    await fetchDonationList(user.nickname);
    await fetchMyBarrages(user.username); // 获取我的弹幕
  } catch (error) {
    console.error('获取用户信息失败', error);
  }
};

// 获取捐赠记录
const fetchDonationList = async (username) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/user/finance-records', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      params: {
        username: username,
      },
    });

    if (response.code === 200) {
      const donations = response.data.filter(record => record.category === '资金捐赠');
      donationList.value = donations.map(record => ({
        date: new Date(record.time).toLocaleDateString(),
        amount: record.amount.toFixed(2),
        paymentMethod: record.paymentMethod === 0 ? '支付宝' : '微信',
      }));
    } else {
      throw new Error(`请求失败，状态码：${response.code}`);
    }
  } catch (error) {
    console.error('获取捐赠记录失败', error);
    ElMessage.error('获取捐赠记录失败，请稍后重试');
  }
};

// 获取我的弹幕
const fetchMyBarrages = async (username) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.get('/user/barrages', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      params: {
        username: username,
      },
    });

    if (response.code === 200) {
      myBarrages.value = response.data.map(barrage => ({
        id: barrage.id,
        content: barrage.content,
        createdAt: new Date(barrage.createdAt).toLocaleString(),
      }));
    } else {
      throw new Error('获取我的弹幕失败');
    }
  } catch (error) {
    console.error('获取我的弹幕失败', error);
    ElMessage.error('获取我的弹幕失败，请稍后重试');
  }
};

// 删除弹幕
const deleteBarrage = async (barrageId) => {
  try {
    const token = localStorage.getItem('token');
    const response = await axios.delete(`/user/barrages/${barrageId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.code === 200) {
      ElMessage.success('弹幕删除成功');
      await fetchMyBarrages(form.value.username); // 重新获取我的弹幕
    } else {
      throw new Error('弹幕删除失败');
    }
  } catch (error) {
    console.error('弹幕删除失败', error);
    ElMessage.error('弹幕删除失败，请稍后重试');
  }
};

// 处理头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0];
  if (file) {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const token = localStorage.getItem('token');
      const response = await axios.post('/files/upload', formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      // 获取上传后的图片 URL
      avatarUrl.value = response.data;
      ElMessage.success('头像上传成功');
    } catch (error) {
      console.error('上传头像失败', error);
      ElMessage.error('上传头像失败');
    }
  }
};

// 保存头像
const saveAvatar = async () => {
  try {
    const token = localStorage.getItem('token');

    // 确保 avatarUrl 是一个字符串
    if (typeof avatarUrl.value !== 'string') {
      throw new Error('avatarUrl 不是一个字符串');
    }

    const requestData = {
      avatar: avatarUrl.value,
    };

    await axios.put('/user/profile', requestData, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    ElMessage.success('头像保存成功');

    // 乐观加载：立即更新导航栏头像
    if (window.navBarInstance) {
      window.navBarInstance.updateProfile(avatarUrl.value, form.value.nickname);
    }

    // 刷新页面
    location.reload();
  } catch (error) {
    console.error('保存头像失败', error);
    ElMessage.error('保存头像失败');
  }
};

// 保存信息
const saveInfo = async () => {
  try {
    const token = localStorage.getItem('token');

    // 检查昵称长度
    if (form.value.nickname.length > 6) {
      ElMessage.error('昵称最多为6位');
      return; // 直接返回，不继续执行
    }

    const requestData = {
      nickname: form.value.nickname,
      email: form.value.email,
      phone: form.value.phone,
    };

    const response = await axios.put('/user/profile', requestData, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });

    // 检查响应状态码
    if (response.code !== 200) {
      ElMessage.error('昵称已存在，请换一个昵称');
      return; // 直接返回，不继续执行
    }

    ElMessage.success('信息保存成功');

    // 刷新页面
    location.reload();

    // 乐观加载：立即更新导航栏信息
    if (window.navBarInstance) {
      window.navBarInstance.updateProfile(avatarUrl.value, form.value.nickname);
    }
  } catch (error) {
    console.error('保存信息失败', error);

    // 处理昵称已存在的错误
    if (error.response && error.response.status === 400 && error.response.data.message === '昵称已存在') {
      ElMessage.error('昵称已存在，请换一个昵称');
    } else {
      ElMessage.error('保存信息失败');
    }
  }
};

// 保存密码
const savePassword = async () => {
  try {
    const token = localStorage.getItem('token');

    // 检查密码是否一致
    if (form.value.password !== form.value.confirmPassword) {
      throw new Error('两次输入的密码不一致');
    }

    const requestData = {
      password: form.value.password,
    };

    await axios.put('/user/profile', requestData, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });



    // 清空密码输入框
    form.value.password = '';
    form.value.confirmPassword = '';

    // 删除本地 token
    localStorage.removeItem('token');
    ElMessage.success('密码修改成功，请重新登录');
    // 跳转到登录界面
    await router.push('/login-register');
  } catch (error) {
    console.error('保存密码失败', error);
    ElMessage.error('保存密码失败');
  }
};

// 生命周期钩子
onMounted(() => {
  fetchUserProfile();
});
</script>

<style scoped>
.profile-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f0f0;
}

.container {
  width: 800px;
  height: 500px;
  background-color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  padding: 20px;
  position: relative;
  margin-top: -80px;
}

.header {
  margin-top: -10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.content {
  margin-top: -20px;
  margin-left: -10px;
  display: flex;
  flex: 1;
}

.sidebar {
  width: 200px;
  border-right: 1px solid #e0e0e0;
  padding-right: 20px;
}

.sidebar ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar li {
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
}

.sidebar li:hover {
  background-color: #ffcccc; /* 红色悬停背景 */
}

.sidebar li.active {
  background-color: #cc0000; /* 红色选中背景 */
  color: white;
}

.sidebar-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.main-content {
  flex: 1;
  padding-left: 20px;
}

.xingXi {
  margin-left: -40px;
  margin-top: 100px;
}

.tab-content {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 5px;
}

.tab-title {
  margin-bottom: 50px;
  margin-top: -90px;
  color: #cc0000; /* 红色标题 */
  position: relative;
  display: flex;
  align-items: center;
}

.tab-title::before {
  content: '';
  position: absolute;
  left: -15px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background-color: #cc0000; /* 红色标题装饰线 */
}

.title-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.save-button {
  margin-left: 254px;
  background-color: #cc0000; /* 红色按钮 */
  border-color: #cc0000;
}

.save-button:hover {
  background-color: #a30000; /* 深红色悬停 */
  border-color: #a30000;
}

.save-buttonn {
  margin-left: 254px;
  margin-top: 20px;
  background-color: #cc0000; /* 红色按钮 */
  border-color: #cc0000;
}

.save-buttonn:hover {
  background-color: #a30000; /* 深红色悬停 */
  border-color: #a30000;
}

.avatar-content {
  margin-left: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 80px;
}

.first-change-lb {
  cursor: pointer;
  background: #f1f2f5;
  width: 178px;
  height: 84px;
  border: 1px solid #e5e9ef;
  border-radius: 4px;
  transition: all .6s ease;
  display: flex;
  align-items: center;
  padding-left: 35px;
}

.first-change-lb_img {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.border-line {
  height: 182px;
  width: 1px;
  background: #e5e9ef;
  float: left;
}

.descript {
  margin-top: 40px;
  margin-left: 10px;
  line-height: 16px;
  color: #99a2aa;
  font-size: 15px;
}

.avatar-preview {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e0e0e0;
  margin-right: 50px;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.pagination button {
  margin: 0 5px;
  padding: 5px 10px;
  border: 1px solid #cc0000;
  background-color: white;
  color: #cc0000;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.3s;
}

.pagination button.active {
  background-color: #cc0000;
  color: white;
}

.pagination button:hover {
  background-color: #cc0000;
  color: white;
}

.total-amount {
  margin-bottom: 20px;
  font-size: 16px;
  color: #000; /* 总金额文字为黑色 */
}

.amount-value {
  font-style: italic;
  color: #cc0000; /* 金额值为红色 */
  font-weight: bold;
  font-size: 20px;
}
</style>
