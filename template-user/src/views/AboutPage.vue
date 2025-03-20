<template>
  <div>
    <!-- 切换按钮 -->
    <div class="tabs">
      <button :class="{ active: currentTab === 'card' }" @click="currentTab = 'card'">
        名片
      </button>
      <button :class="{ active: currentTab === 'messageWall' }" @click="currentTab = 'messageWall'">
        留言墙
      </button>
    </div>

    <!-- 名片模块 -->
    <div v-if="currentTab === 'card'" class="contact">
      <div class="project-list-div">联系我们</div>
      <div class="contact-container">
        <!-- 基金会信息 -->
        <div class="contact-item">
          <h2>北京市心阳慈善基金会</h2>
          <p>电话：400-172-7014</p>
          <p>邮箱：xinyang@foxmail.com</p>
          <p>地址：北京市顺义区牛栏山镇牛富路牛山段3号</p>
          <p>邮编：101301</p>
        </div>

        <!-- 学生资助客服 -->
        <div class="contact-item1">
          <img src="@/assets/xu_student.png" alt="学生资助客服" class="contact-image" />
          <p>学生资助客服</p>
        </div>

        <!-- 爱心人士客服 -->
        <div class="contact-item1">
          <img src="@/assets/wx_user.png" alt="爱心人士客服" class="contact-image" />
          <p>爱心人士客服</p>
        </div>
      </div>
    </div>

    <!-- 留言墙模块 -->
    <div v-if="currentTab === 'messageWall'" class="message-wall-container">
      <div class="project-list-div">留言墙</div>
      <!-- 弹幕容器 -->
      <div class="danmu-container">
        <div
            v-for="(message, index) in messages"
            :key="index"
            :style="message.style"
            class="danmu-item"
            @mouseenter="pauseAnimation(index)"
            @mouseleave="resumeAnimation(index)"
        >
          <img :src="message.avatar" alt="头像" class="danmu-avatar" />
          <span class="danmu-text">{{ message.text }}</span>
        </div>
      </div>
      <!-- 输入框和提交按钮 -->
      <div class="message-input-container">
        <textarea
            v-model="newMessage"
            placeholder="请输入您的留言"
            class="message-input"
        ></textarea>
        <button @click="addMessage" class="submit-button">提交留言</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

// 当前选中的选项卡
const currentTab = ref('card');

// 弹幕样式计算
const getDanmuStyle = () => {
  const top = Math.floor(Math.random() * 80) + 10; // 随机顶部位置
  const duration = Math.floor(Math.random() * 10) + 5; // 随机滚动速度
  return {
    top: `${top}%`,
    animationDuration: `${duration}s`,
  };
};

// 留言墙数据
const messages = ref([]);

// 新留言内容
const newMessage = ref('');

// 获取留言数据
const fetchMessages = async () => {
  try {
    const token = localStorage.getItem('token'); // 从本地存储中获取令牌
    const response = await axios.get('/user/barrages', {
      headers: {
        Authorization: `Bearer ${token}`, // 将令牌添加到请求头
      },
    });

    if (response.code === 200) {
      // 将后端返回的留言数据格式化
      messages.value = response.data.map(message => ({
        text: message.content,
        avatar: message.avatar, // 使用后端返回的头像
        style: getDanmuStyle(), // 动态生成弹幕样式
      }));
    } else {
      throw new Error('获取留言数据失败');
    }
  } catch (error) {
    console.error('获取留言数据失败', error);
    ElMessage.error('获取留言数据失败，请稍后重试');
  }
};

// 添加留言
const addMessage = async () => {
  if (newMessage.value.trim()) {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.post(
          '/user/barrages',
          { content: newMessage.value },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
      );
      if (response.code === 200) {
        // 提交成功后，重新获取留言数据
        await fetchMessages();
        newMessage.value = ''; // 清空输入框
        ElMessage.success('留言提交成功');
      } else {
        throw new Error('留言提交失败');
      }
    } catch (error) {
      console.error('留言提交失败', error);
      ElMessage.error('您已经被禁止发送弹幕，请联系管理员');
    }
  } else {
    ElMessage.warning('留言内容不能为空');
  }
};

// 暂停动画
const pauseAnimation = (index) => {
  const danmuItem = document.querySelectorAll('.danmu-item')[index];
  if (danmuItem) {
    danmuItem.style.animationPlayState = 'paused';
  }
};

// 恢复动画
const resumeAnimation = (index) => {
  const danmuItem = document.querySelectorAll('.danmu-item')[index];
  if (danmuItem) {
    danmuItem.style.animationPlayState = 'running';
  }
};

// 生命周期钩子
onMounted(() => {
  fetchMessages(); // 组件挂载时获取留言数据
});
</script>

<style scoped>
.tabs {
  margin-top: 30px;
  margin-left: 300px;
  margin-bottom: 20px;
}

.tabs button {
  padding: 10px 20px;
  margin: 0 10px;
  border: none;
  border-radius: 4px;
  background-color: #f0f0f0;
  cursor: pointer;
  font-size: 16px;
}

.tabs button.active {
  background-color: #b50020;
  color: #fff;
}

.contact {
  height: 334px;
  margin-top: 30px;
  margin-bottom: 30px;
  margin-left: 20%;
  width: 60%;
  padding: 20px;
  text-align: center;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.project-list-div {
  font-weight: 700;
  font-size: 1.7rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  text-overflow: ellipsis;
  color: #b50020;
  -webkit-box-orient: vertical;
  font-family: 'createCrudeBlack', serif;
  width: max-content;
  margin-bottom: 20px;
}

.contact-container {
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 20px;
}

.contact-item {
  text-align: left;
  width: 400px;
  padding: 20px;
  border-radius: 8px;
}

.contact-item1 {
  text-align: center;
  flex: 1;
  max-width: 400px;
  padding: 20px;
  border-radius: 8px;
}

.contact-item h2 {
  font-size: 18px;
  margin-bottom: 10px;
}

.contact-item p {
  font-size: 14px;
  color: #666;
}

.contact-image {
  width: 150px;
  height: 150px;
  margin-bottom: 10px;
}

.message-wall-container {
  height: 534px;
  margin-top: 30px;
  margin-bottom: 30px;
  margin-left: 20%;
  width: 60%;
  padding: 20px;
  text-align: center;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.danmu-container {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.danmu-item {
  position: absolute;
  display: flex;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.9); /* 背景色 */
  border-radius: 20px; /* 胶囊圆角 */
  padding: 5px 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 阴影 */
  animation: danmu linear infinite;
  animation-play-state: running; /* 默认动画播放 */
}

.danmu-item:hover {
  animation-play-state: paused; /* 鼠标悬停时暂停动画 */
}

.message-input-container {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
}

.submit-button {
  padding: 10px 20px;
  background-color: #b50020;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  white-space: nowrap;
}

.submit-button:hover {
  background-color: #8c0018;
}

.danmu-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%; /* 圆形头像 */
  margin-right: 10px; /* 头像和文字的间距 */
}

.danmu-text {
  font-size: 14px;
  color: #333;
}

@keyframes danmu {
  from {
    transform: translateX(-100%); /* 从左边开始 */
  }
  to {
    transform: translateX(100vw); /* 移动到视口右边边界时消失 */
  }
}
</style>
