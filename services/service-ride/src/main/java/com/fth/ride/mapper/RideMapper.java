package com.fth.ride.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fth.ride.domain.model.Ride;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RideMapper extends BaseMapper<Ride> {
}
