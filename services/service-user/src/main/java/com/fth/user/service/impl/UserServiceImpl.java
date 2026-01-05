package com.fth.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fth.user.domain.model.User;
import com.fth.user.mapper.UserMapper;
import com.fth.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 用户服务实现类（继承MyBatis-Plus的ServiceImpl）
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    
    @Override
    public void updateUserInfo(Long userId, java.util.Map<String, Object> userInfo) {
        User user = baseMapper.selectById(userId);
        if (user != null) {
            // 更新用户信息字段
            if (userInfo.get("nickname") != null) {
                user.setNickname((String) userInfo.get("nickname"));
            }
            if (userInfo.get("phone") != null) {
                user.setPhone((String) userInfo.get("phone"));
            }
            if (userInfo.get("email") != null) {
                user.setEmail((String) userInfo.get("email"));
            }
            if (userInfo.get("avatar") != null) {
                user.setAvatar((String) userInfo.get("avatar"));
            }
            if (userInfo.get("gender") != null) {
                user.setGender((String) userInfo.get("gender"));
            }
            if (userInfo.get("birthday") != null) {
                // 处理生日字段，可能是字符串格式，需要转换为LocalDate
                Object birthdayObj = userInfo.get("birthday");
                if (birthdayObj instanceof String) {
                    String birthdayStr = (String) birthdayObj;
                    if (birthdayStr != null && !birthdayStr.isEmpty()) {
                        try {
                            user.setBirthday(LocalDate.parse(birthdayStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        } catch (Exception e) {
                            // 如果解析失败，保持原值
                            System.out.println("生日格式错误: " + birthdayStr + ", 错误: " + e.getMessage());
                        }
                    }
                } else if (birthdayObj instanceof LocalDate) {
                    user.setBirthday((LocalDate) birthdayObj);
                }
            }
            
            baseMapper.updateById(user);
        }
    }
    
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = baseMapper.selectById(userId);
        if (user != null) {
            // 验证旧密码是否正确（使用BCryptPasswordEncoder验证）
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new RuntimeException("旧密码不正确");
            }
            
            // 对新密码进行加密
            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);
            baseMapper.updateById(user);
        }
    }
}