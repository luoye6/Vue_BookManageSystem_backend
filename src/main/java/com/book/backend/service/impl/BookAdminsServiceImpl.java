package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.BookAdmins;
import com.book.backend.service.BookAdminsService;
import com.book.backend.mapper.BookAdminsMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_book_admins】的数据库操作Service实现
* @createDate 2023-02-04 16:55:39
*/
@Service
public class BookAdminsServiceImpl extends ServiceImpl<BookAdminsMapper, BookAdmins>
    implements BookAdminsService{

}




