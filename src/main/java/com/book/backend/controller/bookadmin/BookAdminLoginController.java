package com.book.backend.controller.bookadmin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.book.backend.common.Constant;
import com.book.backend.common.R;
import com.book.backend.pojo.BookAdmins;
import com.book.backend.service.BookAdminsService;
import com.book.backend.utils.JwtKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 赵天宇
 */
@RestController
@RequestMapping("/bookadmin")
public class BookAdminLoginController {
    @Autowired
    private BookAdminsService bookAdminsService;
    @Autowired
    private JwtKit jwtKit;

    /**
     * 图书管理员登录
     *
     * @param users 图书管理员
     * @return 返回R通用数据
     */
    @PostMapping("/login")
    public R login(@RequestBody BookAdmins users) {
        /**
         * 1.将axios请求携带的json字符串反序列成实体类
         * 2.从实体类中获取用户名(判断空的情况)，从数据库中查询,如果不存在，直接返回响应状态码404和错误信息
         * 3.用户存在,判断是否为禁用状态，如果是直接返回
         * 4.将密码进行MD5加密，然后和数据库比对
         * 5.密码校验成功，使用工具类生成Token(传入User)
         * 6.返回给前端，响应状态码 200(请求成功) 并在map动态数据中放入token，传输给前端
         */
        R result = new R<>();
        // 检查用户名是否为空或null等情况
        if (StringUtils.isBlank(users.getUsername())) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 判断图书管理员是否存在
        LambdaUpdateWrapper<BookAdmins> adminWrapper = new LambdaUpdateWrapper<>();
        adminWrapper.eq(BookAdmins::getUsername, users.getUsername());
        BookAdmins bookAdminOne = bookAdminsService.getOne(adminWrapper);
        if (bookAdminOne == null) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 系统管理员存在 判断禁用情况
        if (Constant.DISABLE.equals(bookAdminOne.getStatus())) {
            return R.error("该图书管理员已被禁用");
        }
        String password = users.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(bookAdminOne.getPassword())) {
            result.setStatus(404);
            return R.error("用户名或密码错误");
        }
        // 密码校验成功 生成Token
        String token = jwtKit.generateToken(users);
        // 返回成功信息，并将token加入到动态数据map中
        result.setStatus(200);
        result.add("token", token);
        result.setMsg("登录成功");
        result.add("id", bookAdminOne.getBookAdminId());
        return result;
    }

    /**
     * 返回给前端图书管理员的数据
     *
     * @param bookAdmins 图书管理员
     * @return R<BookAdmins>
     */
    @PostMapping("/getData")
    public R<BookAdmins> getUserData(@RequestBody BookAdmins bookAdmins) {
        /**
         *  1.先获取请求中的id
         *  2.根据id到数据库中查询id是否存活
         *  3.如果存在，查询出数据，
         *  4.用户数据需要脱敏 将密码设为空
         *  5.然后封装到R，设置响应状态码和请求信息,返回前端
         */
        R<BookAdmins> r = new R<>();
        Integer userId = bookAdmins.getBookAdminId();
        // 条件构造器
        LambdaQueryWrapper<BookAdmins> adminsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminsLambdaQueryWrapper.eq(BookAdmins::getBookAdminId, bookAdmins.getBookAdminId());
        BookAdmins bookAdminOne = bookAdminsService.getOne(adminsLambdaQueryWrapper);
        if (bookAdminOne == null) {
            return R.error("图书管理员不存在");
        }
        bookAdminOne.setPassword("");
        r.setData(bookAdminOne);
        r.setStatus(200);
        r.setMsg("获取图书管理员数据成功");
        return r;
    }

}
