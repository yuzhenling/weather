<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({
  username: '',
  password: '',
  role: 'USER' // 默认角色
})
const isEdit = ref(false)
const editId = ref(null)

const loadUsers = async () => {
  try {
    users.value = await request.get('/users')
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加用户'
  isEdit.value = false
  form.value = {
    username: '',
    password: '',
    role: 'USER'
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  isEdit.value = true
  editId.value = row.id
  form.value = {
    username: row.username,
    password: '', // 编辑时密码为空，如果不修改则不更新密码
    role: row.role
  }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/users/${id}`)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      // 如果密码为空，则不更新密码
      const updateData = { ...form.value }
      if (!updateData.password) {
        delete updateData.password
      }
      await request.put(`/users/${editId.value}`, updateData)
    } else {
      await request.post('/users', form.value)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
    dialogVisible.value = false
    loadUsers()
  } catch (error) {
    console.error('保存用户失败:', error)
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<template>
  <div class="user-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加用户
      </el-button>
    </div>

    <el-table :data="users" border style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'success'">
            {{ row.role }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(row.id)"
            :disabled="row.role === 'ADMIN'"
          >
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
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input 
            v-model="form.password" 
            type="password"
            :placeholder="isEdit ? '不修改请留空' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="普通用户" value="USER" />
          </el-select>
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
.user-container {
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.el-tag {
  text-transform: capitalize;
}
</style> 