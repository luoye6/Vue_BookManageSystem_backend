package com.book.backend.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @TableName t_books_borrow
 */
@TableName(value ="t_books_borrow")
@Data
public class BooksBorrow implements Serializable {
    /**
     * 借阅表唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer borrowId;

    /**
     * 借阅证编号 固定11位随机生成 用户和图书关联的唯一标识
     */
    private Long cardNumber;

    /**
     * 图书编号 图书唯一标识
     */
    private Long bookNumber;

    /**
     * 借阅日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime borrowDate;

    /**
     * 截止日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime closeDate;

    /**
     * 归还日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime returnDate;

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

    public BooksBorrow(Integer borrowId, Long cardNumber, Long bookNumber, LocalDateTime borrowDate, LocalDateTime closeDate, LocalDateTime returnDate, String createTime, String updateTime) {
        this.borrowId = borrowId;
        this.cardNumber = cardNumber;
        this.bookNumber = bookNumber;
        this.borrowDate = borrowDate;
        this.closeDate = closeDate;
        this.returnDate = returnDate;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public BooksBorrow() {
    }
}
