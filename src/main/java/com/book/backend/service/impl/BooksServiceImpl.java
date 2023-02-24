package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.Books;
import com.book.backend.service.BooksService;
import com.book.backend.mapper.BooksMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_books】的数据库操作Service实现
* @createDate 2023-02-04 18:07:43
*/
@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books>
    implements BooksService{

}




