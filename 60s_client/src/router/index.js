// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import RegisterPage from '@/components/RegisterPage.vue';
import LoginPage from "@/components/LoginPage.vue"; // 调整路径以匹配您的文件结构
import test from "@/components/test.vue";
import talkingRoom from "@/components/chatroom/TalkingRoom.vue"
import systemCommand from "@/components/SystemCommand.vue"




const routes = [
    {
        path: '/test', // 测试页面
        name: 'test ',
        component: test
    },
    {
        path: '/', // 默认为登录页面
        name: 'Default',
        component: LoginPage
    },
    {
        path: '/login', // 登录页面的路由路径
        name: 'Login',
        component: LoginPage
    },
    {
        path: '/register', // 注册页面的路由路径
        name: 'Register',
        component: RegisterPage
    },
    // 您可以在这里添加更多的路由配置
    {
        path: '/talkingRoom',
        name: 'TalkingRoom',
        component: talkingRoom
    },
    {
        path: '/systemCommand',
        name: 'SystemCommand',
        component: systemCommand
    }
];


const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;