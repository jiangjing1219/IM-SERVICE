package com.example.imtcp.rabbitmq.process;

/**
 * 获取消息接收处理类的相应实例
 * @author Admin
 */
public class ProcessFactory {

    private static final BaseProcess DEFAULT_PROCESS;

    static {
        DEFAULT_PROCESS = new BaseProcess() {
            @Override
            public void processBefore() {

            }

            @Override
            public void processAfter() {

            }
        };
    }

    /**
     * 后续扩展可以根据 command 类型，配置相应的消息接收处理类
     *
     * @param command
     * @return
     */
    public static BaseProcess getMessageProcess(Integer command) {
        return DEFAULT_PROCESS;
    }
}
