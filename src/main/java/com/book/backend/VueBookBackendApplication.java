package com.book.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 程序员小白条
 */
// @SpringBootApplication(exclude = {RedisAutoConfiguration.class})
// todo 如需关闭 Redis，须添加 exclude 中的内容
@SpringBootApplication()
@MapperScan("com.book.backend.mapper")
@EnableTransactionManagement
public class VueBookBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VueBookBackendApplication.class, args);
    }

}
