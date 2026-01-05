# RideNow API 接口文档

## 1. 用户服务 (service-user)

### 1.1 认证接口

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/user/login | POST | username, password | token, userId, username, role | 用户登录 |
| /api/user/register | POST | username, password, phone, email, nickname | userId, username, message | 用户注册 |
| /api/user/info | GET | userId (query) | id, username, phone, email, nickname, role, roles[] | 获取用户信息 |

### 1.2 用户管理接口

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/user/update/{userId} | POST | userId (path), userInfo (body) | success | 更新用户信息 |
| /api/user/change-password/{userId} | POST | userId (path), oldPassword, newPassword | success | 修改密码 |

---

## 2. 司机服务 (service-driver)

### 2.1 司机基础管理

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/driver/add | POST | Driver对象 | success, message, driverId | 添加司机 |
| /api/driver/delete | DELETE | driverId (query) | success, message, driverId | 删除司机 |
| /api/driver/update | PUT | Driver对象 | success, message, driverId, updatedFields | 修改司机信息 |
| /api/driver/get | GET | driverId (query) | success, message, data | 查询司机信息 |
| /api/driver/is-driver | GET | userId (query) | boolean | 判断用户是否为司机 |

### 2.2 司机位置与状态

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/driver/updateLocation | PUT | driverId, lng, lat | success, message, driverId, longitude, latitude, asyncTip | 更新司机位置（异步RabbitMQ） |
| /api/driver/online | PUT | driverId, lng, lat | success, message, driverId, longitude, latitude, timestamp | 司机上线 |
| /api/driver/offline | PUT | driverId (query) | success, message, driverId, locationRemoved, timestamp | 司机下线 |
| /api/driver/online/check | GET | driverId (query) | success, driverId, isOnline, hasLocation, longitude, latitude | 检查司机是否在线 |
| /api/driver/online/list | GET | - | success, message, count, data[] | 查询所有在线司机 |

### 2.3 司机搜索

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/driver/searchNearby | GET | lng, lat, radius (默认5000) | success, count, data[], message | 搜索附近司机（GET方式） |
| /api/driver/searchNearby | POST | lng, lat, radius | success, count, data[], message | 搜索附近司机（POST方式） |
| /api/driver/search | GET | lng, lat, radius (默认5000) | success, count, data[], message | 通过服务层搜索附近司机 |

### 2.4 测试接口

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/driver/test/init | GET | - | success, message, key | 添加测试司机到Redis |
| /api/driver/test/list | GET | - | keyExists, keyName, drivers[], count | 查看Redis中的司机数据 |
| /api/driver/test/echo | POST | lng, lat, radius | received_dto, lng, lat, radius, lng_is_null, lat_is_null, radius_is_null | 验证参数接收 |

---

## 3. 行程服务 (service-ride)

### 3.1 行程管理

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/ride/create | POST | passengerId, pickupLng, pickupLat, dropoffLng, dropoffLat | rideId, status, passengerId, driverId, fare, createTime | 创建新行程 |
| /api/ride/detail/{rideId} | GET | rideId (path) | rideId, status, passengerId, driverId, pickupLocation, dropoffLocation, fare, createTime | 获取行程详情 |
| /api/ride/history/{passengerId} | GET | passengerId (path) | rides[] | 查询乘客历史行程 |
| /api/ride/nearby/{driverId} | GET | driverId (path) | rides[] | 获取司机附近的行程 |
| /api/ride/change-status | POST | rideId, status (query) | rideId, status, message | 修改行程状态 |

### 3.2 行程流程

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/ride/accept | POST | driverId, rideId | success | 司机接单 |
| /api/ride/arrived/{rideId} | POST | rideId (path) | success | 司机到达乘客位置 |
| /api/ride/start/{rideId} | POST | rideId (path) | success | 开始行程 |
| /api/ride/end/{rideId} | POST | rideId (path) | success | 结束行程 |

---

## 4. 匹配服务 (service-matching)

### 4.1 司机匹配

| API | Method | Request | Response | Description |
|-----|--------|---------|----------|-------------|
| /api/matching/findBest | GET | lng, lat (query) | driverList[] (id, name, lng, lat, rating, matchingScore) | 查找最佳匹配司机 |

---

