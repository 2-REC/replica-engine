/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.replica.engine;

import javax.microedition.khronos.opengles.GL10;

// !!!! TODO: remove duplicate stuff from TiledVertexGrid !!!!
// => need to change all the members visibility status.

/**
 * Handles moving grid patch.
 */
public class TiledVertexGridPatch extends TiledVertexGrid {

    private float mMovingSpeedX = 0.0f;
    private float mMovingSpeedY = 0.0f;
    private float mMoveOffsetX = 0.0f;
    private float mMoveOffsetY = 0.0f;


    public TiledVertexGridPatch(Texture texture,
            int width, int height, int tileWidth, int tileHeight,
            float widthSizeFactor, float heightSizeFactor, float movingSpeedX, float movingSpeedY) {
        super(texture, width, height, tileWidth, tileHeight, widthSizeFactor, heightSizeFactor);
        mMovingSpeedX = movingSpeedX * widthSizeFactor;
        mMovingSpeedY = movingSpeedY * heightSizeFactor;
    }

    @Override
    public void reset() {
    }

    @Override
    public void draw(float x, float y, float scrollOriginX, float scrollOriginY) {
        TiledWorld world = mWorld;
        GL10 gl = OpenGLSystem.getGL();
        if (!mGenerated && world != null && gl != null && mTexture != null) {
            final int tilesAcross = mWorld.getWidth();
            final int tilesDown = mWorld.getHeight();

            mWorldPixelWidth = mWorld.getWidth() * mTileWidth;
            mWorldPixelHeight = mWorld.getHeight() * mTileHeight;
            mTilesPerRow = tilesAcross;
            mTilesPerColumn = tilesDown;


            BufferLibrary bufferLibrary = sSystemRegistry.bufferLibrary;

            Grid grid = generateGrid((int)mWorldPixelWidth, (int)mWorldPixelHeight, 0, 0);
            mTileMap = grid;
            mGenerated = true;
            if (grid != null) {
                bufferLibrary.add(grid);
                if (sSystemRegistry.contextParameters.supportsVBOs) {
                    grid.generateHardwareBuffers(gl);
                }
            }

        }

        final Grid tileMap = mTileMap;
        if (tileMap != null) {
            final Texture texture = mTexture;
            if (gl != null && texture != null) {

//////// b
/*
                int originX = (int) (x - scrollOriginX);
                int originY = (int) (y - scrollOriginY);


                final float worldPixelWidth = mWorldPixelWidth;

                final float percentageScrollRight =
                    scrollOriginX != 0.0f ? scrollOriginX / worldPixelWidth : 0.0f;
                final float tileSpaceX = percentageScrollRight * mTilesPerRow;
                final int leftTile = (int)tileSpaceX;

                // calculate the top tile index
                final float worldPixelHeight = mWorldPixelHeight;

                final float percentageScrollUp =
                    scrollOriginY != 0.0f ? scrollOriginY / worldPixelHeight : 0.0f;
                final float tileSpaceY = percentageScrollUp * mTilesPerColumn;
                final int bottomTile = (int)tileSpaceY;

                // calculate any sub-tile slop that our scroll position may require.
                final int horizontalSlop = ((tileSpaceX - leftTile) * mTileWidth) > 0 ? 1 : 0;
                final int verticalSlop = ((tileSpaceY - bottomTile) * mTileHeight) > 0 ? 1 : 0;


                OpenGLSystem.bindTexture(GL10.GL_TEXTURE_2D, texture.name);
                tileMap.beginDrawingStrips(gl, true);

                final int horzTileCount = (int)Math.ceil((float)mWidth / mTileWidth);
                final int vertTileCount = (int)Math.ceil((float)mHeight / mTileHeight);
                // draw vertex strips
                final int startX = leftTile;
                final int startY = bottomTile;
                final int endX = startX + horizontalSlop + horzTileCount;
                final int endY = startY + verticalSlop +  vertTileCount;

                gl.glPushMatrix();
                gl.glLoadIdentity();
                gl.glTranslatef(
                        originX,
                        originY,
                        0.0f);


                final int indexesPerTile = 6;
                final int indexesPerRow = mTilesPerRow * indexesPerTile;
                final int startOffset = (startX * indexesPerTile);
                final int count = (endX - startX) * indexesPerTile;
                for (int tileY = startY; tileY < endY && tileY < mTilesPerColumn; tileY++) {
                    final int row = tileY * indexesPerRow;
                    tileMap.drawStrip(gl, true, row + startOffset, count);
                }

                gl.glPopMatrix();

                Grid.endDrawing(gl);
*/
//////// m

// !!!! TODO : shouldn't be done here, but in an "update" method !!!!
// => but where/when to call it ?
                mMoveOffsetX += mMovingSpeedX;
                mMoveOffsetY += mMovingSpeedY;

                final float worldPixelWidth = mWorldPixelWidth;
                final float worldPixelHeight = mWorldPixelHeight;

                int originX = (int)(x - (scrollOriginX + mMoveOffsetX));
                while (originX + worldPixelWidth <= 0) {
                    originX += worldPixelWidth;
                    mMoveOffsetX -= worldPixelWidth;
                }
                while (originX > 0) {
                    originX -= worldPixelWidth;
                    mMoveOffsetX += worldPixelWidth;
                }

                int originY = (int)(y - (scrollOriginY + mMoveOffsetY));
                while (originY + worldPixelHeight <= 0) {
                    originY += worldPixelHeight;
                    mMoveOffsetY -= worldPixelHeight;
                }
                while (originY > 0) {
                    originY -= worldPixelHeight;
                    mMoveOffsetY += worldPixelHeight;
                }
                final int firstOriginY = originY;

                final float percentageScrollRight = scrollOriginX + mMoveOffsetX != 0.0f ?
                        (scrollOriginX + mMoveOffsetX) / worldPixelWidth : 0.0f;
                final float tileSpaceX = percentageScrollRight * mTilesPerRow;

                final float percentageScrollUp = scrollOriginY + mMoveOffsetY != 0.0f ?
                        (scrollOriginY + mMoveOffsetY) / worldPixelHeight : 0.0f;
                final float tileSpaceY = percentageScrollUp * mTilesPerColumn;

                // calculate any sub-tile slop that our scroll position may require.
                final int horizontalSlop = ((tileSpaceX - (int)tileSpaceX) * mTileWidth) > 0 ? 1 : 0;
                final int verticalSlop = ((tileSpaceY - (int)tileSpaceY) * mTileHeight) > 0 ? 1 : 0;

                OpenGLSystem.bindTexture(GL10.GL_TEXTURE_2D, texture.name);
                tileMap.beginDrawingStrips(gl, true);

                final int horzTileCount = (int)Math.ceil((float)mWidth / mTileWidth);
                final int vertTileCount = (int)Math.ceil((float)mHeight / mTileHeight);

                final int indexesPerTile = 6;
                final int indexesPerRow = mTilesPerRow * indexesPerTile;

                gl.glPushMatrix();
//                gl.glLoadIdentity();

                while (originX < mWidth) {
                    originY = firstOriginY;
                    while (originY < mHeight) {
                        int leftTile = 0;
                        if (originX < 0) {
                            leftTile = (int)tileSpaceX;
                        }
                        final int startX = leftTile;

                        int bottomTile = 0;
                        if (originY < 0) {
                            bottomTile = (int)tileSpaceY;
                        }
                        final int startY = bottomTile;

                        int rightTile = startX + horzTileCount;
                        if (originX + worldPixelWidth > mWidth) {
                        	rightTile = startX + horizontalSlop + horzTileCount;
                        }
                        final int endX = rightTile;

                        int topTile = startY + vertTileCount;
                        if (originY + worldPixelHeight > mHeight) {
                        	topTile = startY + verticalSlop +  vertTileCount;
                        }
                        final int endY = topTile;

                        gl.glLoadIdentity();
                        gl.glTranslatef(originX, originY, 0.0f);

                        final int startOffset = startX * indexesPerTile;
                        final int count = (endX - startX) * indexesPerTile;
                        for (int tileY = startY; (tileY < endY) && (tileY < mTilesPerColumn); tileY++) {
                            final int row = tileY * indexesPerRow;
                            tileMap.drawStrip(gl, true, row + startOffset, count);
                        }
                        originY += worldPixelHeight;
                    }
                    originX += worldPixelWidth;
                }

                gl.glPopMatrix();
                Grid.endDrawing(gl);
//////// e
            }
        }
    }

}
