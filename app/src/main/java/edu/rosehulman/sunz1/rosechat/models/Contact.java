package edu.rosehulman.sunz1.rosechat.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by agarwaa on 21-Jul-17.
 *
 */

public class Contact {

    private String key;

    private String nickName;
    private String profilePicUrl;
    private Map<String, Boolean> friends;
    private String uid;
    private String phoneNumber;
    private String email;

    public Contact(){

    }


    public Contact(String uid, String nickName, String profilePicUrl, Map<String, Boolean> friends, String phoneNumber, String email) {
        this.nickName = nickName;
        this.profilePicUrl = profilePicUrl;
        this.friends = friends;
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, Boolean> friends) {
        this.friends = friends;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String name) {
        this.nickName = name;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        profilePicUrl = profilePicUrl;
    }

}
