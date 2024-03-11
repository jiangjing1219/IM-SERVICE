package com.jiangjing.im;

import com.jiangjing.im.message.service.StoreMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ImMessageStoreApplicationTests {

    @Autowired
    StoreMessageService storeMessageService;

    @Test
    void contextLoads() {

    }
}
