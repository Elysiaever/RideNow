package com.fth.driver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fth.driver.domain.model.Driver;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

@Mapper
public interface DriverMapper extends BaseMapper<Driver> {

    //更新司机状态
    void updateDriverStatus(Long driverId, String status);

    @Select("SELECT COUNT(1) FROM t_driver WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

}
