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
 * Manages the position of an object based on a target game object.
 */
public class FollowComponent extends GameComponent {
    private Vector2 mCurrentPosition;

    private GameObject mTarget;
    private boolean mAttached;
    private float mFollowOffsetX;
    private boolean mFollowOffsetXAbsolute;
    private float mFollowOffsetY;
    private boolean mFollowOffsetYAbsolute;

    private Vector2 mTargetPosition;
    private Vector2 mTargetDirection;
    private Vector2 mAcceleration;

    private float mImpulse;
    private float mMaxSpeed;


    public FollowComponent() {
        super();

        mCurrentPosition = new Vector2();

        mTargetPosition = new Vector2();
        mTargetDirection = new Vector2();
        mAcceleration = new Vector2();

// !!!! TODO: when "attached", small delay when object moves fast !!!!
// => should change priority? (or add target's velocity to position?
        setPhase(ComponentPhases.PHYSICS.ordinal());
    }

    @Override
    public void reset() {
        mTarget = null;
        mAttached = false;
        mFollowOffsetX = 0.0f;
        mFollowOffsetXAbsolute = false;
        mFollowOffsetY = 0.0f;
        mFollowOffsetYAbsolute = false;

        //mCurrentPosition.zero();
        //mTargetPosition.zero();
    	//mTargetDirection.zero();
        //mAcceleration.zero();

        mImpulse = 0.0f;
        mMaxSpeed = 0.0f;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        if (mTarget != null) {
            if (!mAttached) {
                final GameObject parentObject = (GameObject)parent;
//                mCurrentPosition.set(parentObject.getCenteredPositionX(), parentObject.getCenteredPositionY());
                mCurrentPosition.set(parentObject.getCenteredPositionX() + parentObject.getVelocity().x,
                        parentObject.getCenteredPositionY() + parentObject.getVelocity().y);
                final Vector2 currentPosition = mCurrentPosition;

// !!!! ???? TODO: '+' or '-' offsets (y?) ? ???? !!!!
                final float offsetX = (mFollowOffsetXAbsolute) ? mFollowOffsetX : (mFollowOffsetX * mTarget.facingDirection.x);
                final float offsetY = (mFollowOffsetYAbsolute) ? mFollowOffsetY : (mFollowOffsetY * mTarget.facingDirection.y);
                mTargetPosition.set(mTarget.getCenteredPositionX() + offsetX, mTarget.getCenteredPositionY() + offsetY);
                final Vector2 targetPosition = mTargetPosition;

                mTargetDirection.set(targetPosition);
                mTargetDirection.subtract(currentPosition);
                final Vector2 targetDirection = mTargetDirection;

                mAcceleration.set(targetDirection);
                mAcceleration.normalize();
                mAcceleration.multiply(timeDelta * mImpulse);
               final Vector2 acceleration = mAcceleration;

                parentObject.getVelocity().add(acceleration);
// !!!! ???? TODO: OK ? ???? !!!!
                if (mMaxSpeed > 0.0f) {
                    parentObject.getVelocity().limitMax(mMaxSpeed);
                }

            } else {
                final float offsetX = (mFollowOffsetXAbsolute) ? mFollowOffsetX : (mFollowOffsetX * mTarget.facingDirection.x);
                final float offsetY = (mFollowOffsetYAbsolute) ? mFollowOffsetY : (mFollowOffsetY * mTarget.facingDirection.y);

// !!!! TODO: handle offset from parent object !!!!
                final GameObject parentObject = (GameObject)parent;
//                parentObject.getPosition().set(mTarget.getCenteredPositionX() + offsetX, mTarget.getCenteredPositionY() + offsetY);
                parentObject.getPosition().x = mTarget.getCenteredPositionX() + offsetX - (parentObject.width / 2.0f);
                parentObject.getPosition().y = mTarget.getCenteredPositionY() + offsetY - (parentObject.height / 2.0f);
            }
        }
    }

    public final void setTarget(GameObject target) {
        mTarget = target;
    }

    public final void setAttached(boolean attached) {
        mAttached = attached;
    }

    public final void setImpulse(float impulse) {
        mImpulse = impulse;
    }

    public final void setMaxSpeed(float maxSpeed) {
        mMaxSpeed = maxSpeed;
    }

    public final void setOffsetX(float offsetX, boolean absolute) {
        mFollowOffsetX = offsetX;
        mFollowOffsetXAbsolute = absolute;
    }

    public final void setOffsetY(float offsetY, boolean absolute) {
        mFollowOffsetY = offsetY;
        mFollowOffsetYAbsolute = absolute;
    }

/*
    public Vector2 getGravity() {
        return mGravity;
    }
*/

/*
????
    public void setGravityMultiplier(float multiplier) {
        mGravity.set( sDefaultGravity );
        mGravity.multiply( multiplier );
    }
*/

}
