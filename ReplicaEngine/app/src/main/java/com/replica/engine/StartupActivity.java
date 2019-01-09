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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.media.AudioManager;
import android.os.Bundle;
import org.xmlpull.v1.XmlPullParser;

public class StartupActivity extends Activity {

    private static final int MAX_ANIMATIONS = 10;
    private static final int ACTIVITY_NONE = -1;
    private static final int ACTIVITY_MAIN = 0;
    private static final int ACTIVITY_ANIM = 1;

    private Context context;
    private SharedPreferences.Editor mPrefsEditor;
    private int mState;
    private boolean mDone;
    private int[] mAnimsIds = new int[ MAX_ANIMATIONS ];
    private int mAnimsNb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        mAnimsNb = 0;
        final int initRsc = UtilsResources.getResourceIdByName(context, "xml", "init");
        if (initRsc != 0) {
            loadResources(initRsc);
        }

        final String gameName = getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));
        final SharedPreferences prefs = getSharedPreferences(gameName + PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
        mPrefsEditor = prefs.edit(); //move inside "if", & need commit?
        if (initRsc == 0) {
            // hack to clean preferences
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_MAIN_STATE);
            mState = ACTIVITY_NONE;
        } else {
            mState = prefs.getInt(PreferenceConstants.PREFERENCE_MAIN_STATE, ACTIVITY_NONE);
        }

        mDone = false;
        if (mState == ACTIVITY_NONE) {
            mState = (mAnimsNb == 0) ? ACTIVITY_MAIN : ACTIVITY_ANIM;
            mDone = startActivity(mState, null);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mDone = startActivity(requestCode, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mDone) {
            mDone = startActivity(mState, null);
        }
    }

    private boolean startActivity(final int state, Intent intent) {
        boolean done = false;
        if (state == ACTIVITY_MAIN) {
            // start Main Menu Activity
            if (intent == null) {
                intent = new Intent();
            } else {
                intent.removeExtra("animation");
            }

            final String mainMenuClassName = UtilsResources.getClassName(getApplicationContext(), "class_name_main_menu");
            intent.setClassName(context, mainMenuClassName);

            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_MAIN_STATE);
            mPrefsEditor.commit();

            startActivity(intent);
            finish();

            UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");

            done = true;

        } else if (state > ACTIVITY_MAIN) {
            // start animation Activity
            final int animId = mAnimsIds[state - ACTIVITY_ANIM];

            if (intent == null) {
                intent = new Intent();
            } else {
                intent.removeExtra("animation");
            }

// !!!! ???? TODO: OK ? ???? !!!!
/*
            final String animationPlayerClassName = UtilsResources.getClassName(context, "class_name_animation_player");
            intent.setClassName(context, animationPlayerClassName);
*/
            intent.setClass(this, AnimationPlayerActivityImpl.class);
            intent.putExtra("animation", animId);

            final int newState = state == mAnimsNb ? ACTIVITY_MAIN : state + 1;
            mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_MAIN_STATE, newState);
            mPrefsEditor.commit();

            startActivityForResult(intent, newState);

            UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");

            done = true;
/*
        } else { // animNb < ACTIVITY_MAIN
            //ERROR!!!!
*/
        }

        return done;
    }


    private void addAnimation(final String animXmlFile) {
        if (mAnimsNb == MAX_ANIMATIONS) {
            return;
        }
        mAnimsIds[mAnimsNb] = UtilsResources.getResourceIdByName(getApplicationContext(), "xml", animXmlFile);
        ++mAnimsNb;
    }

    private void addMusic(final int musicId, final String musicFile) {
        MusicManager.setResource(musicId, UtilsResources.getResourceIdByName(getApplicationContext(), "raw", musicFile));
    }

    private void addSound(final int soundId, final String soundFile) {
    	SoundManager.loadSound(this, soundId, UtilsResources.getResourceIdByName(getApplicationContext(), "raw", soundFile));
    }

    private void loadResources(final int initFile) {
        XmlResourceParser parser = this.getResources().getXml(initFile);

        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("animation")) {
                        String filename = "";

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("filename")) {
                                filename = parser.getAttributeValue(i);
                                break;
                            }
                        }

                        if (!( "".equals(filename))) {
                            addAnimation(filename);
                        }

                    } else if (parser.getName().equals("music")) {
                        int id = -1;
                        String filename = "";

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("id")) {
                                id = parser.getAttributeIntValue(i, -1);
                            } else if ( parser.getAttributeName(i).equals("filename")) {
                                filename = parser.getAttributeValue(i);
                            }
                        }

                        if (id != -1 && !"".equals(filename)) {
                            addMusic(id, filename);
                        }
                    } else if (parser.getName().equals("sound")) {
                        int id = -1;
                        String filename = "";

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("id")) {
                                id = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("filename")) {
                                filename = parser.getAttributeValue(i);
                            }
                        }

                        if (id != -1 && !"".equals(filename)) {
                            addSound(id, filename);
                        }
                    }
                }
                eventType = parser.next();
            }

        } catch (Exception e) {
            DebugLog.e("StartupActivity", e.getStackTrace().toString());
        } finally {
            parser.close();
        }
    }

}
