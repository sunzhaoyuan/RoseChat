package edu.rosehulman.sunz1.rosechat.models;

import com.google.firebase.database.Exclude;

/**
 * Created by agarwaa on 21-Jul-17.
 *
 */

public class Contact {

    private String key;

    private String uid;
    private String nickName;
    private String profilePicUrl;
    private String phoneNumber;
    private String email;

    public Contact(){

    }


    public Contact(String uid, String nickName, String profilePicUrl, String phoneNumber, String email) {
        this.nickName = nickName;
        this.profilePicUrl = profilePicUrl;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
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

    @Override
    public String toString() {
        return "Contact{" +
                "key='" + key + '\'' +
                ", uid='" + uid + '\'' +
                ", nickName='" + nickName + '\'' +
                ", profilePicUrl='" + profilePicUrl + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
