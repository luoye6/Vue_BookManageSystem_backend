package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName t_user_interface_info
 */
@TableName(value ="t_user_interface_info")
@Data
public class UserInterfaceInfo implements Serializable {
    /**
     *
     */
    @TableId(type=IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 1 表示AI聊天接口 2表示智能分析接口
     */
    private Long interfaceId;

    /**
     * 总共调用接口次数

     */
    private Integer totalNum;

    /**
     * 剩余接口可用次数
     */
    private Integer leftNum;

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
