package com.github.gw2app.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.gw2app.R;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

/**
 * Created by tidus on 4/09/13.
 */
public class Gw2ChatActivity extends Activity {
    public final static String INTENT_USER_ID = "JID";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        String userJID = getIntent().getStringExtra("JID");
        Log.d("Gw2", "Opening chat with user " + userJID);
        final ListView messageList = (ListView) findViewById(R.id.chatView);
        final ChatAdapter adapter = new ChatAdapter(this, messageList);

        //Fetch chat history


        /*ChatManager chatManager = Gw2ChatSession.getConnection().getChatManager();
        final Chat chat = chatManager.createChat(userJID, adapter);
        messageList.setAdapter(adapter);

        final Button button = (Button) findViewById(R.id.send);
        final EditText msg = (EditText) findViewById(R.id.message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Gw2", "Adding message");
                if (msg != null && !(msg.getText().equals("") || msg.getText().length() == 0)) {
                    adapter.addMessage(new ChatMessage(msg.getText().toString(), ChatMessage.MSG_RIGHT));
                    adapter.notifyDataSetChanged();
                    try {
                        chat.sendMessage(msg.getText().toString());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    msg.setText("");
                    messageList.smoothScrollToPosition(adapter.getCount() - 1);

                }
            }
        });*/
    }

    private class ChatMessage {
        public static final int MSG_LEFT = 0;
        public static final int MSG_RIGHT = 1;
        private String msg;
        private int type;

        public ChatMessage(String msg, int type) {
            this.msg = msg;
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        public String getMessage() {
            return this.msg;
        }

    }

    private class ChatAdapter extends BaseAdapter implements MessageListener {
        private ArrayList<ChatMessage> messageList = null;
        private Context mContext;
        private ListView mChatList;

        public ChatAdapter(Context context, ListView chatList) {
            super();
            this.messageList = new ArrayList<ChatMessage>();
            mContext = context;
            mChatList = chatList;
        }

        public void addMessage(ChatMessage msg) {
            messageList.add(msg);
        }

        @Override
        public int getCount() {
            return messageList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView text = new TextView(mContext);
            ChatMessage msg = messageList.get(i);
            text.setText(msg.getMessage());
            if (msg.getType() == ChatMessage.MSG_RIGHT) {
                text.setGravity(Gravity.RIGHT);
            }
            return text;
        }

        @Override
        public void processMessage(Chat chat, Message message) {
            Log.d("Gw2", "Received message "+message.getBody());
            addMessage(new ChatMessage(message.getBody(), ChatMessage.MSG_LEFT));
            mChatList.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }
}
