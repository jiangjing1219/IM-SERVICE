package com.jiangjing.im.common.enums.command;

public enum ConversationEventCommand implements Command {

    //删除会话 5000
    CONVERSATION_DELETE(0x1388),

    //删除会话 5001
    CONVERSATION_UPDATE(0x1389),

    ;

    private int command;

    ConversationEventCommand(int command) {
        this.command = command;
    }


    @Override
    public int getCommand() {
        return command;
    }
}
