<template>
<div>
            <!-- 参与家庭财务讨论 -->
            <section>
              <el-card>
        <h2>参与家庭财务讨论</h2>
        <!-- 消息输入框 -->
        <div>
          <el-input
            v-model="newMessage"
            placeholder="请输入消息内容"
            type="textarea"
            rows="3"
          ></el-input>
          <el-button
            type="primary"
            @click="sendMessage"
            :loading="sending"
            style="margin-top: 10px;"
          >
            发送消息
          </el-button>
        </div>

        <!-- 消息列表 -->
        <el-divider></el-divider>
        <el-scrollbar style="max-height: 400px; overflow-y: auto;">
          <div v-for="message in messages" :key="message.id" style="margin-bottom: 20px;">
            <el-card shadow="never">
              <div>
                <b>{{ message.userId }}</b> - <i>{{ formatDate(message.createdAt) }}</i>
              </div>
              <p>{{ message.content }}</p>
            </el-card>
          </div>
        </el-scrollbar>
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


const messages = ref([]);
const newMessage = ref("");
const sending = ref(false);

// 格式化时间
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString();
};

// 获取家庭消息
const fetchMessages = async () => {

  try {
  const response = await axios.get(`http://localhost:8081/api/member/findFamilyId/${memberId}`);
  familyID.value = response.data;
 } catch (error) {
  console.error('获取家庭ID失败', error);
 }
  try {
    const response = await axios.get(`http://localhost:8081/api/families/${familyID.value}/messages`);
    messages.value = response.data;
  } catch (error) {
    ElMessage.error("获取消息失败！");
  } 
};

// 发送消息
const sendMessage = async () => {
  if (!newMessage.value.trim()) {
    ElMessage.warning("消息内容不能为空！");
    return;
  }
  sending.value = true;
  try {
    const response = await axios.post(`http://localhost:8081/api/families/${familyID.value}/messages`, {
      userId: memberId, // 当前用户 ID，实际情况从登录状态中获取
      content: newMessage.value.trim(),
    });
    messages.value.push(response.data);
    newMessage.value = "";
    ElMessage.success("消息发送成功！");
  } catch (error) {
    ElMessage.error("消息发送失败！");
  } finally {
    sending.value = false;
  }
};

// 页面加载时获取消息
onMounted(fetchMessages);

</script>