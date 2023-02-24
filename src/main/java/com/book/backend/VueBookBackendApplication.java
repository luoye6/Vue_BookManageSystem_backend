package com.book.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 赵天宇
 */
@SpringBootApplication
@MapperScan("com.book.backend.mapper")
public class VueBookBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VueBookBackendApplication.class, args);
    }

}
