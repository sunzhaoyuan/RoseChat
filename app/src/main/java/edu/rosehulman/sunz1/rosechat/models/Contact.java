package edu.rosehulman.sunz1.rosechat.models;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class Contact {


    String nickName;
    String ProfilePicUrl;
    String uid;
    int phoneNumber;

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

    String email;



    public Contact(String temp, String pictureURL) {
        nickName = temp;
        ProfilePicUrl = pictureURL;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        ProfilePicUrl = profilePicUrl;
    }
}
