<template>
  <div class="user-info">
    <!-- 用户信息展示 -->
    <div class="row-info" style="height: 32%;">
      <span id="row-name">头像</span>
      <div id="row-content" class="photo-content">
        <img :src="userInf.userPhotoUrl" alt="User Photo" class="user-photo" />
      </div>
    </div>
    <div class="row-info">
      <span id="row-name">名称</span>
      <div id="row-content">{{ userInf.userNickName }}</div>
    </div>
    <div class="row-info">
      <span id="row-name">账号</span>
      <div id="row-content">{{ userInf.userId }}</div>
    </div>
    <div class="row-info">
      <span id="row-name">邮箱</span>
      <div id="row-content">{{ userInf.userEmail }}</div>
    </div>

    <!-- 修改信息按钮 -->
    <el-button class="edit-button" @click="toggleEditModal(true)">修改信息</el-button>

    <!-- 自定义模态框 -->
    <div v-if="editModalVisible" class="edit-modal">
      <div class="modal-content">
        <el-form class="edit-form">
          <el-form-item label="用户名称">
            <el-input v-model="editedUserNickName"></el-input>
          </el-form-item>
          <el-form-item class="form-url" label="用户头像URL">
            <textarea class="url-textarea" v-model="editedUserPhotoUrl"></textarea>
          </el-form-item>

        </el-form>
        <div class="option-choice">
          <el-button @click="toggleEditModal(false)">退出修改</el-button>
          <el-button type="primary" @click="confirmEdit">确认修改</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, reactive, onMounted } from 'vue';
import { ElButton, ElForm, ElFormItem, ElInput, ElMessage } from 'element-plus';
import axios from 'axios';
import { sharedCookie } from '@/global/globalCookie.js';
import { sharedData } from '@/global/globalData.js';
import router from '@/router/index.js';

const userInf = ref({
  userPhotoUrl: null,
  userId: null,
  userNickName: null,
  userEmail: null
});

const editModalVisible = ref(false);
const editedUserNickName = ref('');
const editedUserPhotoUrl = ref('');

function setPhotoContent() {
  document.querySelectorAll('.photo-content').forEach(element => {
    const height = element.offsetHeight;
    element.style.width = height + 'px'; // 设置宽度等于高度
  });
}

onMounted(() => {
  const userInfo = sharedCookie.getJson("loginUser");
  if (userInfo) {
    userInf.value = userInfo;
    editedUserNickName.value = userInfo.userNickName;
    editedUserPhotoUrl.value = userInfo.userPhotoUrl;
  }
  setPhotoContent()
});

const toggleEditModal = (isVisible) => {
  editModalVisible.value = isVisible;
};

const confirmEdit = () => {
  console.log("发送请求");
  const newUserInf = {
    userId: userInf.value.userId,
    userNickName: editedUserNickName.value,
    userPhotoUrl: editedUserPhotoUrl.value
  };

  axios.put(`http${sharedData.serverIP}/user/userModify`, newUserInf, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: sharedCookie.get("loginAuthorization")
    }
  })
      .then(response => {
        const { code } = response.data;
        if (code === 200) {
          ElMessage.success("修改信息成功");
          userInf.value.userNickName = editedUserNickName.value;
          userInf.value.userPhotoUrl = editedUserPhotoUrl.value;
          sharedCookie.setJson("loginUser", userInf.value);
          location.reload();
        } else if (code === 400) {
          ElMessage.error("请输入合法的名字");
        } else {
          ElMessage.error("修改信息失败");
        }
      })
      .catch(error => {
        ElMessage.error("修改信息失败");
        const errCode = error.response.status;
        if (errCode === 401) {
          ElMessage.warning("出现异常，需要重新登录");
          sharedCookie.clearAll();
          router.push("/Login");
        }
      })
      .finally(() => {
        toggleEditModal(false);
      });
};
</script>

<style scoped>
.user-info {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.row-info {
  display: flex;
  justify-content: space-between;
  align-self: center;
  width: 100%;
  height: 8%;
  border-bottom: 1px black solid;
  padding-left: 10px;
}

#row-name{
  display: flex;
  justify-content: space-between;
  align-self: center;
}
#row-content {
  display: flex;
  align-self: center;
  justify-content: right;
  flex: 2;
  padding-right: 10px;
}

.photo-content {
  height: 95%; /* 设置高度为父级元素的80% */
  width: auto; /* 设置宽度为父级元素的80% */
  max-width: 40%  ; /* 可以设置一个最大宽度限制 */
  max-height: 200px; /* 可以设置一个最大高度限制 */
  overflow: hidden; /* 隐藏超出容器的部分 */
  position: relative; /* 定位子元素 */
  margin-right: 10px;
}

.user-photo {
  width: 100%; /* 使图片填满容器宽度 */
  height: 100%; /* 使图片填满容器高度 */
  object-fit: cover; /* 裁剪图片以填充容器 */
  position: absolute; /* 绝对定位 */

  top: 50%; /* 从顶部开始定位 */
  left: 50%; /* 从左侧开始定位 */

  transform: translate(-50%, -50%); /* 居中图片 */
}

.edit-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #409eff;
  border: none;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.edit-button:hover {
  background-color: #66b1ff;
}

.edit-modal {
  position: absolute;
  top: 10%;
  left: 0;
  width: 100%;
  height: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 5px;
  width: 80%;
  height: 100%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.edit-form .el-form-item {
  margin-bottom: 20px;
}
.edit-form  .form-url {
  height: 100px;
}
.edit-form .form-url .url-textarea{
  height: 100%;
}

.option-choice{
  width: 100%;
  display: flex;
  align-self: center;
  justify-content: center;
}
</style>

