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

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.WindowManager;

/**
 * Dialog box to receive a button press to configure controls on OUYA.
 */
public class ButtonDialogPreference extends DialogPreference
                                        implements DialogInterface.OnKeyListener {
    private ButtonDialogListener mListener;

    private int oldValue;
    private int newValue;

    public abstract interface ButtonDialogListener {
        public abstract void onDialogClosed(String key, int value);
    }


    public ButtonDialogPreference(Context context, AttributeSet attrs) {
//?        this(context, attrs, android.R.attr.yesNoPreferenceStyle);

        super(context, attrs);

        setPositiveButtonText("");
        setNegativeButtonText("");
        setDefaultValue( 0 );
    }

// !!!! ???? TODO: needed ? ???? !!!!
/*
    public ButtonDialogPreferenceOuya(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPositiveButtonText("");
        setNegativeButtonText("");
    }
*/
    public ButtonDialogPreference(Context context) {
        this(context, null);
    }

    public void setListener(ButtonDialogListener listener) {
        mListener = listener;
    }

    public final int getKeyValue() {
        return newValue;
    }

// !!!! ???? TODO: never called ? ???? !!!!
    public final void setKey(int key) {
        oldValue = newValue = key;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);

        builder.setOnKeyListener(this);
        builder.setMessage(Controls.convertKeyCode(oldValue));
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        final Dialog dialog = getDialog();
        if (dialog != null) {
// !!!! ???? TODO: need that ? ???? !!!!
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (!positiveResult) {
            newValue = oldValue;
        } else {
            if (mListener != null) {
                mListener.onDialogClosed(getKey(), newValue);
            }
            oldValue = newValue;
            persistInt(newValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        oldValue = (restoreValue ? getPersistedInt(0) : ((Integer)defaultValue).intValue());
        newValue = oldValue;
    }

    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (!isKeyConfigurable(keyCode)) {
            return false;
        }

        newValue = keyCode;
        super.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
        dialog.dismiss();
        return true;
    }

    private boolean isKeyConfigurable(int keyCode) {
        switch (keyCode) {
// !!!! TODO: adapt !!!!
            case KeyEvent.KEYCODE_MENU:
/*
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
*/
            return false;
        }
        return true;
    }

}
