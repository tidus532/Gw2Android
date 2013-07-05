package com.example.Gw2Android;

import android.graphics.Bitmap;

/**
 * Created by tidus on 3/07/13.
 */
public class Gw2Tile {
    private Bitmap mTile;
    private int continent_id;
    private int floor;
    private int z;
    private int x;
    private int y;

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
