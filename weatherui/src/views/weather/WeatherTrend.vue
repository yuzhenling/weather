<script setup>
import { ref, onMounted, nextTick } from 'vue'
import request from '../../utils/request'
import * as echarts from 'echarts'

const cities = ref([])
const selectedCityId = ref('')
const weatherData = ref([])
const weatherChart = ref(null)

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

// 加载城市列表
const loadCities = async () => {
  try {
    cities.value = await request.get('/api/cities')
    if (cities.value.length > 0) {
      selectedCityId.value = cities.value[0].id
      await loadWeatherData()
    }
  } catch (error) {
    console.error('加载城市列表失败:', error)
  }
}

// 加载天气数据并绘制图表
const loadWeatherData = async () => {
  if (!selectedCityId.value) return
  
  const { startDate, endDate } = getDateRange()
  try {
    weatherData.value = await request.get(`/api/weather-data/city/${selectedCityId.value}`, {
      params: { startDate, endDate }
    })
    
    await nextTick()
    drawChart()
  } catch (error) {
    console.error('加载天气数据失败:', error)
  }
}

// 绘制图表
const drawChart = () => {
  const chartDom = document.getElementById('weather-chart')
  if (!chartDom) return
  
  if (weatherChart.value) {
    weatherChart.value.dispose()
  }
  
  weatherChart.value = echarts.init(chartDom)
  
  const dates = weatherData.value.map(item => item.date)
  const temperatures = weatherData.value.map(item => item.temperature)
  const humidity = weatherData.value.map(item => item.humidity)
  
  const option = {
    title: {
      text: '近7天天气趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['温度(°C)', '湿度(%)'],
      top: 30
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '温度(°C)',
        position: 'left'
      },
      {
        type: 'value',
        name: '湿度(%)',
        position: 'right'
      }
    ],
    series: [
      {
        name: '温度(°C)',
        type: 'line',
        data: temperatures,
        smooth: true
      },
      {
        name: '湿度(%)',
        type: 'line',
        yAxisIndex: 1,
        data: humidity,
        smooth: true
      }
    ],
    grid: {
      top: 80,
      bottom: 50
    }
  }
  
  weatherChart.value.setOption(option)
}

// 监听城市选择变化
const handleCityChange = () => {
  loadWeatherData()
}

// 监听窗口大小变化，调整图表大小
const handleResize = () => {
  if (weatherChart.value) {
    weatherChart.value.resize()
  }
}

onMounted(async () => {
  await loadCities()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (weatherChart.value) {
    weatherChart.value.dispose()
  }
})
</script>

<template>
  <div class="weather-trend-container">
    <el-card class="weather-card">
      <template #header>
        <div class="card-header">
          <el-select 
            v-model="selectedCityId" 
            placeholder="选择城市"
            @change="handleCityChange"
            style="width: 200px;"
          >
            <el-option
              v-for="city in cities"
              :key="city.id"
              :label="city.name"
              :value="city.id"
            />
          </el-select>
        </div>
      </template>
      
      <!-- 图表区域 -->
      <div id="weather-chart" class="chart-container"></div>
      
      <!-- 天气数据列表 -->
      <el-table :data="weatherData" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="temperature" label="温度(°C)" width="100" />
        <el-table-column prop="humidity" label="湿度(%)" width="100" />
        <el-table-column prop="weather" label="天气" width="100" />
        <el-table-column prop="windDirection" label="风向" width="100" />
        <el-table-column prop="windSpeed" label="风速(m/s)" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.weather-trend-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.weather-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  background-color: #f4f6f8;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-select) {
  width: 200px;
}

.chart-container {
  height: 400px;
  width: 100%;
  margin: 20px 0;
  padding: 20px;
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table) {
  border-radius: 6px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f4f6f8;
}
</style> 