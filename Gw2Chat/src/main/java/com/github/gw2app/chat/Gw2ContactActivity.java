package com.github.gw2app.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.gw2app.R;
import com.github.gw2app.db.Contact;
import com.github.gw2app.events.SettingsActivity;
import com.github.gw2app.chat.service.Gw2ChatService;

import com.github.gw2app.db.ChatDB;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by tidus on 25/08/13.
 */
public class Gw2ContactActivity extends ActionBarActivity {
    private String token = null;
    private boolean mBound = false;
    private Gw2ChatService.LocalBinder mBinder;
    private Gw2ChatService mService;
    private ContactsAdapter mContactAdapter;
    private ListView mContactListView;

    private void bindToService() {
        bindService(new Intent(this, Gw2ChatService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);
        Log.d("Gw2", "Oncreate called on thread " + Thread.currentThread());

        token = getIntent().getStringExtra(Gw2ChatAuthenticator.GW2_AUTH_TOKEN_KEY);
        if (token == null) {
            Log.e("Gw2", "No token is present in the intent.");
            finish();
        }

        //Setup UI.
        mContactListView = (ListView) findViewById(R.id.contactList);
        mContactAdapter = new ContactsAdapter(this, mContactListView);
        mContactListView.setAdapter(mContactAdapter);
        mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Launch Gw2ChatActivity with JID in the intent data.
                Intent chat = new Intent(Gw2ContactActivity.this, Gw2ChatActivity.class);
                Contact contact = (Contact) mContactAdapter.getItem(i);
                chat.putExtra(Gw2ChatActivity.INTENT_USER_ID, contact.getUserID());
                startActivity(chat);
            }
        });

        //Fetch contact information from DB
        ChatDB msgDB = new ChatDB(Gw2ContactActivity.this);
        AbstractList<Contact> contacts = msgDB.getContacts();
        for(Contact contact : contacts){
            mContactAdapter.addContact(contact);
        }

       /* AbstractCollection<com.example.Gw2Android.db.Message> lastMsg = msgDB.getLastMessages();
        Map<String, String> chatNames = msgDB.getContactNames();
        Log.d("Gw2", "CHATNAMES MAP" + chatNames);
        for (com.example.Gw2Android.db.Message msg : lastMsg) {
            Contact contact = new Contact(msg.getUserID(), chatNames.get(msg.getUserID()));
            Log.d("Gw2", "CONTACT USER ID" + msg.getUserID());
            Log.d("Gw2", "CONTACT NAME FROM DB " + chatNames.get(msg.getUserID()));
            contact.setLastMessage(msg.getMessage());
            mContactAdapter.addContact(contact);
        }*/

        Log.d("Gw2", "Starting GW2 Service...");
        //Start the GW2ChatService
        Intent chatService = new Intent(this, Gw2ChatService.class);
        chatService.putExtra(Gw2ChatService.INTENT_API_KEY, "1374668296097328");
        chatService.putExtra(Gw2ChatService.INTENT_ACCESS_TOKEN, token);
        startService(chatService);

        Log.d("Gw2", "Binding to Gw2 Service...");
        //Bind to startService
        bindToService();


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            mService.getRoster().removeRosterListener(mContactAdapter);
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        bindToService();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            openSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the settings activity.
     */
    private void openSettings() {
        Intent launchSettings = new Intent(this, SettingsActivity.class);
        startActivity(launchSettings);
    }

    /**
     * Process an incoming message packet.
     *
     * @param packet
     */
    private void processMessagePacket(Message packet) {
        String from = packet.getFrom();
        if (from != null) {
            Log.d("Gw2", "RECEIVED MSG FROM " + from);
            Contact fromEntry = mContactAdapter.getContactEntry(from);
            if (fromEntry != null) {
                fromEntry.setSelected(true);
                fromEntry.setLastMessage(packet.getBody());


                mContactListView.post(new Runnable() {
                    @Override
                    public void run() {
                        mContactAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                Log.e("Gw2", "Gw2ContactActivity::processMessagePacket could not find entry");
            }

        }
    }

    PacketFilter mFilter = new PacketTypeFilter(Message.class);
    PacketListener mMessageListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
            processMessagePacket((Message) packet);
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBound = true;
            mBinder = (Gw2ChatService.LocalBinder) iBinder;
            mService = mBinder.getService();

            //Perform our login
            if (!mService.isAuthenticated()) {
                mService.loginAsync();
            }

            //Register our roster listener.
            mService.getRoster().addRosterListener(mContactAdapter);

            //Register message listener.
            mService.addPacketListener(mMessageListener, mFilter);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private class ContactsAdapter extends BaseAdapter implements RosterListener {
        AbstractList<Contact> mContactList;
        Context mContext;
        Roster mRoster;
        ListView mContactListView;

        public ContactsAdapter(Context context, ListView contactList) {
            mContext = context;
            mContactList = new ArrayList<Contact>();
            mContactListView = contactList;
        }

        public void addContact(RosterEntry contact) {
            mContactList.add(new Contact(contact));
        }

        public void addContact(Contact contact) {
            mContactList.add(contact);
        }

        @Override
        public int getCount() {
            return mContactList.size();
        }

        @Override
        public Object getItem(int i) {
            return mContactList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public Contact getContactEntry(String user) {
            for (Contact entry : mContactList) {
                if (entry.getUserID().equals(user)) {
                    Log.d("Gw2", "FOUND ENTRY");
                    return entry;
                }
            }
            return null;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            //Get contact entry.
            Contact contact = mContactList.get(i);


            //Inflate row and fill in data. TODO: should reuse views instead of always inflating them.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.contact_row, parent, false);
            TextView contactName = (TextView) rowView.findViewById(R.id.contactName);
            TextView lastMessage = (TextView) rowView.findViewById(R.id.lastMessage);
            contactName.setText(contact.getName());
            lastMessage.setText(contact.getLastMessage());
            rowView.setSelected(contact.isSelected());

            return rowView;
        }

        @Override
        public void entriesAdded(Collection<String> addresses) {
            Log.d("Gw2", "Roster entries added on thread " + Thread.currentThread());
            //Get the roster. The service is bound because this class needs to be registered first.
            //This can only be done when the service is bound.
            if (mRoster == null) {
                mRoster = mService.getRoster();
            }
            for (String address : addresses) {
                Log.d("Gw2", "entriesAdded addr" + address);
                RosterEntry contact = mRoster.getEntry(address+ "@chat.facebook.com");
                Log.d("Gw2", "entriesAdded contact add" + contact.getUser());

                //
                // if (contact.getName().equals("Hilde Geelen") || contact.getName().equals("Mitchell R. Florentijn Snyders")) {
                Contact contactEntry = new Contact(contact);
                if (!mContactList.contains(contactEntry)) {
                    addContact(contact);
                    Log.d("Gw2", contact.getName());
                }
                //}
            }

            //This method is called from a non UI thread.
            //Must run notifyDataSetChanged on the UI thread.
            mContactListView.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

        }


        @Override
        public void entriesUpdated(Collection<String> addresses) {

        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {

        }

        @Override
        public void presenceChanged(Presence presence) {

        }
    }


}
