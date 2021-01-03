package com.bjfu.fortree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 启动类
 * @author warthog
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
public class ForTreeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForTreeApplication.class, args);
	}

}
