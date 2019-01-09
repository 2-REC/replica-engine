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

import android.app.Activity;
import android.view.MotionEvent;
import tv.ouya.console.api.OuyaController;

// !!!! ???? TODO: remove or keep keyboard stuff ? ???? !!!!

/**
 * Manages input from touch screen.
 * Reduces frequent UI messages to an average direction over a short period of time.
 */
public class InputSystemImpl extends InputSystem {
    private static final float STICK_DEADZONE_2 = OuyaController.STICK_DEADZONE * OuyaController.STICK_DEADZONE;
// !!!! ???? TODO: what value ? ???? !!!!
    private static final float TRIGGER_DEADZONE = 0.1f; //0.25f
///	OuyaController mOuyaController;
    private InputKeyboard mButtons = new InputKeyboard();
    private InputXY mLeftStick = new InputXY();
    private InputXY mRightStick = new InputXY();
    private InputButton mL2 = new InputButton();
    private InputButton mR2 = new InputButton();
// !!!! ???? TODO: handle touchpad ? ???? !!!!
//    private InputXY mTouchPad = new InputXY();

    public InputSystemImpl(Activity activity) {
        super(activity);

// !!!! ???? TODO: need this ? ???? !!!!
///        OuyaController.init(activity);

/*
/////        mOuyaController = OuyaController.getControllerByDeviceId( deviceId );
///?        mOuyaController = OuyaController.getControllerByPlayer( 1 );
*/
        reset();
    }

    @Override
    public void reset() {
        super.reset();

///?        mOuyaController.reset();

        mButtons.resetAll();
        mLeftStick.reset();
        mRightStick.reset();
        mL2.reset();
        mR2.reset();
    }

    @Override
    public void releaseAllKeys() {
///?        mOuyaController.release();

        mButtons.releaseAll();
        mLeftStick.release();
        mRightStick.release();
        mL2.release();
        mR2.release();
    }

    @Override
    public void motion(final MotionEvent event) {
/*
/////        OuyaController.onGenericMotionEvent(event);
///        mOuyaController.onGenericMotionEvent(event);
*/

        TimeSystem time = sSystemRegistry.timeSystem;


        // Get the player #
//!        final int player = OuyaController.getPlayerNumByDeviceId(event.getDeviceId());
//        final int player = 1;

        // Get all the axis for the event
        float LS_X = event.getAxisValue(OuyaController.AXIS_LS_X);
        float LS_Y = event.getAxisValue(OuyaController.AXIS_LS_Y);
        if (((LS_X * LS_X) + (LS_Y * LS_Y)) < STICK_DEADZONE_2) {
//            LS_X = LS_Y = 0.0f;
            mLeftStick.release();
        } else {
            LS_X = Math.min(LS_X, 1.0f);
            LS_X = Math.max(LS_X, -1.0f);

            LS_Y = Math.min(LS_Y, 1.0f);
            LS_Y = Math.max(LS_Y, -1.0f);

            mLeftStick.press(time.getGameTime(), LS_X, -LS_Y);
        }

        float RS_X = event.getAxisValue(OuyaController.AXIS_RS_X);
        float RS_Y = event.getAxisValue(OuyaController.AXIS_RS_Y);
        if (((RS_X * RS_X) + (RS_Y * RS_Y)) < STICK_DEADZONE_2) {
//            RS_X = RS_Y = 0.0f;
            mRightStick.release();
        } else {
            RS_X = Math.min(RS_X, 1.0f);
            RS_X = Math.max(RS_X, -1.0f);

            RS_Y = Math.min(RS_Y, 1.0f);
            RS_Y = Math.max(RS_Y, -1.0f);

            mRightStick.press(time.getGameTime(), RS_X, -RS_Y);
        }

        float L2 = event.getAxisValue(OuyaController.AXIS_L2);
        if (L2 < TRIGGER_DEADZONE) {
//            L2 = 0.0f;
            mL2.release();
        } else {
            L2 = Math.min(L2, 1.0f);
            mL2.press(time.getGameTime(), L2);
        }

        float R2 = event.getAxisValue(OuyaController.AXIS_R2);
        R2 = Math.min( R2, 1.0f );
        if (R2 < TRIGGER_DEADZONE) {
//            R2 = 0.0f;
            mL2.release();
        } else {
            mL2.press(time.getGameTime(), R2);
        }

    }

    @Override
    public void keyDown(int keyCode) {
///        mOuyaController.onKeyDown(keyCode, event);

//event.getButton( OuyaController.BUTTON_O );
        TimeSystem time = sSystemRegistry.timeSystem;
        final float gameTime = time.getGameTime();
        mButtons.press(gameTime, keyCode);
    }

    @Override
    public void keyUp(int keyCode) {
///        mOuyaController.onKeyUp(keyCode, event);

        mButtons.release(keyCode);
    }

    public InputKeyboard getButtons() {
        return mButtons;
    }

    public InputXY getLeftStick() {
        return mLeftStick;
    }

    public InputXY getRightStick() {
        return mRightStick;
    }

    public InputButton getL2() {
        return mL2;
    }

    public InputButton getR2() {
        return mR2;
    }
}
