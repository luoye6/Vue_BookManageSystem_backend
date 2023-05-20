package com.book.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.book.backend.common.BasePage;
import com.book.backend.common.R;
import com.book.backend.pojo.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 赵天宇
 * @description 针对表【t_notice】的数据库操作Service
 * @createDate 2023-02-05 16:14:03
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 查询公告信息
     *
     * @return R<List < Notice>>
     */
    R<List<Notice>> getNoticeList();

    /**
     * 获取公告列表
     *
     * @return R<Notice>
     */
    R<Page<Notice>> getNoticeList(BasePage basePage);

    /**
     * 添加公告
     *
     * @param notice 公告
     * @return R<String>
     */
    R<String> addNotice(Notice notice);

    /**
     * 删除公告根据指定的id
     *
     * @param noticeId 公告id
     * @return R
     */
    R<String> deleteNoticeById(Integer noticeId);

    /**
     * 根据指定id获取公告
     *
     * @param noticeId 公告id
     * @return R<Notice>
     */
    R<Notice> getNoticeByNoticeId(Integer noticeId);
    /**
     * 更新公告根据公告id
     *
     * @param noticeId 公告id
     * @param notice   公告
     * @return R
     */
    R<String> updateNoticeByNoticeId(Integer noticeId, Notice notice);
}
