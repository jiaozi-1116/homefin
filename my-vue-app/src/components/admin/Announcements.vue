<template>
  <div class="announcement-container">
    <!-- 页面标题 -->
    <h1 class="page-title">
      <i class="el-icon-message-solid"></i> 家庭公告管理
    </h1>

    <div class="grid-layout">
      <!-- 发布公告表单 -->
      <el-card shadow="hover" class="form-card">
        <div class="card-header">
          <h2><i class="el-icon-edit"></i> 发布新公告</h2>
        </div>
        <el-form 
          :model="announcementForm" 
          @submit.prevent="publishAnnouncement"
          label-position="top"
          class="announcement-form"
        >
          <el-form-item label="公告标题" required>
            <el-input 
              v-model="announcementForm.title" 
              placeholder="请输入公告标题"
              clearable
              maxlength="30"
              show-word-limit
            >
              <template #prefix>
                <i class="el-icon-edit-outline"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="公告内容" required>
            <el-input 
              v-model="announcementForm.content" 
              type="textarea"
              :rows="5"
              placeholder="请输入公告内容"
              resize="none"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-button 
            type="primary" 
            native-type="submit"
            :disabled="!announcementForm.title || !announcementForm.content"
            :loading="publishing"
            class="publish-btn"
          >
            <i class="el-icon-upload"></i> 发布公告
          </el-button>
        </el-form>
      </el-card>

      <!-- 公告列表 -->
      <el-card shadow="hover" class="list-card">
        <div class="card-header">
          <h2><i class="el-icon-tickets"></i> 公告列表</h2>
          <el-button 
            type="text" 
            @click="fetchAnnouncements"
            class="refresh-btn"
          >
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>

        <!-- 空状态 -->
        <el-empty 
          v-if="announcements.length === 0 && !loading" 
          description="暂无公告"
          :image-size="100"
        ></el-empty>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-icon class="is-loading"><i class="el-icon-loading"></i></el-icon>
          <span>加载中...</span>
        </div>

        <!-- 公告表格 -->
        <el-table 
          v-if="announcements.length > 0 && !loading"
          :data="announcements" 
          style="width: 100%"
          stripe
          border
          v-loading="loading"
          class="announcement-table"
        >
          <el-table-column 
            prop="title" 
            label="标题" 
            min-width="150"
            show-overflow-tooltip
          />

          <el-table-column 
            prop="content" 
            label="内容" 
            min-width="250"
            show-overflow-tooltip
          />

          <el-table-column 
            prop="createTime" 
            label="发布时间" 
            width="180"
            :formatter="formatDate"
          />

          <el-table-column 
            label="操作" 
            width="120"
            align="center"
          >
            <template #default="{row}">
              <el-button 
                type="danger" 
                size="small" 
                plain
                @click="confirmDelete(row.noticeId)"
                class="delete-btn"
              >
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const userInfo = JSON.parse(localStorage.getItem('userInfo'))
const adminName = ref(userInfo.name)
const adminId = ref(userInfo.id)
const familyID = ref(null)

const announcements = ref([])
const loading = ref(false)
const publishing = ref(false)

const announcementForm = ref({
  title: '',
  content: ''
})

// 格式化日期
const formatDate = (row, column, cellValue) => {
  return dayjs(cellValue).format('YYYY-MM-DD HH:mm')
}

// 获取家庭ID
const fetchFamilyId = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}`)
    familyID.value = response.data
  } catch (error) {
    console.error('获取家庭ID失败', error)
    ElMessage.error('获取家庭信息失败')
  }
}

// 获取公告列表
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    await fetchFamilyId()
    if (familyID.value) {
      const response = await axios.get(`http://localhost:8081/api/notices/family/${familyID.value}`)
      announcements.value = response.data
    }
  } catch (error) {
    console.error('无法获取公告列表:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

// 发布新公告
const publishAnnouncement = async () => {
  if (!announcementForm.value.title || !announcementForm.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }

  publishing.value = true
  try {
    const newAnnouncement = {
      familyId: familyID.value,
      adminId: adminId.value,
      title: announcementForm.value.title,
      content: announcementForm.value.content
    }

    const response = await axios.post('http://localhost:8081/api/notices', newAnnouncement)
    announcements.value.unshift(response.data)
    
    // 重置表单
    announcementForm.value.title = ''
    announcementForm.value.content = ''
    
    ElMessage.success('公告发布成功')
  } catch (error) {
    console.error('发布公告失败:', error)
    ElMessage.error('发布公告失败')
  } finally {
    publishing.value = false
  }
}

// 确认删除公告
const confirmDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条公告吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAnnouncement(id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除操作异常:', error)
    }
  }
}

// 删除公告
const deleteAnnouncement = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/notices/${id}`)
    announcements.value = announcements.value.filter(announcement => announcement.noticeId !== id)
    ElMessage.success('公告已删除')
  } catch (error) {
    console.error('删除公告失败:', error)
    ElMessage.error('删除公告失败')
  }
}

onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announcement-container {
  max-width: 1200px;
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
  grid-template-columns: 1fr 2fr;
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

.announcement-form {
  padding: 0 10px;
}

.publish-btn {
  width: 100%;
  margin-top: 10px;
  padding: 12px;
}

.refresh-btn {
  padding: 0;
  font-size: 14px;
}

.refresh-btn i {
  margin-right: 5px;
}

.announcement-table {
  margin-top: 10px;
}

.delete-btn {
  padding: 7px 12px;
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

@media (max-width: 992px) {
  .grid-layout {
    grid-template-columns: 1fr;
  }
}
</style>