package com.book.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.mapper.NoticeMapper;
import com.book.backend.pojo.Notice;
import com.book.backend.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 赵天宇
 * @description 针对表【t_notice】的数据库操作Service实现
 * @createDate 2023-02-05 16:14:03
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
        implements NoticeService {
    /**
     * 1.创建条件构造器，根据日期升序，查询
     * 2.判断是否为空,如果为空,设置响应状态码和请求信息返回前端
     * 3.如果不为空,则设置200响应状态码和请求信息，封装到通用类，返回前端
     */
    @Override
    public R<List<Notice>> getNoticeList() {

        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Notice::getCreateTime);
        List<Notice> list = this.list(queryWrapper);
        R<List<Notice>> result = new R<>();
        if (list.isEmpty()) {
            result.setStatus(404);
            result.setMsg("获取公告信息失败");
            return result;
        }
        result.setData(list);
        result.setStatus(200);
        result.setMsg("获取公告信息成功");
        return result;
    }

    /**
     * 1.查询出公告列表(创建时间升序)
     * 2.判断是否为空
     * 3.为空，返回错误信息,不为空返回成功新
     */
    @Override
    public R<Page<Notice>> getNoticeList(BasePage basePage) {

        int pageNum = basePage.getPageNum();
        int pageSize = basePage.getPageSize();
        Page<Notice> pageInfo = new Page<>(pageNum, pageSize);
        R<Page<Notice>> result = new R<>();
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Notice::getCreateTime);
        Page<Notice> page = this.page(pageInfo, queryWrapper);
        if (page.getTotal() == 0) {
            return R.error("公告列表为空");
        }
        result.setStatus(200);
        result.setMsg("获取公告列表成功");
        result.setData(pageInfo);
        return result;
    }

    /**
     * 1.接受公告标题和公告内容(检查是否为空的情况),图书管理员id
     * 2.如果都不为空，调用服务新增公告，返回响应状态码和请求信息
     */
    @Override
    public R<String> addNotice(Notice notice) {

        String noticeTitle = notice.getNoticeTitle();
        String noticeContent = notice.getNoticeContent();
        if (StringUtils.isBlank(noticeTitle) || StringUtils.isBlank(noticeContent)) {
            return R.error("新增公告失败，存在空值");
        }
        Notice notice1 = new Notice();
        notice1.setNoticeId(null);
        notice1.setNoticeTitle(noticeTitle);
        notice1.setNoticeContent(noticeContent);
        notice1.setNoticeAdminId(notice.getNoticeAdminId());
        boolean save = this.save(notice1);
        if (!save) {
            return R.error("新增公告失败");
        }
        return R.success(null, "新增公告成功");
    }

    @Override
    public R<String> deleteNoticeById(Integer noticeId) {
        boolean remove = this.removeById(noticeId);
        if (!remove) {
            return R.error("删除公告失败");
        }
        return R.success(null, "删除公告成功");
    }

    @Override
    public R<Notice> getNoticeByNoticeId(Integer noticeId) {
        Notice notice = this.getById(noticeId);
        if (notice == null) {
            return R.error("获取公告失败");
        }
        R<Notice> result = new R<>();
        result.setStatus(200);
        result.setMsg("获取公告信息成功");
        result.setData(notice);
        return result;
    }

    @Override
    public R<String> updateNoticeByNoticeId(Integer noticeId, Notice notice) {
        String noticeTitle = notice.getNoticeTitle();
        String noticeContent = notice.getNoticeContent();
        if (StringUtils.isBlank(noticeTitle) || StringUtils.isBlank(noticeContent)) {
            return R.error("修改公告失败");
        }
        Notice notice2 = this.getById(noticeId);
        if (notice2 == null) {
            return R.error("修改公告失败");
        }
        notice2.setNoticeTitle(noticeTitle);
        notice2.setNoticeContent(noticeContent);
        boolean update = this.updateById(notice2);
        if (!update) {
            return R.error("修改公告失败");
        }

        return R.success(null, "修改公告成功");
    }
}




