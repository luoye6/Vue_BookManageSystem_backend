package com.book.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.mapper.BookRuleMapper;
import com.book.backend.pojo.BookRule;
import com.book.backend.pojo.dto.BookRuleDTO;
import com.book.backend.service.BookRuleService;
import com.book.backend.utils.NumberUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 赵天宇
* @description 针对表【t_book_rule】的数据库操作Service实现
* @createDate 2023-02-05 15:11:20
*/
@Service
public class BookRuleServiceImpl extends ServiceImpl<BookRuleMapper, BookRule>
    implements BookRuleService{
    /**
     * 1.获取所有读者规则
     * 2.判断是否为空，如果为空，设置响应状态码和请求信息返回前端
     * 3.如果不为空，则设置200响应状态码和请求信息，封装到通用类,返回前端
     */
    @Override
    public R<List<BookRule>> getRuleList() {

        List<BookRule> list = this.list();
        R<List<BookRule>> result = new R<>();
        if (list.isEmpty()) {
            result.setStatus(404);
            result.setMsg("获取读者规则失败");
            return result;
        }
        result.setData(list);
        result.setStatus(200);
        result.setMsg("获取读者规则成功");
        return result;
    }
    /**
     * 1.接受页码和页数，创建分页构造器
     * 2.调用服务的page方法，条件构造器按创建时间升序
     * 3.判断是否page返回为空
     * 4.不为空，200->前端
     */
    @Override
    public R<Page<BookRule>> getRuleListByPage(BasePage basePage) {
        // 页码
        int pageNum = basePage.getPageNum();
        // 页数
        int pageSize = basePage.getPageSize();
        Page<BookRule> pageInfo = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(BookRule::getCreateTime);
        Page<BookRule> page = this.page(pageInfo, queryWrapper);
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
     * 1.获取限制天数，限制本数，逾期费用和限制的图书馆
     * 2.随机生成三位数编号
     * 3.调用服务，插入图书编号判断是否成功
     * 4.成功->200,失败->错误信息
     */
    @Override
    public R<String> addRule(BookRule bookRule) {
        // 三位数
        bookRule.setBookRuleId(Integer.parseInt(new String(NumberUtil.getNumber(3))));
        boolean save = this.save(bookRule);
        if (!save) {
            return R.error("添加图书规则失败");
        }
        return R.success(null,"添加图书规则成功");
    }
    /**
     * 1.先根据规则编号查询是否有该规则
     * 2.没有查询到直接返回，查询到，将属性拷贝，并将限制图书馆封装给DTO
     * 3.将DTO返回前端
     */
    @Override
    public R<BookRuleDTO> getRuleByRuleId(Integer ruleId) {
        BookRule bookRule = this.getById(ruleId);
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
     * 1.接受限制的图书馆数组，将数组变为字符串
     * 2.拷贝属性忽略限制图书馆，单独设置字符串的限制图书馆
     * 3.调用服务更新规则，判断是否成功
     * 4.成功->200,失败->错误信息
     */
    @Transactional
    @Override
    public R<String> updateRule(BookRuleDTO bookRuleDTO) {
        String[] checkList = bookRuleDTO.getCheckList();
        String bookLimitLibrary = String.join(",", checkList);
        BookRule bookRule = new BookRule();
        BeanUtils.copyProperties(bookRuleDTO, bookRule, "bookLimitLibrary");
        bookRule.setBookLimitLibrary(bookLimitLibrary);
        boolean update = this.updateById(bookRule);
        if (!update) {
            return R.error("更新规则失败");
        }
        return R.success(null,"更新规则成功");
    }
    /**
     * 1.根据规则id查询是否有该规则
     * 2.如果有调用删除的方法，判断是否成功
     */
    @Transactional
    @Override
    public R<String> deleteRule(Integer ruleId) {
        BookRule bookRule = this.getById(ruleId);
        if (bookRule == null) {
            return R.error("删除规则失败");
        }
        boolean remove = this.removeById(ruleId);
        if (!remove) {
            return R.error("删除规则失败");
        }
        return R.success(null, "删除规则成功");
    }
}




