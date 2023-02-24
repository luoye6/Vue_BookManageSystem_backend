package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.Admins;
import com.book.backend.service.AdminsService;
import com.book.backend.mapper.AdminsMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_admins】的数据库操作Service实现
* @createDate 2023-02-03 20:01:01
*/
@Service
public class AdminsServiceImpl extends ServiceImpl<AdminsMapper, Admins>
    implements AdminsService{

}




