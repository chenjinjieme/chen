package com.chen.aigis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.chen.aigis")
@MapperScan("com.chen.aigis.dao")
public class AigisApplication {
    public static void main(String[] args) {
        SpringApplication.run(AigisApplication.class, args);
    }
}
