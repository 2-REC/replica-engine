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
 */
public abstract class ForceComponent extends GameComponent {
    private static final float DEFAULT_MASS = 1.0f;
    private static final float DEFAULT_BOUNCINESS = 0.1f;
    private static final float DEFAULT_STATIC_FRICTION_COEFFICIENT = 0.05f;
    private static final float DEFAULT_DYNAMIC_FRICTION_COEFFICIENT = 0.02f;

    private float mMass;
    private float mBounciness; // 1.0 = super bouncy, 0.0 = zero bounce
    private float mStaticFrictionCoefficient;
    private float mDynamicFrictionCoefficient;


    public ForceComponent() {
        super();
//        reset();
        setPhase(ComponentPhases.POST_PHYSICS.ordinal());
    }

    @Override
    public void reset() {
        mMass = DEFAULT_MASS;
        mBounciness = DEFAULT_BOUNCINESS;
        mStaticFrictionCoefficient = DEFAULT_STATIC_FRICTION_COEFFICIENT;
        mDynamicFrictionCoefficient = DEFAULT_DYNAMIC_FRICTION_COEFFICIENT;
    }


    protected void resolveCollision(Vector2 velocity, Vector2 impulse,
            Vector2 opposingNormal, Vector2 outputImpulse) {
        VectorPool vectorPool = sSystemRegistry.vectorPool;

        outputImpulse.set(impulse);

        Vector2 collisionNormal = vectorPool.allocate(opposingNormal);

        collisionNormal.normalize();

        Vector2 relativeVelocity = vectorPool.allocate(velocity);
        relativeVelocity.add(impulse);

        final float dotRelativeAndNormal = relativeVelocity.dot(collisionNormal);

        // make sure the motion of the entity requires resolution
        if (dotRelativeAndNormal < 0.0f) {
            final float coefficientOfRestitution = getBounciness(); // 0 = perfectly inelastic,
                                                                    // 1 = perfectly elastic

            // calculate an impulse to apply to the entity
            float j = (-(1 + coefficientOfRestitution) * dotRelativeAndNormal);

            j /= ((collisionNormal.dot(collisionNormal)) * (1 / getMass()));

            Vector2 entity1Adjust = vectorPool.allocate(collisionNormal);

            entity1Adjust.set(collisionNormal);
            entity1Adjust.multiply(j);
            entity1Adjust.divide(getMass());
            entity1Adjust.add(impulse);
            outputImpulse.set(entity1Adjust);
            vectorPool.release(entity1Adjust);
        }
        vectorPool.release(collisionNormal);
        vectorPool.release(relativeVelocity);
    }

    protected void resolveCollision(Vector2 velocity, Vector2 impulse,
            Vector2 opposingNormal, float otherMass,
            Vector2 otherVelocity, Vector2 otherImpulse,
            float otherBounciness, Vector2 outputImpulse) {
        VectorPool vectorPool = sSystemRegistry.vectorPool;

        Vector2 collisionNormal = vectorPool.allocate(opposingNormal);
        collisionNormal.normalize();

        Vector2 entity1Velocity = vectorPool.allocate(velocity);
        entity1Velocity.add(impulse);

        Vector2 entity2Velocity = vectorPool.allocate(otherVelocity);
        entity2Velocity.add(otherImpulse);

        Vector2 relativeVelocity = vectorPool.allocate(entity1Velocity);
        relativeVelocity.subtract(entity2Velocity);

        final float dotRelativeAndNormal = relativeVelocity.dot(collisionNormal);

        // make sure the entities' motion requires resolution
        if (dotRelativeAndNormal < 0.0f) {
            final float bounciness = Math.min(getBounciness() + otherBounciness, 1.0f);
            final float coefficientOfRestitution = bounciness;  // 0 = perfectly inelastic,
                                                                // 1 = perfectly elastic

            // calculate an impulse to apply to both entities
            float j = (-(1 + coefficientOfRestitution) * dotRelativeAndNormal);

            j /= ((collisionNormal.dot(collisionNormal)) * (1 / getMass() + 1 / otherMass));

            Vector2 entity1Adjust = vectorPool.allocate(collisionNormal);
            entity1Adjust.multiply(j);
            entity1Adjust.divide(getMass());
            entity1Adjust.add(impulse);

            outputImpulse.set(entity1Adjust);

            // TODO: Deal impulses both ways.
            /*
             * Vector3 entity2Adjust = (collisionNormal j);
             * entity2Adjust[0] /= otherMass;
             * entity2Adjust[1] /= otherMass;
             * entity2Adjust[2] /= otherMass;
             *
             * const Vector3 newEntity2Impulse = otherImpulse + entity2Adjust;
             */
            vectorPool.release(entity1Adjust);
        }
        vectorPool.release(collisionNormal);
        vectorPool.release(entity1Velocity);
        vectorPool.release(entity2Velocity);
        vectorPool.release(relativeVelocity);
    }

    public float getMass() {
        return mMass;
    }

    public void setMass(float mass) {
        mMass = mass;
    }

    public float getBounciness() {
        return mBounciness;
    }

    public void setBounciness(float bounciness) {
        mBounciness = bounciness;
    }

    public float getStaticFrictionCoeffecient() {
        return mStaticFrictionCoefficient;
    }

    public void setStaticFrictionCoeffecient(float staticFrictionCoefficient) {
        mStaticFrictionCoefficient = staticFrictionCoefficient;
    }

    public float getDynamicFrictionCoeffecient() {
        return mDynamicFrictionCoefficient;
    }

    public void setDynamicFrictionCoeffecient(float dynamicFrictionCoefficient) {
        mDynamicFrictionCoefficient = dynamicFrictionCoefficient;
    }

}
