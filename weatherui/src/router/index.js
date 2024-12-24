import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/cities',
    name: 'Cities',
    component: () => import('../views/city/CityList.vue')
  },
  {
    path: '/weather-data',
    name: 'WeatherData',
    component: () => import('../views/weather/WeatherDataList.vue')
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('../views/user/UserList.vue')
  },
  {
    path: '/weather-trend',
    name: 'WeatherTrend',
    component: () => import('../views/charts/WeatherTrend.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 