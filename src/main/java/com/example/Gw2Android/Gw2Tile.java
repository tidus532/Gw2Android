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
package com.example.Gw2Android;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by tidus on 3/07/13.
 */
public class Gw2Tile {
    public int continent_id;
    public int floor;
    public int zoom;
    public Gw2Point worldCoord = null;
    public RectF screenRect = null;
    private Bitmap mBitmap = null;
    private final ReentrantLock bitmapLock = new ReentrantLock();

    public Gw2Tile() {
        this.screenRect = new RectF();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap tile) {
        mBitmap = tile;
    }

    public void set(int continent_id, int floor, int zoom, Gw2Point worldCoord) {
        this.continent_id = continent_id;
        this.floor = floor;
        if (worldCoord == null) {
            Log.d("Gw2", "Gw2Tile::set worldCoord is null");
        }
        this.worldCoord = worldCoord;
        this.zoom = zoom;
    }
}
