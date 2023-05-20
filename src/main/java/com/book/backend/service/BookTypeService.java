package com.book.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.BookType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 赵天宇
 * @description 针对表【t_book_type】的数据库操作Service
 * @createDate 2023-02-04 18:51:24
 */
public interface BookTypeService extends IService<BookType> {
    /**
     * 查询书籍类型的列表 用于添加图书中回显分类
     *
     * @return R<BookType>
     */
    R<List<BookType>> getBookTypeList();

    /**
     * 书籍类别 获取书籍类别的列表
     *
     * @return R<List < BookType>>
     */
    R<Page<BookType>> getBookTypeListByPage(BasePage basePage);

    /**
     * 添加书籍类别
     *
     * @param bookType 书籍类别
     * @return R
     */
    R<String> addBookType(BookType bookType);
    /**
     * 根据书籍类别id 获取书籍类别信息
     *
     * @param typeId 书籍类别id
     * @return R
     */
    R<BookType> getBookTypeByTypeId(Integer typeId);
    /**
     * 更新书籍类别
     *
     * @param bookType 书籍类别
     * @return R
     */
    R<String> updateBookType( BookType bookType);
    /**
     * 删除书籍类别 根据书籍类别的ID
     *
     * @param typeId 书籍类别的id
     * @return R
     */
    R<String> deleteBookTypeByTypeId( Integer typeId);
}
