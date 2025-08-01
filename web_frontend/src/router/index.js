import { createRouter, createWebHistory } from 'vue-router';
import WelcomePage from '../components/WelcomePage.vue';
import MainPage from "@/components/MainPage.vue";
import RegisterPage from "@/components/RegisterPage.vue";

const routes = [
    { path: '/', component: WelcomePage },
    { path: '/login', component: WelcomePage },
    { path: '/main', component: MainPage},
    { path: '/register', component: RegisterPage},
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
