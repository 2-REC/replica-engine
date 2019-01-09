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
import android.media.MediaPlayer;

// !!!! TODO: add more error handling !!!!
//=> "setOnErrorListener(...)"

/**
 * Manages music in Activities, allowing to continue music between activity changes.
 */
public class MusicManager {
    public static final int MUSIC_PREVIOUS = -1;
    public static final int MUSIC_MENU = 0;
    public static final int MUSIC_PREFERENCES = 1;
    public static final int MUSIC_LEVEL_SELECT = 2;
    public static final int MUSIC_END_LEVEL = 3;
    public static final int MUSIC_DIE = 4;
    public static final int MUSIC_GAME_WIN = 5;
    public static final int MUSIC_GAME_LOSE = 6;
    public static final int MUSIC_ANIMATION = 7;
    public static final int MUSIC_GAME = 8;
//?    public static final int MUSIC_DIALOG = 9;
// !!!! ???? TODO: need more musics ? ???? !!!!
    public static final int MUSICS_NB = 9;

    private static int[] mResources = new int[ MUSICS_NB ];
    private static MediaPlayer[] players = new MediaPlayer[ MUSICS_NB ];
    private static int currentMusic = -1;
    private static int previousMusic = -1;

    public static float getMusicVolume(final Context context) {
        String appName = context.getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));
        SharedPreferences prefs = context.getSharedPreferences(appName + PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        boolean audioDisabled = prefs.getBoolean(PreferenceConstants.PREFERENCE_AUDIO_DISABLE, false);
        float volume = audioDisabled ? 0.0f : prefs.getInt(PreferenceConstants.PREFERENCE_MUSIC_VOLUME, 100) / 100.0f;
        return volume;
    }

    public static void setResource(final int music, final int resource) {
        if (music >= 0 && music < MusicManager.MUSICS_NB) {
            if (resource != mResources[music]) {
                mResources[music] = resource;

                final MediaPlayer mp = players[music];
                if (mp != null) {
                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                    mp.release();
                    players[music] = null;
                }
            }
        }
    }

    public static int getResource(final int music) {
        if (music >= 0 && music < MusicManager.MUSICS_NB) {
            return mResources[music];
        }
        return 0;
    }

    public static void start(final Context context, int music) {
        start(context, music, false);
    }

    public static void start(final Context context, int music, final boolean force) {
        if (!force && currentMusic > -1) {
            // already playing some music and not forced to change
            return;
        }
        if (music == MUSIC_PREVIOUS) {
            music = previousMusic;
        }
        if (currentMusic == music) {
            // already playing this music
            return;
        }
        if (currentMusic != -1) {
            previousMusic = currentMusic;
            // playing some other music, pause it and change
            pause();
        }
        currentMusic = music;
        if (currentMusic == -1) {
            return;
        }

        if (music < 0 || music >= MusicManager.MUSICS_NB) {
            return;
        }

        MediaPlayer mp = players[music];
        if (mp != null) {
            if (!mp.isPlaying()) {
                mp.start();
            }
        } else {
            if (mResources[music] == 0) {
                return;
            }

            mp = MediaPlayer.create(context, mResources[music]);
            if (mp == null) {
                //ERROR!
                DebugLog.e("MusicManager", "start: player was not created successfully");
            } else {
                players[music] = mp;
                float volume = getMusicVolume(context);
                mp.setVolume(volume, volume);

                try {
                    mp.setLooping(true);
                    if (volume != 0.0f) {
                        mp.start();
                    }
                } catch (Exception e) {
                    //ERROR!
                    DebugLog.e("MusicManager", e.getMessage(), e);
                }
            }
        }
    }

    public static void pause() {
        for (int i=0; i<MUSICS_NB; ++i) {
            final MediaPlayer mp = players[i];
            if (mp != null && mp.isPlaying()) {
                mp.pause();
            }
        }
        // previousMusic should always be something valid
        if (currentMusic != -1) {
            previousMusic = currentMusic;
        }
        currentMusic = -1;
    }

    public static void stop() {
        final int current = currentMusic;
        pause();
        if (current != -1) {
            final MediaPlayer mp = players[current];
            if (mp != null) {
                mp.seekTo(0);
            }
        }
    }

    public static void setCurrentAsPrevious() {
        // previousMusic should always be something valid
        if (currentMusic != -1) {
            previousMusic = currentMusic;
        }
    }

    public static void updateVolumeFromPrefs(Context context) {
        try {
            float volume = getMusicVolume(context);

            for (int i=0; i<MUSICS_NB; ++i) {
                final MediaPlayer mp = players[i];
                if (mp != null) {
// !!!! ???? TODO: stop music playback if sound = 0 ? ???? !!!!
                    mp.setVolume(volume, volume);
                }
            }

        } catch (Exception e) {
            //ERROR!
            DebugLog.e("MusicManager", e.getMessage(), e);
        }
    }

    public static void setTemporaryVolume(final float volume) {
        try {
            for (int i=0; i<MUSICS_NB; ++i) {
                final MediaPlayer mp = players[i];
                if (mp != null) {
                    mp.setVolume(volume, volume);
                }
            }
        } catch (Exception e) {
            //ERROR!
            DebugLog.e("MusicManager", e.getMessage(), e);
        }
    }

    public static void release() {
        for (int i=0; i<MUSICS_NB; ++i) {
            final MediaPlayer mp = players[i];
            if (mp != null) {
                try {
                    if (mp.isPlaying()) {
                        mp.stop();
                    }
                    mp.release();
                } catch (Exception e) {
                    //ERROR!
                    DebugLog.e("MusicManager", e.getMessage(), e);
                }
                players[i] = null;
            }
        }

        if (currentMusic != -1) {
            previousMusic = currentMusic;
        }
        currentMusic = -1;
    }

}
