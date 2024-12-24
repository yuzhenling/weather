<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Download } from '@element-plus/icons-vue'

const weatherDataList = ref([])
const cities = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({
  cityId: '',
  date: '',
  temperature: '',
  humidity: '',
  weather: '',
  windDirection: '',
  windSpeed: ''
})
const isEdit = ref(false)
const editId = ref(null)

// 查询条件
const queryParams = ref({
  cityId: '',
  startDate: '',
  endDate: ''
})

// 添加日期格式化函数
const formatDate = (date) => {
  if (!date) return ''
  if (typeof date === 'string') return date
  return date.toISOString().split('T')[0]
}

const loadCities = async () => {
  try {
    cities.value = await request.get('/cities')
    // 如果有城市数据，默认选择第一个城市
    if (cities.value.length > 0) {
      queryParams.value.cityId = cities.value[0].id
      // 设置默认的日期范围（比如最近7天）
      const today = new Date()
      const sevenDaysAgo = new Date()
      sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 6)
      
      queryParams.value.startDate = sevenDaysAgo
      queryParams.value.endDate = today
      
      // 自动加载第一个城市的数据
      loadWeatherData()
    }
  } catch (error) {
    console.error('加载城市列表失败:', error)
  }
}

const loadWeatherData = async () => {
  try {
    if (queryParams.value.cityId && queryParams.value.startDate && queryParams.value.endDate) {
      weatherDataList.value = await request.get(`/weather-data/city/${queryParams.value.cityId}`, {
        params: {
          startDate: formatDate(queryParams.value.startDate),
          endDate: formatDate(queryParams.value.endDate)
        }
      })
    }
  } catch (error) {
    console.error('加载天气数据失败:', error)
  }
}

const handleSearch = () => {
  loadWeatherData()
}

const handleAdd = () => {
  dialogTitle.value = '添加天气数据'
  isEdit.value = false
  form.value = {
    cityId: '',
    date: '',
    temperature: '',
    humidity: '',
    weather: '',
    windDirection: '',
    windSpeed: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑天气数据'
  isEdit.value = true
  editId.value = row.id
  form.value = {
    cityId: row.city.id,
    date: row.date,
    temperature: row.temperature,
    humidity: row.humidity,
    weather: row.weather,
    windDirection: row.windDirection,
    windSpeed: row.windSpeed
  }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条天气数据吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/weather-data/${id}`)
    ElMessage.success('删除成功')
    loadWeatherData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除天气数据失败:', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    const data = {
      ...form.value,
      date: formatDate(form.value.date),  // 格式化日期
      city: { id: form.value.cityId }
    }
    if (isEdit.value) {
      await request.put(`/weather-data/${editId.value}`, data)
    } else {
      await request.post('/weather-data', data)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    loadWeatherData()
  } catch (error) {
    console.error('保存天气数据失败:', error)
  }
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success(response.data)
    loadWeatherData()
  } else {
    ElMessage.error(response.message)
  }
}

const handleUploadError = (error) => {
  ElMessage.error('上传失败：' + error.message)
}

const beforeUpload = (file) => {
  const isCsv = file.type === 'text/csv' || file.name.endsWith('.csv')
  if (!isCsv) {
    ElMessage.error('只能上传CSV文件！')
    return false
  }
  return true
}

const handleDownloadData = async () => {
  try {
    if (!queryParams.value.cityId) {
      ElMessage.warning('请先选择城市')
      return
    }

    // 获取当前城市的天气数据
    const weatherData = await request.get(`/weather-data/city/${queryParams.value.cityId}`, {
      params: {
        startDate: formatDate(queryParams.value.startDate),
        endDate: formatDate(queryParams.value.endDate)
      }
    })

    if (!weatherData || weatherData.length === 0) {
      ElMessage.warning('没有找到数据')
      return
    }

    // 生成CSV内容
    const headers = ['城市ID,日期,温度,湿度,天气,风向,风速']
    const rows = weatherData.map(item => {
      return [
        item.city.id,
        item.date,
        item.temperature,
        item.humidity,
        item.weather,
        item.windDirection,
        item.windSpeed
      ].join(',')
    })

    const csvContent = [headers, ...rows].join('\n')
    const selectedCity = cities.value.find(city => city.id === queryParams.value.cityId)
    const fileName = `${selectedCity.name}天气数据.csv`

    // 创建并下载文件
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = fileName
    link.click()
    URL.revokeObjectURL(link.href)

    ElMessage.success('数据导出成功')
  } catch (error) {
    console.error('导出数据失败:', error)
    ElMessage.error('导出数据失败')
  }
}

onMounted(() => {
  loadCities()
})
</script>

<template>
  <div class="weather-data-container">
    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <div class="form-content">
          <!-- 查询条件 -->
          <el-form-item label="城市">
            <el-select 
              v-model="queryParams.cityId" 
              placeholder="请选择城市"
              style="width: 200px;"
            >
              <el-option
                v-for="city in cities"
                :key="city.id"
                :label="city.name"
                :value="city.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="queryParams.startDate"
              type="date"
              placeholder="开始日期"
              value-format="YYYY-MM-DD"
            />
            <span class="separator">-</span>
            <el-date-picker
              v-model="queryParams.endDate"
              type="date"
              placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <!-- 所有按钮 -->
          <div class="button-group">
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>添加
            </el-button>
            
            <el-divider direction="vertical" />
            
            <el-upload
              class="upload-button"
              action="/api/weather-data/upload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :before-upload="beforeUpload"
              accept=".csv"
            >
              <el-button type="success">
                <el-icon><Upload /></el-icon>上传CSV
              </el-button>
            </el-upload>
            
            <el-button type="warning" @click="handleDownloadData">
              <el-icon><Download /></el-icon>导出数据
            </el-button>
          </div>
        </div>
      </el-form>
    </div>

    <el-table :data="weatherDataList" border style="width: 100%">
      <el-table-column prop="city.name" label="城市" />
      <el-table-column prop="date" label="日期" />
      <el-table-column prop="temperature" label="温度(°C)" />
      <el-table-column prop="humidity" label="湿度(%)" />
      <el-table-column prop="weather" label="天气" />
      <el-table-column prop="windDirection" label="风向" />
      <el-table-column prop="windSpeed" label="风速(m/s)" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="城市">
          <el-select v-model="form.cityId" placeholder="请选择城市">
            <el-option
              v-for="city in cities"
              :key="city.id"
              :label="city.name"
              :value="city.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="温度">
          <el-input v-model="form.temperature" type="number" />
        </el-form-item>
        <el-form-item label="湿度">
          <el-input v-model="form.humidity" type="number" />
        </el-form-item>
        <el-form-item label="天气">
          <el-input v-model="form.weather" />
        </el-form-item>
        <el-form-item label="风向">
          <el-input v-model="form.windDirection" />
        </el-form-item>
        <el-form-item label="风速">
          <el-input v-model="form.windSpeed" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.weather-data-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.search-form {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 24px;
}

.form-content {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.button-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto; /* 让按钮组靠右 */
}

.upload-button {
  margin: 0;
}

:deep(.upload-button .el-upload) {
  width: auto;
}

:deep(.el-divider--vertical) {
  height: 20px;
  margin: 0 8px;
}

/* 调整表单项间距 */
:deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 0;
}

:deep(.el-button) {
  border-radius: 4px;
  padding: 8px 16px;
}

:deep(.el-button .el-icon) {
  margin-right: 4px;
}

.separator {
  margin: 0 10px;
  color: #909399;
}

:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  background-color: #f4f6f8;
  padding: 20px;
  margin: 0;
  border-radius: 8px 8px 0 0;
}

:deep(.el-dialog__body) {
  padding: 30px 20px;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #e4e7ed;
  padding: 15px 20px;
}
</style> 