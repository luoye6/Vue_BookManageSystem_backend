package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.Comment;
import com.book.backend.service.CommentService;
import com.book.backend.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_comment】的数据库操作Service实现
* @createDate 2023-02-06 19:19:20
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




