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

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.MotionEvent;

public class TouchFilter extends BaseObject {
    private boolean mCheckedForMultitouch = false;
    private boolean mSupportsMultitouch = false;

    public void updateTouch(MotionEvent event) {
        ContextParameters params = sSystemRegistry.contextParameters;

        final int pointerCount = event.getPointerCount();
        for (int x = 0; x < pointerCount; x++) {
            final int action = event.getAction();
            final int actualEvent = action & MotionEvent.ACTION_MASK;
            final int id = event.getPointerId(x);

            if (actualEvent == MotionEvent.ACTION_POINTER_UP ||
                    actualEvent == MotionEvent.ACTION_UP ||
                    actualEvent == MotionEvent.ACTION_CANCEL) {
                BaseObject.sSystemRegistry.inputSystem.touchUp(id,
                        event.getX(x) * (1.0f / params.viewScaleX),
                        event.getY(x) * (1.0f / params.viewScaleY));
            } else {
                BaseObject.sSystemRegistry.inputSystem.touchDown(id,
                        event.getX(x) * (1.0f / params.viewScaleX),
                        event.getY(x) * (1.0f / params.viewScaleY));
            }
        }
    }

    public boolean supportsMultitouch(Context context) {
        if (!mCheckedForMultitouch) {
            PackageManager packageManager = context.getPackageManager();
            mSupportsMultitouch = packageManager.hasSystemFeature("android.hardware.touchscreen.multitouch");
            mCheckedForMultitouch = true;
        }
        return mSupportsMultitouch;
    }

    @Override
    public void reset() {

    }

}
