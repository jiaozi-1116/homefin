<template>
<div>
    <!-- 提交财务建议和反馈 -->
    <section>
      <el-card>
        <h2>提交财务建议和反馈</h2>
        <el-form :model="suggestionForm" @submit.prevent="submitSuggestion">
          <!-- <el-form-item label="标题">
            <el-input v-model="suggestionForm.title"></el-input>
          </el-form-item> -->
          <el-form-item label="内容">
            <el-input type="textarea" v-model="suggestionForm.content"></el-input>
          </el-form-item>
          <el-button type="primary" native-type="submit">提交</el-button>
        </el-form>
      </el-card>
    </section>

    <!-- 我的建议 -->
    <section id="my-suggestions">
      <el-card v-if="existingSuggestions.length > 0">
        <h3>我的建议</h3>
        <el-list>
          <el-list-item v-for="(suggestion, index) in existingSuggestions" :key="index">
            <!-- <h4>标题：{{ suggestion.title }}</h4> -->
            <p>内容：{{ suggestion.content }}</p>
            <small>提交时间：{{ suggestion.dateSubmitted }}</small>
            <p>-----------------------------------------------------------------------------</p>
          </el-list-item>
        </el-list>
      </el-card>
      <el-card v-else>
        <p>您还没有提交过任何建议。</p>
      </el-card>
    </section>

    <section>
      <el-card>
        <h2>AI财务助手</h2>
        <el-form @submit.prevent="getAIAnswer">
          <el-form-item label="请输入财务问题">
            <el-input 
              type="textarea" 
              v-model="aiQuestion"
              placeholder="例如：如何合理规划家庭预算？"
            ></el-input>
          </el-form-item>
          <el-button type="primary" native-type="submit">获取解答</el-button>
        </el-form>
        
        <div v-if="aiAnswer" class="ai-answer">
          <h3>AI解答：</h3>
          <div class="answer-content">{{ aiAnswer }}</div>
        </div>
        
        <div v-if="isLoading" class="loading">解答生成中...</div>
        <div v-if="error" class="error">{{ error }}</div>
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
  // title: '',
  content: ''  // 标题和内容
});
  
const existingSuggestions = ref([]);  // 存储从 API 获取的已有建议

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
    // 提交新的建议，传递标题和内容
    const response = await axios.post('http://localhost:8081/api/suggestion/member', {
      
      userId:memberId,  // 提交建议的 memberId
      familyId:familyID.value,
      // title: suggestionForm.value.title,
      content: suggestionForm.value.content
    });

    // console.log(familyId);  // 确认是否为 null


    // 如果提交成功，重新获取最新的建议列表
    if (response.status === 200) {
      suggestionForm.value.title = '';
      suggestionForm.value.content = '';
      const updatedResponse = await axios.get(`http://localhost:8081/api/suggestion/member/${memberId}`);
      existingSuggestions.value = updatedResponse.data;
    }
  } catch (error) {
    console.error("Error submitting suggestion:", error);
  }
};

// 新增响应式数据
const aiQuestion = ref('');
const aiAnswer = ref('');
const isLoading = ref(false);
const error = ref('');

// 新增获取AI解答方法
const getAIAnswer = async () => {
  if (!aiQuestion.value.trim()) {
    ElMessage.warning('请输入问题内容');
    return;
  }

  try {
    isLoading.value = true;
    error.value = '';
    
    const response = await axios.get(
      `http://localhost:8081/api/suggestion/AISuggestion/${encodeURIComponent(aiQuestion.value)}`
    );
    
    // 解析API响应（根据实际返回格式调整）
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
.ai-answer {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
}

.answer-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

.loading {
  color: #409eff;
  margin-top: 10px;
}

.error {
  color: #f56c6c;
  margin-top: 10px;
}
</style>