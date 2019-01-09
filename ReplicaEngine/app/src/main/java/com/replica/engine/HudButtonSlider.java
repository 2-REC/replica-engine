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
 * Slider button.
 */
public class HudButtonSlider extends HudIcon {

    private final float BUTTON_WIDTH;
    private final float BUTTON_HEIGHT;


    private float mButtonX;
    private float mButtonY;
    private float mButtonWidth;
    private float mButtonHeight;

    private DrawableBitmap mButtonEnabledDrawable;
    private DrawableBitmap mButtonDisabledDrawable;
    private DrawableBitmap mButtonDepressedDrawable;

    private Vector2 mButtonLocation;
    private boolean mButtonPressed;
    private boolean mActive;


    public HudButtonSlider(Size relativeSize,
            float posX, Alignment originX, Alignment alignmentX,
            float posY, Alignment originY, Alignment alignmentY,
            float width, float height, float padding,
            float buttonWidth, float buttonHeight) {
        super(relativeSize, posX, originX, alignmentX,
                posY, originY, alignmentY, width, height, padding);

        if (DebugChecks.DEBUG) {
            DebugChecks.assertCondition(buttonWidth >= 0.0f);
            DebugChecks.assertCondition(buttonHeight >= 0.0f);
            DebugChecks.assertCondition(buttonWidth != 0.0f || buttonHeight != 0.0f);
        }

        BUTTON_WIDTH = buttonWidth;
        BUTTON_HEIGHT = buttonHeight;

        mButtonLocation = new Vector2();
    }

    @Override
    public void reset() {
        super.reset();

        mButtonEnabledDrawable = null;
        mButtonDisabledDrawable = null;
        mButtonDepressedDrawable = null;
        mButtonPressed = false;
    }

    @Override
    protected void init() {
        super.init();

    	float refX = mWidth;
        float refY = mHeight;
        if (SIZE == Size.ABSOLUTE) {
            ContextParameters params = sSystemRegistry.contextParameters;
            final DisplayMetrics dm = params.context.getResources().getDisplayMetrics();
            refX = dm.xdpi;
            refY = dm.ydpi;
        }

        Texture tex = mButtonEnabledDrawable.getTexture();
        final float ratio = (float)tex.width / tex.height;
        if (BUTTON_WIDTH != 0.0f) {
            mButtonWidth = BUTTON_WIDTH * refX;
            if (BUTTON_HEIGHT != 0.0f) {
                mButtonHeight = BUTTON_HEIGHT * refY;
            } else {
                mButtonHeight = mButtonWidth / ratio;
            }
        } else {
            mButtonHeight = BUTTON_HEIGHT * refY;
            mButtonWidth = mButtonHeight * ratio;
        }

        mButtonX = mPosX + (mWidth / 2.0f) - (mButtonWidth / 2.0f);
        mButtonY = mPosY + (mHeight / 2.0f) - (mButtonHeight / 2.0f);
    }

    @Override
    protected void scaleSizes() {
        super.scaleSizes();

        final ContextParameters params = sSystemRegistry.contextParameters;
        final float invViewScaleX = 1.0f / params.viewScaleX;
        final float invViewScaleY = 1.0f / params.viewScaleY;
        mButtonX *= invViewScaleX;
        mButtonY *= invViewScaleY;
        mButtonWidth *= invViewScaleX;
        mButtonHeight *= invViewScaleY;
    }

    @Override
    protected void setLocation() {
        super.setLocation();

        mButtonLocation.set(mButtonX, mButtonY);
    }

    @Override
    protected void draw(RenderSystem render) {
        super.draw(render);

        DrawableBitmap bitmap = mButtonEnabledDrawable;
        if (mActive && mButtonPressed) {
            bitmap = mButtonDepressedDrawable;
        } else if (!mActive) {
            bitmap = mButtonDisabledDrawable;
        }

        render.scheduleForDraw(bitmap, mButtonLocation,
                SortConstants.HUD + 1, false);
    }

    @Override
    protected void resizeDrawables(final int width, final int height) {
//        super.resizeDrawables(width, height);
        mDrawable.resize(width, height);

// !!!! TODO: padding should consider button/base size ratio !!!!
        final int buttonWidth = (int)(mButtonWidth - (mPaddingX * 2.0f));
        final int buttonHeight = (int)(mButtonHeight - (mPaddingY * 2.0f));
        mButtonEnabledDrawable.resize(buttonWidth, buttonHeight);
        mButtonDisabledDrawable.resize(buttonWidth, buttonHeight);
        mButtonDepressedDrawable.resize(buttonWidth, buttonHeight);
    }

    // this method shouldn't be used
    @Override
    public void setDrawable(DrawableBitmap bitmap) {
        setDrawables(bitmap, bitmap, bitmap, bitmap);
    }

    @Override
    protected boolean checkDrawables() {
        return (mDrawable != null &&
                mButtonEnabledDrawable != null &&
                mButtonDisabledDrawable != null &&
                mButtonDepressedDrawable != null);
    }


    public void setDrawables(DrawableBitmap base, DrawableBitmap buttonEnabled,
            DrawableBitmap buttonDisabled, DrawableBitmap buttonDepressed) {
        mDrawable = base;
        mButtonEnabledDrawable = buttonEnabled;
        mButtonDisabledDrawable = buttonDisabled;
        mButtonDepressedDrawable = buttonDepressed;

// !!!! ???? TODO : do that here or in caller ? ???? !!!!
        mDrawable.setCropUse(false);
        mButtonEnabledDrawable.setCropUse(false);
        mButtonDisabledDrawable.setCropUse(false);
        mButtonDepressedDrawable.setCropUse(false);
    }


    public void setState(boolean pressed) {
        mButtonPressed = pressed;
    }

    public void setButtonOffset(float offsetX, float offsetY) {
        mButtonX = mPosX + (mWidth / 2.0f) * (1.0f + offsetX) - (mButtonWidth / 2.0f);
        mButtonY = mPosY + (mHeight / 2.0f) * (1.0f + offsetY) - (mButtonHeight / 2.0f);

        mButtonLocation.set(mButtonX, mButtonY);
    }

    public float getButtonX() {
       return mButtonX;
    }

    public float getButtonY() {
       return mButtonY;
    }

    public float getButtonWidth() {
       return mButtonWidth;
    }

    public float getButtonHeight() {
       return mButtonHeight;
    }

}
