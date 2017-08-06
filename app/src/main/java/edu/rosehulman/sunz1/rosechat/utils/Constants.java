package edu.rosehulman.sunz1.rosechat.utils;

import edu.rosehulman.sunz1.rosechat.activities.MainActivity;

/**
 * Created by sun on 7/21/17.
 *
 * Used for storing all constants that we will need in this project
 */

public class Constants {

    // For the Chat Feature
    public static final String ARG_USERS = "users";
    public static final String ARG_RECEIVER = "receiver";
    public static final String ARG_MESSAGE_NAME = "message_name";
    public static final String ARG_RECEIVER_UID = "receiver_uid";
    public static final String ARG_FIREBASE_TOKEN = "firebase_token";
    public static final String ARG_MESSAGE = "message";
    public static final String ARG_MESSAGE_KEY = "messageKey";

    // For the Profile Feature
    public static final String PROF_NICK_NAME = "prof_nick_name";
    public static final String PROF_EMAIL = "prof_email";
    public static final String PROF_PHONE = "prof_phone";

    // For the FireBase
    public static final String PATH_CHAT = "chats";
    public static final String PATH_CONTACT = "contacts";
    public static final String PATH_MESSAGE = "messages";
    public static final String PATH_INVITATION = "invitations";
    public static final String PATH_PROFILE_PIC = "profile_pics/";
    public static final String PATH_PP_DEFAULT = PATH_PROFILE_PIC + "default";

    // For the ViewPager Set Up - TODO: not in use ?
    public static final int VIEW_PAGER_MESSAGE = 0;
    public static final int VIEW_PAGER_CONTACT = 1;
    public static final int VIEW_PAGER_PROFILE_VIEW = 2;
    public static final int VIEW_PAGER_PROFILE_EDIT = 4; //TODO: it should be 3. See line-196 in MainActivity.

    // For the SharedPreferences
    public static final String PREFS = "PREFS";
    public static final String UID_KEY = "UID_KEY";

    // For TAGs
    public static final String TAG = MainActivity.class.getName();
    public static final String TAG_ASYNC = "async_test";
    public static final String TAG_CHAT_COMMUNICATOR = "chatsComm_test";
    public static final String TAG_CHAT = "chat_test";
    public static final String TAG_PROFILE = "profile_test";

}
