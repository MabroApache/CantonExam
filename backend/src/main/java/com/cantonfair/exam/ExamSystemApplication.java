package com.cantonfair.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 广交会在线考试系统启动类
 */
@SpringBootApplication
@MapperScan("com.cantonfair.exam.mapper")
public class ExamSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamSystemApplication.class, args);
        System.out.println("====================================");
        System.out.println("广交会在线考试系统启动成功！");
        System.out.println("====================================");
    }
}