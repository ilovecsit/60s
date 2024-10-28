// main.js
import { createApp } from 'vue';
import App from './App.vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import router from './router'; // 引入路由配置
import lazyPlugin from 'vue3-lazy'

// 引入组件
import ChatCard from '@/components/chatroom/chatCard/ChatCard.vue';
import DefaultView from '@/components/chatroom/mainView/Default.vue';
import GroupView from '@/components/chatroom/mainView/Group.vue';
import GroupSelectView from '@/components/chatroom/mainView/GroupSelect.vue';
import GroupJoinView from '@/components/chatroom/mainView/GroupJoin.vue';
import UserInfView from '@/components/chatroom/mainView/UserInfView.vue';
import ChatCardOnlyOne   from "@/components/chatroom/chatCard/ChatCardOnlyOne.vue";
import GroupMemInfView  from "@/components/chatroom/mainView/GroupMemInf.vue";


const app = createApp(App);

// 注册全局组件
app.component('ChatCard', ChatCard);
app.component('DefaultView', DefaultView);
app.component('GroupView', GroupView);
app.component('GroupSelectView', GroupSelectView);
app.component('GroupJoinView', GroupJoinView);
app.component('UserInfView', UserInfView);
app.component('ChatCardOnlyOne',ChatCardOnlyOne)
app.component('GroupMemInfView',GroupMemInfView)

app.use(lazyPlugin,{
    loading:'loaded.gif',
    error:'error.gif'
});
app.use(ElementPlus);
app.use(router);
app.mount('#app');
