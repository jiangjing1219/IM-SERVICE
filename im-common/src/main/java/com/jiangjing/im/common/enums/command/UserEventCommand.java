package com.jiangjing.im.common.enums.command;

/**
 * @author Admin
 */

public enum UserEventCommand implements Command {

    //用户修改command 4000
    USER_MODIFY(4000),

    // 4001 用户现在状态变更——TCP发送给service逻辑层
    USER_ONLINE_STATUS_CHANGE(4001),

    // 4003 用户自定义在线状态通知报文——发送给订阅当前用户在线状态的其他用户（service - TCP）
    USER_CUSTOM_STATUS_CHANGE_NOTIFY(4003),

    // 4004 用户在线状态通知报文——发送给订阅当前用户在线状态的其他用户（service - TCP）
    USER_ONLINE_STATUS_CHANGE_NOTIFY(4004),

    // 4005 用户在线状态通知同步报文——发送给当前用户的其他在线端（service - TCP）
    USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC(4005),


    ;

    private int command;

    UserEventCommand(int command) {
        this.command = command;
    }


    @Override
    public int getCommand() {
        return command;
    }
}
