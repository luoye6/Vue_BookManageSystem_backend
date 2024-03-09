package com.book.backend.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.*;
import com.book.backend.pojo.dto.CommentDTO;
import com.book.backend.pojo.dto.ViolationDTO;
import com.book.backend.service.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 程序员小白条
 */
@RestController
@RequestMapping("/user")
public class UserFunctionController {
    @Resource
    private BooksService booksService;
    @Resource
    private BookRuleService bookRuleService;
    @Resource
    private NoticeService noticeService;
    @Resource
    private UsersService usersService;
    @Resource
    private BooksBorrowService booksBorrowService;
    @Resource
    private ViolationService violationService;

    @Resource
    private CommentService commentService;
    @Resource
    private AiIntelligentService aiIntelligentService;

    /**
     * 图书查询 分页和条件查询 (模糊查询)
     *
     * @param basePage 用于接受分页传参
     * @return R<Page < Books>>
     */
    @PostMapping("/search_book_page")
    @ApiOperation("图书查询 分页和条件查询")
    public R<Page<Books>> searchBookPage(@RequestBody BasePage basePage) {
        return booksService.searchBookPage(basePage);
    }

    /**
     * 读者规则查询
     *
     * @return R<List < BookRule>>
     */
    @GetMapping("get_rulelist")
    @ApiOperation("读者规则查询")
    public R<List<BookRule>> getRuleList() {
        return bookRuleService.getRuleList();
    }

    /**
     * 查询公告信息
     *
     * @return R<List < Notice>>
     */
    @GetMapping("get_noticelist")
    @ApiOperation("查询公告信息")
    public R<List<Notice>> getNoticeList() {
        return noticeService.getNoticeList();
    }

    /**
     * Rest接受参数 查询个人用户userId
     *
     * @param userId 用户id
     * @return R<Users>
     */
    @GetMapping("get_information/{userId}")
    @ApiOperation("查询个人用户")
    public R<Users> getUserByUserId(@PathVariable("userId") Integer userId) {
        return usersService.getUserByUserId(userId);
    }

    /**
     * 修改密码
     *
     * @return R
     */
    @PostMapping("update_password")
    @ApiOperation("修改密码")
    public R<String> updatePassword(@RequestBody Users users) {
        return usersService.updatePassword(users);
    }

    /**
     * 借阅信息查询 根据用户id，条件及其内容
     *
     * @param basePage 用于接受分页传参和用户id
     * @return R<Page < BooksBorrow>>
     */
    @PostMapping("get_bookborrow")
    @ApiOperation("借阅信息查询")
    public R<Page<BooksBorrow>> getBookBorrowPage(@RequestBody BasePage basePage) {
        return booksBorrowService.getBookBorrowPage(basePage);
    }

    /**
     * 查询违章信息(借阅证)
     *
     * @param basePage 获取前端的分页参数，条件和内容，借阅证
     * @return R<Page < ViolationDTO>>
     */
    @PostMapping("get_violation")
    @ApiOperation("查询违章信息")
    public R<Page<ViolationDTO>> getViolationListByPage(@RequestBody BasePage basePage) {
        return violationService.getViolationListByPage(basePage);
    }

    /**
     * 获取弹幕列表
     *
     * @return R<Comment>
     */
    @GetMapping("get_commentlist")
    @ApiOperation("获取弹幕列表")
    public R<List<CommentDTO>> getCommentList() {
        return commentService.getCommentList();

    }

    /**
     * 添加弹幕
     *
     * @return R
     */
    @PostMapping("add_comment")
    @ApiOperation("添加弹幕")
    public R<String> addComment(@RequestBody CommentDTO commentDTO) {
        return commentService.addComment(commentDTO);
    }

    /**
     * 调用AI模型，获取数据库中有的，并且推荐图书给用户
     * @param aiIntelligent AI实体类
     * @return R<String>
     */
    @PostMapping("ai_intelligent")
    @ApiOperation("推荐图书")
    public R<String> aiRecommend(@RequestBody AiIntelligent aiIntelligent){
        return aiIntelligentService.getGenResult(aiIntelligent);
    }

    /**
     * 根据用户ID 获取该用户和AI聊天的最近的五条消息
     * @param userId 用户id
     * @return R<List<AiIntelligent>>
     */
    @GetMapping("ai_list_information/{userId}")
    @ApiOperation("获取该用户和AI聊天的最近的五条消息")
    public R<List<AiIntelligent>> getAiInformationByUserId(@PathVariable("userId") Long userId){
        return aiIntelligentService.getAiInformationByUserId(userId);
    }
}
