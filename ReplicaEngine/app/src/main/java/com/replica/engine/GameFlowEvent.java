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

public class GameFlowEvent implements Runnable {
    public static final int EVENT_INVALID = -1;
    public static final int EVENT_START_LEVEL = 0;
    public static final int EVENT_END_LEVEL = 1;
    public static final int EVENT_RESUME_LEVEL = 2;
    public static final int EVENT_RESTART_LEVEL = 3;
    public static final int EVENT_GO_TO_NEXT_LEVEL = 4;
    public static final int EVENT_GAME_LOST = 5;
    public static final int EVENT_GAME_WON = 6;
    public static final int EVENT_END_GAME = 7;
    public static final int EVENT_SHOW_RESULTS_GAME = 8;
//////// lev res - m
    public static final int EVENT_SHOW_RESULTS_LEVEL = 9;
//////// lev res - e
    public static final int EVENT_PLAYER_DIE = 10;

    public static final int EVENT_SHOW_ANIMATION = 11;
    public static final int EVENT_SHOW_INTRO_ANIMATION = 12;
    public static final int EVENT_SHOW_OUTRO_ANIMATION = 13;
    public static final int EVENT_SHOW_END_GOOD_ANIMATION = 14;
    public static final int EVENT_SHOW_END_BAD_ANIMATION = 15;
    public static final int EVENT_SHOW_DIALOG_CHARACTER = 16;
//    public static final int EVENT_SHOW_DIARY = 101;

    private int mEventCode;
    private int mDataIndex;
    private Main mMainActivity;
    
    public void post(int event, int index, Context context) {
        if (context instanceof Main) {
        	DebugLog.d("GameFlowEvent", "Post Game Flow Event: " + event + ", " + index);
            mEventCode = event;
            mDataIndex = index;
            mMainActivity = (Main)context;
            mMainActivity.runOnUiThread(this);
        }
    }
    
    public void postImmediate(int event, int index, Context context) {
        if (context instanceof Main) {
        	DebugLog.d("GameFlowEvent", "Execute Immediate Game Flow Event: " + event + ", " + index);
            mEventCode = event;
            mDataIndex = index;
            mMainActivity = (Main)context;
            mMainActivity.onGameFlowEvent(mEventCode, mDataIndex);
        }
    }
    
    public void run() {
        if (mMainActivity != null) {
        	DebugLog.d("GameFlowEvent", "Execute Game Flow Event: " + mEventCode + ", " + mDataIndex);
            mMainActivity.onGameFlowEvent(mEventCode, mDataIndex);
            mMainActivity = null;
        }
    }

}
