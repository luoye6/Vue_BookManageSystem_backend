package com.book.backend.service;

import com.book.backend.common.R;
import com.book.backend.pojo.AiIntelligent;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author xiaobaitiao
* @description 针对表【t_ai_intelligent】的数据库操作Service
* @createDate 2023-08-27 18:44:26
*/
public interface AiIntelligentService extends IService<AiIntelligent> {
        /**
         * 调用AI接口，获取推荐的图书信息字符串
         * @param aiIntelligent
         * @return
         */
        R<String> getGenResult(AiIntelligent aiIntelligent);

        /**
         * 根据用户ID 获取该用户和AI聊天的最近的五条消息
         * @param userId
         * @return
         */
        R<List<AiIntelligent>> getAiInformationByUserId(Long userId);
}
