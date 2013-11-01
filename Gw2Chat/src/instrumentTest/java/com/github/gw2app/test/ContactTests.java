package com.github.gw2app.test;

import android.test.AndroidTestCase;

import com.github.gw2app.db.Contact;

/**
 * Created by tidus on 27/10/13. TODO: test equals.
 */
public class ContactTests extends AndroidTestCase {

    public void testContactConstructorWithMsg(){
        Contact contact = new Contact("123456789", "The Tester", "Test");
        //Check state of contact
        assertEquals(false, contact.isSelected());
        assertEquals("123456789", contact.getUserID());
        assertEquals("The Tester", contact.getName());
        assertEquals("Test", contact.getLastMessage());
    }

    public void testContactConstructorWithoutMsg(){
        Contact contact = new Contact("123456789", "The Tester");
        //Check state of contact
        assertEquals(false, contact.isSelected());
        assertEquals("123456789", contact.getUserID());
        assertEquals("The Tester", contact.getName());
        assertEquals("", contact.getLastMessage());
    }

    public void testContactConstructorRosterEntry(){
        //TODO: find a way to construct a RosterEntry.
    }

    public void testContactSetters(){
        Contact contact = new Contact("123456789", "The Tester");
        //Check state of contact
        contact.setSelected(true);
        assertEquals(true, contact.isSelected());

        contact.setLastMessage("test");
        assertEquals("test", contact.getLastMessage());
    }
}
