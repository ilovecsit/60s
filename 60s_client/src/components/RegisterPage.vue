<template>
  <div class="register-page">

    <div class="agree_box" v-show="agree_box_show">

      <div class="agreement_box">
        <h1 style="text-align: center">欢迎来到[60s]网络聊天室！</h1>
        <p>
          我们很高兴您选择加入我们的社区。在开始使用我们的服务之前，请仔细阅读以下条款和条件（以下简称“协议”）。通过使用我们的聊天室，您同意遵守本协议的所有条款。</p>
        <h2>1. 服务条款</h2>
        <p>1.1 本服务是由个人开发者提供的在线聊天服务。我们保留随时更新、修改、添加或删除本服务内容的权利。</p>
        <p>1.2 您同意仅出于个人非商业目的使用本服务。</p>
        <h2>2. 用户资格</h2>
        <p>2.1 您必须年满13岁才能使用本服务。如果您未满13岁，请在父母或法定监护人的监督下使用。</p>
        <p>2.2 您同意提供真实、准确、最新和完整的注册信息。</p>
        <h2>3. 用户行为</h2>
        <p>3.1 您同意不使用本服务进行任何非法活动。</p>
        <p>3.2
          您同意不发布、传播或以其他方式传送任何违法、有害、滥用、骚扰、诽谤、侵犯他人权利或隐私、或以其他方式违反任何适用法律、法规的内容。</p>
        <p>3.3 您同意不使用本服务进行任何可能损害、干扰、过度负担或损害我们的服务或网络的行为。</p>
        <h2>4. 知识产权</h2>
        <p>4.1 我们拥有本服务及其所有内容的版权、商标权和其他相关知识产权。</p>
        <p>4.2 您同意不修改、出租、出借、出售、分发或创建基于本服务的衍生作品，除非我们明确授权。</p>
        <h2>5. 隐私政策</h2>
        <p>5.1 我们尊重用户的隐私。我们使用您的个人信息是为了提供和改进我们的服务。</p>
        <p>5.2 我们可能会根据适用法律和政策的要求，与第三方共享您的信息。</p>
        <h2>6. 免责声明</h2>
        <p>6.1 本服务“按原样”提供，我们不提供任何形式的保证。</p>
        <p>6.2 在任何情况下，我们均不对任何直接、间接、附带、特殊、惩罚性或后果性损害承担责任，无论这些损害是否可预见。</p>
        <h2>7. 终止</h2>
        <p>7.1 我们可以随时终止本协议，立即暂停或终止您对本服务的访问。</p>
        <p>7.2 如果您违反本协议的任何条款，我们可能会立即终止本协议。</p>
        <h2>8. 适用法律</h2>
        <p>本协议受中国大陆法律管辖，任何争议将提交至法院解决。</p>
        <h2>9. 其他</h2>
        <p>9.1 本协议构成双方之间关于本服务的完整协议，并取代所有先前的协议和沟通。</p>
        <p>9.2 如果本协议的任何条款被认定为无效或不可执行，其余条款仍然有效。</p>
        <p>感谢您选择我们的网络聊天室。我们期待为您提供一个安全、有趣和富有成效的在线交流环境。</p>
      </div>

      <div class="choice-box">
        <button class="choice_button disagree_button" @click="disagree_agreement">
          不同意
        </button>
        <button class="choice_button agree_button" @click="agree_agreement">
          同意
        </button>
      </div>
    </div>

    <!-- 月亮 -->
    <div
        ref="moonElement"
        class="moon"
        id="moon"
        @click="toggleMoon"
    >


      <!-- 内联 SVG 内容 -->
      <svg id="moo1" class="moon-icon" v-if="moon1Show" viewBox="0 0 1024 1024" version="1.1"
           xmlns="http://www.w3.org/2000/svg"
           width="200"
           height="200"

      >
        <defs>
          <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" :stop-color="startColor"/>
            <stop offset="100%" :stop-color="endColor"/>
          </linearGradient>
        </defs>
        <path
            d="M861.4 869.8c-280 0-507-227-507-507 0-112.1 37.7-214.8 99.3-298.8-179.9 63-309.8 232.6-309.8 434.3 0 254.8 206.8 461.7 461.9 461.7 103.2 0 197.4-35 274.3-92.3-6.4 0.3-12.3 2.1-18.7 2.1z"
            fill="url(#grad1)"></path>
      </svg>

      <svg id="moon2" v-if="moon2Show" class="moon-icon" viewBox="0 0 1024 1024" version="1.1"
           xmlns="http://www.w3.org/2000/svg"
           width="200"
           height="200"
      >
        <defs>
          <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" :stop-color="startColor"/>
            <stop offset="100%" :stop-color="endColor"/>
          </linearGradient>
        </defs>
        <path
            d="M511.862721 1020.714574A508.983962 508.983962 0 0 1 313.796837 43.012991a508.983962 508.983962 0 0 1 396.143863 937.713442 505.597186 505.597186 0 0 1-198.077979 39.988141z"
            fill="url(#grad1)"></path>
        <path
            d="M511.862721 6.048757c279.408971 0 505.827003 226.466414 505.827003 505.814907S791.223309 1017.690667 511.862721 1017.690667 6.047813 791.224253 6.047813 511.863664 232.514228 6.048757 511.862721 6.048757m0-6.047814a512.007868 512.007868 0 0 0-199.239159 983.507484A511.995773 511.995773 0 0 0 711.113975 40.230997 508.681571 508.681571 0 0 0 511.862721 0.000943z"
            fill="url(#grad1)"></path>
      </svg>

      <svg v-if="moon3Show" id="moon3" t="1723206186232" class="moon-icon" viewBox="0 0 1024 1024" version="1.1"
           xmlns="http://www.w3.org/2000/svg"
           p-id="32323" width="200" height="200"
      >
        <defs>
          <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" :stop-color="startColor"/>
            <stop offset="100%" :stop-color="endColor"/>
          </linearGradient>
        </defs>
        <path
            d="M644.5056 70.528C834.4064 127.488 972.8 303.5648 972.8 512c0 254.4896-206.3104 460.8-460.8 460.8-222.4128 0-408.0128-157.568-451.2768-367.1296A433.4848 433.4848 0 0 0 230.4 640c240.3584 0 435.2-194.8416 435.2-435.2 0-44.2112-6.5792-86.8608-18.8416-127.0528z"
            fill="url(#grad1)"
        ></path>
      </svg>

    </div>


    <!-- 动态生成的星星 -->
    <div v-for="star in stars" :key="`star-${star.size}`"
         class="star"
         :style="getStarStyle(star)"
    ></div>

    <!-- 注册表单 -->
    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="register-form"
    >

      <!-- 表单标题 -->
      <h1 class="form-title">账号注册</h1>

      <!-- 用户邮箱输入 -->
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入正确的邮箱"></el-input>
      </el-form-item>

      <!-- 验证码输入和发送按钮 -->
      <el-form-item label="验证码" prop="captcha">
        <el-input v-model="form.captcha" placeholder="请输入验证码">
          <template #append>
            <el-button type="primary" @click="getVerificationCode">获取验证码</el-button>
          </template>
        </el-input>
      </el-form-item>

      <!-- 密码输入 -->
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="form.password" placeholder="长度为6到20位的数字或字母"></el-input>
      </el-form-item>

      <!-- 确认密码输入 -->
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input type="password" v-model="form.confirmPassword" placeholder="请再次确认密码"></el-input>
      </el-form-item>

      <!-- 用户协议复选框 -->
      <!--      <el-form-item>-->
      <div style="display: flex;align-self: center;justify-content: right;">
        <el-checkbox v-model="form.agree">我已阅读并同意<a href="#" @click.prevent="showAgreement">用户协议</a>
        </el-checkbox>
      </div>
      <!--      </el-form-item>-->

      <!-- 修改注册按钮的代码，添加表单验证 -->
      <el-form-item>
        <el-button type="primary" @click="submitForm" class="register-button" :disabled="!form.agree">注册</el-button>
        <router-link to="/login" class="back-text">返回</router-link>
      </el-form-item>
    </el-form>
  </div>
</template>


<script setup>


import {ref, onMounted} from 'vue';
import axios from 'axios';
import {ElMessage, ElForm, ElFormItem, ElInput, ElButton, ElMessageBox} from 'element-plus';
import {sharedData} from '@/global/globalData.js';
import router from "../router/index.js";

// 定义四种渐变色
const gradientColors = [
  {start: '#c4152f', end: '#8a0404'},  // 玫瑰红到血红色
  {start: '#699ee5', end: '#73c5e3'},    // 冰蓝色到天蓝色
  {start: 'rgba(255,255,255,0.97)', end: 'rgba(222,219,206,0.91)'},    // 奶白色到银白色
  {start: '#FFD700', end: '#ee9b02'}     // 暖黄色到橙黄色
];

let agree_box_show = false;

// 存储当前月亮颜色的响应式引用
const moonElement = ref(null);
// 定义光束元素
const moons = ref([
  document.getElementById('moon1'),
  document.getElementById('moon2'),
  document.getElementById('moon3')
]);

let moon1Show = false;
let moon2Show = true;
let moon3Show = true;


let startColor = null;
let endColor = null;
const toggleMoon = () => {
  // 随机选择颜色
  const randomGradient = gradientColors[Math.floor(Math.random() * gradientColors.length)];
  startColor = randomGradient.start;
  endColor = randomGradient.end;


  // 随机选择一个月亮
  const randomIndex = Math.floor(Math.random() * moons.value.length);

  moon1Show = randomIndex == 0;
  moon2Show = randomIndex == 1;
  moon3Show = randomIndex == 2;


};


const stars = ref([]);

const getStarStyle = (star) => ({
  width: star.size + 'px',
  height: star.size + 'px',
  top: star.top,
  left: star.left,
  opacity: star.opacity, // 使用数据属性来控制透明度
});


onMounted(() => {
  toggleMoon();

  for (let i = 0; i < Math.random() * 400 + 100; i++) {
    const size = Math.random() * 3 + 1; // 星星大小1px到4px
    const top = Math.random() * 100 + '%';
    const left = Math.random() * 100 + '%';
    stars.value.push({
      size,
      top,
      left,
    });
  }

// 为每个星星创建一个独立的定时器
  stars.value.forEach(star => {
    let brightness = Math.random();
    let direction = Math.random() < 0.5 ? -1 : 1;
    let speed = Math.random() * 0.05 + 0.01;

    function updateBrightness() {
      brightness += direction * speed;
      if (brightness <= 0 || brightness >= 1) {
        direction *= -1;
        brightness = brightness <= 0 ? 0 : 1;
      }
      star.opacity = brightness; // 修改Vue实例的数据属性
      requestAnimationFrame(updateBrightness);
    }

    updateBrightness();
  });

});


const form = ref({
  email: '',
  captcha: '',
  password: '',
  confirmPassword: '',
  agree: false  // 添加一个属性来跟踪用户是否同意协议
});


const rules = ref({
  email: [
    {required: true, message: '请输入邮箱地址', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入邮箱地址'));
        } else if (!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value)) {
          callback(new Error('邮箱格式错误'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  captcha: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入验证码'));
        } else if (!/^[A-Za-z0-9]{4}$/.test(value)) {
          callback(new Error('验证码格式错误，应为4位数字或字母'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入密码'));
        } else if (!/^[0-9A-Za-z!@#$%^&*?.,]{6,20}$/.test(value)) {
          callback(new Error('密码格式错误，长度为6到20位的数字或字母'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    {required: true, message: '请再次确认密码', trigger: 'blur'},
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请再次确认密码'));
        } else if (value !== form.value.password) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  agree: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('您必须同意用户协议才能注册'));
        } else {
          callback();
        }
      }, trigger: 'change'
    }
  ]
});

// 添加一个方法来显示用户协议
const showAgreement = () => {
  agree_box_show = true;
};

function agree_agreement() {
  agree_box_show = false;
  form.value.agree = true;
  ElMessage.success('已同意用户协议');
}

function disagree_agreement() {
  form.value.agree = false;
  agree_box_show = false;
  ElMessage.error('您必须同意用户协议才能注册');
}

const formRef = ref(null); // 引用注册表单的ref

let emailVerificationAuthorization = "";


// 获取验证码
const getVerificationCode = () => {
  if (!form.value.email || !/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(form.value.email)) {
    ElMessage.error('邮箱不符合规范');
    return;
  }
  ElMessage.warning("正在请求发送验证码");
  axios.get("http" + sharedData.serverIP + `/email/emailVerification`, {
    params: {to: form.value.email}
  }).then(response => {
    const {code, data} = response.data;
    switch (code) {
      case 403:
        ElMessage.error('今日邮箱服务次数达到上限');
        break;
      case 409:
        ElMessage.error('该邮箱已被注册');
        break;
      case 500:
        ElMessage.error('服务器错误，无法注册');
        break;
      case 200:
        ElMessage.success('获取验证码成功,请检查你的邮箱');
        emailVerificationAuthorization = data;
        break;
      default:
        ElMessage.error('未知错误');
        break;
    }
  }).catch(error => {
    ElMessage.error('获取验证码失败' + error);
    // console.error(error);
  });
};

// 注册账号
const submitForm = () => {
  formRef.value?.validate((valid) => {
    if (valid) {
      axios.post('http' + `${sharedData.serverIP}/user/register`, {
        userEmail: form.value.email,
        verificationCode: form.value.captcha,
        password: form.value.password
      }, {
        headers: {
          Authorization: emailVerificationAuthorization || '',
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })
          .then(response => {
            const {code} = response.data;
            if (code === 200) {
              ElMessage.success('注册成功');
              setTimeout(() => {
                router.push('/login');
              }, 1000);
            } else {
              ElMessage.error('注册失败：' + (response.data.message || ''));
            }
          })
          .catch(error => {
            ElMessage.error('注册失败：' + error.message);
            // console.error(error);
          });
    } else {
      ElMessage.error('请填写正确的信息');
    }
  });
};


</script>


<style scoped>

.agree_box {
  min-width: 50%;
  max-width: 90%;
  height: 90%;

  position: absolute;

  background: #96b8e0;

  z-index: 9999;
}

.agreement_box {
  height: 90%;
  width: 100%;

  background: #b9b9de;

  position: absolute;

  top: 0;

  overflow-y: scroll;
}

.choice-box {
  height: 10%;
  width: 100%;

  /*background: blue;*/

  position: absolute;

  bottom: 0;

  display: flex;
  flex-direction: row;
  align-self: center;
  justify-content: space-around;
}

.choice_button {
  width: 30%;
  height: 80%;

  background: #409eff;
}

.disagree_button {
  background: #ff4d51;
}

:root {
  --bg-color: #0a0a23; /* 蓝黑混合色，您可以根据需要调整 */
}

/* 在 RegisterPage.vue 的 <style> 标签中 */
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100vh;
  margin: 0;
  background: conic-gradient(#03032a, #02022f);
}

.moon {
  filter: blur(1px);
  cursor: pointer;
  position: absolute;
  top: 5%;
  right: 15%;
  z-index: 100;
}


.register-form {

  min-width: 300px;
  width: 25%;
}


label {
  margin-left: 0px;
  margin-bottom: 20px; /* 表单项与表单项之间的间距 */
}

.form-title {
  text-align: center;
  color: #fff; /* 文字颜色 */
  margin-bottom: 20px; /* 标题与表单的间距 */
}

.register-form {
  width: 32%; /* 设置表单宽度 */
  padding: 20px;
  background: rgba(255, 255, 255, 0.7); /* 半透明背景 */
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  z-index: 1000;
}

.star {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 2px; /* 星星的基础大小 */
  height: 2px;
  background-color: white;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 1; /* 初始不可见 */
}

@keyframes twinkle {
  0%, 100% {
    opacity: 1;
    box-shadow: 0 0 5px #fff; /* 星星的光晕 */
  }
  50% {
    opacity: 0.5;
    box-shadow: 0 0 10px #fff, 0 0 20px #fff, 0 0 30px #fff; /* 星星闪烁 */
  }
}


.register-button {
  margin-left: 15%;
  width: 50%; /* 按钮宽度 */
  padding: 10px; /* 根据需要调整内边距 */
  font-size: 18px; /* 按钮文字大小 */
}

.back-text {
  color: black; /* 文字颜色 */
  position: absolute;
  right: 20px;
}

/* 添加媒体查询以适应小屏幕设备 */
@media (max-width: 768px) {
  .register-page {
    padding: 20px; /* 增加内边距以适应较小的屏幕 */
  }

  .register-form {
    width: 80%; /* 在小屏幕上增加表单的宽度 */
    margin: 0 auto; /* 水平居中表单 */
  }

  label {
    margin-bottom: 10px; /* 减小表单项之间的间距 */
  }

  .moon {
    top: 10%; /* 调整月亮的位置 */
    right: 5%; /* 调整月亮的位置 */
  }

  .star {
    width: 1px; /* 减小星星的大小 */
    height: 1px;
  }

  .register-button {
    margin-left: 0; /* 移除按钮左侧的内边距 */
    width: 100%; /* 在小屏幕上让按钮占据整个宽度 */
  }

  .back-text {
    right: 10px; /* 调整文本位置 */
  }
}
</style>