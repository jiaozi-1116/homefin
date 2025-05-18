<template>
  <div class="notice-container">
    <!-- 主标题 -->
    <h1 class="page-title">
      <i class="el-icon-message-solid"></i> 家庭公告与通知
    </h1>
    
    <!-- 公告卡片 -->
    <el-card shadow="hover" class="notice-card">
      <div class="card-header">
        <h2><i class="el-icon-bell"></i> 最新公告</h2>
        <el-button 
          type="text" 
          @click="refreshNotices"
          class="refresh-btn"
        >
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>
      
      <!-- 空状态 -->
      <el-empty 
        v-if="announcements.length === 0 && !isLoading" 
        description="暂无公告"
        :image-size="100"
      ></el-empty>
      
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-state">
        <el-icon class="is-loading"><i class="el-icon-loading"></i></el-icon>
        <span>加载中...</span>
      </div>
      
      <!-- 公告列表 -->
      <div v-if="announcements.length > 0 && !isLoading">
        <el-timeline>
          <el-timeline-item
            v-for="(notice, index) in announcements"
            :key="index"
            :timestamp="formatDate(notice.createTime)"
            placement="top"
            :color="index === 0 ? '#409EFF' : ''"
            :hide-timestamp="index > 0 && sameDay(notice.createTime, announcements[index-1].createTime)"
          >
            <el-card shadow="never" class="notice-item">
              <div class="notice-header">
                <h3 class="notice-title">
                  <i class="el-icon-document"></i> {{ notice.title }}
                </h3>
                <el-tag 
                  v-if="index === 0" 
                  type="primary" 
                  size="small"
                  effect="dark"
                >
                  最新
                </el-tag>
              </div>
              <div class="notice-content">
                {{ notice.content }}
              </div>
              <div class="notice-footer" v-if="notice.publisher">
                <span class="publisher">
                  <i class="el-icon-user"></i> 发布人: {{ notice.publisher }}
                </span>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
      
      <!-- 错误提示 -->
      <div v-if="error" class="error-message">
        <el-alert :title="error" type="error" show-icon></el-alert>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const userInfo = JSON.parse(localStorage.getItem('userInfo'))
const memberId = userInfo.id
const familyID = ref(null)
const announcements = ref([])
const isLoading = ref(false)
const error = ref('')

// 格式化日期
const formatDate = (dateString) => {
  return dayjs(dateString).format('YYYY-MM-DD HH:mm')
}

// 判断是否同一天
const sameDay = (date1, date2) => {
  return dayjs(date1).isSame(date2, 'day')
}

// 获取家庭ID
const getFamilyID = async () => { 
  try { 
    const response = await axios.get(`http://localhost:8081/api/member/findFamilyId/${memberId}`)
    familyID.value = response.data
    error.value = ''
    return response.data
  } catch (err) { 
    error.value = '获取家庭ID失败'
    console.error(err)
    return null
  } 
}

// 获取公告列表
const fetchNotices = async () => {
  isLoading.value = true
  error.value = ''
  
  try {
    const familyID = await getFamilyID()
    if (familyID !== null) { 
      const response = await axios.get(`http://localhost:8081/api/notices/family/${familyID}`)
      announcements.value = response.data
    }
  } catch (err) { 
    error.value = '获取公告失败，请稍后重试'
    console.error("Error fetching notices:", err)
  } finally {
    isLoading.value = false
  }
}

// 刷新公告
const refreshNotices = async () => {
  await fetchNotices()
  ElMessage.success('公告已刷新')
}

// 初始化加载
onMounted(() => {
  fetchNotices()
})
</script>

<style scoped>
.notice-container {
  max-width: 1000px;
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

.notice-card {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.notice-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
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

.refresh-btn {
  padding: 0;
  font-size: 14px;
}

.refresh-btn i {
  margin-right: 5px;
}

.notice-item {
  margin-bottom: 10px;
  border-radius: 6px;
  border-left: 3px solid #409EFF;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.notice-title {
  margin: 0;
  font-size: 16px;
  color: #303133;
  display: flex;
  align-items: center;
}

.notice-title i {
  margin-right: 8px;
  color: #67C23A;
}

.notice-content {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 10px;
}

.notice-footer {
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.notice-footer i {
  margin-right: 5px;
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

/* 时间线样式调整 */
:deep(.el-timeline-item__timestamp) {
  color: #606266;
  font-size: 13px;
}

:deep(.el-timeline-item__node) {
  width: 14px;
  height: 14px;
}

:deep(.el-timeline-item__tail) {
  border-left-width: 2px;
}
</style>