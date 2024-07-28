package com.jiangjing.im.service.message.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

@Service
/**
 * 调用通义千文接口
 */
public class AgentChatService {

    public void streamCallWithCallback(Message userMsg, Consumer<GenerationResult> onEvent, Consumer<StringBuilder> onComplete)
            throws NoApiKeyException, ApiException, InputRequiredException, InterruptedException {
        Generation gen = new Generation();
        GenerationParam param = GenerationParam.builder()
                .model("qwen-max")
                // //set result format message
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                // set messages
                .messages(Collections.singletonList(userMsg))
                .topP(0.8)
                .apiKey("sk-ce8a909da6e74b74809d7f005d46ddab")
                // set streaming output incrementally
                .incrementalOutput(true)
                .build();
        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();
        gen.streamCall(param, new ResultCallback<GenerationResult>() {
            @Override
            public void onEvent(GenerationResult message) {
                /* 获取到的消息内容 */
                fullContent.append(message.getOutput().getChoices().get(0).getMessage().getContent());
                System.out.println(message.getOutput().getChoices().get(0).getMessage().getContent());
                onEvent.accept(message);
            }

            @Override
            public void onError(Exception err) {
                /* 回复异常需要回复 */
                onComplete.accept(fullContent);
                semaphore.release();
            }

            @Override
            public void onComplete() {
                /* 回复完成，需要发送回复完成的标识 */
                onComplete.accept(fullContent);
                semaphore.release();
            }

        });
        semaphore.acquire();
    }
}
