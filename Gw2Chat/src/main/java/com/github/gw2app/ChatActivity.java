package com.github.gw2app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by tidus on 25/08/13.
 */
public class ChatActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        WebView web = new WebView(this);
        WebViewClient client = new AuthWebView();
        web.setWebViewClient(client);
        web.loadUrl("https://www.facebook.com/dialog/oauth?client_id=1374668296097328&redirect_uri=http%3A%2F%2Fgw2bb.app&response_type=token");
        setContentView(web);
    }

    private class AuthWebView extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){

            if(view != null && url != null){
                if(url.startsWith("http://gw2bb.app")){
                    url = url.replace("#", "?");
                    Log.d("Gw2", "url: "+url);
                    Uri uri = Uri.parse(url);
                    uri.getPath();
                    String token = uri.getQueryParameter("access_token");
                    Log.d("Gw2", "Access token: "+token);
                    Log.d("Gw2", "params = " + uri.getQueryParameterNames());
                    return true;
                }
            }
            return false;
        }
    }
}
