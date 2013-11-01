package com.github.gw2app.test;

import android.test.AndroidTestCase;

import com.github.gw2app.db.Message;

/**
 * Created by tidus on 28/10/13.
 */
public class MessageTests extends AndroidTestCase {

    public void testMessageConstructor(){
        Message msg = new Message("test message", "-123456789", "10/10/2012 10:12:12");
        assertEquals("test message", msg.getMessage());
        assertEquals("-123456789", msg.getUserID());
        assertEquals("10/10/2012 10:12:12", msg.getTimeStamp());
    }
}
