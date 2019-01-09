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

public class InputInterfaceDPadImpl extends InputInterfaceSlider {
    private InputSystemImpl mInputSystem;

    private boolean mUseLeft;
    private int mLeftTouchRegionX;
    private int mLeftTouchRegionY;
    private int mLeftTouchRegionWidth;
    private int mLeftTouchRegionHeight;
    private HudButton mLeftHudButton;
    private InputButton mLeftInputButton = new InputButton();;

    private boolean mUseRight;
    private int mRightTouchRegionX;
    private int mRightTouchRegionY;
    private int mRightTouchRegionWidth;
    private int mRightTouchRegionHeight;
    private HudButton mRightHudButton;
    private InputButton mRightInputButton = new InputButton();;

    private boolean mUseUp;
    private int mUpTouchRegionX;
    private int mUpTouchRegionY;
    private int mUpTouchRegionWidth;
    private int mUpTouchRegionHeight;
    private HudButton mUpHudButton;
    private InputButton mUpInputButton = new InputButton();;

    private boolean mUseDown;
    private int mDownTouchRegionX;
    private int mDownTouchRegionY;
    private int mDownTouchRegionWidth;
    private int mDownTouchRegionHeight;
    private HudButton mDownHudButton;
    private InputButton mDownInputButton = new InputButton();;

    public InputInterfaceDPadImpl(InputSystemImpl inputSystem,
            HudButton leftButton, HudButton rightButton, HudButton upButton, HudButton downButton) {
        super();

        mInputSystem = inputSystem;

        mLeftHudButton = leftButton;
        mRightHudButton = rightButton;
        mUpHudButton = upButton;
        mDownHudButton = downButton;

        mUseLeft = (mLeftHudButton != null) ? true : false;
        mUseRight = (mRightHudButton != null) ? true : false;
        mUseUp = (mUpHudButton != null) ? true : false;
        mUseDown = (mDownHudButton != null) ? true : false;

        // force init at update
        mLeftTouchRegionWidth = 0;
        mRightTouchRegionWidth = 0;
        mUpTouchRegionWidth = 0;
        mDownTouchRegionWidth = 0;

        reset();
    }


// !!!! ???? TODO : OK ? ???? !!!!
// => "good" destructor ?
    @Override
    public void destroy() {
        super.destroy();

// !!!! ???? TODO: no need to destroy something ? ???? !!!!
        mLeftHudButton = null;
        mRightHudButton = null;
        mUpHudButton = null;
        mDownHudButton = null;

        mLeftInputButton = null;
        mRightInputButton = null;
        mUpInputButton = null;
        mDownInputButton = null;
    }

    @Override
    public void reset() {
        super.reset();

        if (mLeftInputButton != null)
            mLeftInputButton.release();
        if (mRightInputButton != null)
            mRightInputButton.release();
        if (mUpInputButton != null)
            mUpInputButton.release();
        if (mDownInputButton != null)
            mDownInputButton.release();
    }

    @Override
    public void update(float timeDelta, float gameTime) {
// !!!! ???? TODO: OK to test for only 1 control ? ???? !!!!
        if (mLeftTouchRegionWidth == 0) {
            mLeftTouchRegionWidth = -1; // make sure not to come back here

            if (mLeftHudButton != null) {
                mLeftTouchRegionX = (int)mLeftHudButton.getPosX();
                mLeftTouchRegionY = (int)mLeftHudButton.getPosY();
                mLeftTouchRegionWidth = (int)mLeftHudButton.getWidth();
                mLeftTouchRegionHeight = (int)mLeftHudButton.getHeight();
            }

            if (mRightHudButton != null) {
                mRightTouchRegionX = (int)mRightHudButton.getPosX();
                mRightTouchRegionY = (int)mRightHudButton.getPosY();
                mRightTouchRegionWidth = (int)mRightHudButton.getWidth();
                mRightTouchRegionHeight = (int)mRightHudButton.getHeight();
            }

            if (mUpHudButton != null) {
                mUpTouchRegionX = (int)mUpHudButton.getPosX();
                mUpTouchRegionY = (int)mUpHudButton.getPosY();
                mUpTouchRegionWidth = (int)mUpHudButton.getWidth();
                mUpTouchRegionHeight = (int)mUpHudButton.getHeight();
            }

            if (mDownHudButton != null) {
                mDownTouchRegionX = (int)mDownHudButton.getPosX();
                mDownTouchRegionY = (int)mDownHudButton.getPosY();
                mDownTouchRegionWidth = (int)mDownHudButton.getWidth();
                mDownTouchRegionHeight = (int)mDownHudButton.getHeight();
            }
        }


        final InputTouchScreen touch = mInputSystem.getTouchScreen();

// !!!! ???? TODO: OK here ? ???? !!!!
        mSlider.release();

        if (mUseLeft) {
            final InputXY leftTouch = touch.findPointerInRegion(
                    mLeftTouchRegionX, mLeftTouchRegionY, mLeftTouchRegionWidth, mLeftTouchRegionHeight);
            if (leftTouch != null) {
                if (!mLeftInputButton.getPressed()) {
                    mLeftInputButton.press(leftTouch.getLastPressedTime(), 1.0f);
                }
            } else {
            	mLeftInputButton.release();
            }
            mLeftHudButton.setState(mLeftInputButton.getPressed());
        }

        if (mUseRight) {
            final InputXY rightTouch = touch.findPointerInRegion(
                    mRightTouchRegionX, mRightTouchRegionY, mRightTouchRegionWidth, mRightTouchRegionHeight);
            if (rightTouch != null) {
                if (!mRightInputButton.getPressed()) {
                    mRightInputButton.press(rightTouch.getLastPressedTime(), 1.0f);
                }
            } else {
            	mRightInputButton.release();
            }
            mRightHudButton.setState(mRightInputButton.getPressed());
        }

        if (mUseUp) {
            final InputXY upTouch = touch.findPointerInRegion(
                    mUpTouchRegionX, mUpTouchRegionY, mUpTouchRegionWidth, mUpTouchRegionHeight);
            if (upTouch != null) {
                if (!mUpInputButton.getPressed()) {
                    mUpInputButton.press(upTouch.getLastPressedTime(), 1.0f);
                }
            } else {
            	mUpInputButton.release();
            }
            mUpHudButton.setState(mUpInputButton.getPressed());
        }

        if (mUseDown) {
            final InputXY downTouch = touch.findPointerInRegion(mDownTouchRegionX, mDownTouchRegionY, mDownTouchRegionWidth, mDownTouchRegionHeight);
            if (downTouch != null) {
                if (!mDownInputButton.getPressed()) {
                    mDownInputButton.press(downTouch.getLastPressedTime(), 1.0f);
                }
            } else {
            	mDownInputButton.release();
            }
            mDownHudButton.setState(mDownInputButton.getPressed());
        }

        float magnitudeX = 0.0f;
        if (mUseLeft || mUseRight) {
            magnitudeX = mRightInputButton.getMagnitude() - mLeftInputButton.getMagnitude();
        }

        float magnitudeY = 0.0f;
        if (mUseUp || mUseDown) {
// !!!! ???? TODO: need to reverse ? ???? !!!!
            magnitudeY = mUpInputButton.getMagnitude() - mDownInputButton.getMagnitude();
        }

// !!!! ???? TODO: OK to multiply by sensitivity here ? ???? !!!!
        mSlider.press(gameTime, magnitudeX * mSensitivity, magnitudeY * mSensitivity);
    }

    public void setTouchRegionSizeFactor(float factorX, float factorY) {
        if (mLeftHudButton != null) {
            if (factorX != 1.0f) {
                mLeftTouchRegionWidth = (int)(mLeftHudButton.getWidth() * factorX);

                mLeftTouchRegionX = (int)(mLeftHudButton.getPosX()
                        + (mLeftHudButton.getWidth() / 2.0f) - (mLeftTouchRegionWidth / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
                if (mLeftTouchRegionX < 0) {
                    mLeftTouchRegionX = 0;
                }
*/
            } else {
                mLeftTouchRegionX = (int)mLeftHudButton.getPosX();
                mLeftTouchRegionWidth = (int)mLeftHudButton.getWidth();
            }

            if (factorY != 1.0f) {
                mLeftTouchRegionHeight = (int)(mLeftHudButton.getHeight() * factorY);

                mLeftTouchRegionY = (int)(mLeftHudButton.getPosY()
                        + (mLeftHudButton.getHeight() / 2.0f) - (mLeftTouchRegionHeight / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
                if (mLeftTouchRegionY < 0) {
                    mLeftTouchRegionY = 0;
                }
*/
            } else {
                mLeftTouchRegionY = (int)(mLeftHudButton.getPosY());
                mLeftTouchRegionHeight = (int)( mLeftHudButton.getHeight());
            }
        }

        if (mRightHudButton != null) {
            if (factorX != 1.0f) {
                mRightTouchRegionWidth = (int)(mRightHudButton.getWidth() * factorX);

                mRightTouchRegionX = (int)(mRightHudButton.getPosX()
                        + (mRightHudButton.getWidth() / 2.0f) - (mRightTouchRegionWidth / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
                if (mRightTouchRegionX < 0) {
                    mRightTouchRegionX = 0;
                }
*/
            } else {
                mRightTouchRegionX = (int)mRightHudButton.getPosX();
                mRightTouchRegionWidth = (int)mRightHudButton.getWidth();
            }

            if (factorY != 1.0f) {
                mRightTouchRegionHeight = (int)(mRightHudButton.getHeight() * factorY);

                mRightTouchRegionY = (int)(mRightHudButton.getPosY()
                        + (mRightHudButton.getHeight() / 2.0f) - (mRightTouchRegionHeight / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
                if (mTouchRegionY < 0) {
                    mTouchRegionY = 0;
                }
*/
            } else {
                mRightTouchRegionY = (int)(mRightHudButton.getPosY());
                mRightTouchRegionHeight = (int)( mRightHudButton.getHeight());
            }
        }

        if (mUpHudButton != null) {
            if (factorX != 1.0f) {
                mUpTouchRegionWidth = (int)(mUpHudButton.getWidth() * factorX);

                mUpTouchRegionX = (int)(mUpHudButton.getPosX()
                        + (mUpHudButton.getWidth() / 2.0f) - (mUpTouchRegionWidth / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
                if (mUpTouchRegionX < 0) {
                    mUpTouchRegionX = 0;
                }
*/
            } else {
                mUpTouchRegionX = (int)mUpHudButton.getPosX();
                mUpTouchRegionWidth = (int)mUpHudButton.getWidth();
            }

            if (factorY != 1.0f) {
                mUpTouchRegionHeight = (int)(mUpHudButton.getHeight() * factorY);

                mUpTouchRegionY = (int)(mUpHudButton.getPosY()
                        + (mUpHudButton.getHeight() / 2.0f) - (mUpTouchRegionHeight / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
                if (mUpTouchRegionY < 0) {
                    mUpTouchRegionY = 0;
                }
*/
            } else {
                mUpTouchRegionY = (int)(mUpHudButton.getPosY());
                mUpTouchRegionHeight = (int)( mUpHudButton.getHeight());
            }
        }

        if (mDownHudButton != null) {
            if (factorX != 1.0f) {
                mDownTouchRegionWidth = (int)(mDownHudButton.getWidth() * factorX);

                mDownTouchRegionX = (int)(mDownHudButton.getPosX()
                        + (mDownHudButton.getWidth() / 2.0f) - (mDownTouchRegionWidth / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
                if (mDownTouchRegionX < 0) {
                    mDownTouchRegionX = 0;
                }
*/
            } else {
                mDownTouchRegionX = (int)mDownHudButton.getPosX();
                mDownTouchRegionWidth = (int)mDownHudButton.getWidth();
            }

            if (factorY != 1.0f) {
                mDownTouchRegionHeight = (int)(mDownHudButton.getHeight() * factorY);

                mDownTouchRegionY = (int)(mDownHudButton.getPosY()
                        + (mDownHudButton.getHeight() / 2.0f) - (mDownTouchRegionHeight / 2.0f));
// !!!! ???? TODO : useless ? ???? !!!!
/*
                if (mDownTouchRegionY < 0) {
                    mDownTouchRegionY = 0;
                }
*/
            } else {
                mDownTouchRegionY = (int)(mDownHudButton.getPosY());
                mDownTouchRegionHeight = (int)(mDownHudButton.getHeight());
            }
        }
    }

    public void setLeftDrawables(DrawableBitmap buttonEnabled,
            DrawableBitmap buttonDisabled, DrawableBitmap buttonDepressed) {
        mLeftHudButton.setDrawables(buttonEnabled, buttonDisabled, buttonDepressed);
    }

    public void setRightDrawables(DrawableBitmap buttonEnabled,
            DrawableBitmap buttonDisabled, DrawableBitmap buttonDepressed) {
        mRightHudButton.setDrawables(buttonEnabled, buttonDisabled, buttonDepressed);
    }

    public void setUpDrawables(DrawableBitmap buttonEnabled,
            DrawableBitmap buttonDisabled, DrawableBitmap buttonDepressed) {
        mUpHudButton.setDrawables( buttonEnabled, buttonDisabled, buttonDepressed);
    }

    public void setDownDrawables(DrawableBitmap buttonEnabled,
            DrawableBitmap buttonDisabled, DrawableBitmap buttonDepressed) {
        mDownHudButton.setDrawables( buttonEnabled, buttonDisabled, buttonDepressed );
    }

    public void show(boolean show) {
    	mLeftHudButton.show(show);
    	mRightHudButton.show(show);
    	mUpHudButton.show(show);
    	mDownHudButton.show(show);
    }

    public void use(boolean left, boolean right, boolean up, boolean down) {
        mUseLeft = false;
        mUseRight = false;
        mUseUp = false;
        mUseDown = false;

        if (mLeftHudButton != null) {
            mUseLeft = left;
            mLeftInputButton.release();
            mLeftHudButton.setState(false);
// !!!! ???? TODO: "show" here ? ???? !!!!
            mLeftHudButton.show(left);
        }

        if (mRightHudButton != null) {
            mUseRight = right;
            mRightInputButton.release();
            mRightHudButton.setState(false);
// !!!! ???? TODO: "show" here ? ???? !!!!
            mRightHudButton.show(right);
        }

        if (mUpHudButton != null) {
            mUseUp = up;
            mUpInputButton.release();
            mUpHudButton.setState(false);
// !!!! ???? TODO: "show" here ? ???? !!!!
            mUpHudButton.show(up);
        }

        if (mDownHudButton != null) {
            mUseDown = down;
            mDownInputButton.release();
            mDownHudButton.setState(false);
// !!!! ???? TODO: "show" here ? ???? !!!!
            mDownHudButton.show(down);
        }

        mSlider.release();
    }

}
