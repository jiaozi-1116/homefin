<template>
  <div class="suggestion-container">
    <!-- 主标题 -->
    <h1 class="page-title">财务建议与AI助手</h1>
    
    <div class="grid-layout">
      <!-- 提交财务建议和反馈 -->
      <section class="card-section">
        <el-card shadow="hover" class="form-card">
          <div class="card-header">
            <h2><i class="el-icon-edit"></i> 提交财务建议和反馈</h2>
          </div>
          <el-form :model="suggestionForm" @submit.prevent="submitSuggestion">
            <el-form-item label="内容">
              <el-input 
                type="textarea" 
                v-model="suggestionForm.content"
                :rows="4"
                placeholder="请输入您的宝贵建议..."
                resize="none"
              ></el-input>
            </el-form-item>
            <el-button 
              type="primary" 
              native-type="submit"
              :disabled="!suggestionForm.content.trim()"
            >
              <i class="el-icon-upload"></i> 提交
            </el-button>
          </el-form>
        </el-card>
      </section>

      <!-- AI财务助手 -->
      <section class="card-section">
        <el-card shadow="hover" class="form-card">
          <div class="card-header">
            <h2><i class="el-icon-cpu"></i> AI财务助手</h2>
          </div>
          <el-form @submit.prevent="getAIAnswer">
            <el-form-item label="请输入财务问题">
              <el-input 
                type="textarea" 
                v-model="aiQuestion"
                :rows="4"
                placeholder="例如：如何合理规划家庭预算？"
                resize="none"
              ></el-input>
            </el-form-item>
            <el-button 
              type="primary" 
              native-type="submit"
              :disabled="!aiQuestion.trim()"
            >
              <i class="el-icon-chat-line-round"></i> 获取解答
            </el-button>
          </el-form>
          
          <div v-if="isLoading" class="loading-state">
            <el-icon class="is-loading"><i class="el-icon-loading"></i></el-icon>
            <span>AI正在思考中...</span>
          </div>
          
          <div v-if="error" class="error-message">
            <el-alert :title="error" type="error" show-icon></el-alert>
          </div>
          
          <div v-if="aiAnswer" class="ai-answer">
            <h3 class="answer-title"><i class="el-icon-chat-line-square"></i> AI解答：</h3>
            <div class="answer-content">{{ aiAnswer }}</div>
          </div>
        </el-card>
      </section>
    </div>

    <!-- 我的建议 -->
    <section class="history-section">
      <el-card shadow="hover">
        <div class="card-header">
          <h2><i class="el-icon-collection"></i> 我的建议历史</h2>
        </div>
        <div v-if="existingSuggestions.length > 0">
          <div 
            v-for="(suggestion, index) in existingSuggestions" 
            :key="index" 
            class="suggestion-item"
          >
            <div class="suggestion-content">
              <p>{{ suggestion.content }}</p>
              <div class="suggestion-meta">
                <span class="date">
                  <i class="el-icon-time"></i> {{ suggestion.dateSubmitted }}
                </span>
              </div>
            </div>
            <el-divider v-if="index < existingSuggestions.length - 1"></el-divider>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-empty description="您还没有提交过任何建议"></el-empty>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus'
import * as echarts from 'echarts';
import { nextTick } from 'vue';

const userInfo = JSON.parse(localStorage.getItem('userInfo'))
const memberId = userInfo.id
const familyID = ref(null); 

const suggestionForm = ref({
  content: ''
});

const existingSuggestions = ref([]);

// 获取已有的建议数据
onMounted(async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/suggestion/member/${memberId}`);
    existingSuggestions.value = response.data;
  } catch (error) {
    console.error("Error fetching notices:", error);
  }
});

// 提交新的建议
const submitSuggestion = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/member/findFamilyId/${memberId}`);
    familyID.value = response.data;
  } catch (error) {
    console.error('获取家庭ID失败', error);
  }
  
  try {
    const response = await axios.post('http://localhost:8081/api/suggestion/member', {
      userId: memberId,
      familyId: familyID.value,
      content: suggestionForm.value.content
    });

    if (response.status === 200) {
      ElMessage.success('建议提交成功！');
      suggestionForm.value.content = '';
      const updatedResponse = await axios.get(`http://localhost:8081/api/suggestion/member/${memberId}`);
      existingSuggestions.value = updatedResponse.data;
    }
  } catch (error) {
    console.error("Error submitting suggestion:", error);
    ElMessage.error('提交失败，请重试');
  }
};

// AI助手相关
const aiQuestion = ref('');
const aiAnswer = ref('');
const isLoading = ref(false);
const error = ref('');

const getAIAnswer = async () => {
  if (!aiQuestion.value.trim()) {
    ElMessage.warning('请输入问题内容');
    return;
  }

  try {
    isLoading.value = true;
    error.value = '';
    aiAnswer.value = '';
    
    const response = await axios.get(
      `http://localhost:8081/api/suggestion/AISuggestion/${encodeURIComponent(aiQuestion.value)}`
    );
    
    const responseData = JSON.parse(response.data);
    aiAnswer.value = responseData.choices[0].message.content;
    
  } catch (err) {
    console.error('获取AI解答失败:', err);
    error.value = '获取解答失败，请稍后重试';
    ElMessage.error('请求失败');
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.suggestion-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  color: #409EFF;
  margin-bottom: 30px;
  font-weight: 500;
}

.grid-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.card-section {
  margin-bottom: 20px;
}

.form-card {
  height: 100%;
  transition: all 0.3s ease;
}

.form-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.card-header h2 {
  color: #303133;
  font-size: 18px;
  display: flex;
  align-items: center;
}

.card-header i {
  margin-right: 10px;
  color: #409EFF;
}

.history-section {
  margin-top: 20px;
}

.suggestion-item {
  padding: 15px 0;
}

.suggestion-content p {
  margin-bottom: 10px;
  line-height: 1.6;
  color: #606266;
}

.suggestion-meta {
  display: flex;
  justify-content: flex-end;
  font-size: 12px;
  color: #909399;
}

.suggestion-meta i {
  margin-right: 5px;
}

.ai-answer {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #409EFF;
}

.answer-title {
  color: #409EFF;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.answer-title i {
  margin-right: 8px;
}

.answer-content {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #606266;
}

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px;
  color: #409EFF;
}

.loading-state i {
  margin-right: 8px;
  font-size: 18px;
}

.error-message {
  margin-top: 15px;
}

.empty-state {
  padding: 40px 0;
}

.el-button {
  width: 100%;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .grid-layout {
    grid-template-columns: 1fr;
  }
}
</style>