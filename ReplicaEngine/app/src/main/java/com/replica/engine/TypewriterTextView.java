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
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class TypewriterTextView extends TextView {

    private final static float TEXT_CHARACTER_DELAY = 0.1f;
    private final static int TEXT_CHARACTER_DELAY_MS = (int)(TEXT_CHARACTER_DELAY * 1000);

    private int mCurrentCharacter;
    private long mLastTime;
    private CharSequence mText;
    private View mOkArrow;
    private Main mParent;
    private boolean mPaused = false;

    public TypewriterTextView(Context context) {
        super(context);
    }

    public TypewriterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypewriterTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setParentActivity(Main parent) {
        mParent = parent;
    }

    public void setTypewriterText(CharSequence text) {
        mText = text;
        mCurrentCharacter = 0;
        mLastTime = 0;
        postInvalidate();
    }

    public long getRemainingTime() {
        return ((mText.length() - mCurrentCharacter) * TEXT_CHARACTER_DELAY_MS);
    }

    public void snapToEnd() {
        mCurrentCharacter = mText.length() - 1; 
    }

    public void setOkArrow(View arrow) {
        mOkArrow = arrow;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // We need to wait until layout has occurred before we can setup the
        // text page.  Ugh.  Bidirectional dependency!
        if (mParent != null) {
            mParent.processText();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onDraw(Canvas canvas) {
// !!!! ???? TODO: OK here ? ???? !!!!
        if (!mPaused) {
            final long time = SystemClock.uptimeMillis();
            final long delta = time - mLastTime;
            if (delta > TEXT_CHARACTER_DELAY_MS) {
                if (mText != null) {
                    if (mCurrentCharacter <= mText.length()) {
                        CharSequence subtext = mText.subSequence(0, mCurrentCharacter);
                        setText(subtext, TextView.BufferType.SPANNABLE);
                        mCurrentCharacter++;
                        postInvalidateDelayed(TEXT_CHARACTER_DELAY_MS);
                    } else {
                        if (mOkArrow != null) {
                            mOkArrow.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
        super.onDraw(canvas);
    }

    public void onPause() {
        mPaused = true;
    }

    public void onResume() {
        mPaused = false;
    }

}
