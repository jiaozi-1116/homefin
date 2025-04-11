<template>
<div>
            <!-- 查看家庭公告和通知 -->
            <section>
              <el-card>
                <h2>查看家庭公告和通知</h2>
                <el-table :data="announcements" style="width: 100%">
                  <el-table-column prop="title" label="标题" width="180"></el-table-column>
                  <el-table-column prop="content" label="内容"></el-table-column>
                </el-table>
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



const error = ref(''); 
const getFamilyID = async () => { 
  try { 
    const response = await axios.get(`http://localhost:8081/api/member/findFamilyId/${memberId}`); 
    familyID.value = response.data; 
    return response.data;
    error.value = ''; 
  } catch (err) { 
    error.value = 'Error retrieving Family ID'; 
    console.error(err); 
    return null;
  } 
};

const announcements = ref([
  { 
    title: '', 
    content: '' 
  },
  ])



//获取已有的公告 
onMounted(async () => { 
  const familyID = await getFamilyID(); 
  console.log('familyID:',familyID)
  if (familyID !== null) { 
    try { 
      const response = await axios.get(`http://localhost:8081/api/notices/family/${familyID}`); 
      announcements.value = response.data; 
    } catch (error) { 
      console.error("Error fetching notices:", error); 
    } 
  } else { 
    console.error("Family ID is null, cannot fetch notices."); 
  } 
});



</script>