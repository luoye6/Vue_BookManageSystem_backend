package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.BookRule;
import com.book.backend.service.BookRuleService;
import com.book.backend.mapper.BookRuleMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_book_rule】的数据库操作Service实现
* @createDate 2023-02-05 15:11:20
*/
@Service
public class BookRuleServiceImpl extends ServiceImpl<BookRuleMapper, BookRule>
    implements BookRuleService{

}




