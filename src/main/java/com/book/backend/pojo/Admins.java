package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName t_admins
 */
@TableName(value ="t_admins")
@Data
public class Admins implements Serializable {
    /**
     * 管理员表的唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Long adminId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码(MD5加密)
     */
    private String password;

    /**
     * 管理员真实姓名
     */
    private String adminName;

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
