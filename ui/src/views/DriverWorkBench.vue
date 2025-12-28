<template>
  <div class="workbench-container">
    <DriverMap 
      :status="currentStatus" 
      :driver-loc="driverLocation"
      :order="currentOrder"
    />

    <div class="control-panel">
      
      <div v-if="currentStatus === DriverStatus.OFFLINE || currentStatus === DriverStatus.IDLE" class="panel-card idle-mode">
        <div class="status-indicator">
          当前状态：{{ currentStatus === DriverStatus.IDLE ? '听单中...' : '已休车' }}
        </div>
        <button 
          class="main-btn" 
          :class="{ 'btn-stop': currentStatus === DriverStatus.IDLE }"
          @click="toggleWorkStatus"
          :disabled="isLoading" 
        >
          {{ btnText }}
        </button>
      </div>

      <div v-if="currentStatus === DriverStatus.PICKUP" class="panel-card pickup-mode">
        <div class="order-info">
          <h3>正在前往接驾</h3>
          <p>乘客位置：{{ currentOrder?.passengerLoc.address }}</p>
        </div>
        <SlideButton 
          ref="slideBtnRef" 
          text="滑动确认接到乘客" 
          @confirm="handlePassengerOnBoard" 
        />
      </div>

      <div v-if="currentStatus === DriverStatus.IN_TRIP" class="panel-card trip-mode">
        <div class="trip-dashboard">
          <div class="dash-item">
            <span class="label">行驶时间</span>
            <span class="value">{{ tripTimeStr }}</span>
          </div>
          <div class="dash-item">
            <span class="label">当前金额</span>
            <span class="value">¥{{ currentFare.toFixed(2) }}</span>
          </div>
        </div>
        <div class="dest-info">
          目的地：{{ currentOrder?.destLoc.address }}
        </div>
        <SlideButton 
          ref="slideBtnRef" 
          text="滑动确认到达目的地" 
          @confirm="handleArriveDest" 
        />
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted } from 'vue';
import DriverMap from '../components/DriverMap.vue';
import SlideButton from '../components/SlideButton.vue';
import { DriverStatus, type OrderInfo } from '../types';
// 引入封装好的 API
import { driverOnline, driverOffline } from '../api/driver';

// ===================== 1. 状态管理 =====================
const currentStatus = ref<DriverStatus>(DriverStatus.OFFLINE);
const currentOrder = ref<OrderInfo | null>(null);
const slideBtnRef = ref(); // 用于调用子组件 reset 方法
const isLoading = ref(false); // 防止按钮重复点击

// 【核心配置】司机ID (根据你的截图为 14)
const DRIVER_ID = 14; 

// 模拟司机当前位置 (北京)
const driverLocation = ref({ lng: 116.404, lat: 39.915 });

// 按钮文字计算属性
const btnText = computed(() => {
  if (isLoading.value) return '请求中...';
  return currentStatus.value === DriverStatus.IDLE ? '收车休息' : '出车听单';
});

// ===================== 2. 计时器与计价器 =====================
const tripDuration = ref(0);
const currentFare = ref(0.0);
let timer: any = null;

// 格式化时间 MM:SS
const tripTimeStr = computed(() => {
  const m = Math.floor(tripDuration.value / 60).toString().padStart(2, '0');
  const s = (tripDuration.value % 60).toString().padStart(2, '0');
  return `${m}:${s}`;
});

// ===================== 3. 业务逻辑方法 =====================

/**
 * 切换 出车/休车
 * 修改为 async 函数以支持后端 API 调用
 */
const toggleWorkStatus = async () => {
  if (isLoading.value) return;
  isLoading.value = true;

  try {
    if (currentStatus.value === DriverStatus.OFFLINE) {
      // >>> 执行上线逻辑 <<<
      const res = await driverOnline({
        driverId: DRIVER_ID,
        lng: driverLocation.value.lng,
        lat: driverLocation.value.lat
      });

      if (res.data && res.data.success) {
        currentStatus.value = DriverStatus.IDLE;
        console.log('上线成功:', res.data.message);
        
        // 上线成功后，300秒后模拟派单
        setTimeout(mockReceiveOrder, 300000);
      } else {
        alert('上线失败: ' + (res.data?.message || '未知错误'));
      }

    } else {
      // >>> 执行下线逻辑 <<<
      const res = await driverOffline(DRIVER_ID);

      if (res.data && res.data.success) {
        currentStatus.value = DriverStatus.OFFLINE;
        console.log('下线成功:', res.data.message);
      } else {
        alert('下线失败: ' + (res.data?.message || '未知错误'));
      }
    }
  } catch (error) {
    console.error('API 请求错误:', error);
    alert('网络请求失败，请检查后端服务是否启动');
  } finally {
    isLoading.value = false;
  }
};

// 模拟接到订单 
const mockReceiveOrder = () => {
  // 严谨判断：如果司机已经收车，或者已经在服务中，则不派单
  if (currentStatus.value !== DriverStatus.IDLE) return;
  
  const newOrderId = `ORD_${Date.now()}`;
  const isEven = Date.now() % 2 === 0;

  alert("您有新的订单！");
  
  currentOrder.value = {
    id: newOrderId,
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
  
  // 状态自动转为去接驾
  currentStatus.value = DriverStatus.PICKUP;
};

// 滑动确认：接到乘客
const handlePassengerOnBoard = () => {
  slideBtnRef.value?.reset(); // 重置滑块
  currentStatus.value = DriverStatus.IN_TRIP; // 切换状态
  startTripMeter(); // 开始计费
};

// 滑动确认：到达目的地
const handleArriveDest = () => {
  // 1. 停止计费
  stopTripMeter();
  
  // 2. 弹出结算信息
  alert(`行程结束！\n总耗时 ${tripTimeStr.value}\n费用 ¥${currentFare.value.toFixed(2)}`);
  
  // 3. 清理当前订单
  slideBtnRef.value?.reset();
  currentOrder.value = null;
  
  // 4. 状态回到听单中
  currentStatus.value = DriverStatus.IDLE;
  
  // 5. 开启300秒倒计时，自动模拟派下一单
  console.log("正在为您寻找下一单...");
  setTimeout(() => {
    mockReceiveOrder();
  }, 300000);
};

// ===================== 4. 计费模拟 =====================
const startTripMeter = () => {
  tripDuration.value = 0;
  currentFare.value = 13.0; // 起步价
  
  // 清除旧的 timer 防止重复
  if (timer) clearInterval(timer);

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
</script>

<style scoped>
.workbench-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f5f5;
}

.control-panel {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 96%;
  max-width: 450px;
  
  background: white;
  border-radius: 20px 20px 0 0;
  box-shadow: 0 -2px 15px rgba(0,0,0,0.1);
  padding: 25px 20px;
  box-sizing: border-box;
  z-index: 999;
  margin-bottom: 0;
}

.panel-card {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.main-btn {
  width: 100%;
  height: 50px;
  border-radius: 25px;
  border: none;
  font-size: 18px;
  font-weight: bold;
  color: white;
  background: #409EFF; 
  cursor: pointer;
  transition: opacity 0.2s, background-color 0.2s;
}
.main-btn:active {
  opacity: 0.8;
}
.main-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.btn-stop {
  background: #F56C6C; 
}

.status-indicator {
  text-align: center;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.order-info h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}
.order-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.trip-dashboard {
  display: flex;
  justify-content: space-around;
  margin-bottom: 10px;
  padding: 10px 0;
  background: #f9f9f9;
  border-radius: 10px;
}

.dash-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.dash-item .label {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.dash-item .value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  font-family: monospace;
}

.dest-info {
  font-size: 15px;
  color: #333;
  padding: 0 5px;
  font-weight: 500;
}
</style>