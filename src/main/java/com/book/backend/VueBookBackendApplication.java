package com.book.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 赵天宇
 */
@SpringBootApplication
@MapperScan("com.book.backend.mapper")
@EnableTransactionManagement
public class VueBookBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VueBookBackendApplication.class, args);
    }

}
