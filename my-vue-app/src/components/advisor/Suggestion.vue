<template>
  <div class="suggestion-container">
    <!-- 新建建议表单 -->
    <div class="create-section">
      <h2>发布新建议</h2>
      <form @submit.prevent="submitSuggestion">
        <div class="form-group">
          <label>标题：</label>
          <input v-model="newSuggestion.title" type="text" required />
        </div>
        <div class="form-group">
          <label>内容：</label>
          <textarea v-model="newSuggestion.content" required></textarea>
        </div>
        <button type="submit" class="submit-btn">提交建议</button>
      </form>
    </div>

    <!-- 建议列表 -->
    <div class="list-section">
      <h2>已发布的建议</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else>
        <div v-if="suggestions.length === 0" class="empty">暂无建议</div>
        <div v-for="suggestion in suggestions" :key="suggestion.id" class="suggestion-card">
          <h3>{{ suggestion.title }}</h3>
          <p class="content">{{ suggestion.content }}</p>
          <div class="meta">
            <span>状态：{{ suggestion.status || '待处理' }}</span>
            <span>日期：{{ formatDate(suggestion.dateSubmitted) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const userInfo = JSON.parse(localStorage.getItem('userInfo'))

// 用户ID（需根据实际登录状态获取）
const currentUserId = ref(userInfo.id) // 示例ID，实际应从登录状态获取

// 新建建议数据
const newSuggestion = ref({
  title: '',
  content: '',
  userId: currentUserId.value
})

// 建议列表数据
const suggestions = ref([])
const loading = ref(true)

// 获取用户建议
const fetchSuggestions = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/suggestion/member/${currentUserId.value}`)
    suggestions.value = response.data
  } catch (error) {
    console.error('获取建议失败:', error)
    alert('获取建议列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 提交新建议
const submitSuggestion = async () => {
  try {
    const response = await axios.post('http://localhost:8081/api/suggestion/member', newSuggestion.value)
    suggestions.value.unshift(response.data) // 将新建议添加到列表顶部
    newSuggestion.value = { title: '', content: '', userId: currentUserId.value } // 清空表单
    alert('建议提交成功！')
  } catch (error) {
    console.error('提交失败:', error)
    alert('建议提交失败，请检查网络后重试')
  }
}

// 日期格式化
const formatDate = (timestamp) => {
  if (!timestamp) return '未知日期'
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
}

// 初始化时加载数据
onMounted(fetchSuggestions)
</script>

<style scoped>
.suggestion-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
  display: grid;
  gap: 40px;
}

.create-section, .list-section {
  background: #fff;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input, textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

textarea {
  height: 120px;
}

.submit-btn {
  background: #409eff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.suggestion-card {
  margin: 15px 0;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 6px;
}

.meta {
  margin-top: 10px;
  color: #666;
  font-size: 0.9em;
  display: flex;
  justify-content: space-between;
}

.loading, .empty {
  text-align: center;
  padding: 20px;
  color: #888;
}
</style>