import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import AboutPage from '@/views/AboutPage.vue';
import ServicesPage from '@/views/ServicesPage.vue';
import ProjectDetail from '@/views/ProjectDetail.vue';
import DonationPage from '@/views/DonationPage.vue';
import LoginRegister from '@/views/LoginRegister.vue'; // 登录注册界面
import NewsDetail from '@/views/NewsDetail.vue';
import ProjectsPage from '@/views/ProjectsPage.vue'; // 公益项目页面
import NewsPage from '@/views/NewsPage.vue'; // 新闻资讯页面
import ApplyPage from '@/views/ApplyPage.vue'; // 申请救助页面
import ProfilePage from '@/views/ProfilePage.vue'; // 个人中心页面


const routes = [
    { path: '/', component: HomePage },
    { path: '/about', component: AboutPage },
    { path: '/services', component: ServicesPage },
    {
        path: '/project/:id',
        name: 'ProjectDetail',
        component: ProjectDetail,
    },
    {
        path: '/donation/:id',
        name: 'DonationPage',
        component: DonationPage,
    },
    {
        path: '/login-register', // 登录注册界面
        name: 'LoginRegister',
        component: LoginRegister,
    },
    {
        path: '/news/:id',
        name: 'NewsDetail',
        component: NewsDetail,
    },
    {
        path: '/projects', // 公益项目页面
        name: 'ProjectsPage',
        component: ProjectsPage,
    },
    {
        path: '/news', // 新闻资讯页面
        name: 'NewsPage',
        component: NewsPage,
    },
    {
        path: '/apply', // 申请救助页面
        name: 'ApplyPage',
        component: ApplyPage,
    },
    // 新增路由
    {
        path: '/profile', // 个人中心页面
        name: 'ProfilePage',
        component: ProfilePage,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
