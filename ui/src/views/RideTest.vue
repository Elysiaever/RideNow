<template>
  <div class="ride-page">
    <!-- 地图组件 -->
    <BaiduMap 
      ref="baiduMap" 
      :pickMode="pickMode" 
      @pick-point="onPickPoint"
      :drivers="drivers" 
    />

    <!-- 顶部控制面板 -->
    <div class="top-panel">
      <el-card class="ride-card">
        <template #header>
          <div class="card-header">
            <span>行程信息</span>
            <el-button type="primary" link @click="goToHistory">查看历史行程</el-button>
          </div>
        </template>
        
        <el-form :model="form" label-width="90px" size="large">
          <el-form-item label="乘客">{{ userInfo.nickname || userInfo.username }}</el-form-item>
          
          <el-form-item label="出发地">
            <el-input
              v-model="form.origin.display"
              placeholder="点击地图选择出发地"
              clearable
              @focus="pickMode = 'origin'"
              prefix-icon="Location"
            />
          </el-form-item>

          <el-form-item label="目的地">
            <el-input
              v-model="form.destination.display"
              placeholder="点击地图选择目的地"
              clearable
              @focus="pickMode = 'destination'"
              prefix-icon="Location"
            />
          </el-form-item>

          <el-button 
            type="primary" 
            @click="submitForm" 
            class="submit-btn"
            :loading="submitting"
            :disabled="!form.origin.lng || !form.destination.lng"
            size="large"
            style="width: 100%; margin-top: 10px;"
          >
            {{ submitting ? '正在叫车...' : '开始叫车' }}
          </el-button>
        </el-form>
      </el-card>
    </div>

    <!-- 右侧司机列表 -->
    <div class="driver-list-panel">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>附近司机</span>
            <el-tag type="info" size="small">{{ drivers.length }}位</el-tag>
          </div>
        </template>
        
        <div v-if="drivers.length === 0" class="no-drivers">
          <el-empty description="暂无附近司机" :image-size="60" />
        </div>
        
        <div v-else>
          <div v-for="d in drivers" :key="d.id" class="driver-card">
            <div class="driver-info">
              <div class="driver-avatar">
                <el-avatar size="large" :src="driverAvatar" />
              </div>
              <div class="driver-details">
                <h4>{{ d.name }}</h4>
                <p class="driver-phone">电话：{{ d.phone }}</p>
                <p class="driver-plate">车牌：{{ d.plateNumber }}</p>
              </div>
            </div>
            <el-button size="small" @click="contactDriver(d)">联系司机</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 当前行程状态面板 -->
    <div class="status-panel" v-if="currentRide">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>当前行程</span>
            <el-tag :type="getRideStatusType(currentRide.status)">{{ getRideStatusText(currentRide.status) }}</el-tag>
          </div>
        </template>
        
        <div class="ride-status-info">
          <p><i class="el-icon-position"></i> {{ currentRide.originAddress }}</p>
          <p><i class="el-icon-arrow-right"></i> {{ currentRide.destinationAddress }}</p>
          <p><i class="el-icon-timer"></i> {{ currentRide.startTime }}</p>
          <div class="fare-info" v-if="currentRide.fare">
            <span class="fare-label">预估费用：</span>
            <span class="fare-amount">¥{{ currentRide.fare }}</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import BaiduMap from "@/components/PassengerMap.vue";
import { useUserStore } from "@/stores/userStore";
import request from "@/utils/request";

const router = useRouter();
const userStore = useUserStore();

const baiduMap = ref<any>(null);
const pickMode = ref<"origin" | "destination">("origin");
const submitting = ref(false);

// 当前行程状态
const currentRide = ref<any>(null);

// 用户信息
const userInfo = ref(userStore.userInfo);

// 默认司机头像
const driverAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png');

const form = reactive({
  passengerId: userStore.userInfo.id || "",
  origin: { display: "", lng: null as number | null, lat: null as number | null },
  destination: { display: "", lng: null as number | null, lat: null as number | null }
});

const onPickPoint = (p: { lng:number; lat:number; address:string; mode:string }) => {
  if(p.mode === "origin") form.origin = { display:p.address, lng:p.lng, lat:p.lat };
  else form.destination = { display:p.address, lng:p.lng, lat:p.lat };
  ElMessage.success(`已选择：${p.address}`);
};

const submitForm = async () => {
  if(!form.origin.lng || !form.destination.lng) {
    ElMessage.warning("请先选择出发地和目的地");
    return;
  }

  try {
    submitting.value = true;
    
    const response = await createRide({
      passengerId: form.passengerId,
      originAddress: form.origin.display, 
      originLng: form.origin.lng, 
      originLat: form.origin.lat,
      destAddress: form.destination.display, 
      destLng: form.destination.lng, 
      destLat: form.destination.lat
    });
    
    if(response.data.success) {
      ElMessage.success("行程创建成功！");
      // 更新当前行程状态
      currentRide.value = {
        id: response.data.data.rideId,
        originAddress: form.origin.display,
        destinationAddress: form.destination.display,
        startTime: new Date().toLocaleString(),
        status: 'PENDING',
        fare: response.data.data.fare || null
      };
      
      // 清空表单
      form.origin = { display: "", lng: null, lat: null };
      form.destination = { display: "", lng: null, lat: null };
    } else {
      ElMessage.error(response.data.message || "创建行程失败");
    }
  } catch(err) {
    console.error(err);
    ElMessage.error("提交失败：" + (err as any).response?.data?.message || (err as any).message || "网络错误");
  } finally {
    submitting.value = false;
  }
};

// 联系司机
const contactDriver = (driver: any) => {
  ElMessageBox.confirm(
    `是否拨打司机 ${driver.name} 的电话 ${driver.phone}？`,
    '联系司机',
    {
      confirmButtonText: '拨打',
      cancelButtonText: '取消',
      type: 'info',
    }
  ).then(() => {
    // 这里可以调用拨打电话的逻辑
    window.open(`tel:${driver.phone}`, '_self');
  }).catch(() => {
    // 取消操作
  });
};

// 跳转到历史行程页面
const goToHistory = () => {
  router.push('/history');
};

// 获取行程状态类型
const getRideStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': 'info',
    'ACCEPTED': 'warning',
    'ARRIVED': 'primary',
    'IN_TRIP': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'danger'
  };
  return statusMap[status] || 'info';
};

// 获取行程状态文本
const getRideStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': '待接单',
    'ACCEPTED': '已接单',
    'ARRIVED': '已到达',
    'IN_TRIP': '行程中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  };
  return statusMap[status] || status;
};

interface Driver { 
  id:string; 
  name:string; 
  phone:string; 
  plateNumber?:string; 
  lng:number; 
  lat:number; 
}
const drivers: Driver[] = [
  { id:"1", name:"张三", phone:"13800000001", plateNumber:"沪A12345", lng:116.404, lat:39.915 },
  { id:"2", name:"李四", phone:"13800000002", plateNumber:"沪B54321", lng:116.414, lat:39.925 },
  { id:"3", name:"王五", phone:"13800000003", plateNumber:"沪C67890", lng:116.424, lat:39.935 }
];

onMounted(() => {
  // 更新用户信息
  userInfo.value = userStore.userInfo;
  
  // 初始化乘客ID
  if (userStore.userInfo.id) {
    form.passengerId = userStore.userInfo.id.toString();
  }
});
</script>

<style scoped>
.ride-page {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  display: grid;
  grid-template-columns: 1fr 300px;
  grid-template-rows: 1fr;
  gap: 15px;
  padding: 15px;
  box-sizing: border-box;
}

.top-panel {
  position: absolute;
  top: 15px;
  left: 15px;
  right: 315px;
  z-index: 10;
}

.ride-card {
  max-width: 500px;
  margin: 0 auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.driver-list-panel {
  position: absolute;
  right: 15px;
  top: 15px;
  width: 280px;
  z-index: 10;
}

.no-drivers {
  text-align: center;
  padding: 20px 0;
}

.driver-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.driver-card:last-child {
  border-bottom: none;
}

.driver-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.driver-avatar {
  margin-right: 12px;
}

.driver-details h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
}

.driver-details p {
  margin: 2px 0;
  font-size: 12px;
  color: #666;
}

.status-panel {
  position: absolute;
  left: 15px;
  bottom: 15px;
  width: calc(100% - 30px);
  max-width: 500px;
  z-index: 10;
}

.ride-status-info p {
  margin: 8px 0;
  display: flex;
  align-items: center;
}

.ride-status-info p i {
  margin-right: 8px;
  width: 16px;
}

.fare-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.fare-amount {
  font-size: 18px;
  font-weight: bold;
  color: #e6a23c;
}

@media (max-width: 768px) {
  .ride-page {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr auto;
    padding: 10px;
  }
  
  .top-panel {
    position: relative;
    top: auto;
    left: auto;
    right: auto;
    width: 100%;
  }
  
  .driver-list-panel {
    position: relative;
    right: auto;
    top: auto;
    width: 100%;
    margin-top: 15px;
  }
  
  .status-panel {
    position: relative;
    left: auto;
    bottom: auto;
    margin-top: 15px;
  }
}
</style>
