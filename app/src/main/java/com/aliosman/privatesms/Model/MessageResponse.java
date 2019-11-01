/*
 * Copyright (c) 2019. Ali Osman OKTAR
 * aliosmanoktar@gmail.com
 */

package com.aliosman.privatesms.Model;

import java.io.Serializable;

public class MessageResponse implements Serializable {
    private int messageID;
    private long ThreadID;

    public MessageResponse() {

    }

    public MessageResponse(int messageID, long threadID) {
        this.messageID = messageID;
        ThreadID = threadID;
    }


    public int getMessageID() {
        return messageID;
    }

    public long getThreadID() {
        return ThreadID;
    }

    public MessageResponse setMessageID(int messageID) {
        this.messageID = messageID;
        return this;
    }

    public MessageResponse setThreadID(long threadID) {
        ThreadID = threadID;
        return this;
    }
}
