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

import android.util.DisplayMetrics;

/**
 * Generic abstract hud element.
 */
public abstract class HudElement extends BaseObject {

    public static enum Size {
        ABSOLUTE,
        RELATIVE,
    }

    public static enum Alignment {
        CENTER,
        LEFT,
        RIGHT,
        BOTTOM,
        TOP,
    }


    protected final Size SIZE;

    protected final float PADDING;
	protected final float X;
	protected final Alignment X_ORIGIN;
	protected final Alignment X_ALIGNMENT;
	protected final float Y;
	protected final Alignment Y_ORIGIN;
	protected final Alignment Y_ALIGNMENT;
	protected final float WIDTH;
    protected final float HEIGHT;

    protected float mPosX;
    protected float mPosY;
    protected float mWidth;
    protected float mHeight;
    protected float mPaddingX;
    protected float mPaddingY;

    protected Vector2 mLocation;
    protected boolean mVisible;

    private boolean mInitialised;


    public HudElement(Size relativeSize,
            float posX, Alignment originX, Alignment alignmentX,
            float posY, Alignment originY, Alignment alignmentY,
            float width, float height, float padding) {
        super();

        if (DebugChecks.DEBUG) {
            DebugChecks.assertCondition((originX == Alignment.LEFT) || (originX == Alignment.CENTER) || (originX == Alignment.RIGHT));
            DebugChecks.assertCondition((alignmentX == Alignment.LEFT) || (alignmentX == Alignment.CENTER) || (originX == Alignment.RIGHT));
            DebugChecks.assertCondition((originY == Alignment.BOTTOM) || (originY == Alignment.CENTER) || (originY == Alignment.TOP));
            DebugChecks.assertCondition((alignmentY == Alignment.BOTTOM) || (alignmentY == Alignment.CENTER) || (originY == Alignment.TOP));
            DebugChecks.assertCondition(width >= 0.0f);
            DebugChecks.assertCondition(height >= 0.0f);
        }

        SIZE = relativeSize;

        X = posX;
        X_ORIGIN = originX;
        X_ALIGNMENT = alignmentX;

        Y = posY;
        Y_ORIGIN = originY;
        Y_ALIGNMENT = alignmentY;

        PADDING = padding;
        WIDTH = width;
        HEIGHT = height;

        mLocation = new Vector2();

        mVisible = true;

        mInitialised = false;
    }

    @Override
    public void reset() {
        mInitialised = false;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        if (mVisible) {
            final RenderSystem render = sSystemRegistry.renderSystem;
            if (render != null && checkDrawables()) {
//                if (mDrawable.getWidth() == 0) {
                if (!mInitialised) {
                    // first time init
                    init();
                    scaleSizes();
                    mInitialised = true;

//                    mLocation.set(mPosX + mPaddingX, mPosY + mPaddingY);
                    setLocation();

                    final int width = (int)(mWidth - (mPaddingX * 2.0f));
                    final int height = (int)(mHeight - (mPaddingY * 2.0f));
//                    mDrawable.resize(width, height);
                    resizeDrawables(width, height);
                }

/*
                render.scheduleForDraw(mDrawable, mLocation, SortConstants.HUD, false);
*/
                draw(render);
            }
        }
    }

    protected void init() {
        final ContextParameters params = sSystemRegistry.contextParameters;
        final int viewWidth = params.viewWidth;
        final int viewHeight = params.viewHeight;

        float refX = viewWidth;
        float refY = viewHeight;
        if (SIZE == Size.ABSOLUTE) {
            final DisplayMetrics dm = params.context.getResources().getDisplayMetrics();
            refX = dm.xdpi;
            refY = dm.ydpi;
        }

        // sizes
        final float ratio = getRatio();
        if (WIDTH != 0.0f) {
            mWidth = WIDTH * refX;
            if (HEIGHT != 0.0f) {
                mHeight = HEIGHT * refY;
            } else {
                mHeight = mWidth / ratio;
            }
        } else {
            mHeight = HEIGHT * refY;
            mWidth = mHeight * ratio;
        }

        // x position
        mPosX = X * refX;
        if (X_ORIGIN == Alignment.RIGHT) {
            mPosX = viewWidth - mPosX;
        } else if (X_ORIGIN == Alignment.CENTER) {
            mPosX = (viewWidth / 2.0f) + mPosX;
        }
        // x alignment
        if (X_ALIGNMENT == Alignment.CENTER) {
            mPosX -= (mWidth / 2.0f);
        } else if (X_ALIGNMENT == Alignment.RIGHT) {
            mPosX -= mWidth;
        }

        // y position
        mPosY = Y * refY;
        if (Y_ORIGIN == Alignment.TOP) {
            mPosY = (viewHeight - mPosY);
        } else if (Y_ORIGIN == Alignment.CENTER) {
            mPosY = (viewHeight / 2.0f) + mPosY;
        }
        // y alignment
        if (Y_ALIGNMENT == Alignment.CENTER) {
            mPosY -= (mHeight / 2.0f);
        } else if (Y_ALIGNMENT == Alignment.TOP) {
            mPosY -= mHeight;
        }

        // padding
        if (SIZE == Size.RELATIVE) {
            mPaddingX = mWidth * PADDING;
            mPaddingY = mHeight * PADDING;
        } else {
            mPaddingX = refX * PADDING;
            mPaddingY = refY * PADDING;
        }
    }

    protected void scaleSizes() {
        final ContextParameters params = sSystemRegistry.contextParameters;
        final float invViewScaleX = 1.0f / params.viewScaleX;
        final float invViewScaleY = 1.0f / params.viewScaleY;

        mPosX *= invViewScaleX;
        mPosY *= invViewScaleY;
        mWidth *= invViewScaleX;
        mHeight *= invViewScaleY;
        mPaddingX *= invViewScaleX;
        mPaddingY *= invViewScaleY;
    }

    protected float getRatio() {
        return 1.0f;
    }

    protected void setLocation() {
        mLocation.set(mPosX + mPaddingX, mPosY + mPaddingY);
    }

// !!!! TODO: implement "flip" handling !!!!
/*
    public void flip() {
        mPosX = sSystemRegistry.contextParameters.viewWidth - (mPosX + mWidth);
    }
*/

    public void show(boolean visible) {
        mVisible = visible;
    }

    public float getPosX() {
        return mPosX;
    }
    public float getPosY() {
        return mPosY;
    }
    public float getWidth() {
        return mWidth;
    }
    public float getHeight() {
        return mHeight;
    }


    protected abstract void draw(RenderSystem render);

    protected abstract boolean checkDrawables();

    protected abstract void resizeDrawables(final int width, final int height);

}
