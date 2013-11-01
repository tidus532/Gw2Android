package com.github.gw2app.db;

/**
 * Created by tidus on 7/10/13.
 */
public class Message {
    private String mMessage = null;
    private String mUserID = null;
    private String mTimestamp = null;

    public Message(String message, String userID, String timestamp){
        mMessage = message;
        mUserID = userID;
        mTimestamp = timestamp;
    }

    public String getMessage(){
        return mMessage;
    }

    public String getUserID(){
        return mUserID;
    }

    public String getTimeStamp(){
        return mTimestamp;
    }
}
