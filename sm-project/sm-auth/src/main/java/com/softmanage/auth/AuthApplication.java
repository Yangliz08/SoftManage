package com.softmanage.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.softmanage.auth.mapper")
public class AuthApplication {
@ComponentScan(basePackages = {"com.softmanage.auth", "com.softmanage.common"})
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}

