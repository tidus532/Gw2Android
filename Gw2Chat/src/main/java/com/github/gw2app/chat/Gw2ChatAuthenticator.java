package com.github.gw2app.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Pieter De Maeyer on 19/08/13.
 *
 * Displays the login page to authenticate the user. Returns the token via an intent.
 * To receive the token this activity must be started via the startActivityWithResult() method call.
 */
public class Gw2ChatAuthenticator extends ActionBarActivity {
    private String token = null;

    /**
     *
     */
    public static final String GW2_AUTH_TOKEN_KEY = "access_token";
    public static final String GW2_AUTH_EXPIRES_KEY = "expires_in";
    public static final String GW2_AUTH_APP_ID = "app_id";
    public static final String GW2_AUTH_REDIRECT_URI = "redirect_uri";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView web = new WebView(this);
        WebViewClient client = new AuthWebView();
        web.setWebViewClient(client);
        web.loadUrl("https://www.facebook.com/dialog/oauth?client_id=1374668296097328&redirect_uri=http%3A%2F%2Fgw2bb.app&response_type=token&scope=xmpp_login");
        setContentView(web);
    }

    private class AuthWebView extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){

            if(view != null && url != null){
                if(url.startsWith("http://gw2bb.app")){
                    url = url.replace("#", "?");
                    Log.d("Gw2", "url: " + url);
                    Uri uri = Uri.parse(url);
                    uri.getPath();
                    token = uri.getQueryParameter("access_token");
                    Log.d("Gw2", "Access token: "+token);
                    //Log.d("Gw2", "params = " + uri.getQueryParameterNames()); //API11 call, cannot do this.

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("access_token", token);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    return true;
                }
            }
            return false;
        }
    }
}
