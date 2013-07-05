package com.example.Gw2Android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by tidus on 3/07/13.
 */
public class Gw2TileProvider extends AsyncTask<Gw2Tile[], Gw2Tile, Void> {
    private Gw2ITileReceiver mReceiver;

    public Gw2TileProvider(Gw2ITileReceiver receiver){
        super();
        mReceiver = receiver;
    }

    protected String constructURL(int continent_id, int floor, int z, int x, int y){
        return "https://tiles.guildwars2.com/" + continent_id + "/" + floor + "/" + z + "/" + x + "/" + y + ".jpg";
    }

    protected Gw2Tile downloadTile(Gw2Tile tile){

        try {
            String url = constructURL(tile.getContinentId(), tile.getFloor(), tile.getZ(),tile.getX(),tile.getY());
            Log.d("Gw2", url);
            InputStream is = new URL(url).openStream();
            tile.setBitmap(BitmapFactory.decodeStream(is));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(tile.getBitmap() == null){
           Log.d("Gw2", "Shits not lookin good bro");
        }

        return tile;
    }

    @Override
    protected Void doInBackground(Gw2Tile[]... tiles) {
        Log.d("Gw2", "url length "+tiles[0].length);
        for(Integer i = 0; i < tiles[0].length; i++){
            publishProgress(downloadTile(tiles[0][i]));
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Gw2Tile... tiles){
        mReceiver.receiveTile(tiles[0]);
    }
}
