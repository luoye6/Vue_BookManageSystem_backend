package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.BooksBorrow;
import com.book.backend.service.BooksBorrowService;
import com.book.backend.mapper.BooksBorrowMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_books_borrow】的数据库操作Service实现
* @createDate 2023-02-05 18:53:07
*/
@Service
public class BooksBorrowServiceImpl extends ServiceImpl<BooksBorrowMapper, BooksBorrow>
    implements BooksBorrowService{

}




