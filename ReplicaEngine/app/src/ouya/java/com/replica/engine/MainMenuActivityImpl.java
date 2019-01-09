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

//import tv.ouya.console.api.OuyaController;
import android.content.SharedPreferences;
//import android.view.KeyEvent;

public class MainMenuActivityImpl extends MainMenuActivity {
// !!!! TODO: TEST LIKE THIS & COMPARE WITH DIFFICULTY & PAUSE ACTIVITIES !!!!
// !!!! TODO: check if need to handle specific buttons or if ok like that !!!!
// => should have only "O" for OK, "A" for CANCEL, & "MENU" for quitting
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == OuyaController.BUTTON_O) {
            super.onKeyDown(keyCode, event);
        }
        return true;
    }
*/

//////////////// SETTINGS - MID
    protected void setInitialSettings(SharedPreferences prefs) {
        // default values for OUYA games => will only be set the first time, when they don't exist yet

        SharedPreferences.Editor prefsEditor = prefs.edit();

        // no tilt controls & no on-screen controls
/*
        final boolean onScreenControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, false);
        prefsEditor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, onScreenControls);

        final boolean tiltControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, false);
        prefsEditor.putBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, tiltControls);
*/
        prefsEditor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, false);
        prefsEditor.putBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, false);

//        final int movementSensitivity = prefs.getInt(PreferenceConstants.PREFERENCE_MOVEMENT_SENSITIVITY, 100);
//        prefsEditor.putInt(PreferenceConstants.PREFERENCE_MOVEMENT_SENSITIVITY, movementSensitivity);

//        final boolean flipControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_FLIP, false);

        prefsEditor.commit();
    }
//////////////// SETTINGS - END

}
