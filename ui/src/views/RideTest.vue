<template>
  <div class="page">
    <BaiduMap ref="baiduMap" :pickMode="pickMode" @pick-point="onPickPoint" />

    <div class="bottom-panel">
      <el-form :model="form" label-width="90px" class="form">
        
        <el-form-item label="乘客ID">
          <el-input
          style="width:90%" 
          v-model="form.passengerId" />
        </el-form-item>

        <!-- 出发地（绿色） -->
        <el-form-item label="出发地">
          <el-input
            v-model="form.origin.display"
            placeholder="点击地图选择出发地"
            clearable
            style="width:75%"
            @focus="pickMode = 'origin'"
          />
          <el-button type="primary" @click="locateOrigin">定位</el-button>
        </el-form-item>

        <!-- 目的地（红色） -->
        <el-form-item label="目的地">
          <el-input
            v-model="form.destination.display"
            placeholder="点击地图选择目的地"
            clearable
            style="width:75%"
            @focus="pickMode = 'destination'"
          />
          <el-button type="primary" @click="locateDestination">定位</el-button>
        </el-form-item>

        <el-button type="primary" @click="submitForm" class="submit-btn">
          开始叫车
        </el-button>

      </el-form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import BaiduMap from "@/components/BaiduMap.vue";
import request from "@/utils/request";

// 地图组件引用
const baiduMap = ref<any>(null);

// 当前地点拾取模式
const pickMode = ref<"origin" | "destination">("origin");

// 表单数据
const form = reactive({
  passengerId: "",
  origin: {
    display: "",
    lng: null as number | null,
    lat: null as number | null
  },
  destination: {
    display: "",
    lng: null as number | null,
    lat: null as number | null
  }
});

// 地图点击回调
const onPickPoint = (p: { lng: number; lat: number; address: string; mode: string }) => {
  if (p.mode === "origin") {
    form.origin = {
      display: p.address,
      lng: p.lng,
      lat: p.lat
    };
  } else {
    form.destination = {
      display: p.address,
      lng: p.lng,
      lat: p.lat
    };
  }

  ElMessage.success(`已选择：${p.address}`);
};

// 定位出发地
const locateOrigin = () => {
  if (!form.origin.display) {
    ElMessage.warning("请输入出发地地址");
    return;
  }
  baiduMap.value?.locatePoint({
    address: form.origin.display,
    type: "origin",
  });
};

// 定位目的地
const locateDestination = () => {
  if (!form.destination.display) {
    ElMessage.warning("请输入目的地地址");
    return;
  }
  baiduMap.value?.locatePoint({
    address: form.destination.display,
    type: "destination",
  });
};

// 提交行程
const submitForm = async () => {
  if (!form.passengerId) {
    ElMessage.warning("请输入乘客ID");
    return;
  }
  if (!form.origin.lng || !form.destination.lng) {
    ElMessage.error("请先选择出发地和目的地");
    return;
  }

  try {
    await request.post("/api/ride/create", {
      passengerId: form.passengerId,
      originAddress: form.origin.display,
      originLng: form.origin.lng,
      originLat: form.origin.lat,
      destAddress: form.destination.display,
      destLng: form.destination.lng,
      destLat: form.destination.lat
    });

    ElMessage.success("行程创建成功！");
  } catch (err) {
    console.error(err);
    ElMessage.error("提交失败");
  }
};
</script>

<style scoped>
.page {
  width: 100%;
  height: 100vh;
  position: relative;
}
.bottom-panel {
  position: absolute;
  bottom: 0;
  width: 100%;
  background: rgba(255,255,255,0.92);
  padding: 20px 10px;
}
.form {
  max-width: 650px;
  margin: auto;
}
.submit-btn {
  width: 100%;
  margin-top: 10px;
}
</style>
