package com.book.backend.pojo.dto;

import com.book.backend.pojo.Books;
import lombok.Data;

/**
 * @author 程序员小白条
 */
@Data
public class BookDTO extends Books {
    /**
     * 书籍类型
     */
    public Integer bookTypeNumber;

}
