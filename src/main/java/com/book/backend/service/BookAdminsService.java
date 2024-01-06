package com.book.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.BookAdmins;
import com.baomidou.mybatisplus.extension.service.IService;
import com.book.backend.pojo.dto.ViolationDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
* @author 程序员小白条
* @description 针对表【t_book_admins】的数据库操作Service
* @createDate 2023-02-04 16:55:39
*/
public interface BookAdminsService extends IService<BookAdmins> {

    /**
     * 添加图书管理员
     * @param bookAdmins 图书管理员
     * @return R<String>
     */
    R<String> addBookAdmin(BookAdmins bookAdmins);
    /**
     * 图书管理员登录
     *
     * @param users 图书管理员
     * @return 返回R通用数据
     */
    R login( BookAdmins users);
    /**
     * 返回给前端图书管理员的数据
     *
     * @param bookAdmins 图书管理员
     * @return R<BookAdmins>
     */
    R<BookAdmins> getUserData( BookAdmins bookAdmins);
    /**
     * 获取借书报表
     *
     * @param basePage 接受分页构造器和模糊查询的传参
     * @return R<Page < ViolationDTO>>
     */
    R<Page<ViolationDTO>> getBorrowStatement( BasePage basePage);
    /**
     * 获取图书管理员的列表
     *
     * @param basePage 分页构造器传参
     * @return R<Page < BookAdmins>>
     */
    R<Page<BookAdmins>> getBookAdminListByPage( BasePage basePage);
    /**
     * 获取图书管理员信息 通过图书管理员id
     *
     * @param bookAdminId 图书管理员id
     * @return R<BookAdmins>
     */
    R<BookAdmins> getBookAdminById( Integer bookAdminId);
    /**
     * 删除图书管理员 根据图书管理员id
     *
     * @param bookAdminId 图书管理员id
     * @return R<String>
     */
    R<String> deleteBookAdminById( Integer bookAdminId);
    /**
     * 修改图书管理员
     *
     * @param bookAdmins 图书管理员
     * @return R<String>
     */
    R<String> updateBookAdmin( BookAdmins bookAdmins);
}
