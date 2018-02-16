package edu.rosehulman.sunz1.rosechat.models;

/**
 * Created by liy12 on 2/10/2018.
 */
public class ChatRoom {

    private String name; //chatroom name
    private Integer CID; //chatroom ID
    private String lastText;

    public ChatRoom(String name, int CID, String lastText) {
        this.name = name;
        this.CID = CID;
        this.lastText = lastText;
    }

    public String getName() {
        return name;
    }

    public int getCID() {
        return CID;
    }

    public String getLastText() {return lastText;}

}
