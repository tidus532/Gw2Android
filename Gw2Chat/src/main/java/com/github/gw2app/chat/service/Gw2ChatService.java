package com.github.gw2app.chat.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.gw2app.R;
import com.github.gw2app.db.ChatDB;
import com.github.gw2app.db.Gw2DB;

import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.AndroidConnectionConfiguration;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;
import org.jivesoftware.smack.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.measite.smack.Sasl;

/**
 * Created by tidus on 7/09/13.
 */
public class Gw2ChatService extends Service {
    public final static String INTENT_API_KEY = "intent_api_key";
    public final static String INTENT_ACCESS_TOKEN = "intent_access_token";
    private final static String PREF_MSG_NOT = "pref_notification_enabled";
    private final IBinder mBinder = new LocalBinder();
    private AndroidConnectionConfiguration mXMPPConfig = null;
    private Connection mConnection = null;
    private String mApiKey;
    private String mToken;
    private boolean mHasClients = false;
    private boolean mMessageNotification;
    private Gw2DB mDBHelper;

    public class LocalBinder extends Binder {
        public Gw2ChatService getService() {
            return Gw2ChatService.this;
        }

    }

    private void setupMessageListener() {
        PacketFilter filter = new PacketTypeFilter(Message.class);
        PacketListener messageListener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                Log.d("Gw2", "Received packet from " + packet.getFrom() + " with message " + ((Message) packet).getBody());
                if (!mHasClients && mMessageNotification) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Gw2ChatService.this);
                    builder.setSmallIcon(R.drawable.gw2_logo_notification);
                    builder.setContentTitle(mConnection.getRoster().getEntry(packet.getFrom()).getName());
                    builder.setContentText(((Message) packet).getBody());

                    //Intent resultIntent = new Intent(this, Gw2ContactActivity.class);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify(100, builder.build());
                }

                //Add comment to DB.
                ChatDB chatDB = new ChatDB(Gw2ChatService.this);
                chatDB.addMessage(((Message) packet).getBody(), StringUtils.parseName(packet.getFrom()));

            }
        };

        RosterDBListener rosterListener = new RosterDBListener();
        getRoster().addRosterListener(rosterListener);
        mConnection.addPacketListener(messageListener, filter);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Load preferences.
        checkAllPreferences();

        //Initialize smack. Setup auth, connection and message listener.
        SmackAndroid.init(this);
        SASLAuthentication.registerSASLMechanism(SASLXFacebookPlatformMechanism.mName, SASLXFacebookPlatformMechanism.class);
        SASLAuthentication.supportSASLMechanism(SASLXFacebookPlatformMechanism.mName, 0);
        try {
            mXMPPConfig = new AndroidConnectionConfiguration("chat.facebook.com", 5222);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        mXMPPConfig.setSASLAuthenticationEnabled(true);
        //mXMPPConfig.setDebuggerEnabled(true);
        mConnection = new XMPPConnection(mXMPPConfig);
        setupMessageListener();

        //Setup DB.
        mDBHelper = Gw2DB.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mApiKey = intent.getStringExtra(INTENT_API_KEY);
            mToken = intent.getStringExtra(INTENT_ACCESS_TOKEN);
        }

        if (flags == START_FLAG_REDELIVERY) {
            loginAsync();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mHasClients = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mHasClients = false;
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        mHasClients = true;
    }

    public boolean isAuthenticated() {
        return mConnection.isAuthenticated();
    }

    public void loginAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mConnection.connect();
                    mConnection.login(mApiKey, mToken);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Roster getRoster() {
        return mConnection.getRoster();
    }

    public void addPacketListener(PacketListener packetListener, PacketFilter packetFilter) {
        mConnection.addPacketListener(packetListener, packetFilter);
    }

    public void removePacketListener(PacketListener packetListener) {
        mConnection.removePacketListener(packetListener);
    }

    public void sharedPreferencesChanged(SharedPreferences sharedPreferences, String key) {
        //Recheck all settings.
        Log.d("Gw2", "YEAH MR. WHITE! YEAH SCIENCE!");
        if (key.equals(PREF_MSG_NOT)) {
            mMessageNotification = sharedPreferences.getBoolean(key, true);

        }

    }

    /**
     * Checks the shared preferences to get the preferences of the user.
     */
    private void checkAllPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mMessageNotification = sharedPreferences.getBoolean(PREF_MSG_NOT, true);
    }

    public static class SASLXFacebookPlatformMechanism extends SASLMechanism {
        public final static String mName = "X-FACEBOOK-PLATFORM";
        private String apikey;
        private String token;

        public SASLXFacebookPlatformMechanism(SASLAuthentication saslAuth) {
            super(saslAuth);
        }

        @Override
        protected String getName() {
            return mName;
        }

        @Override
        protected void authenticate() {
            AuthMechanism stanza = new AuthMechanism(getName(), null);
            getSASLAuthentication().send(stanza);
        }

        @Override
        public void authenticate(String apikey, String host, String accessToken) {
            Log.d("Gw2", "SASLXFacebookPlatformMechanism:: authenticate(3)-1 called");
            this.apikey = apikey;
            this.token = accessToken;

            try {
                String[] mechanisms = {"DIGEST-MD5"};
                Map<String, String> props = new HashMap<String, String>();
                this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, this);
                authenticate();
            } catch (SaslException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void authenticate(String username, String host, CallbackHandler cbh) {
            Log.d("Gw2", "SASLXFacebookPlatformMechanism:: authenticate(3)-2 called");
        }

        @Override
        public void challengeReceived(String challenge) {
            Log.d("Gw2", "SASLXFacebookPlatformMechanism received challenge");

            //Decoding challenge.
            String decodedChallenge = new String(Base64.decode(challenge));
            Log.d("Gw2", decodedChallenge);

            Map<String, String> map = new HashMap<String, String>();
            String parameters[] = decodedChallenge.split("&");
            for (String param : parameters) {
                map.put(param.split("=")[0], param.split("=")[1]);
            }

            Log.d("Gw2", "nonce " + map.get("nonce"));
            Log.d("Gw2", "method " + map.get("method"));

            String response = null;
            try {
                response = String.format(
                        "method=%s&api_key=%s&access_token=%s&call_id=%s&v=%s&nonce=%s",
                        URLEncoder.encode(map.get("method"), "UTF-8"),
                        URLEncoder.encode(apikey, "UTF-8"),
                        URLEncoder.encode(token, "UTF-8"),
                        System.currentTimeMillis(),
                        URLEncoder.encode(map.get("version"), "UTF-8"),
                        URLEncoder.encode(map.get("nonce"), "UTF-8")
                );
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("Gw2", "response " + response);

            response = Base64.encodeBytes(response.getBytes(), Base64.DONT_BREAK_LINES);

            Log.d("Gw2", "response base64 " + response);
            Response authenticationText = new Response(response);
            getSASLAuthentication().send(authenticationText);
        }

    }

    private class RosterDBListener implements RosterListener{

        @Override
        public void entriesAdded(Collection<String> addresses) {
            //TODO: should do this with transactions.
            Log.d("Gw2", "USDKIOJMKDJSGNMKSDFJGHPIDSEUGNHPIRUGBNGIUFUCKINGSHIT");
            Roster roster = getRoster();
            for(String addr : addresses){
                RosterEntry rosterEntry = roster.getEntry(addr);
                ChatDB msgDB = new ChatDB(Gw2ChatService.this);
                Log.d("Gw2", "ADDING CHATNAME " +addr + " " + rosterEntry.getName());
                msgDB.addContact(addr, rosterEntry.getName());
            }
        }

        @Override
        public void entriesUpdated(Collection<String> strings) {

        }

        @Override
        public void entriesDeleted(Collection<String> strings) {

        }

        @Override
        public void presenceChanged(Presence presence) {

        }
    }
}
