<template>
  <div class="group-mem-inf-page">
    <div class="search">
      <!--通过关键字搜索群聊，左边是输入框，右边是查询按钮-->
      <el-input v-model="search_input" placeholder="输入关键字以查找" class="search-input" clearable/>
      <el-button type="primary" class="search-button" @click="searchGroup">搜索</el-button>
    </div>
    <div class="group-mems">
      <div
          v-for="(group, index) in groups"
          :key="index"
          class="mem-row"
          @click="handleGroupClick(index)"
      >
        <div class="mem-avatar">
          <img :src="group.groupAvatar"/>
        </div>
        <div class="mem-inf">
          <div class="mem-name">{{ group.groupName }}</div>
        </div>
        <div class="group-members">(<a style="color:red;">{{ group.groupMemNum }}</a>/{{ group.groupMaxMemNum }})</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref, defineEmits, inject} from 'vue';
import ElementPlus, {ElMessage, ElMessageBox} from "element-plus";
import {sharedData} from "@/global/globalData.js";
import axios from "axios";
import {sharedCookie} from "@/global/globalCookie.js";
import router from "@/router/index.js";

const search_input=ref('');

function searchGroup(){
  console.log(search_input.value.toString())
  axios.get('http' + sharedData.serverIP + '/group/likeSelect?keyWord='+search_input.value.toString(), {
    headers: {
      Authorization: sharedCookie.get('loginAuthorization')
    }
  })
      .then(response => {
        console.log(response.data)
        const {code} = response.data;
        if (code === 200) {
          groups.value=response.data.data;
        }
        else {
          ElMessage.error('查询失败');
        }
      })
      .catch(error => {
        ElMessage.error('发生错误' + error.message);
        const errCode=error.response.status;
        if(errCode===401){
          ElMessage.warning("出现异常，需要重新登录");
          sharedCookie.clearAll();
          router.push("/Login");
        }
      });
}

// 模拟群聊数据
const groups = ref({});

// 使用 defineEmits 声明可触发的事件
const emit = defineEmits(['setWH', 'groupInto']);

onMounted(() => {
  init();
  groups.value = inject('chatCardSharedData').groupsInf.value;
});

function groupJoinHandler(groupInf) {
  const groupId = groupInf.groupId;
  /**
   * axios请求，post，需要设置 数据格式 x-www 添加请求头 Authorization 请求路径http://localhost:9191/60s/group/groupJoin*/
  axios.post('http' + sharedData.serverIP + "/group/groupJoin", {
    groupId: groupId
  }, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      Authorization: sharedCookie.get("loginAuthorization")
    }
  })
      .then(response => {
        const {code} = response.data;
        if (code === 200) {
          ElMessage.success("加入群聊成功");
          emit('groupInto', groupInf);
        }
        else {
          ElMessage.error("加入群聊失败");
          console.log(response.data)
        }
      }).catch(error => {
    if(error.code===401){
      ElMessage.warning("需要重新登录");
      sharedCookie.clearAll();
      router.push("/Login");
    }
  })
}

/**
 * 设置每一行点击后加入对应的群聊
 */
function handleGroupClick(index) {
  /**
   * 用elementplus询问是否加入该群聊*/
  ElMessageBox.confirm('是否加入群聊   ' + groups.value[index].groupName + '   ？', '提示', {
    confirmButtonText: '加入',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    // 确认加入群聊
    ElMessage.warning("正在加入中……");
    groupJoinHandler(groups.value[index]);
  }).catch(() => {
  });
}


function init() {
  emit('setWH', {});
}


</script>


<style scoped>

.group-mem-inf-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  /*padding: 10px;*/
  box-sizing: border-box;
}

.search {
  flex: 1;

  display: flex;
  flex-direction: row;

  height: 100%;
  width: 100%;

  border:none;
}

.search-input{
  flex:5;
  height: 100%;
  width: 100%;

  border:none;
}

.search-button{
  flex:1;
  height: 100%;
  width: 100%;

  border:none;
}

.group-mems {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  overflow-y: auto;
  flex: 15;
}

.mem-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  min-height: 10%; /* 每行高度固定，基于父容器的高度 */
  margin-bottom: 2px;
  padding: 0 3px;
  box-sizing: border-box;
  border-bottom: 1px solid #ccc;

  border-radius: 5px;
}

/**
设置 .group-row 鼠标浮动效果，背景颜色变黑并且浮起效果*/
.mem-row:hover {
  background-color: #f0f0f0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.mem-avatar {
  flex: 2;
  display: flex;
  padding: 2px;
  justify-content: left;
}

.mem-avatar img {
  border-radius: 5px;

  --size: 40px;
  width: var(--size);
  height: var(--size);

  --maxsize: 80%;
  max-width: var(--maxsize);
  max-height: var(--maxsize);

  object-fit: cover;
}

.mem-inf {
  flex: 7;
  display: flex;
  flex-direction: column;
  justify-content: left;
  overflow: hidden;
  text-overflow: ellipsis;


}

.mem-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 1em;
  width: 100%;

  font-family: "Segoe UI Bold", "Helvetica Neue Bold", sans-serif;
  font-weight: 666;
  letter-spacing: 1px;

  /*
    text-shadow: 1px 0px 0 rgb(3, 3, 42); !* 添加阴影效果 *!
    */
}

.group-members {
  flex: 1;
  font-size: 1em;
  text-align: right;
}
</style>
