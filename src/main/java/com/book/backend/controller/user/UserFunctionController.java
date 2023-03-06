package com.book.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.common.exception.CommonError;
import com.book.backend.common.exception.VueBookException;
import com.book.backend.pojo.*;
import com.book.backend.pojo.dto.CommentDTO;
import com.book.backend.pojo.dto.ViolationDTO;
import com.book.backend.service.*;
import com.book.backend.utils.NumberUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 赵天宇
 */
@RestController
@RequestMapping("/user")
public class UserFunctionController {
    @Autowired
    private BooksService booksService;
    @Autowired
    private BookRuleService bookRuleService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private BooksBorrowService booksBorrowService;
    @Autowired
    private ViolationService violationService;
    @Autowired
    private BookAdminsService bookAdminsService;
    @Autowired
    private CommentService commentService;

    /**
     * 图书查询 分页和条件查询 (模糊查询)
     *
     * @param basePage 用于接受分页传参
     * @return R<Page < Books>>
     */
    @PostMapping("/search_book_page")
    public R<Page<Books>> searchBookPage(@RequestBody BasePage basePage) {
        /**
         * 1.先判断BasePage中传入的condition和query是否有空值
         * 2.如果有空值，查询所有书本,放入分页构造器,设置响应状态码和请求信息，返回给前端
         * 3.如果没有空值，创建条件构造器，并根据条件和内容查询
         * 4.获取书本数据，判断是否为空，如果为空，设置响应状态码404,并提示前端查询不到数据
         * 5.如果不为空,放入分页构造器,设置响应状态码和请求信息，返回给前端
         */
        String condition = basePage.getCondition();
        String query = basePage.getQuery();
        // 页数
        int pageSize = basePage.getPageSize();
        // 当前页码
        int pageNum = basePage.getPageNum();
        // 创建分页构造器
        Page<Books> pageInfo = new Page<>(pageNum, pageSize);
        R<Page<Books>> result = new R<>();
        // 检验空值情况
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(query)) {
            LambdaQueryWrapper<Books> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(Books::getBookId);
            // 分页操作(按主键升序)
            booksService.page(pageInfo, queryWrapper);
            result.setData(pageInfo);
            result.setStatus(200);
            result.setMsg("获取图书信息成功");
            return result;
        }
        // 创建条件构造器
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(condition, query);
        // 根据条件模糊查询后分页
        Page<Books> page = booksService.page(pageInfo, queryWrapper);
        // 如果查询不到数据
        if (page.getTotal() == 0) {
            result.setMsg("查询不到该图书");
            result.setStatus(404);
            return result;
        }
        result.setData(page);
        result.setStatus(200);
        result.setMsg("获取图书信息成功");
        return result;
    }

    /**
     * 读者规则查询
     *
     * @return  R<List<BookRule>>
     */
    @GetMapping("get_rulelist")
    public R<List<BookRule>> getRuleList() {
        /**
         * 1.获取所有读者规则
         * 2.判断是否为空，如果为空，设置响应状态码和请求信息返回前端
         * 3.如果不为空，则设置200响应状态码和请求信息，封装到通用类,返回前端
         */
        List<BookRule> list = bookRuleService.list();
        R<List<BookRule>> result = new R<>();
        if (list.isEmpty()) {
            result.setStatus(404);
            result.setMsg("获取读者规则失败");
            return result;
        }
        result.setData(list);
        result.setStatus(200);
        result.setMsg("获取读者规则成功");
        return result;
    }

    /**
     * 查询公告信息
     *
     * @return R<List < Notice>>
     */
    @GetMapping("get_noticelist")
    public R<List<Notice>> getNoticeList() {
        /**
         * 1.创建条件构造器，根据日期升序，查询
         * 2.判断是否为空,如果为空,设置响应状态码和请求信息返回前端
         * 3.如果不为空,则设置200响应状态码和请求信息，封装到通用类，返回前端
         */
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Notice::getCreateTime);
        List<Notice> list = noticeService.list(queryWrapper);
        R<List<Notice>> result = new R<>();
        if (list.isEmpty()) {
            result.setStatus(404);
            result.setMsg("获取公告信息失败");
            return result;
        }
        result.setData(list);
        result.setStatus(200);
        result.setMsg("获取公告信息成功");
        return result;
    }

    /**
     * Rest接受参数 查询个人用户userId
     *
     * @param userId 用户id
     * @return R<Users>
     */
    @GetMapping("get_information/{userId}")
    public R<Users> getUserByUserId(@PathVariable("userId") Integer userId) {
        /**
         * 1.获取userId,创建条件构造器 判断userId是否为null
         * 2.调用userService的getOne查询是否等于该用户id的用户
         * 3.如果没有，设置响应状态码404和请求信息，封装后，返回前端
         * 4.如果有，将用户信息脱敏后，设置响应状态码200和请求信息，封装后，返回前端
         */
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        R<Users> result = new R<>();
        // 判断userId是否为null
        if (userId == null) {
            VueBookException.cast(CommonError.USER_NULL);
        }
        queryWrapper.eq(Users::getUserId, userId);
        Users users = usersService.getOne(queryWrapper);
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
     * 修改密码
     *
     * @return R
     */
    @PostMapping("update_password")
    public R updatePassword(@RequestBody Users users) {
        /**
         * 1.获取用户传输的密码和用户id
         * 2.根据用户id查询数据库是否有该用户
         * 3.将密码进行md5加密
         * 4.更新该用户的密码
         * 5.设置响应状态码和请求信息，封装后，返回前端
         */
        // 条件构造器
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        R result = new R<>();
        Integer userId = users.getUserId();
        // 当userId!=null时,调用条件构造器
        queryWrapper.eq(userId != null, Users::getUserId, userId);
        String password = users.getPassword();
        Users userOne = usersService.getOne(queryWrapper);
        // 当用户不存在时，返回错误信息
        if (userOne == null) {
            return R.error("更改密码失败");
        }
        // 密码加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        userOne.setPassword(md5Password);
        boolean update = usersService.update(userOne, queryWrapper);
        if (!update) {
            return R.error("更改密码失败");
        }
        result.setStatus(200);
        result.setMsg("更改密码成功");
        return result;
    }

    /**
     * 借阅信息查询 根据用户id，条件及其内容
     *
     * @param basePage 用于接受分页传参和用户id
     * @return R<Page < BooksBorrow>>
     */
    @PostMapping("get_bookborrow")
    public R<Page<BooksBorrow>> getBookBorrowPage(@RequestBody BasePage basePage) {
        /**
         * 1.先根据借阅证查询是否该用户在借阅表中，如果没有直接返回
         * 2.先判断BasePage中传入的condition和query是否有空值
         * 3.如果有空值，根据借阅证查询所有的借阅信息,放入分页构造器,设置响应状态码和请求信息，返回给前端
         * 5.如果没有空值，创建条件构造器，并根据用户id、条件、内容查询
         * 6.获取借阅数据，判断是否为空，如果为空，设置响应状态码404,并提示前端查询不到数据
         * 7.如果不为空,放入分页构造器,设置响应状态码和请求信息，返回给前端
         */
        String cardNumberString = basePage.getCardNumber();
        long cardNumber = Long.parseLong(cardNumberString);
        R<Page<BooksBorrow>> result = new R<>();
        QueryWrapper<BooksBorrow> queryWrapper = new QueryWrapper<>();
        // 页码
        int pageNum = basePage.getPageNum();
        // 页数
        int pageSize = basePage.getPageSize();
        // 创建分页构造器
        Page<BooksBorrow> pageInfo = new Page<>(pageNum, pageSize);
        queryWrapper.eq("card_number", cardNumber);
        List<BooksBorrow> list = booksBorrowService.list(queryWrapper);
        // 判断用户id 是否有借阅记录
        if (list.isEmpty()) {
            return R.error("获取不到该用户借阅信息");
        }
        // 有借阅记录
        String condition = basePage.getCondition();
        String query = basePage.getQuery();
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(query)) {
            LambdaQueryWrapper<BooksBorrow> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(BooksBorrow::getCardNumber, cardNumber).orderByAsc(BooksBorrow::getCreateTime);
            booksBorrowService.page(pageInfo, queryWrapper1);
            result.setData(pageInfo);
            result.setStatus(200);
            result.setMsg("获取借阅信息成功");
            return result;
        }
        queryWrapper.like(condition, query);
        Page<BooksBorrow> page = booksBorrowService.page(pageInfo, queryWrapper);
        if (page.getTotal() == 0) {
            return R.error("查询不到该借阅信息");
        }
        result.setData(pageInfo);
        result.setStatus(200);
        result.setMsg("获取借阅信息成功");
        return result;
    }

    /**
     * 查询违章信息(借阅证)
     *
     * @param basePage 获取前端的分页参数，条件和内容，借阅证
     * @return R<Page < ViolationDTO>>
     */
    @PostMapping("get_violation")
    public R<Page<ViolationDTO>> getViolationListByPage(@RequestBody BasePage basePage) {
        /**
         * 1.先根据借阅证查询是否该用户在违章表中，如果没有直接返回
         * 2.先判断BasePage中传入的condition和query是否有空值
         * 3.如果有空值，根据借阅证查询所有的违章信息,进行分页查询
         *      3.1利用对象拷贝BeanUtils将分页构造器拷贝到DTO的分页构造器(忽略records)
         *      3.2根据图书管理员的id去获取用户名，然后封装到每一个DTO,返回DTOList
         *      3.3利用DTO分页构造器的setRecords重新赋值DTOList,将DTO分页构造器放入通用类的数据中,设置响应状态码和请求信息，返回前端
         * 4.如果没有空值,创建条件构造器，并根据借阅证编号、条件、内容查询
         * 5.获取违章数据，判断是否为空，如果为空，设置响应状态码404,并提示前端查询不到数据
         * 6.如果不为空,先放入原来的分页构造器
         *      6.1利用对象拷贝BeanUtils将分页构造器拷贝到DTO的分页构造器(忽略records)
         *      6.2根据图书管理员的id去获取用户名，然后封装到每一个DTO,返回DTOList
         *      6.3利用DTO分页构造器的setRecords重新赋值DTOList,将DTO分页构造器放入通用类的数据中,设置响应状态码和请求信息，返回前端
         */
        String cardNumber = basePage.getCardNumber();
        QueryWrapper<Violation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("card_number", cardNumber);
        List<Violation> list = violationService.list(queryWrapper);
        if (list.isEmpty()) {
            return R.error("没有该用户的违章信息");
        }
        // 页码
        int pageNum = basePage.getPageNum();
        // 页数
        int pageSize = basePage.getPageSize();
        // 两个分页构造器
        Page<Violation> pageInfo = new Page<>(pageNum,pageSize);
        Page<ViolationDTO> dtoPage = new Page<>(pageNum,pageSize);
        // 创建返回结果类
        R<Page<ViolationDTO>> result = new R<>();
        // 有违章记录
        String condition = basePage.getCondition();
        String query = basePage.getQuery();
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(query)) {
            LambdaQueryWrapper<Violation> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Violation::getCardNumber,cardNumber).orderByAsc(Violation::getCreateTime);
            violationService.page(pageInfo,queryWrapper1);
            BeanUtils.copyProperties(pageInfo,dtoPage,"records");
            List<Violation> records = pageInfo.getRecords();
            List<ViolationDTO> listDTO = records.stream().map((item)->{
                ViolationDTO violationDTO = new ViolationDTO();
                // 对象拷贝
                BeanUtils.copyProperties(item,violationDTO);
                // 获取图书管理员的id
                Integer violationAdminId = item.getViolationAdminId();
                // 根据id去查询用户名
                LambdaQueryWrapper<BookAdmins> queryWrapper2 = new LambdaQueryWrapper<>();
                queryWrapper2.eq(BookAdmins::getBookAdminId,violationAdminId);
                BookAdmins bookAdmins = bookAdminsService.getOne(queryWrapper2);
                if(bookAdmins!=null){
                    // 获取用户名
                    String username = bookAdmins.getUsername();
                    violationDTO.setViolationAdmin(username);
                }
                return violationDTO;
            }).collect(Collectors.toList());
            dtoPage.setRecords(listDTO);
            result.setData(dtoPage);
            result.setMsg("获取违章记录成功");
            result.setStatus(200);
            return result;
        }
        // 模糊查询
        queryWrapper.like(condition,query);
        Page<Violation> page = violationService.page(pageInfo, queryWrapper);
        if(page.getTotal()==0){
            return R.error("查询不到该用户的违章记录");
        }
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Violation> records2 = pageInfo.getRecords();
        List<ViolationDTO> violationDTOList =  records2.stream().map((item)-> {
            ViolationDTO violationDTO2 = new ViolationDTO();
            BeanUtils.copyProperties(item,violationDTO2);
            // 获取图书管理员的id
            Integer violationAdminId = item.getViolationAdminId();
            // 根据id获取用户名
            LambdaQueryWrapper<BookAdmins> queryWrapper3 = new LambdaQueryWrapper<>();
            queryWrapper3.eq(BookAdmins::getBookAdminId,violationAdminId);
            BookAdmins bookAdmins2 = bookAdminsService.getOne(queryWrapper3);
            if(bookAdmins2!=null){
                // 获取用户名
                String username = bookAdmins2.getUsername();
                violationDTO2.setViolationAdmin(username);
            }
            return violationDTO2;
        }).collect(Collectors.toList());
        dtoPage.setRecords(violationDTOList);
        result.setData(dtoPage);
        result.setMsg("获取违章信息成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 获取弹幕列表
     * @return R<Comment>
     */
    @GetMapping("get_commentlist")
    public R<List<CommentDTO>> getCommentList(){
        /**
         * 1.查询当前的弹幕列表
         * 2.如果弹幕列表为空，设置响应状态码404,和请求信息，返回前端
         * 3.不为空，封装为DTO,设置响应状态码200和请求信息，返回前端
         */
        R<List<CommentDTO>> result = new R<>();
        List<Comment> list = commentService.list();
        if(list.isEmpty()){
            result.setStatus(404);
            result.setMsg("弹幕列表为空");
            return result;
        }
        List<CommentDTO> commentDTOList = list.stream().map((item)->{
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setAvatar(item.getCommentAvatar());
            commentDTO.setId(item.getCommentId());
            commentDTO.setMsg(item.getCommentMessage());
            commentDTO.setTime(item.getCommentTime());
            commentDTO.setBarrageStyle(item.getCommentBarrageStyle());
            return commentDTO;
        }).collect(Collectors.toList());
        result.setData(commentDTOList);
        result.setMsg("获取弹幕列表成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 添加弹幕
     * @return R
     */
    @PostMapping("add_comment")
    public R addComment(@RequestBody CommentDTO commentDTO){
        /**
         * 1.先获取请求中的参数(msg即可)
         * 2.id为null,因为数据库设置了自增
         * 3.avatar可以设置为数组中随机获取，这里暂定同一个头像
         * 4.时间随机生成(5-9秒)调用工具类
         * 5.barrageStyle从数组中随机获取
         * 6.将生成参数，传给Comment,调用service进行插入
         * 7.如果插入失败，返回404,和错误信息
         * 8.插入成功，返回200和成功信息
         */
        R result = new R();
        String [] barrageStyleArrays = {"yibai","erbai","sanbai","sibai","wubai","liubai","qibai","babai","jiubai","yiqian"};
        Comment comment = new Comment();
        comment.setCommentId(null);
        comment.setCommentAvatar("https://img0.baidu.com/it/u=825023390,3429989944&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500");
        comment.setCommentTime(NumberUtil.getBarrageTime());
        comment.setCommentMessage(commentDTO.getMsg());
        long index = Math.round(Math.random()*10);
        //如果四舍五入后，下标越界，直接-1
        if(index == 10) {
            index = 9L;
        }
        String s = Long.toString(index);
        int newIndex = Integer.parseInt(s);
        comment.setCommentBarrageStyle(barrageStyleArrays[newIndex]);
        boolean flag = commentService.save(comment);
        if(!flag){
            return R.error("添加弹幕失败");
        }
        result.setStatus(200);
        result.setMsg("添加弹幕成功");
        return result;
    }
}
