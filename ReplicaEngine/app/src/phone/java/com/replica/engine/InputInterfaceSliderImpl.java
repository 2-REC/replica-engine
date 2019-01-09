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

// !!!! TODO: change this completely to be able to handle several sliders !!!!
// (onscreen controls or not, etc.)

public class InputInterfaceSliderImpl extends InputInterfaceSlider {
    private static final float ORIENTATION_DEAD_ZONE_MIN = 0.03f;
    private static final float ORIENTATION_DEAD_ZONE_MAX = 0.1f;
    private static final float ORIENTATION_DEAD_ZONE_SCALE = 0.75f;

//?    private static InputXY mTilt = new InputXY();

    private InputSystemImpl mInputSystem;

    private boolean mUseOrientation = false;
// !!!! ???? TODO: keep these ? ???? !!!!
// ( if yes, need to see how to use them & their role ... )
    private float mOrientationDeadZoneMin = ORIENTATION_DEAD_ZONE_MIN;
    private float mOrientationDeadZoneMax = ORIENTATION_DEAD_ZONE_MAX;
    private float mOrientationDeadZoneScale = ORIENTATION_DEAD_ZONE_SCALE;
// !!!! ???? TODO: this field is not used, what to do with it ? ???? !!!!
// => keep it and remove "factor" ? or keep "factor" ? or both ?
//    private float mOrientationSensitivity = 1.0f;
    private float mOrientationSensitivityFactor = 1.0f;

    private int mTouchRegionX;
    private int mTouchRegionY;
    private int mTouchRegionWidth;
    private int mTouchRegionHeight;

    private HudButtonSlider mHudSlider;

    public InputInterfaceSliderImpl(InputSystemImpl inputSystem, HudButtonSlider slider) {
        super();

        mInputSystem = inputSystem;

        mHudSlider = slider;

// !!!! TODO : no need to do that here, as values are not set yet !!!!
        mTouchRegionX = (int)mHudSlider.getPosX();
        mTouchRegionY = (int)mHudSlider.getPosY();
        mTouchRegionWidth = (int)mHudSlider.getWidth();
        mTouchRegionHeight = (int)mHudSlider.getHeight();

//?        mUseOrientation = ...

        reset();
    }


// !!!! ???? TODO : OK ? ???? !!!!
// => "good" destructor ?
    @Override
    public void destroy() {
        super.destroy();

// !!!! ???? TODO: no need to destroy something ? ???? !!!!
        mHudSlider = null;
    }


    @Override
    public void reset() {
        super.reset();

//?        mTilt.release();

    }

    @Override
    public void update(float timeDelta, float gameTime) {
        if (mTouchRegionWidth == 0) {
            mTouchRegionX = (int)mHudSlider.getPosX();
            mTouchRegionY = (int)mHudSlider.getPosY();
            mTouchRegionWidth = (int)mHudSlider.getWidth();
            mTouchRegionHeight = (int)mHudSlider.getHeight();
        }

        if (mUseOrientation) {
            final InputXY orientation = mInputSystem.getOrientationSensor();
//?            mTilt.clone(orientation);

            mSlider.clone(orientation);
            mSlider.setMagnitude(filterOrientation(orientation.getX()),
                    filterOrientation(orientation.getY()));

        } else {
            final InputTouchScreen touch = mInputSystem.getTouchScreen();

            final InputXY sliderTouch = touch.findPointerInRegion(
                    mTouchRegionX, mTouchRegionY, mTouchRegionWidth, mTouchRegionHeight);

            float dpadOffsetX = 0.0f;
            float dpadOffsetY = 0.0f;

            if (sliderTouch != null) {
// !!!! ???? TODO : should be computed at creation, as will not change ? ???? !!!!
// => faster to have them as member variables (so not "final") or to compute them every step like this ?
                final float halfWidth = mTouchRegionWidth / 2.0f;
                final float halfHeight = mTouchRegionHeight / 2.0f;

                final float centerX = mTouchRegionX + halfWidth;
                final float centerY = mTouchRegionY + halfHeight;

                final float offsetX = sliderTouch.getX() - centerX;
                final float offsetY = sliderTouch.getY() - centerY;

                float magnitudeRampX = (Math.abs(offsetX) > halfWidth) ? 1.0f : (Math.abs(offsetX) / halfWidth);
                float magnitudeRampY = (Math.abs(offsetY) > halfHeight) ? 1.0f : (Math.abs(offsetY) / halfHeight);

                dpadOffsetX = magnitudeRampX * Utils.sign(offsetX);
                dpadOffsetY = magnitudeRampY * Utils.sign(offsetY);

//                final float magnitudeX = dpadOffsetX * SLIDER_FILTER * mSensitivity;
                final float magnitudeX = dpadOffsetX * mSensitivity;
//                final float magnitudeY = dpadOffsetY * SLIDER_FILTER * mSensitivity;
                final float magnitudeY = dpadOffsetY * mSensitivity;

                mSlider.press(gameTime, magnitudeX, magnitudeY);

            } else {
                mSlider.release();
            }

            mHudSlider.setButtonOffset(dpadOffsetX, dpadOffsetY);
        }

    }

    private float filterOrientation(float magnitude) {
        float scaledMagnitude = magnitude * mOrientationSensitivityFactor;
        return deadZoneFilter(scaledMagnitude,
                mOrientationDeadZoneMin, mOrientationDeadZoneMax, mOrientationDeadZoneScale);
    }

/*
?
    public final InputXY getTilt() {
        return mTilt;
    }
*/

    public void useOrientation(boolean use) {
        mUseOrientation = use;
        mHudSlider.show(!use);
    }

    public void setOrientationSensitivity(float sensitivity) {
//        mOrientationSensitivity = sensitivity;
        mOrientationSensitivityFactor = (2.9f * sensitivity) + 0.1f;
    }

    public void setTouchRegionSizeFactor(float factorX, float factorY) {
        if (factorX != 1.0f) {
            mTouchRegionWidth = (int)(mHudSlider.getWidth() * factorX);

            mTouchRegionX = (int)(mHudSlider.getPosX()
                    + (mHudSlider.getWidth() / 2.0f) - (mTouchRegionWidth / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
            if (mTouchRegionX < 0) {
                mTouchRegionX = 0;
            }
*/
        } else {
            mTouchRegionX = (int)mHudSlider.getPosX();
            mTouchRegionWidth = (int)mHudSlider.getWidth();
        }

        if (factorY != 1.0f) {
            mTouchRegionHeight = (int)(mHudSlider.getHeight() * factorY);

            mTouchRegionY = (int)(mHudSlider.getPosY()
                    + (mHudSlider.getHeight() / 2.0f) - (mTouchRegionHeight / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
            if (mTouchRegionY < 0) {
                mTouchRegionY = 0;
            }
*/
        } else {
            mTouchRegionY = (int)(mHudSlider.getPosY());
            mTouchRegionHeight = (int)(mHudSlider.getHeight());
        }
    }

    public void setDrawables(DrawableBitmap base, DrawableBitmap buttonEnabled,
            DrawableBitmap buttonDisabled, DrawableBitmap buttonDepressed) {
        mHudSlider.setDrawables(base, buttonEnabled, buttonDisabled, buttonDepressed);
    }

    public void show(boolean show) {
    	mHudSlider.show(show);
    }

}
