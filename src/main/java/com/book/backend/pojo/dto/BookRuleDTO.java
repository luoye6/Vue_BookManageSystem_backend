package com.book.backend.pojo.dto;

import com.book.backend.pojo.BookRule;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 赵天宇
 */
@Data
public class BookRuleDTO extends BookRule implements Serializable {
    public String[] checkList;
}
