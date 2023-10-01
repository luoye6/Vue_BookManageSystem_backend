package com.book.backend.manager;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@Service
@Slf4j
public class GuavaRateLimiterManager {
    public boolean doRateLimit(Long userId) {
        // 每秒允许的最大访问速率为1个许可
        RateLimiter rateLimiter = RateLimiter.create(1);
            if (rateLimiter.tryAcquire()) {
                // 可以进行处理的代码，表示限流允许通过
                log.info("用户id: " + userId + "请求一个令牌成功");
                return true;
            } else {
                // 限流超过了速率限制的处理代码
               log.info("用户id: " + userId + "请求一个令牌失败");
               return false;
            }
    }
}
