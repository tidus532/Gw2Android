package com.github.gw2app;

import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.gw2app.events.Gw2StaticData;

/**
 * Created by tidus on 29/06/13.
 */
public class Gw2StaticDataProgression extends Gw2StaticData {
    private ProgressBar progressBar;
    private TextView progressText;
    private Context context;

    public Gw2StaticDataProgression(Context context, ProgressBar progressBar, TextView progressText) {
        super(context);
        this.context = context;
        this.progressBar = progressBar;
        this.progressText = progressText;
    }

    @Override
    public void onProgressUpdate(Integer... progress){
        this.progressText.setText(this.text);
        this.progressBar.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Void result){
        Intent intent = new Intent(context, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
