package edu.rosehulman.sunz1.rosechat.models;

/**
 * Created by liy12 on 2/10/2018.
 */
public class ChatRoom {

    private String name; //chatroom name
    private int CID; //chatroom ID

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
