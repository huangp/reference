package net.huangp.example.springbatch.domain;


public enum SendOrReceive {
    SEND, RECEIVE;

    public SendOrReceive fromValue(String name) {
        if (name.equalsIgnoreCase("deliver")) {
            return RECEIVE;
        } else {
            return SEND;
        }
    }
}
