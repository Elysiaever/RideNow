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
    //新增用户
    void addUser(User user);
    //删除用户
    void deleteUser(Long userId);
    //修改用户信息
    void updateUser(User user);
    //查询用户
    User getUserById(Long userId);
}