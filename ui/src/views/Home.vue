<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <h1>欢迎使用 RideNow</h1>
        <p>随时随地，轻松出行</p>
        <div class="hero-buttons">
          <el-button type="primary" size="large" @click="goToRide" :icon="Bicycle">
            立即叫车
          </el-button>
          <el-button size="large" @click="goToDriver" :icon="Van">
            成为司机
          </el-button>
        </div>
      </div>
    </div>

    <div class="features-section">
      <div class="section-header">
        <h2>为什么选择 RideNow</h2>
        <p>我们为您提供最优质的出行服务</p>
      </div>
      
      <div class="features-grid">
        <el-card class="feature-card" v-for="(feature, index) in features" :key="index">
          <div class="feature-icon">
            <el-icon :size="40" :color="feature.color">
              <component :is="feature.icon" />
            </el-icon>
          </div>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.description }}</p>
        </el-card>
      </div>
    </div>

    <div class="stats-section">
      <div class="stats-container">
        <div class="stat-item">
          <h3>{{ stats.drivers }}</h3>
          <p>活跃司机</p>
        </div>
        <div class="stat-item">
          <h3>{{ stats.passengers }}</h3>
          <p>注册乘客</p>
        </div>
        <div class="stat-item">
          <h3>{{ stats.rides }}</h3>
          <p>日均订单</p>
        </div>
        <div class="stat-item">
          <h3>{{ stats.rating }}</h3>
          <p>平均评分</p>
        </div>
      </div>
    </div>

    <div class="download-section">
      <div class="section-header">
        <h2>下载我们的 App</h2>
        <p>随时随地享受便捷出行</p>
      </div>
      
      <div class="download-buttons">
        <el-button type="primary" size="large" :icon="Iphone">
          App Store
        </el-button>
        <el-button type="primary" size="large" :icon="User">
          Google Play
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { 
  Bicycle, 
  Van, 
  Iphone, 
  User,
  Lightning,
  Star,
  Headset,
  Location
} from '@element-plus/icons-vue'

const router = useRouter()

// 特色功能
const features = [
  {
    icon: Lightning,
    title: '快速响应',
    description: '智能调度系统，平均2分钟内接单',
    color: '#409EFF'
  },
  {
    icon: Star,
    title: '优质服务',
    description: '专业司机团队，确保安全舒适出行',
    color: '#E6A23C'
  },
  {
    icon: Headset,
    title: '贴心客服',
    description: '24小时在线客服，随时解决您的问题',
    color: '#67C23A'
  },
  {
    icon: Location,
    title: '精准定位',
    description: '实时GPS定位，准确找到您的位置',
    color: '#F56C6C'
  }
]

// 统计数据
const stats = {
  drivers: '10,000+',
  passengers: '500,000+',
  rides: '50,000+',
  rating: '4.8'
}

// 路由跳转
const goToRide = () => {
  router.push('/ride')
}

const goToDriver = () => {
  router.push('/driver')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
}

.hero-section {
  height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  background: linear-gradient(120deg, #409EFF 0%, #1d5fa0 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('https://images.unsplash.com/photo-1503376780353-7e6692767b70?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1740&q=80') no-repeat center center;
  background-size: cover;
  opacity: 0.2;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 800px;
  padding: 0 20px;
}

.hero-content h1 {
  font-size: 3.5rem;
  margin-bottom: 1rem;
  font-weight: 700;
}

.hero-content p {
  font-size: 1.5rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.hero-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.features-section {
  padding: 80px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 50px;
}

.section-header h2 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 15px;
}

.section-header p {
  font-size: 1.2rem;
  color: #666;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  text-align: center;
  padding: 40px 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #333;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

.stats-section {
  background: linear-gradient(120deg, #409EFF 0%, #1d5fa0 100%);
  color: white;
  padding: 60px 20px;
}

.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  text-align: center;
}

.stat-item h3 {
  font-size: 3rem;
  margin-bottom: 10px;
  font-weight: 700;
}

.stat-item p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.download-section {
  padding: 80px 20px;
  text-align: center;
  background: #f8f9fa;
}

.download-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 30px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 2.5rem;
  }
  
  .hero-content p {
    font-size: 1.2rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .section-header h2 {
    font-size: 2rem;
  }
  
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
  
  .stat-item h3 {
    font-size: 2rem;
  }
}
</style>