import random
from locust import FastHttpUser, task, between # 注意引入 FastHttpUser

class TaxiDriver(FastHttpUser):
    # 高并发下，可以适当降低每单个用户的频率，比如 2~5秒
    wait_time = between(2, 5)

    def on_start(self):
        self.driver_id = random.randint(10000, 999999)
        self.lat = 39.90 + random.uniform(-0.05, 0.05)
        self.lng = 116.40 + random.uniform(-0.05, 0.05)

        # 1. 定义 Header 字典
        self.my_headers = {
            # 请替换为你真实的 Token
            "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyMzUyODM1IiwiZXhwIjoxNzY2OTQ1MDA3fQ.mX4M1vQZU96c-73zuuTathT37XMnsXlf8Nxy4Ebf784", 
            "Content-Type": "application/json"
        }

    @task
    def report_location(self):
        self.lat += random.uniform(-0.0001, 0.0001)
        self.lng += random.uniform(-0.0001, 0.0001)

        payload = {
            "driverId": self.driver_id,
            "lng": round(self.lng, 6),
            "lat": round(self.lat, 6)
        }

        # 2. 显式传递 headers=self.my_headers
        with self.client.put(
            "/api/driver/updateLocation", 
            json=payload, 
            headers=self.my_headers, 
            catch_response=True
        ) as response:
            if response.status_code != 200:
                response.failure(f"Error: {response.status_code}")