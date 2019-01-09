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

// !!!! TODO: handle counters aligned to the right (each new digit drawn on left) !!!!
// => determine position/padding depending on nb digits (could get it from "intToDigitArray")

/**
 * Counter.
 */
public class HudCounter extends HudElement {
    protected final int MAX_DIGITS;

    protected int mCounterValue;
    protected HudDigits mHudDigits;
    protected int[] mDigits;
    protected boolean mShowZeroes;
    protected boolean mDigitsChanged;

    public HudCounter(Size relativeSize,
            float posX, Alignment originX, Alignment alignmentX,
            float posY, Alignment originY, Alignment alignmentY,
            float padding, HudDigits hudDigits, int nbDigits,
            final boolean showZeroes) {
        super(relativeSize, posX, originX, alignmentX, posY, originY, alignmentY,
                0.0f, 0.0f, padding);

        DebugChecks.assertCondition(nbDigits > 0);

        MAX_DIGITS = nbDigits;

        mHudDigits = hudDigits;
        mDigits = new int[MAX_DIGITS];
        mShowZeroes = showZeroes;
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

        // x alignment
        final float width = mHudDigits.getDigitWidth() * MAX_DIGITS;
        if (X_ALIGNMENT == Alignment.CENTER) {
            mPosX -= width / 2.0f;
        } else if (X_ALIGNMENT == Alignment.RIGHT) {
            mPosX -= width;
        }

        // y alignment
        final float height = mHudDigits.getDigitHeight();
        if (Y_ALIGNMENT == Alignment.CENTER) {
            mPosY -= height / 2.0f;
        } else if (Y_ALIGNMENT == Alignment.TOP) {
            mPosY -= height;
        }

        // padding
        if (SIZE == Size.RELATIVE) {
            mPaddingX = width * PADDING;
            mPaddingY = height * PADDING;
/*
        } else {
            mPaddingX = PADDING;
            mPaddingY = PADDING;
*/
        }
    }

    @Override
    protected void draw(RenderSystem render) {
        if (mDigitsChanged) {
            HudDigits.intToDigitArray(mCounterValue, mDigits, mShowZeroes);
            mDigitsChanged = false;
        }
        mHudDigits.drawNumber(mLocation, mDigits, true);
    }

    @Override
    protected void resizeDrawables(final int width, final int height) {
    }

    @Override
    protected boolean checkDrawables() {
        return true;
    }

}
