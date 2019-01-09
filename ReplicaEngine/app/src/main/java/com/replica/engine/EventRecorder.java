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

public abstract class EventRecorder extends BaseObject {
    private final DifficultyConstants sDifficultyConstants;
	
	private Vector2 mLastDeathPosition = new Vector2();
	private int mLastEnding = -1;

    public EventRecorder(DifficultyConstants difficultyConstants) {
        sDifficultyConstants = difficultyConstants;
    }
	
	@Override
	public void reset() {
	}
	
	public synchronized void setLastDeathPosition(Vector2 position) {
		mLastDeathPosition.set(position);
	}
	
	public synchronized Vector2 getLastDeathPosition() {
		return mLastDeathPosition;
	}
	
	public synchronized void setLastEnding(int ending) {
		mLastEnding = ending;
	}
	
	public synchronized int getLastEnding() {
		return mLastEnding;
	}
	
// !!!! TODO : should have a more generic array handled here !!!!
//	synchronized abstract void incrementEventCounter(int event);
    public abstract void incrementEventCounter(int event, int value);

// !!!! TODO : should have a generic "get" function !!!!
// => eg: void getEventCounter( int event ) { return mArray[ i ]; }

//// diff - b
    public DifficultyConstants getDifficultyConstants() {
        return sDifficultyConstants;
    }
//// diff - m
}
