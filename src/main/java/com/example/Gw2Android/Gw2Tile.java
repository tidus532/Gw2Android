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

/**
 * Created by tidus on 3/07/13.
 */
public class Gw2Tile {
    public Bitmap bitmap;
    public int continent_id;
    public int floor;
    public int zoom;
    Gw2Point tileCoord;
    Gw2Point screenCoord;

    public Gw2Tile(int continent_id, int floor, int zoom, Gw2Point tileCoord, Gw2Point screenCoord, Bitmap tile){
        bitmap = tile;
        this.continent_id = continent_id;
        this.floor = floor;
        this.tileCoord = tileCoord;
        this.screenCoord = screenCoord;
        this.zoom = zoom;
    }

    public Gw2Tile(int continent_id, int floor, int zoom, Gw2Point tileCoord, Gw2Point screenCoord){
        this.continent_id = continent_id;
        this.floor = floor;
        this.tileCoord = tileCoord;
        this.screenCoord = screenCoord;
        this.zoom = zoom;
    }
}
