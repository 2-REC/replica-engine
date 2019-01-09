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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
//import android.view.View;
//////// sensor - b
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
//////// sensor - m

//////// sensor - b
public abstract class MainSpecific extends Main implements SensorEventListener {
//////// sensor - m
//public abstract class MainPhone extends Main {
//////// sensor - e
//////// sensor - b
    private SensorManager mSensorManager;
//////// sensor - m

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//////// sensor - b
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
//////// sensor - m
    }

    @Override
    protected void onPause() {
        super.onPause();

//////// sensor - b
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
//////// sensor - m
    }

    @Override
    protected void onResume() {
        super.onResume();

//////// sensor - b
        if (mSensorManager != null) {
            Sensor orientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            if (orientation != null) {
                mSensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_GAME, null);
            }
        }
//////// sensor - m
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = true;
        if (!mGame.isPaused()) {
            mGame.onTouchEvent(event);

            final long time = System.currentTimeMillis();
            if (event.getAction() == MotionEvent.ACTION_MOVE &&
                    (time - mLastInputTime < 32 )) {
                // Sleep so that the main thread doesn't get flooded with UI events
                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                    // No big deal if this sleep is interrupted
                }
                mGame.getRenderer().waitDrawingComplete();
            }
            mLastInputTime = time;
//////// DIALOGS - MID
        } else {
            result = super.onTouchEvent(event);
//////// DIALOGS - END
        }

        return result;
    }

//////// sensor - b
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
// !!!! ???? TODO : Sleep so that the main thread doesn't get flooded with UI events ? ???? !!!!
                final float x = event.values[1];
                final float y = event.values[2];
                final float z = event.values[0];
                mGame.onOrientationEvent(x, y, z);
            }
        }
    }
//////// sensor - m

    protected void loadSpecificPrefsCtrl(SharedPreferences prefs) {
    }

}
