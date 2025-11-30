<template>
  <div class="page">
    <!-- 地图组件 -->
    <BaiduMap 
      ref="baiduMap" 
      :pickMode="pickMode" 
      @pick-point="onPickPoint"
      :drivers="drivers" 
    />

    <!-- 表单面板：底部居中 -->
    <div class="bottom-panel">
      <el-form :model="form" label-width="90px">
        <el-form-item label="乘客ID">
          <el-input v-model="form.passengerId" :style="{width: 'calc(100% - 80px)'}"/>
        </el-form-item>

        <el-form-item label="出发地">
          <el-input
            v-model="form.origin.display"
            placeholder="点击地图选择出发地"
            clearable
            :style="{width: 'calc(100% - 100px)'}"
            @focus="pickMode = 'origin'"
          />
          <el-button type="primary" @click="locateOrigin" style="width:90px; margin-left:10px;">定位</el-button>
        </el-form-item>

        <el-form-item label="目的地">
          <el-input
            v-model="form.destination.display"
            placeholder="点击地图选择目的地"
            clearable
            :style="{width: 'calc(100% - 100px)'}"
            @focus="pickMode = 'destination'"
          />
          <el-button type="primary" @click="locateDestination" style="width:90px; margin-left:10px;">定位</el-button>
        </el-form-item>

        <el-button type="primary" @click="submitForm" class="submit-btn">
          开始叫车
        </el-button>
      </el-form>
    </div>
    <div class="driver-list-panel">
  <h3>附近司机</h3>

  <div v-for="d in drivers" :key="d.id" class="driver-card">
    <p><strong>{{ d.name }}</strong></p>
    <p>电话：{{ d.phone }}</p>
    <p>车牌：{{ d.plateNumber }}</p>
  </div>
</div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import BaiduMap from "@/components/BaiduMap.vue";
import request from "@/utils/request";

const baiduMap = ref<any>(null);
const pickMode = ref<"origin" | "destination">("origin");

const form = reactive({
  passengerId: "",
  origin: { display: "", lng: null as number | null, lat: null as number | null },
  destination: { display: "", lng: null as number | null, lat: null as number | null }
});

const onPickPoint = (p: { lng:number; lat:number; address:string; mode:string }) => {
  if(p.mode === "origin") form.origin = { display:p.address, lng:p.lng, lat:p.lat };
  else form.destination = { display:p.address, lng:p.lng, lat:p.lat };
  ElMessage.success(`已选择：${p.address}`);
};

const locateOrigin = async () => {
  if(!form.origin.display){ ElMessage.warning("请输入出发地地址"); return; }
  try{
    const p = await baiduMap.value.locatePoint({ address: form.origin.display, type:"origin" });
    form.origin.lng = p.lng; form.origin.lat = p.lat;
  }catch(err){ ElMessage.error("定位失败："+err);}
};
const locateDestination = async () => {
  if(!form.destination.display){ ElMessage.warning("请输入目的地地址"); return; }
  try{
    const p = await baiduMap.value.locatePoint({ address: form.destination.display, type:"destination" });
    form.destination.lng = p.lng; form.destination.lat = p.lat;
  }catch(err){ ElMessage.error("定位失败："+err);}
};

const submitForm = async () => {
  if(!form.passengerId){ ElMessage.warning("请输入乘客ID"); return; }

  try{
    if(!form.origin.lng && form.origin.display){
      const p = await baiduMap.value.locatePoint({ address: form.origin.display, type:"origin" });
      form.origin.lng = p.lng; form.origin.lat = p.lat;
    }
    if(!form.destination.lng && form.destination.display){
      const p = await baiduMap.value.locatePoint({ address: form.destination.display, type:"destination" });
      form.destination.lng = p.lng; form.destination.lat = p.lat;
    }
    if(!form.origin.lng || !form.destination.lng){ ElMessage.error("请先选择出发地和目的地"); return; }

    await request.post("/api/ride/create", {
      passengerId: form.passengerId,
      originAddress: form.origin.display, originLng: form.origin.lng, originLat: form.origin.lat,
      destAddress: form.destination.display, destLng: form.destination.lng, destLat: form.destination.lat
    });

    ElMessage.success("行程创建成功！");
  }catch(err){ console.error(err); ElMessage.error("提交失败："+err);}
};

interface Driver { id:string; name:string; phone:string; plateNumber?:string; lng:number; lat:number; }
const drivers: Driver[] = [
  { id:"1", name:"张三", phone:"13800000001", plateNumber:"沪A12345", lng:116.404, lat:39.915 },
  { id:"2", name:"李四", phone:"13800000002", plateNumber:"沪B54321", lng:116.414, lat:39.925 },
  { id:"3", name:"王五", phone:"13800000003", plateNumber:"沪C67890", lng:116.424, lat:39.935 }
];
</script>

<style scoped>
.page{ width:100vw; height:100vh; position:relative; overflow:hidden; }
.bottom-panel{
  position:fixed; bottom:20px; left:50%; transform:translateX(-50%);
  width:650px; background:rgba(255,255,255,0.95); padding:20px;
  box-shadow:0 2px 15px rgba(0,0,0,0.2); border-radius:8px; z-index:10;
}
@media (max-width:680px){ .bottom-panel{ width:calc(100% - 40px); bottom:10px; } }
.submit-btn{ width:100%; margin-top:15px; }
.driver-list-panel {
  position: fixed;
  right: 20px;
  top: 20px;
  width: 260px;
  background: rgba(255,255,255,0.95);
  box-shadow: 0 2px 12px rgba(0,0,0,0.2);
  padding: 15px;
  border-radius: 8px;
  z-index: 20; /* 高于地图，低于底部表单 */
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}

.driver-card {
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.driver-card:last-child {
  border-bottom: none;
}

</style>
