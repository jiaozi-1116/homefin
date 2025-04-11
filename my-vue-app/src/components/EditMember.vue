<template>
    <div>
      <el-form :model="member" ref="memberForm">
        <el-form-item label="姓名">
          <el-input v-model="member.name"></el-input>
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="member.email"></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="member.phone"></el-input>
        </el-form-item>
        <el-form-item label="注册日期">
          <el-date-picker v-model="member.dateRegistered" type="date" format="yyyy-MM-dd"></el-date-picker>
        </el-form-item>
        <el-form-item label="关系">
          <el-input v-model="member.relationship"></el-input>
        </el-form-item>
        <el-button @click="saveMember">保存</el-button>

        <el-button @click="goBack" type="default">返回</el-button>
      </el-form>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import axios from 'axios'
  import { useRouter } from 'vue-router';
  const router = useRouter();
  
  const route = useRoute() // 获取路由信息
  const memberId = route.params.id // 获取成员的 ID 参数
  const member = ref({
    id: '',
    name: '',
    email: '',
    phone: '',
    dateRegistered: '',
    relationship: '',
  })
  
  // 模拟获取成员数据的 API 调用
  const getMemberById = async (id) => {
    try {
      const response = await axios.get(`http://localhost:8081/api/family/members/${id}`)
      member.value = response.data // 将获取的数据存入 member
    } catch (error) {
      console.error('Error fetching member data:', error)
    }
  }
  
  // 页面加载时获取成员信息
  onMounted(() => {
    if (memberId) {
      getMemberById(memberId) // 根据 ID 获取成员数据
    }
  })
  
  // 保存成员数据
  const saveMember = async () => {
    if (!member.value.id) {
    console.error("ID 缺失，无法保存数据");
    return;
  }
    try {
      const response = await axios.put(`http://localhost:8081/api/family/members/${member.value.id}`, member.value)
      console.log("保存的成员信息:", response.data)
      router.back();
    } catch (error) {
      console.error("Error saving member data:", error)
    }
  }

  const goBack = () => {
    router.back();  // 返回上一页
};


  </script>
  