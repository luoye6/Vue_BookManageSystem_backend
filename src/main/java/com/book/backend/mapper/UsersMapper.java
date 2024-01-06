package com.book.backend.mapper;

import com.book.backend.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 程序员小白条
* @description 针对表【t_users】的数据库操作Mapper
* @createDate 2023-02-02 16:20:02
* @Entity com.book.backend.pojo.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}




