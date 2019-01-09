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

/**
 * Specific OUYA hack activity.
 * To solve problem of activities being killed when left.
 */
public class EndingActivity extends Activity {

    private static final int MAX_ANIMATIONS = 10;
    private static final int ACTIVITY_NONE = -1;
    private static final int ACTIVITY_QUIT = 0;
    private static final int ACTIVITY_ANIM = 1;

    private SharedPreferences.Editor mPrefsEditor;
    private int mState;
    private boolean mDone;
    private int[] mAnimsIds = new int[ MAX_ANIMATIONS ];
    private int mAnimsNb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();

        mAnimsNb = 0;
        final int endingRsc = UtilsResources.getResourceIdByName(context, "xml", "end");
        if (endingRsc != 0) {
            loadAnimations(endingRsc);
        }

        final String gameName = getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));
        final SharedPreferences prefs = getSharedPreferences(gameName + PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
        mPrefsEditor = prefs.edit();
        if (endingRsc == 0) {
            // hack to clean preferences
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_END_STATE);
            mState = ACTIVITY_NONE;
        } else {
            mState = prefs.getInt(PreferenceConstants.PREFERENCE_END_STATE, ACTIVITY_NONE);
        }

        mDone = false;
        if (mState == ACTIVITY_NONE) {
            mState = (mAnimsNb == 0) ? ACTIVITY_QUIT : ACTIVITY_ANIM;
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

    final private boolean startActivity(final int state, Intent intent) {
        boolean done = false;
        if (state == ACTIVITY_QUIT) {
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_END_STATE);
            mPrefsEditor.commit();

            finish();
            startFade();

            done = true;

        } else if (state > ACTIVITY_QUIT) {
            // start animation Activity
            final String packageName = getPackageName();

            final int animId = mAnimsIds[state - ACTIVITY_ANIM];

            if (intent == null) {
                intent = new Intent();

            } else {
                intent.removeExtra("animation");
            }

// !!!! ???? TODO: OK ? ???? !!!!
//            intent.setClassName(getBaseContext(), UtilsResources.getClassName(context, "class_name_animation_player"));
            intent.setClass(getBaseContext(), AnimationPlayerActivityImpl.class); //?
            intent.putExtra("animation", animId);

            final int newState = (state == mAnimsNb) ? ACTIVITY_QUIT : (state + 1);
            mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_END_STATE, newState);
            mPrefsEditor.commit();

            startActivityForResult(intent, newState);

            startFade();

            done = true;
/*
        } else { // animNb < ACTIVITY_QUIT
            //ERROR!!!!
*/
        }
        return done;
    }

    final private void startFade() {
        UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");
    }


    final private void addAnimation(final String animXmlFile) {
        if (mAnimsNb == MAX_ANIMATIONS) {
            return;
        }
        mAnimsIds[mAnimsNb] = UtilsResources.getResourceIdByName(getApplicationContext(), "xml", animXmlFile);
        ++mAnimsNb;
    }

    final private void loadAnimations(final int endFile) {
        XmlResourceParser parser = (this.getResources().getXml(endFile));

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

                        if (!("".equals(filename))) {
                            addAnimation(filename);
                        }
                    }
                }
                eventType = parser.next();
            }

        } catch (Exception e) {
            DebugLog.e("EndActivity", e.getStackTrace().toString());

        } finally {
            parser.close();
        }
    }

}
