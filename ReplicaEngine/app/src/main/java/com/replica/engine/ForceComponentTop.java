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
 * A game component that implements a constant force.
 * Adding this component to a game object will cause it to constantly move in the specified direction.
 *
 * !!!! CAUTION: this component shouldn't be used with "PhysicsComponentTop" !!!!
 */
public class ForceComponentTop extends ForceComponent {
    private Vector2 mForce;


    public ForceComponentTop() {
        super();
        mForce = new Vector2();
        reset();
//        setPhase(ComponentPhases.POST_PHYSICS.ordinal());
    }

    @Override
    public void reset() {
        super.reset();
        mForce.zero();
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject)parent;

        // we look to user data so that other code can provide impulses
        Vector2 impulseVector = parentObject.getImpulse();

        final Vector2 currentVelocity = parentObject.getVelocity();
        final Vector2 surfaceNormal = parentObject.getBackgroundCollisionNormal();
        if (surfaceNormal.length2() > 0.0f) {
            resolveCollision(currentVelocity, impulseVector, surfaceNormal, impulseVector);
        }

        VectorPool vectorPool = sSystemRegistry.vectorPool;

        Vector2 newVelocity = vectorPool.allocate(currentVelocity);
        newVelocity.add(impulseVector);

// !!!! TODO: change when handling jumping objects !!!!
// ( or enough only to use a Gravity component ? )
        final boolean touchingFloor = true;

        GravityComponentTop gravity = parentObject.findByClass(GravityComponentTop.class);

        if (touchingFloor && gravity != null) {
            final float gravityForce = gravity.getGravity();

            if (Math.abs(newVelocity.x) != mForce.x) {
                // if we were moving last frame, we'll use dynamic friction. Else static.
                float frictionCoefficient = Math.abs(currentVelocity.x) > 0.0f ?
                        getDynamicFrictionCoeffecient() : getStaticFrictionCoeffecient();
                frictionCoefficient *= timeDelta;

                // Friction = cofN, where cof = friction coefficient
                //  and N = force perpendicular to the ground.
                final float maxFriction = Math.abs(gravityForce) * getMass() * frictionCoefficient;

                if (maxFriction > Math.abs(newVelocity.x - mForce.x)) {
                    newVelocity.x = mForce.x;
                } else {
                    newVelocity.x = newVelocity.x - (maxFriction * Utils.sign(newVelocity.x - mForce.x));
                }
            }

            if (Math.abs(newVelocity.y) != mForce.y) {
                // if we were moving last frame, we'll use dynamic friction. Else static.
                float frictionCoefficient = Math.abs(currentVelocity.y) > 0.0f ?
                        getDynamicFrictionCoeffecient() : getStaticFrictionCoeffecient();
                frictionCoefficient *= timeDelta;

                // Friction = cofN, where cof = friction coefficient
                //  and N = force perpendicular to the ground.
                final float maxFriction = Math.abs(gravityForce) * getMass() * frictionCoefficient;

                if (maxFriction > Math.abs(newVelocity.y - mForce.y)) {
                    newVelocity.y = mForce.y;
                } else {
                    newVelocity.y = newVelocity.y - (maxFriction * Utils.sign(newVelocity.y - mForce.y));
                }
            }
        }

        if (Math.abs(newVelocity.x - mForce.x) < 0.01f) {
            newVelocity.x = mForce.x;
        }

        if (Math.abs(newVelocity.y - mForce.y) < 0.01f) {
            newVelocity.y = mForce.y;
        }

        // physics-based movements means constant acceleration, always.
        // set the target to the velocity.
        parentObject.setVelocity(newVelocity);
        parentObject.setTargetVelocity(newVelocity);
        parentObject.setAcceleration(Vector2.ZERO);
        parentObject.setImpulse(Vector2.ZERO);

        vectorPool.release(newVelocity);
    }


/*
    public Vector2 getForce() {
        return mForce;
    }
*/

    public void setForce(float forceX, float forceY) {
        mForce.x = forceX;
        mForce.y = forceY;
    }

}
