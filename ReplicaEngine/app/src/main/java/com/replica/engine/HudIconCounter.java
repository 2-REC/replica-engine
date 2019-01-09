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
 * Counter with icon.
 */
public class HudIconCounter extends HudIcon {

    protected final int MAX_DIGITS;
    private final float INSIDE_PADDING;

    private Vector2 mCounterLocation;
    private float mCounterOffsetX;
    private float mCounterOffsetY;

    protected int mCounterValue;
    protected HudDigits mHudDigits;
    protected int[] mDigits;
    protected boolean mShowZeroes;
    protected boolean mDigitsChanged;

    public HudIconCounter(Size relativeSize,
            float posX, Alignment originX, Alignment alignmentX,
            float posY, Alignment originY, Alignment alignmentY,
            float width, float height, float padding, float insidePadding,
            HudDigits hudDigits, int nbDigits, boolean showZeroes) {
        super(relativeSize, posX, originX, alignmentX, posY, originY, alignmentY,
                width, height, padding);

        DebugChecks.assertCondition(nbDigits > 0);

        INSIDE_PADDING = insidePadding;
        MAX_DIGITS = nbDigits;

        mHudDigits = hudDigits;
        mDigits = new int[MAX_DIGITS];
        mShowZeroes = showZeroes;

        mCounterLocation = new Vector2();

    }

    @Override
    public void reset() {
        super.reset();

        mCounterValue = 0;
        mDigits[0] = 0;
        if (MAX_DIGITS > 1) {
            mDigits[1] = -1;
        }
        mDigitsChanged = true;
    }

    @Override
    protected void init() {
        super.init();

        float refX = mWidth;
//        float refY = mHeight;
        if (SIZE == Size.ABSOLUTE) {
            final ContextParameters params = sSystemRegistry.contextParameters;
            final DisplayMetrics dm = params.context.getResources().getDisplayMetrics();
            refX = dm.xdpi;
//            refY = dm.ydpi;
        }

        // x alignment
        final float width = mHudDigits.getDigitWidth() * MAX_DIGITS;
        final float insideOffset = refX * INSIDE_PADDING;
        if (X_ALIGNMENT == Alignment.CENTER) {
            mPosX -= width / 2.0f;
            mPosX -= insideOffset / 2.0f;
        } else if (X_ALIGNMENT == Alignment.RIGHT) {
            mPosX -= width;
            mPosX -= insideOffset;
        }

        mCounterOffsetX = mWidth + insideOffset;
        // align counter y position to icon (centre)
        mCounterOffsetY = (mHeight - mHudDigits.getDigitHeight()) / 2.0f;
    }

    @Override
    protected void scaleSizes() {
        super.scaleSizes();

        final ContextParameters params = sSystemRegistry.contextParameters;
        final float invViewScaleX = 1.0f / params.viewScaleX;
        final float invViewScaleY = 1.0f / params.viewScaleY;
        mCounterOffsetX *= invViewScaleX;
        mCounterOffsetY *= invViewScaleY;
    }

    @Override
    protected void setLocation() {
        super.setLocation();

        mCounterLocation.set(mLocation);
        mCounterLocation.add(mCounterOffsetX, mCounterOffsetY);
    }

    @Override
    protected void draw(RenderSystem render) {
        super.draw(render);

        if (mDigitsChanged) {
            HudDigits.intToDigitArray(mCounterValue, mDigits, mShowZeroes);
            mDigitsChanged = false;
        }
        mHudDigits.drawNumber(mCounterLocation, mDigits, true);
    }


    public void updateCounter(int newValue) {
    	mDigitsChanged = true;
    	mCounterValue = newValue;
    }

    public final int getCounterValue() {
        return mCounterValue;
    }

}
