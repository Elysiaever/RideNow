<template>
  <div ref="mapContainer" class="map-container"></div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";

export default defineComponent({
  name: "BaiduMap",
  emits: ["pick-point"],
  setup(_, { emit }) {
    const mapContainer = ref<HTMLDivElement | null>(null);
    let map: any;
    let geocoder: any;

    onMounted(() => {
      if (!mapContainer.value) return;

      // 初始化地图
      map = new BMap.Map(mapContainer.value);
      const defaultPoint = new BMap.Point(116.404, 39.915);
      map.centerAndZoom(defaultPoint, 12);
      map.enableScrollWheelZoom(true);

      // 初始化 Geocoder
      geocoder = new BMap.Geocoder();

      // 点击地图触发 pick-point
      map.addEventListener("click", function (e: any) {
        const point = e.point;
        emit("pick-point", { lng: point.lng, lat: point.lat });

        map.clearOverlays();
        const marker = new BMap.Marker(point);
        map.addOverlay(marker);
      });
    });

    // 根据经纬度或地址移动地图并标记
    const locatePoint = (data: { lng?: number; lat?: number; address?: string }) => {
      if (!map) return;
      if (data.lng != null && data.lat != null) {
        const point = new BMap.Point(data.lng, data.lat);
        map.clearOverlays();
        const marker = new BMap.Marker(point);
        map.addOverlay(marker);
        map.centerAndZoom(point, 15);
      } else if (data.address) {
        geocoder.getPoint(data.address, (point: any) => {
          if (point) {
            map.clearOverlays();
            const marker = new BMap.Marker(point);
            map.addOverlay(marker);
            map.centerAndZoom(point, 15);
          } else {
            alert("未找到该地址");
          }
        });
      }
    };

    // 根据经纬度获取地址 (逆地理编码)
    const getAddressFromPoint = (point: { lng: number; lat: number }) => {
      return new Promise<string | null>((resolve) => {
        if (!geocoder) return resolve(null);
        const bmapPoint = new BMap.Point(point.lng, point.lat);
        geocoder.getLocation(bmapPoint, (result: any) => {
          if (result) resolve(result.address);
          else resolve(null);
        });
      });
    };

    return { mapContainer, locatePoint, getAddressFromPoint };
  },
});
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 100vh;
}
</style>
