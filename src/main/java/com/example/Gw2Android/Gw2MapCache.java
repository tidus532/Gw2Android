package com.example.Gw2Android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by tidus on 16/07/13.
 */
public class Gw2MapCache {

    private static String fileName(Gw2Tile tile) {
        return tile.continent_id + "_" + tile.floor + "_" + tile.zoom + "_" + tile.worldCoord.x + "_" + tile.worldCoord.y+".jpg";
    }

    //TODO: pick external cache dir if available.
    public static void storeBitmap(Context context, Gw2Tile tile) {
        //Open cache dir and create new file for the tile.
        if (tile.getBitmap() != null) {
            File cacheDir = context.getCacheDir();
            File tileImage = new File(cacheDir, fileName(tile));
            try {
                FileOutputStream fOut = new FileOutputStream(tileImage);
                tile.getBitmap().compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (FileNotFoundException e) {
                Log.e("Gw2", "Could not save image, file not found");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static boolean getBitmap(Context context, Gw2Tile tile) {
        File cacheDir = context.getCacheDir();
        File tileImage = new File(cacheDir, fileName(tile));
        if(tileImage.exists()){
            try {
                FileInputStream fIn = new FileInputStream(tileImage);
                tile.setBitmap(BitmapFactory.decodeStream(fIn));
                if(tile.getBitmap() == null){
                    Log.e("Gw2", "Something went wrong loading the image");
                    return false;
                }
            } catch (FileNotFoundException e) {
                Log.e("Gw2", "Cannot load image, file not found.");
            }
            return true;
        } else {
            return false;
        }
    }
}
