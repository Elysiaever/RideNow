<template>
  <div class="page">
    <BaiduMap ref="baiduMap" @pick-point="onPickPoint" />

    <div class="bottom-panel">
      <el-form :model="form" label-width="90px" class="form">

        <el-form-item label="乘客ID">
          <el-input v-model="form.passengerId" />
        </el-form-item>

        <!-- 出发地（纯经纬度输入） -->
        <el-form-item label="出发地">
          <el-input
              v-model="form.origin.raw"
              placeholder="格式：经度,纬度 例如：121.5233,31.2456"
              clearable
          />
        </el-form-item>

        <!-- 目的地（纯经纬度输入） -->
        <el-form-item label="目的地">
          <el-input
              v-model="form.destination.raw"
              placeholder="格式：经度,纬度 或点击地图选择"
              clearable
              style="width:80%"
          />
          <el-button type="primary" @click="locateDestination">定位</el-button>
        </el-form-item>

        <el-button type="primary" @click="submitForm" class="submit-btn">
          提交行程
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

// ==========================
// 🌍 只保留“字符串”形式的经纬度
// ==========================
const form = reactive({
  passengerId: "",
  origin: {
    raw: "", // "121.5233,31.2456"
  },
  destination: {
    raw: "",
  },
});

// 获取子组件
const baiduMap = ref<any>(null);

// ==========================
// 📌 点击地图 → 自动写入 "lng,lat"
// ==========================
const onPickPoint = async (p: { lng: number; lat: number }) => {
  const lngLatStr = `${p.lng.toFixed(6)},${p.lat.toFixed(6)}`;
  form.destination.raw = lngLatStr;

  ElMessage.success(`已选择坐标：${lngLatStr}`);
};

// ==========================
// 📍 定位输入框 → 地图
// ==========================
const locateDestination = () => {
  if (!form.destination.raw) {
    ElMessage.warning("请输入经纬度，例如：121.5233,31.2456");
    return;
  }

  const [lngStr, latStr] = form.destination.raw.split(",");
  const lng = parseFloat(lngStr);
  const lat = parseFloat(latStr);

  if (isNaN(lng) || isNaN(lat)) {
    ElMessage.error("请输入合法格式：经度,纬度");
    return;
  }

  baiduMap.value?.locatePoint({ lng, lat });
};

// ==========================
// 🧩 工具函数：解析经纬度
// ==========================
function parseLngLat(raw: string) {
  const arr = raw.split(",");
  if (arr.length !== 2) return null;
  const lng = parseFloat(arr[0]);
  const lat = parseFloat(arr[1]);
  if (isNaN(lng) || isNaN(lat)) return null;
  return { lng, lat };
}

// ==========================
// 🚗 表单提交 → 后端
// ==========================
const submitForm = async () => {
  if (!form.passengerId || !form.origin.raw || !form.destination.raw) {
    ElMessage.warning("请填写完整信息");
    return;
  }

  const origin = parseLngLat(form.origin.raw);
  const dest = parseLngLat(form.destination.raw);

  if (!origin || !dest) {
    ElMessage.error("经纬度格式错误，请使用：121.5233,31.2456");
    return;
  }

  try {
    const res = await request.post("/api/ride/create", {
      passengerId: form.passengerId,

      originLng: origin.lng,
      originLat: origin.lat,

      destLng: dest.lng,
      destLat: dest.lat,
    });

    ElMessage.success("行程创建成功！");
    console.log("后端返回：", res.data);

  } catch (error) {
    console.error(error);
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
  background: rgba(255, 255, 255, 0.92);
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
