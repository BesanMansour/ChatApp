package com.project.chatapp2.Model;

public class Chat {
    private String sender;
    private String receive;
    private String message;
    private boolean isSeen;

    public Chat() {
    }

    public Chat(String sender, String receive, String message,boolean isSeen) {
        this.sender = sender;
        this.receive = receive;
        this.message = message;
        this.isSeen = isSeen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
