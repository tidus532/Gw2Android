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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by tidus on 29/06/13.
 */
public class Gw2Map extends View implements Gw2ITileReceiver{
    private Integer mWidth;
    private Integer mHeight;
    private int tilesH;
    private int tilesV;
    private int currentZoom;
    private ArrayList<Gw2Tile> tiles;
    private Gw2TileProvider tileProvider;
    private Paint mPaint;
    private float mScale = 1;
    private RectF mDstRect;
    private boolean zoom = false;

    public Gw2Map(Context context) {
        super(context);
        this.tileProvider = new Gw2TileProvider(this);
        this.tiles = new ArrayList<Gw2Tile>();
        mPaint = new Paint();
        mDstRect = new RectF();
    }

    public void initialize(){
        determineZoomAndTiles();
        Gw2Tile tiles[] = new Gw2Tile[(int) (Math.pow(2, currentZoom)) * (int) (Math.pow(2, currentZoom))];
        int k = 0;
        for(int i = 0; i < (int) (Math.pow(2, currentZoom)); i++){
            for(int j = 0; j < (int) (Math.pow(2, currentZoom)); j++){
                tiles[k] = new Gw2Tile(1,1,currentZoom, i,j);
                k++;
            }
        }

        //Shuffle them, looks cooler.
        List<Gw2Tile> tempList = Arrays.asList(tiles);
        Collections.shuffle(tempList);
        tileProvider.execute((Gw2Tile[]) tempList.toArray());
    }

    /**
     * Calculates to which tile a pixel belongs. Takes into account the current zoom level and scale.
     */
    protected Gw2Point pixelToTile(float x, float y){
        float sizeOfTiles = 256 * mScale;
        int tileX = (int) Math.floor(x / sizeOfTiles);
        int tileY = (int) Math.floor(y / sizeOfTiles);
        Log.d("Gw2", "Tile coord: ("+tileX+","+tileY+")");
        return new Gw2Point(tileX, tileY);
    }

    /**
     * Returns the tile coordinates a tile while have in the next zoomlevel.
     */
    protected Gw2Point[] tileToNextZoomLevel(int x, int y){
        int x1;
        int x2;
        int y1;
        int y2;

        if(x == 0){
            x1 = 0;
            x2 = 1;
        } else {
            x1 = x *2;
            x2 = x1 +1;
        }

        if(y == 0){
            y1 = 0;
            y2 = 1;
        } else {
            y1 = y * 2;
            y2 = y1 +1;
        }
        Gw2Point[] points = new Gw2Point[] {new Gw2Point(x1, y1), new Gw2Point(x1, y2), new Gw2Point(x2, y1),new Gw2Point(x2, y2)};

        Log.d("Gw2", "("+x1+","+y1+")");
        Log.d("Gw2", "("+x1+","+y2+")");
        Log.d("Gw2", "("+x2+","+y1+")");
        Log.d("Gw2", "("+x2+","+y2+")");

        return points;
    }

    protected void determineZoomAndTiles(){
        double temp = mWidth / 256.0;
        tilesH = (int) Math.ceil(temp);
        Log.d("Gw2", "tilesH "+tilesH);
        temp = mHeight / 256.0;
        tilesV = (int) Math.ceil(temp);
        Log.d("Gw2", "tilesV "+tilesV);

        currentZoom = (int) Math.ceil(Math.max(Math.log((double) tilesH) / Math.log(2.0), Math.log((double) tilesV) / Math.log(2.0)));
        if(mWidth < mHeight){
            mScale = mWidth / (float) (Math.pow(2, currentZoom)*256);
        } else {
            mScale = mHeight / (float) (Math.pow(2, currentZoom)*256);
        }
        Log.d("Gw2", "ZOOM: "+currentZoom);

    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        mWidth = xNew;
        mHeight = yNew;
        this.initialize();
    }

    @Override
    protected void onDraw(Canvas canvas){
        if(zoom){
            canvas.scale((float) 1.5, (float) 1.5);
            zoom = false;
        }
        else {
            if(tiles != null){
                for(int i = 0; i < tiles.size(); i++){
                    Gw2Tile test = tiles.get(i);
                    mDstRect.set( (float) (test.getX()*256*mScale), (float) (test.getY()*256*mScale), (float) ((test.getX()+1)*256*mScale), (float) ((test.getY()+1)*256*mScale));
                    mPaint.setFilterBitmap(true);
                    canvas.drawBitmap(test.getBitmap(), null, mDstRect, mPaint);
                }
            }
        }
    }

    @Override
    public void receiveTile(Gw2Tile tile) {
        Log.d("Gw2", "Received a fucking tile, F-F-UCK");
        tiles.add(tile);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();

        if(action == MotionEvent.ACTION_UP){
            float x = event.getX();
            float y = event.getY();
            Gw2Point p = pixelToTile(x, y);
            Gw2Point[] tileCoord = tileToNextZoomLevel(p.x, p.y);
            this.tiles.clear();
            Gw2Tile[] download = new Gw2Tile[4];
            for(int i = 0; i < tileCoord.length; i++){
               download[i] = new Gw2Tile(1, 1, currentZoom+1, tileCoord[i].x, tileCoord[i].y, null);
            }
            mScale = 1;
            currentZoom++;
            this.tileProvider = new Gw2TileProvider(this);
            tileProvider.execute(download);
            //Zoom 50%
            /*float left = x / 2;
            float top = y / 2;
            float right = (mWidth - x) / 2;
            float bottom = (mHeight - y) / 2;
            mDstRect.set(left, top, right, bottom);
            zoom = true;*/
        }
        return true;
    }
}
