package com.book.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.backend.common.exception.CommonError;
import com.book.backend.common.exception.ErrorCode;
import com.book.backend.common.exception.VueBookException;
import com.book.backend.mapper.BooksMapper;
import com.book.backend.pojo.Books;
import com.book.backend.pojo.BooksBorrow;
import com.book.backend.pojo.Users;
import com.book.backend.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

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

    @Test
    public void testBorrowBooks(){
        BooksBorrow booksBorrow1 = new BooksBorrow();
        booksBorrow1.setBorrowId(null);
        booksBorrow1.setBookNumber(50970375442L);
        booksBorrow1.setCardNumber(18012345678L);
        booksBorrow1.setBorrowDate(LocalDateTime.now());
        booksBorrow1.setCloseDate(LocalDateTime.now().plusDays(30));
        booksBorrow1.setReturnDate(null);
        boolean flag = booksBorrowService.save(booksBorrow1);
        System.out.println(flag);
    }
    @Test
    public void testBooksBorrowTypeStatistic(){
        HashMap<String, Integer> hashMap = new HashMap<>();
        BooksBorrowService borrowService = booksBorrowService;
        List<BooksBorrow> booksBorrowList = borrowService.list();
        for (BooksBorrow booksBorrow : booksBorrowList) {
            Long bookNumber = booksBorrow.getBookNumber();
            LambdaQueryWrapper<Books> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Books::getBookNumber,bookNumber);
            Books book = booksService.getOne(queryWrapper);
            if(book == null){
                VueBookException.cast(CommonError.OBJECT_NULL);
            }
            String bookType = book.getBookType();
            hashMap.put(bookType, hashMap.getOrDefault(bookType,0)+1);
        }
        ArrayList<HashMap<String,Integer>> list = new ArrayList<>();
        Set<Map.Entry<String, Integer>> entries = hashMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put(entry.getKey(),entry.getValue());
            list.add(map);
        }
        System.out.println(list);
    }
}
