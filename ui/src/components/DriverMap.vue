<template>
  <div class="driver-map-container" ref="mapContainer"></div>
</template>

<script setup lang="ts">
import { onMounted, watch, ref, onUnmounted, nextTick } from 'vue';
import { DriverStatus, type OrderInfo } from '../types';

import { loadBaiduMap } from '@/utils/baiduMapLoader';

// 声明 BMap，防止 TS 报错
declare const BMap: any;

const props = defineProps<{
  status: DriverStatus;
  driverLoc: { lng: number; lat: number };
  order: OrderInfo | null;
}>();

const mapContainer = ref<HTMLElement | null>(null);
let map: any = null;
let drivingRoute: any = null;
let myMarker: any = null;

// 图标资源 - 延迟到地图初始化时创建
let ICON_DRIVER: any, ICON_START: any, ICON_END: any;

const initMap = async () => {
  if (!mapContainer.value) return;
  
  // 等待百度地图API加载完成
  try {
    await loadBaiduMap('45fhg18PIZtRwysqRlZrIQBG0NymF9XR');
    await nextTick();
    
    // 初始化图标资源
    ICON_DRIVER = new BMap.Icon("https://maps.gstatic.com/mapfiles/ms2/micons/cabs.png", new BMap.Size(32, 32));
    ICON_START = new BMap.Icon("https://maps.gstatic.com/mapfiles/ms2/micons/green-dot.png", new BMap.Size(32, 32));
    ICON_END = new BMap.Icon("https://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png", new BMap.Size(32, 32));
    
    // 1. 初始化地图
    map = new BMap.Map(mapContainer.value);
    const point = new BMap.Point(props.driverLoc.lng, props.driverLoc.lat);
    map.centerAndZoom(point, 15);
    map.enableScrollWheelZoom(true);

    // 2. 初始化司机 Marker
    myMarker = new BMap.Marker(point, { icon: ICON_DRIVER });
    map.addOverlay(myMarker);

    // 3. 初始状态检查
    updateMapState();
  } catch (error) {
    console.error('百度地图API加载失败:', error);
  }
};

// 渲染路径规划
const renderRoute = (
  start: { lng: number, lat: number }, 
  end: { lng: number, lat: number },
  policy: string // 'pickup' | 'trip'
) => {
  if (!map) return;
  
  // 清除旧路线
  if (drivingRoute) {
    drivingRoute.clearResults();
  }

  const p1 = new BMap.Point(start.lng, start.lat);
  const p2 = new BMap.Point(end.lng, end.lat);

  // 自定义起点终点图标（可选，为了更好看）
  const startIcon = policy === 'pickup' ? undefined : new BMap.Icon(ICON_START, new BMap.Size(32,32));
  const endIcon = policy === 'pickup' ? new BMap.Icon(ICON_START, new BMap.Size(32,32)) : new BMap.Icon(ICON_END, new BMap.Size(32,32));

  drivingRoute = new BMap.DrivingRoute(map, {
    renderOptions: { 
      map: map, 
      autoViewport: true, // 关键：自动调整视野包含路径
      enableDragging: false // 司机端通常不允许拖拽改变路线
    },
    onPolylinesSet: (routes: any[]) => {
       // 可以在这里设置折线颜色等样式
    }
  });

  // 如果需要自定义图标，DrivingRoute 比较麻烦，通常使用默认即可
  // 这里直接搜索
  drivingRoute.search(p1, p2);
};

// 核心状态机逻辑
const updateMapState = () => {
  if (!map || !myMarker) return;

  // 1. 更新司机位置图标
  const driverPoint = new BMap.Point(props.driverLoc.lng, props.driverLoc.lat);
  myMarker.setPosition(driverPoint);

  // 2. 根据状态处理路线
  if (props.status === DriverStatus.PICKUP && props.order) {
    // 场景：去接乘客
    // 起点：司机当前位置，终点：乘客上车点
    renderRoute(props.driverLoc, props.order.passengerLoc, 'pickup');
  } 
  else if (props.status === DriverStatus.IN_TRIP && props.order) {
    // 场景：送乘客去目的地
    // 起点：司机当前位置（或乘客上车点），终点：目的地
    // *注意*：为了导航准确，起点应实时更新为司机位置，但频繁重绘消耗资源。
    // 简单做法：初次进入 IN_TRIP 规划一次，后续只动车标。
    // 如果需要实时导航线，需要高频调用 API，这里假设只画一次静态线或低频更新。
    if (!drivingRoute || drivingRoute.getStatus() === 0) {
       renderRoute(props.driverLoc, props.order.destLoc, 'trip');
    }
  } 
  else {
    // 场景：闲置/听单/下线
    if (drivingRoute) {
      drivingRoute.clearResults();
      drivingRoute = null;
    }
    // 视野回归司机中心
    map.panTo(driverPoint);
  }
};

// 监听状态和位置变化
watch(
  () => [props.status, props.order?.id], 
  () => { updateMapState(); },
  { immediate: true }
);

// 监听位置实时变化（只移动图标，不频繁重绘路线以节省资源）
watch(
  () => props.driverLoc,
  (newVal) => {
    if (map && myMarker) {
      const p = new BMap.Point(newVal.lng, newVal.lat);
      myMarker.setPosition(p);
      // 如果是闲置状态，地图跟随司机移动
      if (props.status === DriverStatus.IDLE) {
        map.panTo(p);
      }
    }
  },
  { deep: true }
);

// 窗口大小调整
const handleResize = () => {
  if (map) map.resize();
};

onMounted(() => {
  initMap();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.driver-map-container {
  width: 100%;
  height: 100%;
  position: absolute; /* 确保填满父容器 */
  top: 0;
  left: 0;
}

/* 覆盖百度地图的一些默认样式，去掉版权遮挡等（按需） */
:deep(.anchorBL) {
  display: none; 
}
</style>