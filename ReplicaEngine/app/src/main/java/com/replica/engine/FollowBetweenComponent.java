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
 * Manages the position of an object based on 2 target game objects.
 * A ratio specifies the position between the 2 objects.
*/
public class FollowBetweenComponent extends GameComponent {
    private Vector2 mCurrentPosition;
    private Vector2 mTargetsDistance;
    private float mDistanceRatio;

    private GameObject mTarget1;
    private float mTarget1OffsetX;
    private boolean mTarget1OffsetXAbsolute;
    private float mTarget1OffsetY;
    private boolean mTarget1OffsetYAbsolute;

    private GameObject mTarget2;
    private float mTarget2OffsetX;
    private boolean mTarget2OffsetXAbsolute;
    private float mTarget2OffsetY;
    private boolean mTarget2OffsetYAbsolute;


    public FollowBetweenComponent() {
        super();

        mCurrentPosition = new Vector2();
        mTargetsDistance = new Vector2();

        setPhase(ComponentPhases.PHYSICS.ordinal());
    }

    @Override
    public void reset() {
        mTarget1 = null;
        mTarget1OffsetX = 0.0f;
        mTarget1OffsetXAbsolute = true;
        mTarget1OffsetY = 0.0f;
        mTarget1OffsetYAbsolute = true;

        mTarget2 = null;
        mTarget2OffsetX = 0.0f;
        mTarget2OffsetXAbsolute = true;
        mTarget2OffsetY = 0.0f;
        mTarget2OffsetYAbsolute = true;

        // useless...
        //mCurrentPosition.zero();
        mTargetsDistance.zero();
        mDistanceRatio = 0.0f;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        if (mTarget1 != null && mTarget2 != null) {
            final float offsetTarget1X = (mTarget1OffsetXAbsolute) ?
                    mTarget1OffsetX : (mTarget1OffsetX * mTarget1.facingDirection.x);
            final float offsetTarget1Y = (mTarget1OffsetYAbsolute) ?
                    mTarget1OffsetY : (mTarget1OffsetY * mTarget1.facingDirection.y);

            final float offsetTarget2X = (mTarget2OffsetXAbsolute) ?
                    mTarget2OffsetX : (mTarget2OffsetX * mTarget2.facingDirection.x);
            final float offsetTarget2Y = (mTarget2OffsetYAbsolute) ?
                    mTarget2OffsetY : (mTarget2OffsetY * mTarget2.facingDirection.y);

            final float startX = mTarget1.getCenteredPositionX() + offsetTarget1X;
            final float startY = mTarget1.getCenteredPositionY() + offsetTarget1Y;

            final float endX = mTarget2.getCenteredPositionX() + offsetTarget2X;
            final float endY = mTarget2.getCenteredPositionY() + offsetTarget2Y;

            final float posX = (startX - endX) * mDistanceRatio;
            final float posY = (startY - endY) * mDistanceRatio;

            final GameObject parentObject = (GameObject)parent;
            parentObject.getPosition().x = startX - posX - (parentObject.width / 2.0f);
            parentObject.getPosition().y = startY - posY - (parentObject.height / 2.0f);
        }
    }

    public final void setTargets(final GameObject target1, final GameObject target2,
            final float ratio) {
        mTarget1 = target1;
        mTarget2 = target2;
        mDistanceRatio = ratio;
    }

    public final void setTarget1OffsetX(float offsetX, boolean absolute) {
    	mTarget1OffsetX = offsetX;
        mTarget1OffsetXAbsolute = absolute;
    }

    public final void setTarget1OffsetY(float offsetY, boolean absolute) {
    	mTarget1OffsetY = offsetY;
        mTarget1OffsetYAbsolute = absolute;
    }

    public final void setTarget2OffsetX(float offsetX, boolean absolute) {
    	mTarget2OffsetX = offsetX;
        mTarget2OffsetXAbsolute = absolute;
    }

    public final void setTarget2OffsetY(float offsetY, boolean absolute) {
    	mTarget2OffsetY = offsetY;
        mTarget2OffsetYAbsolute = absolute;
    }

}
