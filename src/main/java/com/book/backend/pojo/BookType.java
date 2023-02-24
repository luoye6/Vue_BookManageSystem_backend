package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName t_book_type
 */
@TableName(value ="t_book_type")
@Data
public class BookType implements Serializable {
    /**
     * 图书类别唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer typeId;

    /**
     * 借阅类别的昵称
     */
    private String typeName;

    /**
     * 借阅类别的描述
     */
    private String typeContent;

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
