package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName t_comment
 */
@TableName(value ="t_comment")
@Data
public class Comment implements Serializable {
    /**
     * 留言表唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer commentId;

    /**
     * 留言的头像 链接
     */
    private String commentAvatar;

    /**
     * 弹幕的高度(样式)
     */
    private String commentBarrageStyle;

    /**
     * 弹幕的内容
     */
    private String commentMessage;

    /**
     * 留言的时间(控制速度)
     */
    private Integer commentTime;

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
