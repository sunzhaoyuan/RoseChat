package edu.rosehulman.sunz1.rosechat.utils;

import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Created by sun on 7/21/17.
 *
 * Used for storing all constants that we will need in this project
 */

public class Constants {

    public static final String TAG = "RC";

    public static final String ARG_USERS = "users";
    public static final String ARG_RECEIVER = "receiver";
    public static final String ARG_RECEIVER_UID = "receiver_uid";
    public static final String ARG_CHAT = "chats";
    public static final String ARG_FIREBASE_TOKEN = "firebase_token";
    public static final String ARG_MESSAGE = "message";
    public static final String ARG_MESSAGE_KEY = "message_key";

    public static final int VIEW_PAGER_MESSAGE = 0;
    public static final int VIEW_PAGER_CONTACT = 1;
    public static final int VIEW_PAGER_PROFILE_VIEW = 2;
    public static final int VIEW_PAGER_PROFILE_EDIT = 4; //TODO: it should be 3. See line-196 in MainActivity.

    public static final String FAKE_USER = "sunz1";
    public static final List<String> FAKE_RECEIVER = singletonList("Abu");
}
