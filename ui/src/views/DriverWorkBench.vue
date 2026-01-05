<template>
  <div class="driver-workbench-container">
    <DriverMap 
      :status="currentStatus" 
      :driver-loc="driverLocation"
      :order="currentOrder"
    />

    <!-- 司机状态控制面板 -->
    <div class="driver-control-panel">
      <el-card class="status-card" v-if="currentStatus === DriverStatus.OFFLINE || currentStatus === DriverStatus.IDLE">
        <template #header>
          <div class="status-header">
            <span class="status-title">
              <el-icon :class="currentStatus === DriverStatus.IDLE ? 'status-online' : 'status-offline'">
                <CircleCheck v-if="currentStatus === DriverStatus.IDLE" />
                <CircleClose v-else />
              </el-icon>
              {{ currentStatus === DriverStatus.IDLE ? '听单中' : '已休车' }}
            </span>
            <el-tag :type="currentStatus === DriverStatus.IDLE ? 'success' : 'info'">
              {{ currentStatus === DriverStatus.IDLE ? '在线' : '离线' }}
            </el-tag>
          </div>
        </template>
        
        <div class="status-actions">
          <el-button 
            type="primary" 
            @click="toggleWorkStatus"
            :icon="currentStatus === DriverStatus.IDLE ? 'SwitchButton' : 'CircleClose'"
            :class="{ 'btn-stop': currentStatus === DriverStatus.IDLE }"
            size="large"
            style="width: 100%;"
          >
            {{ currentStatus === DriverStatus.IDLE ? '收车休息' : '出车听单' }}
          </el-button>
          
          <div class="stats-info" v-if="currentStatus === DriverStatus.IDLE">
            <div class="stat-item">
              <span class="stat-label">今日订单</span>
              <span class="stat-value">{{ todayOrders }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">今日收入</span>
              <span class="stat-value">¥{{ todayIncome.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="pickup-card" v-else-if="currentStatus === DriverStatus.PICKUP">
        <template #header>
          <div class="status-header">
            <span class="status-title">
              <el-icon class="status-pickup">
                <Location />
              </el-icon>
              前往接驾
            </span>
            <el-tag type="warning">进行中</el-tag>
          </div>
        </template>
        
        <div class="order-details">
          <div class="order-info">
            <h4>乘客信息</h4>
            <p><i class="el-icon-user"></i> 乘客：{{ currentOrder?.passengerName || '张三' }}</p>
            <p><i class="el-icon-location"></i> 位置：{{ currentOrder?.passengerLoc.address }}</p>
            <p><i class="el-icon-phone"></i> 电话：{{ currentOrder?.passengerPhone || '13800138000' }}</p>
          </div>
          
          <div class="progress-section">
            <el-progress 
              :percentage="40" 
              status="warning" 
              :stroke-width="8" 
              style="margin: 15px 0;"
            />
            <p>正在前往接乘客...</p>
          </div>
          
          <SlideButton 
            ref="slideBtnRef" 
            text="滑动确认接到乘客" 
            @confirm="handlePassengerOnBoard" 
          />
        </div>
      </el-card>

      <el-card class="trip-card" v-else-if="currentStatus === DriverStatus.IN_TRIP">
        <template #header>
          <div class="status-header">
            <span class="status-title">
              <el-icon class="status-trip">
                <Van />
              </el-icon>
              行程中
            </span>
            <el-tag type="primary">进行中</el-tag>
          </div>
        </template>
        
        <div class="trip-details">
          <div class="trip-dashboard">
            <div class="dash-item">
              <div class="label">行驶时间</div>
              <div class="value">{{ tripTimeStr }}</div>
            </div>
            <div class="dash-item">
              <div class="label">当前金额</div>
              <div class="value">¥{{ currentFare.toFixed(2) }}</div>
            </div>
          </div>
          
          <div class="dest-info">
            <i class="el-icon-location"></i>
            <span>目的地：{{ currentOrder?.destLoc.address }}</span>
          </div>
          
          <div class="progress-section">
            <el-progress 
              :percentage="80" 
              status="success" 
              :stroke-width="8" 
              style="margin: 15px 0;"
            />
            <p>前往目的地...</p>
          </div>
          
          <SlideButton 
            ref="slideBtnRef" 
            text="滑动确认到达目的地" 
            @confirm="handleArriveDest" 
          />
        </div>
      </el-card>
    </div>

    <!-- 司机信息面板 -->
    <div class="driver-info-panel">
      <el-card>
        <template #header>
          <div class="info-header">
            <span>司机信息</span>
          </div>
        </template>
        
        <div class="driver-info-content">
          <div class="driver-avatar">
            <el-avatar size="large" :src="driverAvatar" />
            <div class="driver-name">
              <h4>{{ driverInfo.name || '张司机' }}</h4>
              <p>评分：{{ driverInfo.rating || '4.9' }} ★</p>
            </div>
          </div>
          
          <div class="driver-stats">
            <div class="stat-item">
              <span class="stat-label">总订单</span>
              <span class="stat-value">{{ driverInfo.totalOrders || 128 }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">总收入</span>
              <span class="stat-value">¥{{ driverInfo.totalIncome || 12800 }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import DriverMap from '../components/DriverMap.vue';
import SlideButton from '../components/SlideButton.vue';
import { DriverStatus, type OrderInfo } from '../types';
import { useUserStore } from '@/stores/userStore';
import { CircleCheck, CircleClose, Location, Van } from '@element-plus/icons-vue';

const userStore = useUserStore();

// ===================== 1. 状态管理 =====================
const currentStatus = ref<DriverStatus>(DriverStatus.OFFLINE);
const currentOrder = ref<OrderInfo | null>(null);
const slideBtnRef = ref();

// 模拟司机当前位置 (北京)
const driverLocation = ref({ lng: 116.404, lat: 39.915 });

// 司机信息
const driverInfo = ref({
  name: userStore.userInfo.nickname || userStore.userInfo.username || '张司机' || undefined,
  rating: 4.9,
  totalOrders: 128,
  totalIncome: 12800
});

// 默认司机头像
const driverAvatar = ref('https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png');

// ===================== 2. 计时器与计价器 =====================
const tripDuration = ref(0);
const currentFare = ref(0.0);
let timer: any = null;

// 今日统计
const todayOrders = ref(5);
const todayIncome = ref(245.50);

// 格式化时间 MM:SS
const tripTimeStr = computed(() => {
  const m = Math.floor(tripDuration.value / 60).toString().padStart(2, '0');
  const s = (tripDuration.value % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
});

// ===================== 3. 业务逻辑方法 =====================

// 切换 出车/休车
const toggleWorkStatus = () => {
  if (currentStatus.value === DriverStatus.OFFLINE) {
    ElMessageBox.confirm('确定要开始出车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success',
    }).then(() => {
      currentStatus.value = DriverStatus.IDLE;
      ElMessage.success('已切换为听单状态');
      // 刚出车时，模拟3秒后接到第一个订单
      setTimeout(mockReceiveOrder, 3000);
    }).catch(() => {
      // 用户取消操作
    });
  } else {
    ElMessageBox.confirm('确定要收车休息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      currentStatus.value = DriverStatus.OFFLINE;
      ElMessage.info('已切换为休车状态');
    }).catch(() => {
      // 用户取消操作
    });
  }
};

// 模拟接到订单 (增加了随机性，确保地图组件能感知到变化)
const mockReceiveOrder = () => {
  // 如果司机已经收车，则不派单
  if (currentStatus.value !== DriverStatus.IDLE) return;
  
  // 随机生成一个ID，保证每次都是新订单
  const newOrderId = `ORD_${Date.now()}`;
  
  // 简单模拟两个地点的切换，或者稍微随机化坐标
  const isEven = Date.now() % 2 === 0;

  ElMessageBox({
    title: '新订单提醒',
    message: '您有新的订单！',
    type: 'success',
    showCancelButton: true,
    confirmButtonText: '接受',
    cancelButtonText: '拒绝',
  }).then(() => {
    currentOrder.value = {
      id: newOrderId,
      passengerName: isEven ? '李先生' : '王女士',
      passengerPhone: isEven ? '13800138001' : '13800138002',
      passengerLoc: { 
        lng: 116.380 + (Math.random() * 0.005), 
        lat: 39.920 + (Math.random() * 0.005), 
        address: isEven ? '西单大悦城' : '天安门广场' 
      },
      destLoc: { 
        lng: 116.450 + (Math.random() * 0.005), 
        lat: 39.930 + (Math.random() * 0.005), 
        address: isEven ? '三里屯太古里' : '北京朝阳站' 
      }
    };
    
    // 状态自动转为去接驾 -> DriverMap 会自动规划路线
    currentStatus.value = DriverStatus.PICKUP;
    ElMessage.success('已接受订单，正在前往接乘客');
  }).catch(() => {
    // 拒绝订单，3秒后继续派单
    setTimeout(() => {
      mockReceiveOrder();
    }, 3000);
  });
};

// 滑动确认：接到乘客
const handlePassengerOnBoard = () => {
  slideBtnRef.value?.reset();
  
  // 切换状态 -> DriverMap 会自动规划去目的地的路线
  currentStatus.value = DriverStatus.IN_TRIP;
  
  startTripMeter();
  ElMessage.success('已接到乘客，开始行程');
};

// 滑动确认：到达目的地
const handleArriveDest = () => {
  // 1. 停止计费
  stopTripMeter();
  
  // 2. 更新统计信息
  todayOrders.value += 1;
  todayIncome.value += currentFare.value;
  
  // 3. 弹出结算信息
  ElMessageBox.alert(
    `行程结束！<br/>总耗时：${tripTimeStr.value}<br/>费用：¥${currentFare.value.toFixed(2)}`,
    '行程完成',
    {
      confirmButtonText: '确定',
      type: 'success',
    }
  );
  
  // 4. 清理当前订单
  slideBtnRef.value?.reset();
  currentOrder.value = null;
  
  // 5. 状态回到听单中 -> DriverMap 清除路线
  currentStatus.value = DriverStatus.IDLE;
  
  // 6. 【核心逻辑】开启3秒倒计时，自动模拟派下一单
  console.log("正在为您寻找下一单...");
  setTimeout(() => {
    mockReceiveOrder();
  }, 3000);
};

// ===================== 4. 计费模拟 =====================
const startTripMeter = () => {
  tripDuration.value = 0;
  currentFare.value = 13.0; // 起步价
  timer = setInterval(() => {
    tripDuration.value++;
    // 简单模拟计价
    currentFare.value += 0.1;
    
    // 模拟司机移动 (修改坐标触发地图小车移动)
    driverLocation.value.lng += 0.0001; 
    driverLocation.value.lat += 0.0001;
  }, 1000);
};

const stopTripMeter = () => {
  if (timer) clearInterval(timer);
  timer = null;
};

onUnmounted(() => {
  stopTripMeter();
});

onMounted(() => {
  // 初始化司机信息
  if (userStore.userInfo.nickname || userStore.userInfo.username) {
    driverInfo.value.name = userStore.userInfo.nickname || userStore.userInfo.username;
  }
});
</script>

<style scoped>
.driver-workbench-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f5f5; /* 防止地图加载前白屏太刺眼 */
  display: grid;
  grid-template-columns: 1fr 300px;
  grid-template-rows: 1fr;
  gap: 15px;
  padding: 15px;
  box-sizing: border-box;
}

.driver-control-panel {
  position: absolute;
  left: 15px;
  bottom: 15px;
  width: calc(100% - 330px);
  max-width: 500px;
  z-index: 10;
}

.driver-info-panel {
  position: absolute;
  right: 15px;
  top: 15px;
  width: 280px;
  z-index: 10;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-title {
  display: flex;
  align-items: center;
  font-weight: 600;
}

.status-title .el-icon {
  margin-right: 8px;
}

.status-online {
  color: #67c23a;
}

.status-offline {
  color: #909399;
}

.status-pickup {
  color: #e6a23c;
}

.status-trip {
  color: #409eff;
}

.status-actions {
  padding-top: 10px;
}

.stats-info {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #666;
}

.stat-value {
  display: block;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.order-details {
  padding: 10px 0;
}

.order-info h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.order-info p {
  margin: 8px 0;
  display: flex;
  align-items: center;
  color: #666;
}

.order-info p i {
  margin-right: 8px;
  width: 16px;
}

.trip-details {
  padding: 10px 0;
}

.trip-dashboard {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 8px;
  border: 1px solid #d9ecff;
}

.dash-item {
  text-align: center;
}

.dash-item .label {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
}

.dash-item .value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  font-family: monospace;
}

.dest-info {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #333;
  margin: 15px 0;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 6px;
}

.dest-info i {
  margin-right: 8px;
}

.progress-section {
  margin: 15px 0;
}

.progress-section p {
  text-align: center;
  margin-top: 10px;
  color: #666;
}

.info-header {
  font-weight: 600;
}

.driver-info-content {
  padding: 10px 0;
}

.driver-avatar {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.driver-avatar .el-avatar {
  margin-right: 12px;
}

.driver-name h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.driver-name p {
  margin: 0;
  font-size: 12px;
  color: #e6a23c;
}

.driver-stats {
  display: flex;
  justify-content: space-between;
}

.main-btn {
  width: 100%;
  height: 50px;
  border-radius: 8px;
  border: none;
  font-size: 16px;
  font-weight: bold;
  color: white;
  background: #409EFF; 
  cursor: pointer;
  transition: opacity 0.2s;
}

.main-btn:active {
  opacity: 0.8;
}

.btn-stop {
  background: #F56C6C; 
}

@media (max-width: 768px) {
  .driver-workbench-container {
    grid-template-columns: 1fr;
    grid-template-rows: 1fr auto;
    padding: 10px;
  }
  
  .driver-control-panel {
    position: relative;
    left: auto;
    bottom: auto;
    width: 100%;
    margin-top: 10px;
  }
  
  .driver-info-panel {
    position: relative;
    right: auto;
    top: auto;
    width: 100%;
    margin-top: 15px;
  }
}
</style>