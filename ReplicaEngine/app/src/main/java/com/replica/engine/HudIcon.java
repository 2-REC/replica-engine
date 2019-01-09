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
 * Icon.
 */
public class HudIcon extends HudElement {

    protected DrawableBitmap mDrawable;

    public HudIcon(Size relativeSize,
            float posX, Alignment originX, Alignment alignmentX,
            float posY, Alignment originY, Alignment alignmentY,
            float width, float height, float padding) {
        super(relativeSize, posX, originX, alignmentX, posY, originY, alignmentY,
                width, height, padding);
    }

    @Override
    public void reset() {
        super.reset();
        mDrawable = null;
    }

    @Override
    protected float getRatio() {
        if (mDrawable == null) {
            return 1.0f;
        }

        final Texture tex = mDrawable.getTexture();
        return (float)(tex.width / tex.height);
    }

    @Override
    protected void draw(RenderSystem render) {
        DrawableBitmap bitmap = getDrawable();
        render.scheduleForDraw(bitmap, mLocation,
                SortConstants.HUD, false);
    }

    @Override
    protected void resizeDrawables(final int width, final int height) {
        mDrawable.resize(width, height);
    }

    public void setDrawable(DrawableBitmap bitmap) {
        mDrawable = bitmap;

// !!!! ???? TODO : do that here or in caller ? ???? !!!!
        mDrawable.setCropUse(false);
    }

    public DrawableBitmap getDrawable() {
        return mDrawable;
    }

    @Override
    protected boolean checkDrawables() {
        return (mDrawable != null);
    }

}
