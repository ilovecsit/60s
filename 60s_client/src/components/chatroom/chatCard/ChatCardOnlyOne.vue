<template>
  <div class="mobile">
    <div class="information-bar" id="high-bar">
      <div class="logo left">
        <el-dropdown trigger="click" @command="handleCommand">
          <img src="../../../assets/svg/bar-left.svg"/>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="groupSelect">
                <div class="dropdown-item">选择群聊</div>
              </el-dropdown-item>
              <el-dropdown-item command="groupJoin">
                <div class="dropdown-item">加入群聊</div>
              </el-dropdown-item>
              <el-dropdown-item command="systemCommand">
                <div class="dropdown-item">系统命令</div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <div class="info">
        <span>{{ titleNow }}</span>
      </div>

      <div class="gag-container" v-if="gag_show">
        <el-button type="info" @click="memInfOpen" size="small" >禁言</el-button>
      </div>

      <div class="logo right">
        <div class="img-container" style="width: 100px;">
          <img class="user-img" :src=user_img alt="个人信息" @click="user_information">
        </div>
      </div>

    </div>
    <div class="main-screen">
      <component @sendMessageHandle="sendMessageHandle" :is="currentComponent" @groupInto="groupInto"
                 style="width: 100%;height: 100%"></component>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, shallowRef, provide, defineProps} from 'vue';
import {ElDropdown, ElDropdownMenu, ElDropdownItem, ElMessage} from 'element-plus';

import axios from 'axios';
import {sharedData} from '@/global/globalData.js';
import {sharedCookie} from '@/global/globalCookie.js';
import {sharedFunction} from "@/global/globalFunction.js";
import router from "@/router/index.js";

let titleNow = '开始';

const props = defineProps(['name']); // 定义接收的属性


let ComponentName = props.name;
let currentComponent = ref('DefaultView');


let gag_show = false;


function autoFix() {

  titleNow = sharedCookie.get('titleNow-' + ComponentName);
  if(titleNow===null){
    return;
  }
  if(sharedCookie.get("autoLogin") === 'true') {

    if(titleNow==='个人信息'){
      currentComponent.value = 'UserInfView';
    }else if(titleNow==='进入群聊'){
      groupSelect();
    }else if(titleNow==='加入群聊'){
      groupJoin();
    }else if(titleNow==='禁言'){
      memInfOpen();
    }else{
      //   此时代表进入了群聊的情况
      groupInto(sharedCookie.getJson('groupNow:'+ComponentName));
    }
  }
}

const user_img = ref(sharedCookie.getJson("loginUser").userPhotoUrl);

let groupsInf = ref({});
let groupNow = ref({});


provide('chatCardSharedData', {
  groupsInf
});

provide('groupNow', groupNow);


function memInfOpen() {
  gag_show = false;
  titleNow = '禁言';

  const groupInf = sharedCookie.getJson('groupNow:' + ComponentName);

  groupNow.value = groupInf;

  currentComponent.value = 'GroupMemInfView';
  sharedCookie.set('titleNow-' + ComponentName.toString(), titleNow);
}

function user_information() {
  gag_show = false;
  titleNow = '个人信息';
  currentComponent.value = 'UserInfView';
  sharedCookie.set('titleNow-' + ComponentName.toString(), titleNow);
}

function handleCommand(command) {
  gag_show = false;
  switch (command) {
    case 'groupSelect':
      groupSelect();
      break;
    case 'groupJoin':
      groupJoin();
      break;
    case 'systemCommand':
      systemCommand();
      break;
    case 'memInfOpen':
      memInfOpen();
      break;
    default:
      // console.log('Unknown command:', command);
  }
}

function groupSelect() {
  emit('getGroupMes');
  titleNow = '进入群聊';
  let res = false;
  sharedCookie.set('titleNow-' + ComponentName.toString(), titleNow);
  axios.get('http' + sharedData.serverIP + '/group/allGroupIncludeUser', {
    headers: {
      Authorization: sharedCookie.get('loginAuthorization')
    }
  }).then(response => {
    const {code} = response.data;
    if (code === 200) {
      groupsInf.value = response.data.data;
      currentComponent.value = 'GroupSelectView';
    } else {
      ElMessage.error('获取群聊信息失败');
    }
  })
      .catch(error => {
        ElMessage.error('获取群聊信息失败' + error.message);
        const errCode = error.response.status;
        if (errCode === 401) {
          ElMessage.warning("出现异常，需要重新登录");
          sharedCookie.clearAll();
          router.push("/Login");
        }
      });
}

function groupInto(groupInf) {

  currentComponent.value = 'GroupView';
  titleNow = groupInf.groupName.toString();
  groupNow.value = groupInf;
  sharedCookie.set('titleNow-' + ComponentName, titleNow);
  sharedCookie.setJson('groupNow:' + ComponentName, groupInf);

  if (groupInf.groupCreatedId === sharedCookie.getJson("loginUser").userId) {
    sharedCookie.set("selectGroupMemGroupId:" + groupInf.groupId, groupInf.groupId);
    gag_show = true;
  }
}

function groupJoin() {
  titleNow = '加入群聊';
  sharedCookie.set('titleNow-' + ComponentName, titleNow);
  axios.get('http' + sharedData.serverIP + '/group/allPublicGroupExcludeUser', {
    headers: {
      Authorization: sharedCookie.get('loginAuthorization')
    }
  })
      .then(response => {
        const {code} = response.data;
        if (code === 200) {
          groupsInf.value = response.data.data;
          currentComponent.value = 'GroupJoinView';
        } else {
          ElMessage.error('获取公共群聊信息失败');
        }
      })
      .catch(error => {
        // 使用component ErrorCheckTool
        ElMessage.error('获取公共群聊信息失败' + error.message);

        const errCode = error.response.status;
        if (errCode === 401) {
          ElMessage.warning("出现异常，需要重新登录");
          sharedCookie.clearAll();
          router.push("/Login");
        }
      });
}

function systemCommand() {
  // 实现系统命令的功能
  router.push("systemCommand")
}

// 计算手机的宽度和高度
const screenHeight = window.innerHeight * 0.85; // 90% of the screen width
const screenWidth = screenHeight * (4 / 3); // 根据高宽比计算高度


onMounted(() => {
  setRightPhotoContainer();
  setWH();
  window.addEventListener('resize', setWH);
  // 等待1s后执行
  autoFix();
});

/**
 * 让容器的宽度等于高毒*/
function setRightPhotoContainer() {
  const container = document.querySelector('.img-container');
  container.style.width = window.innerHeight * 0.85 * 0.1 + 'px';
  container.style.height = window.innerHeight * 0.85 * 0.1 + 'px';
}

function setWH() {
  if (!sharedFunction.isPhone()) {
    document.querySelectorAll('.mobile').forEach(element => {
      element.style.width = screenWidth + 'px';
      element.style.height = screenHeight + 'px';
    });
  } else {
    document.querySelector('.mobile').style.width = window.innerWidth + 'px';
    document.querySelector('.mobile').style.height = window.innerHeight + 'px';
  }
}

/**
 * 利用websocket发送消息*/
function sendMessageHandle(content) {
  emit('sendMessageHandle', content);
}

// 使用 defineEmits 声明可触发的事件
const emit = defineEmits(['sendMessageHandle','getGroupMes']);
</script>

<style scoped>

.mobile {

  border-radius: 10px;
  overflow: hidden;
  box-shadow: inset 5px 0 10px rgba(0, 0, 0, 0.5), -5px 0 10px rgba(255, 255, 255, 0.2);
  /*margin: 50px auto;*/
  position: relative;
  background: #f8f8f8;

  margin: 0px 50px;
}

.information-bar {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;

  /*  padding: 10px;*/
  background-color: rgba(240, 240, 240, 0.91);
  color: #011d38;
  font-weight: bold;
  height: 12%;
  box-shadow: inset 5px 0 10px rgba(0, 0, 0, 0.5), -5px 0 10px rgba(255, 255, 255, 0.2);
  width: 100%; /* 确保信息栏宽度为100% */
}

.logo {
  flex: 1;
  height: 80%;
}

.logo,
.info {
  /**设置子元素居中*/
  display: flex;
  align-self: center;
  justify-content: center;
}

.info {
  text-align: center;
  white-space: nowrap; /* 确保文本不换行 */
  overflow: hidden; /* 隐藏超出部分 */
  text-overflow: ellipsis; /* 显示省略号 */
  /* 如果需要多行文本省略，可以使用-webkit-line-clamp，但请注意浏览器兼容性 */
  -webkit-line-clamp: 1; /* 显示的行数 */
  -webkit-box-orient: vertical;
  display: -webkit-box;
}

@supports (-webkit-line-clamp: initial) {
  .information-bar .info {
    white-space: normal;
  }
}

.left img {
  width: 80%;
  height: auto; /* 高度自适应 */
  max-width: 80%; /* 可以设置一个最大宽度限制，根据实际需要调整 */
  margin-right: 20px;
  border-radius: 50%;
}

.right {
  height: 90%;
  display: flex;
  align-self: center;
  justify-content: center;
}

.left,
.right{
  flex:1;
}

.info,
.gag-container{
  flex:3;
}

.gag-container{
  display: flex;
  align-self: center;
  justify-content: center;
}

.img-container {
  width: 100px;
  height: 100%;
  display: flex;

  align-self: center;
  justify-content: center;

  position: relative;
  overflow: hidden; /* 隐藏超出容器的部分 */

  border-radius: 50%;
}


.user-img {
  width: 100%; /* 使图片填满容器宽度 */
  height: 100%; /* 使图片填满容器高度 */
  object-fit: cover; /* 裁剪图片以填充容器 */
  position: absolute; /* 绝对定位 */
  top: 50%; /* 从顶部开始定位 */
  left: 50%; /* 从左侧开始定位 */
  transform: translate(-50%, -50%); /* 居中图片 */
  overflow: hidden;
}

.main-screen {
  /* 根据需要添加更多样式 */
  width: 100%;
  height: 100%;
}


</style>