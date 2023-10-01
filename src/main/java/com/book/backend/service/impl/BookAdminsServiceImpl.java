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
import com.book.backend.mapper.BookAdminsMapper;
import com.book.backend.pojo.BookAdmins;
import com.book.backend.pojo.Violation;
import com.book.backend.pojo.dto.ViolationDTO;
import com.book.backend.service.BookAdminsService;
import com.book.backend.service.ViolationService;
import com.book.backend.utils.JwtKit;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 赵天宇
 * @description 针对表【t_book_admins】的数据库操作Service实现
 * @createDate 2023-02-04 16:55:39
 */
@Service
public class BookAdminsServiceImpl extends ServiceImpl<BookAdminsMapper, BookAdmins>
        implements BookAdminsService {
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "xiaobaitiao";
    @Resource
    private JwtKit jwtKit;

    private ViolationService violationService;
    @Autowired
    public BookAdminsServiceImpl(@Lazy ViolationService violationService){
        this.violationService = violationService;
    }
    /**
     * 1.接受图书管理员的参数(用户名，密码，姓名，邮箱)
     * 2.对密码进行md5加密,设置状态为1可用
     * 3.调用服务插入图书管理员，判断是否成功
     * 4.成功->200,失败->返回错误信息
     */
    @Override
    public R<String> addBookAdmin(BookAdmins bookAdmins) {

        // 获取未加密的密码
        String password = SALT + bookAdmins.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        bookAdmins.setPassword(md5Password);
        bookAdmins.setStatus(Constant.AVAILABLE);
        boolean save = this.save(bookAdmins);
        if (!save) {
            return R.error("添加图书管理员失败");
        }
        return R.success(null, "添加图书管理员成功");
    }
    /**
     * 1.将axios请求携带的json字符串反序列成实体类
     * 2.从实体类中获取用户名(判断空的情况)，从数据库中查询,如果不存在，直接返回响应状态码404和错误信息
     * 3.用户存在,判断是否为禁用状态，如果是直接返回
     * 4.直接和数据库比对
     * 5.密码校验成功，使用工具类生成Token(传入User)
     * 6.返回给前端，响应状态码 200(请求成功) 并在map动态数据中放入token，传输给前端
     */
    @Override
    public R login(BookAdmins users) {

        R result = new R<>();
        // 检查用户名是否为空或null等情况
        if (StringUtils.isBlank(users.getUsername())) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 判断图书管理员是否存在
        LambdaUpdateWrapper<BookAdmins> adminWrapper = new LambdaUpdateWrapper<>();
        adminWrapper.eq(BookAdmins::getUsername, users.getUsername());
        BookAdmins bookAdminOne = this.getOne(adminWrapper);
        if (bookAdminOne == null) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 系统管理员存在 判断禁用情况
        if (Constant.DISABLE.equals(bookAdminOne.getStatus())) {
            return R.error("该图书管理员已被禁用");
        }
        String password = users.getPassword();
        if (!password.equals(bookAdminOne.getPassword())) {
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
     *  1.先获取请求中的id
     *  2.根据id到数据库中查询id是否存活
     *  3.如果存在，查询出数据，
     *  4.用户数据需要脱敏 将密码设为空
     *  5.然后封装到R，设置响应状态码和请求信息,返回前端
     */
    @Override
    public R<BookAdmins> getUserData(BookAdmins bookAdmins) {

        R<BookAdmins> r = new R<>();
        // 条件构造器
        LambdaQueryWrapper<BookAdmins> adminsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminsLambdaQueryWrapper.eq(BookAdmins::getBookAdminId, bookAdmins.getBookAdminId());
        BookAdmins bookAdminOne = this.getOne(adminsLambdaQueryWrapper);
        if (bookAdminOne == null) {
            return R.error("图书管理员不存在");
        }
        bookAdminOne.setPassword("");
        r.setData(bookAdminOne);
        r.setStatus(200);
        r.setMsg("获取图书管理员数据成功");
        return r;
    }
    /**
     * 1.获取页码，页数，条件和查询内容
     * 2.判断条件或者查询内容是否有空值情况
     * 3.如果有空值，查询出所有记录(归还日期不为null),封装DTO对象，调用Page方法,返回
     * 4.创建条件构造器，like,调用booksBorrow.page(pageInfo,构造器)
     * 5.如果不为空则返回正确信息，为空返回错误信息
     */
    @Override
    public R<Page<ViolationDTO>> getBorrowStatement(BasePage basePage) {

        // 页数
        int pageSize = basePage.getPageSize();
        // 页码
        int pageNum = basePage.getPageNum();
        // 条件
        String condition = basePage.getCondition();
        // 内容
        String query = basePage.getQuery();
        Page<Violation> pageInfo = new Page<>(pageNum, pageSize);
        Page<ViolationDTO> dtoPage = new Page<>(pageNum, pageSize);
        R<Page<ViolationDTO>> result = new R<>();
        // 有空值的情况
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(query)) {
            LambdaQueryWrapper<Violation> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.isNotNull(Violation::getReturnDate).orderByAsc(Violation::getBorrowDate);
            Page<Violation> page = violationService.page(pageInfo, queryWrapper);
            if (page.getTotal() == 0) {
                return R.error("借书报表为空");
            }
            // 不为空，封装为DTO返回
            BeanUtils.copyProperties(pageInfo, dtoPage, "records");
            List<Violation> records = page.getRecords();
            List<ViolationDTO> dtoList = records.stream().map((item) -> {
                ViolationDTO violationDTO = new ViolationDTO();
                BeanUtils.copyProperties(item, violationDTO);
                // 获取图书管理员id
                Integer violationAdminId = item.getViolationAdminId();
                // 根据id查询用户名
                LambdaQueryWrapper<BookAdmins> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(BookAdmins::getBookAdminId, violationAdminId);
                BookAdmins bookAdmins = this.getOne(queryWrapper1);
                if (bookAdmins != null) {
                    // 获取用户名
                    String username = bookAdmins.getUsername();
                    violationDTO.setViolationAdmin(username);
                }
                return violationDTO;
            }).collect(Collectors.toList());
            dtoPage.setRecords(dtoList);
            result.setData(dtoPage);
            result.setStatus(200);
            result.setMsg("获取借书报表信息成功");
            return result;
        }
        QueryWrapper<Violation> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(condition, query).isNotNull("return_date");
        Page<Violation> page = violationService.page(pageInfo, queryWrapper);
        if (page.getTotal() == 0) {
            return R.error("借书报表为空");
        }
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Violation> records = pageInfo.getRecords();
        List<ViolationDTO> dtoList = records.stream().map((item) -> {
            ViolationDTO violationDTO = new ViolationDTO();
            BeanUtils.copyProperties(item, violationDTO);
            Integer violationAdminId = item.getViolationAdminId();
            LambdaQueryWrapper<BookAdmins> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(BookAdmins::getBookAdminId, violationAdminId);
            BookAdmins bookAdmins = this.getOne(queryWrapper1);
            if (bookAdmins != null) {
                String username = bookAdmins.getUsername();
                violationDTO.setViolationAdmin(username);
            }
            return violationDTO;
        }).collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        result.setData(dtoPage);
        result.setStatus(200);
        result.setMsg("获取借书报表信息成功");
        return result;
    }

    @Override
    public R<Page<BookAdmins>> getBookAdminListByPage(BasePage basePage) {
        // 页码
        int pageNum = basePage.getPageNum();
        // 页数
        int pageSize = basePage.getPageSize();
        Page<BookAdmins> pageInfo = new Page<>(pageNum, pageSize);
        Page<BookAdmins> page = this.page(pageInfo);
        if (page.getTotal() == 0) {
            return R.error("图书管理员列表为空");
        }
        return R.success(pageInfo, "获取图书管理员列表成功");
    }
    /**
     * 1.调用服务查询是否有该id对应的图书管理员
     * 2.如果存在，封装到数据实体类
     * 3.不存在，返回错误信息
     */
    @Override
    public R<BookAdmins> getBookAdminById(Integer bookAdminId) {
        BookAdmins bookAdmins = this.getById(bookAdminId);
        if (bookAdmins == null) {
            return R.error("获取图书管理员信息失败");
        }
        return R.success(bookAdmins, "获取图书管理员信息成功");
    }

    @Override
    public R<String> deleteBookAdminById(Integer bookAdminId) {
        BookAdmins bookAdmins = this.getById(bookAdminId);
        if (bookAdmins == null) {
            return R.error("删除图书管理员失败");
        }
        boolean remove = this.removeById(bookAdminId);
        if (!remove) {
            return R.error("删除图书管理员失败");
        }
        return R.success(null, "删除图书管理员成功");
    }

    @Override
    public R<String> updateBookAdmin(BookAdmins bookAdmins) {
        String password = bookAdmins.getPassword();
        if (password.length() >= Constant.MD5PASSWORD) {
            bookAdmins.setPassword(password);
        } else {
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
            bookAdmins.setPassword(md5Password);
        }
        boolean update = this.updateById(bookAdmins);
        if (!update) {
            return R.error("修改图书管理员失败");
        }
        return R.success(null, "修改图书管理员成功");
    }
}




