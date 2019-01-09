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

import java.util.ArrayList;
import java.util.List;

/**
 * Input implementation for OUYA.
 */
public abstract class InputGameInterfaceOuya extends InputGameInterface {
    private InputInterfaceSliderImpl mMovementPad;
    private List<InputInterfaceButtonImpl> mButtons;

    public InputGameInterfaceOuya() {
        super();

        final InputSystemImpl input = (InputSystemImpl)sSystemRegistry.inputSystem;
        assert input != null;

        mSupportOrientation = false;
        mUseOrientation = false;
        mOrientationSensitivity = 1.0f;

        mSupportOnScreenControls = true;
        mUseOnScreenControls = true;
        mMovementSensitivity = 1.0f;

        mMovementPad = new InputInterfaceSliderImpl(input);
        mButtons = new ArrayList<InputInterfaceButtonImpl>();

        reset();
    }

    @Override
    public void reset() {
        mMovementPad.reset();
        for (int i = 0; i < mButtons.size(); ++i) {
            mButtons.get(i).reset();
        }
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        final float gameTime = sSystemRegistry.timeSystem.getGameTime();

        mMovementPad.update(timeDelta, gameTime);
        for (int i = 0; i < mButtons.size(); ++i) {
            mButtons.get(i).update(timeDelta, gameTime);
        }
    }

    @Override
    public void setMovementSensitivity(float sensitivity) {
        super.setMovementSensitivity(sensitivity);
        mMovementPad.setSensitivity(sensitivity);
    }

    @Override
    public final InputXY getMovementPad() {
        return mMovementPad.getSlider();
    }


    public void setMovementSticks(final boolean leftStick, final boolean rightStick) {
        mMovementPad.setSticks(leftStick, rightStick);
    }

    public void setMovementButtons(final int left, final int right, final int up, final int down) {
        if (left == -1 || right == -1 || up == -1 || down == -1) {
            cancelMovementButtons();
        } else {
            mMovementPad.setButtons(left, right, up, down);
        }
    }

    public void cancelMovementButtons() {
    	mMovementPad.cancelButtons();
    }

    public void setButtons(List<Controls.ButtonData> buttons) {
        mButtons.clear();
        final InputSystemImpl input = (InputSystemImpl)sSystemRegistry.inputSystem;
        for (Controls.ButtonData button : buttons) {
            mButtons.add(new InputInterfaceButtonImpl(input, button.currentButton));
        }
    }

    public final InputButton getButton(final int key) {
        return mButtons.get(key).getButton();
    }
}
