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

// !!!! TODO: add possibility to have more than 1 button codes per direction !!!!
// (to allow more than 1 dpad to have the same functions)

public class InputInterfaceSliderImpl extends InputInterfaceSlider {
    private static final float STICK_DEAD_ZONE_MIN = 0.03f;
    private static final float STICK_DEAD_ZONE_MAX = 0.1f;
    private static final float STICK_DEAD_ZONE_SCALE = 0.75f;

    private InputSystemImpl mInputSystem;

    private boolean mUseLeftStick;
    private boolean mUseRightStick;
    private float mStickDeadZoneMin = STICK_DEAD_ZONE_MIN;
    private float mStickDeadZoneMax = STICK_DEAD_ZONE_MAX;
    private float mStickDeadZoneScale = STICK_DEAD_ZONE_SCALE;
//    private float mStickSensitivity = 1.0f;
    private float mStickSensitivityFactor = 1.0f;

    private boolean mUseButtons;
    private int mLeftButtonCode;
    private int mRightButtonCode;
    private int mUpButtonCode;
    private int mDownButtonCode;

    public InputInterfaceSliderImpl(InputSystemImpl inputSystem) {
        super();

        mInputSystem = inputSystem;
        mUseLeftStick = true;
        mUseRightStick = false;
        mUseButtons = false;

        reset();
    }


    @Override
    public void destroy() {
        super.destroy();
        mInputSystem = null;
    }

/*
    @Override
    public void reset() {
        super.reset();
    }
*/

    @Override
    public void update(float timeDelta, float gameTime) {
        float magnitudeX = 0.0f;
        float magnitudeY = 0.0f;
        float pressTimeX = 0.0f;
        float pressTimeY = 0.0f;

        // priority on buttons, then left stick, then right stick
// !!!! ???? TODO: OK to do like that ? ???? !!!!
        if (mUseButtons) {
            final InputButton[] buttons = mInputSystem.getButtons().getKeys();

            // keys
            final InputButton left = buttons[mLeftButtonCode];
            final InputButton right = buttons[mRightButtonCode];
            final InputButton up = buttons[mUpButtonCode];
            final InputButton down = buttons[mDownButtonCode];

            final float leftPressedTime = left.getLastPressedTime();
            final float rightPressedTime = right.getLastPressedTime();
            final float upPressedTime = up.getLastPressedTime();
            final float downPressedTime = down.getLastPressedTime();

            // left and right are mutually exclusive
            if (leftPressedTime > rightPressedTime) {
//                magnitudeX = -left.getMagnitude() * SLIDER_FILTER * mSensitivity;
                magnitudeX = -left.getMagnitude() * mSensitivity;
                pressTimeX = leftPressedTime;
            } else {
//                magnitudeX = right.getMagnitude() * SLIDER_FILTER * mSensitivity;
                magnitudeX = right.getMagnitude() * mSensitivity;
                pressTimeX = rightPressedTime;
            }

            // up and down are mutually exclusive
            if (upPressedTime > downPressedTime) {
//                magnitudeY = -up.getMagnitude() * SLIDER_FILTER * mSensitivity;
                magnitudeY = -up.getMagnitude() * mSensitivity;
                pressTimeY = upPressedTime;
            } else {
//                magnitudeY = down.getMagnitude() * SLIDER_FILTER * mSensitivity;
                magnitudeY = down.getMagnitude() * mSensitivity;
                pressTimeY = downPressedTime;
            }

            if (magnitudeX != 0.0f || magnitudeY != 0.0f) {
                mSlider.press(Math.max(pressTimeX, pressTimeY),
                        magnitudeX, magnitudeY);
                return;
            }
        }

        if (mUseLeftStick) {
            final InputXY stick = mInputSystem.getLeftStick();
            magnitudeX = filterInput(stick.getX());
            magnitudeY = filterInput(stick.getY());

            if (magnitudeX != 0.0f || magnitudeY != 0.0f) {
                mSlider.clone(stick);
                mSlider.setMagnitude(magnitudeX, magnitudeY);
                return;
            }
        }

        if (mUseRightStick) {
            final InputXY stick = mInputSystem.getRightStick();
            magnitudeX = filterInput(stick.getX());
            magnitudeY = filterInput(stick.getY());

            if (magnitudeX != 0.0f || magnitudeY != 0.0f) {
                mSlider.clone(stick);
                mSlider.setMagnitude(magnitudeX, magnitudeY);
                return;
            }
        }

        mSlider.release();
    }

    private float filterInput(final float magnitude) {
        float scaledMagnitude = magnitude * mStickSensitivityFactor;
        return deadZoneFilter(scaledMagnitude,
                mStickDeadZoneMin, mStickDeadZoneMax, mStickDeadZoneScale);
    }

    public void setSticks(final boolean leftStick, final boolean rightStick) {
    	mUseLeftStick = leftStick;
    	mUseRightStick = rightStick;
    }

    public void setButtons(final int buttonLeft, final int buttonRight,
            final int buttonUp, final int buttonDown) {
        mLeftButtonCode = buttonLeft;
        mRightButtonCode = buttonRight;
        mUpButtonCode = buttonUp;
        mDownButtonCode = buttonDown;
        mUseButtons = true;
    }

    public void cancelButtons() {
        mUseButtons = false;
    }

}
