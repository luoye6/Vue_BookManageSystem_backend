package com.book.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.common.BasePage;
import com.book.backend.constant.Constant;
import com.book.backend.common.R;
import com.book.backend.common.exception.CommonError;
import com.book.backend.common.exception.VueBookException;
import com.book.backend.mapper.UsersMapper;
import com.book.backend.pojo.Users;
import com.book.backend.pojo.dto.UsersDTO;
import com.book.backend.service.UsersService;
import com.book.backend.utils.JwtKit;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * @author 程序员小白条
 * @description 针对表【t_users】的数据库操作Service实现
 * @createDate 2023-02-02 16:20:02
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
        implements UsersService {

    @Resource
    private JwtKit jwtKit;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "xiaobaitiao";
    /**
     * 1.获取userId,创建条件构造器 判断userId是否为null
     * 2.调用userService的getOne查询是否等于该用户id的用户
     * 3.如果没有，设置响应状态码404和请求信息，封装后，返回前端
     * 4.如果有，将用户信息脱敏后，设置响应状态码200和请求信息，封装后，返回前端
     */
    @Override
    public R<Users> getUserByUserId(Integer userId) {

        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        R<Users> result = new R<>();
        // 判断userId是否为null
        if (userId == null) {
            VueBookException.cast(CommonError.USER_NULL);
        }
        queryWrapper.eq(Users::getUserId, userId);
        Users users = this.getOne(queryWrapper);
        // 判断是否有用户id等于userId的用户
        if (users == null) {
            result.setStatus(404);
            result.setMsg("获取用户信息失败");
            return result;
        }
        // 用户信息脱敏
        users.setPassword("");
        result.setData(users);
        result.setStatus(200);
        result.setMsg("获取用户信息成功");
        return result;
    }

    /**
     * 1.获取用户传输的密码和用户id
     * 2.根据用户id查询数据库是否有该用户
     * 3.将密码进行md5加密
     * 4.更新该用户的密码
     * 5.设置响应状态码和请求信息，封装后，返回前端
     */
    @Override
    public R<String> updatePassword(Users users) {

        // 条件构造器
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = users.getUserId();
        // 当userId!=null时,调用条件构造器
        queryWrapper.eq(userId != null, Users::getUserId, userId);
        String password = users.getPassword();
        Users userOne = this.getOne(queryWrapper);
        // 当用户不存在时，返回错误信息
        if (userOne == null) {
            return R.error("更改密码失败");
        }
        // 密码加密加上盐值
        String saltPassword = SALT+password;
        String md5Password = DigestUtils.md5DigestAsHex(saltPassword.getBytes());
        userOne.setPassword(md5Password);
        // todo 取消注释即可成功修改密码 我这边防止有人恶意修改默认用户导致其他游客访问不了，因此取消修改密码的逻辑
//        boolean update = this.update(userOne, queryWrapper);
//        if (!update) {
//            return R.error("更改密码失败");
//        }
        return R.success(null, "更改密码成功");
    }

    /**
     * 1.将axios请求携带的json字符串反序列成实体类
     * 2.从实体类中获取用户名(判断空的情况)，从数据库中查询,如果不存在，直接返回响应状态码404和错误信息
     * 3.用户存在,判断状态是否为禁用状态，如果是直接返回
     * 4.直接和数据库比对
     * 5.密码校验成功，使用工具类生成Token(传入User)
     * 6.返回给前端，响应状态码 200(请求成功) 并在map动态数据中放入token，传输给前端
     */
    @Override
    public R login(Users users) {

        R result = new R<>();
        // 检查用户名是否为空或null等情况
        if (StringUtils.isBlank(users.getUsername())) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 判断用户是否存在
        LambdaUpdateWrapper<Users> userWrapper = new LambdaUpdateWrapper<>();
        userWrapper.eq(Users::getUsername, users.getUsername());
        Users user = this.getOne(userWrapper);
        if (user == null) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 用户存在 判断是否为禁用状态
        if (Constant.DISABLE.equals(user.getStatus())) {
            return R.error("账号已被禁止登录");
        }

        String password = DigestUtils.md5DigestAsHex((SALT+users.getPassword()).getBytes());
        if (!password.equals(user.getPassword())) {
            result.setStatus(404);
            return R.error("用户名或密码错误");
        }
        // 密码校验成功 生成Token
        String token = jwtKit.generateToken(user);
        // 返回成功信息，并将token加入到动态数据map中
        result.setStatus(200);
        result.add("token", token);
        result.setMsg("登录成功");
        result.add("id", user.getUserId());
        return result;
    }

    /**
     * 1.先获取请求中的id
     * 2.根据id到数据库中查询id是否存活
     * 3.如果存在，查询出数据，
     * 4.用户数据需要脱敏 将密码设为空
     * 5.然后封装到R，设置响应状态码和请求信息,返回前端
     */
    @Override
    public R<Users> getUserData(Users users) {

        R<Users> r = new R<>();
        // 条件构造器
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUserId, users.getUserId());
        Users userOne = this.getOne(queryWrapper);
        if (userOne == null) {
            return R.error("用户不存在");
        }
        userOne.setPassword("");
        r.setData(userOne);
        r.setStatus(200);
        r.setMsg("获取用户数据成功");
        return r;
    }

    /**
     * 1.接受页数、页码、模糊查询条件和内容 创建分页构造器
     * 2.判断条件和内容是否有一个为空，如果为空则查询所有记录(判空),放入分页构造器
     * 3.如果二者都不为空，构造条件构造器，通过QueryWrapper的like方法模糊查询
     * 4.放入分页构造器，判断getTotal是否为空
     * 5.不为空->正确信息,为空->错误信息
     */
    @Override
    public R<Page<Users>> getStatementList(BasePage basePage) {
        // 页数
        int pageSize = basePage.getPageSize();
        // 页码
        int pageNum = basePage.getPageNum();
        // 条件
        String condition = basePage.getCondition();
        // 内容
        String query = basePage.getQuery();
        R<Page<Users>> result = new R<>();
        Page<Users> pageInfo = new Page<>(pageNum, pageSize);
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(query)) {
            Page<Users> page = this.page(pageInfo);
            if (page.getTotal() == 0) {
                return R.error("借阅证列表为空");
            }
            result.setStatus(200);
            result.setMsg("获取借阅证列表成功");
            result.setData(pageInfo);
            return result;
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(condition, query);
        Page<Users> page = this.page(pageInfo, queryWrapper);
        if (page.getTotal() == 0) {
            return R.error("借阅证列表为空");
        }
        result.setStatus(200);
        result.setData(pageInfo);
        result.setMsg("获取借阅证列表成功");
        return result;
    }

    /**
     * 1.根据用户id查询是否存在该用户
     * 2.如果存在，封装DTO返回信息
     * 3.不存在,返回错误信息
     */
    @Override
    public R<UsersDTO> getStatementByUserId(Integer userId) {
        Users users = this.getById(userId);
        if (users == null) {
            return R.error("获取用户信息失败");
        }
        R<UsersDTO> result = new R<>();
        Integer status = users.getStatus();
        UsersDTO usersDTO = new UsersDTO();
        BeanUtils.copyProperties(users, usersDTO);
        if (status.equals(Constant.AVAILABLE)) {
            usersDTO.setUserStatus("可用");
        } else {
            usersDTO.setUserStatus("禁用");
        }
        result.setData(usersDTO);
        result.setStatus(200);
        result.setMsg("获取用户信息成功");
        return result;
    }

    /**
     * 1.接受用户名，密码(需要md5加密),规则编号，状态
     * 2.将usersDTO拷贝到users,忽略状态
     * 3.根据可用/禁用，设置用户的状态
     * 4.调用服务更新用户信息
     * 5.判断是否成功，成功->返回前端,错误->错误信息返回
     */
    @Override
    public R<String> updateStatement(UsersDTO usersDTO) {
        Users users = new Users();
        BeanUtils.copyProperties(usersDTO, users, "password", "userStatus");
        String userStatus = usersDTO.getUserStatus();
        if (Constant.USERAVAILABLE.equals(userStatus)) {
            users.setStatus(1);
        } else {
            users.setStatus(0);
        }
        String password = usersDTO.getPassword();
        if (password.length() >= Constant.MD5PASSWORD) {
            users.setPassword(password);
        } else {
            String saltPassword = SALT+password;
            String md5Password = DigestUtils.md5DigestAsHex(saltPassword.getBytes());
            users.setPassword(md5Password);
        }

        boolean update = this.updateById(users);
        if (!update) {
            return R.error("修改借阅证信息失败");
        }
        return R.success(null, "修改借阅证信息成功");
    }

    /**
     * 1.根据userId查询是否有该用户
     * 2.如果有，执行删除操作，判断是否成功
     */
    @Transactional
    @Override
    public R<String> deleteStatementByUserId(Integer userId) {
        Users users = this.getById(userId);
        if (users == null) {
            return R.error("删除借阅证失败");
        }
        boolean remove = this.removeById(userId);
        if (!remove) {
            return R.error("删除借阅证失败");
        }
        return R.success(null, "删除借阅证成功");
    }
}




