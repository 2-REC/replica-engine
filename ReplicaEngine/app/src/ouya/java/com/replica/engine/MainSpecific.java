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

import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import tv.ouya.console.api.OuyaController;

public abstract class MainSpecific extends Main {

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
*/

    @Override
    protected void onResume() {
        super.onResume();

        // Preferences may have changed while we were paused
// => OR NOT, as not handling settings menu when playing ... (?)
//        SharedPreferences prefs = getSharedPreferences(GAME_NAME + PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);

//        mGame.setKeyConfig(leftKey, rightKey, jumpKey, attackKey);
    }


    @Override
    public boolean onGenericMotionEvent(final MotionEvent event) {
        if (!mGame.isPaused()) {
            mGame.onGenericMotionEvent(event);

////////
// !!!! ???? TODO: OK ? ???? !!!!
            final long time = System.currentTimeMillis();
            if (event.getAction() == MotionEvent.ACTION_MOVE &&
                    (time - mLastInputTime < 32)) {
                // Sleep so that the main thread doesn't get flooded with UI events
                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                    // No big deal if this sleep is interrupted
                }
                mGame.getRenderer().waitDrawingComplete();
            }
            mLastInputTime = time;
////////
        }
// !!!! ???? TODO: want to be able to unpause/quit and to skip dialogs with the touchpad ? ???? !!!!
// => could, but not very interesting (and could be problematic)
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == OuyaController.BUTTON_MENU) {
            result = super.onKeyDown(KeyEvent.KEYCODE_BACK,  event);
//////// PAUSE - MID
        } else if (mGame.isPaused()) {
//////// DIALOGS - MID
//////// RESUME - BEGIN
//            if (mPauseMenu.getVisibility() == View.VISIBLE) {
//////// RESUME - MID
            if (mIsPaused) {
//////// RESUME - END
                result = super.onKeyDown(keyCode,  event);
            } else {
//////// RESUME - BEGIN
//                if (mDialog.getVisibility() == View.VISIBLE) {
//////// RESUME - MID
// !!!! TODO: can have a crash if touching screen when dialog appears !!!!
// => "mText" is null in "mTv.getRemainingTime()"
//hack            if (mIsDialog) {
       	        if (mIsDialog && (mDialog.getVisibility() == View.VISIBLE)) {
//////// RESUME - END
                    if (keyCode == OuyaController.BUTTON_O) {
                        processDialog();

                        // Sleep so that the main thread doesn't get flooded with UI events
                        try {
                            Thread.sleep(32);
                        } catch (InterruptedException e) {
                            // No big deal if this sleep is interrupted
                        }
                    }
                }
            }
//////// DIALOGS - END
//////// PAUSE - END

        } else {
            result = mGame.onKeyDownEvent(keyCode);

            // Sleep so that the main thread doesn't get flooded with UI events
            try {
                Thread.sleep(32);
            } catch (InterruptedException e) {
                // No big deal if this sleep is interrupted
            }
        }
        return result;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean result = false;
// !!!! TODO: check if button corresponds to "BACK" or "MENU" or "HOME" ? !!!!
/*
?        if (keyCode == BACK | MENU | HOME) {
            result = super.onKeyUp(keyCode,  event);
        } else
*/
        {
            result = mGame.onKeyUpEvent(keyCode);

            // Sleep so that the main thread doesn't get flooded with UI events
            try {
                Thread.sleep(4);
            } catch (InterruptedException e) {
                // No big deal if this sleep is interrupted
            }
        }
        return result;
    }

    @Override
    protected void loadSpecificPrefsCtrl(SharedPreferences prefs) {
        final GameSpecific game = (GameSpecific)mGame;

        final boolean bLeftStick = prefs.getBoolean(PreferenceConstantsOuya.PREFERENCE_LEFT_STICK, true);
        final boolean bRightStick = prefs.getBoolean(PreferenceConstantsOuya.PREFERENCE_RIGHT_STICK, false);
        game.setMovementSticksConfig(bLeftStick, bRightStick);

// !!!! ???? TODO: should make it possible to use only subset of the 4 directions ? ???? !!!!
// (eg: only left & right)
        final int leftButton = prefs.getInt(PreferenceConstantsOuya.PREFERENCE_LEFT_BUTTON, -1);
        final int rightButton = prefs.getInt(PreferenceConstantsOuya.PREFERENCE_RIGHT_BUTTON, -1);
        final int upButton = prefs.getInt(PreferenceConstantsOuya.PREFERENCE_UP_BUTTON, -1);
        final int downButton = prefs.getInt(PreferenceConstantsOuya.PREFERENCE_DOWN_BUTTON, -1);
        game.setMovementButtonsConfig(leftButton, rightButton, upButton, downButton);

        Controls controls = new Controls(this);
        game.setActionButtonsConfig(controls.getButtons());
    }

}
