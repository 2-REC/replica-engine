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

// !!!! TODO: handle bars with changeable size !!!!
// (eg: life bar increased)

/**
 * Progress bar.
 */
public class HudBar extends HudIcon {

    private final float BAR_PADDING;
    private final float DECREASE_SPEED;
    private final float INCREASE_SPEED;

    private float mBarPaddingX;
    private float mBarPaddingY;

    private DrawableBitmap mBarDrawable;
    private Vector2 mBarLocation;

    private float mPercent;
    private float mTargetPercent;


    public HudBar(Size relativeSize,
            float posX, Alignment originX, Alignment alignmentX,
            float posY, Alignment originY, Alignment alignmentY,
            float width, float height, float padding, float barPadding,
            float decreaseSpeed, float increaseSpeed) {
        super(relativeSize, posX, originX, alignmentX,
                posY, originY, alignmentY, width, height, padding);

        BAR_PADDING = barPadding;
        DECREASE_SPEED = decreaseSpeed;
        INCREASE_SPEED = increaseSpeed;

        mBarLocation = new Vector2();
    }


    @Override
    public void reset() {
        super.reset();

        mBarDrawable = null;

        mPercent = 1.0f;
        mTargetPercent = 1.0f;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        if (mVisible) {
            if (mPercent < mTargetPercent) {
                if (INCREASE_SPEED == 0) {
                    mPercent = mTargetPercent;
                } else {
                    mPercent += (INCREASE_SPEED * timeDelta);
                    if (mPercent > mTargetPercent) {
                        mPercent = mTargetPercent;
                    }
                }
            } else if (mPercent > mTargetPercent) {
                if (DECREASE_SPEED == 0) {
                    mPercent = mTargetPercent;
                } else {
                    mPercent -= (DECREASE_SPEED * timeDelta);
                    if (mPercent < mTargetPercent) {
                        mPercent = mTargetPercent;
                    }
                }
            }
        }

        super.update(timeDelta, parent);
    }

    @Override
    protected void init() {
        super.init();

        if (SIZE == Size.RELATIVE) {
            mBarPaddingX = mWidth * BAR_PADDING;
            mBarPaddingY = mHeight * BAR_PADDING;
        } else {
            mBarPaddingX = BAR_PADDING;
            mBarPaddingY = BAR_PADDING;
        }
    }

    @Override
    protected void scaleSizes() {
        super.scaleSizes();

        final ContextParameters params = sSystemRegistry.contextParameters;
        final float invViewScaleX = 1.0f / params.viewScaleX;
        final float invViewScaleY = 1.0f / params.viewScaleY;
        mBarPaddingX *= invViewScaleX;
        mBarPaddingY *= invViewScaleY;
    }

    @Override
    protected void setLocation() {
        super.setLocation();

        mBarLocation.set(mLocation);
        mBarLocation.add(mBarPaddingX, mBarPaddingY);
    }

    @Override
    protected void draw(RenderSystem render) {
        super.draw(render);

        final int barWidth = (int)(mPercent * mBarDrawable.getWidth());
        if (barWidth >= 1) {
            final DrawableFactory factory = sSystemRegistry.drawableFactory;
            if (factory != null) {
                DrawableBitmap barBitmap = factory.allocateDrawableBitmap();
                if (barBitmap != null) {
                	barBitmap.resize(barWidth, mBarDrawable.getHeight());
                	barBitmap.setTexture(mBarDrawable.getTexture());
                    render.scheduleForDraw(barBitmap, mBarLocation,
                            SortConstants.HUD + 1, false);
                }
            }
        }
    }

    @Override
    protected void resizeDrawables(final int width, final int height) {
//        super.resizeDrawables(width, height);
        mDrawable.resize(width, height);

        mBarDrawable.resize((int)(width - (mBarPaddingX * 2.0f)), (int)(height - (mBarPaddingY * 2.0f)));
    }

    // this method shouldn't be used
    @Override
    public void setDrawable(DrawableBitmap bitmap) {
        setDrawables(bitmap, bitmap);
    }

    @Override
    protected boolean checkDrawables() {
        return (mDrawable != null && mBarDrawable != null);
    }

    public void setDrawables(DrawableBitmap bar, DrawableBitmap background) {
    	mBarDrawable = bar;
    	mDrawable = background;

// !!!! ???? TODO : do that here or in caller ? ???? !!!!
        mBarDrawable.setCropUse(false);
        mDrawable.setCropUse(false);
    }


    public void setPercent(float percent) {
        DebugChecks.assertCondition(percent >= 0.0f && percent <= 1.0f);
        mTargetPercent = percent;
    }

}
