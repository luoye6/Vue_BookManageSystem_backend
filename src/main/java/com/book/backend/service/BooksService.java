package com.book.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.Books;
import com.book.backend.pojo.dto.BookDTO;
import com.book.backend.pojo.dto.BooksBorrowDTO;
import com.book.backend.pojo.dto.BorrowTypeDTO;

import java.util.List;

/**
 * @author 赵天宇
 * @description 针对表【t_books】的数据库操作Service
 * @createDate 2023-02-04 18:07:43
 */
public interface BooksService extends IService<Books> {
    /**
     * 图书查询 分页和条件查询 (模糊查询)
     *
     * @param basePage 用于接受分页传参
     * @return R<Page < Books>>
     */
    R<Page<Books>> searchBookPage(BasePage basePage);

    /**
     * 借阅图书根据借阅证号和图书编号
     *
     * @return R
     */
    R<String> borrowBookByCardNumberAndBookNumber(BooksBorrowDTO booksBorrowDTO);

    /**
     * 查看图书是否有逾期(查看是否借出)
     *
     * @param bookNumber 图书编号
     * @return R
     */
    R<String> queryBookExpireByBookNumber(Long bookNumber);

    /**
     * 获取图书列表
     *
     * @param basePage 页码，页数，条件和内容
     * @return R<Page < Books>>
     */
    R<Page<Books>> getBookList(BasePage basePage);
    /**
     * 添加图书 利用DTO去接受 书籍类别的id 然后再通过id查询分类表获取分类名称 封装给图书
     *
     * @return R
     */
    R<String> addBook( BookDTO bookDTO);
    /**
     * 根据图书id删除对应的图书
     *
     * @param bookId 图书id
     * @return R
     */
    R<String> deleteBookByBookId( Integer bookId);
    /**
     * 根据图书id获得相对应的图书信息
     *
     * @param bookId 图书id
     * @return R<Books>
     */
    R<Books> getBookInformationByBookId( Integer bookId);
    /**
     * 根据前端传输的图书信息更新图书
     *
     * @param books 图书
     * @return R
     */
    R<String> updateBookByEditForm( Books books);
    /**
     * 获取借书分类统计情况
     *
     * @return R<List<BorrowTypeDTO>>
     */
    R<List<BorrowTypeDTO>> getBorrowTypeStatistic();
    /**
     * 批量删除图书
     *
     * @param booksList 图书列表
     * @return R<String>
     */
    R<String> deleteBookByBatch( List<Books> booksList);

}
