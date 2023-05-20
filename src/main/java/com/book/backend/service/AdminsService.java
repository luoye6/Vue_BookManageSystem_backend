package com.book.backend.service;

import com.book.backend.common.R;
import com.book.backend.pojo.Admins;
import com.baomidou.mybatisplus.extension.service.IService;
import com.book.backend.pojo.BookRule;
import com.book.backend.pojo.dto.UsersDTO;
import org.springframework.web.bind.annotation.RequestBody;

/**
* @author 赵天宇
* @description 针对表【t_admins】的数据库操作Service
* @createDate 2023-02-03 20:01:01
*/
public interface AdminsService extends IService<Admins> {

    /**
     * 添加借阅证
     * @param usersDTO
     * @return
     */
    R<String> addRule(UsersDTO usersDTO);
    /**
     * 系统管理员登录
     * @param users 系统管理员
     * @return 返回R通用数据
     */
    R login( Admins users);
    /**
     * 返回给前端系统管理员的数据
     * @param admin 系统管理员
     * @return R<Admins>
     */
    R<Admins> getUserData( Admins admin);
}
