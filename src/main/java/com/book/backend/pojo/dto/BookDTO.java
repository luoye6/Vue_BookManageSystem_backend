package com.book.backend.pojo.dto;

import com.book.backend.pojo.Books;
import lombok.Data;

/**
 * @author 赵天宇
 */
@Data
public class BookDTO extends Books {
    /**
     * 书籍类型
     */
    public Integer bookTypeNumber;

}
