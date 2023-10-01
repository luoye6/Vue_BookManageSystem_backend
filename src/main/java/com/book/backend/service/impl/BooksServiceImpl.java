package com.book.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.common.BasePage;
import com.book.backend.constant.Constant;
import com.book.backend.common.R;
import com.book.backend.common.exception.CommonError;
import com.book.backend.common.exception.VueBookException;
import com.book.backend.mapper.BooksMapper;
import com.book.backend.pojo.*;
import com.book.backend.pojo.dto.BookDTO;
import com.book.backend.pojo.dto.BooksBorrowDTO;
import com.book.backend.pojo.dto.BorrowTypeDTO;
import com.book.backend.service.*;
import com.book.backend.utils.NumberUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 赵天宇
 * @description 针对表【t_books】的数据库操作Service实现
 * @createDate 2023-02-04 18:07:43
 */
@Service
public class BooksServiceImpl extends ServiceImpl<BooksMapper, Books>
        implements BooksService {
    @Resource
    private UsersService usersService;
    @Resource
    private BookRuleService bookRuleService;
    @Resource
    private ViolationService violationService;
    @Resource
    private BookTypeService bookTypeService;
    private BooksBorrowService booksBorrowService;

    @Autowired
    public BooksServiceImpl(@Lazy BooksBorrowService booksBorrowService) {
        this.booksBorrowService = booksBorrowService;
    }

    /**
     * 1.先判断BasePage中传入的condition和query是否有空值
     * 2.如果有空值，查询所有书本,放入分页构造器,设置响应状态码和请求信息，返回给前端
     * 3.如果没有空值，创建条件构造器，并根据条件和内容查询
     * 4.获取书本数据，判断是否为空，如果为空，设置响应状态码404,并提示前端查询不到数据
     * 5.如果不为空,放入分页构造器,设置响应状态码和请求信息，返回给前端
     */
    @Override
    public R<Page<Books>> searchBookPage(BasePage basePage) {

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
            this.page(pageInfo, queryWrapper);
            result.setData(pageInfo);
            result.setStatus(200);
            result.setMsg("获取图书信息成功");
            return result;
        }
        // 创建条件构造器
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(condition, query);
        // 根据条件模糊查询后分页
        Page<Books> page = this.page(pageInfo, queryWrapper);
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
     * 1.接受前端请求中的参数(借阅证号、图书编号、借阅时间(不能为空))
     * 2.先根据借阅证号查询是否有此用户存在，不存在直接返回错误信息
     * 3.用户存在，根据图书编号查询图书表，查询是否有图书存在并且该图书的状态是未借出
     * 4.用户存在，图书存在，且未借出 说明可以借出该图书
     * 5.#获取用户的规则编号#，根据编号查询出规则(判断空),获取规则的可借天数
     * 6.设置期限天数为当前时间+规则的可借天数 设置归还日期为空
     * 7.调用bookBorrow，进行插入记录
     * 8.如果插入成功，修改在图书表中对应图书编号的状态为已借出
     * 9.判断是否更新成功
     * 10.插入成功+更新成功，则返回请求状态码200和请求信息
     */
    @Transactional
    @Override
    public R<String> borrowBookByCardNumberAndBookNumber(BooksBorrowDTO booksBorrowDTO) {

        // 图书编号
        Long bookNumber = booksBorrowDTO.getBookNumber();
        // 借阅证号
        Long cardNumber = booksBorrowDTO.getCardNumber();
        // 借阅时间
        LocalDateTime borrowDate = booksBorrowDTO.getBorrowDate();
        if (borrowDate == null) {
            return R.error("借阅时间不能为空");
        }
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getCardNumber, cardNumber);
        Users users = usersService.getOne(queryWrapper);
        if (users == null) {
            return R.error("借阅图书失败");
        }
        LambdaQueryWrapper<Books> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Books::getBookNumber, bookNumber);
        Books book = this.getOne(queryWrapper1);
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
        boolean update = this.update(book, queryWrapper1);
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
        return R.success(null, "借阅图书成功");
    }

    /**
     * 1.获取图书编号
     * 2.根据图书编号查询图书表中是否存在该书
     * 3.如果存在并且状态为已借出，则返回成功信息
     * 4.状态未借出:返回错误信息
     */
    @Override
    public R<String> queryBookExpireByBookNumber(Long bookNumber) {

        LambdaQueryWrapper<Books> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Books::getBookNumber, bookNumber);
        Books book = this.getOne(queryWrapper);
        if (book == null || book.getBookStatus().equals(Constant.BOOKAVAILABLE)) {
            return R.error("该图书未借出或图书编号不存在");
        }
        return R.success(null, "图书已借出");
    }

    /**
     * 1.获取页码，页数，条件和查询内容
     * 2.判断条件或者查询内容是否有空值情况
     * 3.如果有空值，查询出所有记录(根据图书编号升序)
     * 4.创建条件构造器，like,调用booksBorrow.page(pageInfo,构造器)
     * 5.如果不为空则返回正确信息，为空返回错误信息
     */
    @Override
    public R<Page<Books>> getBookList(BasePage basePage) {

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
            Page<Books> page = this.page(pageInfo, queryWrapper);
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
        Page<Books> page = this.page(pageInfo, queryWrapper1);
        if (page.getTotal() == 0) {
            return R.error("图书列表为空");
        }
        result.setData(pageInfo);
        result.setStatus(200);
        result.setMsg("获取图书列表成功");
        return result;
    }

    /**
     * 1.获取图书名称，图书作者，图书馆名称，书籍类别的id,书籍位置，书籍状态，书籍介绍
     * 2.随机生成11位数字的图书编号
     * 3.根据书籍类别的id查询分类表中书籍类别的名称
     * 4.封装名称，保存图书
     * 5.判断是否成功，赋值相应的响应状态吗和请求信息，返回前端
     */
    @Transactional
    @Override
    public R<String> addBook(BookDTO bookDTO) {

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
        boolean save = this.save(books);
        if (!save) {
            return R.error("添加图书失败");
        }

        return R.success(null, "添加图书信息成功");
    }

    /**
     * 1.先根据图书id查询是否有这本图书，如果图书不存在直接返回
     * 2.图书存在，执行删除操作
     * 3.删除是否成功，返回响应的响应状态码和请求信息,返回前端
     */
    @Transactional
    @Override
    public R<String> deleteBookByBookId(Integer bookId) {

        Books books = this.getById(bookId);
        if (books == null) {
            return R.error("删除图书失败");
        }
        boolean remove = this.removeById(bookId);
        if (!remove) {
            return R.error("删除图书失败");
        }
        return R.success(null, "删除图书成功");
    }

    /**
     * 1.根据图书id获取相对应的图书
     * 2.判断图书是否为null,如果为空，返回错误信息
     * 3.如果不为空，返回图书数据，响应状态吗和请求信息。
     */
    @Override
    public R<Books> getBookInformationByBookId(Integer bookId) {
        Books books = this.getById(bookId);
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
     * 1.判断books是否为空
     * 2.为空返回错误信息
     * 3.不为空，返回响应状态吗和正确信息
     */
    @Override
    public R<String> updateBookByEditForm(Books books) {
        if (books == null) {
            return R.error("修改图书失败");
        }
        boolean update = this.updateById(books);
        if (!update) {
            return R.error("修改图书失败");
        }
        return R.success(null, "修改图书成功");
    }

    /**
     * 1.先获取所有的借书记录
     * 2.然后根据每条记录的图书编号去查询对应的分类
     * 3.如果hashMap中没有该分类，那么就初始化，添加String分类，然后Integer为0
     * 4.如果hashMap中有该分类，那么就获取该分类的值+1
     * 5.封装到通用格式中，返回前端
     */
    @Override
    public R<List<BorrowTypeDTO>> getBorrowTypeStatistic() {

        HashMap<String, Integer> hashMap = new HashMap<>();
        List<BorrowTypeDTO> list = new ArrayList<>();

        BooksBorrowService borrowService = booksBorrowService;
        List<BooksBorrow> booksBorrowList = borrowService.list();
        for (BooksBorrow booksBorrow : booksBorrowList) {
            Long bookNumber = booksBorrow.getBookNumber();
            LambdaQueryWrapper<Books> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Books::getBookNumber, bookNumber);
            Books book = this.getOne(queryWrapper);
            if (book == null) {
                VueBookException.cast(CommonError.OBJECT_NULL);
            }
            String bookType = book.getBookType();
            hashMap.put(bookType, hashMap.getOrDefault(bookType, 0) + 1);
        }
        Set<Map.Entry<String, Integer>> entries = hashMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            BorrowTypeDTO borrowTypeDTO = new BorrowTypeDTO();
            borrowTypeDTO.setBookTypes(entry.getKey());
            borrowTypeDTO.setBorrowNumbers(entry.getValue());
            list.add(borrowTypeDTO);
        }
        return R.success(list, "获取借书分类统计情况成功");
    }

    /**
     * 1.先获取所有的图书列表
     * 2.遍历图书列表，把所有需要删除的id都加入到一个集合中去
     * 3.调用booksService的批量删除图书(根据图书id)
     * 4.判断是否成功,如何成功返回相对应的提示信息，失败则提示失败信息
     */
    @Override
    public R<String> deleteBookByBatch(List<Books> booksList) {
        ArrayList<Integer> list = new ArrayList<>();
        for (Books books : booksList) {
            list.add(books.getBookId());
        }
        boolean delete = this.removeBatchByIds(list);
        if (delete) {
            return R.success(null, "批量删除图书成功");
        }
        return R.error("批量删除图书失败");
    }


}




