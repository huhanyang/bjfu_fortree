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
@EnableJpaRepositories
@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
public class ForTreeApplication {

	public static void main(String[] args) {
		// todo 后端异常处理链路
		// todo 前端异常响应码拦截
		// todo 前端多边形excel导出接入
		// todo 前端部署时路由问题
		SpringApplication.run(ForTreeApplication.class, args);
	}

}
