package com.book.backend.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.constant.Constant;
import com.book.backend.common.R;
import com.book.backend.mapper.AdminsMapper;
import com.book.backend.pojo.Admins;
import com.book.backend.pojo.Books;
import com.book.backend.pojo.Users;
import com.book.backend.pojo.dto.BookData;
import com.book.backend.pojo.dto.UsersDTO;
import com.book.backend.service.AdminsService;
import com.book.backend.service.BooksService;
import com.book.backend.service.UsersService;
import com.book.backend.utils.JwtKit;
import com.book.backend.utils.NumberUtil;
import com.book.backend.utils.RandomNameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author 赵天宇
 * @description 针对表【t_admins】的数据库操作Service实现
 * @createDate 2023-02-03 20:01:01
 */
@Service
public class AdminsServiceImpl extends ServiceImpl<AdminsMapper, Admins>
        implements AdminsService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "xiaobaitiao";
    @Resource
    private UsersService usersService;
    @Resource
    private BooksService booksService;
    @Resource
    private JwtKit jwtKit;
    /**
     * 1.接受请求发送的用户名，密码，规则编号，用户状态
     * 2.根据用户状态可用/禁用 去设置1和0
     * 3.用户id自增设为null,密码需要md5加密,随机生成card_name姓名
     * 4.工具类随机生成11位借阅证编号
     * 5.调用服务插入用户，判断是否成功
     */
    @Override
    public R<String> addRule(UsersDTO usersDTO) {

        // 密码
        String password = SALT + usersDTO.getPassword();
        // 用户状态
        String userStatus = usersDTO.getUserStatus();
        int status = 0;
        if (Constant.USERAVAILABLE.equals(userStatus)) {
            status = 1;
        }
        Users users = new Users();
        BeanUtils.copyProperties(usersDTO, users, "userStatus");
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        users.setStatus(status);
        users.setCardName(RandomNameUtils.fullName());
        // 密码加密
        users.setPassword(md5Password);
        long cardNumber = Long.parseLong(new String(NumberUtil.getNumber(11)));
        users.setCardNumber(cardNumber);
        boolean save = usersService.save(users);
        if (!save) {
            return R.error("添加借阅证失败");
        }
        return R.success(null, "添加借阅证成功");
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
    public R login(Admins users) {

        R result = new R<>();
        // 检查用户名是否为空或null等情况
        if (StringUtils.isBlank(users.getUsername())) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 判断系统管理员是否存在
        LambdaUpdateWrapper<Admins> adminWrapper = new LambdaUpdateWrapper<>();
        adminWrapper.eq(Admins::getUsername, users.getUsername());
        Admins adminOne = this.getOne(adminWrapper);
        if (adminOne == null) {
            result.setStatus(404);
            return R.error("用户名不存在");
        }
        // 系统管理员存在 判断禁用情况
        if (Constant.DISABLE.equals(adminOne.getStatus())) {
            return R.error("该系统管理员已被禁用");
        }
        String password = users.getPassword();
        if (!password.equals(adminOne.getPassword())) {
            result.setStatus(404);
            return R.error("用户名或密码错误");
        }
        // 密码校验成功 生成Token
        String token = jwtKit.generateToken(users);
        // 返回成功信息，并将token加入到动态数据map中
        result.setStatus(200);
        result.add("token", token);
        result.setMsg("登录成功");
        result.add("id", adminOne.getAdminId());
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
    public R<Admins> getUserData(Admins admin) {
        R<Admins> r = new R<>();
        // 条件构造器
        LambdaQueryWrapper<Admins> adminsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        adminsLambdaQueryWrapper.eq(Admins::getAdminId,admin.getAdminId());
        Admins adminOne = this.getOne(adminsLambdaQueryWrapper);
        if (adminOne == null) {
            return R.error("系统管理员不存在");
        }
        adminOne.setPassword("");
        r.setData(adminOne);
        r.setStatus(200);
        r.setMsg("获取系统管理员数据成功");
        return r;
    }

    @Override
    public R<String> upload(MultipartFile file) throws IOException {

        // 读取excel
        EasyExcel.read(file.getInputStream(), BookData.class, new AnalysisEventListener<BookData>() {
            ArrayList<Books> booksList = new ArrayList<>();
            // 每解析一行数据,该方法会被调用一次
            @Override
            public void invoke(BookData bookData, AnalysisContext analysisContext) {
                Books books = new Books();
                // 生成11位数字的图书编号
                StringBuilder stringBuilder = NumberUtil.getNumber(11);
                long bookNumber = Long.parseLong(new String(stringBuilder));
                BeanUtils.copyProperties(bookData,books);
                books.setBookNumber(bookNumber);
                booksList.add(books);
//                System.out.println("解析数据为:" + bookData.toString());
            }
            // 全部解析完成被调用
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                // 全部加入到容器list中后，一次性批量导入,先判断容器是否为空
                if(!booksList.isEmpty()){
                    // 可以将解析的数据保存到数据库
                    boolean flag = booksService.saveBatch(booksList);
                    // 如果数据添加成功
                    if(flag){
                        System.out.println("Excel批量添加图书成功");
                    }else{
                        System.out.println("Excel批量添加图书失败");
                    }
                }else{
                    System.out.println("空表无法进行数据导入");
                }
                System.out.println("解析完成...");

            }
        }).sheet().doRead();
        return R.success(null,"Excel批量添加图书成功");
    }
}




