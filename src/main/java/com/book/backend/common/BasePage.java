package com.book.backend.common;

import lombok.Data;

/**
 * @author 程序员小白条
 */
@Data
public class BasePage {
    /**
     * 分页参数 当前页
     */
    private int pageNum = 1;
    /**
     * 分页参数每页条数
     */
    private int pageSize = 3;
    /**
     * 查询的内容
     */
    private String query;
    /**
     * 查询的条件
     */
    private String condition;
    /**
     * 借阅证编号
     */
    private String cardNumber;
}
