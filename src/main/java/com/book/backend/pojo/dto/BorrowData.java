package com.book.backend.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 程序员小白条
 */
@Data
public class BorrowData implements Serializable {
    /**
     * 借阅时期 一周为一个间隔
     */
    public String [] borrowDates;
    /**
     * 借阅量 每个数值代表一周的借阅量
     */
    public Integer [] borrowNumber;

    public BorrowData(String[] borrowDates, Integer[] borrowNumber) {
        this.borrowDates = borrowDates;
        this.borrowNumber = borrowNumber;
    }
}
