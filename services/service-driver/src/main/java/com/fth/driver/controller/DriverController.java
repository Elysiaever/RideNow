package com.fth.driver.controller;

import com.fth.common.core.constant.RedisConstant;
import com.fth.driver.domain.dto.LocationUpdateDto;
import com.fth.driver.domain.dto.SearchDriverDto;
import com.fth.driver.domain.model.Driver;
import com.fth.driver.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DriverService driverService;

    /**
     * 【测试用】添加测试司机到 Redis
     */
    @GetMapping("/test/init")
    public Map<String, Object> initTestData() {
        log.info("========== 开始添加测试数据 ==========");

        // 清空旧数据
        redisTemplate.delete(RedisConstant.DRIVER_GEO_KEY);
        log.info("已清空旧数据");

        // 添加4个测试司机（北京天安门附近）
        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_KEY,
                new Point(116.404, 39.915),
                "driver001"
        );
        log.info("已添加 driver001");

        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_KEY,
                new Point(116.408, 39.918),
                "driver002"
        );
        log.info("已添加 driver002");

        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_KEY,
                new Point(116.400, 39.910),
                "driver003"
        );
        log.info("已添加 driver003");

        redisTemplate.opsForGeo().add(
                RedisConstant.DRIVER_GEO_KEY,
                new Point(116.420, 39.920),
                "driver004"
        );
        log.info("已添加 driver004");

        log.info("========== 测试数据添加完成 ==========");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "✅ 成功添加4个测试司机！");
        result.put("key", RedisConstant.DRIVER_GEO_KEY);
        return result;
    }

    /**
     * 搜索附近司机 - GET 方式（使用 @RequestParam）
     */
    @GetMapping("/searchNearby")
    public Map<String, Object> searchNearby(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam(defaultValue = "5000") double radius) {

        log.info("╔════════════════════════════════════════╗");
        log.info("║        搜索附近司机                     ║");
        log.info("╚════════════════════════════════════════╝");
        log.info("请求参数: lng={}, lat={}, radius={}", lng, lat, radius);
        log.info("使用的 Key: {}", RedisConstant.DRIVER_GEO_KEY);

        // 1. 检查 key 是否存在
        Boolean keyExists = redisTemplate.hasKey(RedisConstant.DRIVER_GEO_KEY);
        log.info("Key 是否存在: {}", keyExists);

        // 2. 创建搜索参数
        Point center = new Point(lng, lat);
        Circle circle = new Circle(center, radius);

        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        .includeDistance()      // 包含距离
                        .includeCoordinates()   // 包含坐标
                        .sortAscending();       // 按距离升序

        // 3. 执行搜索
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults =
                redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_KEY, circle, args);

        // 4. 处理结果
        Map<String, Object> result = new HashMap<>();

        if (geoResults == null || geoResults.getContent().isEmpty()) {
            log.warn("❌ 没有找到附近的司机");
            result.put("success", true);
            result.put("count", 0);
            result.put("data", List.of());
            result.put("message", "附近没有可用司机");
            return result;
        }

        // 5. 转换结果
        List<Map<String, Object>> drivers = geoResults.getContent().stream()
                .map(r -> {
                    Map<String, Object> driver = new HashMap<>();
                    driver.put("driverId", r.getContent().getName());
                    driver.put("distance", r.getDistance().getValue());
                    driver.put("longitude", r.getContent().getPoint().getX());
                    driver.put("latitude", r.getContent().getPoint().getY());

                    log.info("  - 司机: {}, 距离: {} 米",
                            r.getContent().getName(),
                            r.getDistance().getValue());

                    return driver;
                })
                .collect(Collectors.toList());

        log.info("✅ 找到 {} 个司机", drivers.size());
        log.info("════════════════════════════════════════");

        result.put("success", true);
        result.put("count", drivers.size());
        result.put("data", drivers);
        return result;
    }

    /**
     * 搜索附近司机 - POST 方式（使用 @RequestBody）
     */
    @PostMapping("/searchNearby")
    public Map<String, Object> searchNearbyPost(@RequestBody SearchDriverDto dto) {
        log.info("收到 POST 请求，参数: {}", dto);
        return searchNearby(dto.getLng(), dto.getLat(), dto.getRadius());
    }

    /**
     * 更新司机位置
     */
    @PutMapping("/updateLocation")
    public Map<String, Object> updateLocation(@RequestBody LocationUpdateDto dto) {
        log.info("╔════════════════════════════════════════╗");
        log.info("║        更新司机位置                     ║");
        log.info("╚════════════════════════════════════════╝");
        log.info("司机ID: {}", dto.getDriverId());
        log.info("经度: {}, 纬度: {}", dto.getLng(), dto.getLat());
        log.info("使用的 Key: {}", RedisConstant.DRIVER_GEO_KEY);

        try {
            // 添加或更新司机位置
            Long count = redisTemplate.opsForGeo().add(
                    RedisConstant.DRIVER_GEO_KEY,
                    new Point(dto.getLng(), dto.getLat()),
                    dto.getDriverId().toString()
            );

            log.info("更新结果: {} (1=新增, 0=更新已有位置)", count);
            log.info("════════════════════════════════════════");

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "位置更新成功");
            result.put("driverId", dto.getDriverId());
            result.put("longitude", dto.getLng());
            result.put("latitude", dto.getLat());
            result.put("isNew", count == 1);

            return result;

        } catch (Exception e) {
            log.error("❌ 更新位置失败: {}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "位置更新失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 【测试用】验证参数接收
     */
    @PostMapping("/test/echo")
    public Map<String, Object> echo(@RequestBody SearchDriverDto dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("received_dto", dto);
        result.put("lng", dto.getLng());
        result.put("lat", dto.getLat());
        result.put("radius", dto.getRadius());
        result.put("lng_is_null", dto.getLng() == null);
        result.put("lat_is_null", dto.getLat() == null);
        result.put("radius_is_null", dto.getRadius() == null);
        return result;
    }

    /**
     * 【测试用】查看 Redis 中的数据
     */
    @GetMapping("/test/list")
    public Map<String, Object> listDrivers() {
        Map<String, Object> result = new HashMap<>();

        Boolean exists = redisTemplate.hasKey(RedisConstant.DRIVER_GEO_KEY);
        result.put("keyExists", exists);
        result.put("keyName", RedisConstant.DRIVER_GEO_KEY);

        if (exists) {
            // 搜索大范围内的所有司机
            Point center = new Point(116.404, 39.915);
            Circle circle = new Circle(center, 50000);  // 50公里

            RedisGeoCommands.GeoRadiusCommandArgs args =
                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                            .includeDistance()
                            .includeCoordinates();

            GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults =
                    redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_KEY, circle, args);

            if (geoResults != null) {
                List<Map<String, Object>> drivers = geoResults.getContent().stream()
                        .map(r -> {
                            Map<String, Object> driver = new HashMap<>();
                            driver.put("driverId", r.getContent().getName());
                            driver.put("distance", r.getDistance().getValue());
                            driver.put("longitude", r.getContent().getPoint().getX());
                            driver.put("latitude", r.getContent().getPoint().getY());
                            return driver;
                        })
                        .collect(Collectors.toList());

                result.put("drivers", drivers);
                result.put("count", drivers.size());
            }
        }

        return result;
    }

    // ==================== 其他接口保持不变 ====================

    /**
     * 添加司机
     */
    @PostMapping("/add")
    public Map<String, Object> addDriver(@RequestBody Driver driver) {
        log.info("添加司机: {}", driver);

        Map<String, Object> result = new HashMap<>();
        try {
            driverService.addDriver(driver);

            result.put("success", true);
            result.put("message", "司机添加成功");
            result.put("driverId", driver.getId());
            //result.put("driverName", driver.getName());

            log.info("✅ 司机添加成功，ID: {}", driver.getId());

        } catch (Exception e) {
            log.error("❌ 添加司机失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除司机
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteDriver(@RequestParam Long driverId) {
        log.info("删除司机，ID: {}", driverId);

        Map<String, Object> result = new HashMap<>();
        try {
            // 先查询司机是否存在
            Driver driver = driverService.getDriverById(driverId);

            if (driver == null) {
                result.put("success", false);
                result.put("message", "司机不存在");
                result.put("driverId", driverId);
                return result;
            }

            driverService.deleteDriver(driverId);

            result.put("success", true);
            result.put("message", "司机删除成功");
            result.put("driverId", driverId);

            log.info("✅ 司机删除成功，ID: {}", driverId);

        } catch (Exception e) {
            log.error("❌ 删除司机失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            result.put("driverId", driverId);
        }

        return result;
    }

    /**
     * 修改司机信息
     */
    @PutMapping("/update")
    public Map<String, Object> updateDriver(@RequestBody Driver driver) {
        log.info("更新司机信息: {}", driver);

        Map<String, Object> result = new HashMap<>();
        try {
            // 检查司机是否存在
            Driver existingDriver = driverService.getDriverById(driver.getId());

            if (existingDriver == null) {
                result.put("success", false);
                result.put("message", "司机不存在，无法更新");
                result.put("driverId", driver.getId());
                return result;
            }

            driverService.updateDriver(driver);

            result.put("success", true);
            result.put("message", "司机信息更新成功");
            result.put("driverId", driver.getId());
            result.put("updatedFields", driver);

            log.info("✅ 司机信息更新成功，ID: {}", driver.getId());

        } catch (Exception e) {
            log.error("❌ 更新司机失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 查询司机信息
     */
    @GetMapping("/get")
    public Map<String, Object> getDriver(@RequestParam Long driverId) {
        log.info("查询司机信息，ID: {}", driverId);

        Map<String, Object> result = new HashMap<>();
        try {
            Driver driver = driverService.getDriverById(driverId);

            if (driver == null) {
                result.put("success", false);
                result.put("message", "司机不存在");
                result.put("driverId", driverId);
                result.put("data", null);
            } else {
                result.put("success", true);
                result.put("message", "查询成功");
                result.put("driverId", driverId);
                result.put("data", driver);

            }

        } catch (Exception e) {
            log.error("❌ 查询司机失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
            result.put("driverId", driverId);
        }

        return result;
    }

    /**
     * 司机上线
     */
    @PutMapping("/online")
    public Map<String, Object> driverOnline(@RequestBody LocationUpdateDto locationUpdateDto) {
        log.info("╔════════════════════════════════════════╗");
        log.info("║        司机上线                         ║");
        log.info("╚════════════════════════════════════════╝");
        log.info("司机ID: {}", locationUpdateDto.getDriverId());
        log.info("位置: ({}, {})", locationUpdateDto.getLng(), locationUpdateDto.getLat());

        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 设置司机上线状态
            driverService.driverOnline(
                    locationUpdateDto.getDriverId(),
                    locationUpdateDto.getLng(),
                    locationUpdateDto.getLat()
            );

            // 2. 更新司机位置到 Redis GEO
            Long geoCount = redisTemplate.opsForGeo().add(
                    RedisConstant.DRIVER_GEO_KEY,
                    new Point(locationUpdateDto.getLng(), locationUpdateDto.getLat()),
                    locationUpdateDto.getDriverId().toString()
            );

            result.put("success", true);
            result.put("message", "司机上线成功");
            result.put("driverId", locationUpdateDto.getDriverId());
            result.put("longitude", locationUpdateDto.getLng());
            result.put("latitude", locationUpdateDto.getLat());
            result.put("locationUpdated", geoCount != null);
            result.put("timestamp", System.currentTimeMillis());

            log.info("✅ 司机上线成功");
            log.info("════════════════════════════════════════");

        } catch (Exception e) {
            log.error("❌ 司机上线失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "上线失败: " + e.getMessage());
            result.put("driverId", locationUpdateDto.getDriverId());
        }

        return result;
    }

    /**
     * 司机下线
     */
    @PutMapping("/offline")
    public Map<String, Object> driverOffline(@RequestParam Long driverId) {
        log.info("╔════════════════════════════════════════╗");
        log.info("║        司机下线                         ║");
        log.info("╚════════════════════════════════════════╝");
        log.info("司机ID: {}", driverId);

        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 设置司机下线状态
            driverService.driverOffline(driverId);

            // 2. 从 Redis GEO 中删除司机位置
            Long removed = redisTemplate.opsForGeo().remove(
                    RedisConstant.DRIVER_GEO_KEY,
                    driverId.toString()
            );

            result.put("success", true);
            result.put("message", "司机下线成功");
            result.put("driverId", driverId);
            result.put("locationRemoved", removed != null && removed > 0);
            result.put("timestamp", System.currentTimeMillis());

            log.info("✅ 司机下线成功，位置信息已移除");
            log.info("════════════════════════════════════════");

        } catch (Exception e) {
            log.error("❌ 司机下线失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "下线失败: " + e.getMessage());
            result.put("driverId", driverId);
        }

        return result;
    }

    /**
     * 【新增】批量查询在线司机
     */
    @GetMapping("/online/list")
    public Map<String, Object> getOnlineDrivers() {
        log.info("查询所有在线司机");

        Map<String, Object> result = new HashMap<>();
        try {
            // 从 Redis GEO 中获取所有司机
            Point center = new Point(116.404, 39.915);
            Circle circle = new Circle(center, 100000);  // 100公里范围

            RedisGeoCommands.GeoRadiusCommandArgs args =
                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                            .includeDistance()
                            .includeCoordinates();

            GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults =
                    redisTemplate.opsForGeo().radius(RedisConstant.DRIVER_GEO_KEY, circle, args);

            if (geoResults == null || geoResults.getContent().isEmpty()) {
                result.put("success", true);
                result.put("message", "当前没有在线司机");
                result.put("count", 0);
                result.put("data", List.of());
                return result;
            }

            List<Map<String, Object>> onlineDrivers = geoResults.getContent().stream()
                    .map(r -> {
                        Map<String, Object> driver = new HashMap<>();
                        driver.put("driverId", r.getContent().getName());
                        driver.put("longitude", r.getContent().getPoint().getX());
                        driver.put("latitude", r.getContent().getPoint().getY());
                        return driver;
                    })
                    .collect(Collectors.toList());

            result.put("success", true);
            result.put("message", "查询成功");
            result.put("count", onlineDrivers.size());
            result.put("data", onlineDrivers);

            log.info("✅ 找到 {} 个在线司机", onlineDrivers.size());

        } catch (Exception e) {
            log.error("❌ 查询在线司机失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 【新增】检查司机是否在线
     */
    @GetMapping("/online/check")
    public Map<String, Object> checkDriverOnline(@RequestParam Long driverId) {
        log.info("检查司机是否在线，ID: {}", driverId);

        Map<String, Object> result = new HashMap<>();
        try {
            // 检查在线状态（从 Redis 字符串 key 检查）
            boolean isOnline = driverService.isDriverOnline(driverId);

            // 检查位置是否存在（从 Redis GEO 检查）
            List<Point> positions = redisTemplate.opsForGeo().position(
                    RedisConstant.DRIVER_GEO_KEY,
                    driverId.toString()
            );

            boolean hasLocation = positions != null && !positions.isEmpty() && positions.get(0) != null;

            result.put("success", true);
            result.put("driverId", driverId);
            result.put("isOnline", isOnline);
            result.put("hasLocation", hasLocation);

            if (hasLocation && positions.get(0) != null) {
                result.put("longitude", positions.get(0).getX());
                result.put("latitude", positions.get(0).getY());
            }

            log.info("司机 {} 在线状态: {}, 位置: {}", driverId, isOnline, hasLocation);

        } catch (Exception e) {
            log.error("❌ 检查司机在线状态失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "检查失败: " + e.getMessage());
        }

        return result;
    }

    @GetMapping("/is-driver")
    public boolean isDriver(@RequestParam Long userId) {
        return driverService.isDriver(userId);
    }
}