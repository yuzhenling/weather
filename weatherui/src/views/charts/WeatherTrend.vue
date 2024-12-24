<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import request from '../../utils/request'
import * as echarts from 'echarts'

const cities = ref([])
const selectedCityId = ref('')
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

// 初始化城市列表
const initCities = async () => {
  try {
    const response = await request.get('/cities')
    cities.value = response
    if (cities.value.length > 0) {
      selectedCityId.value = cities.value[0].id
      await loadWeatherData()
    }
  } catch (error) {
    console.error('加载城市列表失败:', error)
  }
}

// 加载天气数据
const loadWeatherData = async () => {
  if (!selectedCityId.value) return
  
  try {
    const { startDate, endDate } = getDateRange()
    const response = await request.get(`/weather-data/city/${selectedCityId.value}`, {
      params: { startDate, endDate }
    })
    
    initChart(response)
  } catch (error) {
    console.error('加载天气数据失败:', error)
  }
}

// 初始化图表
const initChart = (data) => {
  const chartDom = document.getElementById('weather-chart')
  if (!chartDom) return
  
  if (weatherChart.value) {
    weatherChart.value.dispose()
  }
  
  weatherChart.value = echarts.init(chartDom)
  
  const option = {
    title: {
      text: '近7天天气趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['温度', '湿度'],
      top: '30px'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true,
      top: 80
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map(item => item.date.substring(5)), // 只显示月-日
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '温度(°C)',
        position: 'left',
        axisLine: {
          show: true,
          lineStyle: {
            color: '#ff7300'
          }
        }
      },
      {
        type: 'value',
        name: '湿度(%)',
        position: 'right',
        axisLine: {
          show: true,
          lineStyle: {
            color: '#5470C6'
          }
        }
      }
    ],
    series: [
      {
        name: '温度',
        type: 'line',
        smooth: true,
        data: data.map(item => item.temperature),
        itemStyle: {
          color: '#ff7300'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 115, 0, 0.3)' },
            { offset: 1, color: 'rgba(255, 115, 0, 0.1)' }
          ])
        }
      },
      {
        name: '湿度',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: data.map(item => item.humidity),
        itemStyle: {
          color: '#5470C6'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(84, 112, 198, 0.3)' },
            { offset: 1, color: 'rgba(84, 112, 198, 0.1)' }
          ])
        }
      }
    ]
  }
  
  weatherChart.value.setOption(option)
}

// 处理城市选择变化
const handleCityChange = () => {
  loadWeatherData()
}

// 处理窗口大小变化
const handleResize = () => {
  if (weatherChart.value) {
    weatherChart.value.resize()
  }
}

onMounted(() => {
  initCities()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (weatherChart.value) {
    weatherChart.value.dispose()
  }
})
</script>

<template>
  <div class="trend-container">
    <el-card class="chart-card">
      <div class="city-select-container">
        <el-select 
          v-model="selectedCityId"
          placeholder="请选择城市"
          @change="handleCityChange"
          class="city-select"
        >
          <el-option
            v-for="city in cities"
            :key="city.id"
            :label="city.name"
            :value="city.id"
          />
        </el-select>
      </div>
      
      <div id="weather-chart" class="chart-area"></div>
    </el-card>
  </div>
</template>

<style scoped>
.trend-container {
  padding: 20px;
}

.chart-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.city-select-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.city-select {
  width: 200px;
}

.chart-area {
  height: 500px;
  width: 100%;
  padding: 20px;
}
</style> 