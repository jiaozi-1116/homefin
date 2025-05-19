<template>
  <div class="purchase-container">
    <!-- 页面标题 -->
    <h1 class="page-title">
      <i class="el-icon-shopping-cart-full"></i> 家庭采购申请
    </h1>

    <div class="grid-layout">
      <!-- 申请表单 -->
      <el-card class="form-card" shadow="hover">
        <div class="card-header">
          <h2><i class="el-icon-document-add"></i> 提交购入申请</h2>
        </div>
        <el-form 
          :model="form" 
          label-width="120px"
          label-position="top"
          class="purchase-form"
        >
          <el-form-item label="商品名称" required>
            <el-input 
              v-model="form.itemName" 
              placeholder="请输入商品名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="申请金额" required>
            <el-input-number 
              v-model="form.amount" 
              :min="0" 
              :precision="2" 
              :controls="false"
              placeholder="0.00"
              class="amount-input"
            />
            <span class="currency-symbol">¥</span>
          </el-form-item>
          <el-form-item label="商品描述">
            <el-input 
              v-model="form.description" 
              type="textarea" 
              :rows="4"
              placeholder="请输入商品描述（可选）"
              resize="none"
            />
          </el-form-item>
          <el-form-item>
            <el-button 
              type="primary" 
              @click="submitRequest"
              :disabled="!form.itemName || form.amount <= 0"
              class="submit-btn"
            >
              <i class="el-icon-upload"></i> 提交申请
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 申请记录 -->
      <el-card class="record-card" shadow="hover">
        <div class="card-header">
          <h2><i class="el-icon-tickets"></i> 申请记录</h2>
          <el-button 
            type="text" 
            @click="fetchRequests"
            class="refresh-btn"
          >
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
        
        <!-- 空状态 -->
        <el-empty 
          v-if="requests.length === 0 && !loading" 
          description="暂无申请记录"
          :image-size="100"
        ></el-empty>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-icon class="is-loading"><i class="el-icon-loading"></i></el-icon>
          <span>加载中...</span>
        </div>
        
        <!-- 申请表格 -->
        <el-table 
          v-if="requests.length > 0 && !loading"
          :data="requests" 
          style="width: 100%"
          stripe
          border
          size="medium"
          class="request-table"
        >
          <el-table-column 
            prop="itemName" 
            label="商品名称" 
            min-width="120"
          />
          <el-table-column 
            prop="description" 
            label="商品描述" 
            min-width="180"
            show-overflow-tooltip
          />
          <el-table-column 
            prop="amount" 
            label="金额" 
            width="120"
            align="right"
          >
            <template #default="{row}">
              <span class="amount-cell">
                ¥{{ row.amount.toFixed(2) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column 
            prop="status" 
            label="状态" 
            width="120"
            align="center"
          >
            <template #default="{row}">
              <el-tag 
                :type="statusTagType(row.status)"
                :effect="row.status === 'Approved' ? 'dark' : 'plain'"
                size="small"
                class="status-tag"
              >
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column 
            prop="deadline" 
            label="截止时间" 
            width="160"
          >
            <template #default="{row}">
              <div :class="{'expired-text': isExpired(row.deadline)}">
                {{ formatDeadline(row.deadline) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column 
            label="操作" 
            width="150"
            align="center"
          >
            <template #default="{row}">
              <el-upload
                v-if="row.status === 'Approved' && !isExpired(row.deadline)"
                :show-file-list="false"
                :http-request="uploadImage"
                :data="{ requestId: row.requestId }"
              >
                <el-button 
                  type="primary" 
                  size="small"
                  plain
                  class="upload-btn"
                >
                  <i class="el-icon-upload"></i> 上传凭证
                </el-button>
              </el-upload>
              <el-tag 
                v-else-if="isExpired(row.deadline)" 
                type="info"
                effect="plain"
                size="small"
              >
                已超时
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 错误提示 -->
        <div v-if="error" class="error-message">
          <el-alert :title="error" type="error" show-icon></el-alert>
        </div>
      </el-card>
    </div>
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
const loading = ref(false)
const error = ref('')

// 获取申请记录
const fetchRequests = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const { data } = await axios.get(`http://localhost:8081/api/purchase/my-requests/${memberId}`)
    requests.value = data
  } catch (err) {
    error.value = '获取申请记录失败'
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 提交申请
const submitRequest = async () => {
  if (!form.value.itemName || form.value.amount <= 0) {
    ElMessage.warning('请填写完整的申请信息')
    return
  }

  try {
    await axios.post('http://localhost:8081/api/purchase/request', form.value)
    ElMessage.success('申请提交成功')
    form.value.itemName = ''
    form.value.amount = 0
    form.value.description = ''
    fetchRequests()
  } catch (err) {
    ElMessage.error('提交申请失败')
    console.error(err)
  }
}

// 上传图片
const uploadImage = async ({ file, data }) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    await axios.post(`http://localhost:8081/api/purchase/upload/${data.requestId}`, formData)
    ElMessage.success('上传成功')
    fetchRequests()
  } catch (err) {
    ElMessage.error('上传失败')
    console.error(err)
  }
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

// 格式化截止时间
const formatDeadline = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
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

onMounted(fetchRequests)
</script>

<style scoped>
.purchase-container {
  /* max-width: 1200px; */
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  color: #409EFF;
  margin-bottom: 30px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-title i {
  margin-right: 10px;
  font-size: 28px;
}

.grid-layout {
  display: grid;
  grid-template-columns: 1fr 2.3fr;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.card-header h2 {
  color: #303133;
  font-size: 18px;
  display: flex;
  align-items: center;
  margin: 0;
}

.card-header i {
  margin-right: 10px;
  color: #409EFF;
}

.form-card {
  height: fit-content;
}

.purchase-form {
  padding: 0 10px;
}

.amount-input {
  width: 100%;
}

.amount-input :deep(.el-input__inner) {
  padding-left: 24px;
  text-align: right;
}

.currency-symbol {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #606266;
  z-index: 1;
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
}

.refresh-btn {
  padding: 0;
  font-size: 14px;
}

.refresh-btn i {
  margin-right: 5px;
}

.request-table {
  min-width: 100%;
  table-layout: auto;
}

.amount-cell {
  font-weight: 500;
  color: #F56C6C;
}

.status-tag {
  font-weight: 500;
}

.upload-btn {
  padding: 7px 12px;
}

.expired-text {
  color: #909399;
  font-size: 13px;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px 0;
  color: #409EFF;
}

.loading-state i {
  margin-right: 8px;
  font-size: 18px;
}

.error-message {
  margin-top: 15px;
}

@media (max-width: 992px) {
  .grid-layout {
    grid-template-columns: 1fr;
  }
}
</style>