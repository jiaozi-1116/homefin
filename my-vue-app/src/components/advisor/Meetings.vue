<template>
    <div class="meetings-container">
      <el-card class="message-box">
        <div class="message-list">
          <div v-for="message in messages" :key="message.id" class="message-item">
            <div class="message-header">
              <span class="user-id">用户{{ message.user_id }}</span>
              <span class="message-time">{{ formatDate(message.created_at) }}</span>
            </div>
            <div class="message-content">{{ message.content }}</div>
          </div>
        </div>
  
        <el-divider></el-divider>
  
        <el-form @submit.prevent="submitMessage">
          <el-input
            v-model="newMessage"
            type="textarea"
            :rows="3"
            placeholder="输入您的发言..."
            resize="none"
          ></el-input>
          <div class="submit-area">
            <el-button type="primary" native-type="submit" :loading="sending">
              发送消息
            </el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, watch } from 'vue'
  import axios from 'axios'
  import { ElMessage } from 'element-plus'

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  
  const props = defineProps({
    familyId: {
      type: Number,
      required: true
    }
  })
  
  const messages = ref([])
  const newMessage = ref('')
  const sending = ref(false)
  
  // 获取消息列表
  const fetchMessages = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8081/api/families/${props.familyId}/messages`
      )
      messages.value = response.data.sort((a, b) => 
        new Date(b.created_at) - new Date(a.created_at)
      )
    } catch (error) {
      ElMessage.error('获取消息失败')
    }
  }
  
  // 提交新消息
  const submitMessage = async () => {
    if (!newMessage.value.trim()) return
  
    sending.value = true
    try {
      await axios.post(
        `http://localhost:8081/api/families/${props.familyId}/messages`,
        {
          userId: userInfo.id, // 需替换为实际用户ID获取逻辑
          content: newMessage.value.trim()
        }
      )
      newMessage.value = ''
      await fetchMessages()
      ElMessage.success('发送成功')
    } catch (error) {
      ElMessage.error('发送失败')
    } finally {
      sending.value = false
    }
  }
  
  // 日期格式化
  const formatDate = (dateString) => {
    const options = { 
      year: 'numeric', 
      month: '2-digit', 
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }
    return new Date(dateString).toLocaleString('zh-CN', options)
  }
  
  // 监听familyId变化
  watch(() => props.familyId, (newVal) => {
    if (newVal) fetchMessages()
  }, { immediate: true })
  </script>
  
  <style scoped>
  .meetings-container {
    padding: 20px;
    max-width: 800px;
    margin: 0 auto;
  }
  
  .message-box {
    margin-top: 20px;
  }
  
  .message-list {
    max-height: 500px;
    overflow-y: auto;
    padding: 10px;
  }
  
  .message-item {
    margin-bottom: 15px;
    padding: 10px;
    border-bottom: 1px solid #eee;
  }
  
  .message-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;
    font-size: 0.9em;
    color: #666;
  }
  
  .message-content {
    font-size: 1em;
    line-height: 1.5;
  }
  
  .submit-area {
    margin-top: 15px;
    display: flex;
    justify-content: flex-end;
  }
  </style>