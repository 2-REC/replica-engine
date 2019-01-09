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
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public abstract class MainMenuActivity extends Activity {
//    private final static int WHATS_NEW_DIALOG = 0;
//    private final static int TILT_TO_SCREEN_CONTROLS_DIALOG = 1;
//    private final static int CONTROL_SETUP_DIALOG = 2;

    private boolean mPaused;
    private ImageButton mStartButton;
    private ImageButton mContinueButton;
    private ImageButton mSettingsButton;
//    private ImageButton mExtrasButton;
    private View mBackground;
//    private View mTicker;
    private Animation mButtonFlickerAnimation;
    private Animation mFadeOutAnimation;
    private Animation mAlternateFadeOutAnimation;
//    private Animation mFadeInAnimation;
    private boolean mJustCreated;
//    private String mSelectedControlsString;

    private View.OnClickListener sStartButtonListener;
    private View.OnClickListener sContinueButtonListener;
    private View.OnClickListener sSettingsButtonListener;
//    private View.OnClickListener sExtrasButtonListener;

    private boolean mContinueMusic;
    private boolean mStopMusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = this.getApplicationContext();

        setContentView(UtilsResources.getResourceIdByName(context, "layout", "main_menu"));
        mPaused = true;

        mBackground = findViewById(UtilsResources.getResourceIdByName(context, "id", "mainMenuBackground"));
        UtilsResources.startIfAnimatable(mBackground);

        View title = findViewById(UtilsResources.getResourceIdByName(context, "id", "mainMenuTitle"));
        UtilsResources.startIfAnimatable(title);

        mStartButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "startButton"));
        mContinueButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "continueButton"));
        if (mContinueButton != null) {
            mContinueButton.setVisibility(View.GONE);
        }
        mSettingsButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "settingsButton"));
//        mExtrasButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "extrasButton"));


        sStartButtonListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (!mPaused) {
                    mContinueMusic = true;
                    SoundManager.play(SoundManager.SOUND_OK);

// !!!! ???? TODO: OK ? ???? !!!!
/*
                    final String difficultyMenuImplClassName = UtilsResources.getClassName(context, "class_name_difficulty_menu");
                    Intent i = new Intent();
                    i.setClassName(context, difficultyMenuImplClassName);
*/
                    Intent i = new Intent(getBaseContext(), DifficultyMenuActivityImpl.class);
                    i.putExtra("newGame", true);

                    v.startAnimation(mButtonFlickerAnimation);
                    mButtonFlickerAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                    mPaused = true;
                }
            }
        };
        mStartButton.setOnClickListener(sStartButtonListener);

        if (mContinueButton != null) {
            sContinueButtonListener = new View.OnClickListener() {
                public void onClick(View v) {
                    if (!mPaused) {
                        mStopMusic = true;
                        SoundManager.play(SoundManager.SOUND_START);

                        final String mainClassName = UtilsResources.getClassName(context, "class_name_main");
                        Intent i = new Intent();
                        i.setClassName(context, mainClassName);

                        v.startAnimation(mButtonFlickerAnimation);
                        mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                        mBackground.startAnimation(mFadeOutAnimation);
                        mStartButton.startAnimation(mAlternateFadeOutAnimation);
                        if (mSettingsButton != null) {
                            mSettingsButton.startAnimation(mAlternateFadeOutAnimation);
                        }
//                        mExtrasButton.startAnimation(mAlternateFadeOutAnimation);
//                        mTicker.startAnimation(mAlternateFadeOutAnimation);
                        mPaused = true;
                    }
                }
            };
            mContinueButton.setOnClickListener(sContinueButtonListener);
        }


        if (mSettingsButton != null) {
            sSettingsButtonListener = new View.OnClickListener() {
                public void onClick(View v) {
                    if (!mPaused) {
                        if (MusicManager.getResource(MusicManager.MUSIC_PREFERENCES) > 0) {
                            mStopMusic = true;
                        } else {
                            mContinueMusic = true;
                            MusicManager.setCurrentAsPrevious(); //hack to allow preferences activity to use same music
                        }
                        SoundManager.play(SoundManager.SOUND_OK);

                        final String settingsClassName = UtilsResources.getClassName(context, "class_name_settings");
                        Intent i = new Intent();
                        i.setClassName(context, settingsClassName);

                        v.startAnimation(mButtonFlickerAnimation);
                        mFadeOutAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                        mBackground.startAnimation(mFadeOutAnimation);
                        mStartButton.startAnimation(mAlternateFadeOutAnimation);
                        if (mContinueButton != null) {
                            mContinueButton.startAnimation(mAlternateFadeOutAnimation);
                        }
//                        mExtrasButton.startAnimation(mAlternateFadeOutAnimation);
//                        mTicker.startAnimation(mAlternateFadeOutAnimation);
                        mPaused = true;
                    }
                }
            };
            mSettingsButton.setOnClickListener(sSettingsButtonListener);
        }

/*
        sExtrasButtonListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (!mPaused) {
                    mContinueMusic = true;
                    SoundManager.play(SoundManager.SOUND_OK);

                    final String extrasImplClassName = UtilsResources.getClassName(context, "class_name_prefs");
                    Intent i = new Intent();
                    i.setClassName(context, extrasImplClassName);

                    v.startAnimation(mButtonFlickerAnimation);
                    mButtonFlickerAnimation.setAnimationListener(new StartActivityAfterAnimation(i));
                    mPaused = true;
                }
            }
        };
        mExtrasButton.setOnClickListener(sExtrasButtonListener);
*/

        mButtonFlickerAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "button_flicker"));
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "fade_out"));
        mAlternateFadeOutAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "fade_out"));
//        mFadeInAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(packageName, "anim", "fade_in"));

/*
        SharedPreferences prefs = getSharedPreferences(getString(UtilsResources.getResourceIdByName(packageName, "string", "app_name")) + PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);

        final int row = prefs.getInt(PreferenceConstants.PREFERENCE_LEVEL_ROW, 0);
        final int index = prefs.getInt(PreferenceConstants.PREFERENCE_LEVEL_INDEX, 0);

        int levelTreeResource = UtilsResources.getResourceIdByName(packageName, "xml", "level_tree");

        if (row != 0 || index != 0) {
            final int linear = prefs.getInt(PreferenceConstants.PREFERENCE_LINEAR_MODE, 0);
            if (linear != 0) {
                levelTreeResource = UtilsResources.getResourceIdByName(packageName, "xml", "linear_level_tree");
            }
        }
/*
        if (!LevelTree.isLoaded(levelTreeResource)) {
            LevelTree.loadLevelTree(levelTreeResource, this);
        }
*/
/*
        mTicker = findViewById(R.id.ticker);
        if (mTicker != null) {
            mTicker.setFocusable(true);
            mTicker.requestFocus();
            mTicker.setSelected(true);
        }
*/

        mJustCreated = true;

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;

        // to make sure it's done when coming back from preferences menu
        MusicManager.updateVolumeFromPrefs(this);
        SoundManager.updateVolumeFromPrefs(this);

        mContinueMusic = false;
        mStopMusic = false;
        MusicManager.start(this, MusicManager.MUSIC_MENU, true);


        // remove game "state" information
        final Context context = this.getApplicationContext();
        final String gameName = getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));
        final SharedPreferences prefs = getSharedPreferences(gameName + PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.remove(PreferenceConstants.PREFERENCE_GAME_STATE);
        prefsEditor.commit();

        mButtonFlickerAnimation.setAnimationListener(null);

        if (mContinueButton != null) {
            // show "continue" button if there's a saved game
//////// CONTINUE 20140411 - BEGIN
/*
            final int row = prefs.getInt(PreferenceConstants.PREFERENCE_LEVEL_ROW, 0);
            final int index = prefs.getInt(PreferenceConstants.PREFERENCE_LEVEL_INDEX, 0);
            if (row != 0 || index != 0) {
*/
//////// CONTINUE 20140411 - MID
            final int isContinue = prefs.getInt(PreferenceConstants.PREFERENCE_CONTINUE, 0);
            if (isContinue != 0) {
//////// CONTINUE 20140411 - END
                mContinueButton.setVisibility(View.VISIBLE);
            } else {
                mContinueButton.setVisibility(View.GONE);
            }

//////////////// SETTINGS - BEGIN
/*
?
// !!!! ???? TODO : OK ? ???? !!!!
//            if (Math.abs(lastVersion) < Math.abs(MyGame.VERSION)) {
            if (Math.abs(lastVersion) < Math.abs(Integer.parseInt(R.string.game_version))) {
                // This is a new install or an upgrade

                // Check the safe mode option
                // Useful reference: http://en.wikipedia.org/wiki/List_of_Android_devices
                if (Build.PRODUCT.contains("morrison") || // Motorola Cliq/Dext
                        Build.MODEL.contains("Pulse") || // Huawei Pulse
                        Build.MODEL.contains("U8220") || // Huawei Pulse
                        Build.MODEL.contains("U8230") || // Huawei U8230
                        Build.MODEL.contains("MB300") || // Motorola Backflip
                        Build.MODEL.contains("MB501") || // Motorola Quench / Cliq XT
                        Build.MODEL.contains("Behold+II")) { // Samsung Behold II
                    // These are all models that users have complained about.
                    // They likely use the same buggy QTC graphics driver.
                    // Turn on Safe Mode by default for these devices.
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(PreferenceConstants.PREFERENCE_SAFE_MODE, true);
                    editor.commit();
                }
*/
/*
                SharedPreferences.Editor editor = prefs.edit();
// !!!! ???? TODO : what number ? ???? !!!!
// => needed ?
                if (lastVersion > 0 && lastVersion < 14) {
                    // if the user has beat the game once, go ahead and unlock stuff
                    if (prefs.getInt(PreferenceConstants.PREFERENCE_LAST_ENDING, -1) != -1) {
                        editor.putBoolean(PreferenceConstants.PREFERENCE_EXTRAS_UNLOCKED, true);
                    }
                }

                editor.putInt(PreferenceConstants.PREFERENCE_LAST_VERSION, Integer.parseInt(R.string.game_version));
                editor.commit();

/*
                // show what's new message
                showDialog(WHATS_NEW_DIALOG);
*/

// !!!! TODO : see what we want !!!!
/*
                if ((lastVersion > 0) && (lastVersion < 14) &&
                        prefs.getBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, false)) {
                    if (touch.supportsMultitouch(this)) {
                        // show message about switching from tilt to screen controls
                        showDialog(TILT_TO_SCREEN_CONTROLS_DIALOG);
                    }
                } else {
*/
/*
                } else if (lastVersion == 0) {
                    // show message about auto-selected control schemes.
                    showDialog(CONTROL_SETUP_DIALOG);
                }
            }
*/
//////////////// SETTINGS - MID
//////// SETTINGS_INIT 20140408 - BEGIN
/*
            final int lastVersion = prefs.getInt(PreferenceConstants.PREFERENCE_LAST_VERSION, 0);
            if (lastVersion == 0) {
                // first time the game has been run
                setInitialSettings(prefs);
            }
*/
// !!!! ???? TODO: OK if called every time ? ???? !!!!
            setInitialSettings(prefs);
//////////////// SETTINGS - END
        }

        if (mBackground != null) {
            mBackground.clearAnimation();
        }

/*
        if (mTicker != null) {
            mTicker.clearAnimation();
            mTicker.setAnimation(mFadeInAnimation);
        }
*/

        if (mJustCreated) {
            if (mStartButton != null) {
                Animation anim = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "button_fade_out"));
//                anim.setStartOffset(1000L);
                mStartButton.startAnimation(anim);
            }

            if (mContinueButton != null) {
                Animation anim = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "button_fade_out"));
//                anim.setStartOffset( 1000L );
                mContinueButton.startAnimation(anim);
            }

            if (mSettingsButton != null) {
                Animation anim = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "button_fade_out"));
//                anim.setStartOffset(1000L);
                mSettingsButton.startAnimation(anim);
            }

/*
            if (mExtrasButton != null) {
                Animation anim = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(packageName, "anim", "button_fade_out"));
                anim.setStartOffset(500L);
                mExtrasButton.startAnimation(anim);
            }
*/

            mJustCreated = false;

        } else {
            mStartButton.clearAnimation();
            if (mContinueButton != null) {
                mContinueButton.clearAnimation();
            }
            if (mSettingsButton != null) {
                mSettingsButton.clearAnimation();
            }
//            mExtrasButton.clearAnimation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;

        if (!mContinueMusic) {
            if (mStopMusic) {
                MusicManager.stop();
            } else {
                MusicManager.pause();
            }
        }
    }


//////////////// SETTINGS - MID
    protected abstract void setInitialSettings(SharedPreferences prefs);
//////////////// SETTINGS - END

/*
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
/*
        if (id == WHATS_NEW_DIALOG) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.whats_new_dialog_title)
                    .setPositiveButton(R.string.whats_new_dialog_ok, null)
                    .setMessage(R.string.whats_new_dialog_message)
                    .create();

        } else
*/
/*
        if (id == TILT_TO_SCREEN_CONTROLS_DIALOG) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.onscreen_tilt_dialog_title)
                    .setPositiveButton(R.string.onscreen_tilt_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences prefs = getSharedPreferences(R.string.game_name + PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, true);
                                editor.commit();
                            }
                        }
                    )
                    .setNegativeButton(R.string.onscreen_tilt_dialog_cancel, null)
                    .setMessage(R.string.onscreen_tilt_dialog_message)
                    .create();

        } else if (id == CONTROL_SETUP_DIALOG) {
            String messageFormat = getResources().getString(R.string.control_setup_dialog_message);
            String message = String.format(messageFormat, mSelectedControlsString);
            CharSequence sytledMessage = Html.fromHtml(message); // lame
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.control_setup_dialog_title)
                    .setPositiveButton(R.string.control_setup_dialog_ok, null)
                    .setNegativeButton(R.string.control_setup_dialog_change,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent i = new Intent(getBaseContext(), SetPreferencesActivity.class);
                                i.putExtra("controlConfig", true);
                                startActivity(i);
                            }
                        }
                    )
                    .setMessage(sytledMessage)
                    .create();

        } else {
            dialog = super.onCreateDialog(id);
        }
        return dialog;
    }
*/


    protected class StartActivityAfterAnimation implements Animation.AnimationListener {
        private Intent mIntent;

        StartActivityAfterAnimation(Intent intent) {
            mIntent = intent;
        }

        public void onAnimationEnd(Animation animation) {
            startActivity(mIntent);
            UtilsActivities.startTransition(MainMenuActivity.this, "activity_fade_in", "activity_fade_out");
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }
    }
}
