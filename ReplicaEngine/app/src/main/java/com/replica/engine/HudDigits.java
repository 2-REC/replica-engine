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
 * Manages the UI digits.
 */
public class HudDigits {

    private DrawableBitmap[] mDigitDrawables;
    private float mDigitsFactorWidth;
    private float mDigitsFactorHeight;
    private HudElement.Size mDigitsSize;

    public HudDigits() {
        super();

        mDigitDrawables = new DrawableBitmap[10];
        mDigitsFactorWidth = 0.1f;
        mDigitsFactorHeight = 0.1f;

        reset();
    }


    public void reset() {
        for (int x = 0; x < mDigitDrawables.length; x++) {
            mDigitDrawables[x] = null;
        }
    }

    public void setDrawables(final DrawableBitmap[] digits, final boolean setCropUse) {
        for (int x = 0; (x < mDigitDrawables.length) && (x < digits.length); x++) {
            mDigitDrawables[x] = digits[x];
            mDigitDrawables[x].setCropUse(setCropUse);
        }
    }

    public void setSizeFactors(HudElement.Size size, float widthFactor, float heightFactor) {
        DebugChecks.assertCondition((widthFactor != 0.0f) || (heightFactor != 0.0f));

        mDigitsSize = size;
        mDigitsFactorWidth = widthFactor;
        mDigitsFactorHeight = heightFactor;
    }

    private void init() {
        final ContextParameters params = BaseObject.sSystemRegistry.contextParameters;

        final Texture tex = mDigitDrawables[0].getTexture();
        final float ratio = (float)tex.width / tex.height;

        float width; 
        float height;

        if (mDigitsSize == HudElement.Size.RELATIVE) {
            width = params.viewWidth;
            height = params.viewHeight;
        } else {
            final DisplayMetrics dm = params.context.getResources().getDisplayMetrics();
            width = dm.xdpi;
            height = dm.ydpi;
        }

        if (mDigitsFactorWidth != 0.0f) {
            width *= mDigitsFactorWidth;
            if (mDigitsFactorHeight != 0.0f) {
                height *= mDigitsFactorHeight;
            } else {
                height = width / ratio;
            }
        } else {
            height *= mDigitsFactorHeight;
            width = height * ratio;
        }

        width /= params.viewScaleX;
        height /= params.viewScaleY;

        final int w = (int)width;
        final int h = (int)height;


        for (int x = 0; x < mDigitDrawables.length; x++) {
            mDigitDrawables[x].resize(w, h);
        }
    }

    public void drawNumber(Vector2 location, int[] digits, boolean drawX) {
        final RenderSystem render = BaseObject.sSystemRegistry.renderSystem;

        if (mDigitDrawables[0].getWidth() == 0) {
            // first time init
            init();
        }

        final float characterWidth = mDigitDrawables[0].getWidth() / 2.0f;
        float offset = 0.0f;

        for (int x = 0; (x < digits.length) && (digits[x] != -1); x++) {
            int index = digits[x];
            DrawableBitmap digit = mDigitDrawables[index];
            if (digit != null) {
                render.scheduleForDraw(digit, location, SortConstants.HUD, false);
                location.x += characterWidth;
                offset += characterWidth;
            }
        }
        location.x -= offset;
    }

    public int getDigitWidth() {
        DebugChecks.assertCondition(mDigitDrawables[0] != null);

        if (mDigitDrawables[0].getWidth() == 0) {
            // first time init
            init();
        }

        return (int)(mDigitDrawables[0].getWidth() * BaseObject.sSystemRegistry.contextParameters.viewScaleX);
    }

    public int getDigitHeight() {
        DebugChecks.assertCondition(mDigitDrawables[0] != null);

        if (mDigitDrawables[0].getWidth() == 0) {
            // first time init
            init();
        }

        return (int)(mDigitDrawables[0].getHeight() * BaseObject.sSystemRegistry.contextParameters.viewScaleY);
    }

// !!!! TODO: check that OK !!!!
/*
    public int intToDigitArray(int value, int[] digits) {
        int characterCount = 1;
// !!!! TODO : make something more generic !!!!
// (to handle numbers of arbitrary digits number)
        if (value >= 1000) {
            characterCount = 4;
        } else if (value >= 100) {
            characterCount = 3;
        } else if (value >= 10) {
            characterCount = 2;
        }

        int remainingValue = value;
        int count = 0;
        do {
            int index = remainingValue != 0 ? remainingValue % 10 : 0;
            remainingValue /= 10;
            digits[characterCount - 1 - count] = index;
            count++;
        }
        while ((remainingValue > 0) && (count < digits.length));

        if (count < digits.length) {
            digits[count] = -1;
        }
        return characterCount;
    }
*/
    public static int intToDigitArray(int value, int[] digits, boolean showZeroes) {
        // fill array with reversed number
        int remainingValue = value;
        int count = 0;
        do {
            int index = remainingValue != 0 ? remainingValue % 10 : 0;
            remainingValue /= 10;
            digits[count] = index;
            count++;
        }
        while ((remainingValue > 0) && (count < digits.length));

        // add trailing -1 or pad with 0s
        if (!showZeroes) {
            if (count < digits.length) {
                digits[count] = -1;
            }
        } else {
            while (count < digits.length) {
                digits[count] = 0;
                count++;
            }
        }

        // reverse array
        for (int i=0, j=count-1; i<j; ++i,--j) {
            final int digit = digits[i];
            digits[i] = digits[j];
            digits[j] = digit;
        }

        return count;
    }

}
