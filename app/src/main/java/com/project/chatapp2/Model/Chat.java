package com.project.chatapp2.Model;

public class Chat {
    private String sender;
    private String receive;
    private String message;
    private boolean isSeen;
    private long timeSent; // إضافة هذا الحقل لتخزين الوقت

    public Chat() {
    }

    public Chat(String sender, String receive, String message,boolean isSeen) {
        this.sender = sender;
        this.receive = receive;
        this.message = message;
        this.isSeen = isSeen;
    }

    public Chat(String sender, String receive, String message, boolean isSeen, long timeSent) {
        this.sender = sender;
        this.receive = receive;
        this.message = message;
        this.isSeen = isSeen;
        this.timeSent = timeSent;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
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

    @Override
    public String toString() {
        return "Chat{" +
                "sender='" + sender + '\'' +
                ", receive='" + receive + '\'' +
                ", message='" + message + '\'' +
                ", isSeen=" + isSeen +
                ", timeSent=" + timeSent +
                '}';
    }
}
