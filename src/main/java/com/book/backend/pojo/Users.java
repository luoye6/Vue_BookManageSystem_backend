package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author 程序员小白条
 * @TableName t_users
 */
@TableName(value ="t_users")
@Data
public class Users implements Serializable {
    /**
     * 用户表的唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码 MD5加密
     */
    private String password;

    /**
     * 真实姓名
     */
    private String cardName;

    /**
     * 借阅证编号 固定11位随机生成 非空
     */
    private Long cardNumber;

    /**
     * 规则编号 可以自定义也就是权限功能
     */
    private Integer ruleNumber;

    /**
     * 1表示可用 0表示禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
