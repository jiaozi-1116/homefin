<template>
    <div>
        <!-- ---------------------------------------------------管理家庭成员信息 ----------------------------------------->
        <section id="manage-members" class="manage-members-section">
            <el-card>
            <h2>管理家庭成员信息</h2>
            <el-table :data="members" style="width: 100%">
                <el-table-column prop="name" label="姓名" width="180"></el-table-column>
                <el-table-column prop="email" label="电子邮箱" width="220"></el-table-column>
                <el-table-column prop="phone" label="电话" width="180"></el-table-column>
                <el-table-column prop="dateRegistered" label="注册日期" width="180" :formatter="formatDate"></el-table-column>
                <el-table-column prop="relationship" label="关系" width="180"></el-table-column>
                <el-table-column label="操作">
                <template #default="scope">
                    <el-button @click="editMember(scope.row)">编辑</el-button>
                    <el-button type="danger" @click="deleteMember(scope.row)">删除</el-button>
                </template>
                </el-table-column>
            </el-table>
            <el-button @click="addMember">添加成员</el-button>
            </el-card>
        </section>
        </div>

         <!-- 添加成员的弹出框 -->
  <el-dialog v-model="dialogVisible" title="添加家庭成员">
    <el-form :model="form" label-width="120px">
      <el-form-item label="选择成员">
        <el-select v-model="form.userId" placeholder="请选择成员">
          <el-option
            v-for="user in users"
            :key="user.userId"
            :label="user.fullName"
            :value="user.userId"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="关系">
        <el-input v-model="form.relationship" placeholder="请输入关系"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveMember">确定</el-button>
    </span>
  </el-dialog>
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

  const router = useRouter()
  const route = useRoute()
// 使用 ref 来存储 adminId 和 adminName


const userInfo = JSON.parse(localStorage.getItem('userInfo'))

const adminName = ref('')
const adminId = ref(userInfo.id)

  const members = ref([])
  //获取家庭成员
  const fetchFamilyMembers = async () => { 
    try { 
      const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}/members`) // 假设家庭管理员ID为1 
        members.value = response.data 
    } catch (error) { 
          console.error("Error fetching family members:", error) 
      } 
  }

    onMounted(() => { 
        fetchFamilyMembers() 
    })

    const formatDate = (row, column, cellValue) => { 
      const date = new Date(cellValue); 
      const year = date.getFullYear(); 
      const month = (date.getMonth() + 1).toString().padStart(2, '0'); 
      const day = date.getDate().toString().padStart(2, '0'); 
      return `${year}-${month}-${day}`; 
    }


  //----------------------------------------------------添加家庭成员---------------------------------------------
  const users = ref([]);  // 存储所有用户


  const dialogVisible = ref(false);
  const form = ref({
  userId: '',
  relationship: ''
});

 let familyID = ref(null);

// 获取所有用户
onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8081/api/users');
    users.value = response.data;
  } catch (error) {
    ElMessage.error('加载用户数据失败');
  }

   // 获取家庭ID
   try {
    const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}`);
    familyID.value = response.data;
  } catch (error) {
    console.error('获取家庭ID失败', error);
  }
});


  
  const addMember = () => {
    // 添加成员逻辑
    dialogVisible.value = true;
  }


  // 保存新的家庭成员
const saveMember = async () => {
  try {
    const response = await axios.post(`http://localhost:8081/api/family/${familyID.value}/addMember`, {
      userId: form.value.userId,
      relationship: form.value.relationship
    });

    if (response.data === '家庭成员添加成功') {
      ElMessage.success('家庭成员添加成功');
      dialogVisible.value = false;
      // 调用已有的 fetchFamilyMembers 方法来更新家庭成员数据
      await fetchFamilyMembers();  // 获取并更新家庭成员列表
    }
  } catch (error) {
    ElMessage.error('添加成员失败');
  }
};

//-------------------------------------------编辑家庭成员----------------------------------------------------------
  
  // 跳转到编辑页面
  const editMember = (members) => {
    router.push({ name: 'EditMember', params: { id: members.id } });
  };

 //----------------------------------------------删除家庭成员----------------------------------------------------
  
 const deleteMember = async (member) => {
  try {
    // 弹出确认框，询问是否确认删除成员
    const result = await ElMessageBox.confirm(
      '确定要删除此家庭成员吗？', 
      '提示', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    // 如果用户点击“确定”，继续删除操作
    if (result === 'confirm') {
      // 调用后端 API 删除家庭成员，传递 userId 作为唯一标识符
      const response = await axios.delete(`http://localhost:8081/api/family/deleteMember`, {
        data: { userId: member.id }  // 传递要删除的成员的 userId
      });

      if (response.data === '删除成功') {
        ElMessage.success('家庭成员删除成功');
        
        // 从本地列表中移除该成员
        // members.value = members.value.filter(item => item.userId !== member.userId);
        // 调用已有的 fetchFamilyMembers 方法来更新家庭成员数据
        await fetchFamilyMembers();  // 获取并更新家庭成员列表
      } else {
        ElMessage.error('删除失败');
      }
    }
  } catch (error) {
    // 如果用户点击“取消”或发生其他错误，弹出提示
    if (error === 'cancel') {
      ElMessage.info('删除操作已取消');
    } else {
      console.error('删除家庭成员时发生错误:', error);
      ElMessage.error('删除失败');
    }
  }
};

</script>