<template>
  <div class="terminal">
    <div class="terminal-body">
      <div class="terminal-content">
        <div v-for="(line, index) in lines" :key="index" class="terminal-line">
          <span v-if="index%2==1" class="prompt">{{ userPrompt }}</span>
          <span class="command">{{ line }}</span>
        </div>
        <div class="input-line">
          <span class="prompt">{{ userPrompt }}</span>
          <input
              type="text"
              class="terminal-input"
              v-model="input"
              @keyup.enter="executeCommand"
              id="commandInput"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import {sharedCookie} from "@/global/globalCookie.js";
import axios from "axios";
import {sharedData} from "@/global/globalData.js";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {sharedFunction} from "@/global/globalFunction.js";

const lines = ref(['您正在使用60s命令,输入 help 以获取帮助']);
const input = ref('');
const userEmail = ref("60s@"+sharedCookie.getJson("loginUser").userId);

const tip = '系统提示:';
const userPrompt = `${userEmail.value}>`;

/**
 * 常见的命令*/
const optionList = ref([
  'create group kind groupName\t创建群聊 kind(群聊类型):(1,公开;2,私密） groupName(群聊名称)',
  'select my groupInf \t查询所有已创建的群聊信息',
  'update groupInf set groupName=?,groupKind=?,groupAvatar=? where groupId=?\t通过群聊id修改群聊信息,如不需修改则用old填充,如groupKind=old',
  'back\t返回聊天室页面',
  'exit\t退出登录',
  'random\t这似乎不是一个选择'
  // ... 其他命令
]);

const randomList=ref(
    [
      'old message\t希望能够找到错过的信息……',
      'delete groupId=?\t信息可以被删除，但过去不行……',
      'select joinCodes where groupId=?',
      'set cardNum=?\t也许现实不会总是如愿……',
      'set announcement=? where groupId=?\t也许只是徒劳……',
      'join secretGroupId=? use joinCode=?\t进行一场秘密派对……'
      // ... 其他命令
    ]
)

function executeCommand() {
  const command = input.value.trim();

  let createGroupKind;
  let createGroupName;

  function isCreateGroup(command) {
    // 定义正则表达式来匹配正确的创建群聊命令格式
    const createGroupRegex = /^create group (\d+) (.+)$/;

    // 使用正则表达式测试输入的命令
    const match = command.match(createGroupRegex);

    // console.log(match);

    // 如果匹配成功并且匹配到的类型是1或2，则返回true
    if (match && (match[1] === '1' || match[1] === '2')) {
      createGroupKind = match[1];
      createGroupName = match[2];
      return true;
    }

    // 如果不符合格式，则返回false
    return false;
  }

  let announcementNow;
  let announcementGroupId;
  function isAnnouncementSet(command) {
    const announcementSetRegex=/\bset\s+announcement\s*=\s*(.{1,100})\s+where\s+groupId\s*=\s*(\d{1,5})/;
    const match = command.match(announcementSetRegex);

    if(match){
      announcementNow=match[1];
      announcementGroupId=match[2];
      return true;
    }

    // 如果不符合格式，则返回false
    return false;
  }

  let updateGroupGroupName;
  let updateGroupGroupKind;
  let updateGroupGroupAvatar;
  let updateGroupGroupId;
  function isUpdateGroup(command){
    const updateGroupRegex=/\bupdate\s*groupInf\s*set\s*groupName=\s*(.{1,20}),\s*groupKind=(1|2),\s*groupAvatar=\s*(.{1,500})\s*where\s*groupId=(\d{1,5})/;
    const match=command.match(updateGroupRegex);
    if(match){
      updateGroupGroupName=match[1];
      updateGroupGroupKind=match[2];
      updateGroupGroupAvatar=match[3];
      updateGroupGroupId=match[4];
      return true;
    }

    return false;
  }

  let deleteGroupId;
  function isDeleteGroup(command){
    const deleteGroupRegex=/\b\s*delete\s*groupId=(\d{1,5})/;
    const match=command.match(deleteGroupRegex);
    if(match){
      deleteGroupId=match[1];
      return true;
    }

    return false;
  }

  let joinCodesGroupId;
  function isSelectJoinCode(command){
    const selectJoinCode=/\b\s*select\s*joinCodes\s*where\s*groupId=(\d{1,5})/;
    const match=command.match(selectJoinCode);
    if(match){
      joinCodesGroupId=match[1];
      return true;
    }
    return false;
  }

  let secretGroupId;
  let secretGroupJoinCode;
  function isSecretGroupJoin(command){
    const secretGroupJoin=/\b\s*join\s*secretGroupId=(\d{1,5})\s*use\s*joinCode=\s*(.{1,20})/;
    const match=command.match(secretGroupJoin);
    if(match){
      secretGroupId=match[1];
      secretGroupJoinCode=match[2];
      return true;
    }
    return false;
  }

  let cardNum;
  function isCardNumSet(command){
    const cardNumSet=/\bset\s+cardNum\s*=(\d{1,5})/;
    const match=command.match(cardNumSet);
    if(match){
      cardNum=match[1];
      return true;
    }
    return false;
  }
  function cardNumSet(){

    if(sharedFunction.isPhone()){
      lines.value.push("无法设置");
      return;
    }

    lines.value.push("设置成功")

    sharedCookie.set("cardNum",cardNum);
  }

  function secretGroupJoin(){
    // 定义请求体数据
    const data = {
      groupId: secretGroupId,
      joinCode: secretGroupJoinCode
    };

// 定义请求头，包括自定义的 Authorization 头
    const config = {
      headers: {
        'Authorization': sharedCookie.get("loginAuthorization") // 替换为你的授权令牌
      }
    };

// 发送 POST 请求
    axios.put('http' + sharedData.serverIP + '/group/secretGroupJoin?groupId='+secretGroupId+"&joinCode="+secretGroupJoinCode, data, config)
        .then(function (response) {
          if (response.data.code == 200) {
            lines.value.push('加入群聊成功');
          }
          else {
            lines.value.push('加入群聊失败 ' + response.data.message);
          }
        })
        .catch(function (error) {
          lines.value.push('请求失败');

          const errCode=error.response.status;
          if(errCode===401){
            ElMessage.warning("出现异常，需要重新登录");
            sharedCookie.clearAll();
            router.push("/Login");
          }
        });
  }

  function selectJoinCode(){
    axios.get('http' + sharedData.serverIP + '/group/getJoinCode?groupId='+joinCodesGroupId, {
      headers: {
        'Authorization': sharedCookie.get("loginAuthorization")
      }
    })
        .then(function (response) {
          lines.value.push();
          let resSelect='';
          let count=0;
          if (response.data.code == 200) {
            // console.log(response.data.data)
            resSelect="";
            response.data.data.split('|').forEach(function (item) {
              if(count%10==0){
                resSelect+="\n";
              }
              resSelect+=item+" ";
              count=count+1;
            });
            lines.value.push("查询成功,还有"+count+"个加入码（注意，如果群聊人数已满，加入码依然无法生效，且每个加入码只能生效一次）"+
                resSelect);
          } else {
            lines.value.push('查询失败 ' + response.data.message);
          }
        })
        .catch(function (error) {
          lines.value.push('请求失败');
          const errCode=error.response.status;
          if(errCode===401){
            ElMessage.warning("出现异常，需要重新登录");
            sharedCookie.clearAll();
            router.push("/Login");
          }
        });
  }

  function deleteGroup(){
    // 定义请求体数据
    const data = {
      groupId: deleteGroupId
    };

// 定义请求头，包括自定义的 Authorization 头
    const config = {
      headers: {
        'Authorization': sharedCookie.get("loginAuthorization")
      }
    };


// 发送 POST 请求
    axios.put('http' + sharedData.serverIP + '/group/deleteGroup', data, config)
        .then(function (response) {
          if (response.data.code == 200) {
            lines.value.push('删除群聊成功');
          } else {
            lines.value.push('群聊删除失败 ' + response.data.message);
          }
        })
        .catch(function (error) {
          lines.value.push('请求失败');
          const errCode=error.response.status;
          if(errCode===401){
            ElMessage.warning("出现异常，需要重新登录");
            sharedCookie.clearAll();
            router.push("/Login");
          }
        });
  }

  function createGroup(createGroupKind, createGroupName) {
    // 定义请求体数据
    const data = {
      groupKind: createGroupKind, // 或者 '2'，根据你的需要
      groupName: createGroupName// 替换为实际的群聊名称
    };

// 定义请求头，包括自定义的 Authorization 头
    const config = {
      headers: {
        'Authorization': sharedCookie.get("loginAuthorization") // 替换为你的授权令牌
      }
    };

// 发送 POST 请求
    axios.post('http' + sharedData.serverIP + '/group/createGroup', data, config)
        .then(function (response) {
          // console.log('请求成功:', response.data);
          if (response.data.code == 200) {
            lines.value.push('群聊创建成功');
          }
          else {
            lines.value.push('群聊创建失败 ' + response.data.message);
          }
        })
        .catch(function (error) {
          lines.value.push('请求失败');

          const errCode=error.response.status;
          if(errCode===401){
            ElMessage.warning("出现异常，需要重新登录");
            sharedCookie.clearAll();
            router.push("/Login");
          }
        });
  }

  function announcementSet(announcementNow, announcementGroupId) {
    // 定义请求体数据
    const data = {
      groupAnnouncement:announcementNow,
      groupId:announcementGroupId
    };

// 定义请求头，包括自定义的 Authorization 头
    const config = {
      headers: {
        'Authorization': sharedCookie.get("loginAuthorization")
      }
    };

// 发送 POST 请求
    axios.put('http' + sharedData.serverIP + '/group/groupAnnouncementSet', data, config)
        .then(function (response) {
          // console.log(response.data);
          if (response.data.code == 200) {
            lines.value.push('群公告设置成功');
          } else {
            lines.value.push('群公告设置失败 ' + response.data.message);
          }
        })
        .catch(function (error) {
          lines.value.push('请求失败');
          const errCode=error.response.status;
          if(errCode===401){
            ElMessage.warning("出现异常，需要重新登录");
            sharedCookie.clearAll();
            router.push("/Login");
          }
        });
  }

  function updateGroup() {
    // 定义请求体数据
    const data = {
      groupId: updateGroupGroupId,
      groupName: updateGroupGroupName.toString(),
      groupKind: updateGroupGroupKind,
      groupAvatar:updateGroupGroupAvatar.toString()
    };

// 定义请求头，包括自定义的 Authorization 头
    const config = {
      headers: {
        'Authorization': sharedCookie.get("loginAuthorization")
      }
    };


// 发送 POST 请求
    axios.put('http' + sharedData.serverIP + '/group/groupModify', data, config)
        .then(function (response) {
          if (response.data.code == 200) {
            lines.value.push('群信息修改成功');
          } else {
            lines.value.push('群信息修改失败 ' + response.data.message);
          }
        })
        .catch(function (error) {
          lines.value.push('请求失败');
          const errCode=error.response.status;
          if(errCode===401){
            ElMessage.warning("出现异常，需要重新登录");
            sharedCookie.clearAll();
            router.push("/Login");
          }
        });
  }

  if (command === 'help') {
    lines.value.push(command); // Add command to the history
    let options = '';
    optionList.value.forEach(item => {
      options += item + "\n";
    })
    lines.value.push(options); // Feedback message
  }
  else if(isCardNumSet(command)){
    lines.value.push(command);
    cardNumSet();
  }
  else if(isSecretGroupJoin(command)){
    lines.value.push(command);
    secretGroupJoin();
  }
  else if(isSelectJoinCode(command)){
    lines.value.push(command);
    selectJoinCode();
  }
  else if(isDeleteGroup(command)){
    lines.value.push(command);
    deleteGroup();
  }
  else if (isCreateGroup(command)) {
    lines.value.push(command);
    createGroup(createGroupKind, createGroupName);
  }else if(isUpdateGroup(command)){
    lines.value.push(command);
    updateGroup();
  } else if (command === 'back') {
    router.push("/talkingRoom");
  } else if (command === 'exit') {
    sharedCookie.clearAll();
    router.push("/Login");
  } else if(command==='random') {
    lines.value.push(command);
    lines.value.push(randomList.value[Math.floor(Math.random() * randomList.value.length)]);
  }else if(command==='old message'){
    lines.value.push(command);
    sharedCookie.set("old message",'true');
    lines.value.push("操作成功");
  }else if(command==='select my groupInf'){
    lines.value.push(command);
    axios.get('http' + sharedData.serverIP + '/group/selectGroupUserCreated', {
      headers: {
        'Authorization': sharedCookie.get("loginAuthorization")
      }
    })
      .then(function (response) {
        let resSelect='';
        if (response.data.code == 200) {
          resSelect='查询成功';
          response.data.data.forEach(item=>{
            resSelect+="\n"+item.groupId+" "+item.groupName+" "+item.groupKind+" "+item.groupAvatar;
          });
          lines.value.push(resSelect);
        } else {
          lines.value.push('查询失败 ' + response.data.message);
        }
      })
      .catch(function (error) {
        lines.value.push('请求失败');
        const errCode=error.response.status;
        if(errCode===401){
          ElMessage.warning("出现异常，需要重新登录");
          sharedCookie.clearAll();
          router.push("/Login");
        }
      });
  }
  else if(isAnnouncementSet(command)){
    lines.value.push(command);
    announcementSet(announcementNow,announcementGroupId);
  }
  else
  {
    lines.value.push(command); // Add command to the history
    lines.value.push('请输入正确的命令'); // Feedback message
  }

  input.value = ''; // Clear input after execution

  doubleTerminalScrollHeight();
}


// Method to double the scroll height
function doubleTerminalScrollHeight() {
  // 获取终端内容区域的DOM元素
  const terminalContent = document.querySelector('.terminal-body');
  if (terminalContent) {
    // 获取当前的max-height值
    const currentHeight = parseInt(window.getComputedStyle(terminalContent).height);

    terminalContent.style.height = `${currentHeight * 1.5}px`;
  }
}


onMounted(() => {
  document.getElementById("commandInput").focus();
})

</script>

<style scoped>
.terminal {
  font-family: 'Courier New', Courier, monospace;
  background-color: #000;
  color: #fff;
  padding: 10px;
  border-radius: 5px;

  width: 100%;
  height: 100%;

  margin: 20px auto;
  overflow-y: auto;
}

.terminal-body {
  padding: 10px;
}

.terminal-content {
  white-space: pre-wrap;
}

.terminal-line {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.prompt {
  color: #0f0;
}

.command {
  color: #fff;
  font-size: 1em; /* Slightly larger font size for commands */
}

.input-line {
  display: flex;
  align-items: center;
  margin-bottom: 5px; /* 给输入行添加一些间距 */
}

.terminal-input {
  flex: 1;
  border: none;
  background: transparent;
  color: #fff;
  outline: none;
  font-family: 'Courier New', Courier, monospace; /* 与 .command 一致 */
  font-size: 1em; /* 与 .command 一致 */
  padding: 0; /* 移除内边距，使输入框更紧凑 */
  width: 0; /* 使输入框填充剩余空间 */
}

/* Cursor blink animation */
@keyframes blink {
  to {
    opacity: 0;
  }
}
</style>