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

/** 
 * Manages input from a roller wheel and touch screen.  Reduces frequent UI messages to
 * an average direction over a short period of time.
 */
public abstract class InputSystem extends BaseObject {
               
    public InputSystem(Activity activity) {
        super();
    }
    
    @Override
    public void reset() {
    }

    public abstract void releaseAllKeys();
    
    public void touchDown(int index, float x, float y) {
    }
    
    public void touchUp(int index, float x, float y) {
    }
    
    
    public void setOrientation(float x, float y, float z) {
    }

    public void keyDown(int keyCode) {
    }
    
    public void keyUp(int keyCode) {
    }

    public void motion(MotionEvent event) {
    }
    
}
