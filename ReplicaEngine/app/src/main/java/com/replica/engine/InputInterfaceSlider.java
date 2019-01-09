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

// !!!! TODO: change this completely to be able to handle several sliders, onscreen controls or not, etc. !!!!

public abstract class InputInterfaceSlider {

//    protected static final float SLIDER_FILTER = 0.25f;

    protected InputXY mSlider = new InputXY();
    protected float mSensitivity = 1.0f;


    public InputInterfaceSlider() {
    }


// !!!! ???? TODO : OK ? ???? !!!!
// => "good" destructor ?
    public void destroy() {
        mSlider.release();
        mSlider = null;
    }

    public void reset() {
        mSlider.release();
    }

    public abstract void update(float timeDelta, float gameTime);

    public void setSensitivity(float sensitivity) {
        mSensitivity = sensitivity;
    }

    protected float deadZoneFilter(float magnitude, float min, float max, float scale) {
        float smoothedMagnitude = magnitude;
        if (Math.abs(magnitude) < min) {
            smoothedMagnitude = 0.0f; // dead zone
        } else if (Math.abs(magnitude) < max) {
            smoothedMagnitude *= scale;
        }
        return smoothedMagnitude;
    }

    public final InputXY getSlider() {
        return mSlider;
    }

}
