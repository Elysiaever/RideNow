<template>
  <div class="page">
    <!-- 全屏地图 -->
    <BaiduMap ref="baiduMap" @pick-point="onPickPoint" />

    <!-- 底部悬浮输入栏 -->
    <div class="bottom-panel">
      <el-form :model="form" label-width="90px" class="form">
        <el-form-item label="乘客ID">
          <el-input v-model="form.passengerId" />
        </el-form-item>

        <el-form-item label="出发地">
          <el-autocomplete
            v-model="form.origin"
            :fetch-suggestions="searchPoi"
            placeholder="输入地址或点击地图选择"
            clearable
          />
        </el-form-item>

        <el-form-item label="目的地">
          <el-input
            v-model="form.destination"
            placeholder="输入目的地"
            clearable
            style="width: 80%;"
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

const form = reactive({
  passengerId: "",
  origin: "",
  destination: "",
});

// 获取 BaiduMap 子组件引用
const baiduMap = ref<any>(null);

// 点击地图，得到经纬度
const onPickPoint = async (p: { lng: number; lat: number }) => {
  // 调用子组件的逆地理编码函数
  const address = await baiduMap.value?.getAddressFromPoint(p);
  if (address) {
    form.destination = address;
    ElMessage.success(`已选择地图位置: ${address}`);
  } else {
    form.destination = `${p.lng.toFixed(6)},${p.lat.toFixed(6)}`;
    ElMessage.warning("未找到该地址，使用坐标填充");
  }
};

// 百度地点搜索 suggestion API
const searchPoi = async (query: string, cb: any) => {
  if (!query) return cb([]);
  const ak = import.meta.env.VITE_BAIDU_MAP_AK;
  const url = `https://api.map.baidu.com/place/v2/suggestion?query=${encodeURIComponent(
    query
  )}&region=全国&city_limit=false&output=json&ak=${ak}`;
  try {
    const res = await fetch(url);
    const json = await res.json();
    const list =
      json.result?.map((item: any) => ({
        value: item.name,
        address: `${item.city || ""}${item.district || ""}`,
        location: item.location,
      })) ?? [];
    cb(list);
  } catch (err) {
    console.error("POI 搜索失败：", err);
    cb([]);
  }
};

// 定位目的地按钮点击
const locateDestination = () => {
  if (!form.destination) {
    ElMessage.warning("请输入目的地");
    return;
  }
  baiduMap.value?.locatePoint({ address: form.destination });
};

// 表单提交
const submitForm = () => {
  if (!form.passengerId || !form.origin || !form.destination) {
    ElMessage.warning("请填写完整信息");
    return;
  }
  ElMessage.success("行程创建成功（示例）");
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
  backdrop-filter: blur(6px);
  padding: 20px 10px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.08);
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
