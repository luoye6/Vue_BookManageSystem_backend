package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.Users;
import com.book.backend.service.UsersService;
import com.book.backend.mapper.UsersMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_users】的数据库操作Service实现
* @createDate 2023-02-02 16:20:02
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

}




