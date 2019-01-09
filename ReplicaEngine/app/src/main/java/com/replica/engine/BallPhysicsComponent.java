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
 * A light-weight physics implementation for use with ball objects.
 */
public class BallPhysicsComponent extends GameComponent {
    private static final float DEFAULT_BOUNCINESS = 0.1f;
    private static final float DEFAULT_MINSPEED = 0.0f;
    private static final float DEFAULT_MAXSPEED = 250.0f;

    private float mBounciness;
    private float mMinSpeed;
    private float mMaxSpeed;

    public BallPhysicsComponent() {
        super();
        setPhase(GameComponent.ComponentPhases.POST_PHYSICS.ordinal());
        reset();
    }

    @Override
    public void reset() {
        mBounciness = DEFAULT_BOUNCINESS;
        mMinSpeed = DEFAULT_MINSPEED;
        mMaxSpeed = DEFAULT_MAXSPEED;
    }

    public void setBounciness(float bounciness) {
        mBounciness = bounciness;
    }

    public void setMaxSpeed(float speed) {
        mMaxSpeed = speed;
    }

    public void setMinSpeed(float speed) {
        mMinSpeed = speed;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject) parent;

        final Vector2 impulse = parentObject.getImpulse();
        float velocityX = parentObject.getVelocity().x + impulse.x;
        float velocityY = parentObject.getVelocity().y + impulse.y;

        if ((parentObject.touchingCeiling() && velocityY > 0.0f)
            || (parentObject.touchingGround() && velocityY < 0.0f)) {
            // force bounce power to be in the accepted range
            final int sign = Utils.sign(velocityY);
            velocityY *= sign;

            if (velocityY > mMaxSpeed) {
                velocityY = mMaxSpeed;
            } else if (velocityY < mMinSpeed) {
                velocityY = mMinSpeed;
            }
            velocityY *= -sign;
        }

        if ((parentObject.touchingRightWall() && velocityX > 0.0f)
            || (parentObject.touchingLeftWall() && velocityX < 0.0f)) {
            velocityX = -velocityX * mBounciness;

            if (Utils.close(velocityX, 0.0f)) {
                velocityX = 0.0f;
            }
        }

        parentObject.getVelocity().set(velocityX, velocityY);
        impulse.zero();
    }
}
