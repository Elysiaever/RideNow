package com.fth.driver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fth.driver.domain.model.Driver;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DriverMapper extends BaseMapper<Driver> {
}
