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

import com.replica.engine.LevelTree.Level;
//////// DIALOGS - MID
import com.replica.engine.ConversationUtils.Conversation;
import com.replica.engine.ConversationUtils.ConversationPage;
//////// DIALOGS - END
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
//////// SOUND - MID
import android.media.AudioManager;
//////// SOUND - END
import android.os.Bundle;
//////// DEBUG_MENU - MID
//import android.os.Debug;
//////// DEBUG_MENU - END
import android.util.DisplayMetrics;
import android.view.KeyEvent;
//////// DIALOGS - MID
import android.view.MotionEvent;
//////// DIALOGS - END
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
//////// PAUSE - MID
import android.widget.ImageButton;
import android.widget.TextView;
//////// PAUSE - END
//////// DIALOGS - MID
import android.widget.ImageView;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import java.util.ArrayList;
//////// DIALOGS - END
import org.xmlpull.v1.XmlPullParser;

/**
 * Core activity for the game.  Sets up a surface view for OpenGL, bootstraps
 * the game engine, and manages UI events.  Also manages game progression,
 * transitioning to other activites, save game, and input events.
 */
public abstract class Main extends Activity {
//////// state - mid
    private static final int ACTIVITY_START_GAME = 0;
    public static final int ACTIVITY_STOP_GAME = 1;
//////// state - end
    private static final int ACTIVITY_CHANGE_LEVELS = 2;
    private static final int ACTIVITY_ANIMATION_PLAYER = 3;
//////// lev res - m
    private static final int ACTIVITY_LEVEL_RESULTS = 4;
//////// lev res - e
//////// state - mid
    private static final int ACTIVITY_GAME_RESULTS_WON = 5;
    private static final int ACTIVITY_GAME_RESULTS_LOST = 6;
//////// state - end

//////// debug - b
//    private static final int CHANGE_LEVEL_ID = Menu.FIRST;
//////// debug - e


    protected static String GAME_NAME;

    private static int VERSION;

//////// state - mid
    private int mState;
//////// state - end

    private int mLinearMode = 0;

    protected Game mGame;
    private GLSurfaceView mGLSurfaceView;

    protected int mViewWidth;
    protected int mViewHeight;

    protected SharedPreferences.Editor mPrefsEditor;

    private View mWaitMessage = null;
    private Animation mWaitFadeAnimation = null;

    protected boolean mIsPaused;
    private View mPauseMenu;
//    private View mPauseBackground;
    private TextView mLevelName = null;

//////// DEBUG_MENU - MID
    private boolean mMethodTracing;
//////// DEBUG_MENU - END

    private ImageButton mContinueButton;
//////// DEBUG_MENU - MID
    private ImageButton mSelectLevelButton;
    private ImageButton mMethodTracingButton;
//    private ImageButton mShowCollisionsButton;
//////// DEBUG_MENU - END
    private ImageButton mQuitButton;

    private View.OnClickListener sContinueButtonListener;
//////// DEBUG_MENU - MID
    private View.OnClickListener sSelectLevelButtonListener;
    private View.OnClickListener sMethodTracingButtonListener;
//    private View.OnClickListener sShowCollisionsButtonListener;
//////// DEBUG_MENU - END
    private View.OnClickListener sQuitButtonListener;

//////// DIALOGS - MID
    protected boolean mIsDialog;
    private ConversationUtils.Conversation mConversation;
    private ArrayList<ConversationUtils.ConversationPage> mPages;
    private int mCurrentPage;

    protected View mDialog;
    private ImageView mSpeaker;
    private TextView mSpeakerName;
    private TypewriterTextView mTv;
    private ImageView mOkArrow;
//////// DIALOGS - END

    private int mLevelRow;
    private int mLevelIndex;
    private float mTotalGameTime;

//////// CONTINUE 20140411 - MID
    private int mNbContinues;
//////// CONTINUE 20140411 - END

    private int mLastEnding = -1;

    private int mDifficulty = 1;

//    private boolean mExtrasUnlocked;

    protected long mLastInputTime = 0L;

    private int mWonAnimId;
    private int mWonBkgId;
    private int mLostAnimId;
    private int mLostBkgId;
    private int mLevelEndBkgId;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = this.getApplicationContext();
        GAME_NAME = getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));
        VERSION = Integer.parseInt(getString(UtilsResources.getResourceIdByName(context, "string", "app_version")));

        SharedPreferences prefs = getSharedPreferences(GAME_NAME + PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
        final boolean debugLogs = prefs.getBoolean(PreferenceConstants.PREFERENCE_ENABLE_DEBUG, false);
        if (VERSION < 0 || debugLogs) {
            DebugLog.setDebugLogging(true);
        } else {
            DebugLog.setDebugLogging(false);
        }
        if (DebugChecks.DEBUG) {
            DebugLog.setDebugLogging(true);
        }

        setContentView(UtilsResources.getResourceIdByName(context, "layout", "main"));
        mGLSurfaceView = (GLSurfaceView)findViewById(UtilsResources.getResourceIdByName(context, "id", "glsurfaceview"));

        mIsPaused = false;
        mPauseMenu = findViewById(UtilsResources.getResourceIdByName(context, "id", "pauseMenu"));
//        mPauseBackground = findViewById(UtilsResources.getResourceIdByName(context, "id", "pauseMenuBackground"));
        mLevelName = (TextView)findViewById(UtilsResources.getResourceIdByName(context, "id", "levelName"));
        mPauseMenu.setVisibility(View.INVISIBLE);

        mContinueButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "pauseContinueButton"));
//////// DEBUG_MENU - MID
        mSelectLevelButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "pauseSelectLevelButton"));
        mMethodTracingButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "pauseMethodTracingButton"));
//        mShowCollisionsButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "pauseShowCollisionsButton"));
        if (VERSION >= 0) { // hide debug buttons if exist
            if (mSelectLevelButton != null) {
                mSelectLevelButton.setVisibility(View.GONE);
            }
            mSelectLevelButton = null;
            if (mMethodTracingButton != null) {
                mMethodTracingButton.setVisibility(View.GONE);
            }
            mMethodTracingButton = null;
/*
            if (mShowCollisionsButton != null) {
                mShowCollisionsButton.setVisibility(View.GONE);
            }
            mShowCollisionsButton = null;
*/
        }
//////// DEBUG_MENU - END
        mQuitButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "pauseQuitButton"));

        sContinueButtonListener = new View.OnClickListener() {
            public void onClick(View v) {
//////// DIALOGS - BEGIN
/*
                hidePauseScreen();
                mGame.onResume(Main.this, true);
*/
//////// DIALOGS - MID
                unPause();
//////// DIALOGS - END
            }
        };
        mContinueButton.setOnClickListener(sContinueButtonListener);

//////// DEBUG_MENU - MID
        if (mSelectLevelButton != null) {
            sSelectLevelButtonListener = new View.OnClickListener() {
                public void onClick(View v) {
                    gotoLevelSelect(true);
                }
            };
            mSelectLevelButton.setOnClickListener(sSelectLevelButtonListener);
        }

        if (mMethodTracingButton != null) {
            sMethodTracingButtonListener = new View.OnClickListener() {
                public void onClick(View v) {
                    debugMethodTracing();
                }
            };
            mMethodTracingButton.setOnClickListener(sMethodTracingButtonListener);
        }
/*
        if (mShowCollisionsButton != null) {
            sShowCollisionsButtonListener = new View.OnClickListener() {
                public void onClick(View v) {
                    debugShowCollisions();
                }
            };
            mShowCollisionsButton.setOnClickListener(sShowCollisionsButtonListener);
        }
*/
//////// DEBUG_MENU - END

        sQuitButtonListener = new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };
        mQuitButton.setOnClickListener(sQuitButtonListener);

//////// DIALOGS - MID
        mIsDialog = false;
        mDialog = findViewById(UtilsResources.getResourceIdByName(context, "id", "dialog"));
        mDialog.setVisibility(View.GONE);

        mOkArrow = (ImageView)findViewById(UtilsResources.getResourceIdByName(context, "id", "ok"));
        mOkArrow.setVisibility(View.INVISIBLE);

        mPages = null;

        mSpeaker = (ImageView)findViewById(UtilsResources.getResourceIdByName(context, "id", "speaker"));
        mSpeaker.setVisibility(View.INVISIBLE);
        mSpeakerName = (TextView)findViewById(UtilsResources.getResourceIdByName(context, "id", "speakername"));

        mTv = (TypewriterTextView)findViewById(UtilsResources.getResourceIdByName(context, "id", "typewritertext"));
        mTv.setParentActivity(this);
        mTv.setOkArrow(mOkArrow);
//////// DIALOGS - END

        mWaitMessage = findViewById(UtilsResources.getResourceIdByName(context, "id", "waitMessage"));
        mWaitFadeAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "wait_message_fade"));

        //mGLSurfaceView.setGLWrapper(new GLErrorLogger());
        mGLSurfaceView.setEGLConfigChooser(false); // 16 bit, no z-buffer
        //mGLSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);

        mGame = getGame();
        mGame.setSurfaceView(mGLSurfaceView);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        setViewSize();

        DebugChecks.assertCondition(mViewWidth != 0 && mViewHeight != 0);

        if (keepScreenRatio()) {
// !!!! ???? TODO: better to scale on width or height ? ???? !!!!
// => should be able to specify it ...
            if (dm.widthPixels != mViewWidth) {
                float ratio = ((float)dm.widthPixels) / dm.heightPixels;
                mViewWidth = (int)(mViewHeight * ratio);
            }
        }

        mPrefsEditor = prefs.edit();

//////// state - mid
        mState = prefs.getInt(PreferenceConstants.PREFERENCE_GAME_STATE, ACTIVITY_START_GAME);
        mPrefsEditor.remove(PreferenceConstants.PREFERENCE_GAME_STATE);
        mPrefsEditor.remove(PreferenceConstants.PREFERENCE_END_STATE);
        mPrefsEditor.commit();

        // OUYA hack!
        if (mState == ACTIVITY_GAME_RESULTS_WON) {
            startEnd();
            return;
        } else if (mState == ACTIVITY_GAME_RESULTS_LOST) {
            finish();
            return;
        }

//////// state - end

        // Make sure that old game information is cleared when we start a new game
        boolean newGame = getIntent().getBooleanExtra("newGame", false);
//////// state - begin
//        if (newGame) {
//////// state - mid
        if (mState == ACTIVITY_START_GAME && newGame) {
//////// state - end
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_DIFFICULTY);
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_LEVEL_ROW);
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_LEVEL_INDEX);
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_LEVEL_COMPLETED);
//////// CONTINUE 20140411 - MID
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_CONTINUE);
//////// CONTINUE 20140411 - END
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_TOTAL_GAME_TIME);
            mPrefsEditor.remove(PreferenceConstants.PREFERENCE_LINEAR_MODE);
            clearSpecificPrefs();
            mPrefsEditor.commit();
        }

        mDifficulty = prefs.getInt(PreferenceConstants.PREFERENCE_DIFFICULTY,
                getIntent().getIntExtra("difficulty", 1));
        mLevelRow = prefs.getInt(PreferenceConstants.PREFERENCE_LEVEL_ROW, 0);
        mLevelIndex = prefs.getInt(PreferenceConstants.PREFERENCE_LEVEL_INDEX, 0);
        int completed = prefs.getInt(PreferenceConstants.PREFERENCE_LEVEL_COMPLETED, 0);
        mTotalGameTime = prefs.getFloat(PreferenceConstants.PREFERENCE_TOTAL_GAME_TIME, 0.0f);
        mLinearMode = prefs.getInt(PreferenceConstants.PREFERENCE_LINEAR_MODE, 
                getIntent().getBooleanExtra("linearMode", false) ? 1 : 0);
//        mExtrasUnlocked = prefs.getBoolean(PreferenceConstants.PREFERENCE_EXTRAS_UNLOCKED, false);

        loadSpecificPrefs(prefs);


        mGame.bootstrap(this, dm.widthPixels, dm.heightPixels, mViewWidth, mViewHeight, mDifficulty, VERSION);

        mGLSurfaceView.setRenderer(mGame.getRenderer());

        final int mainResource = UtilsResources.getResourceIdByName(context, "xml", "main");
        mWonAnimId = 0;
        mWonBkgId = 0;
        mLostAnimId = 0;
        mLostBkgId = 0;
        mLevelEndBkgId = 0;
        if (mainResource != 0) {
            loadResources(mainResource);
        }

        int levelTreeResource = UtilsResources.getResourceIdByName(context, "xml", "level_tree");
        if (mLinearMode != 0) {
            levelTreeResource = UtilsResources.getResourceIdByName(context, "xml", "linear_level_tree");
        }


        // Android activity lifecycle rules make it possible for this activity
        // to be created and come to the foreground without the MainMenu Activity
        // ever running, so in that case we need to make sure that this static data is valid.
        if (!LevelTree.isLoaded(levelTreeResource)) {
            LevelTree.loadLevelTree(levelTreeResource, this);
        }

//////// LEVEL 20140325 - MID
        // hack to force completion reset
        if (newGame) {
            LevelTree.updateCompletedState(-1, 0);
        }
//////// LEVEL 20140325 - END

//////// state - mid
        if (mState == ACTIVITY_START_GAME) {
//////// state - end
            if (!LevelTree.levelIsValid(mLevelRow, mLevelIndex)) {
                if (LevelTree.rowIsValid(mLevelRow)) {
                    mLevelIndex = 0;
                    completed = 0;
                } else if (LevelTree.rowIsValid(mLevelRow - 1)) {
                    mLevelRow--;
                    mLevelIndex = 0;
                    completed = 0;
                }

                if (!LevelTree.levelIsValid(mLevelRow, mLevelIndex)) {
                    mLevelRow = 0;
                    mLevelIndex = 0;
                    completed = 0;
                }
            }

            LevelTree.updateCompletedState(mLevelRow, completed);

            final Level level = LevelTree.get(mLevelRow, mLevelIndex);
//////// LEVEL 20140325 - BEGIN
/*
            if (level.introAnim != -1) {
                onGameFlowEvent(GameFlowEvent.EVENT_SHOW_INTRO_ANIMATION, level.introAnim);
            } else {
                onGameFlowEvent(GameFlowEvent.EVENT_START_LEVEL, 0);
            }
*/
//////// LEVEL 20140325 - MID
            if ((level.selectable != 0) || (LevelTree.levels.get(mLevelRow).levels.size() > 1)) {
//////// DEBUG_MENU - BEGIN
/*
                // go to level select
// !!!! ???? TODO: OK ? ???? !!!!
//                final String levelClassName = UtilsResources.getClassName(context, "class_name_level_select");
//                Intent i = new Intent();
//                i.setClassName(this, levelClassName);
                Intent i = new Intent(this, LevelSelectActivityImpl.class);
//////// state  - mid
// !!!! ???? TODO: OK ? needed ? ???? !!!!
                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_GAME_STATE, ACTIVITY_CHANGE_LEVELS);
                mPrefsEditor.commit();
//////// state  - end
                startActivityForResult(i, ACTIVITY_CHANGE_LEVELS);
*/
//////// DEBUG_MENU - MID
                gotoLevelSelect(false);
//////// DEBUG_MENU - END

                UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");

            } else {
                if (level.introAnim != -1) {
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_INTRO_ANIMATION, level.introAnim);
                } else {
                    onGameFlowEvent(GameFlowEvent.EVENT_START_LEVEL, 0);
                }
            }
//////// LEVEL 20140325 - END
//////// state  - mid
// !!!! ???? TODO: shouldn't be needed as will be called after onCreate ? ???? !!!!
/*
        } else {
            onActivityResult(mState, RESULT_OK, getIntent());
*/
//////// state - end
        }

//////// SOUND - MID
// !!!! ???? TODO: in onCreate or in onResume ? ???? !!!!
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//////// SOUND - END

//////// REPORTER - BEGIN
/*
        mSessionId = prefs.getLong(PreferenceConstants.PREFERENCE_SESSION_ID, System.currentTimeMillis());
        mEventReporter = null;
        mEventReporterThread = null;
        final boolean statsEnabled = prefs.getBoolean(PreferenceConstants.PREFERENCE_STATS_ENABLED, true);
        if (statsEnabled) {
            mEventReporter = new EventReporter();
            mEventReporterThread = new Thread(mEventReporter);
            mEventReporterThread.setName("EventReporter");
            mEventReporterThread.start();
        }
*/
//////// REPORTER - END

//////// CONTINUE 20140411 - MID
        // can't be done earlier, as game isn't initalised yet
        mNbContinues = prefs.getInt(PreferenceConstants.PREFERENCE_CONTINUE, mGame.getNbContinues() + 1);
//////// CONTINUE 20140411 - END

        initSpecifics();
    }


    @Override
    protected void onDestroy() {
        mGame.stop();

////////REPORTER - BEGIN
/*
        if (mEventReporterThread != null) {
            mEventReporter.stop();
            try {
                mEventReporterThread.join();
            } catch (InterruptedException e) {
                mEventReporterThread.interrupt();
            }
        }
*/
////////REPORTER - END

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mGame.onPause();
        mGLSurfaceView.onPause();
        mGame.getRenderer().onPause(); // hack!

        if (mIsDialog) {
            mTv.onPause();
        }

//////// DEBUG_MENU - MID
        if (mMethodTracing) {
//            Debug.stopMethodTracing();
            mMethodTracing = false;
        }
//////// DEBUG_MENU - END
    }

    @Override
    protected void onResume() {
        super.onResume();
DebugLog.e("++++++++++++","++++++++++++++");
        // Preferences may have changed while we were paused
        SharedPreferences prefs = getSharedPreferences(GAME_NAME + PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
        mPrefsEditor = prefs.edit();

        final boolean debugLogs = prefs.getBoolean(PreferenceConstants.PREFERENCE_ENABLE_DEBUG, false);
        if (VERSION < 0 || debugLogs) {
            DebugLog.setDebugLogging(true);
        } else {
            DebugLog.setDebugLogging(false);
        }
DebugLog.setDebugLogging(true);


        mGLSurfaceView.onResume();
        if (mIsPaused) {
            mGame.onPause();
            mTv.onPause();
        } else {
            if (mIsDialog) {
                mGame.onPause();
                resumeDialog();
            } else {
                mGame.onResume(this, false);
            }
        }

//////// SAFE - BEGIN
//        final boolean safeMode = prefs.getBoolean(PreferenceConstants.PREFERENCE_SAFE_MODE, false);
//////// SAFE - END

        // default values for any game
        // => set the desired values in "setInitialSettings" in "MainMenuActivity" of each specific engines/games
        final int movementSensitivity = prefs.getInt(PreferenceConstants.PREFERENCE_MOVEMENT_SENSITIVITY, 100);
        final boolean onScreenControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_SCREEN_CONTROLS, true);
        final boolean tiltControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_TILT_CONTROLS, false);
        final int tiltSensitivity = prefs.getInt(PreferenceConstants.PREFERENCE_TILT_SENSITIVITY, 50);
//        final boolean flipControls = prefs.getBoolean(PreferenceConstants.PREFERENCE_FLIP, false);

        mGame.setControlOptions(tiltControls, tiltSensitivity, movementSensitivity, onScreenControls);

        loadSpecificPrefsCtrl(prefs);

//////// SOUND - MID
        final boolean disableAudio = prefs.getBoolean(PreferenceConstants.PREFERENCE_AUDIO_DISABLE, false);
        mGame.setSoundEnabled(!disableAudio);
//////// NEW_SOUND - MID
        mGame.setSoundVolume(prefs.getInt(PreferenceConstants.PREFERENCE_SOUND_VOLUME, 100));
//////// NEW_SOUND - END
//////// SOUND - END

//////// SAFE - BEGIN
//        mGame.setSafeMode(safeMode);
//////// SAFE - END

//////// SOUND - MID
// !!!! ???? TODO: in onCreate or in onResume ? ???? !!!!
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//////// SOUND - END
    }

//////// DIALOGS - MID
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = true;
        if (!mIsPaused) {
            // hack: can have a crash if touching screen when dialog appears
            // => "mText" is null in "mTv.getRemainingTime()"
// !!!! TODO: see if can make it better !!!!
            if (mIsDialog && (mDialog.getVisibility() == View.VISIBLE)) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    processDialog();
                }

                // Sleep so that the main thread doesn't get flooded with UI events
                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                    // No big deal if this sleep is interrupted
                }
            }
        } else {
            result = super.onTouchEvent(event);
        }
        return result;
    }
//////// DIALOGS - END

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//////// DIALOGS - BEGIN
/*
            if (mGame.isPaused()) {
                hidePauseScreen();
                mGame.onResume(this, true);
            } else {
                mGame.onPause();

                showPauseScreen();
                result = true;
            }
*/
//////// DIALOGS - MID
            if (mIsPaused) {
                unPause();
            } else {
                mGame.onPause();
            	mIsPaused = true;
              	mPauseMenu.setVisibility(View.VISIBLE);
                if (mLevelName != null) {
                    mLevelName.setText(LevelTree.get(mLevelRow, mLevelIndex).name);
                }

//                if (mIsDialog) {
                if (mTv != null) {
                    mTv.onPause();
                }
            }
            result = true;
//////// DIALOGS - END
//////// SOUND - MID
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            result = super.onKeyUp(keyCode, event);
//////// SOUND - END
        } else {
            if (mGame.isPaused()) {
                result = super.onKeyDown(keyCode, event);
            } else {
// !!!! ???? TODO: handle other keys ? ???? !!!!
//?                result = mGame.onKeyDownEvent(keyCode);

                // Sleep so that the main thread doesn't get flooded with UI events
                try {
                    Thread.sleep(4);
                } catch (InterruptedException e) {
                    // No big deal if this sleep is interrupted
                }
            }
        }
        return result;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            result = true;
        } else {
// !!!! ???? TODO: handle other keys ? ???? !!!!
//?            result = mGame.onKeyUpEvent(keyCode);

            // Sleep so that the main thread doesn't get flooded with UI events
            try {
                Thread.sleep(4);
            } catch (InterruptedException e) {
                // No big deal if this sleep is interrupted
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

//////// state - mid
        mPrefsEditor.remove(PreferenceConstants.PREFERENCE_GAME_STATE);
        mPrefsEditor.commit();
//////// state - end

        if (requestCode == ACTIVITY_CHANGE_LEVELS) {
            if (resultCode == RESULT_OK) {
                mLevelRow = intent.getExtras().getInt("row");
                mLevelIndex = intent.getExtras().getInt("index");
//                LevelTree.updateCompletedState(mLevelRow, 0);

                saveGame();

                final Level level = LevelTree.get(mLevelRow, mLevelIndex);
                if (level.introAnim != -1) {
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_INTRO_ANIMATION, level.introAnim);
                } else {
                    onGameFlowEvent(GameFlowEvent.EVENT_START_LEVEL, 0);
                }
            }
        } else if (requestCode == ACTIVITY_ANIMATION_PLAYER) {
/*
//            final int lastAnimation = intent.getIntExtra("animation", -1);
            // record ending events
            if (lastAnimation > -1) {
                mGame.setLastEnding(lastAnimation);
            }
*/
            final int lastAnimationType = intent.getIntExtra("type", -1);
            switch (lastAnimationType) {
                case GameFlowEvent.EVENT_SHOW_ANIMATION:
                    // resume level
                    onGameFlowEvent(GameFlowEvent.EVENT_RESUME_LEVEL, 0);
                    break;
                case GameFlowEvent.EVENT_SHOW_INTRO_ANIMATION:
                    // start current level
                    onGameFlowEvent(GameFlowEvent.EVENT_START_LEVEL, 0);
                    break;
                case GameFlowEvent.EVENT_SHOW_OUTRO_ANIMATION:
                    // go to next level
//////// lev res - b
/*
                    onGameFlowEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0);
*/
//////// lev res - m
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_RESULTS_LEVEL, 0);
//////// lev res - e
                    break;
                case GameFlowEvent.EVENT_SHOW_END_GOOD_ANIMATION:
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_RESULTS_GAME, 0);
                    break;
                case GameFlowEvent.EVENT_SHOW_END_BAD_ANIMATION:
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_RESULTS_GAME, -1);
                    break;
            }
//////// lev res - m
        } else if (requestCode == ACTIVITY_LEVEL_RESULTS) {
            onGameFlowEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0);
//////// lev res - e
        } else if (requestCode == ACTIVITY_GAME_RESULTS_WON) {
            startEnd();
        } else if (requestCode == ACTIVITY_GAME_RESULTS_LOST) {
            finish();
        }
    }

    /*
     When the game thread needs to stop its own execution
      ( to go to a new level, or restart the current level ),
      it registers a runnable on the main thread which orders the action
      via this function.
     */
    public void onGameFlowEvent(int eventCode, int index) {
//////// SKIPPABLE 20140410 - MID
        boolean next = true;
//////// SKIPPABLE 20140410 - END

        switch (eventCode) {
            case GameFlowEvent.EVENT_END_GAME:
            {
                mGame.stop();
//////// state  - mid
                mPrefsEditor.remove(PreferenceConstants.PREFERENCE_GAME_STATE);
                mPrefsEditor.commit();
//////// state  - end
                finish();
                break;
            }
            case GameFlowEvent.EVENT_SHOW_RESULTS_GAME:
            {
                final Context context = getApplicationContext();
                final String gameOverClassName = UtilsResources.getClassName(context, "class_name_game_over");

                Intent i = new Intent();
                i.setClassName(context, gameOverClassName);
// !!!! TODO: could use the index to differentiate "won" & "lost" in called activity !!!!
// => OR USE MORE THAN 2 VALUES, AND HAVE DIFFERENT ENDINGS (if pickup enough objects, kill someone special, etc.)
//                i.putExtra("type", index);
                int activityResult = ACTIVITY_GAME_RESULTS_WON;
                if (index < 0) { // lost
                    i.putExtra("background", mLostBkgId);
                    final String title = getString(UtilsResources.getResourceIdByName(context, "string", "endgame_lost_title"));
                    i.putExtra("title", title);
                    final String text = getString(UtilsResources.getResourceIdByName(context, "string", "endgame_lost_text"));
                    i.putExtra("text", text);
                    activityResult = ACTIVITY_GAME_RESULTS_LOST;
                } else { // won
                    i.putExtra("background", mWonBkgId);
                    final String title = getString(UtilsResources.getResourceIdByName(context, "string", "endgame_won_title"));
                    i.putExtra("title", title);
                    final String text = getString(UtilsResources.getResourceIdByName(context, "string", "endgame_won_text"));
                    i.putExtra("text", text);
                    activityResult = ACTIVITY_GAME_RESULTS_WON;
                }

                // if want to have current level results in game over screen added to already completed data !!!!
                // => BUT doesn't work, need to change that ...
                //updateSpecifics();
                addGameOverData(i);
//////// state  - mid
                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_GAME_STATE, activityResult);
                mPrefsEditor.commit();
//////// state  - end

                startActivityForResult(i, activityResult);
                UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");
                break;
            }
            case GameFlowEvent.EVENT_GAME_WON:
            {
//////// REPLAY 20140326 - BEGIN
                mLevelRow = 0;
                mLevelIndex = 0;
//////// REPLAY 20140326 - MID
                mLastEnding = mGame.getLastEnding();
//                mExtrasUnlocked = true;
                saveGame();
                mGame.stop();

                if (mWonAnimId != 0) {
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_END_GOOD_ANIMATION, mWonAnimId);
                } else {
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_RESULTS_GAME, 0  );
                }
                break;
            }
            case GameFlowEvent.EVENT_GAME_LOST:
            {
                mGame.stop();

                if (mLostAnimId != 0) {
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_END_BAD_ANIMATION, mLostAnimId);
                } else {
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_RESULTS_GAME, -1);
                }
                break;
            }
            case GameFlowEvent.EVENT_START_LEVEL:
            {
//////// DEBUG_MENU - MID
// !!!! ???? TODO: OK ? ???? !!!!
                unPause();
//////// DEBUG_MENU - END

                final LevelTree.Level level = LevelTree.get(mLevelRow, mLevelIndex);
//                mGame.setPendingLevel(level);
                mGame.setPendingLevel(level, true);
                if (level.showWaitMessage) {
                    showWaitMessage();
                } else {
                    hideWaitMessage();
                }
// !!!! ???? TODO : OK to do it every time ? ???? !!!!
//=> or should be done only when came from NEXT_LEVEL ?
                mGame.requestNewLevel();
                break;
            }
            case GameFlowEvent.EVENT_END_LEVEL:
            {
// !!!! TODO: should pause the game (but not with "pause" menu !!!!
//...
                final LevelTree.Level level = LevelTree.get(mLevelRow, mLevelIndex);
                if (level.outroAnim != -1) {
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_OUTRO_ANIMATION, level.outroAnim);
                } else {
//////// lev res - b
/*
                    onGameFlowEvent(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0);
*/
//////// lev res - m
                    onGameFlowEvent(GameFlowEvent.EVENT_SHOW_RESULTS_LEVEL, 0);
//////// lev res - e
                }
                break;
            }
            case GameFlowEvent.EVENT_RESTART_LEVEL:
            {
                final LevelTree.Level level = LevelTree.get(mLevelRow, mLevelIndex);
                if (level.restartable) {
                    mGame.restartLevel();
                    break;
                }
                // else, fall through and go to the next level
//////// SKIPPABLE 20140410 - MID
                if (!level.skippable) {
                    next = false;

                    // get first selectable level & set "uncomplete" all following ones
                    boolean ok = false;
                    while (!ok) {
                        while (mLevelIndex > 0) {
                            --mLevelIndex;
                            final LevelTree.Level previousLevel = LevelTree.get(mLevelRow, mLevelIndex);
                            previousLevel.completed = false;
//                            if (previousLevel.selectable) {
                            if (previousLevel.selectable != 0) {
                                ok = true;
                                break;
                            }
                        }
                        if (!ok && (mLevelRow > 0)) {
                            --mLevelRow;
                            final LevelTree.LevelGroup currentGroup = LevelTree.levels.get(mLevelRow);
                            mLevelIndex = currentGroup.levels.size() - 1;
                            final LevelTree.Level previousLevel = LevelTree.get(mLevelRow, mLevelIndex);
                            previousLevel.completed = false;
//                            if (previousLevel.selectable) {
                            if (previousLevel.selectable != 0) {
                                ok = true;
                                break;
                            }
                        } else {
                            ok = true;
                        }
                    }
                }
//////// SKIPPABLE 20140410 - END
            }
            case GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL:
            {
//////// SKIPPABLE 20140410 - MID
                if (next) {
//////// SKIPPABLE 20140410 - END
                LevelTree.get(mLevelRow, mLevelIndex).completed = true;
                final LevelTree.LevelGroup currentGroup = LevelTree.levels.get(mLevelRow);
                final int count = currentGroup.levels.size();
                boolean groupCompleted = true;
// !!!! TODO: could add event reporter stuff here ... !!!!
                for (int x = 0; x < count; x++) {
                    if (currentGroup.levels.get(x).completed == false) {
                        // We haven't completed the group yet
                        mLevelIndex = x;
                        groupCompleted = false;
                        break;
                    }
                }

                if (groupCompleted) {
                    mLevelIndex = 0;
                    mLevelRow++;
                }

                mTotalGameTime += mGame.getGameTime();

                updateSpecifics();

//////// SKIPPABLE 20140410 - MID
                }
//////// SKIPPABLE 20140410 - END

                if (mLevelRow < LevelTree.levels.size()) {
                    final LevelTree.Level currentLevel = LevelTree.get(mLevelRow, mLevelIndex);

                    if ((currentLevel.selectable != 0) || (LevelTree.levels.get(mLevelRow).levels.size() > 1)) {
//////// DEBUG_MENU - BEGIN
/*
                        // go to level select
// !!!! ???? TODO: OK ? ???? !!!!
//                        final String levelClassName = UtilsResources.getClassName(getApplicationContext(), "class_name_level_select");
//                        Intent i = new Intent();
//                        i.setClassName(this, levelClassName);
                        Intent i = new Intent(this, LevelSelectActivityImpl.class);
//////// state  - mid
// !!!! ???? TODO: OK ? needed ? ???? !!!!
                        mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_GAME_STATE, ACTIVITY_CHANGE_LEVELS);
                        mPrefsEditor.commit();
//////// state  - end
                        startActivityForResult(i, ACTIVITY_CHANGE_LEVELS);
*/
//////// DEBUG_MENU - MID
                        gotoLevelSelect(false);
//////// DEBUG_MENU - END
                        UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");
                    } else {
                        // next level
                        if (currentLevel.introAnim != -1) {
                            onGameFlowEvent(GameFlowEvent.EVENT_SHOW_INTRO_ANIMATION, currentLevel.introAnim);
                        } else {
                            onGameFlowEvent(GameFlowEvent.EVENT_START_LEVEL, 0);
                        }
                    }
                    saveGame();
                } else {
                    // game won
                    onGameFlowEvent(GameFlowEvent.EVENT_GAME_WON, 0);
                }
                break;
            }
//////// anim - m
            case GameFlowEvent.EVENT_RESUME_LEVEL:
            {
// !!!! ???? TODO : need to do some fading ? ???? !!!!
//                mGame.requestNewLevel();
                mGame.resumeLevel();
                break;
            }
//////// anim - e

//////// lev res - m
            case GameFlowEvent.EVENT_SHOW_RESULTS_LEVEL:
            {
                final Context context  = getApplicationContext();
                final String resultsClassName = UtilsResources.getClassName(context, "class_name_level_results");
                Intent i = new Intent();
                i.setClassName(context, resultsClassName);
//                i.putExtra("?", index);
                final LevelTree.Level level = LevelTree.get(mLevelRow, mLevelIndex);
                i.putExtra("title", level.name);
                final int backgroundRes = (level.levelEndResource != 0) ? level.levelEndResource : mLevelEndBkgId;
                i.putExtra("background", backgroundRes);

                addLevelResultsData(i);
//////// state  - mid
                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_GAME_STATE, ACTIVITY_LEVEL_RESULTS);
                mPrefsEditor.commit();
//////// state  - end
                startActivityForResult(i, ACTIVITY_LEVEL_RESULTS);
                UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");
                break;
            }
//////// lev res - e
            case GameFlowEvent.EVENT_PLAYER_DIE:
            {
                Game game = (Game)mGame;
                if (game.getNbLives() == 0) {
//////// CONTINUE 20140411 - MID
                    --mNbContinues;
                    if (mNbContinues > 0) {
                        mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_CONTINUE, mNbContinues);
                    } else {
                        mPrefsEditor.remove(PreferenceConstants.PREFERENCE_CONTINUE);
                    }
//////// CONTINUE 20140411 - END

                    onGameFlowEvent(GameFlowEvent.EVENT_GAME_LOST, 0);
                } else {
                    onGameFlowEvent(GameFlowEvent.EVENT_RESTART_LEVEL, 0);
                }
                break;
            }
/*
            case GameFlowEvent.EVENT_SHOW_DIARY:
            {
                Intent i = new Intent(this, DiaryActivity.class);
                LevelTree.Level level = LevelTree.get(mLevelRow, mLevelIndex);
                level.diaryCollected = true;
                i.putExtra("text", level.dialogResources.diaryEntry);
                startActivity(i);
                startTransition();
                break;
            }
*/
            case GameFlowEvent.EVENT_SHOW_DIALOG_CHARACTER:
            {
//////// DIALOGS - BEGIN
/*
                Intent i = new Intent(this, ConversationDialogActivity.class);
                i.putExtra("levelRow", mLevelRow);
                i.putExtra("levelIndex", mLevelIndex);
                i.putExtra("index", index);
// !!!! ???? TODO: needed ? ???? !!!!
//////// state  - mid
//                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_GAME_STATE, ACTIVITY_... DIALOG ?);
//                mPrefsEditor.commit();
//////// state  - end
                startActivity(i);
*/
//////// DIALOGS - MID

                mConversation = LevelTree.get(mLevelRow, mLevelIndex).dialogResources.getConversation(index);
                if (mConversation != null) {
                    mIsDialog = true;
                    mGame.onPause();

//////// DIALOG_RESET 20140327 - MID
                    // if don't do that : Error "wrong thread" : can't touch Views from here.
                    mTv.post(new Runnable() { public void run() { processText(); } } );
//////// DIALOG_RESET 20140327 - END

                    // if don't do that : Error "wrong thread" : can't touch Views from here.
                    mDialog.post( new Runnable() { public void run() { mDialog.setVisibility( View.VISIBLE ); } } );

// !!!! ???? TODO: useless ? ???? !!!!
/*
                    // prevent dialog to play if just hit pause button
                    if (mIsPaused) {
                        mTv.onPause();
                    }
*/
                }
//////// DIALOGS - END
                break;
            }
            case GameFlowEvent.EVENT_SHOW_ANIMATION:
            case GameFlowEvent.EVENT_SHOW_INTRO_ANIMATION:
            case GameFlowEvent.EVENT_SHOW_OUTRO_ANIMATION:
            case GameFlowEvent.EVENT_SHOW_END_GOOD_ANIMATION:
            case GameFlowEvent.EVENT_SHOW_END_BAD_ANIMATION:
            {
/*
                Intent i = new Intent();
                final String animationPlayerClassName = UtilsResources.getClassName(context, "class_name_animation_player");
                i.setClassName(context, animationPlayerClassName);
*/
                Intent i = new Intent(this, AnimationPlayerActivityImpl.class);
                i.putExtra("animation", index);
                i.putExtra("type", eventCode);
//////// state  - mid
                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_GAME_STATE, ACTIVITY_ANIMATION_PLAYER);
                mPrefsEditor.commit();
//////// state  - end
                startActivityForResult(i, ACTIVITY_ANIMATION_PLAYER);
                UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");
                break;
            }
        }
    }


    protected void saveGame() {
        if (mPrefsEditor != null) {
            mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_DIFFICULTY, mDifficulty);

        	if (LevelTree.levelIsValid(mLevelRow, mLevelIndex)) {
                final int completed = LevelTree.packCompletedLevels(mLevelRow);
                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_LEVEL_ROW, mLevelRow);
                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_LEVEL_INDEX, mLevelIndex);
                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_LEVEL_COMPLETED, completed);
//////// CONTINUE 20140411 - MID
                mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_CONTINUE, mNbContinues);
//////// CONTINUE 20140411 - END
            }

//////// REPORTER - BEGIN
//            mPrefsEditor.putLong(PreferenceConstants.PREFERENCE_SESSION_ID, mSessionId);
//////// REPORTER - END
            mPrefsEditor.putFloat(PreferenceConstants.PREFERENCE_TOTAL_GAME_TIME, mTotalGameTime);
            mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_LINEAR_MODE, mLinearMode);
//            mPrefsEditor.putBoolean(PreferenceConstants.PREFERENCE_EXTRAS_UNLOCKED, mExtrasUnlocked);
            mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_LAST_ENDING, mLastEnding);

            saveSpecificPrefs();

            mPrefsEditor.commit();
        }
    }

    private void startEnd() {
        Intent intent = new Intent(this, EndingActivity.class);
        startActivity(intent);
        finish();
    }

    protected void showWaitMessage() {
        if (mWaitMessage != null) {
            mWaitMessage.setVisibility(View.VISIBLE);
            mWaitMessage.startAnimation(mWaitFadeAnimation);
        }
    }

    protected void hideWaitMessage() {
        if (mWaitMessage != null) {
            mWaitMessage.setVisibility(View.GONE);
            mWaitMessage.clearAnimation();
        }
    }


    final private void loadResources(final int resourcesFile) {
        XmlResourceParser parser = (this.getResources().getXml(resourcesFile));

        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("game_won")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("filename")) {
                                mWonAnimId = UtilsResources.getResourceIdByName(getApplicationContext(), "xml", parser.getAttributeValue(i));
                                break;
                            }
                        }
                    } else if (parser.getName().equals("game_lost")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("filename")) {
                                mLostAnimId = UtilsResources.getResourceIdByName(getApplicationContext(), "xml", parser.getAttributeValue(i));
                                break;
                            }
                        }
                    } else if (parser.getName().equals("game_won_background")) {
                        String wonResource = "";
                        String wonResourceType = "drawable";

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("filename")) {
                                wonResource = parser.getAttributeValue(i);
                            } else if (parser.getAttributeName(i).equals("animation")) {
                                wonResourceType = parser.getAttributeBooleanValue(i, false) ? "anim" : "drawable";
                            }
                        }
                        mWonBkgId = UtilsResources.getResourceIdByName(getApplicationContext(), wonResourceType, wonResource);
                    } else if (parser.getName().equals("game_lost_background")) {
                        String lostResource = "";
                        String lostResourceType = "drawable";

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("filename")) {
                                lostResource = parser.getAttributeValue(i);
                            } else if (parser.getAttributeName(i).equals("animation")) {
                                lostResourceType = (parser.getAttributeBooleanValue(i, false)) ? "anim" : "drawable";
                            }
                        }
                        mLostBkgId = UtilsResources.getResourceIdByName(getApplicationContext(), lostResourceType, lostResource);
                    } else if (parser.getName().equals("level_end")) {
                        String levelEndResource = "";
                        String levelEndResourceType = "drawable";

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("filename")) {
                            	levelEndResource = parser.getAttributeValue(i);
                            } else if (parser.getAttributeName(i).equals("animation")) {
                            	levelEndResourceType = parser.getAttributeBooleanValue(i, false) ? "anim" : "drawable";
                            }
                        }
                        mLevelEndBkgId = UtilsResources.getResourceIdByName(getApplicationContext(), levelEndResourceType, levelEndResource);
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            DebugLog.e("Main", e.getStackTrace().toString());
        } finally {
            parser.close();
        }
    }


    // to override if don't want to keep the screen ratio
    protected boolean keepScreenRatio() {
        return true;
    }

//////// DIALOGS - MID
//////// DIALOG_RESET 20140327 - BEGIN
//    private void formatPages(Conversation conversation, TextView textView) {
//////// DIALOG_RESET 20140327 - MID
    private boolean formatPages(Conversation conversation, TextView textView) {
//////// DIALOG_RESET 20140327 - END
        final int maxWidth = textView.getWidth();
        final int maxHeight = textView.getHeight();

//////// DIALOG_RESET 20140327 - MID
        if (maxWidth == 0 || maxHeight == 0) {
	        return false;
        }
//////// DIALOG_RESET 20140327 - END

        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        paint.setTypeface(textView.getTypeface());

        for (int page = conversation.pages.size() - 1; page >= 0 ; page--) {
            ConversationUtils.ConversationPage currentPage = conversation.pages.get(page);
            CharSequence text = currentPage.text;

            // Iterate line by line through the text.
            // Add \n if it gets too wide, and split into a new page if it gets too long.
            int currentOffset = 0;
            int textLength = text.length();
            SpannableStringBuilder spannedText = new SpannableStringBuilder(text);
            int lineCount = 0;
            final float fontHeight = -paint.ascent() + paint.descent();
            final int maxLinesPerPage = (int)(maxHeight / fontHeight);
            CharSequence newline = "\n";
            int addedPages = 0;
            int lastPageStart = 0;

            do {
                int fittingChars = paint.breakText(text, currentOffset, textLength, true, maxWidth, null);

                if (currentOffset + fittingChars < textLength) {
                    fittingChars -= 2;
                    // Text doesn't fit on the line.  Insert a return after the last space.
                    int lastSpace = TextUtils.lastIndexOf(text, ' ', currentOffset + fittingChars - 1);
                    if (lastSpace == -1) {
                        // No spaces, just split at the last character.
                        lastSpace = currentOffset + fittingChars - 1;
                    }
                    spannedText.replace(lastSpace, lastSpace + 1, newline, 0, 1);
                    lineCount++;
                    currentOffset = lastSpace + 1;
                } else {
                    lineCount++;
                    currentOffset = textLength;
                }

                if (lineCount >= maxLinesPerPage || currentOffset >= textLength) {
                    lineCount = 0;
                    if (addedPages == 0) {
                        // overwrite the original page
                        currentPage.text = spannedText.subSequence(lastPageStart, currentOffset);
                    } else {
                        // split into a new page
                        ConversationPage newPage = new ConversationPage();
                        newPage.imageResource = currentPage.imageResource;
                        newPage.text = spannedText.subSequence(lastPageStart, currentOffset);
                        newPage.title = currentPage.title;
                        conversation.pages.add(page + addedPages, newPage);
                    }
                    lastPageStart = currentOffset;
                    addedPages++;
                }
            } while (currentOffset < textLength);
        }
        Runtime.getRuntime().gc();
//////// DIALOG_RESET 20140327 - MID
        return true;
//////// DIALOG_RESET 20140327 - END
    }

    protected void showPage(ConversationUtils.ConversationPage page) {
        mTv.setTypewriterText(page.text);

        mOkArrow.setVisibility(View.INVISIBLE);
        UtilsResources.startIfAnimatable(mOkArrow);

        if (page.imageResource != 0) {
//            mSpeaker.setImageResource(page.imageResource);
            UtilsResources.setAnimatableImageResource(mSpeaker, page.imageResource);
            mSpeaker.setVisibility(View.VISIBLE);
        } else {
            mSpeaker.setVisibility(View.GONE);
        }

        if (page.title != null) {
            mSpeakerName.setText(page.title);
            mSpeakerName.setVisibility(View.VISIBLE);
        } else {
            mSpeakerName.setVisibility(View.GONE);
        }
    }

    public void processText()
    {
        if (mConversation != null) {
            if (!mConversation.splittingComplete) {
//////// DIALOG_RESET 20140327 - BEGIN
/*
                formatPages(mConversation, mTv);
                mConversation.splittingComplete = true;
*/
//////// DIALOG_RESET 20140327 - MID
                if (formatPages(mConversation, mTv)) {
                    mConversation.splittingComplete = true;
                }
//////// DIALOG_RESET 20140327 - END
            }

            if (mPages == null) {
                mPages = mConversation.pages;
                showPage(mPages.get(0));

                mCurrentPage = 0;
            }
        }
    }

    protected void processDialog() {
        if (mTv != null) {
            if (mTv.getRemainingTime() > 0) {
                mTv.snapToEnd();
            } else {
                mCurrentPage++;
                if (mCurrentPage < mPages.size()) {
                    showPage(mPages.get(mCurrentPage));
                } else {
                    mIsDialog = false;
                    mDialog.setVisibility(View.GONE);
                    mConversation = null;
//////// DIALOG_RESET 20140327 - MID
                    mPages = null;
//////// DIALOG_RESET 20140327 - END
                    Runtime.getRuntime().gc();
                    mGame.onResume(this, true);
                }
            }
        }
    }

    private void resumeDialog() {
        if (mTv != null) {
            mTv.onResume();
            mTv.postInvalidate(); // force call to "mTv.onDraw"
        }
    }

    private void unPause() {
        mIsPaused = false;
        mPauseMenu.setVisibility(View.INVISIBLE);
        resumeDialog();
        if (!mIsDialog) {
            mGame.onResume(Main.this, true);
        }
    }
//////// DIALOGS - END

//////// DEBUG_MENU - MID
    private void gotoLevelSelect(final boolean unlockAll) {
DebugLog.e("MAIN", "gotoLevelSelect");
// !!!! ???? TODO: OK ? ???? !!!!
/*
        final String levelClassName = UtilsResources.getClassName(getApplicationContext(), "class_name_levelselect");

        Intent i = new Intent();
        i.setClassName(this, levelClassName);
*/
        Intent i = new Intent(this, LevelSelectActivityImpl.class);
        if (unlockAll) {
            i.putExtra("unlockAll", true);
        }

//////// state  - mid
// !!!! ???? TODO: OK ? needed ? ???? !!!!
        mPrefsEditor.putInt(PreferenceConstants.PREFERENCE_GAME_STATE, ACTIVITY_CHANGE_LEVELS);
        mPrefsEditor.commit();
//////// state  - end

////////
// !!!! ???? TODO: OK ? ???? !!!!
        if (mIsPaused) {
            unPause();
        }
////////

        startActivityForResult(i, ACTIVITY_CHANGE_LEVELS);
    }

    private void debugMethodTracing() {
DebugLog.e("MAIN", "debugMethodTracing (" + !mMethodTracing + ")");
/*
        if (mMethodTracing) {
            Debug.stopMethodTracing();
        } else {
            Debug.startMethodTracing(GAME_NAME);
        }
*/
        mMethodTracing = !mMethodTracing;
    }
//////// DEBUG_MENU - END

    protected abstract Game getGame();


    protected abstract void setViewSize();


    protected abstract void clearSpecificPrefs();

    protected abstract void loadSpecificPrefs(SharedPreferences prefs);

    protected abstract void loadSpecificPrefsCtrl(SharedPreferences prefs);

    protected abstract void saveSpecificPrefs();

    protected abstract void updateSpecifics();


    protected abstract void initSpecifics();

//////// lev res - m
    protected abstract void addLevelResultsData(Intent intent);
//////// lev res - e

    protected abstract void addGameOverData(Intent intent);

}
