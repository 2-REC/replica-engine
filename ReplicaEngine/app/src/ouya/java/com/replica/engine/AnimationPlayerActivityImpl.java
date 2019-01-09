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

/*
import tv.ouya.console.api.OuyaController;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;
*/

public class AnimationPlayerActivityImpl extends AnimationPlayerActivity {
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == OuyaController.BUTTON_MENU) {
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == OuyaController.BUTTON_A) {
            finish();
//            return super.onKeyDown(KeyEvent.KEYCODE_BACK, event);
        } else if (keyCode == OuyaController.BUTTON_O) {
            long time = System.currentTimeMillis();
            if (time > mAnimationEndTime) {
                finish();
            } else {
                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
*/
/*
// !!!! TODO: test if A (BACK) button works ... !!!!
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == OuyaController.BUTTON_MENU) {
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == OuyaController.BUTTON_A) {
            return super.onKeyDown(KeyEvent.KEYCODE_BACK, event);
        }
//        return super.onKeyDown(keyCode, event);
        return true;
    }
*/
/*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == OuyaController.BUTTON_A) {
                return super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
            }
        }
        return super.dispatchKeyEvent(event);
    }
*/
/*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == OuyaController.BUTTON_A) {
Toast.makeText(this, "PRESSED A", Toast.LENGTH_SHORT).show();
//                return super.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                return super.dispatchTouchEvent(MotionEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), 1.0f, 1.0f, 0));
            }
        }
        return super.dispatchKeyEvent(event);
    }
*/
}
