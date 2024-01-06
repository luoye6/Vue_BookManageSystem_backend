package com.book.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.BooksBorrow;
import com.book.backend.pojo.Violation;
import com.book.backend.pojo.dto.ViolationDTO;

/**
 * @author 程序员小白条
 * @description 针对表【t_books_borrow】的数据库操作Service
 * @createDate 2023-02-05 18:53:07
 */
public interface BooksBorrowService extends IService<BooksBorrow> {
    /**
     * 借阅信息查询 根据用户id，条件及其内容
     *
     * @param basePage 用于接受分页传参和用户id
     * @return R<Page < BooksBorrow>>
     */
    R<Page<BooksBorrow>> getBookBorrowPage(BasePage basePage);

    /**
     * 获取图书逾期信息
     *
     * @param bookNumber 图书编号
     * @return R<Violation>
     */
    R<ViolationDTO> queryExpireInformationByBookNumber(Long bookNumber);

    /**
     * 归还图书
     *
     * @param violation 违章表
     * @return R
     */
    R<String> returnBook(Violation violation);
    /**
     * 获取还书报表
     *
     * @param basePage 接受分页构造器和模糊查询的传参
     * @return R<Page < BooksBorrow>>
     */
    R<Page<BooksBorrow>> getReturnStatement(BasePage basePage);
}
