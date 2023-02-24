package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 * @TableName t_book_rule
 */
@TableName(value ="t_book_rule")
@Data
public class BookRule implements Serializable {
    /**
     * 借阅规则记录的唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer ruleId;

    /**
     * 借阅规则编号
     */
    private Integer bookRuleId;

    /**
     * 借阅天数
     */
    private Integer bookDays;

    /**
     * 限制借阅的本数
     */
    private Integer bookLimitNumber;

    /**
     * 限制的图书馆
     */
    private String bookLimitLibrary;

    /**
     * 图书借阅后每天逾期费用
     */
    private Double bookOverdueFee;

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
