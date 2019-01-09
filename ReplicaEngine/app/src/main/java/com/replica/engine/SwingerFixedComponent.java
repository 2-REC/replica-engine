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
 * Implements rope swinging constraint movement from a fixed point.
 */
public class SwingerFixedComponent extends GameComponent {

    private boolean mAttached;
    private Vector2 mAttachedPoint;
    private float mMaxDistance;
    private float mMaxDistance2;

    private float mPositionOffsetX;
    private boolean mPositionOffsetXAbsolute;
    private float mPositionOffsetY;
    private boolean mPositionOffsetYAbsolute;

    private boolean mPerpetual;
    private float mMaxSpeed;

    private Vector2 mRopeVector;
    private Vector2 mVelocityVector;
    private Vector2 mNormalVector;


    public SwingerFixedComponent() {
        super();

        mAttached = false;
        mAttachedPoint = new Vector2();
        mRopeVector = new Vector2();
        mVelocityVector = new Vector2();
        mNormalVector = new Vector2();
        mPerpetual = false;

        setPhase(ComponentPhases.PHYSICS.ordinal());
    }

    @Override
    public void reset() {
        mAttached = false;
        mAttachedPoint.zero();

        mMaxDistance = 0.0f;
        mMaxDistance2 = 0.0f;
        mPerpetual = false;
        mMaxSpeed = 0.0f;

        mPositionOffsetX = 0.0f;
        mPositionOffsetXAbsolute = true;
        mPositionOffsetY = 0.0f;
        mPositionOffsetYAbsolute = true;

        mVelocityVector.zero();
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        if (mAttached) {
            final GameObject parentObject = (GameObject) parent;
            final Vector2 initialVelocity = parentObject.getVelocity();

            final Vector2 attachedPoint = mAttachedPoint;

//// OFFSET - BEGIN
//            mRopeVector.set(parentObject.getCenteredPositionX(), parentObject.getCenteredPositionY());
//// OFFSET - MID
            final Vector2 parentDirection = parentObject.facingDirection;
            final float offsetX = mPositionOffsetXAbsolute ? mPositionOffsetX : mPositionOffsetX * parentDirection.x;
            final float offsetY = mPositionOffsetYAbsolute ? mPositionOffsetY : mPositionOffsetY * parentDirection.y;
            mRopeVector.set(parentObject.getCenteredPositionX() + offsetX,
                    parentObject.getCenteredPositionY() + offsetY);
//// OFFSET - END
            mRopeVector.subtract(attachedPoint);
            final Vector2 ropeVector = mRopeVector;

            if (ropeVector.length2() > mMaxDistance2) {
                // get normal to rope (tangent to circle, same side as movement)
                getNormal(ropeVector, initialVelocity, mNormalVector);
                final float normalLength = mNormalVector.normalize();

                // remove lost speed from angle with rope
                mVelocityVector.set(initialVelocity);
                final float tangentialFactor = tangentialFactor(ropeVector, initialVelocity);
                mVelocityVector.multiply(tangentialFactor);

                // set new movement direction
                final float speed = mVelocityVector.length();
                mVelocityVector.set(mNormalVector);
                mVelocityVector.multiply(speed);

                if (mPerpetual) {
// !!!! TODO: should check if perpendicular to gravity instead of horizontal !!!!
                    // if velocity vector is horizontal ( mNormalVector.y == 0 )
                    //  => set velocity back to maximum value
                    if ((normalLength != 0.0f) &&
                            Utils.close(mNormalVector.y, 0.0f, 0.1f)) {
                        mVelocityVector.x = mMaxSpeed * Utils.sign(mNormalVector.x);
                        mVelocityVector.y = 0.0f;
                    }
                }

                ropeVector.normalize();
                ropeVector.multiply(mMaxDistance);

                // set new position & velocity
//// OFFSET - BEGIN
//                parentObject.getPosition().set(attachedPoint);
//                parentObject.getPosition().add(ropeVector);
//// OFFSET - MID
// !!!! TODO: check ok with offsets !!!!
                parentObject.getPosition().x = (attachedPoint.x + ropeVector.x) - offsetX - (parentObject.width / 2.0f);
                parentObject.getPosition().y = (attachedPoint.y + ropeVector.y) - offsetY - (parentObject.height / 2.0f);
//// OFFSET - MID

// !!!! TODO: should stop movement when too small !!!!
/*
velocity = (0,0);
x = attachedPoint.x ...
y = attachedPoint.y - mMaxDistance ...
*/

                parentObject.getVelocity().set(mVelocityVector);
            }
        }
    }

    public final void setAttachPoint(final float x, final float y, float distance) {
        mAttached = true;
        mAttachedPoint.set(x, y);
        mMaxDistance = distance;
        mMaxDistance2 = distance * distance;
    }
    public final void Detach() {
        mAttached = false;
    }

    public final void setPositionOffsetX(final float offsetX, final boolean absolute) {
    	mPositionOffsetX = offsetX;
    	mPositionOffsetXAbsolute = absolute;
    }

    public final void setPositionOffsetY(final float offsetY, final boolean absolute) {
    	mPositionOffsetY = offsetY;
    	mPositionOffsetYAbsolute = absolute;
    }

// !!!! TODO: give "max angle" instead of "max speed" !!!!
// (& determine speed from m*g*h)
    public final void setPerpetual(boolean bPerpetual, float speed) {
        mPerpetual = bPerpetual;
        mMaxSpeed = speed;
    }


    private final static void getNormal(Vector2 line, Vector2 direction, Vector2 normal) {
        // normals: (-line.y, line.x) and (line.y, -line.x)
        normal.x = -line.y;
        normal.y = line.x;

        // choose normal on same side as "direction"
        if (direction.dot(normal) <= 0) {
            normal.x = line.y;
            normal.y = -line.x;
        }
    }

    private final static float tangentialFactor(Vector2 rope, Vector2 movement) {
        /*
         f: angle of contact (rope angle): between "rope" & "vertical(0, -1)"
         a: angle of approach (movement angle): between "movement" & "vertical(0, -1)"
          => idem as: angle between "rope" & "movement"

         (f - a) = acos(dotProduct(rope, movement) / (rope.Length() * movement.Length()))
          (should be always between 0-PI => sin >= 0)
        */

//        final float invertAngle = rope.dot(movement) / (rope.length() * movement.length());
        final float denum = rope.length() * movement.length();
        if (denum == 0.0f) {
            return 0.0f;
        }

        final float invertAngle = rope.dot(movement) / (rope.length() * movement.length());
// !!!! ???? TODO: can optimise that ? ???? !!!!
        float diffAngle = (float)Math.acos(invertAngle);
        diffAngle = (float)Math.sin(diffAngle);
//OR:
//        float diffAngle = Math.sqrt(1 - (invertAngle * invertAngle));

        return diffAngle;
    }

}
