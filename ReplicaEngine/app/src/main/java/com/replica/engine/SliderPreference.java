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

import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.view.KeyEvent;

public class SliderPreference extends Preference implements OnSeekBarChangeListener, View.OnKeyListener {
	private final static int MAX_SLIDER_VALUE = 100;
	private final static int INITIAL_VALUE = 50;
	
	private int mValue = INITIAL_VALUE;
	private String mMinText;
	private String mMaxText;
	private Context mContext;
	private SeekBar mBar;
	protected boolean mHack;


	public SliderPreference(Context context) {
		super(context);

		mContext = context;
		setWidgetLayoutResource(UtilsResources.getResourceIdByName(context, "layout", "slider_preference"));
	}

	public SliderPreference(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.preferenceStyle);
		mContext = context;

		setWidgetLayoutResource(UtilsResources.getResourceIdByName(context, "layout", "slider_preference"));
	}

	public SliderPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;

		TypedArray a = context.obtainStyledAttributes(attrs,
				UtilsResources.getResourceDeclareStyleableIntArray(context, "SliderPreference"), defStyle, 0);
		mMinText = a.getString(UtilsResources.getResourceIdByName(context, "styleable", "SliderPreference_minText"));
		mMaxText = a.getString(UtilsResources.getResourceIdByName(context, "styleable", "SliderPreference_maxText"));

        a.recycle();

		setWidgetLayoutResource(UtilsResources.getResourceIdByName(context, "layout", "slider_preference"));
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		if (mMinText != null) {
			TextView minText = (TextView)view.findViewById(UtilsResources.getResourceIdByName(mContext, "id", "min"));
			minText.setText(mMinText);
		}

		if (mMaxText != null) {
			TextView maxText = (TextView)view.findViewById(UtilsResources.getResourceIdByName(mContext, "id", "max"));
			maxText.setText(mMaxText);
		}

		mBar = (SeekBar)view.findViewById(UtilsResources.getResourceIdByName(mContext, "id", "slider"));
		mBar.setMax(MAX_SLIDER_VALUE);
		mBar.setProgress(mValue);
		mBar.setOnSeekBarChangeListener(this);

		view.setOnKeyListener(this);
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mBar != null) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && mValue > 0) {
					--mValue;
				} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && mValue < MAX_SLIDER_VALUE) {
					++mValue;
				} else {
					return false;
				}

				mBar.setProgress(mValue);
				mHack = true;
				return true;
			}
		}
		return false;
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser || mHack) {
			mHack = false;
			mValue = progress;
			persistInt(mValue);
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}


	@Override 
	protected Object onGetDefaultValue(TypedArray ta, int index) {
		int dValue = (int)ta.getInt(index, INITIAL_VALUE);

		return (int)Utils.clamp(dValue, 0, MAX_SLIDER_VALUE);
	}


	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		mValue = defaultValue != null ? (Integer)defaultValue : INITIAL_VALUE;

		if (!restoreValue) {
			persistInt(mValue);
		} else {
			mValue = getPersistedInt(mValue);
		}
	}


}
