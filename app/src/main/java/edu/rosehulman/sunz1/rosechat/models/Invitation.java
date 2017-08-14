package edu.rosehulman.sunz1.rosechat.models;

import com.google.firebase.database.Exclude;

/**
 * Created by agarwaa on 05-Aug-17.
 */

public class Invitation {

    String mName;
    String mMessage;
    String mStatus;

    public Invitation(){

    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }


}
