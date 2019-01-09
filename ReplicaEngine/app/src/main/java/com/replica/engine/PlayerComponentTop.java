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

/**
 * Abstract class defining a "Top Player" (e.g.: RPG, shoot, etc.).
 */
public abstract class PlayerComponentTop extends PlayerComponent {
    protected float mGroundMaxSpeed;
    protected float mGroundImpulseSpeed;


    @Override
    public void reset() {
        super.reset();

        mGroundMaxSpeed = 500.0f;
        mGroundImpulseSpeed = 500.0f;
    }

/*
    @Override
    protected void preUpdateProcess(GameObject parent) {
    }

    @Override
    protected void postUpdateProcess(GameObject parent) {
    }
*/

    @Override
    protected void handleMovement(GameObject parentObject, Vector2 impulse, float timeDelta) {
// !!!! TODO: change speed management !!!!
// => can either have a maximum horizontal+vertical speed, or independent speeds ...
        float horizontalSpeed = mGroundImpulseSpeed;
        float maxHorizontalSpeed = mGroundMaxSpeed;

        float verticalSpeed = mGroundImpulseSpeed;
        float maxVerticalSpeed = mGroundMaxSpeed;

//        final boolean inTheAir = !mTouchingGround;

//=> change if want different speed while "in air"
/*
        if (inTheAir) {
            horizontalSpeed = mAirImpulseSpeed;
            maxHorizontalSpeed = mAirMaxSpeed;

            verticalSpeed = mAirImpulseSpeed;
            maxVerticalSpeed = mAirMaxSpeed;
        } 
*/

        impulse.x = impulse.x * horizontalSpeed * timeDelta;
        impulse.y = impulse.y * verticalSpeed * timeDelta;

        // limit speed
        //impulse.normalise(mGroundMaxSpeed);
        float currentSpeed = parentObject.getVelocity().x;
        float newSpeed = Math.abs(currentSpeed + impulse.x);
        if (newSpeed > maxHorizontalSpeed) {
            if (Math.abs(currentSpeed) < maxHorizontalSpeed) {
                currentSpeed = maxHorizontalSpeed * Utils.sign(impulse.x);
                parentObject.getVelocity().x = currentSpeed;
            }
            impulse.x = 0.0f;
        }

        currentSpeed = parentObject.getVelocity().y;
        newSpeed = Math.abs(currentSpeed + impulse.y);
        if (newSpeed > maxVerticalSpeed) {
            if (Math.abs(currentSpeed) < maxVerticalSpeed) {
                currentSpeed = maxVerticalSpeed * Utils.sign(impulse.y);
                parentObject.getVelocity().y = currentSpeed;
            }
            impulse.y = 0.0f;
        }

//        if (inTheAir)
//        ...
    }

    @Override
    protected boolean shouldDieAtPosition(GameObject parentObject) {
        return false;
    }


    public void setGroundMaxSpeed(float maxSpeed) {
    	mGroundMaxSpeed = maxSpeed;
    }

    public void setGroundImpulseSpeed(float speed) {
    	mGroundImpulseSpeed = speed;
    }

}
