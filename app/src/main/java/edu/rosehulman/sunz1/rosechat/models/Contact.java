package edu.rosehulman.sunz1.rosechat.models;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class Contact {


    String name;
    String ProfilePicUrl;

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

    String uid;
    int phoneNumber;
    String email;


    public Contact(String temp, String pictureURL) {
        name = temp;
        ProfilePicUrl = pictureURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        ProfilePicUrl = profilePicUrl;
    }
}
