package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.Notice;
import com.book.backend.service.NoticeService;
import com.book.backend.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_notice】的数据库操作Service实现
* @createDate 2023-02-05 16:14:03
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
    implements NoticeService{

}




