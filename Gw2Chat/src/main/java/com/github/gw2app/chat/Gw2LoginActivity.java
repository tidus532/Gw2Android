package com.github.gw2app.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.gw2app.R;

/**
 * Created by tidus on 20/09/13.
 */
public class Gw2LoginActivity extends ActionBarActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String token = data.getStringExtra("access_token");
            Log.d("Gw2", "received token: " + token);
            //Pass intent to Chat activity
            Intent intent = new Intent(this, Gw2ContactActivity.class);
            intent.putExtra(Gw2ChatAuthenticator.GW2_AUTH_TOKEN_KEY, token);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Gw2LoginActivity.this, Gw2ChatAuthenticator.class);
                startActivityForResult(intent, 1);
            }
        });
    }
}
