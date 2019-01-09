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

import java.lang.reflect.InvocationTargetException;
import android.app.Activity;

public class UtilsActivities {

    public final static void startTransition(final Activity callingActivity,
            final String animIn, final String animOut) {
        if (UIConstants.mOverridePendingTransition != null) {
            final int fadeInId = UtilsResources.getResourceIdByName(callingActivity.getApplicationContext(), "anim", animIn);
            final int fadeOutId = UtilsResources.getResourceIdByName(callingActivity.getApplicationContext(), "anim", animOut);

            try {
                UIConstants.mOverridePendingTransition.invoke(callingActivity, fadeInId, fadeOutId);
            } catch (InvocationTargetException ite) {
                DebugLog.d("Activity Transition", "Invocation Target Exception");
            } catch (IllegalAccessException ie) {
                DebugLog.d("Activity Transition", "Illegal Access Exception");
            }
        }
    }

}
