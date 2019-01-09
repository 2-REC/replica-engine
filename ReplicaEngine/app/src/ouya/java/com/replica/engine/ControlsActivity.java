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
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.List;
import tv.ouya.console.api.OuyaController;

/**
 * PreferencesActivity to configure controls on OUYA.
 */
public class ControlsActivity extends PreferenceActivity {
    private ButtonsPreferenceFragment preferencesFragment;
    private ImageButton defaultButton;
    private View.OnClickListener defaultButtonListener;
    private boolean continueMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(UtilsResources.getResourceIdByName(getApplicationContext(), "layout", "preferences_controls"));

        defaultButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(getApplicationContext(), "id", "defaultButton"));
        if (defaultButton != null) {
            defaultButtonListener = new View.OnClickListener() {
                public void onClick(View v) {
                    if (preferencesFragment != null) {
                        preferencesFragment.setDefaults();
                    }
                }
            };
            defaultButton.setOnClickListener(defaultButtonListener);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();

        continueMusic = false;
        MusicManager.start(this, MusicManager.MUSIC_PREVIOUS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!continueMusic) {
            MusicManager.pause();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == OuyaController.BUTTON_A || keyCode == KeyEvent.KEYCODE_BACK) {
            if (preferencesFragment != null && !preferencesFragment.checkButtons()) {
                Toast.makeText(this,
                        UtilsResources.getResourceIdByName(getApplicationContext(), "string", "preference_controls_unfinished"),
                        Toast.LENGTH_SHORT).show();

                return true;
            }

            finish();
            return true;
        }
        return false;
    }

// !!!! ???? TODO: ignore all keyUp ? ???? !!!!
/*
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return true;
    }
*/

    protected void goBack() {
        continueMusic = true;
        SoundManager.play(SoundManager.SOUND_CANCEL);

        finish();
        UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");
    }

    private void setControlsFragment(final ButtonsPreferenceFragment fragment) {
        preferencesFragment = fragment;
    }

    ////////////////////////////////////////////////////////////////

    public static class ButtonsPreferenceFragment extends PreferenceFragment
                                                  implements ButtonDialogPreference.ButtonDialogListener {
        Controls controls;
        PreferenceCategory category;

        public static final ButtonsPreferenceFragment newInstance() {
            return new ButtonsPreferenceFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            final ControlsActivity activity = (ControlsActivity)getActivity();
            activity.setControlsFragment(this);

            final String appName = getString(UtilsResources.getResourceIdByName(activity.getApplicationContext(), "string", "app_name"));
            getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
            getPreferenceManager().setSharedPreferencesName(appName + PreferenceConstants.PREFERENCE_NAME);

            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(activity);
            category = new PreferenceCategory(activity);
            //category.setTitle("buttons");
            screen.addPreference(category);

            controls = new Controls(activity);
            addPreferences();
            setPreferenceScreen(screen);
        }

        @Override
        public void onDialogClosed(String key, int value) {
            Controls.ButtonData button = controls.getButton(key);
            if (button.currentButton != value) {
                button.currentButton = value;
                category.findPreference(key).setSummary(Controls.convertKeyCode(value));
                if (controls.removeDuplicates(button)) {
                    category.removeAll();
                    addPreferences();
                }
            }
	    }

        private void addPreferences() {
            List<Controls.ButtonData> buttons = controls.getButtons();
            for (Controls.ButtonData button : buttons) {
                ButtonDialogPreference pref = new ButtonDialogPreference(getActivity());
                pref.setKey(button.key);
                pref.setTitle(button.name);
                pref.setSummary(Controls.convertKeyCode(button.currentButton));
// !!!! TODO: ... !!!!
                pref.setDialogTitle("Press a button");
                pref.setDialogMessage("Current: ");

                ButtonDialogPreference buttonDialog = (ButtonDialogPreference)pref;
                buttonDialog.setListener(this);
                category.addPreference(pref);
            }
        }

        public void setDefaults() {
            category.removeAll();
            controls.setDefaults();
            addPreferences();
        }

        public boolean checkButtons() {
            return controls.checkButtons();
        }

    }

}
