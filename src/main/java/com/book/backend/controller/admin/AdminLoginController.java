package com.book.backend.controller.admin;

import com.book.backend.common.R;
import com.book.backend.pojo.Admins;
import com.book.backend.service.AdminsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 赵天宇
 */
@RestController
@RequestMapping("/admin")
public class AdminLoginController {
    @Resource
    private AdminsService adminsService;

    /**
     * 系统管理员登录
     * @param users 系统管理员
     * @return 返回R通用数据
     */
    @PostMapping("/login")
    public R login(@RequestBody Admins users){
        return adminsService.login(users);
    }

    /**
     * 返回给前端系统管理员的数据
     * @param admin 系统管理员
     * @return R<Admins>
     */
    @PostMapping ("/getData")
    public R<Admins> getUserData(@RequestBody Admins admin){
        return adminsService.getUserData(admin);
    }
}
