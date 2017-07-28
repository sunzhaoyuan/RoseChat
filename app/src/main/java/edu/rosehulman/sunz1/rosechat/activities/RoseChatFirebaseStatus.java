package edu.rosehulman.sunz1.rosechat.activities;

import android.app.Application;

/**
 * Created by sun on 7/28/17.
 *
 * @author sun
 * @thanks delaroy
 *
 * @note this is not the ultimate solution for the scenario
 */

public class RoseChatFirebaseStatus extends Application{
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        RoseChatFirebaseStatus.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
