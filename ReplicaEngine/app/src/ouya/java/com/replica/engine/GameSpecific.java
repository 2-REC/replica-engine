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
import java.util.List;

/**
 * Setup object for the OUYA game engine.
 */
public abstract class GameSpecific extends Game {
    private InputSystemImpl mInputSystem;

    public GameSpecific() {
        super();
        mInputSystem = null;
    }

    @Override
    public void setControlOptions(boolean tiltControls, int tiltSensitivity,
            int movementSensitivity, boolean onScreenControls) {
        tiltControls = false; // no tilt control
        tiltSensitivity = 100;
        onScreenControls = false; // no on-screen controls
        super.setControlOptions(tiltControls, tiltSensitivity, movementSensitivity, onScreenControls);
    }

    @Override
    protected InputSystem getInputSystem(Context context) {
        if (mInputSystem == null) {
        	mInputSystem = new InputSystemImpl((Activity)context);
        }
        return mInputSystem;
    }


    public void setMovementSticksConfig(final boolean leftStick, final boolean rightStick) {
        ((InputGameInterfaceOuya)BaseObject.sSystemRegistry.inputGameInterface).setMovementSticks(leftStick, rightStick);
    }

    public void setMovementButtonsConfig(int leftButton, int rightButton, int upButton, int downButton) {
        ((InputGameInterfaceOuya)BaseObject.sSystemRegistry.inputGameInterface).setMovementButtons(leftButton,  rightButton,  upButton,  downButton);
    }

    public void setActionButtonsConfig(List<Controls.ButtonData> buttons) {
        ((InputGameInterfaceOuya)BaseObject.sSystemRegistry.inputGameInterface).setButtons(buttons);
    }
}
