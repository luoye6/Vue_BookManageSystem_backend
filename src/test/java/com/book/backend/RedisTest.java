package com.book.backend;

import cn.hutool.core.date.StopWatch;
import com.book.backend.common.R;
import com.book.backend.pojo.dto.CommentDTO;
import com.book.backend.service.CommentService;
import com.book.backend.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@SpringBootTest
class RedisTest {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CommentService commentService;
    @Test
    public void test1(){
        Boolean cat = redisTemplate.expire("comment:2023-11-26", 1, TimeUnit.MINUTES);
        System.out.println(cat);
    }
    @Test
    public void convertCommentListToRedis(){
        List<CommentDTO> range = (List<CommentDTO>) redisTemplate.opsForList().range("comment:2023-11-26", 0, -1);
        range.stream().forEach(System.out::println);
    }
    @Test
    public void getCommentListFromRedis(){
        LocalDate localDate = LocalDate.now();
        String key = "comment:"+localDate;
        R<List<CommentDTO>> commentList = commentService.getCommentList();
        List<CommentDTO> data = commentList.getData();
        redisTemplate.opsForList().rightPushAll(key,data);
    }
}
