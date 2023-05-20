package com.book.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.Violation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.book.backend.pojo.dto.BorrowData;
import com.book.backend.pojo.dto.ViolationDTO;
import org.springframework.web.bind.annotation.RequestBody;

/**
* @author 赵天宇
* @description 针对表【t_violation】的数据库操作Service
* @createDate 2023-02-06 16:31:20
*/
public interface ViolationService extends IService<Violation> {
    /**
     * 查询违章信息(借阅证)
     *
     * @param basePage 获取前端的分页参数，条件和内容，借阅证
     * @return R<Page < ViolationDTO>>
     */
    R<Page<ViolationDTO>> getViolationListByPage( BasePage basePage);
    /**
     * 获取借阅量
     *
     * @return R<BorrowData>
     */
    R<BorrowData> getBorrowDate();
}
