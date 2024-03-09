package com.book.backend.controller.user;

import com.book.backend.common.R;
import com.book.backend.pojo.Users;
import com.book.backend.service.UsersService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 程序员小白条
 */
@RestController
@RequestMapping("/user")
public class UserLoginController {
   @Resource
    private UsersService usersService;

    /**
     * 借阅用户登录
     *
     * @param users 借阅者用户
     * @return 返回R通用数据
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public R login(@RequestBody Users users) {
        return usersService.login(users);
    }

    /**
     * 根据用户id传给用户所需的信息
     *
     * @param users 用户
     * @return R<Users>
     */
    @PostMapping("/getData")
    @ApiOperation("获取用户数据")
    public R<Users> getUserData(@RequestBody Users users) {
        return usersService.getUserData(users);
    }
}
