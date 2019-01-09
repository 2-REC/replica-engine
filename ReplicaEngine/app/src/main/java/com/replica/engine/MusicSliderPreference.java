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
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class MusicSliderPreference extends SliderPreference {

    private int mMusic;

    public MusicSliderPreference(Context context) {
        super(context);
        init();
    }

    public MusicSliderPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.preferenceStyle);
    }

    public MusicSliderPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mMusic = MusicManager.getResource(MusicManager.MUSIC_PREFERENCES);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser || mHack) {
            MusicManager.setTemporaryVolume(progress / 100.0f);
        }
        super.onProgressChanged(seekBar, progress, fromUser);
    }

    @Override
    public void onDependencyChanged(Preference dependency, boolean disableDependent) {
        if (disableDependent) {
            MusicManager.pause();
        } else {
            MusicManager.updateVolumeFromPrefs(getContext());
            if (mMusic > 0) {
                MusicManager.start(getContext(), MusicManager.MUSIC_PREFERENCES, true);
            } else {
                MusicManager.start(getContext(), MusicManager.MUSIC_PREVIOUS);
            }
        }
        super.onDependencyChanged(dependency, disableDependent);
    }

}
