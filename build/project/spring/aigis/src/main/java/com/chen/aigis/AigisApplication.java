package com.chen.aigis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chen.aigis.dao")
public class AigisApplication {
    public static void main(String[] args) {
        SpringApplication.run(AigisApplication.class, args);
    }
}
