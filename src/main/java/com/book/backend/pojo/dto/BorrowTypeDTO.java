package com.book.backend.pojo.dto;

import lombok.Data;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Data
public class BorrowTypeDTO {
    /**
     * 图书分类
     */
    public String bookTypes;
    /**
     * 借阅量
     */
    public Integer  borrowNumbers;
}
