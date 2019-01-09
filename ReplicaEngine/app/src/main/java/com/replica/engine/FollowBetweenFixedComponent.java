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
 * Manages the position of an object based on 1 target game object & a fixed point.
 * A ratio specifies the position between the 2 objects.
 */
public class FollowBetweenFixedComponent extends GameComponent {
    private Vector2 mCurrentPosition;
    private Vector2 mTargetsDistance;
    private float mDistanceRatio;

    private GameObject mObject;
    private float mObjectOffsetX;
    private boolean mObjectOffsetXAbsolute;
    private float mObjectOffsetY;
    private boolean mObjectOffsetYAbsolute;

    private Vector2 mPoint;


    public FollowBetweenFixedComponent() {
        super();

        mCurrentPosition = new Vector2();
        mTargetsDistance = new Vector2();

        mPoint = new Vector2();

        setPhase(ComponentPhases.PHYSICS.ordinal());
    }

    @Override
    public void reset() {

        mObject = null;
        mObjectOffsetX = 0.0f;
        mObjectOffsetXAbsolute = true;
        mObjectOffsetY = 0.0f;
        mObjectOffsetYAbsolute = true;


        // useless...
        //mCurrentPosition.zero();
        //mPoint.zero();
        mTargetsDistance.zero();
        mDistanceRatio = 0.0f;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        if (mObject != null) {
            final float offsetObjectX = (mObjectOffsetXAbsolute) ?
                    mObjectOffsetX : (mObjectOffsetX * mObject.facingDirection.x);
            final float offsetObjectY = (mObjectOffsetYAbsolute) ?
                    mObjectOffsetY : (mObjectOffsetY * mObject.facingDirection.y);

            final float startX = mPoint.x;
            final float startY = mPoint.y;

            final float endX = mObject.getCenteredPositionX() + offsetObjectX;
            final float endY = mObject.getCenteredPositionY() + offsetObjectY;

            final float posX = (startX - endX) * mDistanceRatio;
            final float posY = (startY - endY) * mDistanceRatio;

            final GameObject parentObject = (GameObject)parent;
            parentObject.getPosition().x = startX - posX - (parentObject.width / 2.0f);
            parentObject.getPosition().y = startY - posY - (parentObject.height / 2.0f);
        }
    }

    public final void setTargets(final float fixedX, final float fixedY, final GameObject target,
            final float ratio) {
        mPoint.set(fixedX, fixedY);
        mObject = target;
        mDistanceRatio = ratio;
    }

    public final void setObjectOffsetX(float offsetX, boolean absolute) {
    	mObjectOffsetX = offsetX;
        mObjectOffsetXAbsolute = absolute;
    }

    public final void setObjectOffsetY(float offsetY, boolean absolute) {
    	mObjectOffsetY = offsetY;
        mObjectOffsetYAbsolute = absolute;
    }

}
