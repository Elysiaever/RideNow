package com.fth.user.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id; // 主键 ID

    @TableField("username")
    private String username; // 用户名（非空、唯一）

    @TableField("phone")
    private String phone; // 手机号（唯一）

    @TableField("password")
    private String password;

    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间

    @TableField("updated_at")
    private LocalDateTime updatedAt; // 更新时间
}