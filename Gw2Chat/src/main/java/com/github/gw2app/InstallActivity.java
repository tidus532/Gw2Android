package com.github.gw2app;;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

/**
 * Created by tidus on 28/06/13.
 */
public class InstallActivity  extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.install_activity);
        //TODO: check if user is on wifi or not.

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        TextView progressText = (TextView) findViewById((R.id.progressText));
        Gw2StaticDataProgression staticDownloader = new Gw2StaticDataProgression(this, progressBar, progressText);
        staticDownloader.execute();
    }
}
