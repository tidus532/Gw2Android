package com.example.Gw2Android;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by tidus on 16/07/13.
 */
public class Gw2MapCache {
    private Context mContext;

    public Gw2MapCache(Context context){
        this.mContext = context;
    }

    public Bitmap getBitmap(){
        return null;
    }

    //TODO: pick external cache dir if available.
    public void storeBitmap(Gw2Tile tile){
        File cacheDir = mContext.getCacheDir();

    }
}
