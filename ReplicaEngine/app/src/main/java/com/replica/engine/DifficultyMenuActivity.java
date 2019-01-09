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
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public abstract class DifficultyMenuActivity extends Activity {
    private ImageButton mEasyButton;
    private ImageButton mNormalButton;
    private ImageButton mHardButton;
    private View mBackground;
    private View mEasyText;
    private View mNormalText;
    private View mHardText;
    private Animation mButtonFlickerAnimation;
    private Animation mFadeOutAnimation;
    private Animation mAlternateFadeOutAnimation;

    private View.OnClickListener sEasyButtonListener;
    private View.OnClickListener sNormalButtonListener;
    private View.OnClickListener sHardButtonListener;


    private boolean mContinueMusic;
    private boolean mStopMusic;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = this.getApplicationContext();

        final String mainClassName = UtilsResources.getClassName(context, "class_name_main");

        setContentView(UtilsResources.getResourceIdByName(context, "layout", "difficulty_menu"));


        mBackground = findViewById(UtilsResources.getResourceIdByName(context, "id", "diffMenuBackground"));
        UtilsResources.startIfAnimatable(mBackground);

        mEasyButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "easyButton"));
        mEasyText = findViewById(UtilsResources.getResourceIdByName(context, "id", "easyText"));
        sEasyButtonListener = new View.OnClickListener() {
            public void onClick(View v) {
                mStopMusic = true;
                SoundManager.play(SoundManager.SOUND_START);

                Intent i = new Intent();
                i.setClassName(getBaseContext(), mainClassName);
                i.putExtras(getIntent());
                i.putExtra("difficulty", 0);

                v.startAnimation(mButtonFlickerAnimation);
                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
                mNormalButton.startAnimation(mAlternateFadeOutAnimation);
                mHardButton.startAnimation(mAlternateFadeOutAnimation);

                mEasyText.startAnimation(mAlternateFadeOutAnimation);
                mNormalText.startAnimation(mAlternateFadeOutAnimation);
                mHardText.startAnimation(mAlternateFadeOutAnimation);
            }
        };
        mEasyButton.setOnClickListener(sEasyButtonListener);

        mNormalButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "normalButton"));
        mNormalText = findViewById(UtilsResources.getResourceIdByName(context, "id", "normalText"));
        sNormalButtonListener = new View.OnClickListener() {
            public void onClick(View v) {
                mStopMusic = true;
                SoundManager.play(SoundManager.SOUND_START);

                Intent i = new Intent();
                i.setClassName(getBaseContext(), mainClassName);
                i.putExtras(getIntent());
                i.putExtra("difficulty", 1);

                v.startAnimation(mButtonFlickerAnimation);
                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
                mEasyButton.startAnimation(mAlternateFadeOutAnimation);
                mHardButton.startAnimation(mAlternateFadeOutAnimation);

                mEasyText.startAnimation(mAlternateFadeOutAnimation);
                mNormalText.startAnimation(mAlternateFadeOutAnimation);
                mHardText.startAnimation(mAlternateFadeOutAnimation);
            }
        };
        mNormalButton.setOnClickListener(sNormalButtonListener);

        mHardButton = ( ImageButton )findViewById( UtilsResources.getResourceIdByName( context, "id", "hardButton" ) );
        mHardText = findViewById( UtilsResources.getResourceIdByName( context, "id", "hardText" ) );
        sHardButtonListener = new View.OnClickListener() {
            public void onClick(View v) {
                mStopMusic = true;
                SoundManager.play(SoundManager.SOUND_START);

                Intent i = new Intent();
                i.setClassName(getBaseContext(), mainClassName);
                i.putExtras(getIntent());
                i.putExtra("difficulty", 2);

                v.startAnimation(mButtonFlickerAnimation);
                mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                mBackground.startAnimation(mFadeOutAnimation);
                mEasyButton.startAnimation(mAlternateFadeOutAnimation);
                mNormalButton.startAnimation(mAlternateFadeOutAnimation);

                mEasyText.startAnimation(mAlternateFadeOutAnimation);
                mNormalText.startAnimation(mAlternateFadeOutAnimation);
                mHardText.startAnimation(mAlternateFadeOutAnimation);
            }
        };
        mHardButton.setOnClickListener(sHardButtonListener);

        mButtonFlickerAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "button_flicker"));
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "fade_out"));
        mAlternateFadeOutAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "fade_out"));

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContinueMusic = false;
        mStopMusic = false;
        MusicManager.start(this, MusicManager.MUSIC_PREVIOUS); // music from MainMenu
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mContinueMusic) {
            if (mStopMusic) {
                MusicManager.stop();
            } else {
                MusicManager.pause();
            }
        }
    }

    // go back to Main Menu
    protected void goBack() {
        mContinueMusic = true;
        SoundManager.play(SoundManager.SOUND_CANCEL);

        finish();
        UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");
    }


    protected class StartActivityAfterAnimation implements Animation.AnimationListener {
        private Intent mIntent;

        StartActivityAfterAnimation(Intent intent) {
            mIntent = intent;
        }


        public void onAnimationEnd(Animation animation) {
            mEasyButton.setVisibility(View.INVISIBLE);
            mEasyButton.clearAnimation();
            mNormalButton.setVisibility(View.INVISIBLE);
            mNormalButton.clearAnimation();
            mHardButton.setVisibility(View.INVISIBLE);
            mHardButton.clearAnimation();
            startActivity(mIntent);
            finish();

            UtilsActivities.startTransition(DifficultyMenuActivity.this, "activity_fade_in", "activity_fade_out");
        }

        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub

        }

        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub

        }

    }

}
