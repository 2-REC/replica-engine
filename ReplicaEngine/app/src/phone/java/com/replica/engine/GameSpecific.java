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
import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Setup object for the phone game engine.
 */
public abstract class GameSpecific extends Game {
    private InputSystemImpl mInputSystem;
    protected TouchFilter mTouchFilter;

    public GameSpecific() {
        super();
        mInputSystem = null;
    }

    @Override
    public void bootstrap(Context context, int viewWidth, int viewHeight,
            int gameWidth, int gameHeight, int difficulty, int version) {
        if (!mBootstrapComplete) {
//            BaseObject.sSystemRegistry.vibrationSystem = new VibrationSystem();
        }
        super.bootstrap(context, viewWidth, viewHeight, gameWidth, gameHeight, difficulty, version);
    }

    @Override
    public boolean onOrientationEvent(float x, float y, float z) {
        if (mRunning) {
            BaseObject.sSystemRegistry.inputSystem.setOrientation(x, y, z);
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRunning) {
            mTouchFilter.updateTouch(event);
        }
        return true;
    }

    @Override
    public void setControlOptions(boolean tiltControls, int tiltSensitivity,
            int movementSensitivity, boolean onScreenControls) {
        // make sure there's at least 1 type of controls
        if (tiltControls == false) {
            onScreenControls = true;
        }
        super.setControlOptions(tiltControls, tiltSensitivity, movementSensitivity, onScreenControls);
    }

    @Override
    protected InputSystem getInputSystem(Context context) {
        if (mInputSystem == null) {
        	mInputSystem = new InputSystemImpl((Activity)context);
        }

        WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int rotationIndex = windowMgr.getDefaultDisplay().getRotation();
        mInputSystem.setScreenRotation(rotationIndex);

        if (mTouchFilter == null) {
            mTouchFilter = new TouchFilter();
        }

        return mInputSystem;
    }
}
