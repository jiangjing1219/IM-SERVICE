package com.jiangjing.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.jiangjing.im.message")
public class ImMessageStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImMessageStoreApplication.class, args);
    }

}
