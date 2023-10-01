package com.book.backend.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName t_books
 */
@Data
public class BookData implements Serializable {




    /**
     * 图书名称
     */
    @ExcelProperty(value="图书名称",index = 0)
    private String bookName;

    /**
     * 图书作者
     */
    @ExcelProperty(value="图书作者",index = 1)
    private String bookAuthor;

    /**
     * 图书所在图书馆名称
     */
    @ExcelProperty(value="图书馆",index = 2)
    private String bookLibrary;

    /**
     * 图书类别
     */
    @ExcelProperty(value="图书类别",index = 3)
    private String bookType;

    /**
     * 图书位置
     * 例如A12 B06
     */
    @ExcelProperty(value="图书位置",index = 4)
    private String bookLocation;

    /**
     * 图书状态(已借出/未借出)
     */
    @ExcelProperty(value="图书状态",index = 5)
    private String bookStatus;

    /**
     * 图书描述
     */
    @ExcelProperty(value="图书描述",index = 6)
    private String bookDescription;



}
