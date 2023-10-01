package com.book.backend.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.common.BasePage;
import com.book.backend.constant.Constant;
import com.book.backend.common.R;
import com.book.backend.mapper.BooksBorrowMapper;
import com.book.backend.pojo.Books;
import com.book.backend.pojo.BooksBorrow;
import com.book.backend.pojo.Violation;
import com.book.backend.pojo.dto.ViolationDTO;
import com.book.backend.service.BooksBorrowService;
import com.book.backend.service.BooksService;
import com.book.backend.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author 赵天宇
 * @description 针对表【t_books_borrow】的数据库操作Service实现
 * @createDate 2023-02-05 18:53:07
 */
@Service
public class BooksBorrowServiceImpl extends ServiceImpl<BooksBorrowMapper, BooksBorrow>
        implements BooksBorrowService {
    @Resource
    private ViolationService violationService;

    private BooksService booksService;

    @Autowired
    public BooksBorrowServiceImpl(@Lazy BooksService booksService) {
        this.booksService = booksService;
    }

    /**
     * 1.先根据借阅证查询是否该用户在借阅表中，如果没有直接返回
     * 2.先判断BasePage中传入的condition和query是否有空值
     * 3.如果有空值，根据借阅证查询所有的借阅信息,放入分页构造器,设置响应状态码和请求信息，返回给前端
     * 5.如果没有空值，创建条件构造器，并根据用户id、条件、内容查询
     * 6.获取借阅数据，判断是否为空，如果为空，设置响应状态码404,并提示前端查询不到数据
     * 7.如果不为空,放入分页构造器,设置响应状态码和请求信息，返回给前端
     */
    @Override
    public R<Page<BooksBorrow>> getBookBorrowPage(BasePage basePage) {

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
        List<BooksBorrow> list = this.list(queryWrapper);
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
            this.page(pageInfo, queryWrapper1);
            result.setData(pageInfo);
            result.setStatus(200);
            result.setMsg("获取借阅信息成功");
            return result;
        }
        queryWrapper.like(condition, query);
        Page<BooksBorrow> page = this.page(pageInfo, queryWrapper);
        if (page.getTotal() == 0) {
            return R.error("查询不到该借阅信息");
        }
        result.setData(pageInfo);
        result.setStatus(200);
        result.setMsg("获取借阅信息成功");
        return result;
    }

    /**
     * 1.根据图书编号和归还日期(null)去借阅表中查询唯一的一条记录
     * 2.如果记录不存在,返回错误信息
     * 3.根据获取记录的，现在日期和借阅日期算出 逾期日期 expireDays
     * 4.将截止日期、图书编号、逾期日期、封装到DTO，设置响应状态码和请求信息，返回前端
     */
    @Override
    public R<ViolationDTO> queryExpireInformationByBookNumber(Long bookNumber) {

        LambdaQueryWrapper<BooksBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BooksBorrow::getBookNumber, bookNumber).isNull(BooksBorrow::getReturnDate);
        BooksBorrow bookBorrowRecord = this.getOne(queryWrapper);
        if (bookBorrowRecord == null) {
            return R.error("获取逾期信息失败");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closeDate = bookBorrowRecord.getCloseDate();
        // 格式化
        String nowFormat = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String closeFormat = closeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String[] s = nowFormat.split(" ");
        String[] s1 = closeFormat.split(" ");
        LocalDateTime borrow = LocalDateTimeUtil.parse(s[0] + "T" + s[1]);
        LocalDateTime close = LocalDateTimeUtil.parse(s1[0] + "T" + s1[1]);
        Duration between = LocalDateTimeUtil.between(borrow, close);
        // 获取逾期的天数
        long expireDay = between.toDays();
        ViolationDTO violationDTO = new ViolationDTO();
        violationDTO.setExpireDays(expireDay);
        violationDTO.setBookNumber(bookNumber);
        violationDTO.setCloseDate(closeDate);
        R<ViolationDTO> result = new R<>();
        result.setData(violationDTO);
        result.setStatus(200);
        result.setMsg("获取逾期信息成功");
        return result;
    }

    /**
     * 1.获取归还日期和违章信息和图书编号,判断参数是否有异常
     * 2.根据图书编号，查询归还日期为空的记录,更新图书表
     * 3.更新违章表
     * 4.更新图书表，图书编号的借出状态
     * 5.三个表都更新则返回成功的响应状态码和请求信息，否则返回失败信息
     */
    @Transactional
    @Override
    public R<String> returnBook(Violation violation) {

        Long bookNumber = violation.getBookNumber();
        LocalDateTime returnDate = violation.getReturnDate();
        if (returnDate == null) {
            return R.error("归还日期不能为空");
        }
        String violationMessage = violation.getViolationMessage();
        Integer violationAdminId = violation.getViolationAdminId();
        LambdaQueryWrapper<Violation> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<BooksBorrow> queryWrapper1 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Books> queryWrapper2 = new LambdaQueryWrapper<>();

        queryWrapper.eq(Violation::getBookNumber, bookNumber).isNull(Violation::getReturnDate);
        queryWrapper1.eq(BooksBorrow::getBookNumber, bookNumber).isNull(BooksBorrow::getReturnDate);
        queryWrapper2.eq(Books::getBookNumber, bookNumber);
        Violation violation1 = violationService.getOne(queryWrapper);
        BooksBorrow booksBorrow = this.getOne(queryWrapper1);
        Books book = booksService.getOne(queryWrapper2);
        if (violation1 == null || booksBorrow == null || book == null) {
            return R.error("归还图书失败");
        }

        violation1.setViolationMessage(violationMessage);
        violation1.setReturnDate(returnDate);
        violation1.setViolationAdminId(violationAdminId);
        booksBorrow.setReturnDate(returnDate);
        book.setBookStatus(Constant.BOOKAVAILABLE);
        boolean update1 = violationService.update(violation1, queryWrapper);
        boolean update2 = this.update(booksBorrow, queryWrapper1);
        boolean update3 = booksService.update(book, queryWrapper2);
        if (!update1 || !update2 || !update3) {
            return R.error("归还图书失败");
        }

        return R.success(null, "归还图书成功");
    }

    /**
     * 1.获取页码，页数，条件和查询内容
     * 2.判断条件或者查询内容是否有空值情况
     * 3.如果有空值，查询出所有记录(归还日期为null)
     * 4.创建条件构造器，like,调用booksBorrow.page(pageInfo,构造器)
     * 5.如果不为空则返回正确信息，为空返回错误信息
     */
    @Override
    public R<Page<BooksBorrow>> getReturnStatement(BasePage basePage) {

        // 页码
        int pageNum = basePage.getPageNum();
        // 页数
        int pageSize = basePage.getPageSize();
        // 内容
        String query = basePage.getQuery();
        // 条件
        String condition = basePage.getCondition();
        Page<BooksBorrow> pageInfo = new Page<>(pageNum, pageSize);
        R<Page<BooksBorrow>> result = new R<>();
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(query)) {
            LambdaQueryWrapper<BooksBorrow> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.isNull(BooksBorrow::getReturnDate);
            Page<BooksBorrow> page = this.page(pageInfo, queryWrapper);
            if (page.getTotal() == 0) {
                return R.error("还书报表信息为空");
            }
            result.setData(pageInfo);
            result.setStatus(200);
            result.setMsg("获取还书报表信息成功");
            return result;
        }
        QueryWrapper<BooksBorrow> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.like(condition, query).isNull("return_date").orderByAsc("borrow_date");
        Page<BooksBorrow> page = this.page(pageInfo, queryWrapper1);
        if (page.getTotal() == 0) {
            return R.error("查询不到该还书报表信息");
        }
        result.setData(pageInfo);
        result.setMsg("获取还书报表信息成功");
        result.setStatus(200);
        return result;
    }
}




