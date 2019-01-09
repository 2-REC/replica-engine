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

import com.replica.engine.SoundSystem.Sound;
import java.util.ArrayList;

/** 
 * This component allows objects to die and be deleted when their life is reduced to zero or they
 * meet other configurable criteria.
 */
public class LifetimeComponent extends GameComponent {
    private boolean mDieWhenInvisible;
    private float mTimeUntilDeath;
// !!!! ???? TODO: change to a FixedSizeArray ? ???? !!!!
// => though don't know the size at startup ...
    private ArrayList<Integer> mSpawnOnDeathTypeOrdinals;
    private LaunchProjectileComponent mTrackingSpawner;
    private Vector2 mHotSpotTestPoint;
//////// GHOST - BEGIN
//    private boolean mReleaseGhostOnDeath;
//////// GHOST - MID
    private boolean mVulnerableToDeathTiles;
    private boolean mDieOnHitBackground;
    private Sound mDeathSound;
    private boolean mIncrementEventCounter;
    private int mEventCounter;
    private int mValue;

    public LifetimeComponent() {
        super();
        mHotSpotTestPoint = new Vector2();
        reset();
        setPhase(ComponentPhases.THINK.ordinal());
    }
    
    @Override
    public void reset() {
        mDieWhenInvisible = false;
        mTimeUntilDeath = -1;
        mSpawnOnDeathTypeOrdinals = new ArrayList<Integer>();
        mTrackingSpawner = null;
        mHotSpotTestPoint.zero();
//////// GHOST - BEGIN
//        mReleaseGhostOnDeath = true;
//////// GHOST - MID
        mVulnerableToDeathTiles = false;
        mDieOnHitBackground = false;
        mDeathSound = null;
        mIncrementEventCounter = false;
        mEventCounter = -1;
    }
    
    public void setDieWhenInvisible(boolean die) {
        mDieWhenInvisible = die;
    }
    
    public void setTimeUntilDeath(float time) {
        mTimeUntilDeath = time;
    }
    
    public void addObjectToSpawnOnDeath(int typeOrdinal) {
        mSpawnOnDeathTypeOrdinals.add(typeOrdinal);
    }
    
    public void setIncrementEventCounter(int event, int value) {
        mIncrementEventCounter = true;
        mEventCounter = event;
        mValue = value;
    }
    
    public void setDecrementEventCounter(int event, int value) {
    	setIncrementEventCounter(event, -value);
    }
    
    @Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject)parent;
        if (mTimeUntilDeath > 0) {
            mTimeUntilDeath -= timeDelta;
            if (mTimeUntilDeath <= 0) {
                die(parentObject);
                return;
            }
        }
        
        if (mDieWhenInvisible) {
            CameraSystem camera = sSystemRegistry.cameraSystem;
            ContextParameters context = sSystemRegistry.contextParameters;
            final float dx = 
                Math.abs(parentObject.getPosition().x - camera.getFocusPositionX());
            final float dy = 
                Math.abs(parentObject.getPosition().y - camera.getFocusPositionY());
            if (dx > context.gameWidth || dy > context.gameHeight) {
                // the position of this object is off the screen, destroy!
                // TODO: this is a pretty dumb test.  We should have a bounding volume instead.
                die(parentObject);
                return;
            }
        }
        
        if (parentObject.life > 0 && mVulnerableToDeathTiles) {
            HotSpotSystem hotSpot = sSystemRegistry.hotSpotSystem;
            if (hotSpot != null) {
                // TODO: HACK!  Unify all this code.
// !!!! TODO : could have a "isDead" function in HotSpotSystem to do this !!!!
                if (hotSpot.getHotSpot(parentObject.getCenteredPositionX(),
                        parentObject.getPosition().y + 10.0f) == HotSpotSystem.HotSpotType.DIE) {
                    parentObject.life = 0;
                }
            }
        }
        
        if (parentObject.life > 0 && mDieOnHitBackground) {
        	if (parentObject.getBackgroundCollisionNormal().length2() > 0.0f) {
        		parentObject.life = 0;
        	}
        }
        
        if (parentObject.life <= 0) {
            die(parentObject);
            return;
        }
    }
    
    private void die(GameObject parentObject) {
        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        GameObjectManager manager = sSystemRegistry.gameObjectManager;
        
//////// GHOST - BEGIN
/*
        if (mReleaseGhostOnDeath) {
            // TODO: This is sort of a hack.  Find a better way to do this without introducing a
            // dependency between these two.  Generic on-death event or something.
            GhostComponent ghost = parentObject.findByClass(GhostComponent.class);
            if (ghost != null) {
                ghost.releaseControl(parentObject);
            }
        }
*/
//////// GHOST - MID
        
        if (mIncrementEventCounter) {
        	EventRecorder recorder = sSystemRegistry.eventRecorder;
        	recorder.incrementEventCounter(mEventCounter, mValue);
        }

        if (manager != null) {
            for (int i = 0; i < mSpawnOnDeathTypeOrdinals.size(); ++i) {
                GameObject object = factory.spawnFromOrdinal(mSpawnOnDeathTypeOrdinals.get(i),
                        parentObject.getPosition().x, parentObject.getPosition().y, parentObject.facingDirection.x < 0.0f);

                if (object != null) {
                    manager.add(object);
                }
            }
            
        }
//////// multi spawn - MID
        
        if (mTrackingSpawner != null) {
//////// PANG - BEGIN
//            mTrackingSpawner.trackedProjectileDestroyed();
//////// PANG - MID
            mTrackingSpawner.trackedProjectileDestroyed(parentObject);
//////// PANG - END
        }
        
        
        if (manager != null) {
            manager.destroy(parentObject);
        }
        
        if (mDeathSound != null) {
        	SoundSystem sound = sSystemRegistry.soundSystem;
        	if (sound != null) {
        		sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL);
        	}
        }

    }
    
    public final void setTrackingSpawner(LaunchProjectileComponent spawner) {
        mTrackingSpawner = spawner;
    }
    
//////// GHOST - BEGIN
/*
    public final void setReleaseGhostOnDeath(boolean release) {
        mReleaseGhostOnDeath = release;
    }
*/
//////// GHOST - MID
    
    public final void setVulnerableToDeathTiles(boolean vulnerable) {
        mVulnerableToDeathTiles = vulnerable;
    }
    
    public final void setDieOnHitBackground(boolean die) {
    	mDieOnHitBackground = die;
    }
    
    public final void setDeathSound(Sound deathSound) {
    	mDeathSound = deathSound;
    }
}
