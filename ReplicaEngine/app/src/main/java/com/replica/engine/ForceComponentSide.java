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
 * A game component that implements a constant horizontal force.
 * Adding this component to a game object will cause it to constantly move at the specified horizontal speed.
 *
 * !!!! CAUTION: this component shouldn't be used with "PhysicsComponent" !!!!
 */
public class ForceComponentSide extends ForceComponent {
    private float mForce;


    public ForceComponentSide() {
        super();
        reset();
//        setPhase(ComponentPhases.POST_PHYSICS.ordinal());
    }

    @Override
    public void reset() {
        super.reset();
        mForce = 0.0f;
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

        final boolean touchingFloor = parentObject.touchingGround();

        GravityComponent gravity = parentObject.findByClass(GravityComponent.class);

        if (touchingFloor && (currentVelocity.y <= 0.0f) &&
//                (Math.abs(newVelocity.x) > 0.0f) && (gravity != null)) &&
                (gravity != null)) {
            final Vector2 gravityVector = gravity.getGravity();

// !!!! ???? TODO: should be dynamic all the time ? ???? !!!!
            // if we were moving last frame, we'll use dynamic friction. Else static.
            float frictionCoeffecient = Math.abs(currentVelocity.x) > 0.0f ?
                    getDynamicFrictionCoeffecient() : getStaticFrictionCoeffecient();
            frictionCoeffecient *= timeDelta;

            // Friction = cofN, where cof = friction coefficient
            //  and N = force perpendicular to the ground.
            final float maxFriction = Math.abs(gravityVector.y) * getMass() * frictionCoeffecient;

            if (maxFriction > Math.abs(newVelocity.x - mForce)) {
                newVelocity.x = mForce;
            } else {
                newVelocity.x = newVelocity.x - (maxFriction * Utils.sign(newVelocity.x - mForce));
            }
        }

        if (Math.abs(newVelocity.x - mForce) < 0.01f) {
            newVelocity.x = mForce;
        }

        if (Math.abs(newVelocity.y) < 0.01f) {
            newVelocity.y = 0.0f;
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
    public float getForce() {
        return mForce;
    }
*/

    public void setForce(float force) {
        mForce = force;
    }

}
