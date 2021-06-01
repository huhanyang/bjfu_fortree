package com.bjfu.fortree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启动类
 * @author warthog
 */
@EnableAsync
@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
public class ForTreeApplication {

	public static void main(String[] args) {
		// todo entity dto vo重构
		SpringApplication.run(ForTreeApplication.class, args);
	}

}
