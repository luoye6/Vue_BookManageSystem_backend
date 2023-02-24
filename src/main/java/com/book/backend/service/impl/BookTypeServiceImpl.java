package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.BookType;
import com.book.backend.service.BookTypeService;
import com.book.backend.mapper.BookTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_book_type】的数据库操作Service实现
* @createDate 2023-02-04 18:51:24
*/
@Service
public class BookTypeServiceImpl extends ServiceImpl<BookTypeMapper, BookType>
    implements BookTypeService{

}




