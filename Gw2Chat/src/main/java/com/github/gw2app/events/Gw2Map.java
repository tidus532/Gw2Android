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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Created by tidus on 29/06/13.
 */
public class Gw2Map extends View implements Gw2ITileReceiver, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private Integer mCanvasWidth;
    private Integer mCanvasHeight;
    private int mTilesX;
    private int mTilesY;
    private int mCurrentZoom;
    private float mScale = 1;
    private VelocityTracker mVelocityTracker = null;
    private GestureDetectorCompat mGestureDetector;
    private float lastTouchX = 0;
    private float lastTouchY = 0;
    private int mLastAction = 0;
    private Gw2MapStructure mMap;

    public Gw2Map(Context context) {
        super(context);
        lastTouchY = 0;
        lastTouchX = 0;
        mGestureDetector = new GestureDetectorCompat(context, this);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);

        /*int action = event.getAction();

        if (action == MotionEvent.ACTION_UP && mLastAction == MotionEvent.ACTION_DOWN) {


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
            mMap.translate(x - lastTouchX, y - lastTouchY);

            invalidate();

            lastTouchX = x;
            lastTouchY = y;

            mLastAction = MotionEvent.ACTION_MOVE;
        }
        return true;*/
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        mMap.zoom(event.getX(), event.getY());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float distanceX, float distanceY) {
        mMap.translate(-distanceX, -distanceY);
        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        Log.d("Gw2", "Got a fling event");
        return true;
    }

    public class Gw2MapStructure implements Gw2ITileReceiver {
        private ArrayList<ArrayList<Gw2Tile>> mTiles;
        private Gw2TileProvider mTileProvider;
        private int mNumRows;
        private int mNumColumns;
        private int mNumRowsOrig;
        private int mNumColumnsOrig;
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
                    row.add(temp);
                }
                mTiles.add(row);
            }

            mNumRows = numRows;
            mNumColumns = numColumns;
            mNumRowsOrig = numRows;
            mNumColumnsOrig = numColumns;
            mCache = cache;
            mPaint = new Paint();
        }

        public Gw2Tile getTile(int x, int y) {
            return mTiles.get(y).get(x);
        }

        public ArrayList<Gw2Tile> getRow(int rowNumber) {
            return mTiles.get(rowNumber);
        }

        public int getNumRows() {
            return mNumRows;
        }

        public int getNumColumns() {
            return mNumColumns;
        }

        /**
         * Recalculates the exact position of all tiles.
         */
        private void updatePositions() {
            for (int i = 0; i < mTiles.size(); i++) {
                ArrayList<Gw2Tile> row = mTiles.get(i);
                for (int j = 0; j < row.size(); j++) {
                    Gw2Tile tile = row.get(j);
                    tile.screenRect.set((float) (j * 256 * mScale + mTranslateX), (float) (i * 256 * mScale + mTranslateY), (float) ((j + 1) * 256 * mScale + mTranslateX), (float) ((i + 1) * 256 * mScale + mTranslateY));
                }
            }
        }

        public void draw(Canvas canvas) {
            for (int i = 0; i < mTiles.size(); i++) {
                ArrayList<Gw2Tile> row = mTiles.get(i);
                for (int j = 0; j < row.size(); j++) {
                    Gw2Tile tile = row.get(j);
                    tile.screenRect.set((float) (j * 256 * mScale + mTranslateX), (float) (i * 256 * mScale + mTranslateY), (float) ((j + 1) * 256 * mScale + mTranslateX), (float) ((i + 1) * 256 * mScale + mTranslateY));
                    mPaint.setFilterBitmap(true);
                    if (tile.getBitmap() != null) {
                        canvas.drawBitmap(tile.getBitmap(), null, tile.screenRect, mPaint);
                    }
                }
            }
        }

        /**
         * Remove tiles that are currently not viewable by the user.
         */
        private void pruneTiles() {

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

            //We can start downloading if there was no previous thread or it has finished downloading.
            if (mTileProvider == null || mTileProvider.getStatus() == AsyncTask.Status.FINISHED) {
                mTileProvider = new Gw2TileProvider(this, getContext());
                mTileProvider.execute((Gw2Tile[]) downloadList.toArray(new Gw2Tile[downloadList.size()]));
            } else {
                try {
                    mTileProvider.get();
                } catch (InterruptedException e) {
                    //If it is interrupted we can start downloading.
                    return;
                } catch (CancellationException e) {
                    return;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                mTileProvider = new Gw2TileProvider(this, getContext());
                mTileProvider.execute((Gw2Tile[]) downloadList.toArray(new Gw2Tile[downloadList.size()]));

            }
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
        public void translate(float x, float y) {
            mTranslateX += x;
            mTranslateY += y;
            this.updatePositions();

            boolean download = false;

            Gw2Tile topLeftTile = getTile(0, 0);
            if (topLeftTile.screenRect.left > 0) {
                if (!(topLeftTile.worldCoord.x - 1 >= 0)) {
                    mTranslateX -= x;
                    this.updatePositions();
                } else {
                    Log.d("Gw2", "Need to download new left column");
                    //Insert new column at beginning
                    for (int i = 0; i < mNumRows; i++) {
                        Gw2Tile tile = new Gw2Tile();
                        mTiles.get(i).add(0, tile);
                        tile.set(1, 1, mCurrentZoom, new Gw2Point(topLeftTile.worldCoord.x - 1, topLeftTile.worldCoord.y + i));
                    }
                    mNumColumns++;
                    mTranslateX = -256;
                    this.updatePositions();
                    topLeftTile = getTile(0, 0);
                    download = true;
                }
            }

            if (topLeftTile.screenRect.top > 0) {
                if (!(topLeftTile.worldCoord.y - 1 >= 0)) {
                    mTranslateY -= y;
                    this.updatePositions();
                } else {
                    Log.d("Gw2", "Need to download new top row");

                    //Insert new row at beginning
                    ArrayList<Gw2Tile> row = new ArrayList<Gw2Tile>();
                    mTiles.add(0, row);

                    for (int i = 0; i < mNumColumns; i++) {
                        Gw2Tile tile = new Gw2Tile();
                        row.add(tile);
                        tile.set(1, 1, mCurrentZoom, new Gw2Point(topLeftTile.worldCoord.x + i, topLeftTile.worldCoord.y - 1));
                    }
                    mNumRows++;
                    mTranslateY = -256;

                    this.updatePositions();
                    download = true;
                }

            }

            Gw2Tile bottomRightTile = getTile(mNumColumns - 1, mNumRows - 1);
            if (bottomRightTile.screenRect.bottom <= mCanvasHeight) {

                if (!(bottomRightTile.worldCoord.y + 1 <= Math.pow(2, mCurrentZoom))) {
                    mTranslateY -= y;
                    this.updatePositions();
                } else {
                    Log.d("Gw2", "Need to download new bottom row");

                    //Insert new row at the end
                    ArrayList<Gw2Tile> row = new ArrayList<Gw2Tile>();
                    mTiles.add(row);
                    int worldX = getTile(0, mNumRows - 1).worldCoord.x;

                    for (int i = 0; i < mNumColumns; i++) {
                        Gw2Tile tile = new Gw2Tile();
                        row.add(tile);

                        tile.set(1, 1, mCurrentZoom, new Gw2Point(worldX + i, bottomRightTile.worldCoord.y + 1));
                    }
                    mNumRows++;
                    bottomRightTile = getTile(mNumColumns - 1, mNumRows - 1);

                    this.updatePositions();
                    download = true;
                }
            }


            // Gw2Tile bottomRightTile = getTile(mNumColumns - 1, mNumRows - 1);
            if (bottomRightTile.screenRect.right <= mCanvasWidth) {
                if (!(bottomRightTile.worldCoord.x + 1 <= Math.pow(2, mCurrentZoom))) {
                    mTranslateX -= x;
                    this.updatePositions();
                } else {
                    Log.d("Gw2", "Need to download new right column");
                    int worldY = getTile(mNumColumns - 1, 0).worldCoord.y;
                    //Insert new column the end
                    for (int i = 0; i < mNumRows; i++) {
                        Gw2Tile tile = new Gw2Tile();
                        mTiles.get(i).add(mTiles.get(i).size(), tile);
                        tile.set(1, 1, mCurrentZoom, new Gw2Point(bottomRightTile.worldCoord.x + 1, worldY + i));
                    }
                    mNumColumns++;
                    this.updatePositions();
                    download = true;
                }
            }

            if (download) {
                this.download();
            }
        }

        /**
         * Reset translation to zero.
         */
        public void resetTranslation() {
            mTranslateX = 0;
            mTranslateY = 0;
        }

        public void zoom(float screenX, float screenY) {
            if (mCurrentZoom + 1 <= 7) {
                //Cancel running download tasks.
                mTileProvider.cancel(true);

                //Detemine wich tile was clicked
                Gw2Point clickedTileScreenCoord = null;
                Gw2Point clickedTileWorldCoord = null;
                for (int i = 0; i < mTiles.size(); i++) {
                    ArrayList<Gw2Tile> row = mTiles.get(i);
                    for (int j = 0; j < row.size(); j++) {
                        if (row.get(j).screenRect.contains(screenX, screenY)) {
                            clickedTileScreenCoord = new Gw2Point(j, i);
                            clickedTileWorldCoord = new Gw2Point(row.get(j).worldCoord.x, row.get(j).worldCoord.y);
                        }
                    }
                }

                //Trim number of tiles on screen.
                mTiles.subList(mNumRowsOrig, mTiles.size()).clear();
                mTiles.trimToSize();
                for (int i = 0; i < mTiles.size(); i++) {
                    ArrayList<Gw2Tile> row = mTiles.get(i);
                    row.subList(mNumColumnsOrig, row.size()).clear();
                    row.trimToSize();
                }

                //Set all bitmaps to null for redownloading.
                for (ArrayList<Gw2Tile> rows : mTiles) {
                    for (Gw2Tile tile : rows) {
                        tile.setBitmap(null);
                        tile.zoom++;
                        tile.worldCoord.x = 0;
                        tile.worldCoord.y = 0;
                    }
                }

                mNumColumns = mNumColumnsOrig;
                mNumRows = mNumRowsOrig;

                //Get the tile closest to the actual center of the screen.
                int screenCenterTileX = (int) Math.floor((mNumColumns - 1) / 2);
                int screenCenterTileY = (int) Math.floor((mNumRows - 1) / 2);

                Gw2Tile clickedTile = getTile(clickedTileScreenCoord.x, clickedTileScreenCoord.y);

                if (clickedTileScreenCoord != null) {
                    //Each tile is split up in 4 tiles at the next zoom level, determine which tile needs to be centered.
                    float baseX = clickedTileScreenCoord.x * 256 + mTranslateX;
                    float baseY = clickedTileScreenCoord.y * 256 + mTranslateY;

                    int x1, x2, y1, y2;

                    if (clickedTileWorldCoord.x == 0) {
                        x1 = 0;
                        x2 = 1;
                    } else {
                        x1 = clickedTileWorldCoord.x * 2;
                        x2 = x1 + 1;
                    }

                    if (clickedTileWorldCoord.y == 0) {
                        y1 = 0;
                        y2 = 1;
                    } else {
                        y1 = clickedTileWorldCoord.y * 2;
                        y2 = y1 + 1;
                    }

                    if (screenX <= baseX + 128) {
                        if (screenY <= baseY + 128) {
                            //Tile 1.
                            clickedTile.worldCoord.x = x1;
                            clickedTile.worldCoord.y = y1;
                        } else {
                            //Tile 3.
                            clickedTile.worldCoord.x = x1;
                            clickedTile.worldCoord.y = y2;
                        }
                    } else {
                        if (screenY <= baseY + 128) {
                            //Tile2
                            clickedTile.worldCoord.x = x2;
                            clickedTile.worldCoord.y = y1;
                        } else {
                            //Tile3
                            clickedTile.worldCoord.x = x2;
                            clickedTile.worldCoord.y = y2;
                        }
                    }


                    mCurrentZoom++;
                    resetTranslation();

                    fillScreenAroundArea(new Rect(clickedTileScreenCoord.x,  clickedTileScreenCoord.y,clickedTileScreenCoord.x,  clickedTileScreenCoord.y));
                    mMap.download();

                    //Get the center tile in the actual center of the screen.
                    /*float realCenterX = mCanvasWidth / 2;
                    float realCenterY = mCanvasHeight / 2;
                    float pixelScreenCenterX = (screenCenterTileX * 256) + 128;
                    float pixelScreenCenterY = (screenCenterTileY * 256) + 128;

                    float translateX = realCenterX - pixelScreenCenterX;
                    float translateY = realCenterY - pixelScreenCenterY;
                    translate(translateX, translateY);*/


                } else {
                    Log.e("Gw2", "Gw2MapStructure::zoom unable to determine the clicked tile.");
                }
            }
        }

        public void center() {

        }
    }
}
