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
 * @TableName t_violation
 */
@TableName(value ="t_violation")
@Data
public class Violation implements Serializable {
    /**
     * 违章表唯一标识
     */
    @TableId(type = IdType.AUTO)
    private Integer violationId;

    /**
     * 借阅证编号 11位 随机生成
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
     * 违章信息
     */
    private String violationMessage;

    /**
     * 违章信息管理员的id
     */
    private Integer violationAdminId;

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
