<template>
<div>
            <!-- 管理个人信息和账户 -->
            <section>
              <el-card>
                <h2>管理个人信息和账户</h2>
                <el-form :model="accountForm" @submit.prevent="saveAccount">
                  <el-form-item label="用户名">
                    <el-input v-model="accountForm.username"></el-input>
                  </el-form-item>
                  <el-form-item label="密码">
                    <el-input v-model="accountForm.password" type="password"></el-input>
                  </el-form-item>
                  <el-form-item label="全名">
                    <el-input v-model="accountForm.fullName" type="fullName"></el-input>
                  </el-form-item>

                  <el-form-item label="邮箱">
                    <el-input v-model="accountForm.email" type="email"></el-input>
                  </el-form-item>
                  <el-form-item label="电话号码">
                    <el-input v-model="accountForm.phoneNumber" type="tel"></el-input>
                  </el-form-item>
                  <el-button type="primary" native-type="submit">保存</el-button>
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
  import { nextTick } from 'vue';

const userInfo = JSON.parse(localStorage.getItem('userInfo'))

const memberId = userInfo.id

const familyID = ref(null); 


const accountForm = ref({
    username: '',
    password: '',
    fullName: '',
    email: '',
    phoneNumber: ''
  })


  // 获取用户信息并填充到表单中 
  const fetchUserInfo = async () => { try { 
    const response = await axios.get(`http://localhost:8081/api/users/${memberId}`); 
    accountForm.value = response.data; 
  } catch (error) { 
    console.error("Error fetching user info:", error); 
  } 
};


  const saveAccount = async () => { 
    try { 
      const response = await axios.put(`http://localhost:8081/api/users/${memberId}`, accountForm.value); 
      console.log("Account saved:", response.data); 
      ElMessage.success('账户保存成功');
    } catch (error) { 
      console.error("Error saving account:", error); 
      ElMessage.error('保存账户信息时发生错误');
    } 
  };

  onMounted(() => { 
    fetchUserInfo(); // 加载组件时获取用户信息 
    });

</script>