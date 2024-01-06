package com.book.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.BookRule;
import com.baomidou.mybatisplus.extension.service.IService;
import com.book.backend.pojo.dto.BookRuleDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
* @author 程序员小白条
* @description 针对表【t_book_rule】的数据库操作Service
* @createDate 2023-02-05 15:11:20
*/
public interface BookRuleService extends IService<BookRule> {
    /**
     * 读者规则查询
     *
     * @return R<List < BookRule>>
     */
    R<List<BookRule>> getRuleList();
    /**
     * 获取规则列表(分页)
     *
     * @param basePage 分页构造器用于接受页数和页码
     * @return R<Page < BookRule>>
     */
    R<Page<BookRule>> getRuleListByPage( BasePage basePage);
    /**
     * 添加规则
     *
     * @param bookRule 图书规则
     * @return R
     */
    R<String> addRule( BookRule bookRule);
    /**
     * 根据规则编号 查询规则
     *
     * @param ruleId 规则编号
     * @return R<BookRule>
     */
    R<BookRuleDTO> getRuleByRuleId( Integer ruleId);
    /**
     * 修改规则
     *
     * @param bookRuleDTO 图书规则
     * @return R
     */
    R<String> updateRule( BookRuleDTO bookRuleDTO);
    /**
     * 删除规则
     *
     * @param ruleId 规则编号
     * @return R
     */
    R<String> deleteRule( Integer ruleId);
}
