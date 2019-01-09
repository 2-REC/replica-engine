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
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Manages sound at the global level.
 * Not in-game sounds.
 */
public class SoundManager {
    private static final int MAX_STREAMS = 8;

    public static final int SOUND_NONE = -1;
    public static final int SOUND_OK = 0;
    public static final int SOUND_CANCEL = 1;
    public static final int SOUND_MOVE = 2;
    public static final int SOUND_START = 3;
    public static final int SOUND_TEST = 4;
    public static final int MAX_SOUNDS = 6;

    private static SoundPool mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
    private static int[] mSounds = new int[MAX_SOUNDS];
    private static float mSoundVolume = 0.0f;

    public static float getSoundVolume(final Context context) {
        String appName = context.getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));
        SharedPreferences prefs = context.getSharedPreferences(appName + PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        boolean audioDisabled = prefs.getBoolean(PreferenceConstants.PREFERENCE_AUDIO_DISABLE, false);
        float volume = audioDisabled ? 0.0f : prefs.getInt(PreferenceConstants.PREFERENCE_SOUND_VOLUME, 100) / 100.0f;
        return volume;
    }

    public static void loadSound(final Context context, final int sound, final int resource) {
        // done here to make sure it's done
        mSoundVolume = getSoundVolume(context);

        if (sound >=0 && sound < MAX_SOUNDS) {
            unload(sound);

            if (resource > 0) {
                mSounds[sound] = mSoundPool.load(context, resource, 1);
            }
        }
    }


    public static int play(final int sound) {
        return play(sound, 1.0f);
    }

    public static int play(final int sound, final float rate) {
        if (mSoundVolume != 0.0f) {
            if (sound >= 0 && sound < MAX_SOUNDS && mSounds[sound] > 0) {
                return mSoundPool.play(mSounds[sound], mSoundVolume, mSoundVolume, 1, 0, rate);
            }
        }
        return -1;
    }

// !!!! TODO: should have a stop function to stop sounds immediately !!!!
// (should be able to specify if want a sound to be "exclusive" => stop all other sounds when start playing)
// (also to stop if have looping sounds ...)
/*
    public static final void stop(final int stream) {
        mSoundPool.stop(stream);
    }

    public final void stopAll() {
        Collection sounds = players.values();
        for (MediaPlayer p : mps) {
            if (p.isPlaying()) {
                stop(soundId);
            }
        }
        for (int x = 0; x < MAX_SOUNDS; ++x) {
            stop(mSounds[x]);
        }
    }
*/

    public static void updateVolumeFromPrefs(final Context context) {
        mSoundVolume = getSoundVolume(context);
// !!!! TODO: should stop playing sounds !!!!
    }

    public static void setTemporaryVolume(final float volume) {
        mSoundVolume = volume;
// !!!! TODO: should stop playing sounds !!!!
    }

    public static void unload(int sound) {
        final int soundId = mSounds[sound];
        if (soundId > 0) {
            mSoundPool.unload(soundId);
			mSounds[sound] = 0;
        }
    }

    public static void release() {
        for (int i=0; i<MAX_SOUNDS; ++i) {
            unload(i);
        }
        mSoundPool.release();
    }

}
