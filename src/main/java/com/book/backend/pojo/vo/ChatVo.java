package com.book.backend.pojo.vo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName t_chat
 */
@Data
public class ChatVo implements Serializable {

    private static final long serialVersionUID = 1324635911327892059L;
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


}
