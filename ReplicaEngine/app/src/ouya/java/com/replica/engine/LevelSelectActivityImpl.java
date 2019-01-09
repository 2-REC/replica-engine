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

import android.os.Bundle;
import android.os.Handler;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import tv.ouya.console.api.OuyaController;

public class LevelSelectActivityImpl extends LevelSelectActivity {
    private static final float STICK_DEADZONE_2 = OuyaController.STICK_DEADZONE * OuyaController.STICK_DEADZONE;
    private static final float TRIGGER_DEADZONE = 0.1f;

    private Handler mHandler;
    private Runnable mRunnable;
    private float mPanX;
    private float mPanY;
    private float mZoom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
        mRunnable =
            new Runnable() {
                @Override
                public void run() {
                    if (mPanX != 0.0f || mPanY != 0.0f) {
                        pan(mPanX, mPanY);
                        cancelSelectLevel();
                    }
                    if (mZoom != 1.0f) {
                        zoom(mZoom);
                        cancelSelectLevel();
                    }
                    mHandler.postDelayed(this, 32);
                }
            };
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPanX = 0.0f;
        mPanY = 0.0f;
        mZoom = 1.0f;
        mHandler.post(mRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

// !!!! ???? TODO: OK to have this instead of "dispatchKeyEvent" ? ???? !!!!
//    public boolean dispatchKeyEvent(KeyEvent event) {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
// !!!! TODO: need to handle the MENU (U) button to avoid "pausing" the map !!!!
// (& then unpause by pressing again)
// BUT: need to allow double press for OUYA menu!
        boolean result = false;
        if (keyCode == OuyaController.BUTTON_Y || keyCode == OuyaController.BUTTON_A) {
            cancelSelectLevel();
            result = true;
        } else if (keyCode == OuyaController.BUTTON_DPAD_RIGHT || keyCode == OuyaController.BUTTON_R1) {
            selectNextLevel();
            result = true;
        } else if (keyCode == OuyaController.BUTTON_DPAD_LEFT || keyCode == OuyaController.BUTTON_L1) {
            selectPreviousLevel();
            result = true;
        } else {
            result = super.onKeyDown(keyCode, event);
        }
        return result;
    }

// !!!! ???? TODO: needed ? ???? !!!!
/*
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }
*/

    @Override
//    public boolean onGenericMotionEvent(final MotionEvent event) {
    public boolean dispatchGenericMotionEvent(final MotionEvent event) {
        if ((event.getSource() & InputDevice.SOURCE_CLASS_JOYSTICK) == 0) {
            return false;
        }

//        int player = OuyaController.getPlayerNumByDeviceId(event.getDeviceId());
// !!!! TODO: test that player 1 !!!!
//=> player == 1 ?

        // left stick => pan
        float LS_X = event.getAxisValue(OuyaController.AXIS_LS_X);
        float LS_Y = event.getAxisValue(OuyaController.AXIS_LS_Y);
        if (((LS_X * LS_X) + (LS_Y * LS_Y)) < STICK_DEADZONE_2) {
            LS_X = LS_Y = 0.0f;
        }

        // right stick horizontal => ignored
        //float RS_X = event.getAxisValue(OuyaController.AXIS_RS_X); // no zoom with X axis

        // right stick vertical => zoom
        float RS_Y = event.getAxisValue(OuyaController.AXIS_RS_Y);
        if ((RS_Y * RS_Y) < STICK_DEADZONE_2) {
            RS_Y = 0.0f;
        }

        // left trigger => zoom in
        float L2 = event.getAxisValue(OuyaController.AXIS_L2);
        if (L2 < TRIGGER_DEADZONE) {
            L2 = 0.0f;
        }
        // right trigger => zoom out
        float R2 = event.getAxisValue(OuyaController.AXIS_R2);
        if (R2 < TRIGGER_DEADZONE) {
            R2 = 0.0f;
        }

        mPanX = -LS_X * 25.0f;
        mPanY = -LS_Y * 25.0f;


        float scale = 1.0f;
        if (RS_Y != 0.0f) {
            RS_Y = Math.min(RS_Y, 1.0f);
            RS_Y = Math.max(RS_Y, -1.0f);

            scale = (2.0f - (RS_Y / 4.0f)) / 2.0f; // OK?
        } else if (L2 != 0.0f) {
            scale = (2.0f - (L2 / 4.0f)) / 2.0f; // OK?
        } else if (R2 != 0.0f) {
            scale = (2.0f + (R2 / 4.0f)) / 2.0f; // OK?
        }
        mZoom = scale;

        return true;
    }
}
