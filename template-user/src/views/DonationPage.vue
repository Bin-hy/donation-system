<template>
  <div class="donation-page">
    <!-- Steps 步骤条 -->
    <el-steps :active="activeStep" finish-status="success" align-center>
      <el-step title="选择捐款项目"/>
      <el-step title="确认捐款信息"/>
      <el-step title="在线支付"/>
      <el-step title="完成捐款"/>
    </el-steps>

    <!-- 步骤内容 -->
    <div class="step-content">
      <!-- 第一步：选择捐款项目 -->
      <div v-if="activeStep === 0">
        <h2>选择捐款项目</h2>
        <el-radio-group v-model="selectedProject">
          <el-radio label="慈善文化进校园项目">慈善文化进校园项目</el-radio>
          <el-radio label="其他项目">其他项目</el-radio>
        </el-radio-group>
      </div>

      <!-- 第二步：确认捐款信息 -->
      <div v-if="activeStep === 1">
        <div class="marking">
          请正确填写捐款人信息。标注(<span class="required-mark">*</span>)为必填项目。
        </div>
        <el-form :model="donationForm" :rules="rules" ref="donationFormRef">
          <el-form-item label="捐赠金额" prop="amount" style="display: flex">
            <div class="amount-buttons">
              <el-button
                  v-for="amount in fixedAmounts"
                  :key="amount"
                  :type="donationForm.amount === amount ? 'primary' : ''"
                  @click="selectAmount(amount)"
              >
                {{ amount }} 元
              </el-button>
              <el-input
                  v-model="donationForm.amount"
                  placeholder="请输入捐赠金额"
                  style="display: flex; align-items: center;"
              >
                <template #prefix>
                   <span style="margin-right: 8px; color: #606266; border-right: 1px solid #dcdfe6; padding-right: 8px;">
                    其他
                   </span>
                </template>
              </el-input>
            </div>
          </el-form-item>
          <el-form-item label="捐赠项目" prop="project">
            <el-input v-model="donationForm.project" disabled/>
          </el-form-item>
          <el-form-item label="捐赠人/企业" prop="donorType">
            <el-radio-group v-model="donationForm.donorType">
              <el-radio label="个人">个人</el-radio>
              <el-radio label="企业">企业</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="联系方式" prop="contact">
            <el-input v-model="donationForm.contact" placeholder="请输入联系方式"/>
          </el-form-item>
          <el-form-item label="付款方式" prop="paymentMethod">
            <div class="payment-methods">
              <div
                  v-for="method in paymentMethods"
                  :key="method.value"
                  :class="['payment-method', { active: donationForm.paymentMethod === method.value }]"
                  @click="selectPaymentMethod(method.value)"
              >
                <img :src="method.image" :alt="method.label"/>
              </div>
            </div>
          </el-form-item>
          <el-form-item prop="agreement" style="margin-left: 77px;">
            <el-checkbox v-model="donationForm.agreement">
              我已了解并同意
              <el-link type="primary" :underline="false" @click="showAgreementDialog">
                《捐赠协议》
              </el-link>
            </el-checkbox>
          </el-form-item>
        </el-form>
      </div>

      <!-- 第三步：在线支付 -->
      <div v-if="activeStep === 2" style="text-align: center;">
        <h2>在线支付</h2>
        <p>请扫描以下二维码完成支付：</p>
        <img
            :src="donationForm.paymentMethod === '微信支付' ? wechatQRCode : alipayQRCode"
            alt="支付二维码"
            style="width: 200px; height: 200px"
        />
        <p>支付金额：{{ donationForm.amount }} 元</p>
      </div>

      <!-- 第四步：完成捐款 -->
      <div v-if="activeStep === 3">
        <el-result icon="success" title="捐款成功" sub-title="感谢您的特别捐赠！">
          <template #extra>
            <p><span style="color:#e40000;font-weight: 700">{{ countdown }}</span> 秒后自动返回首页</p>
            <el-button type="primary" @click="goBackToHome">立即返回首页</el-button>
          </template>
        </el-result>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button v-if="activeStep > 0 && activeStep < 3" @click="prevStep">上一步</el-button>
      <el-button
          v-if="activeStep < 3"
          type="primary"
          @click="nextStep"
          :disabled="activeStep === 1 && !isFormValid"
      >
        下一步
      </el-button>
    </div>

    <!-- 捐赠协议弹窗 -->
    <el-dialog
        v-model="agreementDialogVisible"
        width="50%"
        :before-close="handleClose"
    >
      <h2>网上捐赠协议</h2>
      <div class="agreement-content">
        <p><strong>声明：</strong>捐赠人对所捐赠的善款（物）持有所有权，属自愿行为。</p>
        <p><strong>承诺：</strong>您所捐赠的每一份善款（物），将全额用于系统开展的公益慈善项目，无需承担网站的任何管理成本。</p>
        <h3>1、捐赠来源：</h3>
        <p>捐赠人捐赠的财产，是其有权处分的合法财产，且权属关系无争议。</p>
        <h3>2、捐赠用途：</h3>
        <p>本网站所收到的所有捐赠财产将按捐赠人要求（明确使用方向、实施项目和受益人、捐助地区）用于本机构指定项目，未经捐赠人同意，不会改变捐赠财产的用途；捐赠人未作指定要求的捐赠财产，将作为非限定捐赠用于任何需要项目。</p>
        <p>捐赠7日之内可根据情况酌情调整捐赠用途，7日之后则不再变更。</p>
        <h3>3、权利义务：</h3>
        <p>1）、捐赠人有权向本网站查询捐赠财产的使用及管理情况，并提出意见和建议。对于捐赠人的查询，本网站会如实答复。如出现违反捐赠要求使用捐赠财产的情况，捐赠人有权要求履行捐赠要求或者撤销捐赠行为。</p>
        <p>2）、捐赠人对捐赠行为、捐赠财产和其他有关事项要求保密的，本网站将予以保密。</p>
        <p>3）、捐赠行为发生之后，捐赠人可向本机构索取合法、有效的捐赠凭证。</p>
        <p>4）、捐赠行为发生之后，本系统将按要求与捐赠人补签捐赠协议。</p>
        <h3>4、其他约定</h3>
        <p>对捐赠人已捐出的捐赠财产，都视为本机构的捐赠收入，任何情况下本网站不作任何退还或返还补偿。</p>
        <h3>5、法律的适用和管辖：</h3>
        <p>基本声明的生效、履行、解释及争议的解决均适用中华人民共和国法律。如果本声明的任何条款因与中华人民共和国现行法律相抵触而无效，该等无效不影响其他条款的效力。</p>
      </div>
      <template #footer>
        <el-button type="danger" @click="agreementDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';

// 引入本地图片
import wxImage from '@/assets/wx.png'; // 微信支付图片
import alipayImage from '@/assets/zfb.png'; // 支付宝图片
import wechatQRCodeImage from '@/assets/wechat-qrcode.png'; // 微信支付二维码
import alipayQRCodeImage from '@/assets/alipay-qrcode.png'; // 支付宝二维码

const route = useRoute();
const router = useRouter();

// 项目 ID
const projectId = ref(route.params.id);

// 当前步骤
const activeStep = ref(1); // 默认进入第二步

// 捐赠表单数据
const donationForm = ref({
  amount: '', // 默认金额
  paymentMethod: '', // 付款方式
});

// 固定金额选项
const fixedAmounts = ref(['20', '50', '100']);

// 支付方式选项
const paymentMethods = ref([
  {
    value: '微信支付',
    label: '微信支付',
    image: wxImage, // 使用本地图片
  },
  {
    value: '支付宝',
    label: '支付宝',
    image: alipayImage, // 使用本地图片
  },
]);

// 控制捐赠协议弹窗的显示状态
const agreementDialogVisible = ref(false);

// 显示捐赠协议弹窗
const showAgreementDialog = () => {
  agreementDialogVisible.value = true;
};

// 支付二维码图片
const wechatQRCode = wechatQRCodeImage; // 微信支付二维码
const alipayQRCode = alipayQRCodeImage; // 支付宝二维码

// 表单验证规则
const rules = {
  amount: [
    { required: true, message: '请输入捐赠金额', trigger: 'blur' },
    { pattern: /^\d+$/, message: '请输入有效的金额', trigger: 'blur' },
  ],
  paymentMethod: [
    { required: true, message: '请选择付款方式', trigger: 'change' },
  ],
};

// 表单引用
const donationFormRef = ref(null);

// 检查表单是否有效
const isFormValid = computed(() => {
  return donationForm.value.amount && donationForm.value.paymentMethod;
});

// 选择金额
const selectAmount = (amount) => {
  donationForm.value.amount = amount;
};

// 选择支付方式
const selectPaymentMethod = (method) => {
  donationForm.value.paymentMethod = method;
};

// 获取用户坐标
const getLocation = () => {
  return new Promise((resolve, reject) => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
          (position) => {
            const { latitude, longitude } = position.coords;
            resolve({ latitude, longitude });
          },
          (error) => {
            reject(error);
          }
      );
    } else {
      reject(new Error('浏览器不支持地理位置功能'));
    }
  });
};

// 提交捐款数据
const submitDonation = async () => {
  try {
    // 1. 获取用户坐标
    const { latitude, longitude } = await getLocation();

    // 2. 构造请求数据
    const donationData = {
      projectId: projectId.value, // 项目ID
      amount: donationForm.value.amount, // 捐款金额
      paymentMethod: donationForm.value.paymentMethod === '微信支付' ? 1 : 0, // 支付方式
      isForeign: 0, // 默认非境外
      latitude: latitude, // 纬度
      longitude: longitude, // 经度
    };

    // 3. 发送 POST 请求
    const response = await axios.post('/user/donations', donationData);

    // 4. 处理响应
    if (response.data && response.code === 200) {
      ElMessage.success('捐款提交成功！');
      activeStep.value++; // 进入第四步
      startCountdown(); // 启动倒计时
    } else {
      ElMessage.error('捐款提交失败，请重试');
    }
  } catch (error) {
    console.error('提交失败:', error);
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
      router.push('/login');
    } else {
      ElMessage.error('提交失败，请检查网络');
    }
  }
};

// 倒计时
const countdown = ref(5); // 初始倒计时为 5 秒
let timer = null; // 定时器

// 开始倒计时
const startCountdown = () => {
  timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      clearInterval(timer); // 清除定时器
      goBackToHome(); // 倒计时结束后跳转首页
    }
  }, 1000); // 每秒更新一次
};

// 跳转到首页
const goBackToHome = () => {
  router.push('/'); // 跳转到首页
};

// 在组件卸载时清除定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});

// 下一步
const nextStep = async () => {
  if (activeStep.value === 1) {
    // 在第二步时验证表单
    donationFormRef.value.validate((valid) => {
      if (valid) {
        activeStep.value++;
      } else {
        console.log('表单验证失败');
      }
    });
  } else if (activeStep.value === 2) {
    // 在第三步时提交数据
    await submitDonation();
  } else {
    activeStep.value++;
  }
};

// 上一步
const prevStep = () => {
  if (activeStep.value > 0) {
    activeStep.value--;
  }
};

// 获取项目详情
const fetchProjectDetail = async (id) => {
  try {
    const response = await axios.get(`/user/projects/${id}`);
    if (response.data && response.code === 200) {
      const data = response.data;
      // 将项目名称填入表单
      donationForm.value.project = data.title;
    } else {
      ElMessage.error('获取项目详情失败');
    }
  } catch (error) {
    console.error('请求失败:', error);
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
      router.push('/login');
    } else {
      ElMessage.error('请求失败，请检查网络');
    }
  }
};

// 在组件挂载时获取项目详情
onMounted(() => {
  fetchProjectDetail(projectId.value);
});
</script>

<style scoped>
.donation-page {
  max-width: 760px;
  margin: 20px auto;
  padding: 20px;
  text-align: center;
}

.step-content {
  margin-top: 20px;
  text-align: left;
}

.action-buttons {
  margin-top: 20px;
}


.marking {
  text-align: center;
  margin: 20px;
  border: 1px solid #cccc;
  padding: 15px;
}

.required-mark {
  color: red;
}

.amount-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.payment-methods {
  display: flex;
  gap: 20px;
}

.payment-method {
  height: 40px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  padding: 0 20px;
  text-align: center;
}

.payment-method.active {
  border-color: #409eff;
  background-color: #f0f7ff;
}

.payment-method img {
  width: 100%;
  height: 100%;
}

.agreement-content {
  text-align: left;
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  line-height: 1.6;
}
</style>
