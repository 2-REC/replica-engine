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
 * Abstract class defining a generic "Side Player" (e.g.: platformer, beat'em'all, etc.).
 */
public abstract class PlayerComponentSide extends PlayerComponent {
    protected boolean mTouchingGround;

    private float mGroundMaxSpeed;
    private float mGroundHorizontalImpulseSpeed;
    private float mGroundVerticalImpulseSpeed;

// => change if want to handle different speeds in air, and double jump
//    private float mAirHorizontalMaxSpeed;
//    private float mAirHorizontalImpulseSpeed;
    private float mAirVerticalMaxSpeed;
//    private float mAirVerticalImpulseSpeed;

    private float mAirDragSpeed;

//    private float mJumpToJumpDelay;
//    private float mJumpTime;


    @Override
    public void reset() {
        super.reset();

        mTouchingGround = false;

        mGroundMaxSpeed = 500.0f;
        mGroundHorizontalImpulseSpeed = 500.0f;
        mGroundVerticalImpulseSpeed = 250.0f;

//        mAirHorizontalMaxSpeed = 150;
//        mAirHorizontalImpulseSpeed = 4000.0f;
        mAirVerticalMaxSpeed = 250.0f;
//        mAirVerticalImpulseSpeed = 1200.0f;

        mAirDragSpeed = 400.0f; // 4000.0f?

//        mJumpToJumpDelay = 0.5f;
//        mJumpTime = 0.0f;
    }

    @Override
    protected void preUpdateProcess(GameObject parent) {
        final GameObject parentObject = (GameObject)parent;
        mTouchingGround = parentObject.touchingGround();
    }

/*
    @Override
    protected void postUpdateProcess(GameObject parent) {
    }
*/

    @Override
    protected void handleMovement(GameObject parentObject, Vector2 impulse,
            float timeDelta) {
        float horizontalSpeed = mGroundHorizontalImpulseSpeed;
        float maxHorizontalSpeed = mGroundMaxSpeed;
        final boolean inTheAir = !mTouchingGround;

        final float maxVerticalSpeed = mAirVerticalMaxSpeed;

//=> change if want different speed while "in air"
/*
        if (inTheAir) {
            horizontalSpeed = mAirHorizontalImpulseSpeed;
            maxHorizontalSpeed = mAirHorizontalMaxSpeed;
        } 
*/

        impulse.x = impulse.x * horizontalSpeed * timeDelta;

        if (impulse.y != 0.0f) {
            // instant velocity so no need to scale it by time
            impulse.y = mGroundVerticalImpulseSpeed;
//            mJumpTime = time;
        }

        // limit speed
        float currentSpeed = parentObject.getVelocity().x;
        final float newSpeed = Math.abs(currentSpeed + impulse.x);
        if (newSpeed > maxHorizontalSpeed) {
            if (Math.abs(currentSpeed) < maxHorizontalSpeed) {
                currentSpeed = maxHorizontalSpeed * Utils.sign(impulse.x);
                parentObject.getVelocity().x = currentSpeed;
            }
            impulse.x = 0.0f; 
        }

        if ((parentObject.getVelocity().y + impulse.y > maxVerticalSpeed) &&
                (Utils.sign(impulse.y) > 0)) {
            impulse.y = 0.0f;
            if (parentObject.getVelocity().y < maxVerticalSpeed) {
                parentObject.getVelocity().y = maxVerticalSpeed;
            }
        }

        if (inTheAir) {
// !!!! ???? TODO : what does this do ? ???? !!!!
            // Apply drag while in the air
            if (Math.abs(currentSpeed) > maxHorizontalSpeed) {
                float postDragSpeed = currentSpeed - (mAirDragSpeed * timeDelta * Utils.sign(currentSpeed));
                if (Utils.sign(currentSpeed) != Utils.sign(postDragSpeed)) {
                    postDragSpeed = 0.0f;
                } else if (Math.abs(postDragSpeed) < maxHorizontalSpeed) {
                    postDragSpeed = maxHorizontalSpeed * Utils.sign(postDragSpeed);
                }
                parentObject.getVelocity().x = postDragSpeed;
            }
        }
    }

    @Override
    protected boolean shouldDieAtPosition(GameObject parentObject) {
        return (parentObject.getPosition().y < -parentObject.height); // gets out of screen
    }

/*
    @Override
    protected int getHotSpot(GameObject parentObject) {
        int hotSpot = HotSpotSystem.HotSpotType.NONE;

        final HotSpotSystem hotSpotSystem = sSystemRegistry.hotSpotSystem;
        if (hotSpotSystem != null) {
            hotSpot = hotSpotSystem.getHotSpot(parentObject.getCenteredPositionX(),
                    parentObject.getPosition().y + 10.0f);
        }
        return hotSpot;
    }
*/


    public void setGroundMaxSpeed(float maxSpeed) {
    	mGroundMaxSpeed = maxSpeed;
    }

    public void setGroundHorizontalImpulseSpeed(float speed) {
    	mGroundHorizontalImpulseSpeed = speed;
    }

    public void setGroundVerticalImpulseSpeed(float speed) {
        mGroundVerticalImpulseSpeed = speed;
    }

    public void setAirVerticalMaxSpeed(float maxSpeed) {
    	mAirVerticalMaxSpeed = maxSpeed;
    }

    public void setAirDragSpeed(float speed) {
    	mAirDragSpeed = speed;
    }

}
