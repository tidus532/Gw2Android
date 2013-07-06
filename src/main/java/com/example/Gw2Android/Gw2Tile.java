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
    public Bitmap mTile;
    public int continent_id;
    public int floor;
    public int z;
    public int x;
    public int y;

    public int getContinentId() {
        return continent_id;
    }

    public void setContinentId(int continent_id) {
        this.continent_id = continent_id;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Gw2Tile(int continent_id, int floor, int z, int x, int y, Bitmap tile){
        mTile = tile;
        this.continent_id = continent_id;
        this.floor = floor;
        this.z = z;
        this.x = x;
        this.y = y;
    }

    public Gw2Tile(int continent_id, int floor, int z, int x, int y){
        this.continent_id = continent_id;
        this.floor = floor;
        this.z = z;
        this.x = x;
        this.y = y;
    }

    public Bitmap getBitmap(){
        return this.mTile;
    }

    public void setBitmap(Bitmap tile){
        mTile = tile;
    }
}
