package edu.rosehulman.sunz1.rosechat.models;

/**
 * Created by sun on 2/12/18.
 */

public class Message {

    private Integer MID;
    private String text;
    private String SenderID;

    public Message(Integer MID, String text, String SenderID) {
        this.MID = MID;
        this.text = text;
        this.SenderID = SenderID;
    }

    public Integer getMID() {
        return MID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }
}
