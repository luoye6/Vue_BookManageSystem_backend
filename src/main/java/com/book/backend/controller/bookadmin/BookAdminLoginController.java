package com.book.backend.controller.bookadmin;

import com.book.backend.common.R;
import com.book.backend.pojo.BookAdmins;
import com.book.backend.service.BookAdminsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 赵天宇
 */
@RestController
@RequestMapping("/bookadmin")
public class BookAdminLoginController {
    @Resource
    private BookAdminsService bookAdminsService;


    /**
     * 图书管理员登录
     *
     * @param users 图书管理员
     * @return 返回R通用数据
     */
    @PostMapping("/login")
    public R login(@RequestBody BookAdmins users) {
        return bookAdminsService.login(users);
    }

    /**
     * 返回给前端图书管理员的数据
     *
     * @param bookAdmins 图书管理员
     * @return R<BookAdmins>
     */
    @PostMapping("/getData")
    public R<BookAdmins> getUserData(@RequestBody BookAdmins bookAdmins) {
      return bookAdminsService.getUserData(bookAdmins);
    }

}
