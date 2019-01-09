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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class ResultsActivity extends Activity {
    protected ArrayList<IncrementingTextView> mIncTextViews = new ArrayList<IncrementingTextView>();

    private LayoutInflater mInflater;
    private ViewGroup mParent;

    public static class IncrementingTextView extends TextView {
        private static final int INCREMENT_DELAY_MS = 2 * 1000;
        public static final int MODE_NONE = 0;
        public static final int MODE_PERCENT = 1;
        public static final int MODE_TIME = 2;

        private boolean mAsInt = false;
        private float mTargetValue = 0.0f;
        private float mIncrement = 1.0f;
        private float mCurrentValue = 0.0f;
        private int mSound = SoundManager.SOUND_NONE;
        private long mLastTime = 0;
        private int mMode = MODE_NONE;
        private boolean mDone = false;

        public IncrementingTextView(Context context) {
            super(context);
        }

        public IncrementingTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public IncrementingTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public void setAsInt(boolean integerResult) {
            mAsInt = integerResult;
        }

        public void setTargetValue(float target) {
            mTargetValue = target;
            postInvalidate();
        }

        public void setMode(int mode) {
            mMode = mode;
        }

        public void setIncrement(float increment) {
            mIncrement = increment;
        }

        public void setSound(int sound) {
            mSound = sound;
        }

        public boolean isDone() {
            return mDone;
        }

        public void skipIncrement() {
            // almost set to end value to force refresh
        	mCurrentValue = mTargetValue - mIncrement;
        }

        @Override
        public void onDraw(Canvas canvas) {
            final long time = SystemClock.uptimeMillis();
            final long delta = time - mLastTime;
            if (delta > INCREMENT_DELAY_MS) {
                if (mCurrentValue < mTargetValue) {
                    SoundManager.play(mSound);

                    mCurrentValue += mIncrement;
                    mCurrentValue = Math.min(mCurrentValue, mTargetValue);
                    String value;
                    if (mMode == MODE_PERCENT) {
                        if (mAsInt) {
                            int i = (int)mCurrentValue;
                            value = i + "%";
                        } else {
                            value = mCurrentValue + "%";
                        }

                    } else if (mMode == MODE_TIME) {
                        float seconds = mCurrentValue;
                        float minutes = seconds / 60.0f;
                        float hours = minutes / 60.0f;

                        int totalHours = (int)Math.floor(hours);
                        float totalHourMinutes = totalHours * 60.0f;
                        int totalMinutes = (int)(minutes - totalHourMinutes);
                        float totalMinuteSeconds = totalMinutes * 60.0f;
                        float totalHourSeconds = totalHourMinutes * 60.0f;
                        int totalSeconds = (int)(seconds - (totalMinuteSeconds + totalHourSeconds));

                        value = totalHours + ":" + totalMinutes + ":" + totalSeconds;

                    } else {
                        if (mAsInt) {
                            int i = (int)mCurrentValue;
                            value = i + "";
                        } else {
                            value = mCurrentValue + "";
                        }
                    }
                    setText(value);
                    postInvalidateDelayed(INCREMENT_DELAY_MS);

                } else {
                    mDone = true;
                }
            }
            super.onDraw(canvas);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();

        final Intent callingIntent = getIntent();

        final int backgroundRes = callingIntent.getIntExtra("background", 0);
        final String title = callingIntent.getStringExtra("title");
        final String text = callingIntent.getStringExtra("text");

        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(UtilsResources.getResourceIdByName(context, "layout", "results_screen"));

        if (backgroundRes != 0) {
            final ImageView backgroundView = (ImageView)findViewById(UtilsResources.getResourceIdByName(context, "id", "background"));
            UtilsResources.setAnimatableImageResource(backgroundView, backgroundRes);
        }
        final TextView titleView = (TextView)findViewById(UtilsResources.getResourceIdByName(context, "id", "title"));
        titleView.setText(title);
        final TextView textView = (TextView)findViewById(UtilsResources.getResourceIdByName(context, "id", "text"));
        textView.setText(text);

        mParent =(ViewGroup)findViewById(UtilsResources.getResourceIdByName(context, "id", "linear_layout"));

        addContent();


//        final float playTime = prefs.getFloat(PreferenceConstants.PREFERENCE_TOTAL_GAME_TIME, 0.0f);
//        final int ending = prefs.getInt(PreferenceConstants.PREFERENCE_LAST_ENDING, -1);

/*
//////// GAME - BEGIN
// !!!! TODO: see what we want to do for endings !!!!
        if (ending == AnimationPlayerActivity.ENDING_KO) {
            mEndingView.setText(R.string.game_results_bad_ending);
// !!!! ???? TODO: want more endings ? ???? !!!!
//        } else if (ending == AnimationPlayerActivity.ENDING_KO_2) {
//            mEndingView.setText(R.string.game_results_bad_ending_2);
        } else {
            mEndingView.setText(R.string.game_results_ending);
        }
//////// GAME - END
*/

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.start(this, getMusic());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        skip();
        return true;
    }

    private boolean isDone() {
        boolean isDone = true;
        for (int i = 0; i < mIncTextViews.size(); ++i) {
            isDone &= mIncTextViews.get(i).isDone();
        }
        return isDone;
    }

    private void skipCounters() {
        for (int i = 0; i < mIncTextViews.size(); ++i) {
            mIncTextViews.get(i).skipIncrement();
        }
    }


    protected void skip() {
        if (isDone()) {
            leave();
        } else {
            skipCounters();
        }

        try {
            Thread.sleep(32);
        } catch (InterruptedException e) {
            // Safe to ignore
        }
    }

    protected void leave() {
        SoundManager.play(SoundManager.SOUND_OK);
        MusicManager.stop();

        finish();

        UtilsActivities.startTransition(this, "activity_fade_in", "activity_fade_out");
    }

    protected int getMusic() {
// !!!! ???? TODO: OK to play previous when nothing set ? ???? !!!!
// => music shouldn't be stopped in game in this case ...
        return MusicManager.MUSIC_PREVIOUS;
    }

// !!!! TODO: should add "increment" parameter !!!!
    protected final void addPercentageResult(final String label,
            final String stringCollected, final String stringTotal, final int sound) {
        final Context context = getApplicationContext();
        final LinearLayout linearLayout = (LinearLayout)mInflater.inflate(
                UtilsResources.getResourceIdByName(context, "layout", "results_screen_entry"),
                (ViewGroup)mParent, false);

        final TextView textView = (TextView)linearLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "text_view"));
        textView.setText(UtilsResources.getResourceIdByName(context, "string", label));

        final IncrementingTextView resultView = (IncrementingTextView)linearLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "inc_text_view"));
        mIncTextViews.add(resultView);

        final Intent callingIntent = getIntent();
        final int collected = callingIntent.getIntExtra(stringCollected, 0);
        final int total = callingIntent.getIntExtra(stringTotal, 0);
        if (collected > 0 && total > 0) {
            resultView.setTargetValue((int)((collected / (float)total) * 100.0f));

        } else {
            resultView.setText("--");
        }

        resultView.setMode(IncrementingTextView.MODE_PERCENT);

        resultView.setSound(sound);

        mParent.addView(linearLayout);
    }

    protected final void addTimerResult(final String label,
        final String stringTimeEllapsed, final int sound) {
        final Context context = getApplicationContext();
        final LinearLayout linearLayout = (LinearLayout)mInflater.inflate(
                UtilsResources.getResourceIdByName(context, "layout", "results_screen_entry"),
                (ViewGroup)mParent, false);

        final TextView textView = (TextView)linearLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "text_view"));
        textView.setText(UtilsResources.getResourceIdByName(context, "string", label));

        final IncrementingTextView resultView = (IncrementingTextView)linearLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "inc_text_view"));
        mIncTextViews.add(resultView);

        final Intent callingIntent = getIntent();
        final int time = callingIntent.getIntExtra(stringTimeEllapsed, 0);
        if (time > 0) {
            resultView.setTargetValue(time);
        } else {
            resultView.setText("--");
        }

        resultView.setMode(IncrementingTextView.MODE_TIME);

        resultView.setSound(sound);

        mParent.addView(linearLayout);
    }

    protected final void addCounterResult(final String label,
            final String stringCollected, final int sound) {
        final Context context = getApplicationContext();
        final LinearLayout linearLayout = (LinearLayout)mInflater.inflate(
                UtilsResources.getResourceIdByName(context, "layout", "results_screen_entry"),
                (ViewGroup)mParent, false);

        final TextView textView = (TextView)linearLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "text_view"));
        textView.setText(UtilsResources.getResourceIdByName(context, "string", label));

        final IncrementingTextView resultView = (IncrementingTextView)linearLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "inc_text_view"));
        mIncTextViews.add(resultView);

        final Intent callingIntent = getIntent();
        final int collected = callingIntent.getIntExtra(stringCollected, 0);
        if (collected > 0) {
            resultView.setTargetValue(collected);
        } else {
            resultView.setText("--");
        }

        resultView.setMode(IncrementingTextView.MODE_NONE);
        resultView.setAsInt(true);

        resultView.setSound(sound);

        mParent.addView(linearLayout);
    }


    protected abstract void addContent();

}
