package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName t_chat
 */
@TableName(value ="t_chat")
@Data
public class Chat implements Serializable {
    /**
     * 聊天记录id

     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送消息者id

     */
    private Long fromId;

    /**
     * 接受消息者id,可以为空
     */
    private Long toId;

    /**
     * 消息内容
     */
    private String text;

    /**
     * 聊天类型 1-私聊 2-群聊
     */
    private Integer chatType;

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
