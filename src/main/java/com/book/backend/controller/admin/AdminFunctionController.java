package com.book.backend.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.Constant;
import com.book.backend.common.R;
import com.book.backend.pojo.*;
import com.book.backend.pojo.dto.BookDTO;
import com.book.backend.pojo.dto.BookRuleDTO;
import com.book.backend.pojo.dto.BorrowData;
import com.book.backend.pojo.dto.UsersDTO;
import com.book.backend.service.*;
import com.book.backend.utils.BorrowDateUtil;
import com.book.backend.utils.NumberUtil;
import com.book.backend.utils.RandomNameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * @author 赵天宇
 */
@RestController
@RequestMapping("admin")
public class AdminFunctionController {
    @Autowired
    private BooksService booksService;
    @Autowired
    private BookTypeService bookTypeService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private BookRuleService bookRuleService;
    @Autowired
    private BookAdminsService bookAdminsService;
    @Autowired
    private ViolationService violationService;
    @PostMapping("get_booklist")
    public R<Page<Books>> getBookList(@RequestBody BasePage basePage) {
        /**
         * 1.获取页码，页数，条件和查询内容
         * 2.判断条件或者查询内容是否有空值情况
         * 3.如果有空值，查询出所有记录(根据图书编号升序)
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
        Page<Books> pageInfo = new Page<>(pageNum, pageSize);
        R<Page<Books>> result = new R<>();
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(query)) {
            LambdaQueryWrapper<Books> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByAsc(Books::getBookNumber);
            Page<Books> page = booksService.page(pageInfo, queryWrapper);
            if (page.getTotal() == 0) {
                return R.error("图书列表为空");
            }
            result.setData(pageInfo);
            result.setStatus(200);
            result.setMsg("获取图书列表成功");
            return result;

        }
        QueryWrapper<Books> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.like(condition, query);
        Page<Books> page = booksService.page(pageInfo, queryWrapper1);
        if (page.getTotal() == 0) {
            return R.error("图书列表为空");
        }
        result.setData(pageInfo);
        result.setStatus(200);
        result.setMsg("获取图书列表成功");
        return result;
    }

    /**
     * 查询书籍类型的列表 用于添加图书中回显分类
     *
     * @return R<BookType>
     */
    @GetMapping("get_type")
    public R<List<BookType>> getBookTypeList() {
        List<BookType> list = bookTypeService.list();
        R<List<BookType>> result = new R<>();
        if (list.isEmpty()) {
            return R.error("获取书籍分类失败");
        }
        result.setData(list);
        result.setMsg("获取书籍分类成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 添加图书 利用DTO去接受 书籍类别的id 然后再通过id查询分类表获取分类名称 封装给图书
     *
     * @return R
     */
    @PostMapping("add_book")
    public R addBook(@RequestBody BookDTO bookDTO) {
        /**
         * 1.获取图书名称，图书作者，图书馆名称，书籍类别的id,书籍位置，书籍状态，书籍介绍
         * 2.随机生成11位数字的图书编号
         * 3.根据书籍类别的id查询分类表中书籍类别的名称
         * 4.封装名称，保存图书
         * 5.判断是否成功，赋值相应的响应状态吗和请求信息，返回前端
         */
        R result = new R();
        Integer bookTypeNumber = bookDTO.getBookTypeNumber();
        LambdaQueryWrapper<BookType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookType::getTypeId, bookTypeNumber);
        BookType bookType = bookTypeService.getOne(queryWrapper);
        if (bookType == null) {
            return R.error("添加图书失败");
        }
        // 获取类别名称
        String typeName = bookType.getTypeName();
        // 生成11位数字的图书编号
        StringBuilder stringNumber = NumberUtil.getNumber(11);
        long bookNumber = Long.parseLong(new String(stringNumber));
        Books books = new Books();
        BeanUtils.copyProperties(bookDTO, books, "book_type");
        books.setBookType(typeName);
        books.setBookNumber(bookNumber);
        boolean save = booksService.save(books);
        if (!save) {
            return R.error("添加图书失败");
        }
        result.setStatus(200);
        result.setMsg("添加图书信息成功");
        return result;
    }

    /**
     * 根据图书id删除对应的图书
     *
     * @param bookId 图书id
     * @return R
     */
    @GetMapping("delete_book/{bookId}")
    public R deleteBookByBookId(@PathVariable("bookId") Integer bookId) {
        /**
         * 1.先根据图书id查询是否有这本图书，如果图书不存在直接返回
         * 2.图书存在，执行删除操作
         * 3.删除是否成功，返回响应的响应状态码和请求信息,返回前端
         */
        Books books = booksService.getById(bookId);
        if (books == null) {
            return R.error("删除图书失败");
        }
        boolean remove = booksService.removeById(bookId);
        if (!remove) {
            return R.error("删除图书失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("删除图书成功");
        return result;
    }

    /**
     * 根据图书id获得相对应的图书信息
     *
     * @param bookId 图书id
     * @return R<Books>
     */
    @GetMapping("get_bookinformation/{bookId}")
    public R<Books> getBookInformationByBookId(@PathVariable("bookId") Integer bookId) {
        /**
         * 1.根据图书id获取相对应的图书
         * 2.判断图书是否为null,如果为空，返回错误信息
         * 3.如果不为空，返回图书数据，响应状态吗和请求信息。
         */
        Books books = booksService.getById(bookId);
        if (books == null) {
            return R.error("获取图书信息错误");
        }
        R<Books> result = new R<>();
        result.setStatus(200);
        result.setData(books);
        result.setMsg("获取图书信息成功");
        return result;
    }

    /**
     * 根据前端传输的图书信息更新图书
     *
     * @param books 图书
     * @return R
     */
    @PostMapping("update_book")
    public R updateBookByEditForm(@RequestBody Books books) {
        /**
         * 1.判断books是否为空
         * 2.为空返回错误信息
         * 3.不为空，返回响应状态吗和正确信息
         */
        if (books == null) {
            return R.error("修改图书失败");
        }
        boolean update = booksService.updateById(books);
        if (!update) {
            return R.error("修改图书失败");
        }
        R result = new R();
        result.setMsg("修改图书成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 书籍类别 获取书籍类别的列表
     *
     * @return R<List < BookType>>
     */
    @PostMapping("get_booktype_page")
    public R<Page<BookType>> getBookTypeListByPage(@RequestBody BasePage basePage) {
        /**
         * 1.获取页码和页数
         * 2.调用服务的page分页 判断是否为空
         * 3.如果不为空，存入数据，200响应状态吗，请求成功信息
         */
        // 页码
        int pageNum = basePage.getPageNum();
        // 页数
        int pageSize = basePage.getPageSize();
        Page<BookType> pageInfo = new Page<>(pageNum, pageSize);
        Page<BookType> page = bookTypeService.page(pageInfo);
        if (page.getTotal() == 0) {
            return R.error("书籍分类列表为空");
        }
        R<Page<BookType>> result = new R<>();
        result.setData(pageInfo);
        result.setMsg("获取书籍分类列表成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 添加书籍类别
     *
     * @param bookType 书籍类别
     * @return R
     */
    @PostMapping("add_booktype")
    public R addBookType(@RequestBody BookType bookType) {
        /**
         * 1.调用服务插入书籍类别
         * 2.判断是否成功
         * 3.成功返回响应状态码和请求信息
         */
        boolean save = bookTypeService.save(bookType);
        if (!save) {
            return R.error("添加书籍类别失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("添加书籍类别成功");
        return result;
    }

    /**
     * 根据书籍类别id 获取书籍类别信息
     *
     * @param typeId 书籍类别id
     * @return R
     */
    @GetMapping("get_booktype/{typeId}")
    public R getBookTypeByTypeId(@PathVariable("typeId") Integer typeId) {
        /**
         * 1.根据typeId查询
         * 2.判断是否为空 不为空返回前端
         */
        BookType type = bookTypeService.getById(typeId);
        if (type == null) {
            return R.error("获取书籍类别失败");
        }
        R result = new R();
        result.setData(type);
        result.setStatus(200);
        result.setMsg("获取书籍类别成功");
        return result;
    }

    /**
     * 更新书籍类别
     *
     * @param bookType 书籍类别
     * @return R
     */
    @PostMapping("update_booktype")
    public R updateBookType(@RequestBody BookType bookType) {
        /**
         * 1.判断空参数
         * 2.更新书籍 判断是否成功
         * 3.成功->200 失败->错误信息
         */
        String typeContent = bookType.getTypeContent();
        String typeName = bookType.getTypeName();
        if (StringUtils.isBlank(typeContent) || StringUtils.isBlank(typeName)) {
            return R.error("更新书籍类别失败");
        }
        boolean update = bookTypeService.updateById(bookType);
        if (!update) {
            return R.error("更新书籍类别失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("更新书籍类别成功");
        return result;
    }

    /**
     * 删除书籍类别 根据书籍类别的ID
     *
     * @param typeId 书籍类别的id
     * @return R
     */
    @GetMapping("delete_booktype/{typeId}")
    public R deleteBookTypeByTypeId(@PathVariable("typeId") Integer typeId) {
        /**
         * 1.先根据typeId查询是否有此书籍类别
         * 2.调用服务，删除书籍类别，判断是否成功
         * 3.成功->200 失败->错误信息
         */
        BookType bookType = bookTypeService.getById(typeId);
        if (bookType == null) {
            return R.error("删除书籍类别失败");
        }
        boolean remove = bookTypeService.removeById(typeId);
        if (!remove) {
            return R.error("删除书籍类别失败");
        }
        R result = new R();
        result.setMsg("删除书籍类别成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 获取借阅证列表(用户列表)
     *
     * @param basePage 用于接受模糊查询和分页构造的参数
     * @return R<Page < Users>>
     */
    @PostMapping("get_statementlist")
    public R<Page<Users>> getStatementList(@RequestBody BasePage basePage) {
        /**
         * 1.接受页数、页码、模糊查询条件和内容 创建分页构造器
         * 2.判断条件和内容是否有一个为空，如果为空则查询所有记录(判空),放入分页构造器
         * 3.如果二者都不为空，构造条件构造器，通过QueryWrapper的like方法模糊查询
         * 4.放入分页构造器，判断getTotal是否为空
         * 5.不为空->正确信息,为空->错误信息
         */
        // 页数
        int pageSize = basePage.getPageSize();
        // 页码
        int pageNum = basePage.getPageNum();
        // 条件
        String condition = basePage.getCondition();
        // 内容
        String query = basePage.getQuery();
        R<Page<Users>> result = new R<>();
        Page<Users> pageInfo = new Page<>(pageNum, pageSize);
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(query)) {
            Page<Users> page = usersService.page(pageInfo);
            if (page.getTotal() == 0) {
                return R.error("借阅证列表为空");
            }
            result.setStatus(200);
            result.setMsg("获取借阅证列表成功");
            result.setData(pageInfo);
            return result;
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(condition, query);
        Page<Users> page = usersService.page(pageInfo, queryWrapper);
        if (page.getTotal() == 0) {
            return R.error("借阅证列表为空");
        }
        result.setStatus(200);
        result.setData(pageInfo);
        result.setMsg("获取借阅证列表成功");
        return result;
    }

    @PostMapping("add_statement")
    public R addStatement(@RequestBody UsersDTO usersDTO) {
        /**
         * 1.接受请求发送的用户名，密码，规则编号，用户状态
         * 2.根据用户状态可用/禁用 去设置1和0
         * 3.用户id自增设为null,密码需要md5加密,随机生成card_name姓名
         * 4.工具类随机生成11位借阅证编号
         * 5.调用服务插入用户，判断是否成功
         */
        // 用户名
        String username = usersDTO.getUsername();
        // 密码
        String password = usersDTO.getPassword();
        // 规则编号
        Integer ruleNumber = usersDTO.getRuleNumber();
        // 用户状态
        String userStatus = usersDTO.getUserStatus();
        Integer status = 0;
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
        R result = new R();
        result.setMsg("添加借阅证成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 获取用户信息 根据用户id  用于回显借阅证
     *
     * @param userId 用户id
     * @return R<UsersDTO>
     */
    @GetMapping("get_statement/{userId}")
    public R<UsersDTO> getStatementByUserId(@PathVariable("userId") Integer userId) {
        /**
         * 1.根据用户id查询是否存在该用户
         * 2.如果存在，封装DTO返回信息
         * 3.不存在,返回错误信息
         */
        Users users = usersService.getById(userId);
        if (users == null) {
            return R.error("获取用户信息失败");
        }
        R<UsersDTO> result = new R<>();
        Integer status = users.getStatus();
        UsersDTO usersDTO = new UsersDTO();
        BeanUtils.copyProperties(users, usersDTO);
        if (status.equals(Constant.AVAILABLE)) {
            usersDTO.setUserStatus("可用");
        } else {
            usersDTO.setUserStatus("禁用");
        }
        result.setData(usersDTO);
        result.setStatus(200);
        result.setMsg("获取用户信息成功");
        return result;
    }

    /**
     * 修改借阅证信息(用户信息)
     *
     * @param usersDTO 用户DTO
     * @return R
     */
    @PostMapping("update_statement")
    public R updateStatement(@RequestBody UsersDTO usersDTO) {
        /**
         * 1.接受用户名，密码(需要md5加密),规则编号，状态
         * 2.将usersDTO拷贝到users,忽略状态
         * 3.根据可用/禁用，设置用户的状态
         * 4.调用服务更新用户信息
         * 5.判断是否成功，成功->返回前端,错误->错误信息返回
         */
        Users users = new Users();
        BeanUtils.copyProperties(usersDTO, users, "password", "userStatus");
        String userStatus = usersDTO.getUserStatus();
        if (Constant.USERAVAILABLE.equals(userStatus)) {
            users.setStatus(1);
        } else {
            users.setStatus(0);
        }
        String password = usersDTO.getPassword();
        if(password.length()>=Constant.MD5PASSWORD){
            users.setPassword(password);
        }else{
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
            users.setPassword(md5Password);
        }

        boolean update = usersService.updateById(users);
        if (!update) {
            return R.error("修改借阅证信息失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("修改借阅证信息成功");
        return result;
    }

    /**
     * 删除借阅证信息 根据用户id
     *
     * @param userId 用户id
     * @return R
     */
    @DeleteMapping("delete_statement/{userId}")
    public R deleteStatementByUserId(@PathVariable("userId") Integer userId) {
        /**
         * 1.根据userId查询是否有该用户
         * 2.如果有，执行删除操作，判断是否成功
         */
        Users users = usersService.getById(userId);
        if (users == null) {
            return R.error("删除借阅证失败");
        }
        boolean remove = usersService.removeById(userId);
        if (!remove) {
            return R.error("删除借阅证失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("删除借阅证成功");
        return result;
    }

    /**
     * 获取规则列表(分页)
     *
     * @param basePage 分页构造器用于接受页数和页码
     * @return R<Page < BookRule>>
     */
    @PostMapping("get_rulelist_page")
    public R<Page<BookRule>> getRuleListByPage(@RequestBody BasePage basePage) {
        /**
         * 1.接受页码和页数，创建分页构造器
         * 2.调用服务的page方法，条件构造器按创建时间升序
         * 3.判断是否page返回为空
         * 4.不为空，200->前端
         */
        // 页码
        int pageNum = basePage.getPageNum();
        // 页数
        int pageSize = basePage.getPageSize();
        Page<BookRule> pageInfo = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(BookRule::getCreateTime);
        Page<BookRule> page = bookRuleService.page(pageInfo, queryWrapper);
        if (page.getTotal() == 0) {
            return R.error("规则列表为空");
        }
        R<Page<BookRule>> result = new R<>();
        result.setData(pageInfo);
        result.setMsg("获取规则列表成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 添加规则
     *
     * @param bookRule 图书规则
     * @return R
     */
    @PostMapping("add_rule")
    public R addRule(@RequestBody BookRule bookRule) {
        /**
         * 1.获取限制天数，限制本数，逾期费用和限制的图书馆
         * 2.随机生成三位数编号
         * 3.调用服务，插入图书编号判断是否成功
         * 4.成功->200,失败->错误信息
         */
        // 限制天数
        Integer bookDays = bookRule.getBookDays();
        // 图书馆
        String bookLimitLibrary = bookRule.getBookLimitLibrary();
        // 限制本数
        Integer bookLimitNumber = bookRule.getBookLimitNumber();
        // 逾期费用
        Double bookOverdueFee = bookRule.getBookOverdueFee();
        // 三位数
        bookRule.setBookRuleId(Integer.parseInt(new String(NumberUtil.getNumber(3))));
        boolean save = bookRuleService.save(bookRule);
        if (!save) {
            return R.error("添加图书规则失败");
        }
        R result = new R();
        result.setStatus(200);
        result.setMsg("添加图书规则成功");
        return result;
    }

    /**
     * 根据规则编号 查询规则
     *
     * @param ruleId 规则编号
     * @return R<BookRule>
     */
    @GetMapping("get_rule_ruleid/{ruleId}")
    public R<BookRuleDTO> getRuleByRuleId(@PathVariable("ruleId") Integer ruleId) {
        /**
         * 1.先根据规则编号查询是否有该规则
         * 2.没有查询到直接返回，查询到，将属性拷贝，并将限制图书馆封装给DTO
         * 3.将DTO返回前端
         */
        BookRule bookRule = bookRuleService.getById(ruleId);
        if (bookRule == null) {
            return R.error("获取规则信息失败");
        }
        BookRuleDTO bookRuleDTO = new BookRuleDTO();
        BeanUtils.copyProperties(bookRule, bookRuleDTO, "bookLimitLibrary");
        //
        String bookLimitLibrary = bookRule.getBookLimitLibrary();
        String[] split = bookLimitLibrary.split(",");
        bookRuleDTO.setCheckList(split);
        R<BookRuleDTO> result = new R<>();
        result.setData(bookRuleDTO);
        result.setStatus(200);
        result.setMsg("获取规则信息成功");
        return result;
    }

    /**
     * 修改规则
     * @param bookRuleDTO 图书规则
     * @return R
     */
    @PutMapping("update_rule")
    public R updateRule(@RequestBody BookRuleDTO bookRuleDTO){
        /**
         * 1.接受限制的图书馆数组，将数组变为字符串
         * 2.拷贝属性忽略限制图书馆，单独设置字符串的限制图书馆
         * 3.调用服务更新规则，判断是否成功
         * 4.成功->200,失败->错误信息
         */
        String[] checkList = bookRuleDTO.getCheckList();
        String bookLimitLibrary = String.join(",", checkList);
        BookRule bookRule = new BookRule();
        BeanUtils.copyProperties(bookRuleDTO,bookRule,"bookLimitLibrary");
        bookRule.setBookLimitLibrary(bookLimitLibrary);
        boolean update = bookRuleService.updateById(bookRule);
        if(!update){
            return R.error("更新规则失败");
        }
        R result = new R();
        result.setMsg("更新规则成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 删除规则
     * @param ruleId 规则编号
     * @return R
     */
    @DeleteMapping("delete_rule/{ruleId}")
    public R deleteRule(@PathVariable("ruleId") Integer ruleId){
        /**
         * 1.根据规则id查询是否有该规则
         * 2.如果有调用删除的方法，判断是否成功
         */
        BookRule bookRule = bookRuleService.getById(ruleId);
        if(bookRule==null){
            return R.error("删除规则失败");
        }
        boolean remove = bookRuleService.removeById(ruleId);
        if(!remove){
            return R.error("删除规则失败");
        }
        return R.success(null,"删除规则成功");
    }

    /**
     * 获取图书管理员的列表
     * @param basePage 分页构造器传参
     * @return R<Page<BookAdmins>>
     */
    @PostMapping("get_bookadminlist")
    public R<Page<BookAdmins>> getBookAdminListByPage(@RequestBody BasePage basePage){
        // 页码
        int pageNum = basePage.getPageNum();
        // 页数
        int pageSize = basePage.getPageSize();
        Page<BookAdmins> pageInfo = new Page<>(pageNum,pageSize);
        Page<BookAdmins> page = bookAdminsService.page(pageInfo);
        if(page.getTotal()==0){
            return R.error("图书管理员列表为空");
        }
        return R.success(pageInfo,"获取图书管理员列表成功");
    }

    /**
     * 添加图书管理员
     * @param bookAdmins 图书管理员
     * @return R<String>
     */
    @PostMapping("add_bookadmin")
    public R<String> addBookAdmin(@RequestBody BookAdmins bookAdmins){
        /**
         * 1.接受图书管理员的参数(用户名，密码，姓名，邮箱)
         * 2.对密码进行md5加密,设置状态为1可用
         * 3.调用服务插入图书管理员，判断是否成功
         * 4.成功->200,失败->返回错误信息
         */
        // 获取未加密的密码
        String password = bookAdmins.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        bookAdmins.setPassword(md5Password);
        bookAdmins.setStatus(Constant.AVAILABLE);
        boolean save = bookAdminsService.save(bookAdmins);
        if(!save){
            return R.error("添加图书管理员失败");
        }
        return R.success(null,"添加图书管理员成功");
    }

    /**
     * 获取图书管理员信息 通过图书管理员id
     * @param bookAdminId 图书管理员id
     * @return R<BookAdmins>
     */
    @GetMapping("get_bookadmin/{bookAdminId}")
    public R<BookAdmins> getBookAdminById(@PathVariable("bookAdminId")Integer bookAdminId){
        /**
         * 1.调用服务查询是否有该id对应的图书管理员
         * 2.如果存在，封装到数据实体类
         * 3.不存在，返回错误信息
         */
        BookAdmins bookAdmins = bookAdminsService.getById(bookAdminId);
        if(bookAdmins==null){
            return R.error("获取图书管理员信息失败");
        }
        return R.success(bookAdmins,"获取图书管理员信息成功");
    }

    /**
     * 删除图书管理员 根据图书管理员id
     * @param bookAdminId 图书管理员id
     * @return R<String>
     */
    @DeleteMapping("delete_bookadmin/{bookAdminId}")
    public R<String> deleteBookAdminById(@PathVariable("bookAdminId")Integer bookAdminId){
        BookAdmins bookAdmins = bookAdminsService.getById(bookAdminId);
        if(bookAdmins==null){
            return R.error("删除图书管理员失败");
        }
        boolean remove = bookAdminsService.removeById(bookAdminId);
        if(!remove){
            return R.error("删除图书管理员失败");
        }
        return R.success(null,"删除图书管理员成功");
    }

    /**
     * 修改图书管理员
     * @param bookAdmins 图书管理员
     * @return R<String>
     */

    @PutMapping("update_bookadmin")
    public R<String> updateBookAdmin(@RequestBody BookAdmins bookAdmins){
        String password = bookAdmins.getPassword();
        if(password.length()>=Constant.MD5PASSWORD){
            bookAdmins.setPassword(password);
        }else{
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
            bookAdmins.setPassword(md5Password);
        }
        boolean update = bookAdminsService.updateById(bookAdmins);
        if(!update){
            return R.error("修改图书管理员失败");
        }
        return R.success(null,"修改图书管理员成功");
    }

    /**
     * 获取借阅量
     * @return  R<BorrowData>
     */
    @GetMapping("get_borrowdata")
    public R<BorrowData> getBorrowDate(){
        /**
         * 1.分别获取5个时间节点，计算四个间隔之间的借阅量，也就是一周的借阅量
         * 2.时间格式化 然后封装到BorrowDate的日期数组中 再分别封装借阅量
         * 3.返回前端
         */
        LocalDateTime now = LocalDateTime.now();
        String[] dateArray = BorrowDateUtil.getDateArray(now);
        LocalDateTime time1 = now.minusWeeks(1);
        LocalDateTime time2 = now.minusWeeks(2);
        LocalDateTime time3 = now.minusWeeks(3);
        LocalDateTime time4 = now.minusWeeks(4);
        LambdaQueryWrapper<Violation> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.between(Violation::getBorrowDate,time1,now);
        LambdaQueryWrapper<Violation> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.between(Violation::getBorrowDate,time2,time1);
        LambdaQueryWrapper<Violation> queryWrapper3 = new LambdaQueryWrapper<>();
        queryWrapper3.between(Violation::getBorrowDate,time3,time2);
        LambdaQueryWrapper<Violation> queryWrapper4 = new LambdaQueryWrapper<>();
        queryWrapper4.between(Violation::getBorrowDate,time4,time3);
        List<Violation> list1 = violationService.list(queryWrapper1);
        List<Violation> list2 = violationService.list(queryWrapper2);
        List<Violation> list3 = violationService.list(queryWrapper3);
        List<Violation> list4 = violationService.list(queryWrapper4);
        Integer [] borrowNumbers = new Integer[4];
        borrowNumbers[3] = list1.size();
        borrowNumbers[1] = list2.size();
        borrowNumbers[2] = list3.size();
        borrowNumbers[0] = list4.size();
        BorrowData borrowData = new BorrowData(dateArray,borrowNumbers);
        return R.success(borrowData,"获取借阅量成功");
    }
}
