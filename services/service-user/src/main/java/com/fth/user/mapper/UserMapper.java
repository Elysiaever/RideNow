package com.fth.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fth.user.domain.model.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;


public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户（用于登录验证）
     */
    Optional<User> selectByUsername(String username);

    /**
     * 检查用户名是否已存在（用于注册校验）
     */
    Integer countByUsername(String username);

    /**
     * 检查手机号是否已存在（用于注册校验）
     */
    Integer countByPhone(String phone);
}