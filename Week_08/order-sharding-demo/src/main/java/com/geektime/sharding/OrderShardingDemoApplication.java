package com.geektime.sharding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.geektime.sharding.repository")
public class OrderShardingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderShardingDemoApplication.class, args);
    }

}
