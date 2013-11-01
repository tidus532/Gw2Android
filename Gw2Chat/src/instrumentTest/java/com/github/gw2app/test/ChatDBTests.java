package com.github.gw2app.test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.github.gw2app.db.Contact;
import com.github.gw2app.db.ChatDB;

import junit.framework.Assert;

import java.util.AbstractList;
import java.util.Map;

/**
 * Created by tidus on 26/10/13.
 */
public class ChatDBTests extends AndroidTestCase {
    private ChatDB mDB;
    private Context mContext;
    private static final String TEST_PREFIX = "test_";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), TEST_PREFIX);
        mContext = context;
        mDB = new ChatDB(context);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDB.cleanDatabase();
        mDB = null;
    }

    public void testPreConditions() {
        assertNotNull(mDB);
    }

    public void testDBIsEmpty() {
        //Check contact list
        AbstractList<Contact> contacts = mDB.getContacts();

        assertNotNull("Returned contact list is null where it should be an empty list", contacts);
        Assert.assertEquals("Returned contact list has a size greater than zero where it should be zero", 0, contacts.size());

        //Check chat names.
        Map<String, String> chatNames = mDB.getContactNames();

        assertNotNull("Returned chat names is null where it should be an empty map", chatNames);
        Assert.assertEquals("Returned chat names has a size greater than zero where it should be zero", 0, chatNames.size());
    }

    public void testAddContactSimple() {
        mDB.addContact("-123658465368", "Jos Vermeulen");
        Map<String, String> chatNames = mDB.getContactNames();
        AbstractList<Contact> contacts = mDB.getContacts();

        //Check the chatNames.
        assertNotNull("Returned chat names is null", chatNames);
        Assert.assertEquals("Returned chat names has a wrong size", 1, chatNames.size());
        Assert.assertEquals("Returned chat names has a wrong name", "Jos Vermeulen", chatNames.get("-123658465368"));

        //Check contacts.
        assertNotNull(contacts);
        assertEquals(1, contacts.size());
        Contact testContact = contacts.get(0);
        //Check if state is correct.
        assertEquals("Jos Vermeulen", testContact.getName());
        assertEquals("", testContact.getLastMessage());
        assertEquals(false, testContact.isSelected());
        assertEquals("-123658465368", testContact.getUserID());
    }

    public void testAddContactSimpleCache() {
        //Add chat name and check if it is correctly returned.
        mDB.addContact("-123658465368", "Jos Vermeulen");
        Map<String, String> chatNames = mDB.getContactNames();
        assertNotNull("Returned chat names is null", chatNames);
        Assert.assertEquals("Returned chat names has a wrong size", 1, chatNames.size());
        Assert.assertEquals("Returned chat names has a wrong name", "Jos Vermeulen", chatNames.get("-123658465368"));

        //Create new chat DB object to clear the cache.
        mDB = new ChatDB(mContext);
        chatNames = mDB.getContactNames();
        assertNotNull("Returned chat names is null", chatNames);
        Assert.assertEquals("Returned chat names has a wrong size", 1, chatNames.size());
        Assert.assertEquals("Returned chat names has a wrong name", "Jos Vermeulen", chatNames.get("-123658465368"));

    }

    public void testAddContactEscaped(){
        //Add chat name and check if it is correctly escaped and added to DB
        mDB.addContact("-123658465368", "J\' AND \'o\'s \"Vermeulen\"");
        Map<String, String> chatNames = mDB.getContactNames();
        assertNotNull("Returned chat names is null", chatNames);
        Assert.assertEquals("Returned chat names has a wrong size", 1, chatNames.size());
        Assert.assertEquals("Returned chat names has a wrong name", "J\' AND \'o\'s \"Vermeulen\"", chatNames.get("-123658465368"));
    }



}
