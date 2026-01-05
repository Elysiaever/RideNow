<template>
  <div ref="mapContainer" class="map-container">
    <!-- 司机信息弹窗 -->
    <el-dialog 
      title="司机信息" 
      v-model="isDriverDialogOpen"
      width="300px" 
      append-to-body 
      :z-index="9999"
      :before-close="handleDialogClose"
    >
      <div class="driver-info">
        <p><span class="label">姓名：</span>{{ currentDriver?.name }}</p>
        <p><span class="label">手机号：</span>{{ currentDriver?.phone }}</p>
        <p><span class="label">车牌号：</span>{{ currentDriver?.plateNumber || "暂无" }}</p>
        <p><span class="label">司机ID：</span>{{ currentDriver?.id }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, watch, nextTick } from "vue";
import { ElDialog, ElMessage } from "element-plus";
import { loadBaiduMap } from '@/utils/baiduMapLoader';
declare const BMap: any;

interface Driver { id:string; name:string; phone:string; plateNumber?:string; lng:number; lat:number; }

export default defineComponent({
  name:"BaiduMap",
  components:{ ElDialog },
  props:{
    pickMode:{ type:String, default:"origin" },
    drivers:{ type:Array as ()=>Driver[], default:()=>[] }
  },
  emits:["pick-point"],
  setup(props,{ emit }) {
    const mapContainer = ref<HTMLDivElement | null>(null);
    let map:any, geocoder:any;
    
    // 将图标定义移到初始化函数中，确保百度地图API已加载
    let iconOrigin: any, iconDestination: any, iconDriver: any;
    let originMarker:any = null, destMarker:any = null, driverMarkers:any[] = [];

    const isDriverDialogOpen = ref(false);
    const currentDriver = ref<Driver|null>(null);

    // 弹窗关闭处理
    const handleDialogClose = () => {
      isDriverDialogOpen.value = false;
      currentDriver.value = null;
    };

    // 渲染司机Marker - 修复点击事件
    const renderDrivers = (drivers:Driver[])=>{
      if (!map) return;
      
      driverMarkers.forEach(m=>{
        if(map && map.getOverlay(m)) map.removeOverlay(m);
      });
      driverMarkers = [];

      drivers.forEach((driver, index)=>{
        try {
          if(!driver.lng || !driver.lat || isNaN(driver.lng) || isNaN(driver.lat)){
            ElMessage.warning(`司机${driver.name}（ID:${driver.id}）位置无效`);
            return;
          }

          const point = new BMap.Point(driver.lng, driver.lat);
          const marker = new BMap.Marker(point, { 
            icon: iconDriver,
            enableClicking: true,
            zIndex: 1000 + index
          });

          // 使用更可靠的事件绑定方式
          marker.addEventListener("click", (e:any)=>{
            console.log("点击司机Marker：", driver);
            
            // 立即阻止所有可能的事件传播
            if(e.domEvent) {
              e.domEvent.stopPropagation();
              e.domEvent.preventDefault();
            }
            // e.stopPropagation();
            // e.preventDefault();
            
            // 使用 setTimeout 确保在事件循环结束后打开弹窗
            setTimeout(() => {
              currentDriver.value = driver;
              isDriverDialogOpen.value = true;
              
              // 地图聚焦到司机位置
              map.panTo(point);
              map.setZoom(15);
              
              // 视觉反馈
              if(marker.setAnimation) {
                marker.setAnimation(2);
                setTimeout(()=>marker.setAnimation(null), 1000);
              }
            }, 0);
          });

          // 添加标题提示
          marker.setTitle(`点击查看${driver.name}的信息`);

          if(map) map.addOverlay(marker);
          driverMarkers.push(marker);

        } catch (err) {
          console.error("创建司机Marker失败：", err);
        }
      });
    };

    // 初始化地图 - 优化事件处理
    const initMap = async()=>{
      if(!mapContainer.value) return;
      
      try {
        // 等待百度地图API加载完成
        await loadBaiduMap('45fhg18PIZtRwysqRlZrIQBG0NymF9XR');
        await nextTick();
        
        // 现在API已加载，初始化图标
        iconOrigin = new BMap.Icon("https://maps.gstatic.com/mapfiles/ms2/micons/green-dot.png", new BMap.Size(32,32));
        iconDestination = new BMap.Icon("https://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png", new BMap.Size(32,32));
        iconDriver = new BMap.Icon(
          "https://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png", 
          new BMap.Size(40,40),
          { anchor: new BMap.Size(20, 40) }
        );
        
        mapContainer.value.style.width = `${window.innerWidth}px`;
        mapContainer.value.style.height = `${window.innerHeight}px`;
        
        // 创建地图实例，禁用默认的双击缩放等可能干扰的事件
        map = new BMap.Map(mapContainer.value, { 
          enableMapClick: true,
          enableDoubleClickZoom: false // 禁用双击缩放，减少事件冲突
        });
        map.centerAndZoom(new BMap.Point(116.404,39.915),12);
        map.enableScrollWheelZoom(true);
        geocoder = new BMap.Geocoder();

        // 地图点击事件 - 添加更严格的条件判断
        map.addEventListener("click", (e:any)=>{
          // 如果弹窗打开，则不处理地图点击
          if(isDriverDialogOpen.value) return;
          
          // 如果点击的是覆盖物，且不是起点/终点标记，则不处理
          if(e.overlay) {
            const isDriverMarker = driverMarkers.includes(e.overlay);
            const isOriginMarker = e.overlay === originMarker;
            const isDestMarker = e.overlay === destMarker;
            
            // 只有点击空白地图区域或起点终点标记时才执行选点
            if(!isOriginMarker && !isDestMarker) return;
          }
          
          const point = e.point;
          geocoder.getLocation(point, (res:any)=>{
            const address = res?.address || "未知地点";
            emit("pick-point", { lng:point.lng, lat:point.lat, address, mode:props.pickMode });
          });
          
          if(props.pickMode==="origin"){
            if(originMarker) map.removeOverlay(originMarker);
            originMarker = new BMap.Marker(point, { icon:iconOrigin, zIndex: 100 });
            map.addOverlay(originMarker);
          }else{
            if(destMarker) map.removeOverlay(destMarker);
            destMarker = new BMap.Marker(point, { icon:iconDestination, zIndex: 100 });
            map.addOverlay(destMarker);
          }
        });

        // 初始渲染司机Marker
        renderDrivers(props.drivers);

        window.addEventListener("resize", ()=>{
          if(mapContainer.value && map){
            mapContainer.value.style.width = `${window.innerWidth}px`;
            mapContainer.value.style.height = `${window.innerHeight}px`;
            map.resize();
          }
        });
      } catch (error) {
        console.error('百度地图API加载失败:', error);
        ElMessage.error('地图加载失败，请检查网络连接');
      }
    };

    onMounted(()=>{
      initMap();
    });

    watch(() => props.drivers, (newDrivers) => {
      if(map) renderDrivers(newDrivers);
    }, { deep:true });

    const locatePoint = (data:{ lng?:number; lat?:number; address?:string; type:"origin"|"destination" }) =>
      new Promise<{lng:number;lat:number;address:string}>((resolve,reject)=>{
        if(!map) return reject("地图未初始化");
        const drawMarker=(point:any)=>{
          if(data.type==="origin"){
            if(originMarker) map.removeOverlay(originMarker);
            originMarker = new BMap.Marker(point, { icon:iconOrigin, zIndex: 100 });
            map.addOverlay(originMarker);
          }else{
            if(destMarker) map.removeOverlay(destMarker);
            destMarker = new BMap.Marker(point, { icon:iconDestination, zIndex: 100 });
            map.addOverlay(destMarker);
          }
          map.centerAndZoom(point, 15);
          resolve({ lng:point.lng, lat:point.lat, address:data.address||"" });
        };
        if(data.lng!=null && data.lat!=null) drawMarker(new BMap.Point(data.lng,data.lat));
        else if(data.address){
          geocoder.getPoint(data.address, (point:any)=>{
            if(!point) return reject("未找到该地址");
            drawMarker(point);
          });
        }else reject("无地址或坐标");
      });

    return { 
      mapContainer, 
      locatePoint, 
      isDriverDialogOpen, 
      currentDriver,
      handleDialogClose 
    };
  }
});
</script>

<style scoped>
.map-container {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  z-index: 1 !important;
  margin: 0 !important;
  padding: 0 !important;
}

.driver-info {
  line-height: 1.8;
  font-size: 14px;
}

.driver-info .label {
  color: #666;
  font-weight: 500;
  display: inline-block;
  width: 60px;
}

/* 确保弹窗层级最高 */
::v-deep .el-dialog__wrapper {
  z-index: 9999 !important;
}

/* 优化百度地图Marker点击区域 */
::v-deep .BMap_Marker {
  pointer-events: auto !important;
  cursor: pointer !important;
}
</style>