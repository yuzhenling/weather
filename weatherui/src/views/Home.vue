<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const cityCount = ref(0)
const weatherDataCount = ref(0)
const cityWeatherData = ref([])

// 获取近7天的日期范围
const getDateRange = () => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 6)
  return {
    startDate: start.toISOString().split('T')[0],
    endDate: end.toISOString().split('T')[0]
  }
}

// 分开加载统计数据和详细数据
const loadStatistics = async () => {
  try {
    // 获取城市总数
    const cityRes = await request.get('/cities/count')
    if (cityRes) {
      cityCount.value = cityRes
    }

    // 获取天气记录总数
    const weatherRes = await request.get('/weather-data/count')
    if (weatherRes) {
      weatherDataCount.value = weatherRes
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载城市天气详细数据
const loadCityWeatherData = async () => {
  try {
    // 1. 获取所有城市
    const cities = await request.get('/cities')
    
    // 2. 获取日期范围
    const { startDate, endDate } = getDateRange()
    
    // 3. 清空之前的数据
    cityWeatherData.value = []
    
    // 4. 遍历每个城市获取天气数据
    for (const city of cities) {
      const weatherData = await request.get(`/weather-data/city/${city.id}`, {
        params: { startDate, endDate }
      })
      
      cityWeatherData.value.push({
        cityName: city.name,
        weatherCount: weatherData.length,
        weatherData: weatherData
      })
    }
  } catch (error) {
    console.error('加载城市天气数据失败:', error)
  }
}

// 页面加载时分别加载统计数据和详细数据
onMounted(() => {
  loadStatistics()
  loadCityWeatherData()
})


</script>

<template>
  <div class="home-container">
    <!-- 添加背景图区域 -->
    <div class="weather-banner">
      <div class="banner-content">
        <h1>天气数据管理系统</h1>
        <p>掌握各城市天气动态</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>城市数量</span>
            </div>
          </template>
          <div class="card-content">
            {{ cityCount }}
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总天气记录数</span>
            </div>
          </template>
          <div class="card-content">
            {{ weatherDataCount }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 城市天气数据列表 -->
    <el-row :gutter="20" class="weather-row">
      <el-col :span="24">
        <el-card v-for="cityData in cityWeatherData" 
                :key="cityData.cityName" 
                class="weather-card"
        >
          <template #header>
            <div class="card-header">
              <span>{{ cityData.cityName }}</span>
              <span class="weather-count">记录数: {{ cityData.weatherCount }}</span>
            </div>
          </template>
          
          <el-table :data="cityData.weatherData" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="temperature" label="温度(°C)" width="100" />
            <el-table-column prop="humidity" label="湿度(%)" width="100" />
            <el-table-column prop="weather" label="天气" width="100" />
            <el-table-column prop="windDirection" label="风向" width="100" />
            <el-table-column prop="windSpeed" label="风速(m/s)" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.home-container {
  padding: 20px;
}

.weather-banner {
  height: 300px;
  margin: -20px -20px 20px -20px;
  position: relative;
  background-image: url('../assets/banner.png');
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.banner-content {
  text-align: center;
  color: #fff;
  z-index: 1;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.banner-content h1 {
  font-size: 3em;
  margin-bottom: 15px;
  font-weight: 600;
}

.banner-content p {
  font-size: 1.4em;
  opacity: 0.9;
}

.statistics-row {
  margin-bottom: 30px;
}

.weather-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.weather-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.card-header {
  background-color: #f4f6f8;
  padding: 15px 20px;
  border-radius: 8px 8px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  padding: 30px 0;
  color: #1e88e5;
}

.weather-count {
  font-size: 14px;
  color: #909399;
  background-color: #f4f6f8;
  padding: 4px 8px;
  border-radius: 4px;
}

:deep(.el-table) {
  border-radius: 6px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f4f6f8;
}
</style> 