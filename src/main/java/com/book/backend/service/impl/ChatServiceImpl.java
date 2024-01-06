package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.Chat;
import com.book.backend.service.ChatService;
import com.book.backend.mapper.ChatMapper;
import org.springframework.stereotype.Service;

/**
* @author xiaobaitiao
* @description 针对表【t_chat】的数据库操作Service实现
* @createDate 2023-11-27 19:29:21
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService{

}




