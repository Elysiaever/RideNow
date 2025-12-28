<template>
  <div class="slide-track" ref="trackRef">
    <div class="slide-bg" :style="{ width: moveX + 'px' }"></div>
    <div class="slide-text">{{ text }}</div>
    <div 
      class="slide-handler" 
      ref="handlerRef"
      
      @mousedown="handleStart"
      @touchstart.passive="handleStart"

      @mousemove="handleMove"
      @touchmove.passive="handleMove"

      @mouseup="handleEnd"
      @mouseleave="handleEnd"
      @touchend="handleEnd"

      :style="{ transform: `translateX(${moveX}px)` }"
    >
      <span style="font-weight:bold; color:#ccc;">>></span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const props = defineProps<{ text: string }>();
const emit = defineEmits(['confirm']);

const trackRef = ref<HTMLElement>();
const handlerRef = ref<HTMLElement>();
const moveX = ref(0);
const maxMove = ref(0);
const isConfirmed = ref(false);
const isDragging = ref(false); // 新增：标记是否正在拖拽

onMounted(() => {
  if (trackRef.value && handlerRef.value) {
    // 计算最大滑动距离 = 轨道宽度 - 滑块宽度
    maxMove.value = trackRef.value.clientWidth - handlerRef.value.clientWidth;
  }
});

let startX = 0;

// 统一获取横坐标的工具函数
const getClientX = (e: MouseEvent | TouchEvent) => {
  if ('touches' in e) {
    return e.touches[0]?.clientX ?? 0;
  }
  return (e as MouseEvent).clientX;
};

const handleStart = (e: MouseEvent | TouchEvent) => {
  if (isConfirmed.value) return;
  isDragging.value = true; // 开始拖拽
  startX = getClientX(e);
};

const handleMove = (e: MouseEvent | TouchEvent) => {
  if (isConfirmed.value || !isDragging.value) return;
  
  const currentX = getClientX(e);
  const diff = currentX - startX;
  
  // 限制滑动范围：0 到 maxMove
  if (diff > 0 && diff <= maxMove.value) {
    moveX.value = diff;
  } else if (diff > maxMove.value) {
    moveX.value = maxMove.value;
  }
};

const handleEnd = () => {
  if (isConfirmed.value || !isDragging.value) return;
  isDragging.value = false; // 结束拖拽

  if (moveX.value > maxMove.value * 0.9) {
    // 滑动超过90%视为成功
    moveX.value = maxMove.value;
    isConfirmed.value = true;
    emit('confirm');
  } else {
    // 回弹
    moveX.value = 0;
  }
};

const reset = () => {
  moveX.value = 0;
  isConfirmed.value = false;
  isDragging.value = false;
};

defineExpose({ reset });
</script>

<style scoped>
.slide-track {
  position: relative;
  width: 100%;
  height: 50px;
  background: #e9e9e9;
  border-radius: 25px;
  overflow: hidden;
  user-select: none; /* 禁止选中文本 */
  box-shadow: inset 0 1px 3px rgba(0,0,0,0.1);
}
.slide-bg {
  height: 100%;
  background: #409EFF; /* 成功色 */
  position: absolute;
  left: 0;
  top: 0;
  transition: width 0.1s; /* 稍微加点动画更丝滑，但在拖动时不要加 */
}
.slide-text {
  position: absolute;
  width: 100%;
  text-align: center;
  line-height: 50px;
  color: #666;
  font-size: 14px;
  z-index: 1;
  pointer-events: none; /* 关键：防止文字阻挡鼠标事件 */
}
.slide-handler {
  position: absolute;
  left: 0;
  top: 0;
  width: 50px;
  height: 50px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2;
  cursor: grab;
}
.slide-handler:active {
  cursor: grabbing;
}
</style>