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

// !!!! TODO: add sound when land on platform !!!!

/**
 * Handles platform collisions.
 * Objects colliding (with HitType.PLATFORM) follow the platform if moving.
 * Also handles jump-through platforms.
 */
public class PlatformComponent extends GameComponent {

    private static final int MAX_OBJECTS = 5; // maximum number of objects simultaneously on platform
// !!!! ???? TODO: what value ? ???? !!!!
    private static final float MIN_SPEED = 10.0f; // speed considered close to 0
    private final Vector2 VERTICAL = new Vector2(0.0f, 1.0f);

    private FixedSizeArray<GameObject> mNewAboveObjects;
    private FixedSizeArray<GameObject> mOldAboveObjects;
    private FixedSizeArray<GameObject> mNewBelowObjects;
    private FixedSizeArray<GameObject> mOldBelowObjects;
    private FixedSizeArray<GameObject> mNewAttachedObjects;
    private FixedSizeArray<GameObject> mOldAttachedObjects;

    private float mPlatformLevel;
    private float mPreviousHorizontalPosition;

    public PlatformComponent() {
        super();
// !!!! ???? TODO: what Phase to use ? ???? !!!!
// (PRE_DRAW? COLLISION_DETECTION? PHYSICS? other?)
//?        setPhase(ComponentPhases.ANIMATION.ordinal());
        setPhase(ComponentPhases.COLLISION_DETECTION.ordinal());

        setMaxObjects(MAX_OBJECTS);

        reset();
    }

    @Override
    public void reset() {
        mNewAboveObjects.clear();
        mOldAboveObjects.clear();
        mNewBelowObjects.clear();
        mOldBelowObjects.clear();
        mNewAttachedObjects.clear();
        mOldAttachedObjects.clear();

        mPlatformLevel = 0.0f;
        mPreviousHorizontalPosition = 0.0f;
    }

    public void setMaxObjects(int maxObjectsCount) {
        if (mNewAboveObjects == null ||
                ((mNewAboveObjects != null) && (mNewAboveObjects.getCount() != maxObjectsCount))) {
            mNewAboveObjects = new FixedSizeArray<GameObject>(maxObjectsCount);
            mOldAboveObjects = new FixedSizeArray<GameObject>(maxObjectsCount);
            mNewBelowObjects = new FixedSizeArray<GameObject>(maxObjectsCount);
            mOldBelowObjects = new FixedSizeArray<GameObject>(maxObjectsCount);
            mNewAttachedObjects = new FixedSizeArray<GameObject>(maxObjectsCount);
            mOldAttachedObjects = new FixedSizeArray<GameObject>(maxObjectsCount);
        }

        mNewAboveObjects.clear();
        mOldAboveObjects.clear();
        mNewBelowObjects.clear();
        mOldBelowObjects.clear();
        mNewAttachedObjects.clear();
        mOldAttachedObjects.clear();
    }

    public void init(float x, float top) {
    	mPlatformLevel = top;
        mPreviousHorizontalPosition = x;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        // update lists
        mOldAboveObjects.clear();
        while (mNewAboveObjects.getCount() > 0) {
            mOldAboveObjects.add(mNewAboveObjects.removeLast());
        }
        mOldBelowObjects.clear();
        while (mNewBelowObjects.getCount() > 0) {
            mOldBelowObjects.add(mNewBelowObjects.removeLast());
        }
/*
DebugLog.e("PLATFORM", "update - old above nb: " + mOldAboveObjects.getCount());
DebugLog.e("PLATFORM", "update - old below nb: " + mOldBelowObjects.getCount());
DebugLog.e("PLATFORM", "update - att attach nb: " + mNewAttachedObjects.getCount());
*/

        // update positions & velocities
        final GameObject platform = (GameObject) parent;
        final Vector2 platPos = platform.getPosition();
        final float offsetX = platPos.x - mPreviousHorizontalPosition;
        mPreviousHorizontalPosition = platPos.x;

        final int nbAttached = mNewAttachedObjects.getCount();
        for (int i = 0; i < nbAttached; ++i) {
            final GameObject object = mNewAttachedObjects.get(i);
//DebugLog.e("PLATFORM", "UPDATE - object.getTargetVelocity().y: " + object.getTargetVelocity().y);
            if (object.getTargetVelocity().y <= MIN_SPEED) {
                object.getVelocity().y = 0.0f;
                object.getTargetVelocity().y = 0.0f;
                object.getAcceleration().y = 0.0f;
                object.getImpulse().y = 0.0f;
                object.getPosition().y = platPos.y + mPlatformLevel;

                final TimeSystem timeSystem = sSystemRegistry.timeSystem;
                if (timeSystem != null) {
                    float time = timeSystem.getGameTime();
                    object.setLastTouchedFloorTime(time);
                }
                object.setBackgroundCollisionNormal(VERTICAL);

                if (mOldAttachedObjects.find(object , false) != -1) {
                    object.getPosition().x += offsetX;
                }
            }
        }

        // update previously attached objects
        mOldAttachedObjects.clear();
        while (mNewAttachedObjects.getCount() > 0) {
            mOldAttachedObjects.add(mNewAttachedObjects.removeLast());
        }
    }

    public void add(GameObject parent, GameObject object) {
        final float objPosY = object.getPosition().y;
// !!!! ???? TODO: need to add an offset ? ???? !!!!
        final float platTop = parent.getPosition().y + mPlatformLevel;
//DebugLog.e("PLATFORM", "add - objPosY: " + objPosY);
//DebugLog.e("PLATFORM", "add - platTop: " + platTop);

//DebugLog.e("PLATFORM", "add - object.getVelocity().y: " + object.getVelocity().y);

        boolean isLevel = (objPosY == platTop);
        boolean isAbove = !isLevel && (objPosY > platTop);
        boolean isBelow = !isLevel && (objPosY < platTop);
/*
if (isLevel) {
    DebugLog.e("PLATFORM", "add - isLevel");
}
if (isAbove) {
    DebugLog.e("PLATFORM", "add - isAbove");
}
if (isBelow) {
    DebugLog.e("PLATFORM", "add - isBelow");
}
*/

        final boolean wasLevel = (mOldAttachedObjects.find(object, false) != -1);
        final boolean wasAbove = (mOldAboveObjects.find(object, false) != -1);
        final boolean wasBelow = (mOldBelowObjects.find(object, false) != -1);
/*
if (wasLevel) {
    DebugLog.e("PLATFORM", "add - wasLevel");
}
if (wasAbove) {
    DebugLog.e("PLATFORM", "add - wasAbove");
}
if (wasBelow) {
    DebugLog.e("PLATFORM", "add - wasBelow");
}
*/

        if (wasLevel) {
            if (isAbove) { // (objPosY > platTop)
                if (object.getVelocity().y <= MIN_SPEED) {
                    isLevel = true;
                    isAbove = false;
                    //isBelow = false;
                }
            } else { // (objPosY <= platTop)
                isLevel = true;
                //isAbove = false;
                isBelow = false;
            }
        } else if (wasAbove) {
            if (!isAbove) { // (objPosY <= platTop)
                isLevel = true;
                //isAbove = false;
                isBelow = false;
            }
        } else if (wasBelow) {
            if (!isBelow) { // (objPosY >= platTop)
                if (object.getVelocity().y <= MIN_SPEED) {
                    isLevel = true;
                    isAbove = false;
                    //isBelow = false;
                } else {
                    isLevel = false;
                    isAbove = true;
                    //isBelow = false;
                }
            }
        }

        //assert(isAbove != isBelow);

/*
        if (isLevel) {
            DebugLog.e("PLATFORM", "add - isLevel");
        }
        if (isAbove) {
            DebugLog.e("PLATFORM", "add - isAbove");
        }
        if (isBelow) {
            DebugLog.e("PLATFORM", "add - isBelow");
        }
*/

        if (isLevel) {
        	mNewAttachedObjects.add(object);
        } else if (isAbove) {
            mNewAboveObjects.add(object);
        } else {
            mNewBelowObjects.add(object);
        }

    }

}
