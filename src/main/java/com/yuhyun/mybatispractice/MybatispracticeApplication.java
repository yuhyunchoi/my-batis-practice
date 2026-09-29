package com.yuhyun.mybatispractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MybatispracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatispracticeApplication.class, args);
	}

}
