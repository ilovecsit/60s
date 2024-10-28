<template>
  <div class="group">
    <div class="groups">
      <div
          v-for="(group, index) in groups"
          :key="index"
          class="group-row"
          @click="handleGroupClick(index)"
          :class="{'system':group.groupKind==0,'common':group.groupKind==1,'secret':group.groupKind==2,'myGroup':group.groupCreatedId==userId}"
      >
        <div class="group-avatar">
          <img :src="group.groupAvatar"/>
        </div>
        <div class="group-inf">
          <div class="group-name"
          >

            <span v-if="group.groupCreatedId==userId?true:false">【我的】</span>
            <span v-if="group.groupKind==2?true:false">【私密】</span>
            <span v-if="group.groupKind==1?true:false">【公开】</span>
            <span v-if="group.groupKind==0?true:false">【系统】</span>
            <span>{{ group.groupName }}</span></div>
          <div class="group-mes-num">
            {{ group.groupMesNumNow}}条
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref, defineEmits, inject, onUnmounted} from 'vue';
import ElementPlus, {ElMessage, ElMessageBox} from "element-plus";
import {sharedCookie} from "@/global/globalCookie.js";

let mes_num = 0;



function getGroupMesNum(groupId) {
  if (sharedCookie.getJson("groups_mes")) {
    if (sharedCookie.getJson("groups_mes")[groupId]) {
      return sharedCookie.getJson("groups_mes")[groupId].value.num
    }
  }
  return 0;
}

/**
 * 定时任务，每隔一秒更新mes_num的值
 * 组件消失时清除任务*/

function updateGroupMes(groupId) {

}

const timer = setInterval(() => {
  updateGroupMes()
}, 1000);

onUnmounted(() => {
  clearInterval(timer);
});

const userId = sharedCookie.getJson("loginUser").userId;

const groups = ref({});

// 使用 defineEmits 声明可触发的事件
const emit = defineEmits(['setWH', 'groupInto']);

onMounted(() => {
  init();
  groups.value = inject('chatCardSharedData').groupsInf.value;
});


/**
 * 设置每一行点击后加入对应的群聊
 */
function handleGroupClick(index) {
  function readAnnouncement() {
    const groupAnnouncement = groups.value[index].groupAnnouncement.toString();
    const groupIdAnnouncementKey = (groups.value[index].groupId + "key").toString();
    if (!(sharedCookie.getJson(groupIdAnnouncementKey) === groupAnnouncement)) {
      ElMessageBox.confirm('群公告', '提示', {
        confirmButtonText: '查看',
        cancelButtonText: '跳过',
        type: 'warning'
      }).then(() => {
        ElMessageBox.alert(groupAnnouncement, groups.value[index].groupName + '-群公告', {
          confirmButtonText: '已阅',
        }).then(() => {
          sharedCookie.setJson(groupIdAnnouncementKey, groupAnnouncement)
          sharedCookie.showAllCookie()
        })
      }).catch(() => {
      });
    }
  }

  /**
   * 用elementplus询问是否加入该群聊*/
  ElMessageBox.confirm('是否进入群聊   ' + groups.value[index].groupName + '   ？', '提示', {
    confirmButtonText: '进入',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    readAnnouncement();
    // 确认进入群聊
    emit('groupInto', groups.value[index]);
  }).catch(() => {
  });
}


function init() {
  emit('setWH', {});
}


</script>


<style scoped>

.group {
  width: 100%;
  display: flex;
  flex-direction: column;
  /*padding: 10px;*/
  box-sizing: border-box;
}

.groups {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  overflow-y: auto;
}

.group-row {
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
.group-row:hover {
  /*  background-color: #f0f0f0;*/
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.group-avatar {
  flex: 2;
  display: flex;
  padding: 2px;
  justify-content: left;
}

.group-avatar img {
  border-radius: 5px;

  --size: 40px;
  width: var(--size);
  height: var(--size);

  --maxsize: 80%;
  max-width: var(--maxsize);
  max-height: var(--maxsize);

  object-fit: cover;
}

.group-inf {
  flex: 7;
  display: flex;
  flex-direction: row;
  justify-content: center;
  overflow: hidden;
  text-overflow: ellipsis;

}

.group-name {
  flex: 5;
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

.group-mes-num {
  flex: 1;
}

.system {
  background: #ff4d51;
  color: whitesmoke;
}

.secret {
  background: mediumpurple;
  color: ghostwhite;
}

.common {
  background: #9db4c0;
  color: #2c3e50;
}

.myGroup {
  background: #6cf341;
  color: #23237c;
}
</style>
