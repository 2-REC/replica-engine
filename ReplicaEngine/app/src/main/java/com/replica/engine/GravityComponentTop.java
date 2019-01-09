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

// !!!! TODO: check !!!!

/**
 * A game component that implements gravity.  Adding this component to a game object will cause
 * it to be pulled down towards the ground.
 */
public class GravityComponentTop extends GameComponent {
    private float mGravity;
//    private float mScaledGravity;
    private static final float sDefaultGravity = -400.0f;

    public GravityComponentTop() {
        super();
        mGravity = sDefaultGravity;
//        mScaledGravity = 0;
        setPhase(ComponentPhases.PHYSICS.ordinal());
    }

    @Override
    public void reset() {
        mGravity = sDefaultGravity;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
/*
        mScaledGravity = mGravity * timeDelta;
        ((GameObject)parent).getVelocity().add(mScaledGravity);
*/
    }

/*
    public Vector2 getGravity() {
        return mGravity;
    }
*/
    public float getGravity() {
        return mGravity;
    }

    public void setGravityMultiplier(float multiplier) {
        mGravity = sDefaultGravity * multiplier;
    }
}
