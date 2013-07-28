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
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tidus on 29/06/13.
 */
public class Gw2Map extends View implements Gw2ITileReceiver {
    private Integer mCanvasWidth;
    private Integer mCanvasHeight;
    private int mTilesX;
    private int mTilesY;
    private int mCurrentZoom;
    private ArrayList<Gw2Tile> tiles;
    private Gw2TileProvider mTileProvider;
    private float mScale = 1;
    private float mTranslateX = 0;
    private float mTranslateY = 0;
    private VelocityTracker mVelocityTracker = null;
    private float lastTouchX = 0;
    private float lastTouchY = 0;
    private int mLastAction = 0;
    private Gw2MapStructure mMap;

    public Gw2Map(Context context) {
        super(context);
        this.mTileProvider = new Gw2TileProvider(this);
        this.tiles = new ArrayList<Gw2Tile>();
        lastTouchY = 0;
        lastTouchX = 0;
    }

    /**
     * Initializes the map.
     */
    private void initialize() {
        //Determine number of tiles in x and y direction.
        mTilesX = (int) Math.ceil(mCanvasWidth / 256.0);
        mTilesY = (int) Math.ceil(mCanvasHeight / 256.0);


        Log.d("Gw2", "mTilesX " + mTilesX);
        Log.d("Gw2", "mTilesY " + mTilesY);

        //Determine initial zoom so that we fill the entire view.
        mCurrentZoom = (int) Math.ceil(Math.max(Math.log((double) mTilesX) / Math.log(2.0), Math.log((double) mTilesY) / Math.log(2.0)));

        Log.d("Gw2", "ZOOM: " + mCurrentZoom);

        //Find center tile and download area around it.
        mMap = new Gw2MapStructure(mTilesY, mTilesX, 0);
        int screenCenterX = (int) Math.floor((mTilesX - 1) / 2);
        int screenCenterY = (int) Math.floor((mTilesY - 1) / 2);

        Log.d("Gw2", "screenCenterX " + screenCenterX);
        Log.d("Gw2", "screenCenterY " + screenCenterY);

        int worldCenter = (int) Math.floor(Math.pow(2, mCurrentZoom) / 2);

        Gw2Tile centerTile = mMap.getTile(screenCenterX, screenCenterY);
        centerTile.set(1, 1, mCurrentZoom, new Gw2Point(worldCenter, worldCenter));
        mMap.fillScreenAroundArea(new Rect(screenCenterX, screenCenterY, screenCenterX, screenCenterY));
        mMap.download();
    }

    protected Gw2Point[][] getNearestTilesAtPreviousZoomLevel(int x, int y) {
        return null;
    }

    /**
     * Returns the nearest tile coordinates a certain point on screen will have at the next zoomlevel.
     * Use this to determine which tiles to download when a user taps to zoom in.
     */
    protected Gw2Point[][] getNearestTilesAtNextZoomLevel(int x, int y) {
        int x1;
        int x2;
        int y1;
        int y2;

        if (x == 0) {
            x1 = 0;
            x2 = 1;
        } else {
            x1 = x * 2;
            x2 = x1 + 1;
        }

        if (y == 0) {
            y1 = 0;
            y2 = 1;
        } else {
            y1 = y * 2;
            y2 = y1 + 1;
        }

        Gw2Point[][] points = new Gw2Point[2][2];
        points[0][0] = new Gw2Point(x1, y1);
        points[0][1] = new Gw2Point(x2, y1);
        points[1][0] = new Gw2Point(x1, y2);
        points[1][1] = new Gw2Point(x2, y2);
        return points;
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        mCanvasWidth = xNew;
        mCanvasHeight = yNew;
        this.initialize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mMap.draw(canvas);
    }

    @Override
    public void receiveTile(Gw2Tile tile) {
        invalidate();
    }

    /**
     * Update exact positioning of all tiles.
     */
    protected void updatePosition() {
        if (tiles != null) {
            for (int i = 0; i < tiles.size(); i++) {
                Gw2Tile tile = tiles.get(i);
                tile.screenRect.set((float) (tile.screenCoord.x * 256 * mScale + mTranslateX), (float) (tile.screenCoord.y * 256 * mScale + mTranslateY), (float) ((tile.screenCoord.x + 1) * 256 * mScale + mTranslateX), (float) ((tile.screenCoord.y + 1) * 256 * mScale + mTranslateY));
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP && mLastAction == MotionEvent.ACTION_DOWN) {

            Gw2Point p = null;
            for (int i = 0; i < tiles.size(); i++) {
                if (tiles.get(i).screenRect.contains(event.getX(), event.getY())) {
                    p = tiles.get(i).worldCoord;
                }
            }
            Gw2Point[][] tileCoord = getNearestTilesAtNextZoomLevel(p.x, p.y);
            this.tiles.clear();
            Gw2Tile[] download = new Gw2Tile[4];

            int k = 0;
            for (int i = 0; i < tileCoord.length; i++) {
                for (int j = 0; j < tileCoord[i].length; j++) {
                    download[k] = new Gw2Tile(1, 1, mCurrentZoom + 1, new Gw2Point(tileCoord[i][j].x, tileCoord[i][j].y), new Gw2Point(j, i), null);
                    k++;
                }
            }

            mScale = 1;
            mCurrentZoom++;
            mTranslateX = 0;
            mTranslateY = 0;
            tiles.addAll(Arrays.asList(download));
            this.mTileProvider = new Gw2TileProvider(this);

            mTileProvider.execute(download);

            mLastAction = MotionEvent.ACTION_UP;
        }

        if (action == MotionEvent.ACTION_DOWN) {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            } else {
                mVelocityTracker.clear();
            }

            mVelocityTracker.addMovement(event);
            final float x = event.getX();
            final float y = event.getY();

            lastTouchX = x;
            lastTouchY = y;

            mLastAction = MotionEvent.ACTION_DOWN;
        }

        if (action == MotionEvent.ACTION_MOVE) {
            final float x = event.getX();
            final float y = event.getY();

            //mTranslateX += x - lastTouchX;
            //mTranslateY += y - lastTouchY;
            mMap.translate(x - lastTouchX, y - lastTouchY);

            //Check if we need to download tiles.
            //TODO: Prune unecessary tiles.

            //Find higher bounds.
            /*int xScreenHigherBound = 0;
            int yScreenHigherBound = 0;

            for (Gw2Tile tile : tiles) {
                if (tile.screenCoord.x > xScreenHigherBound) {
                    xScreenHigherBound = tile.screenCoord.x;
                }

                if (tile.screenCoord.y > yScreenHigherBound) {
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
                    float temp = tile.screenRect.bottom + mTranslateY;
                    bottom = true;
                }

                //Left
                if (tile.screenCoord.x == 0 && (tile.screenRect.left + mTranslateX > 0)) {
                    Log.d("Gw2", "Need to download a new left column");
                    left = true;
                }

                //Right
                if (tile.screenCoord.x == xScreenHigherBound && (tile.screenRect.right + mTranslateY <= mCanvasWidth)) {
                    Log.d("Gw2", "Need to download a new right column");
                    right = true;
                }

            }

            //TODO: new tiles do not have a correct screenRect yet. This causes some problems.
            ArrayList<Gw2Tile> downloadList = new ArrayList<Gw2Tile>();
            if (top) {


                //Increase screen y coordinate by one for all tiles.
                for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.y == 0 && (tile.screenRect.top + mTranslateY > 0) && tile.worldCoord.y - 1 >= 0) {
                        Gw2Tile temp = new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x, tile.worldCoord.y - 1), new Gw2Point(tile.screenCoord.x, 0), null);
                        downloadList.add(temp);
                        this.tiles.add(temp);
                    }

                    tile.screenCoord.y++;
                }
                yScreenHigherBound++;
                //Translate all the tiles so we don't get a sudden jump.
                mTranslateY = -256;
                updatePosition();
            }


            if (bottom) {
                for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.y == yScreenHigherBound && (tile.screenRect.bottom + mTranslateY <= mCanvasHeight) && tile.worldCoord.y + 1 <= Math.pow(2, mCurrentZoom)) {
                        Gw2Tile temp = new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x, tile.worldCoord.y + 1), new Gw2Point(tile.screenCoord.x, yScreenHigherBound + 1), null);
                        downloadList.add(temp);
                        this.tiles.add(temp);
                    }
                }

                yScreenHigherBound++;
                updatePosition();
            }

            if (left) {
                for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.x == 0 && (tile.screenRect.left + mTranslateX > 0) && tile.worldCoord.x - 1 >= 0) {
                        Gw2Tile temp = new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x - 1, tile.worldCoord.y), new Gw2Point(0, tile.screenCoord.y), null);
                        downloadList.add(temp);
                        this.tiles.add(temp);
                    }
                    tile.screenCoord.x++;
                }

                //Translate all the tiles so we don't get a sudden jump.
                xScreenHigherBound++;
                mTranslateX = -256;
                updatePosition();
            }

            if (right) {
                for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.x == xScreenHigherBound && (tile.screenRect.right + mTranslateX <= mCanvasWidth) && tile.worldCoord.x + 1 <= Math.pow(2, mCurrentZoom)) {
                        Gw2Tile temp = new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x + 1, tile.worldCoord.y), new Gw2Point(xScreenHigherBound + 1, tile.screenCoord.y), null);
                        downloadList.add(temp);
                    }

                }

                xScreenHigherBound++;
                updatePosition();
                this.tiles.addAll(downloadList);

            }

            if (downloadList.size() > 0) {
                this.mTileProvider = new Gw2TileProvider(this);
                this.mTileProvider.execute(downloadList.toArray(new Gw2Tile[downloadList.size()]));
            }*/

            invalidate();

            lastTouchX = x;
            lastTouchY = y;

            mLastAction = MotionEvent.ACTION_MOVE;
        }
        return true;
    }

    private class Gw2MapStructure implements Gw2ITileReceiver {
        private ArrayList<ArrayList<Gw2Tile>> mTiles;
        private Gw2TileProvider mTileProvider;
        private int mNumRows;
        private int mNumColumns;
        private int mCache;
        private float mTranslateX = 0;
        private float mTranslateY = 0;
        private Paint mPaint;

        public Gw2MapStructure(int numRows, int numColumns, int cache) {
            mTiles = new ArrayList<ArrayList<Gw2Tile>>(numRows);
            for (int i = 0; i < numRows; i++) {
                ArrayList<Gw2Tile> row = new ArrayList<Gw2Tile>(numColumns);
                for (int j = 0; j < numColumns; j++) {
                    Gw2Tile temp = new Gw2Tile();
                    temp.screenCoord = new Gw2Point(j, i);
                    row.add(temp);
                }
                mTiles.add(row);
            }

            mNumRows = numRows;
            mNumColumns = numColumns;
            mCache = cache;
            mPaint = new Paint();
        }

        public void addTile(int row, int column, Gw2Tile tile) {
            mTiles.get(row).add(column, tile);
        }

        public void addRow(int rowNumber, ArrayList<Gw2Tile> row) {

        }

        public Gw2Tile getTile(int x, int y) {
            return mTiles.get(y).get(x);
        }

        public ArrayList<Gw2Tile> getRow(int rowNumber) {
            return mTiles.get(rowNumber);
        }

        /**
         * Recalculates the exact position of all tiles.
         */
        private void updatePositions(){
            for (int i = 0; i < mTiles.size(); i++) {
                ArrayList<Gw2Tile> row = mTiles.get(i);
                for (int j = 0; j < row.size(); j++) {
                    Gw2Tile tile = row.get(j);
                    tile.screenRect.set((float) (tile.screenCoord.x * 256 * mScale + mTranslateX), (float) (tile.screenCoord.y * 256 * mScale + mTranslateY), (float) ((tile.screenCoord.x + 1) * 256 * mScale + mTranslateX), (float) ((tile.screenCoord.y + 1) * 256 * mScale + mTranslateY));
                }
            }
        }

        public void draw(Canvas canvas) {
            for (int i = 0; i < mTiles.size(); i++) {
                ArrayList<Gw2Tile> row = mTiles.get(i);
                for (int j = 0; j < row.size(); j++) {
                    Gw2Tile tile = row.get(j);
                    tile.screenRect.set((float) (tile.screenCoord.x * 256 * mScale + mTranslateX), (float) (tile.screenCoord.y * 256 * mScale + mTranslateY), (float) ((tile.screenCoord.x + 1) * 256 * mScale + mTranslateX), (float) ((tile.screenCoord.y + 1) * 256 * mScale + mTranslateY));
                    mPaint.setFilterBitmap(true);
                    if (tile.bitmap != null) {
                        canvas.drawBitmap(tile.bitmap, null, tile.screenRect, mPaint);
                    }
                }
            }
        }

        /**
         * Remove tiles that are currently not viewable by the user.
         */
        public void pruneTiles() {

        }

        /**
         * Downloads all the tiles around an area to fill the screen.
         */
        public void fillScreenAroundArea(Rect area) {
            //TODO: what about translation.
            //TODO: avoid creating new objects.
            Gw2Tile topLeft = getTile(area.left, area.top);
            int worldX = topLeft.worldCoord.x - area.left;
            int worldY = topLeft.worldCoord.y - area.top;

            for (int i = 0; i < mNumRows; i++) {
                for (int j = 0; j < mNumColumns; j++) {
                    if (!area.contains(j, i)) {
                        Gw2Tile tile = getTile(j, i);
                        tile.set(1, 1, mCurrentZoom, new Gw2Point(worldX + j, worldY + i));
                    }
                }
            }
        }

        /**
         * Downloads tiles.
         */
        public void download() {
            mTileProvider = new Gw2TileProvider(this);
            ArrayList<Gw2Tile> downloadList = new ArrayList<Gw2Tile>();

            for (int i = 0; i < mTiles.size(); i++) {
                ArrayList<Gw2Tile> row = mTiles.get(i);
                for (int j = 0; j < row.size(); j++) {
                    Gw2Tile tile = row.get(j);
                    if (tile.worldCoord == null) {
                        Log.d("Gw2", "Gw2MapStructure::download() worldCoord is null");
                    }
                }
            }

            for (int i = 0; i < mTiles.size(); i++) {
                downloadList.addAll(mTiles.get(i));
            }
            mTileProvider.execute((Gw2Tile[]) downloadList.toArray(new Gw2Tile[downloadList.size()]));
        }

        @Override
        public void receiveTile(Gw2Tile tile) {
            invalidate();
        }

        /**
         * Translates the map by the given amount. This function will add to the already present translation.
         * New tiles will be automatically downloaded to fill the screen.
         *
         * @param x
         * @param y
         */
        public void translate(float x, float y){
            mTranslateX += x;
            mTranslateY += y;
            this.updatePositions();

            Gw2Tile topLeftTile = getTile(0,0);
            if(topLeftTile.screenRect.left > 0){
                Log.d("Gw2", "Need to download new left column");

               /* for (Gw2Tile tile : tiles) {

                    if (tile.screenCoord.x == 0 && (tile.screenRect.left + mTranslateX > 0) && tile.worldCoord.x - 1 >= 0) {
                        Gw2Tile temp = new Gw2Tile(1, 1, mCurrentZoom, new Gw2Point(tile.worldCoord.x - 1, tile.worldCoord.y), new Gw2Point(0, tile.screenCoord.y), null);
                        downloadList.add(temp);
                        this.tiles.add(temp);
                    }
                    tile.screenCoord.x++;
                }

                //Translate all the tiles so we don't get a sudden jump.
                xScreenHigherBound++;
                mTranslateX = -256;
                updatePosition();*/

            }

            if(topLeftTile.screenRect.top > 0){
                Log.d("Gw2", "Need to download new top row");
            }

            Gw2Tile bottomRightTile = getTile(mNumColumns-1, mNumRows-1);
            if(bottomRightTile.screenRect.bottom <= mCanvasHeight){
                Log.d("Gw2", "Need to download new bottom row");
            }

            if(bottomRightTile.screenRect.right <= mCanvasWidth){
                Log.d("Gw2", "Need to download new right column");
            }

        }

        /**
         * Reset translation to zero.
         */
        public void resetTranslation(){
            mTranslateX = 0;
            mTranslateY = 0;
        }
    }
}
