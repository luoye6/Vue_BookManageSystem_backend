package com.book.backend.job.cycle;

import com.book.backend.pojo.Comment;
import com.book.backend.pojo.dto.CommentDTO;
import com.book.backend.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
// todo 取消 @Component 注释开启任务
// 程序启动时执行一次，然后每隔1天自动执行定时任务
//@Component
@EnableScheduling
@Slf4j
public class ConvertCommentListToRedis {

    @Resource
    private CommentService commentService;
    @Resource
    private RedisTemplate redisTemplate;

    public ConvertCommentListToRedis() {
    }

    /**
     *  每天凌晨三点将数据库中的留言列表转到 Redis
     *
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void run() {
        log.info("准备将数据库中的留言列表转移到Redis");
        LocalDate localDate = LocalDate.now();
        String key = "comment:"+localDate;
        List<Comment> list = commentService.list();
        List<CommentDTO> commentDTOList = list.stream().map((item) -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setAvatar(item.getCommentAvatar());
            commentDTO.setId(item.getCommentId());
            commentDTO.setMsg(item.getCommentMessage());
            commentDTO.setTime(item.getCommentTime());
            commentDTO.setBarrageStyle(item.getCommentBarrageStyle());
            return commentDTO;
        }).collect(Collectors.toList());
        redisTemplate.opsForList().rightPushAll(key,commentDTOList);
        redisTemplate.expire(key,7, TimeUnit.DAYS);
        log.info("留言列表成功转移到Redis");
    }
}
