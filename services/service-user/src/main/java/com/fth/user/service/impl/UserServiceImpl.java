package com.fth.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fth.user.domain.model.User;
import com.fth.user.mapper.UserMapper;
import com.fth.user.service.UserService;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * 用户服务实现类（继承MyBatis-Plus的ServiceImpl）
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Optional<User> getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return baseMapper.countByUsername(username) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return baseMapper.countByPhone(phone) > 0;
    }
    //新增用户
    @Override
    public void addUser(User user) {
        baseMapper.insert(user);
    }

    @Override
    public void deleteUser(Long userId) {
        baseMapper.deleteById(userId);
    }

    @Override
    public void updateUser(User user) {
        baseMapper.updateById(user);
    }

    @Override
    public User getUserById(Long userId) {
        return baseMapper.selectById(userId);
    }


}