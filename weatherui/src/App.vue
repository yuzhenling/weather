<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeMenu = ref('/')

const handleSelect = (key) => {
  router.push(key)
}
</script>

<template>
  <el-container class="layout-container">
    <el-header>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-demo"
        mode="horizontal"
        background-color="#1e88e5"
        text-color="#fff"
        active-text-color="#ffd04b"
        @select="handleSelect"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/weather-data">天气数据查询</el-menu-item>
        <el-menu-item index="/weather-trend">天气趋势图</el-menu-item>
        <el-menu-item index="/cities">城市管理</el-menu-item>
        <el-menu-item index="/users">用户管理</el-menu-item>
      </el-menu>
    </el-header>
    
    <el-main class="main-content">
      <router-view></router-view>
    </el-main>
  </el-container>
</template>

<style scoped>
.layout-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-header {
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
  background-color: #1e88e5;
}

.main-content {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}

:deep(.el-menu-item) {
  font-size: 16px;
}
</style>
