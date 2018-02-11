package edu.rosehulman.sunz1.rosechat.models;

/**
 * Created by liy12 on 2/10/2018.
 */
public class ChatRoom {
    private String name;
    private int CID;

    public ChatRoom(String name, int CID) {
        this.name = name;
        this.CID = CID;
    }

    public String getName() {
        return name;
    }

    public int getCID() {
        return CID;
    }
}
