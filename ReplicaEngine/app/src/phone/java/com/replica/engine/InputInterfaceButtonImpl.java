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

public class InputInterfaceButtonImpl extends InputInterfaceButton {
    private InputSystemImpl mInputSystem;

    private int mTouchRegionX;
    private int mTouchRegionY;
    private int mTouchRegionWidth;
    private int mTouchRegionHeight;

    private HudButton mHudButton;

    public InputInterfaceButtonImpl(InputSystemImpl inputSystem, HudButton button) {
    	super();

        mHudButton = button;

        mInputSystem = inputSystem;

// !!!! TODO : no need to do that here, as values are not set yet !!!!
        mTouchRegionX = (int)mHudButton.getPosX();
        mTouchRegionY = (int)mHudButton.getPosY();
        mTouchRegionWidth = (int)mHudButton.getWidth();
        mTouchRegionHeight = (int)mHudButton.getHeight();

        reset();
    }

    @Override
    public void destroy() {
        super.destroy();

// !!!! ???? TODO: no need to destroy something ? ???? !!!!
        mHudButton = null;
    }

/*
    @Override
    public void reset() {
        super.reset();

    }
*/

    @Override
    public void update(float timeDelta, float gameTime) {
        final InputTouchScreen touch = mInputSystem.getTouchScreen();

        if (mTouchRegionWidth == 0) {
            mTouchRegionX = (int)mHudButton.getPosX();
            mTouchRegionY = (int)mHudButton.getPosY();
            mTouchRegionWidth = (int)mHudButton.getWidth();
            mTouchRegionHeight = (int)mHudButton.getHeight();
        }

        final InputXY touchButton = touch.findPointerInRegion(
                mTouchRegionX, mTouchRegionY,
                mTouchRegionWidth, mTouchRegionHeight);
        if (touchButton != null) {
            if (!mButton.getPressed()) {
                mButton.press(touchButton.getLastPressedTime(), 1.0f);
            }
        } else {
            mButton.release();
        }

        mHudButton.setState(mButton.getPressed());
    }


    public void setTouchRegionSizeFactor(float factorX, float factorY) {
        if (factorX != 1.0f) {
            mTouchRegionWidth = (int)(mHudButton.getWidth() * factorX);

            mTouchRegionX = (int)(mHudButton.getPosX()
                    + (mHudButton.getWidth() / 2.0f) - (mTouchRegionWidth / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
            if (mTouchRegionX < 0) {
                mTouchRegionX = 0;
            }
*/
        } else {
            mTouchRegionX = (int)mHudButton.getPosX();
            mTouchRegionWidth = (int)mHudButton.getWidth();
        }

        if (factorY != 1.0f) {
            mTouchRegionHeight = (int)(mHudButton.getHeight() * factorY);

            mTouchRegionY = (int)(mHudButton.getPosY()
                    + (mHudButton.getHeight() / 2.0f) - (mTouchRegionHeight / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
            if (mTouchRegionY < 0) {
                mTouchRegionY = 0;
            }
*/
        } else {
            mTouchRegionY = (int)mHudButton.getPosY();
            mTouchRegionHeight = (int)mHudButton.getHeight();
        }
    }

    public void setDrawables(DrawableBitmap buttonEnabled, DrawableBitmap buttonDisabled,
            DrawableBitmap buttonDepressed) {
        mHudButton.setDrawables(buttonEnabled, buttonDisabled, buttonDepressed);
    }

    public void show(boolean show) {
        mHudButton.show(show);
    }

}
