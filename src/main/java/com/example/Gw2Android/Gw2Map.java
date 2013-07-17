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
    private Integer mCanvasWidth;
    private Integer mCanvasHeight;
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
        mTilesX = (int) Math.ceil(mCanvasWidth / 256.0);
        mTilesY = (int) Math.ceil( mCanvasHeight / 256.0);

        Log.d("Gw2", "mTilesX "+ mTilesX);
        Log.d("Gw2", "mTilesY "+ mTilesY);

        //Determine initial zoom so that we fill the entire view.
        mCurrentZoom = (int) Math.ceil(Math.max(Math.log((double) mTilesX) / Math.log(2.0), Math.log((double) mTilesY) / Math.log(2.0)));

        Log.d("Gw2", "ZOOM: "+ mCurrentZoom);
        //Find center tile at current zoom level.
        int center = (int) Math.floor(Math.pow(2, mCurrentZoom) / 2);

        //Donwload tiles around the center tile.

        //Download
        // uneven number of tiles

        int downloadX = mTilesX;
        int downloadY = mTilesY;

        if((mTilesX%2)==0){
            downloadX++;
        }

        if((mTilesY%2)==0){
            downloadY++;
        }

        //Gw2Tile tiles[] = new Gw2Tile[downloadX*downloadY];

        int k = 0;
        int baseX = (int) Math.floor(downloadX/2);
        int baseY = (int) Math.floor(downloadY/2);
        for(int i = 0; i <= downloadX / 2; i++){
            for(int j = 0; j <= downloadY / 2; j++){
                Log.d("Gw2", "DOING SHIT");
                if(i==0 && j==0){
                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center,center), new Gw2Point(baseX,baseY), null));
                    k++;
                }

                if(i == 0 && j>0){
                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center,j+center), new Gw2Point(baseX,baseY+j), null));
                    k++;
                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center,-j+center), new Gw2Point(baseX,baseY-j), null));
                    k++;
                }

                if(i>0 && j ==0){
                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center+i,center), new Gw2Point(baseX+i,baseY), null));
                    k++;

                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center-i,center), new Gw2Point(baseX-i,baseY), null));
                    k++;
                }

                if(i>0 && j>0){
                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center+i,center+j), new Gw2Point(baseX+i,baseY+j), null));
                    k++;

                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center+i,center-j), new Gw2Point(baseX+i,baseY-j), null));
                    k++;

                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center-i,center+j), new Gw2Point(baseX-i,baseY+j), null));
                    k++;

                    tiles.add(new Gw2Tile(1,1, mCurrentZoom, new Gw2Point(center-i,center-j), new Gw2Point(baseX-i,baseY-j), null));
                    k++;
                }
            }
        }
        Log.d("Gw2","Test1 "+ downloadX/2);
        Log.d("Gw2","Test2 "+ downloadY/2);
        Log.d("Gw2","Allocated "+ downloadX*downloadY);
        Log.d("Gw2", "K: "+k);

        //Shuffle them, looks cooler. Then download them.
        Collections.shuffle(tiles);
        mTileProvider.execute((Gw2Tile[]) tiles.toArray(new Gw2Tile[tiles.size()]));
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
        mCanvasWidth = xNew;
        mCanvasHeight = yNew;
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
        //tiles.add(tile);
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
            mTranslateX = 0;
            mTranslateY = 0;
            tiles.addAll(Arrays.asList(download));
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

            //Find higher bounds.
            int xScreenHigherBound = 0;
            int yScreenHigherBound = 0;

            for(Gw2Tile tile : tiles){
                if(tile.screenCoord.x > xScreenHigherBound){
                    xScreenHigherBound = tile.screenCoord.x;
                }

                if(tile.screenCoord.y > yScreenHigherBound){
                    yScreenHigherBound = tile.screenCoord.y;
                }
            }

            boolean top = false;
            boolean bottom = false;
            boolean left = false;
            boolean right = false;
            for (Gw2Tile tile : tiles) {

                //Top
                if (tile.screenCoord.y == 0 && (tile.screenRect.top + mTranslateY > 0)) {
                    Log.d("Gw2", "Need to download a new top row");
                    top = true;
                }

                //Bottom
                if (tile.screenCoord.y == yScreenHigherBound && (tile.screenRect.bottom + mTranslateY <= mCanvasHeight)) {
                    Log.d("Gw2", "Need to download a new bottom row");
                    Log.d("Gw2", "canvas height: "+mCanvasHeight);
                    float temp = tile.screenRect.bottom + mTranslateY;
                    Log.d("Gw2", "bottom: "+temp);
                    bottom = true;
                }

                //Left
                if (tile.screenCoord.x == 0 && (tile.screenRect.left + mTranslateX > 0)) {
                    Log.d("Gw2", "Need to download a new left column");
                    left = true;
                }

                //Right
                if (tile.screenCoord.x == xScreenHigherBound && (tile.screenRect.right + mTranslateY  <= mCanvasWidth)){
                    Log.d("Gw2", "Need to download a new right column");
                    right = true;
                }

            }

            //TODO: bounds can change!!
            if(top){
                ArrayList<Gw2Tile> downloadList = new ArrayList<Gw2Tile>();

                //Increase screen y coordinate by one for all tiles.
                for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.y == 0 && (tile.screenRect.top + mTranslateY > 0) && tile.worldCoord.y-1 >= 0) {
                        downloadList.add(new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x, tile.worldCoord.y - 1), new Gw2Point(tile.screenCoord.x,0), null));
                    }

                    tile.screenCoord.y++;

                }
                yScreenHigherBound++;
                //Translate all the tiles so we don't get a sudden jump.
                mTranslateY = -256;
                this.tiles.addAll(downloadList);
                this.mTileProvider = new Gw2TileProvider(this);
                this.mTileProvider.execute(downloadList.toArray(new Gw2Tile[downloadList.size()]));
            }


            if(bottom){
                ArrayList<Gw2Tile> downloadList = new ArrayList<Gw2Tile>();

                for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.y == yScreenHigherBound &&  (tile.screenRect.bottom + mTranslateY <= mCanvasHeight) && tile.worldCoord.y+1 <= Math.pow(2,mCurrentZoom)) {
                        downloadList.add(new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x, tile.worldCoord.y + 1), new Gw2Point(tile.screenCoord.x,yScreenHigherBound+1), null));
                    }
                }

                //Translate all the tiles so we don't get a sudden jump.
                //mTranslateY = 256;
                yScreenHigherBound++;
                this.tiles.addAll(downloadList);
                this.mTileProvider = new Gw2TileProvider(this);
                this.mTileProvider.execute(downloadList.toArray(new Gw2Tile[downloadList.size()]));
            }

            if(left){
                ArrayList<Gw2Tile> downloadList = new ArrayList<Gw2Tile>();
                for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.x == 0 &&  (tile.screenRect.left + mTranslateX > 0) && tile.worldCoord.x-1 >= 0) {
                        downloadList.add(new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x-1, tile.worldCoord.y), new Gw2Point(0,tile.screenCoord.y), null));
                    }
                    tile.screenCoord.x++;
                }

                //Translate all the tiles so we don't get a sudden jump.
                xScreenHigherBound++;
                mTranslateX = -256;
                this.tiles.addAll(downloadList);
                this.mTileProvider = new Gw2TileProvider(this);
                this.mTileProvider.execute(downloadList.toArray(new Gw2Tile[downloadList.size()]));
            }

            if(right){
                ArrayList<Gw2Tile> downloadList = new ArrayList<Gw2Tile>();
                for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.x == xScreenHigherBound &&  (tile.screenRect.right + mTranslateX <= mCanvasWidth) && tile.worldCoord.x+1 <= Math.pow(2, mCurrentZoom)) {
                        downloadList.add(new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x+1, tile.worldCoord.y), new Gw2Point(xScreenHigherBound+1,tile.screenCoord.y), null));
                    }

                }

                //Translate all the tiles so we don't get a sudden jump.

                xScreenHigherBound++;
                this.tiles.addAll(downloadList);
                this.mTileProvider = new Gw2TileProvider(this);
                this.mTileProvider.execute(downloadList.toArray(new Gw2Tile[downloadList.size()]));
            }

            invalidate();

            lastTouchX = x;
            lastTouchY = y;

            mLastAction = MotionEvent.ACTION_MOVE;
        }
        return true;
    }
}
