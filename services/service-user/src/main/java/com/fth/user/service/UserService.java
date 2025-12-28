package com.fth.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fth.user.domain.model.User;
import java.util.Optional;

/**
 * 用户服务接口（继承MyBatis-Plus的IService）
 */
public interface UserService extends IService<User> {
    Optional<User> getByUsername(String username);
    boolean isUsernameExists(String username);
    boolean isPhoneExists(String phone);

}