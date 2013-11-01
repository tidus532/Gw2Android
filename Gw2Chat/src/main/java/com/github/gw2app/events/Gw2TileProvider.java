/*      This file is part of Gw2Android.

        Gw2Android is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        Gw2Android is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with Gw2Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package  com.github.gw2app.events;

import android.content.Context;
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
    private Context mContext;

    public Gw2TileProvider(Gw2ITileReceiver receiver, Context context) {
        super();
        mReceiver = receiver;
        mContext = context;
    }

    protected String constructURL(int continent_id, int floor, int z, int x, int y) {
        return "https://tiles.guildwars2.com/" + continent_id + "/" + floor + "/" + z + "/" + x + "/" + y + ".jpg";
    }

    protected Gw2Tile downloadTile(Gw2Tile tile) {
        try {
            if (tile.getBitmap() == null && tile.worldCoord != null) {
                String url = constructURL(tile.continent_id, tile.floor, tile.zoom, tile.worldCoord.x, tile.worldCoord.y);
                InputStream is = new URL(url).openStream();
                Bitmap image = BitmapFactory.decodeStream(is);
                if (isCancelled()) return null;
                tile.setBitmap(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tile;
    }

    @Override
    protected Void doInBackground(Gw2Tile[]... tiles) {
        Log.d("Gw2", "url length " + tiles[0].length);
        for (Integer i = 0; i < tiles[0].length; i++) {
            //Make sure the task hasn't been cancelled.
            if (isCancelled()) break;

            Gw2Tile tile = tiles[0][i];

            //Load from cache if possible else download it.
            if (Gw2MapCache.getBitmap(mContext, tile)) {
                publishProgress(tile);
            } else {
                //Make sure task is not cancelled before we start downloading.
                if (isCancelled()) break;
                tile = downloadTile(tile);
                Gw2MapCache.storeBitmap(mContext, tile);
                publishProgress(tile);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Gw2Tile... tiles) {
        mReceiver.receiveTile(tiles[0]);
    }
}
