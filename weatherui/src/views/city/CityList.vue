<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const cities = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({
  name: '',
  province: '',
  description: ''
})
const isEdit = ref(false)
const editId = ref(null)

const loadCities = async () => {
  try {
    cities.value = await request.get('/cities')
  } catch (error) {
    console.error('加载城市列表失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加城市'
  isEdit.value = false
  form.value = {
    name: '',
    province: '',
    description: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑城市'
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个城市吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/cities/${id}`)
    ElMessage.success('删除成功')
    loadCities()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除城市失败:', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await request.put(`/cities/${editId.value}`, form.value)
    } else {
      await request.post('/cities', form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    loadCities()
  } catch (error) {
    console.error('保存城市失败:', error)
  }
}

onMounted(() => {
  loadCities()
})
</script>

<template>
  <div class="city-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加城市
      </el-button>
    </div>

    <el-table :data="cities" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="城市名称" />
      <el-table-column prop="province" label="所属省份" />
      <el-table-column prop="description" label="描述" />
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
        <el-form-item label="城市名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="所属省份">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
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
.city-container {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style> 