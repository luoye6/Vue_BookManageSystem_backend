package com.book.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.backend.mapper.BooksMapper;
import com.book.backend.pojo.Books;
import com.book.backend.pojo.BooksBorrow;
import com.book.backend.pojo.Users;
import com.book.backend.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class VueBookBackendUserTest {
    @Autowired
    private BooksService booksService;
    @Resource
    private BooksMapper booksMapper;
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
    @Test
    public void getAllBooks(){
        booksService.list().forEach(System.out::println);
    }

    /**
     * 查询图书
     * 按照查询条件和查询内容
     */
    @Test
    public void getBooksByCondition(){
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        String search = "bookName";
        String input = "红";
        queryWrapper.like(search,input);
        HashMap<String, Object> map = new HashMap<>();
        map.put(search,input);
        LambdaQueryWrapper<Books> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.orderByAsc(Books::getBookId);
//        List<Books> list = booksService.list(queryWrapper);
//        List<Books> list = booksMapper.selectList(queryWrapper);
//        List<Books> list = booksService.listByMap(map);
        List<Books> list = booksMapper.selectList(queryWrapper1);
        System.out.println(list);
    }

    /**
     * 查询所有读者规则
     */
    @Test
    public void getRuleList(){
        bookRuleService.list().forEach(System.out::println);
    }

    /**
     * 查询所有公告列表
     */
    @Test
    public void getNoticeList(){
        noticeService.list().forEach(System.out::println);
    }

    /**
     * 根据用户id 查询用户信息
     */
    @Test
    public void getUserByUserId(){
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUserId,1923);
        Users usersServiceOne = usersService.getOne(queryWrapper);
        System.out.println(usersServiceOne);
    }

    /**
     * 查询图书借阅信息
     */
    @Test
    public void getBookBorrow(){
        String condition = "return_date";
        String search  = "2023-02-25";
        QueryWrapper<BooksBorrow> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(condition,search);
        List<BooksBorrow> list = booksBorrowService.list(queryWrapper);
        System.out.println(list);
    }
    @Test
    public void getViolationList(){
        violationService.list().forEach(System.out::println);
    }
}
