package com.jiangjing.im.service.utils;


import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的工具类
 *
 * @author jiangjing
 */
public class ThreadPoolExecutorUtils {

    /*
     *  本机程数
     */
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors() * 2;


    /**
     * 配置自定义线程池
     */
    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
            AVAILABLE_PROCESSORS,      // Core pool size
            AVAILABLE_PROCESSORS,      // Maximum pool size
            60,                       // Keep alive time
            TimeUnit.MILLISECONDS,    // Time unit for keep alive time
            new LinkedBlockingQueue<>(1000),  // Task queue
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
}
