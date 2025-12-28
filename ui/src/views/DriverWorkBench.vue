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
        >
          {{ currentStatus === DriverStatus.IDLE ? '收车休息' : '出车听单' }}
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

// ===================== 1. 状态管理 =====================
const currentStatus = ref<DriverStatus>(DriverStatus.OFFLINE);
const currentOrder = ref<OrderInfo | null>(null);
const slideBtnRef = ref();

// 模拟司机当前位置 (北京)
const driverLocation = ref({ lng: 116.404, lat: 39.915 });

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

// 切换 出车/休车
const toggleWorkStatus = () => {
  if (currentStatus.value === DriverStatus.OFFLINE) {
    currentStatus.value = DriverStatus.IDLE;
    // 刚出车时，模拟3秒后接到第一个订单
    setTimeout(mockReceiveOrder, 3000);
  } else {
    currentStatus.value = DriverStatus.OFFLINE;
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
  
  // 状态自动转为去接驾 -> DriverMap 会自动规划路线
  currentStatus.value = DriverStatus.PICKUP;
};

// 滑动确认：接到乘客
const handlePassengerOnBoard = () => {
  slideBtnRef.value?.reset();
  
  // 切换状态 -> DriverMap 会自动规划去目的地的路线
  currentStatus.value = DriverStatus.IN_TRIP;
  
  startTripMeter();
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
  
  // 4. 状态回到听单中 -> DriverMap 清除路线
  currentStatus.value = DriverStatus.IDLE;
  
  // 5. 【核心逻辑】开启3秒倒计时，自动模拟派下一单
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
</script>

<style scoped>
.workbench-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #f5f5f5; /* 防止地图加载前白屏太刺眼 */
}

.control-panel {
  position: absolute;
  bottom: 0;
  left: 50%; /* 定位到中线 */
  transform: translateX(-50%); /* 向左回退一半，实现居中 */
  width: 96%; /* 左右留一点缝隙 */
  max-width: 450px; /* 限制最大宽度，适配电脑屏幕 */
  
  background: white;
  border-radius: 20px 20px 0 0; /* 上方圆角 */
  box-shadow: 0 -2px 15px rgba(0,0,0,0.1); /* 阴影加重一点更立体 */
  padding: 25px 20px; /* 增加一点内边距 */
  box-sizing: border-box;
  z-index: 999;
  margin-bottom: 0; /* 贴底 */
}

.panel-card {
  display: flex;
  flex-direction: column;
  gap: 20px; /* 拉大一点间距 */
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
  transition: opacity 0.2s;
}
.main-btn:active {
  opacity: 0.8;
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
  font-family: monospace; /* 数字用等宽字体更好看 */
}

.dest-info {
  font-size: 15px;
  color: #333;
  padding: 0 5px;
  font-weight: 500;
}
</style>