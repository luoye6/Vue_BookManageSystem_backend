package com.book.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.backend.pojo.Admins;
import com.book.backend.pojo.Users;
import com.book.backend.pojo.Violation;
import com.book.backend.service.AdminsService;
import com.book.backend.service.BookAdminsService;
import com.book.backend.service.UsersService;
import com.book.backend.service.ViolationService;
import com.book.backend.utils.BorrowDateUtil;
import com.book.backend.utils.JwtKit;
import com.book.backend.utils.NumberUtil;
import com.book.backend.utils.RandomNameUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest

class VueBookBackendApplicationTests {
    @Autowired
    private UsersService usersService;
    @Autowired
    private AdminsService adminsService;
    @Autowired
    private BookAdminsService bookAdminsService;
    @Autowired
    private ViolationService violationService;
    @Autowired
    private JwtKit jwtKit;
    @Test
    public void getUser(){
        List<Users> list = usersService.list();
        list.forEach(System.out::println);
    }
    @Test
    public void getAdmins(){
        List<Admins> list = adminsService.list();
        list.forEach(System.out::println);
    }
    @Test
    public void getBookAdmins(){
        bookAdminsService.list().forEach(System.out::println);
    }
    @Test
    public void tokenUse(){
        Admins admins = new Admins();
        admins.setAdminName("张三");
        admins.setPassword("2313");
        String s = jwtKit.generateToken(admins);
        Claims claims = jwtKit.parseJwtToken(s);
       System.out.println(  claims.get("username"));
    }
    @Test
    public void testNumber(){
        StringBuilder number = NumberUtil.getNumber(11);
        System.out.println(number);
    }
    @Test
    public void testName(){
        String s = RandomNameUtils.fullName();
        System.out.println(s);
    }
    @Test
    public void testBorrowData(){
        LocalDateTime now = LocalDateTime.now();
        String[] dateArray = BorrowDateUtil.getDateArray(now);
        for (String s : dateArray) {
            System.out.println(s);
        }
    }

}
