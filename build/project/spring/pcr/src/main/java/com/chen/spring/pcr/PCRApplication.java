package com.chen.spring.pcr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chen.spring.pcr.dao")
public class PCRApplication {
    public static void main(String[] args) {
        SpringApplication.run(PCRApplication.class, args);
    }
}
