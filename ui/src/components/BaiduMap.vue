<template>
  <div ref="mapContainer" class="map-container"></div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";

// 声明 Baidu Map
declare const BMap: any;

export default defineComponent({
  name: "BaiduMap",

  props: {
    pickMode: {
      type: String,
      default: "origin"  // "origin" | "destination"
    }
  },

  emits: ["pick-point"],

  setup(props, { emit }) {
    const mapContainer = ref<HTMLDivElement | null>(null);

    let map: any;
    let geocoder: any;

    // 绿色 = 出发地     红色 = 目的地
    const iconOrigin = new BMap.Icon(
      "https://maps.gstatic.com/mapfiles/ms2/micons/green-dot.png",
      new BMap.Size(32, 32)
    );
    const iconDestination = new BMap.Icon(
      "https://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png",
      new BMap.Size(32, 32)
    );

    // 存放两个独立 marker
    let originMarker: any = null;
    let destMarker: any = null;

    onMounted(() => {
      if (!mapContainer.value) return;

      map = new BMap.Map(mapContainer.value);
      map.centerAndZoom(new BMap.Point(116.404, 39.915), 12);
      map.enableScrollWheelZoom(true);

      geocoder = new BMap.Geocoder();

      // 点击地图 → 拾取坐标 + 标注
      map.addEventListener("click", (e: any) => {
        const point = e.point;

        geocoder.getLocation(point, (res: any) => {
          const address = res?.address ?? "未知地点";

          emit("pick-point", {
            lng: point.lng,
            lat: point.lat,
            address,
            mode: props.pickMode  // ⭐ 返回当前模式
          });
        });

        // ⭐ 根据模式绘制标记
        if (props.pickMode === "origin") {
          if (originMarker) map.removeOverlay(originMarker);
          originMarker = new BMap.Marker(point, { icon: iconOrigin });
          map.addOverlay(originMarker);
        } else {
          if (destMarker) map.removeOverlay(destMarker);
          destMarker = new BMap.Marker(point, { icon: iconDestination });
          map.addOverlay(destMarker);
        }
      });
    });

    /**
     * ⭐ 外部调用的定位方法（必须带 type）
     * locatePoint({
     *   lng, lat, address, type: "origin" | "destination"
     * })
     */
    const locatePoint = (data: {
      lng?: number;
      lat?: number;
      address?: string;
      type: "origin" | "destination";
    }) => {
      if (!map) return;

      const drawMarker = (point: any) => {
        if (data.type === "origin") {
          if (originMarker) map.removeOverlay(originMarker);
          originMarker = new BMap.Marker(point, { icon: iconOrigin });
          map.addOverlay(originMarker);
        } else {
          if (destMarker) map.removeOverlay(destMarker);
          destMarker = new BMap.Marker(point, { icon: iconDestination });
          map.addOverlay(destMarker);
        }

        map.centerAndZoom(point, 15);
      };

      if (data.lng != null && data.lat != null) {
        drawMarker(new BMap.Point(data.lng, data.lat));
      } else if (data.address) {
        geocoder.getPoint(data.address, (point: any) => {
          if (!point) {
            alert("未找到该地址");
            return;
          }
          drawMarker(point);
        });
      }
    };

    return { mapContainer, locatePoint };
  }
});
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 100vh;
}
</style>