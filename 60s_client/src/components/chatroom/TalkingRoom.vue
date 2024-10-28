<template>
  <div class="talking-room">
    <!-- 从 chatCards 数组遍历 chatCard 组件 -->
    <ChatCard
        v-if="cardsShow"
        v-for="(chatCard, index) in chatCards"
        :key="index"
        :name="chatCard.name"
        @sendMessageHandle="sendMessageHandle"
    />
    <ChatCardOnlyOne
        v-if="aCardShow"
        v-for="(chatCard, index) in chatCards"
        :key="index"
        :name="chatCard.name"
        @sendMessageHandle="sendMessageHandle"
    />
  </div>

  <div class="background-img-container">
    <img class="background-img">
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted, provide} from 'vue';
import {sharedData} from '@/global/globalData.js';
import {sharedCookie} from '@/global/globalCookie.js';
import {sharedFunction} from "@/global/globalFunction.js";
import {ElMessage, ElMessageBox} from "element-plus";
import router from "@/router/index.js";

/**
 * 定义一个 chatCards 数组
 */
const chatCards = ref([]);

let cardsShow = ref(true);
let aCardShow = ref(false);

const wsUrl = `ws${sharedData.serverIP}/chat?Authorization=${sharedCookie.get('loginAuthorization')}`;
const isConnected = ref(false);

let ws;
let messages = ref([]);

/**
 * 共享提供数据 groupMessages
 */
provide('groupMessages', messages);

let self_exit = false;

const initializeWebSocket = () => {
  ws = new WebSocket(wsUrl);

  // WebSocket 事件处理
  ws.onopen = () => {

    console.log('已经建立 WebSocket 连接');
    isConnected.value = true;
    if (sharedCookie.get("old message") == 'true') {
      ws.send("I need old message");
    }
  };

  ws.onmessage = event => {
    if (event.data === 'fast') {
      ElMessage.warning("太快了，受不了");
      return;
    } else if (event.data === 'gag') {
      ElMessage.error("你已被禁言");
      return;
    }
    /**
     * 如果 event.data 含有 script ，替换成 【script】*/



    const message = JSON.parse(event.data);


    if (message.message.includes("script")) {
      let newMes = '【危险】';
      /**
       * 遍历每一个字符，并让每一个字符都用|隔开*/
      for (let char of message.message) {
        newMes += char;
        newMes += ' ';
      }
      newMes += '【警告】';
      message.message = newMes;
    }

    messages.value.push(message);
  };

  let value_time = 0;
  ws.onclose = () => {
    console.log("断开连接时间：", new Date());

    if (self_exit) {
      return;
    }
    // 输出当前时间

    isConnected.value = false;
    if (value_time++ % 3 == 0) {
      ElMessageBox.alert('连接已断开，是否重新尝试', '提示', {
        type: 'warning',
        confirmButtonText: '尝试',
      }).then(() => {
        initializeWebSocket();
        router.push('/talkingRoom');
      })
    } else {
      initializeWebSocket();
      router.push('/talkingRoom');
      console.log("自动连接");
    }

  };

  ws.onerror = () => {
    isConnected.value = false;
    ElMessageBox.alert('获取信息时发生了错误，是否重新尝试', '提示', {
      type: 'warning',
      confirmButtonText: '尝试',
      onConfirm: () => {
        initializeWebSocket();
      }
    })
  };
};

onUnmounted(
    () => {
      self_exit = true;
    }
)

/**
 * 利用 WebSocket 发送消息
 */
function sendMessageHandle(content) {
  ws.send(content);
}


// 更新消息的时间剩余
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
const timer = setInterval(updateMessages, 500);

// 清理定时器
onUnmounted(() => {
  clearInterval(timer);
});

// 初始化 WebSocket
onMounted(() => {
  initializeWebSocket();

  let cardNum = 3;
  if (sharedCookie.get("cardNum")) {
    cardNum = sharedCookie.get("cardNum");
  }

  if (sharedFunction.isPhone()) {
    cardNum = 1;
  }

  if (cardNum > 1) {
    // 初始化 chatCards 数组
    for (let i = 0; i < cardNum; i++) {
      const item = {name: `chat${i}`}; // 使用反引号 ` ` 进行字符串插值
      chatCards.value.push(item);
    }
  }
  if (cardNum == 1) {
    aCardShow = true;
    cardsShow = false;
    const item = {name: `chat`}; // 使用反引号 ` ` 进行字符串插值
    chatCards.value.push(item)
  }


  // 输出 chatCards 的内容和长度，用于调试
  // console.log('chatCards:', chatCards.value);
  // console.log('chatCards length:', chatCards.value.length);
});

// 组件卸载时关闭 WebSocket 连接
onUnmounted(() => {
  if (ws && ws.readyState !== WebSocket.CLOSED) {
    ws.close();
  }
});
</script>


<style scoped>
/**
 * 设置背景为酷炫的蓝黑色风格
 */

.talking-room {
  width: 100%;
  height: 100%;
  /* background: rgba(245, 246, 247, 0.91);*/

  /**
   * flex 并且让子元素横向垂直居中
   */
  display: flex;
  /**
   * 排列方式 横向
   */
  flex-direction: row;
  justify-content: center;
  align-items: center;

  opacity: 0.8;
}

.background-img-container {
  /**
  绝对定位，占满整个屏幕，并且让内部的图片宽度占满*/
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: -100;
  display: flex;
  background-repeat: no-repeat;
}

.background-img {
  background-image: url("https://picx.zhimg.com/v2-8179aa3bf68028db67ba9e8bdfeb709b_r.jpg?source=1def8aca");
  min-width: 100%;
  max-width: 100%;
  height: auto;
  background-size: cover; /* 或者使用 'contain' */
  background-position: center;
}

/** 手机端设置 */
@media screen and (max-width: 768px) {
  .talking-room {
    flex-direction: column;
    margin: 0 0px 0 0px;
  }
}
</style>
