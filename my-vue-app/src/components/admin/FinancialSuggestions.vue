<template>
<div>
  <!-- 处理家庭成员的财务建议 -->
  <section id="financial-suggestions">
    <el-card>
      <h2>处理家庭成员的财务建议</h2>
      
      <!-- 建议列表 -->
      <el-table :data="suggestions" style="width: 100%">
        <el-table-column prop="content" label="建议内容" width="300"></el-table-column>
        <el-table-column label="提交人" width="150">
          <template #default="scope">
            {{ getUserName(scope.row.userId) }}
          </template>
        </el-table-column>
        <el-table-column prop="date_submitted"  width="150" ></el-table-column>
        <el-table-column label="状态" width="150">
          <template #default="scope">
            <el-select 
              v-model="scope.row.status" 
              @change="updateSuggestionStatus(scope.row)"
              placeholder="选择状态"
            >
              <el-option label="待处理" value="Pending"></el-option>
              <el-option label="已审核" value="Reviewed"></el-option>
              <el-option label="已实施" value="Implemented"></el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button @click="showDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 建议详情对话框 -->
    <el-dialog v-model="detailVisible" title="建议详情">
      <div v-if="selectedSuggestion">
        <p><strong>提交人:</strong> {{ getUserName(selectedSuggestion.userId) }}</p>
        <p><strong>提交时间:</strong> {{ formatDateFull(selectedSuggestion.dateSubmitted) }}</p>
        <p><strong>建议内容:</strong></p>
        <el-input 
          type="textarea" 
          :rows="4" 
          v-model="selectedSuggestion.content" 
          readonly
        ></el-input>
      </div>
    </el-dialog>
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

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))

  const adminName = ref(userInfo.name)
const adminId = ref(userInfo.id)

const users = ref([]);  // 存储所有用户



const suggestions = ref([]);
const detailVisible = ref(false);
const selectedSuggestion = ref(null);

// 获取建议列表
const fetchSuggestions = async () => {
  try {
    // 先获取家庭ID
    const familyRes = await axios.get(`http://localhost:8081/api/family/${adminId.value}`);
    const familyID = familyRes.data;
    
    // 获取该家庭的所有建议
    const response = await axios.get(`http://localhost:8081/api/suggestion/family/${familyID}`);
    suggestions.value = response.data;
  } catch (error) {
    ElMessage.error('获取建议失败');
    console.error("Error fetching suggestions:", error);
  }
};

// 更新建议状态
const updateSuggestionStatus = async (suggestion) => {
  try {
    await axios.put(`http://localhost:8081/api/suggestion/${suggestion.suggestionID}?status=${suggestion.status}`);
    ElMessage.success('状态已更新');
  } catch (error) {
    ElMessage.error('更新状态失败');
    console.error("Error updating suggestion:", error);
  }
};

// 显示建议详情
const showDetail = (suggestion) => {
  selectedSuggestion.value = suggestion;
  detailVisible.value = true;
};

// 通过UserID获取用户名
const getUserName = (userId) => {
  const user = users.value.find(u => u.userId === userId);
  return user ? user.fullName : '未知用户';
};

// 完整日期格式化
const formatDateFull = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 初始化时获取数据
onMounted(() => {
  fetchSuggestions();
  fetchUsers(); // 确保已获取所有用户数据
});

// 获取所有用户（补充到已有users获取逻辑）
const fetchUsers = async () => {
  try {
    const response = await axios.get('http://localhost:8081/api/users');
    users.value = response.data;
  } catch (error) {
    ElMessage.error('加载用户数据失败');
  }
};
</script>