import random
import time
from locust import HttpUser, task, between, events

class TaxiDriver(HttpUser):
    # ==========================
    # 核心配置：上报频率
    # ==========================
    # 题目要求：1次 / 2秒
    # between(2, 2) 强制每个用户等待固定的 2 秒
    wait_time = between(2, 2)

    def on_start(self):
        """
        当模拟的司机“上线”时执行一次。
        用于初始化司机的 ID 和 初始位置。
        """
        # 1. 生成随机司机ID (模拟 2 万个不同的司机)
        self.driver_id = random.randint(10000, 999999)
        
        # 2. 生成初始位置 (这里以北京天安门附近为例)
        # 实际项目中，你可以根据你的地图业务更换基准坐标
        self.lat = 39.9042 + random.uniform(-0.05, 0.05)
        self.lon = 116.4074 + random.uniform(-0.05, 0.05)

    @task
    def report_location(self):
        """
        这是主要的测试任务：上报位置。
        Locust 会每隔 2 秒自动调用一次这个方法。
        """
        # 1. 模拟车辆移动 (让经纬度发生微小的漂移)
        # 0.0001 纬度大约等于 11米，模拟汽车行驶
        self.lat += random.uniform(-0.0001, 0.0001)
        self.lon += random.uniform(-0.0001, 0.0001)

        # 2. 构造发给 Java 后端的 JSON 数据
        # 注意：这里的字段名 (driverId 等) 必须和你 Java Controller 接收的字段一致
        payload = {
            "driverId": self.driver_id,
            "latitude": round(self.lat, 6),  # 保留6位小数
            "longitude": round(self.lon, 6),
            "status": "ONLINE",
            # 生成当前时间戳 (Java通常用 13位 毫秒级时间戳)
            "timestamp": int(time.time() * 1000) 
        }

        # 3. 发送 POST 请求
        # 注意：这里只写 API 的路径，不要写 http://localhost...
        # 域名部分我们在 Web 界面或者命令行里配置
        with self.client.post("/api/driver/location", json=payload, catch_response=True) as response:
            
            # 4. 简单的断言 (判断测试是否成功)
            if response.status_code == 200:
                # 如果后端返回 200，Locust 记为成功
                response.success()
            else:
                # 如果报错，标记为失败，并在 Locust 界面显示错误详情
                response.failure(f"Status: {response.status_code}, Driver: {self.driver_id}")