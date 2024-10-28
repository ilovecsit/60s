<template>


  <div class="login-container">
    <!-- 文本雨背景组件 -->
    <text-rain class="background-rain"></text-rain>


    <div class="userLogin">


      <h1 class="login-title">60S</h1>


      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          class="form-content"
          label-width="100px"
          @keydown.enter="onLogin"
      >

        <el-form-item
            class="form-item"
            prop="email"
        >
          <el-input type="text" placeholder="邮箱账号" v-model="loginForm.email"
                    class="loginInput"
          ></el-input>
        </el-form-item>
        <el-form-item
            class="form-item password-form-item"
            prop="password"
        >
          <el-input
              type="password"
              placeholder="账号密码"
              v-model="loginForm.password"
              class="loginInput"
          ></el-input>

        </el-form-item>
<!--        <div class="forgot-password" @click="onForgtoPassword">忘记密码？</div>-->
        <el-form-item id="loginOrRegister">
          <el-button
              type="primary"
              class="login-button"
              @click="onLogin"
          >登录
          </el-button>
          <span class="register-button" @click="onRegister">注册账号</span>
        </el-form-item>

      </el-form>

    </div>
  </div>

</template>

<script setup>
import {onMounted, ref} from 'vue';
import axios from 'axios';
import TextRain from "@/components/TextRain.vue";
import {ElMessage} from 'element-plus';
import router from "@/router/index.js";
import {sharedData} from '@/global/globalData.js';
import {sharedCookie} from '@/global/globalCookie.js';

const loginForm = ref({
  email: '',
  password: ''
});

// 添加验证规则
const rules = ref({
  email: [
    {validator: validateEmail, trigger: 'blur'}
  ],
  password: [
    {validator: validatePassword, trigger: 'blur'}
  ]
});

// 邮箱验证函数
function validateEmail(rule, value, callback) {
  const regex = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  if (value === '') {
    callback(new Error('邮箱不能为空'));
  } else if (!regex.test(value)) {
    callback(new Error('邮箱格式不正确'));
  } else {
    callback();
  }
}

// 密码验证函数
function validatePassword(rule, value, callback) {
  const regex = /^[0-9A-Za-z]{6,20}$/;
  if (value === '') {
    callback(new Error('密码不能为空'));
  } else if (!regex.test(value)) {
    callback(new Error('密码格式不正确'));
  } else {
    callback();
  }
}

const loginFormRef = ref(null);

// 登录逻辑
async function onLogin() {
  loginFormRef.value?.validate((valid) => {
    if (valid) {
      axios.post('http' + sharedData.serverIP + '/user/login',
          `userEmail=${encodeURIComponent(loginForm.value.email)}&password=${encodeURIComponent(loginForm.value.password)}`,
          {headers: {'Content-Type': 'application/x-www-form-urlencoded'}}
      ).then((response) => {
        const code = response.data.code;
        if (code === 200) {
          ElMessage.success('登录成功');

          sharedCookie.set("loginAuthorization", response.data.data.token.toString(), 365 * 24 * 60);
          const loginUser=response.data.data.userMainInf;
          loginUser.userEmail=loginForm.value.email;
          sharedCookie.setJson("loginUser", loginUser)


          sharedCookie.set("userId", sharedCookie.getJson("loginUser").userId, 365 * 24 * 60)
          sharedCookie.set("autoLogin", "true");

          router.push("/talkingRoom");
        } else if (code === 401) {
          ElMessage.error('账号或密码错误');
        } else {
          ElMessage.error('出现异常');
        }
      }).catch(() => {
        ElMessage.error('出现异常');
      });
    } else {
      ElMessage.error('请输入规范信息');
    }
  });
}

// 注册逻辑
function onRegister() {
  router.push("/register");
}

onMounted(() => {
  if (sharedCookie.get("autoLogin") === 'true') {
    //  检验token是否合法，合法的话跳转到聊天室界面
    axios.get('http' + sharedData.serverIP + '/hello', {
      headers: {
        Authorization: sharedCookie.get("loginAuthorization")
      }
    }).then((response) => {
      if (response.status == 200) {
        router.push("/talkingRoom");
      } else {
        sharedCookie.set("autoLogin", "false");
      }
    }).catch(() => {
      // console.log("出现异常")
    })
  }
})

</script>
<style scoped>
/* 导入字体 */
@import url('https://fonts.googleapis.com/css2?family=Great+Vibes&display=swap');

.login-container {
  position: relative; /* 确保 .rain-effect 相对于 .login-container 定位 */
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background-color: #121212; /* 蓝黑色背景 */
}

.userLogin {
  z-index: 2; /* 确保登录表单在雨效果之上 */
  position: relative; /* 相对于 .login-container 定位 */
  width: 100%;
  max-width: 400px;
  padding: 20px;
  background: #1e1e1e;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  opacity: 0.85;
}

/* 标题样式 */
.login-title {
  color: white;
  text-align: center;
  margin-bottom: 20px;
  font-size: 2rem;
  font-family: 'Great Vibes', cursive;
  font-weight: bold;
  opacity: 1;
}

/* 表单内容样式 */
.form-content {
  display: flex;
  flex-direction: column;
  min-width: 100%;
}

.form-item {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}


/* 忘记密码链接样式 */
.forgot-password {
  text-align: right;
  margin-bottom: 5px;
  display: block;
  color: #409eff;
  cursor: pointer;
  font-size: 0.8rem;
}

/* 密码输入框样式 */
.password-form-item {
  display: flex;
  align-items: center;
}

/* 登录或注册按钮样式 */
#loginOrRegister {
  margin-top: 20px;
  gap: 10px;
  width: 100%;
}

.loginInput {
  margin-right: 50%;
  /*  transform: translateX(50%);*/
  min-width: 80%;
}

.login-button {
  position: absolute;
  left: 50%;
  transform: translateX(-60%);
  color: white;
  background: linear-gradient(60deg, #000, #9d0000, #0d1b34, #000);
  background-size: 200% 200%;
  animation: gradientWipe 4s ease infinite;
  border: none;
  border-radius: 5px;
  padding: 10px 30px;
  font-size: 16px;
  cursor: pointer;
  overflow: hidden;
  transition: background-size 0.5s ease;
  position: relative; /* 用于伪元素 */
}

.login-button:hover {
  background-size: 400% 400%;
  animation: gradientWipe 500ms ease infinite;
}

.login-button::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: inherit;
  border-radius: inherit;
  z-index: -1;
  opacity: 0.8;
}

.register-button {
  position: absolute;
  right: 0;
  text-decoration: underline;
  color: white;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.8rem;
}

.background-rain {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100%;
  z-index: 0; /* 确保背景在表单之下 */
  /* 如果需要，可以添加其他样式来调整背景的显示效果 */
}

</style>