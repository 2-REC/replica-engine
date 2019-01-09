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

import android.content.SharedPreferences;
//////// NEW_SOUND - BEGIN
//import android.view.KeyEvent;
//////// NEW_SOUND - MID

public class MainMenuActivityImpl extends MainMenuActivity {
//////// NEW_SOUND - BEGIN
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
            super.onKeyDown(keyCode, event);
        }
        return true;
    }
*/
//////// NEW_SOUND - MID

//////////////// SETTINGS - MID
    protected void setInitialSettings(SharedPreferences prefs) {
        // default values for phone games => will only be set the first time, when they don't exist yet

        SharedPreferences.Editor prefsEditor = prefs.edit();

        // if multi-touch is not handled, disable on-screen controls
        TouchFilter touch = new TouchFilter();
        if ( !touch.supportsMultitouch(this)) {
        	prefsEditor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, false);

            // turn on tilt controls if there's nothing else
        	prefsEditor.putBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, true);
//            mSelectedControlsString = getString(R.string.control_setup_dialog_tilt);
        }

// not necessary here ...
/*
        final int movementSensitivity = prefs.getInt(PreferenceConstants.PREFERENCE_MOVEMENT_SENSITIVITY, 100);
        prefsEditor.putInt(PreferenceConstants.PREFERENCE_MOVEMENT_SENSITIVITY, movementSensitivity);

        final boolean onScreenControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, true);
        prefsEditor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, onScreenControls);

        final boolean tiltControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, false);
        prefsEditor.putBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, tiltControls);

        final int tiltSensitivity = prefs.getInt(PreferenceConstants.PREFERENCE_TILT_SENSITIVITY, 50);
        prefsEditor.putInt(PreferenceConstants.PREFERENCE_TILT_SENSITIVITY, tiltSensitivity);

//        final boolean flipControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_FLIP, false);

*/

        prefsEditor.commit();
    }
//////////////// SETTINGS - END

}
