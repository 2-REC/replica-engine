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

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public abstract class SetPreferencesActivity extends PreferenceActivity {
    private boolean mTopLevel;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private boolean mContinueMusic;
    private int mPrefsMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String appName = getString(UtilsResources.getResourceIdByName(getApplicationContext(), "string", "app_name"));
        mPrefs = getSharedPreferences(appName + PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        mTopLevel = true;
        loadHeadersFromResource(UtilsResources.getResourceIdByName(getApplicationContext(), "xml", "preferences"), target);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!mContinueMusic) {
            if (mPrefsMusic > 0) {
                MusicManager.stop();
            } else {
                MusicManager.pause();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mContinueMusic = false;
        mPrefsMusic = MusicManager.getResource(MusicManager.MUSIC_PREFERENCES);
        if (mPrefsMusic > 0) {
            MusicManager.start(this, MusicManager.MUSIC_PREFERENCES, true);
        } else {
            MusicManager.start(this, MusicManager.MUSIC_PREVIOUS);
        }
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        super.onHeaderClick(header, position);
        mContinueMusic = true;
    }

    protected void goBack() {
        SoundManager.play(SoundManager.SOUND_CANCEL);
        if (!mTopLevel || mPrefsMusic <= 0) {
            mContinueMusic = true;
        } else {
            mContinueMusic = false;
        }
    }

    public void childrenCalled() {
        mContinueMusic = true;
    }

    public SharedPreferences.Editor startPrefsEdit() {
        mEditor = mPrefs.edit();
        return mEditor;
    }

    public void stopPrefsEdit() {
        if (mEditor != null) {
            mEditor.commit();
            mEditor = null;
        }
    }

    public void removeSpecific(final String preference) {
        if (mEditor != null) {
            mEditor.remove(preference);
        }
    }


    /**
     *  Specify games preferences to delete.
     *  Add lines such as:
     *   removeSpecific( PreferenceConstantsSpecific.PREFERENCE_ID );
     */
    protected abstract void removeSpecifics();

}
