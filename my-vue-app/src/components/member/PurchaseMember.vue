<template>
  <div class="purchase-container">
    <!-- 申请表单 -->
    <el-card class="form-card">
      <h3>提交购入申请</h3>
      <el-form :model="form" label-width="120px">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.itemName" />
        </el-form-item>
        <el-form-item label="申请金额" required>
          <el-input-number v-model="form.amount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitRequest">提交申请</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 申请记录 -->
    <el-card class="record-card">
      <h3>申请记录</h3>
      <el-table :data="requests" border>
        <el-table-column prop="itemName" label="商品名称" />
        <el-table-column prop="description" label="商品描述" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{row}">¥{{ row.amount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{row}">
            <el-tag :type="statusTagType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="180">
          <template #default="{row}">
            {{ formatDeadline(row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{row}">
            <el-upload
              v-if="row.status === 'Approved' && !isExpired(row.deadline)"
              :show-file-list="false"
              :http-request="uploadImage"
              :data="{ requestId: row.requestId }"
            >
              <el-button type="primary">上传凭证</el-button>
            </el-upload>
            <span v-else-if="isExpired(row.deadline)" class="expired-text">
              已超时
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const userInfo = JSON.parse(localStorage.getItem('userInfo'))

const memberId = userInfo.id

const form = ref({
    userId: memberId,
    familyId: 1,
  itemName: '',
  amount: 0,
  description: ''
})

const requests = ref([])

// 获取申请记录
const fetchRequests = async () => {
  const { data } = await axios.get(`http://localhost:8081/api/purchase/my-requests/${memberId}`)
  requests.value = data
}

// 提交申请
const submitRequest = async () => {
  await axios.post('http://localhost:8081/api/purchase/request', form.value)
  ElMessage.success('申请提交成功')
  fetchRequests()
}

// 上传图片
const uploadImage = async ({ file, data }) => {
  const formData = new FormData()
  formData.append('file', file)
  await axios.post(`http://localhost:8081/api/purchase/upload/${data.requestId}`, formData)
  ElMessage.success('上传成功')
  fetchRequests()
}

// 状态标签样式
const statusTagType = (status) => {
  return {
    Pending: 'info',
    Approved: 'success',
    Rejected: 'danger',
    Completed: ''
  }[status]
}

onMounted(fetchRequests)

// 添加缺少的方法定义
const formatDeadline = (date) => {
    if (!date) return '-'
    return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    })
}

// 判断是否过期
const isExpired = (deadline) => {
    if (!deadline) return false
    return new Date(deadline) < new Date()
}
</script>