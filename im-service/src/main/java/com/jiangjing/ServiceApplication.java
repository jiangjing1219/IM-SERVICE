package com.jiangjing;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author Admin
 */

@SpringBootApplication
@EnableSwagger2WebMvc
@EnableDubbo(scanBasePackages = "com.jiangjing.im.service.message.service.dubbo")
public class ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

}
