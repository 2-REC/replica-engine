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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class PreferencesFragmentGeneral extends PreferenceFragment
        implements YesNoDialogPreference.YesNoDialogListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getActivity().getApplicationContext();
        final String appName = getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));

        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        getPreferenceManager().setSharedPreferencesName(appName + PreferenceConstants.PREFERENCE_NAME);

        addPreferencesFromResource(UtilsResources.getResourceIdByName(context, "xml", "preferences_general"));

        Preference eraseGameButton = getPreferenceManager().findPreference("eraseGame");
        if (eraseGameButton != null) {
            YesNoDialogPreference yesNo = (YesNoDialogPreference)eraseGameButton;
            yesNo.setListener(this);
        }
    }

    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SetPreferencesActivity activity = (SetPreferencesActivity)getActivity();
            SharedPreferences.Editor editor = activity.startPrefsEdit();

            editor.remove(PreferenceConstants.PREFERENCE_CONTINUE);
            editor.remove(PreferenceConstants.PREFERENCE_LEVEL_ROW);
            editor.remove(PreferenceConstants.PREFERENCE_LEVEL_INDEX);
            editor.remove(PreferenceConstants.PREFERENCE_LEVEL_COMPLETED);
            editor.remove(PreferenceConstants.PREFERENCE_LINEAR_MODE);
            editor.remove(PreferenceConstants.PREFERENCE_TOTAL_GAME_TIME);
            editor.remove(PreferenceConstants.PREFERENCE_DIFFICULTY);

            activity.removeSpecifics();

            activity.stopPrefsEdit();

            Toast.makeText(activity,
                    UtilsResources.getResourceIdByName(activity.getApplicationContext(), "string", "preference_erase_notification"),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
