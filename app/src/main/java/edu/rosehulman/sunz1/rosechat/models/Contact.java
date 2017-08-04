package edu.rosehulman.sunz1.rosechat.models;

import java.util.HashMap;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class Contact {


    String name;
    String profilePicUrl;
    HashMap<String, Boolean> friends;
    String uid;
    int phoneNumber;
    String email;


    public Contact(String temp, String pictureURL) {
        name = temp;
        profilePicUrl = pictureURL;
    }

    public HashMap<String, Boolean> getFriends() {
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

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        profilePicUrl = profilePicUrl;
    }
}
