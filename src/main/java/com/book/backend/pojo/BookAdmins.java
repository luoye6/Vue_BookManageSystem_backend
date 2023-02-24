package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName t_book_admins
 */
@TableName(value ="t_book_admins")
@Data
public class BookAdmins implements Serializable {
    /**
     * 图书管理员表的唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer bookAdminId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码md5加密
     */
    private String password;

    /**
     * 图书管理员真实姓名
     */
    private String bookAdminName;

    /**
     * 1表示可用 0表示禁用
     */
    private Integer status;

    /**
     * 电子邮箱
     */
    private String email;

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
