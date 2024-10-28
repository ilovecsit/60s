<template>
  <div class="chat-messages-container">
    <div class="chat-messages" :class="{[groupId]:true}">
      <!-- 使用 v-for 渲染消息列表 -->
      <div
          v-for="(message, index) in sortedMessages"
          :key="index"
          class="chat-message"
      >

        <div class="message-container" v-if="message.timeRemaining>0"
             :class="{'sender-self': message.messageSender.userId == thisUserId }">
          <div class="avatar">
            <img :src="message.messageSender.userPhotoUrl"/>
          </div>

          <!-- 消息框 -->
          <div class="message-box">
            <div class="sender">
              <div v-if="message.messageSender.userId==groupCreatedId?true:false"
                   style="display: inline-block;background: #ff4d51;padding: 2px;">
                <span style="color:whitesmoke">群主</span>
              </div>
              {{ message.messageSender.userNickName }}
            </div>
            <div class="content" v-if="message.messageType===0" v-html="message.message"></div>
            <div class="content img-mes-container" v-if="message.messageType===1">
              <img :src="message.message">
            </div>
          </div>

          <!-- 倒计时红色提示框 -->
          <div class="timer">
            {{ message.timeRemaining }}
          </div>
        </div>
      </div>

<!--      -->
      <div v-show="send_pic" class="image-gallery">
        <div class="image-boxes" :style="{ maxHeight: maxHeight + 'px' }">
          <div
              class="image-box"
              v-for="(image, index) in images"
              :key="index"
              :style="getImageBoxStyle(index)"
          >
            <!--图片懒加载-->
            <!--添加右键点击事件，点击后修改图片url        -->
            <img v-lazy="image.url" class="image" :id="image.id"  @click="choose_send_or_update(image)"/>

          </div>


          <div
              class="image-box"
              :style="getImageBoxStyleUnique()"
          >
              <img @click="add_new_pictures" src="https://tse4-mm.cn.bing.net/th/id/OIP-C.eQSIwvH1i2NUMmOOSfggwQAAAA?w=177&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7" class="image"/>
          </div>


          <div
              class="image-box"
              :style="getImageBoxStyleUnique()"
          >
            <img @click="send_pic=false;" src="https://tse1-mm.cn.bing.net/th/id/OIP-C.J8z8xmlqu7CsxT9-r26JWQHaHa?w=178&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7" class="image"/>
          </div>

        </div>
      </div>

    </div>

    <el-button class="message-send" type="primary" size="small" @click="sendMessage"
               v-show="showInputButton"
    >发送
    </el-button>

    <el-button class="message-send-img" @click="sendPicMes" style="width: 100%;margin-left: 0;" v-show="!showInputButton" type="info">发送图片</el-button>
    <div class="inputBox"
         @focus="inputMessage"
         style="flex:2; display: flex; flex-direction: column; align-content: center;justify-content: center;">
      <!-- 消息输入框和发送按钮 -->
      <el-input
          type="textarea"
          v-model="newMessageContent"
          placeholder="请输入消息，按 回车键 可以发送"
          class="message-input"
          @focus="inputMessage"
          @keydown.enter="sendMessage"
      >
      </el-input>
    </div>

  </div>



</template>

<script setup>
import {ref, onMounted, watchEffect, nextTick, onUnmounted, computed, inject, watch} from 'vue';
import {sharedCookie} from '@/global/globalCookie.js';
import {ElMessage, ElMessageBox} from "element-plus";


const thisUserId = sharedCookie.get('userId');


const allMessages = inject('groupMessages');
const groupThis = inject('groupNow');
const groupId = ref('group-100');
let groupCreatedId = ref(10000)


onMounted(() => {
      groupId.value = 'group' + groupThis.value.groupId;
      groupCreatedId = groupThis.value.groupCreatedId;
    }
)


const currentUser = ref({
  userId: sharedCookie.get('userId')
});

let showInputButton = ref(false);

function inputMessage() {
  showInputButton.value = true;
}


const messages = ref([])

// 更新消息的时间剩余
const updateMessages = () => {
  const currentTime = Date.now();

  messages.value.forEach(message => {

    const sendTime = message.messageSendTime;

    const timeDiffInSeconds = Math.floor((currentTime - sendTime) / 1000);

    message.timeRemaining = 60 - timeDiffInSeconds;

    if (message.timeRemaining <= 0) {
      messages.value.splice(messages.value.indexOf(message), 1);
    }
  });
};

// 开始定时器
const timer = setInterval(updateMessages, 1000);


// 清理定时器
onUnmounted(() => {
  clearInterval(timer);
});


// 监听消息列表的变化
watchEffect(async () => {
  /**
   * message清空并获取值*/
  messages.value = [];
  allMessages.value.forEach((message) => {
    if (message.groupId === groupThis.value.groupId) {
      messages.value.push(message);
    }
  })
  await nextTick(
      () => {
        /**
         * 将消息框拉上去*/
        document.querySelector('.chat-messages').scrollTop = document.querySelector('.chat-messages').scrollHeight;

      }
  ); // 等待 Vue 更新 DOM


});

// 排序消息
const sortedMessages = computed(() => {
  return [...messages.value].sort((a, b) => {
    return a.messageSendTime - b.messageSendTime;
  });
});

// 新消息内容
const newMessageContent = ref('');

// 监视 newMessageContent 的值变化
watch(newMessageContent, (newValue) => {
  if (newValue.trim() !== '') {
    showInputButton.value = true;
  } else {
    showInputButton.value = false;
  }
}, {immediate: true});

// 发送消息
const sendMessage = () => {
  if (newMessageContent.value.length >= 255) {
    ElMessage.warning("消息太长，请重新编辑");
    return;
  }
  if (newMessageContent.value.trim() === '') return;
  const jsonMessage = {};
  const messageSender = {};
  messageSender.userId = currentUser.value.userId;
  jsonMessage.messageSender = messageSender;
  jsonMessage.message = newMessageContent.value;
  jsonMessage.messageType = 0;
  jsonMessage.groupId = parseInt(groupThis.value.groupId);

  emit('sendMessageHandle', JSON.stringify(jsonMessage));
  newMessageContent.value = ''; // 清空输入框
};

function sendImgMessage(image){
  if (image.url >= 255) {
    ElMessage.warning("请换一张图片");
    return;
  }
  const jsonMessage = {};
  const messageSender = {};
  messageSender.userId = currentUser.value.userId;
  jsonMessage.messageSender = messageSender;
  jsonMessage.message = image.url;
  jsonMessage.messageType = 1;
  jsonMessage.groupId = parseInt(groupThis.value.groupId);

  emit('sendMessageHandle', JSON.stringify(jsonMessage));
}


// 使用 defineEmits 声明可触发的事件
const emit = defineEmits(['sendMessageHandle']);

let send_pic=ref(false);

function sendPicMes(){
  send_pic.value=true;
}

function choose_send_or_update(image){
  ElMessageBox.confirm('发送图片或者修改图片', '选择', {
    confirmButtonText: '发送图片',
    cancelButtonText: '修改图片',
    type: 'warning',
  }).then(() => {
    /**
     * 如果点击了发送按钮
     * */
    sendImgMessage(image);
  }).catch(() => {
    /**
     * 如果点击了修改按钮*/
    update_photo_url(image);
  });
}

const images = ref([]);

function update_photo_url(image){
  ElMessage.warning("你正在修改图片");
  ElMessageBox.prompt('请输入新图片的地址', '提示', {
    inputValue: '',
    inputPlaceholder: '请输入图片地址',
    inputErrorMessage: '请输入正确的图片地址',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(({value}) => {
    if (value.trim() === '') {
      ElMessageBox.alert('请输入图片地址', '提示', {
        type: 'warning',
      });
      return;
    }

    const picture_inf = {
      'id': image.id,
      'url': value
    };


    sharedCookie.setJson("pic_" + image.id, picture_inf);

    images.value.forEach((p) => {
      if(p.id === image.id) {
        p.url = value;
      }
    });
    const total_img=document.getElementById(image.id);

    document.getElementById(image.id).src=value;
  }).catch(() => {
    // 取消操作
  });
}

function add_new_pictures() {
  ElMessageBox.prompt('请输入图片地址', '提示', {
    inputValue: '',
    inputPlaceholder: '请输入图片地址',
    inputErrorMessage: '请输入正确的图片地址',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(({value}) => {
    if (value.trim() === '') {
      ElMessageBox.alert('请输入图片地址', '提示', {
        type: 'warning',
      });
      return;
    }

    picture_num = Number(picture_num) + 1;

    const picture_inf = {
      'id': picture_num,
      'url': value
    };
    sharedCookie.setJson("pic_" + picture_num, picture_inf);
    sharedCookie.setInt('picture_num', picture_num);


    images.value.push(picture_inf);

    // console.log("图片数量" + picture_num)
    // console.log(images)
  }).catch(() => {
    // 取消操作
  });

}

// 计算每个图片框的样式
const getImageBoxStyle = (index) => {
  const containerWidth=window.innerWidth*0.95*(7/30);
  const minBoxWidth = containerWidth * 0.18;
  const boxWidth = minBoxWidth;
  return {
    width: `${boxWidth}px`,
    height: `${boxWidth}px`
  };
};

// 计算每个图片框的样式
const getImageBoxStyleUnique = () => {
  const containerWidth=window.innerWidth*0.95*(7/30);
  const minBoxWidth = containerWidth * 0.18;
  const boxWidth = minBoxWidth;
  return {
    width: `${boxWidth}px`,
    height: `${boxWidth}px`
  };
};

// 计算最大高度
const maxHeight = computed(() => {
  const containerWidth = window.innerWidth || document.documentElement.clientWidth;
  const rows = Math.ceil(images.value.length / (containerWidth / (containerWidth * 0.3))) || 1;
  const boxWidth = containerWidth * 0.3;
  return rows * (boxWidth / 16 * 9);
});

let picture_num = 0
onMounted(() => {
  picture_num = sharedCookie.getInt('picture_num');


  for (let i = 1; i <= Number(picture_num); i++) {
    images.value.push(sharedCookie.getJson("pic_" + i));
  }


})


</script>


<style scoped>

.chat-messages-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  /**禁止顶起*/
  overflow: hidden;
}

.message-input {
  /*  position: absolute;*/

  flex: 1;

  opacity: 0.7;

  background: #9db4c0;

  /*width: 100%;
  max-width: 100%;

  max-height: 100%;
  height: 100%;*/
}


/*.message-send {
  position: absolute;

  opacity: 0.7;

  height: 100%;

  width: 100%;
  max-width: 100%;

  max-height: 10%;
  height: 10%;

  display: none;
}*/


.chat-messages {
  flex: 8;

  width: 100%;

  /*  height: 80%;
    max-height: 80%;*/

  overflow: auto;
  padding: 10px;
  background: #9db4c0;

}

.message-send {
  width: 100%;
}

.chat-message {
  /*display: flex;

  align-items: self-start;
  justify-content: left;
  min-width: 100%;

  !*max-height: 50%;
  overflow: auto;*!

  margin-bottom: 10px;*/

  min-width: 100%;
  max-width: 100%;
  overflow: auto;

}


.message-container {
  display: flex;

  flex-direction: row;

  align-items: self-start;
  justify-content: left;
  min-width: 100%;

  /*max-height: 50%;
  overflow: auto;*/

  margin-bottom: 10px;
}

.avatar {
  margin-top: 2px;

  /*  border-radius: 5px;*/

  --size: 50px;
  width: var(--size);
  height: var(--size);

  --maxsize: 18%;
  max-width: var(--maxsize);
  max-height: var(--maxsize);

  display: flex;
  align-items: center;
  justify-content: center;

  position: relative;

  margin-right: 15px;
}

.avatar img {
  border-radius: 5px 5px 5px 5px;

  width: 100%;
  height: auto;
  max-height: 100%;

  /**
  让图像填满容器，*/
  object-fit: cover;
}

.message-box {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  max-width: 70%;
  padding: 5px;
  box-sizing: border-box;

  margin-left: -10px;
}

.sender {
  font-size: 14px;
  line-height: 1em;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #27273d;
  white-space: nowrap;

  /**设置文本长度为文字长度*/

  height: 3%; /* 用户名高度 */
  padding: 5px;


  box-sizing: border-box;
}

.content {
  color: black;
  padding: 10px;
  font-size: medium;
  background: #f8f8f8;
  word-break: break-word;
  flex-grow: 1;

  border-radius: 6px;
}

.timer {
  --size: 30px;
  width: var(--size);
  height: var(--size);

  --maxsize: 20vw;
  max-width: var(--maxsize);
  max-height: var(--maxsize);

  background-color: #e55252;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%; /* 圆形 */
  font-size: 16px;

  margin-top: 2%;
}

.sender-self {
  display: flex;

  align-items: self-start;
  justify-content: right;
  min-width: 100%;


  margin-bottom: 10px;
}

/* 区分发送者与接收者 */
.sender-self .avatar {
  order: 2;
}

.sender-self .message-box {
  margin-right: 1%;
  margin-top: 2%;
  order: 1;
}

.img-mes-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.img-mes-container img {
  /**
  设置图片最大占满父元素宽度的90%，让图片是正方形*/
  max-width: 90%;
  height: auto;
  cover: contain;
}

.sender-self .message-box .sender {
  /**设置不显示元素*/
  display: none;
}

.sender-self .message-box .content {
  background: #409eff;

}

.sender-self .timer {
  margin-top: 2%;
  margin-right: 3%;
  order: 0;
}


.image-gallery {
  width: 90%;
  height: 40%;

  opacity: 0.65;

  overflow-y: auto;
}

.image-boxes {
  display: flex;
  flex-wrap: wrap;
  gap: 20px; /* 图片框之间的间距 */
  align-content: space-between;
  align-self: center;
  justify-content: space-between;

  padding: 10px;
}

.image-box {
  overflow: hidden; /* 裁剪超出部分 */
  position: relative; /* 使 img 可以绝对定位 */
  display: flex;
  justify-content: center;
  align-items: center;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 图片铺满并裁剪 */
}

.add_flag_container {
  /*width: 100%;
  height: 100%;*/
}
</style>


