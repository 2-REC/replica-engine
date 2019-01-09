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

/**
 * Press button.
 */
public class HudButton extends HudIcon {

    private DrawableBitmap mDisabledDrawable;
    private DrawableBitmap mDepressedDrawable;
    private boolean mActive;
    private boolean mPressed;

    public HudButton(Size relativeSize,
            float posX, Alignment originX, Alignment alignmentX,
            float posY, Alignment originY, Alignment alignmentY,
            float width, float height, float padding) {
        super(relativeSize, posX, originX, alignmentX,
                posY, originY, alignmentY, width, height, padding);
    }

    @Override
    public void reset() {
        super.reset();

        mDisabledDrawable = null;
        mDepressedDrawable = null;
        mActive = true;
        mPressed = false;
    }

    @Override
    protected void resizeDrawables(final int width, final int height) {
//        super.resizeDrawables(width, height);
        mDrawable.resize(width, height);

        mDisabledDrawable.resize(width, height);
        mDepressedDrawable.resize(width, height);
    }

    public void setDrawables(DrawableBitmap enabled,
            DrawableBitmap disabled, DrawableBitmap depressed) {
        mDrawable = enabled;
        mDisabledDrawable = disabled;
        mDepressedDrawable = depressed;

// !!!! ???? TODO : do that here or in caller ? ???? !!!!
        mDrawable.setCropUse(false);
        mDisabledDrawable.setCropUse(false);
        mDepressedDrawable.setCropUse(false);
    }

    // this method shouldn't be used
    @Override
    public void setDrawable(DrawableBitmap bitmap) {
        setDrawables(bitmap, bitmap, bitmap);
    }

    @Override
    public DrawableBitmap getDrawable() {
        DrawableBitmap bitmap = mDrawable;
        if (mActive && mPressed) {
            bitmap = mDepressedDrawable;
        } else if (!mActive) {
            bitmap = mDisabledDrawable;
        }
        return bitmap;
    }

    @Override
    protected boolean checkDrawables() {
        return (mDrawable != null &&
                mDisabledDrawable != null &&
                mDepressedDrawable != null);
    }


    public void setState(boolean pressed) {
        mPressed = pressed;
    }

}
