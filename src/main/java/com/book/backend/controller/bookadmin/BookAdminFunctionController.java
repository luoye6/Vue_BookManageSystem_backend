package com.book.backend.controller.bookadmin;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.Constant;
import com.book.backend.common.R;
import com.book.backend.common.exception.CommonError;
import com.book.backend.common.exception.VueBookException;
import com.book.backend.pojo.*;
import com.book.backend.pojo.dto.BooksBorrowDTO;
import com.book.backend.pojo.dto.ViolationDTO;
import com.book.backend.service.*;
import com.book.backend.utils.JwtKit;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 赵天宇
 */
@RestController
@RequestMapping("/bookadmin")
public class BookAdminFunctionController {
    @Autowired
    private BookAdminsService bookAdminsService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private BookRuleService bookRuleService;
    @Autowired
    private BooksService booksService;
    @Autowired
    private BooksBorrowService booksBorrowService;
    @Autowired
    private ViolationService violationService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private JwtKit jwtKit;


    /**
     * 借阅图书根据借阅证号和图书编号
     *
     * @return R
     */
    @PostMapping("borrow_book")
    @Transactional
    public R borrowBookByCardNumberAndBookNumber(@RequestBody BooksBorrowDTO booksBorrowDTO) {
        /**
         *  1.接受前端请求中的参数(借阅证号、图书编号、借阅时间)
         *  2.先根据借阅证号查询是否有此用户存在，不存在直接返回错误信息
         *  3.用户存在，根据图书编号查询图书表，查询是否有图书存在并且该图书的状态是未借出
         *  4.用户存在，图书存在，且未借出 说明可以借出该图书
         *  5.#获取用户的规则编号#，根据编号查询出规则(判断空),获取规则的可借天数
         *  6.设置期限天数为当前时间+规则的可借天数 设置归还日期为空
         *  7.调用bookBorrow，进行插入记录
         *  8.如果插入成功，修改在图书表中对应图书编号的状态为已借出
         *  9.判断是否更新成功
         *  10.插入成功+更新成功，则返回请求状态码200和请求信息
         */
        R result = new R<>();
        // 图书编号
        Long bookNumber = booksBorrowDTO.getBookNumber();
        // 借阅证号
        Long cardNumber = booksBorrowDTO.getCardNumber();
        // 借阅时间
        LocalDateTime borrowDate = booksBorrowDTO.getBorrowDate();
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getCardNumber, cardNumber);
        Users users = usersService.getOne(queryWrapper);
        if (users == null) {
            return R.error("借阅图书失败");
        }
        LambdaQueryWrapper<Books> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Books::getBookNumber, bookNumber);
        Books book = booksService.getOne(queryWrapper1);
        if ((book == null) || (book.getBookStatus().equals(Constant.BOOKDISABLE))) {
            VueBookException.cast(CommonError.QUERY_NULL);
        }

        // 规则编号
        Integer ruleNumber = users.getRuleNumber();
        LambdaQueryWrapper<BookRule> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(BookRule::getBookRuleId, ruleNumber);
        BookRule bookRule = bookRuleService.getOne(queryWrapper2);
        if (bookRule == null) {
            return R.error("借阅图书失败");
        }
        // 可借天数
        Integer bookDays = bookRule.getBookDays();
        LocalDateTime closeDate = borrowDate.plusDays(bookDays);
        BooksBorrow booksBorrow1 = new BooksBorrow();
        booksBorrow1.setBorrowId(null);
        booksBorrow1.setBookNumber(bookNumber);
        booksBorrow1.setCardNumber(cardNumber);
        booksBorrow1.setBorrowDate(borrowDate);
        booksBorrow1.setCloseDate(closeDate);
        booksBorrow1.setReturnDate(null);
        boolean flag = booksBorrowService.save(booksBorrow1);
        if (!flag) {
            return R.error("借阅图书失败");
        }
        book.setBookStatus(Constant.BOOKDISABLE);
        boolean update = booksService.update(book, queryWrapper1);
        if (!update) {
            return R.error("借阅图书失败");
        }
        Violation violation = new Violation();
        BeanUtils.copyProperties(booksBorrow1, violation, "borrowId");
        violation.setViolationId(null);
        violation.setViolationMessage("");
        violation.setViolationAdminId(booksBorrowDTO.getBookAdminId());
        boolean save = violationService.save(violation);
        if (!save) {
            return R.error("借阅图书失败");
        }
        result.setStatus(200);
        result.setMsg("借阅图书成功");
        return result;
    }

    /**
     * 查看图书是否有逾期(查看是否借出)
     *
     * @param bookNumber 图书编号
     * @return R
     */
    @GetMapping("query_book/{bookNumber}")
    public R queryBookExpireByBookNumber(@PathVariable("bookNumber") Integer bookNumber) {
        /**
         * 1.获取图书编号
         * 2.根据图书编号查询图书表中是否存在该书
         * 3.如果存在并且状态为已借出，则返回成功信息
         * 4.状态未借出:返回错误信息
         */
        R result = new R();
        LambdaQueryWrapper<Books> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Books::getBookNumber, bookNumber);
        Books book = booksService.getOne(queryWrapper);
        if (book == null || book.getBookStatus().equals(Constant.BOOKAVAILABLE)) {
            return R.error("该图书未借出或图书编号不存在");
        }
        result.setStatus(200);
        result.setMsg("图书已借出");
        return result;
    }

    /**
     * 获取图书逾期信息
     *
     * @param bookNumber 图书编号
     * @return R<Violation>
     */
    @GetMapping("query_expire/{bookNumber}")
    public R<ViolationDTO> queryExpireInformationByBookNumber(@PathVariable("bookNumber") Long bookNumber) {
        /**
         * 1.根据图书编号和归还日期(null)去借阅表中查询唯一的一条记录
         * 2.如果记录不存在,返回错误信息
         * 3.根据获取记录的，现在日期和借阅日期算出 逾期日期 expireDays
         * 4.将截止日期、图书编号、逾期日期、封装到DTO，设置响应状态码和请求信息，返回前端
         */
        LambdaQueryWrapper<BooksBorrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BooksBorrow::getBookNumber, bookNumber).isNull(BooksBorrow::getReturnDate);
        BooksBorrow bookBorrowRecord = booksBorrowService.getOne(queryWrapper);
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
     * 归还图书
     *
     * @param violation 违章表
     * @return R
     */
    @PostMapping("return_book")
    @Transactional
    public R returnBook(@RequestBody Violation violation) {
        /**
         * 1.获取归还日期和违章信息和图书编号
         * 2.根据图书编号，查询归还日期为空的记录,更新图书表
         * 3.更新违章表
         * 4.更新图书表，图书编号的借出状态
         * 5.三个表都更新则返回成功的响应状态码和请求信息，否则返回失败信息
         */
        Long bookNumber = violation.getBookNumber();
        LocalDateTime returnDate = violation.getReturnDate();
        String violationMessage = violation.getViolationMessage();
        Integer violationAdminId = violation.getViolationAdminId();
        LambdaQueryWrapper<Violation> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<BooksBorrow> queryWrapper1 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Books> queryWrapper2 = new LambdaQueryWrapper<>();

        queryWrapper.eq(Violation::getBookNumber, bookNumber).isNull(Violation::getReturnDate);
        queryWrapper1.eq(BooksBorrow::getBookNumber, bookNumber).isNull(BooksBorrow::getReturnDate);
        queryWrapper2.eq(Books::getBookNumber, bookNumber);
        Violation violation1 = violationService.getOne(queryWrapper);
        BooksBorrow booksBorrow = booksBorrowService.getOne(queryWrapper1);
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
        boolean update2 = booksBorrowService.update(booksBorrow, queryWrapper1);
        boolean update3 = booksService.update(book, queryWrapper2);
        if (!update1 || !update2 || !update3) {
            return R.error("归还图书失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("归还图书成功");
        return result;

    }

    /**
     * 获取还书报表
     *
     * @param basePage 接受分页构造器和模糊查询的传参
     * @return R<Page < BooksBorrow>>
     */
    @PostMapping("get_return_statement")
    public R<Page<BooksBorrow>> getReturnStatement(@RequestBody BasePage basePage) {
        /**
         * 1.获取页码，页数，条件和查询内容
         * 2.判断条件或者查询内容是否有空值情况
         * 3.如果有空值，查询出所有记录(归还日期为null)
         * 4.创建条件构造器，like,调用booksBorrow.page(pageInfo,构造器)
         * 5.如果不为空则返回正确信息，为空返回错误信息
         */
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
            Page<BooksBorrow> page = booksBorrowService.page(pageInfo, queryWrapper);
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
        Page<BooksBorrow> page = booksBorrowService.page(pageInfo, queryWrapper1);
        if (page.getTotal() == 0) {
            return R.error("查询不到该还书报表信息");
        }
        result.setData(pageInfo);
        result.setMsg("获取还书报表信息成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 获取借书报表
     *
     * @param basePage 接受分页构造器和模糊查询的传参
     * @return R<Page < ViolationDTO>>
     */
    @PostMapping("get_borrow_statement")
    public R<Page<ViolationDTO>> getBorrowStatement(@RequestBody BasePage basePage) {
        /**
         * 1.获取页码，页数，条件和查询内容
         * 2.判断条件或者查询内容是否有空值情况
         * 3.如果有空值，查询出所有记录(归还日期不为null),封装DTO对象，调用Page方法,返回
         * 4.创建条件构造器，like,调用booksBorrow.page(pageInfo,构造器)
         * 5.如果不为空则返回正确信息，为空返回错误信息
         */
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
                BookAdmins bookAdmins = bookAdminsService.getOne(queryWrapper1);
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
            BookAdmins bookAdmins = bookAdminsService.getOne(queryWrapper1);
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

    /**
     * 获取公告列表
     *
     * @return R<Notice>
     */
    @PostMapping("get_noticelist")
    public R<Page<Notice>> getNoticeList(@RequestBody BasePage basePage) {
        /**
         * 1.查询出公告列表(创建时间升序)
         * 2.判断是否为空
         * 3.为空，返回错误信息,不为空返回成功新
         */
        int pageNum = basePage.getPageNum();
        int pageSize = basePage.getPageSize();
        Page<Notice> pageInfo = new Page<>(pageNum, pageSize);
        R<Page<Notice>> result = new R<>();
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Notice::getCreateTime);
        Page<Notice> page = noticeService.page(pageInfo, queryWrapper);
        if (page.getTotal() == 0) {
            return R.error("公告列表为空");
        }
        result.setStatus(200);
        result.setMsg("获取公告列表成功");
        result.setData(pageInfo);
        return result;
    }

    @PostMapping("add_notice")
    public R addNotice(@RequestBody Notice notice) {
        /**
         * 1.接受公告标题和公告内容(检查是否为空的情况),图书管理员id
         * 2.如果都不为空，调用服务新增公告，返回响应状态码和请求信息
         */
        String noticeTitle = notice.getNoticeTitle();
        String noticeContent = notice.getNoticeContent();
        R result = new R();
        if (StringUtils.isBlank(noticeTitle) || StringUtils.isBlank(noticeContent)) {
            return R.error("新增公告失败，存在空值");
        }
        Notice notice1 = new Notice();
        notice1.setNoticeId(null);
        notice1.setNoticeTitle(noticeTitle);
        notice1.setNoticeContent(noticeContent);
        notice1.setNoticeAdminId(notice.getNoticeAdminId());
        boolean save = noticeService.save(notice1);
        if (!save) {
            return R.error("新增公告失败");
        }
        result.setStatus(200);
        result.setMsg("新增公告成功");
        return result;
    }

    /**
     * 删除公告根据指定的id
     *
     * @param noticeId 公告id
     * @return R
     */
    @GetMapping("delete_notice/{noticeId}")
    public R deleteNoticeById(@PathVariable("noticeId") Integer noticeId) {
        boolean remove = noticeService.removeById(noticeId);
        if (!remove) {
            return R.error("删除公告失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("删除公告成功");
        return result;
    }

    /**
     * 根据指定id获取公告
     * @param noticeId 公告id
     * @return R<Notice>
     */
    @GetMapping("get_notice/{noticeId}")
    public R<Notice> getNoticeByNoticeId(@PathVariable("noticeId") Integer noticeId){
        Notice notice = noticeService.getById(noticeId);
        if(notice==null){
            return R.error("获取公告失败");
        }
        R<Notice> result = new R<>();
        result.setStatus(200);
        result.setMsg("获取公告信息成功");
        result.setData(notice);
        return result;
    }

    /**
     * 更新公告根据公告id
     * @param noticeId 公告id
     * @param notice 公告
     * @return R
     */
    @PutMapping("update_notice/{noticeId}")
    public R updateNoticeByNoticeId(@PathVariable("noticeId")Integer noticeId,@RequestBody Notice notice){
        String noticeTitle = notice.getNoticeTitle();
        String noticeContent = notice.getNoticeContent();
        if(StringUtils.isBlank(noticeTitle)||StringUtils.isBlank(noticeContent)){
            return R.error("修改公告失败");
        }
        Notice notice2 = noticeService.getById(noticeId);
        if(notice2==null){
            return R.error("修改公告失败");
        }
        notice2.setNoticeTitle(noticeTitle);
        notice2.setNoticeContent(noticeContent);
        boolean update = noticeService.updateById(notice2);
        if(!update){
            return R.error("修改公告失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("修改公告成功");
        return result;
    }
}
