import { ref } from 'vue';
// 🟢 [新增] 引入封装好的 API
import { updateDriverLocation } from '../api/driver';

// 定义位置接口
interface Location {
    lat: number;
    lng: number;
}

// 🟢 [配置] 开启模拟模式 (开发阶段设为 true，上线前改为 false)
const IS_SIMULATION = true; 

// 全局状态
let timer: number | null = null;
let isSending = false;
let lastReportedLoc: Location | null = null;
let currentDriverId: number | null = null;

// 模拟模式下的起始坐标 (北京天安门附近)
let simLat = 39.9087; 
let simLng = 116.3975;

const isRunning = ref(false);

export function useLocationReporter() {

    // 启动上报 (接收司机ID)
    const startReporting = (driverId: number) => {
        if (timer) return;
        
        currentDriverId = driverId;
        isRunning.value = true;
        
        console.log(`🚕 司机(ID:${driverId}) 上线 [模式: ${IS_SIMULATION ? '模拟漂移' : '真实GPS'}]`);
        
        // 立即执行一次
        checkAndUpload();
        // 之后每 2秒 执行一次
        timer = setInterval(checkAndUpload, 2000) as unknown as number; 
    };

    // 停止上报
    const stopReporting = () => {
        if (timer) {
            clearInterval(timer);
            timer = null;
        }
        isRunning.value = false;
        currentDriverId = null;
        console.log("🛑 停止上报服务");
    };

    // 核心检测逻辑
    const checkAndUpload = () => {
        if (isSending) return; // 防堆积

        if (IS_SIMULATION) {
            // 🟢 模拟模式：随机制造微小位移
            simLat += (Math.random() - 0.5) * 0.0001;
            simLng += (Math.random() - 0.5) * 0.0001;
            handlePosition(simLat, simLng);
        } else {
            // 真实模式：调用浏览器 GPS
            if (!navigator.geolocation) {
                console.error("浏览器不支持定位");
                return;
            }
            navigator.geolocation.getCurrentPosition(
                (pos) => handlePosition(pos.coords.latitude, pos.coords.longitude),
                (err) => console.error("GPS获取失败", err),
                { enableHighAccuracy: true }
            );
        }
    };

    // 处理位置逻辑
    const handlePosition = (lat: number, lng: number) => {
        // 判断距离是否达标
        if (shouldUpload(lat, lng)) {
            doUpload(lat, lng);
        } else {
            // 距离太近，本地拦截 (仅在控制台显示，方便调试)
            console.log(`💤 距离太近，本地拦截 (Lat: ${lat.toFixed(5)})`);
        }
    };

    // 执行上报 (调用 API)
    const doUpload = async (lat: number, lng: number) => {
        if (!currentDriverId) return;

        isSending = true;
        try {
            const payload = {
                driverId: currentDriverId,
                lng: parseFloat(lng.toFixed(6)),
                lat: parseFloat(lat.toFixed(6))
            };

            // 🟢 [重构] 使用 api/driver.ts 里的方法
            await updateDriverLocation(payload);

            // 如果没有报错，说明成功
            lastReportedLoc = { lat, lng };
            console.log(`✅ 上报成功`);
            
        } catch (e) {
            // 错误由 axios 拦截器处理，或者在这里简单打印
            console.error("上报失败", e);
        } finally {
            isSending = false;
        }
    };

    // 距离计算 (Haversine 公式)
    const shouldUpload = (lat: number, lng: number): boolean => {
        if (!lastReportedLoc) return true; // 第一次必报

        const R = 6371e3; 
        const p1 = lat * Math.PI/180;
        const p2 = lastReportedLoc.lat * Math.PI/180;
        const deltaP = (lastReportedLoc.lat - lat) * Math.PI/180;
        const deltaL = (lastReportedLoc.lng - lng) * Math.PI/180;

        const a = Math.sin(deltaP/2) * Math.sin(deltaP/2) +
                  Math.cos(p1) * Math.cos(p2) *
                  Math.sin(deltaL/2) * Math.sin(deltaL/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        // 阈值：5米
        return (R * c) > 5.0;
    };

    return { isRunning, startReporting, stopReporting };
}