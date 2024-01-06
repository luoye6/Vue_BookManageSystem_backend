package com.book.backend.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.backend.common.exception.BusinessException;
import com.book.backend.common.exception.ErrorCode;
import com.book.backend.pojo.AiIntelligent;
import com.book.backend.pojo.UserInterfaceInfo;
import com.book.backend.pojo.Users;
import com.book.backend.service.AiIntelligentService;
import com.book.backend.service.UserInterfaceInfoService;
import com.book.backend.service.UsersService;
import com.book.backend.service.impl.UserInterfaceInfoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 删除用户调用AI模型的无效回复
 *
 * @author <a href="https://github.com/luoye6">程序员小白条</a>
 * 
 */
// todo 取消 @Component 注释开启任务
// 程序启动时执行一次，然后每隔1天自动执行定时任务
//@Component
@EnableScheduling
@Slf4j
public class IncSyncDeleteAIMessage {

    @Resource
    private AiIntelligentService aiIntelligentService;
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;


    /**
     * 每天执行一次
     *
     */
    @Scheduled(fixedRate = 60 * 60 * 24 * 1000)
    public void run() {
        log.info("正在查询无效的AI回复数据");
        // 查询无效的数据，并进行删除操作
        LambdaQueryWrapper<AiIntelligent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(AiIntelligent::getAiResult).or().eq(AiIntelligent::getAiResult, "");
        List<AiIntelligent> list = aiIntelligentService.list(queryWrapper);
        // 先为用户恢复次数
        log.info("正在为用户恢复次数");
        list.forEach(item -> {
            Long userId = item.getUserId();
            UserInterfaceInfo user = userInterfaceInfoService.getById(userId);
            synchronized (IncSyncDeleteAIMessage.class) {
                // 用户恢复次数
                if (user != null) {
                    user.setLeftNum(user.getLeftNum() + 1);
                    boolean save = userInterfaceInfoService.save(user);
                    if (!save) {
                        log.info("定时任务执行失败");
                        throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作定时任务失败");
                    }
                }
            }
        });
        // 将无效的记录删除
        log.info("正在删除无效的AI回复记录");
        boolean remove = false;
        try {
            remove = aiIntelligentService.remove(queryWrapper);
            if (!remove) {
                log.info("未发现无效的AI回复记录");
            }
        } catch (Exception e) {
            log.info("定时任务执行失败");
            throw new RuntimeException(e);
        }
        log.info("定时任务执行成功，删除无效的AI的结果集："+list.size()+"个");

    }
}
