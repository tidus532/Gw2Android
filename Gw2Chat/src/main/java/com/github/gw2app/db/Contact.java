package com.github.gw2app.db;

import org.jivesoftware.smack.RosterEntry;

/**
 *
 * Created by tidus on 30/09/13.
 */
public class Contact {
    private RosterEntry mRosterEntry = null;
    private boolean mSelected = false;
    private String mLastMessage = "";
    private String mUserID = null;
    private String mName = null;

    public Contact(String userID, String name, String lastMessage){
        mName = name;
        mUserID = userID;
        mLastMessage = lastMessage;
    }

    public Contact(String userID, String name) {
       this(userID, name, "");
    }

    public Contact(RosterEntry rosterEntry) {
        this(rosterEntry.getUser(), rosterEntry.getName(), "");
        mRosterEntry = rosterEntry;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public String getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(String lastMessage) {
        mLastMessage = lastMessage;
    }

    public String getUserID(){
        return mUserID;
    }

    public String getName(){
        return mName;
    }

    @Override
    public boolean equals(Object obj){
        if( ((Contact) (obj)).getUserID() == mUserID){
            return true;
        } else {
            return false;
        }
    }
}
