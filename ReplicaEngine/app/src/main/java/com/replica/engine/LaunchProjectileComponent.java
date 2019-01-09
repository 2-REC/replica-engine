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

import com.replica.engine.GameObject.ActionType;

/**
 * A component that allows an object to spawn other objects and apply velocity to them at 
 * specific intervals.  Can be used to launch projectiles, particle effects, or any other type
 * of game object.
 */
public class LaunchProjectileComponent extends GameComponent {
    protected int mObjectTypeOrdinalToSpawn;
    protected float mOffsetX;
    protected float mOffsetY;
    protected float mVelocityX;
    protected float mVelocityY;
    protected float mThetaError;
    private GameObject.ActionType mRequiredAction;
    private float mDelayBetweenShots;
    private int mProjectilesInSet;
    private float mDelayBetweenSets;
    private int mSetsPerActivation;
    private float mDelayBeforeFirstSet;
    
    private float mLastProjectileTime;
    private float mSetStartedTime;
    protected int mLaunchedCount;
    private int mSetCount;
    
    protected boolean mTrackProjectiles;
    protected int mMaxTrackedProjectiles;
    protected int mTrackedProjectileCount;
//////// ZERO_TRACK - BEGIN
//    protected boolean mActionWhenNoProjectiles;
//////// ZERO_TRACK - END
    
    protected Vector2 mWorkingVector;
    
    protected SoundSystem.Sound mShootSound;
    
    
    public LaunchProjectileComponent() {
        super();
        setPhase(ComponentPhases.POST_COLLISION.ordinal());
        mWorkingVector = new Vector2();
        reset();
    }
    
    @Override
    public void reset() {
        mRequiredAction = ActionType.INVALID;
        mObjectTypeOrdinalToSpawn = 0;
        mOffsetX = 0.0f;
        mOffsetY = 0.0f;
        mVelocityX = 0.0f;
        mVelocityY = 0.0f;
        mDelayBetweenShots = 0.0f;
        mProjectilesInSet = 0;
        mDelayBetweenSets = 0.0f;
        mLastProjectileTime = 0.0f;
        mSetStartedTime = -1.0f;
        mLaunchedCount = 0;
        mSetCount = 0;
        mSetsPerActivation = -1;
        mDelayBeforeFirstSet = 0.0f;
        mTrackProjectiles = false;
        mMaxTrackedProjectiles = 0;
        mTrackedProjectileCount = 0;
//////// ZERO_TRACK - BEGIN
//        mActionWhenNoProjectiles = false;
//////// ZERO_TRACK - END
        mThetaError = 0.0f;
        mShootSound = null;
    }
    
    @Override
    public void update(float timeDelta, BaseObject parent) {   
        GameObject parentObject = (GameObject) parent;
        
        final TimeSystem time = sSystemRegistry.timeSystem;
        final float gameTime = time.getGameTime();
        
        if (mTrackedProjectileCount < mMaxTrackedProjectiles || !mTrackProjectiles) {
//////// ZERO_TRACK - BEGIN
// !!!! TODO: should have something more elaborate, like "send an event" or "spawn something" or ... !!!!
/*
            if (mTrackProjectiles && mActionWhenNoProjectiles && mTrackedProjectileCount == 0) {
DebugLog.d( "LAUNCH", "update - no more projectiles" );
            }
*/
//////// ZERO_TRACK - END
            if (parentObject.getCurrentAction() == mRequiredAction 
                    || mRequiredAction == ActionType.INVALID) {
                
                if (mSetStartedTime == -1.0f) {
                    mLaunchedCount = 0;
                    mLastProjectileTime = 0.0f;
                    mSetStartedTime = gameTime;
                }
    
                final float setDelay = mSetCount > 0 ? mDelayBetweenSets : mDelayBeforeFirstSet;
                
                if (gameTime - mSetStartedTime >= setDelay && 
                        (mSetCount < mSetsPerActivation || mSetsPerActivation == -1)) {
                    // We can start shooting.
                    final float timeSinceLastShot = gameTime - mLastProjectileTime;
                    
                    if (timeSinceLastShot >= mDelayBetweenShots) {
                   
                        launch(parentObject);
                        mLastProjectileTime = gameTime;
                        
                        if (mLaunchedCount >= mProjectilesInSet && mProjectilesInSet > 0) {
                            mSetStartedTime = -1.0f;
                            mSetCount++;
                        }
                    }
                }
            } else {
                // Force the timer to start counting when the right action is activated.
                mSetStartedTime = -1.0f;
                mSetCount = 0;
            }
        }
    }
    
//////// PANG - BEGIN
//    private void launch(GameObject parentObject) {
//////// PANG - MID
    protected void launch(GameObject parentObject) {
//////// PANG - END
        mLaunchedCount++;
        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        GameObjectManager manager = sSystemRegistry.gameObjectManager;
        if (factory != null && manager != null) {
            float offsetX = mOffsetX;
            float offsetY = mOffsetY;
            boolean flip = false;
            if (parentObject.facingDirection.x < 0.0f) {
                offsetX = parentObject.width - mOffsetX;
                flip = true;
            }
                
            if (parentObject.facingDirection.y < 0.0f) {
                offsetY = parentObject.height - mOffsetY;
            }
            
            final float x = parentObject.getPosition().x + offsetX;
            final float y = parentObject.getPosition().y + offsetY;
            GameObject object = factory.spawnFromOrdinal(mObjectTypeOrdinalToSpawn, x, y, flip);
            if (object != null) {
	            mWorkingVector.set(1.0f, 1.0f);
	            if (mThetaError > 0.0f) {
	                final float angle = (float)(Math.random() * mThetaError * Math.PI * 2.0f);
	                mWorkingVector.x = (float)Math.sin(angle);
	                mWorkingVector.y = (float)Math.cos(angle);
	                if (Utils.close(mWorkingVector.length2(), 0.0f)) {
	                    mWorkingVector.set(1.0f, 1.0f);
	                }
	            }
	            mWorkingVector.x *= flip ? -mVelocityX : mVelocityX;
	            mWorkingVector.y *= mVelocityY;  
	            
	            object.getVelocity().set(mWorkingVector);
	            object.getTargetVelocity().set(mWorkingVector);
	            // Center the projectile on the spawn point.
	            object.getPosition().x -= object.width / 2.0f;
	            object.getPosition().y -= object.height / 2.0f;
	            
	            
	            if (mTrackProjectiles) {
	                object.commitUpdates();
	                LifetimeComponent projectileLife = object.findByClass(LifetimeComponent.class);
	                if (projectileLife != null) {
	                    projectileLife.setTrackingSpawner(this);
	                    mTrackedProjectileCount++;
//////// PANG - MID
                        specialHandle(object);
//////// PANG - END
	                }
	            }
	            manager.add(object);
	            
	            if (mShootSound != null) {
	            	SoundSystem sound = sSystemRegistry.soundSystem;
	            	if (sound != null) {
	            		sound.play(mShootSound, false, SoundSystem.PRIORITY_NORMAL);
	            	}
	            }
            }
        }
        
        
    }

    public final void setObjectTypeToSpawn(int objectTypeOrdinalToSpawn) {
        mObjectTypeOrdinalToSpawn = objectTypeOrdinalToSpawn;
    }

    public final void setOffsetX(float offsetX) {
        mOffsetX = offsetX;
    }

    public final void setOffsetY(float offsetY) {
        mOffsetY = offsetY;
    }

    public final void setVelocityX(float velocityX) {
        mVelocityX = velocityX;
    }

    public final void setVelocityY(float velocityY) {
        mVelocityY = velocityY;
    }

    public final void setRequiredAction(GameObject.ActionType requiredAction) {
        mRequiredAction = requiredAction;
    }

    public final void setDelayBetweenShots(float launchDelay) {
        mDelayBetweenShots = launchDelay;
    }
    
    public final void setDelayBetweenSets(float delayBetweenSets) {
        mDelayBetweenSets = delayBetweenSets;
    }
    
    public final void setDelayBeforeFirstSet(float delayBeforeFirstSet) {
        mDelayBeforeFirstSet = delayBeforeFirstSet;
    }

    public final void setShotsPerSet(int shotCount) {
        mProjectilesInSet = shotCount;
    }
    
    public final void setSetsPerActivation(int setCount) {
        mSetsPerActivation = setCount;
    }
    
//////// PANG - BEGIN
//    public final void enableProjectileTracking(int max) {
//////// PANG - MID
    public void enableProjectileTracking(int max) {
//////// PANG - END
        mMaxTrackedProjectiles = max;
        mTrackProjectiles = true;
    }
    
    public final void disableProjectileTracking() {
        mMaxTrackedProjectiles = 0;
        mTrackProjectiles = false;
    }
    
//////// PANG - BEGIN
//    public final void trackedProjectileDestroyed() {
//////// PANG - MID
    public void trackedProjectileDestroyed(GameObject projectile) {
//////// PANG - END
        assert mTrackProjectiles;
        if (mTrackedProjectileCount == mMaxTrackedProjectiles) {
            // Let's restart the set.
            mSetStartedTime = -1.0f;
            mSetCount = 0;
        }
        mTrackedProjectileCount--;
    }
    
//////// ZERO_TRACK - BEGIN
/*
    public void setActionWhenNoProjectiles(boolean set) {
        mActionWhenNoProjectiles = set;
    }
*/
//////// ZERO_TRACK - END

    public final void setThetaError(float error) {
        mThetaError = error;
    }
    
    public final void setShootSound(SoundSystem.Sound shoot) {
    	mShootSound = shoot;
    }

//////// PANG - MID
    protected void specialHandle(GameObject object) {
    }
//////// PANG - END
    
}
