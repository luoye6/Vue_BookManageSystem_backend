package com.book.backend.controller.bookadmin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.BooksBorrow;
import com.book.backend.pojo.Notice;
import com.book.backend.pojo.Violation;
import com.book.backend.pojo.dto.BooksBorrowDTO;
import com.book.backend.pojo.dto.ViolationDTO;
import com.book.backend.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 程序员小白条
 */
@RestController
@RequestMapping("/bookadmin")
public class BookAdminFunctionController {
    @Resource
    private BookAdminsService bookAdminsService;

    @Resource
    private BooksService booksService;
    @Resource
    private BooksBorrowService booksBorrowService;

    @Resource
    private NoticeService noticeService;


    /**
     * 借阅图书根据借阅证号和图书编号
     *
     * @return R
     */
    @PostMapping("borrow_book")
    @ApiOperation("根据借阅证号和图书编号借阅图书")
    public R<String> borrowBookByCardNumberAndBookNumber(@RequestBody BooksBorrowDTO booksBorrowDTO) {
        return booksService.borrowBookByCardNumberAndBookNumber(booksBorrowDTO);
    }

    /**
     * 查看图书是否有逾期(查看是否借出)
     *
     * @param bookNumber 图书编号
     * @return R
     */
    @GetMapping("query_book/{bookNumber}")
    @ApiOperation("查看图书是否有逾期")
    public R<String> queryBookExpireByBookNumber(@PathVariable("bookNumber") Long bookNumber) {
        return booksService.queryBookExpireByBookNumber(bookNumber);
    }

    /**
     * 获取图书逾期信息
     *
     * @param bookNumber 图书编号
     * @return R<Violation>
     */
    @GetMapping("query_expire/{bookNumber}")
    @ApiOperation("获取图书逾期信息")
    public R<ViolationDTO> queryExpireInformationByBookNumber(@PathVariable("bookNumber") Long bookNumber) {
        return booksBorrowService.queryExpireInformationByBookNumber(bookNumber);
    }

    /**
     * 归还图书
     *
     * @param violation 违章表
     * @return R
     */
    @PostMapping("return_book")
    @ApiOperation("归还图书")
    public R<String> returnBook(@RequestBody Violation violation) {
        return booksBorrowService.returnBook(violation);
    }

    /**
     * 获取还书报表
     *
     * @param basePage 接受分页构造器和模糊查询的传参
     * @return R<Page < BooksBorrow>>
     */
    @PostMapping("get_return_statement")
    @ApiOperation("获取还书报表")
    public R<Page<BooksBorrow>> getReturnStatement(@RequestBody BasePage basePage) {
        return booksBorrowService.getReturnStatement(basePage);
    }

    /**
     * 获取借书报表
     *
     * @param basePage 接受分页构造器和模糊查询的传参
     * @return R<Page < ViolationDTO>>
     */
    @PostMapping("get_borrow_statement")
    @ApiOperation("获取借书报表")
    public R<Page<ViolationDTO>> getBorrowStatement(@RequestBody BasePage basePage) {
        return bookAdminsService.getBorrowStatement(basePage);
    }

    /**
     * 获取公告列表
     *
     * @return R<Notice>
     */
    @PostMapping("get_noticelist")
    @ApiOperation("获取公告列表")
    public R<Page<Notice>> getNoticeList(@RequestBody BasePage basePage) {
        return noticeService.getNoticeList(basePage);
    }

    /**
     * 添加公告
     *
     * @param notice 公告
     * @return R<String>
     */
    @PostMapping("add_notice")
    @ApiOperation("添加公告")
    public R<String> addNotice(@RequestBody Notice notice) {
        return noticeService.addNotice(notice);
    }

    /**
     * 删除公告根据指定的id
     *
     * @param noticeId 公告id
     * @return R
     */
    @GetMapping("delete_notice/{noticeId}")
    @ApiOperation("删除公告根据指定的id")
    public R<String> deleteNoticeById(@PathVariable("noticeId") Integer noticeId) {
        return noticeService.deleteNoticeById(noticeId);
    }

    /**
     * 根据指定id获取公告
     *
     * @param noticeId 公告id
     * @return R<Notice>
     */
    @GetMapping("get_notice/{noticeId}")
    @ApiOperation("根据指定id获取公告")
    public R<Notice> getNoticeByNoticeId(@PathVariable("noticeId") Integer noticeId) {
        return noticeService.getNoticeByNoticeId(noticeId);
    }

    /**
     * 更新公告根据公告id
     *
     * @param noticeId 公告id
     * @param notice   公告
     * @return R
     */
    @PutMapping("update_notice/{noticeId}")
    @ApiOperation("更新公告根据公告id")
    public R<String> updateNoticeByNoticeId(@PathVariable("noticeId") Integer noticeId, @RequestBody Notice notice) {
        return noticeService.updateNoticeByNoticeId(noticeId, notice);
    }
}
