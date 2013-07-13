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
import android.support.v4.view.GestureDetectorCompat;
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
    private int mTilesX;
    private int mTilesY;
    private int mCurrentZoom;
    private ArrayList<Gw2Tile> tiles;
    private Gw2TileProvider mTileProvider;
    private Paint mPaint;
    private float mScale = 1;
    private float mTranslateX = 0;
    private float mTranslateY = 0;
    private RectF mDstRect;
    private GestureDetectorCompat mDetector;
    private float lastTouchX = 0;
    private float lastTouchY = 0;
    private int mLastAction = 0;

    public Gw2Map(Context context) {
        super(context);
        this.mTileProvider = new Gw2TileProvider(this);
        this.tiles = new ArrayList<Gw2Tile>();
        mPaint = new Paint();
        mDstRect = new RectF();
        lastTouchY = 0;
        lastTouchX = 0;
    }

    /**
     * Initializes the map.
     */
    private void initialize(){
        //Determine number of tiles in x and y direction.
        mTilesX = (int) Math.ceil(mWidth / 256.0);
        mTilesY = (int) Math.ceil( mHeight / 256.0);

        Log.d("Gw2", "mTilesX "+ mTilesX);
        Log.d("Gw2", "mTilesY "+ mTilesY);

        //Determine initial zoom so that we fill the entire view.
        mCurrentZoom = (int) Math.ceil(Math.max(Math.log((double) mTilesX) / Math.log(2.0), Math.log((double) mTilesY) / Math.log(2.0)));

        Log.d("Gw2", "ZOOM: "+ mCurrentZoom);
        this.center();


    }

    /**
     * Calculates to which tile a pixel belongs. Takes into account the current zoom level and scale.
     */
    protected Gw2Point pixelToTile(float x, float y){
        //TODO: translating.
        float sizeOfTiles = 256 * mScale;
        int tileX = (int) Math.floor(x / sizeOfTiles);
        int tileY = (int) Math.floor(y / sizeOfTiles);
        int xLowerBound = Integer.MAX_VALUE;
        int yLowerBound = Integer.MAX_VALUE;

        for(int i = 0; i < tiles.size(); i++){
            if (tiles.get(i).worldCoord.x < xLowerBound){
                xLowerBound = tiles.get(i).worldCoord.x;
            }

            if (tiles.get(i).worldCoord.y < yLowerBound){
                yLowerBound = tiles.get(i).worldCoord.y;
            }

        }
         tileX += xLowerBound;
         tileY += yLowerBound;
        Log.d("Gw2", "Tile coord: ("+tileX+","+tileY+")");
        return new Gw2Point(tileX, tileY);
    }



    protected Gw2Point[][] getNearestTilesAtPreviousZoomLevel(int x, int y){
        return null;
    }

    /**
     * Returns the nearest tile coordinates a certain point on screen will have at the next zoomlevel.
     * Use this to determine which tiles to download when a user taps to zoom in.
     */
    protected Gw2Point[][] getNearestTilesAtNextZoomLevel(int x, int y){
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

        Gw2Point[][] points = new Gw2Point[2][2];
        points[0][0] = new Gw2Point(x1, y1);
        points[0][1] = new Gw2Point(x2, y1);
        points[1][0] = new Gw2Point(x1, y2);
        points[1][1] = new Gw2Point(x2, y2);

        Log.d("Gw2", "("+x1+","+y1+")");
        Log.d("Gw2", "("+x1+","+y2+")");
        Log.d("Gw2", "("+x2+","+y1+")");
        Log.d("Gw2", "("+x2+","+y2+")");

        return points;
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

            if(tiles != null){
                for(int i = 0; i < tiles.size(); i++){
                    Gw2Tile tile = tiles.get(i);
                    tile.screenRect.set( (float) (tile.screenCoord.x*256*mScale+mTranslateX), (float) (tile.screenCoord.y*256*mScale+mTranslateY), (float) ((tile.screenCoord.x+1)*256*mScale+mTranslateX), (float) ((tile.screenCoord.y+1)*256*mScale+mTranslateY));
                    mPaint.setFilterBitmap(true);
                    if(tile.bitmap == null){
                        Log.e("Gw2", "Tile ("+tile.screenCoord.x+","+tile.screenCoord.y+") bitmap was null");
                    } else {
                        canvas.drawBitmap(tile.bitmap, null, tile.screenRect, mPaint);
                    }
                }
            }
    }

    @Override
    public void receiveTile(Gw2Tile tile) {
        //Log.d("Gw2", "Received a fucking tile, F-F-UCK");
        tiles.add(tile);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();

        if(action == MotionEvent.ACTION_UP && mLastAction == MotionEvent.ACTION_DOWN){

            Gw2Point p = null;
            for(int i = 0; i < tiles.size(); i++){
                if(tiles.get(i).screenRect.contains(event.getX(), event.getY())){
                    p = tiles.get(i).worldCoord;
                }
            }
            Gw2Point[][] tileCoord = getNearestTilesAtNextZoomLevel(p.x, p.y);
            this.tiles.clear();
            Gw2Tile[] download = new Gw2Tile[4];

            int k = 0;
            for(int i = 0; i < tileCoord.length; i++){
                for(int j = 0; j < tileCoord[i].length; j++){
                    download[k] = new Gw2Tile(1, 1, mCurrentZoom +1, new Gw2Point(tileCoord[i][j].x, tileCoord[i][j].y), new Gw2Point(j,i), null);
                    Log.d("Gw2", "Added tile");
                    k++;
                }
            }

            mScale = 1;
            mCurrentZoom++;
            this.mTileProvider = new Gw2TileProvider(this);
            Log.d("Gw2", "ZOOM: "+ mCurrentZoom);

            mTileProvider.execute(download);

            mLastAction = MotionEvent.ACTION_UP;
        }

        if(action == MotionEvent.ACTION_DOWN){
            final float x = event.getX();
            final float y = event.getY();

            lastTouchX = x;
            lastTouchY = y;

            mLastAction = MotionEvent.ACTION_DOWN;
        }

        if(action == MotionEvent.ACTION_MOVE){
            final float x = event.getX();
            final float y = event.getY();

            mTranslateX += x - lastTouchX;
            mTranslateY += y - lastTouchY;

            //Check if we need to download tiles.

            //TODO: Check for all sides.
            //TODO: Prune unecessary tiles.
            for(int i = 0; i < tiles.size(); i++){
                Gw2Tile tile = tiles.get(i);


                if( tile.screenCoord.y == 0 && (tile.screenRect.top + mTranslateY > 0)){
                    Log.d("Gw2", "Need to download a new top row");

                }
            }

            invalidate();

            lastTouchX = x;
            lastTouchY = y;

            mLastAction = MotionEvent.ACTION_MOVE;
        }
        return true;
    }

    //Centers the map at the current zoom level.
    protected void center(){
        //Find center tile at current zoom level.
        int center = (int) Math.floor(Math.pow(2, mCurrentZoom) / 2);

        //Donwload tiles around the center tile.

        //Download uneven number of tiles
        int downloadX = mTilesX;
        int downloadY = mTilesY;

        if((mTilesX%2)==0){
            downloadX++;
        }

        if((mTilesY%2)==0){
            downloadY++;
        }

        Gw2Tile tiles[] = new Gw2Tile[downloadX*downloadY];

        int k = 0;
        int baseX = (int) Math.floor(downloadX/2);
        int baseY = (int) Math.floor(downloadY/2);
        for(int i = 0; i <= downloadX / 2; i++){
            for(int j = 0; j <= downloadY / 2; j++){
                Log.d("Gw2", "DOING SHIT");
                if(i==0 && j==0){
                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center,center), new Gw2Point(baseX,baseY), null);
                    k++;
                }

                if(i == 0 && j>0){
                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center,j+center), new Gw2Point(baseX,baseY+j), null);
                    k++;
                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center,-j+center), new Gw2Point(baseX,baseY-j), null);
                    k++;
                }

                if(i>0 && j ==0){
                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center+i,center), new Gw2Point(baseX+i,baseY), null);
                    k++;

                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center-i,center), new Gw2Point(baseX-i,baseY), null);
                    k++;
                }

                if(i>0 && j>0){
                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center+i,center+j), new Gw2Point(baseX+i,baseY+j), null);
                    k++;

                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center+i,center-j), new Gw2Point(baseX+i,baseY-j), null);
                    k++;

                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center-i,center+j), new Gw2Point(baseX-i,baseY+j), null);
                    k++;

                    tiles[k] = new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center-i,center-j), new Gw2Point(baseX-i,baseY-j), null);
                    k++;
                }
            }
        }
        Log.d("Gw2","Test1 "+ downloadX/2);
        Log.d("Gw2","Test2 "+ downloadY/2);
        Log.d("Gw2","Allocated "+ downloadX*downloadY);
        Log.d("Gw2", "K: "+k);

        //Shuffle them, looks cooler. Then download them.
        List<Gw2Tile> tempList = Arrays.asList(tiles);
        Collections.shuffle(tempList);
        mTileProvider.execute((Gw2Tile[]) tempList.toArray());
    }
}
