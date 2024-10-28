<template>
  <div class="group-mem-inf-page">
    <div>
      <el-button type="info" style="width: 100%" @click="returnTalkingRoom">
        返回
      </el-button>
    </div>
    <div class="group-mems">
      <div
          v-for="(mem, index) in mems"
          :key="index"
          class="mem-row"
      >
        <div class="mem-avatar">
          <img :src="mem.userPhotoUrl"/>
        </div>
        <div class="mem-inf">
          <div class="mem-name">{{ mem.userNickName }}</div>
        </div>
        <div class="gag" v-if="mem.userKind==2?true:false">
          <el-button type="danger" @click="gagUser(mem.groupId,mem.groupMemId)">
            禁言
          </el-button>
        </div>
        <div class="gag-rev" v-if="mem.userKind==3?true:false">
          <el-button type="primary" @click="gagUserRev(mem.groupId,mem.groupMemId)">
            解禁
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref, defineEmits, inject} from 'vue';
import axios from "axios";
import {sharedData} from "@/global/globalData.js";
import {sharedCookie} from "@/global/globalCookie.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";


const mems = ref({});
let groupThis = inject('groupNow');


// 使用 defineEmits 声明可触发的事件
const emit = defineEmits(['setWH','groupInto']);

function gagUserRev(groupId, groupMemId) {
  // 定义请求体数据
  const data = {
    groupId: groupId,
    userId: groupMemId
  };

// 定义请求头，包括自定义的 Authorization 头
  const config = {
    headers: {
      'Authorization': sharedCookie.get("loginAuthorization")
    }
  };
  axios.put('http' + sharedData.serverIP + '/group/removeFromGagList', data, config)
      .then(function (response) {
        if (response.data.code == 200) {
          ElMessage.success("解禁成功");
          /**
           * 遍历mems，让 groupMemId 为参数值的 userKind=2*/
          mems.value.forEach(function (mem) {
            if (mem.groupMemId == groupMemId) {
              mem.userKind = 2;
              return;
            }
          });
        }else{
          ElMessage.error("解禁失败");
        }
      })
      .catch(function (error) {
        ElMessage.error("解禁失败");
        const errCode = error.response.status;
        if (errCode === 401) {
          ElMessage.warning("出现异常，需要重新登录");
          sharedCookie.clearAll();
          router.push("/Login");
        }
      });
}

function returnTalkingRoom(){
  emit('groupInto', groupThis.value);
}

function gagUser(groupId, groupMemId) {
  // 定义请求体数据
  const data = {
    groupId: groupId,
    userId: groupMemId
  };

// 定义请求头，包括自定义的 Authorization 头
  const config = {
    headers: {
      'Authorization': sharedCookie.get("loginAuthorization")
    }
  };
  axios.put('http' + sharedData.serverIP + '/group/addToGagList', data, config)
      .then(function (response) {
        if (response.data.code == 200) {
          ElMessage.success("禁言成功");
          /**
           * 遍历mems，让 groupMemId 为参数值的 userKind=3*/
          mems.value.forEach(function (mem) {
            if (mem.groupMemId == groupMemId) {
              mem.userKind = 3;
              return;
            }
          });
        }else{
          ElMessage.error("禁言失败");
        }
      })
      .catch(function (error) {
        ElMessage.error("禁言失败");
        const errCode = error.response.status;
        if (errCode === 401) {
          ElMessage.warning("出现异常，需要重新登录");
          sharedCookie.clearAll();
          router.push("/Login");
        }
      });
}

function getMemInf() {
  groupThis = inject('groupNow');
  const groupIdNow = sharedCookie.get("selectGroupMemGroupId:" + groupThis.value.groupId);
  /*if(groupIdNow===null){
  /!**
   * 延时100ms后执行*!/
   setTimeout(getMemInf,1000);
   return;
  }*/
  axios.get('http' + sharedData.serverIP + '/group/selectGroupMemInf?groupId=' + groupIdNow, {
    headers: {
      'Authorization': sharedCookie.get("loginAuthorization")
    }
  })
      .then(function (response) {
        if (response.data.code == 200) {
          mems.value = response.data.data;
        } else {
          console.log("查询失败");
        }
      })
      .catch(function (error) {
        const errCode = error.response.status;
        if (errCode === 401) {
          ElMessage.warning("出现异常，需要重新登录");
          sharedCookie.clearAll();
          router.push("/Login");
        }
      });
}

onMounted(() => {
  init();
  getMemInf();
});


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

.group-mems {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  overflow-y: auto;
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
