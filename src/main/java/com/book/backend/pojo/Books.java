package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName t_books
 */
@TableName(value ="t_books")
@Data
public class Books implements Serializable {
    /**
     * 图书表唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer bookId;

    /**
     * 图书编号 图书的唯一标识
     */
    private Long bookNumber;

    /**
     * 图书名称
     */
    private String bookName;

    /**
     * 图书作者
     */
    private String bookAuthor;

    /**
     * 图书所在图书馆名称
     */
    private String bookLibrary;

    /**
     * 图书类别
     */
    private String bookType;

    /**
     * 图书位置
     */
    private String bookLocation;

    /**
     * 图书状态
     */
    private String bookStatus;

    /**
     * 图书描述
     */
    private String bookDescription;

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
