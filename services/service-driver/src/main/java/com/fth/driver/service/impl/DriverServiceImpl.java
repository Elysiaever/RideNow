package com.fth.driver.service.impl;

import com.fth.driver.domain.data.DriverWithDistance;
import com.fth.driver.domain.model.Driver;
import com.fth.driver.mapper.DriverMapper;
import com.fth.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fth.common.service.RedisService;

import java.util.ArrayList;
import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private DriverMapper driverMapper;

    @Override
    public void updateLocation(Long driverId, Double lng, Double lat) {
        redisService.updateDriverLocation(driverId, lng, lat);
    }

    @Override
    public List<?> searchNearby(Double lng, Double lat, Double radius){
        return redisService.searchDriverNearby(lng, lat, radius);
    }

    @Override
    public void addDriver(Driver driver) {
        driverMapper.insert(driver);
    }

    @Override
    public void deleteDriver(Long driverId){
        driverMapper.deleteById(driverId);
    }

    @Override
    public void updateDriver(Driver driver){
        driverMapper.updateById(driver);
    }

    @Override
    public Driver getDriverById(Long driverId){
        return driverMapper.selectById(driverId);
    }

    /**
     * 模拟返回所有在线可接单的司机数据（包含经纬度、评分等核心属性）
     * 真实场景中，该方法会处理司机在线状态、接单权限等校验
     */
    @Override
    public List<DriverWithDistance> getAllOnlineAndAvailableDrivers() {
        // 构建司机列表容器
        List<DriverWithDistance> onlineDrivers = new ArrayList<>();

        // 模拟生成6名在线可接单司机（差异化经纬度、评分，方便验证匹配效果）
        // 司机1：在线、可接单、距离较近、高评分
        DriverWithDistance driver1 = new DriverWithDistance();
        driver1.setId(1L);
        driver1.setName("张三");
        driver1.setLng(116.405); // 经度（贴近乘客默认经纬度116.404, 39.915）
        driver1.setLat(39.916); // 纬度
        driver1.setRating(4.8); // 高评分
        driver1.setMatchingScore(0.0); // 初始匹配得分置0

        // 司机2：在线、可接单、距离最近、评分中等
        DriverWithDistance driver2 = new DriverWithDistance();
        driver2.setId(2L);
        driver2.setName("李四");
        driver2.setLng(116.404);
        driver2.setLat(39.915);
        driver2.setRating(4.2);
        driver2.setMatchingScore(0.0);

        // 司机3：在线、可接单、距离稍远、满分评分
        DriverWithDistance driver3 = new DriverWithDistance();
        driver3.setId(3L);
        driver3.setName("王五");
        driver3.setLng(116.408);
        driver3.setLat(39.919);
        driver3.setRating(5.0); // 满分
        driver3.setMatchingScore(0.0);

        // 司机4：在线、可接单、距离较近、评分偏低
        DriverWithDistance driver4 = new DriverWithDistance();
        driver4.setId(4L);
        driver4.setName("赵六");
        driver4.setLng(116.406);
        driver4.setLat(39.917);
        driver4.setRating(3.5);
        driver4.setMatchingScore(0.0);

        // 司机5：在线、可接单、距离临界、评分良好
        DriverWithDistance driver5 = new DriverWithDistance();
        driver5.setId(5L);
        driver5.setName("孙八");
        driver5.setLng(116.409);
        driver5.setLat(39.920);
        driver5.setRating(4.7);
        driver5.setMatchingScore(0.0);

        // 司机6：在线、可接单、距离超出后续过滤范围（3公里外）
        DriverWithDistance driver6 = new DriverWithDistance();
        driver6.setId(6L);
        driver6.setName("钱九");
        driver6.setLng(116.450); // 远大于3公里
        driver6.setLat(39.950); // 远大于3公里
        driver6.setRating(4.0);
        driver6.setMatchingScore(0.0);

        // 将模拟司机加入列表
        onlineDrivers.add(driver1);
        onlineDrivers.add(driver2);
        onlineDrivers.add(driver3);
        onlineDrivers.add(driver4);
        onlineDrivers.add(driver5);
        onlineDrivers.add(driver6);

        // 返回模拟的在线可接单司机列表
        return onlineDrivers;
    }
}
