<template>
<div>
  <!-- 发布家庭财务公告和通知 -->
  <section id="announcements">
    <el-card>
      <h2>发布家庭财务公告和通知</h2>

      <!-- 显示已发布的公告 -->
      <el-table :data="announcements" style="width: 100%">
  <el-table-column label="标题" prop="title"></el-table-column>
  <el-table-column label="内容" prop="content"></el-table-column>
  <el-table-column label="操作">
    <template #default="scope">
      <el-button type="danger" size="mini" @click="deleteAnnouncement(scope.row.noticeId)">删除</el-button>
    </template>
  </el-table-column>
</el-table>


      <!-- 发布新公告 -->
      <el-form :model="announcementForm" @submit.prevent="publishAnnouncement">
        <el-form-item label="标题">
          <el-input v-model="announcementForm.title"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="announcementForm.content"></el-input>
        </el-form-item>
        <el-button type="primary" native-type="submit">发布</el-button>
      </el-form>
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

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))

  const adminName = ref(userInfo.name)
const adminId = ref(userInfo.id)

let familyID = ref(null);


const announcements = ref([]);
const announcementForm = ref({

  title: '',
  content: ''
});

// 获取公告列表
const fetchAnnouncements = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}`);
    familyID.value = response.data;
  } catch (error) {
    console.error('获取家庭ID失败', error);
  }

  try {
    const response = await axios.get(`http://localhost:8081/api/notices/family/${familyID.value}`);  // 从后端获取公告列表
    console.log('公告列表:', response.data); // 调试信息
    announcements.value = response.data;
  } catch (error) {
    console.error('无法获取公告列表:', error);
  }
};

// 发布新公告
const publishAnnouncement = async () => {
  if (announcementForm.value.title && announcementForm.value.content) {
    const newAnnouncement = {
      familyId: familyID.value,
      adminId: adminId.value,
      title: announcementForm.value.title,
      content: announcementForm.value.content
    };
    console.log('发布新公告', newAnnouncement)

    try {
      const response = await axios.post('http://localhost:8081/api/notices', newAnnouncement);  // 发布公告到后端
      announcements.value.push(response.data);  // 更新本地公告列表
      // 重置表单
      announcementForm.value.title = '';
      announcementForm.value.content = '';
    } catch (error) {
      console.error('发布公告失败:', error);
    }
  } else {
    alert('标题和内容不能为空');
  }
};

// 删除公告
const deleteAnnouncement = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/notices/${id}`);  // 向后端发送删除请求
    announcements.value = announcements.value.filter(announcement => announcement.id !== id);
    alert('公告已删除');
    await fetchAnnouncements(); // 重新获取公告列表
  } catch (error) {
    console.error('删除公告失败:', error);
  }


};

// 在组件挂载时获取公告列表
onMounted(fetchAnnouncements);
</script>