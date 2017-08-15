package edu.rosehulman.sunz1.rosechat.models;

import com.google.firebase.database.Exclude;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class Message {
    private String name;
    private String lastInteraction;
    private String pictureURL;
    private String senderUID;
    private String receiverUID;

    private String key;

    public Message() {

    }

    public Message(String name, String lastInteraction, String pictureURL, String senderUID, String receiverUID) {
        this.name = name;
        this.lastInteraction = lastInteraction;
        this.pictureURL = pictureURL;
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }

    public void setSenderUID(String senderUID) {

        this.senderUID = senderUID;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastInteraction() {
        return lastInteraction;
    }

    public void setLastInteraction(String lastInteraction) {
        this.lastInteraction = lastInteraction;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", lastInteraction='" + lastInteraction + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", senderUID='" + senderUID + '\'' +
                ", receiverUID='" + receiverUID + '\'' +
                '}';
    }
}
