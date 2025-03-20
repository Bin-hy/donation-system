import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import UserList from '@/views/UserList.vue';
import UserMessages from '@/views/UserMessages.vue';
import NewsList from '@/views/NewsList.vue';
import ProjectList from '@/views/ProjectList.vue';
import ProjectIncome from '@/views/ProjectIncome.vue';
import ProjectReview from '@/views/ProjectReview.vue';
import LoginPage from '@/views/LoginPage.vue';
import BarragePage from '@/views/BarragePage.vue'; // 弹幕管理
import CarouselList from '@/views/CarouselList.vue'; // 轮播管理
import AnnouncementList from '@/views/AnnouncementList.vue'; // 公示信息管理


const routes = [
    {
        path: '/',
        component: HomePage,
        meta: { title: '首页', showNavBar: true },
    },
    {
        path: '/user/list',
        component: UserList,
        meta: { title: '用户列表', showNavBar: true },
    },
    {
        path: '/user/messages',
        component: UserMessages,
        meta: { title: '用户消息', showNavBar: true },
    },
    {
        path: '/news/list',
        component: NewsList,
        meta: { title: '新闻列表', showNavBar: true },
    },
    {
        path: '/project/list',
        component: ProjectList,
        meta: { title: '项目列表', showNavBar: true },
    },
    {
        path: '/project/income',
        component: ProjectIncome,
        meta: { title: '项目收入', showNavBar: true },
    },
    {
        path: '/project/review',
        component: ProjectReview,
        meta: { title: '项目评价', showNavBar: true },
    },
    {
        path: '/login',
        component: LoginPage,
        meta: { title: '登录', showNavBar: false },
    },
    {
        path: '/project/barrage',
        component: BarragePage,
        meta: { title: '弹幕管理', showNavBar: true },
    },
    // 新增：轮播管理
    {
        path: '/other-config/carousel',
        component: CarouselList,
        meta: { title: '轮播管理', showNavBar: true },
    },
    // 新增：公示信息管理
    {
        path: '/other-config/announcement',
        component: AnnouncementList,
        meta: { title: '公示信息管理', showNavBar: true },
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
