package com.github.gw2app.chat.service;

import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by tidus on 29/10/13.
 */
public class AndroidConnectionConfigurationFactory {

    public AndroidConnectionConfiguration get(String server, int port) throws XMPPException{
        return new AndroidConnectionConfiguration(server, port);
    }

    public void setTestValue(AndroidConnectionConfiguration testValue){

    }
}
