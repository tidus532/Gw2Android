package com.github.gw2app;

import android.app.Activity;
import android.os.Bundle;

import com.github.gw2app.events.Gw2Map;

;

/**
 * Created by tidus on 2/07/13.
 */
public class MapActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Gw2Map map = new Gw2Map(this);
        setContentView(map);



    }
}
