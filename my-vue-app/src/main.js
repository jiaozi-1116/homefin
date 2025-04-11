import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'
import { ElMessage } from 'element-plus';
import VueLazyload from 'vue3-lazyload';


const app = createApp(App)
app.config.globalProperties.$message = ElMessage;
app.use(ElementPlus)
app.use(router)
// 使用 vue3-lazyload 插件
app.use(VueLazyload, {
    // 加载中的占位图
    loading: 'loading.gif', 
    // 加载失败时显示的图片
    error: 'error.gif' 
  });
app.mount('#app')