package com.github.gw2app.test;

import android.test.AndroidTestCase;

import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.mockito.Mockito;

/**
 * Created by tidus on 28/10/13.
 */
public class Gw2ChatServiceTests extends AndroidTestCase {

    public void testSimpleMockitoStubs(){
        AndroidConnectionConfiguration connMock = Mockito.mock(AndroidConnectionConfiguration.class);
    }
}
