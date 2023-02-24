package com.book.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.book.backend.common.Constant;
import com.book.backend.common.R;
import com.book.backend.pojo.Users;
import com.book.backend.service.UsersService;
import com.book.backend.utils.JwtKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author 赵天宇
 */
@RestController
@RequestMapping("/user")
public class UserLoginController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private JwtKit jwtKit;

    /**
     * 借阅用户登录
     * @param users 借阅者用户
     * @return 返回R通用数据
     */
    @PostMapping("/login")
    public R login(@RequestBody Users users){
        /**
         * 1.将axios请求携带的json字符串反序列成实体类
         * 2.从实体类中获取用户名(判断空的情况)，从数据库中查询,如果不存在，直接返回响应状态码404和错误信息
         * 3.用户存在,判断状态是否为禁用状态，如果是直接返回
         * 4.将密码进行MD5加密，然后和数据库比对
         * 5.密码校验成功，使用工具类生成Token(传入User)
         * 6.返回给前端，响应状态码 200(请求成功) 并在map动态数据中放入token，传输给前端
         */
        R result = new R<>();
        // 检查用户名是否为空或null等情况
        if(StringUtils.isBlank(users.getUsername())){
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 判断用户是否存在
        LambdaUpdateWrapper<Users> userWrapper = new LambdaUpdateWrapper<>();
        userWrapper.eq(Users::getUsername,users.getUsername());
        Users user = usersService.getOne(userWrapper);
        if (user == null) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 用户存在 判断是否为禁用状态
        if(Constant.DISABLE.equals(user.getStatus())){
            return R.error("账号已被禁止登录");
        }
        String password = users.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if(!md5Password.equals(user.getPassword())){
            result.setStatus(404);
            return R.error("用户名或密码错误");
        }
        // 密码校验成功 生成Token
        String token = jwtKit.generateToken(user);
        // 返回成功信息，并将token加入到动态数据map中
        result.setStatus(200);
        result.add("token",token);
        result.setMsg("登录成功");
        result.add("id",user.getUserId());
        return result;
    }

    /**
     * 根据用户id传给用户所需的信息
     * @param users 用户
     * @return R<Users>
     */
    @PostMapping ("/getData")
    public R<Users> getUserData(@RequestBody Users users){
        /**
         *  1.先获取请求中的id
         *  2.根据id到数据库中查询id是否存活
         *  3.如果存在，查询出数据，
         *  4.用户数据需要脱敏 将密码设为空
         *  5.然后封装到R，设置响应状态码和请求信息,返回前端
         */
        R<Users> r = new R<>();
        Integer userId = users.getUserId();
        // 条件构造器
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUserId,users.getUserId());
        Users userOne = usersService.getOne(queryWrapper);
        if (userOne == null) {
            return R.error("用户不存在");
        }
        userOne.setPassword("");
        r.setData(userOne);
        r.setStatus(200);
        r.setMsg("获取用户数据成功");
        return r;
    }
}
