package com.example.Gw2AndroidTest;

import android.app.Activity;
import android.os.Bundle;

import com.example.Gw2Android.Gw2ApiEvents;

/**
 * Created by tidus on 29/05/13.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gw2ApiEvents test = new Gw2ApiEvents(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
