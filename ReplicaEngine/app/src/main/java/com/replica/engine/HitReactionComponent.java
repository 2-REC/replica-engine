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

import com.replica.engine.CollisionParameters.HitType;
import com.replica.engine.GameObject.ActionType;
import com.replica.engine.GameObject.Team;

/** 
 * A general-purpose component that responds to dynamic collision notifications.  This component
 * may be configured to produce common responses to hit (taking damage, being knocked back, etc), or
 * it can be derived for entirely different responses.  This component must exist on an object for
 * that object to respond to dynamic collisions.
 */
public abstract class HitReactionComponent extends GameComponent {
// !!!! TODO: could have different magnitudes for "take hit" & "deal hit" !!!!
    private static final float ATTACK_PAUSE_DELAY = (1.0f / 60) * 4;
    private static final float DEFAULT_BOUNCE_MAGNITUDE = 200.0f;
// !!!! TODO : should add a method to specify this value !!!!
    protected static final float EVENT_SEND_DELAY = 5.0f;

    private boolean mPauseOnAttack;
    private float mPauseOnAttackTime;
    private boolean mBounceOnTakeHit;
    private float mBounceOnTakeMagnitude;
    private float mInvincibleAfterHitTime;
    private float mLastHitTime;
    private boolean mInvincible;
    private boolean mDieOnCollect;
    private boolean mDieOnAttack;
    private ChangeComponentsComponent mPossessionComponent;
    private InventoryComponent.UpdateRecord mInventoryUpdate;
    private LauncherComponent mLauncherComponent;
    private int mLauncherHitType;
    private float mInvincibleTime;
    protected float mLastGameEventTime;
    private boolean mForceInvincibility;
//////// HIT_PROCESS 20140424 - MID
    private HitCounterComponent mHitCounter;
//////// HIT_PROCESS 20140424 - END
//////// PLATFORM - MID
    private PlatformComponent mPlatformComponent;
//////// PLATFORM - END
//////// CONTROLLER - MID
    private TakeControlComponent mController;
    private boolean mReleaseControlWhenHit;
//////// CONTROLLER - END


    public HitReactionComponent() {
        super();
        reset();
        setPhase(ComponentPhases.PRE_DRAW.ordinal());
    }

    @Override
    public void reset() {
        mPauseOnAttack = false;
        mPauseOnAttackTime = ATTACK_PAUSE_DELAY;
        mBounceOnTakeHit = false;
        mBounceOnTakeMagnitude = DEFAULT_BOUNCE_MAGNITUDE;
        mInvincibleAfterHitTime = 0.0f;
        mInvincible = false;
        mDieOnCollect = false;
        mDieOnAttack = false;
        mPossessionComponent = null;
        mInventoryUpdate = null;
        mLauncherComponent = null;
        mLauncherHitType = HitType.LAUNCH;
        mInvincibleTime = 0.0f;
        mLastGameEventTime = -1.0f;
        mForceInvincibility = false;
//////// HIT_PROCESS 20140424 - MID
        mHitCounter = null;
//////// HIT_PROCESS 20140424 - END
//////// PLATFORM - MID
        mPlatformComponent = null;
//////// PLATFORM - END
//////// CONTROLLER - MID
        mController = null;
        mReleaseControlWhenHit = false;
//////// CONTROLLER - END
    }

    /** Called when this object attacks another object. */
    public void hitVictim(GameObject parent, GameObject victim, int hitType,
            boolean hitAccepted) {
        if (hitAccepted) {
            final TimeSystem time = sSystemRegistry.timeSystem;

//////// HIT_PROCESS 20140424 - MID
            if (mHitCounter != null) {
                mHitCounter.addDealHit(parent, victim, hitType, time);
            }
//////// HIT_PROCESS 20140424 - END

            if (mPauseOnAttack && hitType == CollisionParameters.HitType.HIT) {
                time.freeze(mPauseOnAttackTime);
            }

            if (mDieOnAttack) {
                parent.life = 0;
            }

            if (hitType == mLauncherHitType && mLauncherComponent != null) {
                mLauncherComponent.prepareToLaunch(victim, parent);
            }

            if (hitType != CollisionParameters.HitType.INVALID) {
                if (hitType == CollisionParameters.HitType.DIALOG) {
                    parent.getVelocity().zero();
                    parent.getTargetVelocity().zero();
                }

                final float gameTime = time.getGameTime();
                spawnGameEventOnDealHit(hitType, gameTime);

                spawnSoundOnDealHit(hitType);

                spawnObjectOnDealHit(hitType, parent, victim);
            }
        }
    }

    /** Called when this object is hit by another object. */
    public boolean receivedHit(GameObject parent, GameObject attacker, int hitType) {
        final TimeSystem time = sSystemRegistry.timeSystem;

//////// HIT_PROCESS 20140424 - MID
        if (mHitCounter != null) {
            mHitCounter.addTakeHit(parent, attacker, hitType, time);
        }
//////// HIT_PROCESS 20140424 - END

        final float gameTime = time.getGameTime();

        if (hitType != CollisionParameters.HitType.INVALID) {
            hitType = spawnGameEventOnTakeHit(hitType, gameTime);
        }

        switch(hitType) {
            case CollisionParameters.HitType.INVALID:
                break;

            case CollisionParameters.HitType.HIT:
                // don't hit our friends, if we have friends.
                final boolean sameTeam = (parent.team == attacker.team && parent.team != Team.NONE);
                if (!mForceInvincibility && !mInvincible && parent.life > 0 && !sameTeam) {
//////// CONTROLLER - BEGIN
/*
//////// STRENGTH - BEGIN
//                parent.life -= 1;
//////// STRENGTH - MID
                parent.life -= attacker.strength;
//////// STRENGTH - END
*/
//////// CONTROLLER - MID
                    boolean getHit = true;
                    if (mController != null && mReleaseControlWhenHit) {
                        getHit = mController.releaseWithHit(parent, attacker, hitType);
                    }
                    if (getHit) {
                        parent.life -= attacker.strength;
                    }
//////// CONTROLLER - END

                    if (mBounceOnTakeHit && parent.life > 0) {
                        VectorPool pool = sSystemRegistry.vectorPool;
                        Vector2 newVelocity = pool.allocate(parent.getPosition());
                        newVelocity.subtract(attacker.getPosition());
                        newVelocity.set(0.5f * Utils.sign(newVelocity.x),
                                0.5f * Utils.sign(newVelocity.y));
                        newVelocity.multiply(mBounceOnTakeMagnitude);
                        parent.setVelocity(newVelocity);
                        parent.getTargetVelocity().zero();
                        pool.release(newVelocity);
                    }

                    if (mInvincibleAfterHitTime > 0.0f) {
                        mInvincible = true;
                        mInvincibleTime = mInvincibleAfterHitTime;
                    }

                } else {
                    // Ignore this hit.
                    hitType = CollisionParameters.HitType.INVALID;
                }
                break;
            case CollisionParameters.HitType.DEATH:
                // respect teams?
                parent.life = 0;
                break;
            case CollisionParameters.HitType.COLLECT:
                if (mInventoryUpdate != null && parent.life > 0) {
                    InventoryComponent attackerInventory = attacker.findByClass(InventoryComponent.class);
                    if (attackerInventory != null) {
                        attackerInventory.applyUpdate(mInventoryUpdate);
                    }
                }
                if (mDieOnCollect && parent.life > 0) {
                    parent.life = 0;
                }
                break;
            case CollisionParameters.HitType.POSSESS:
                if (mPossessionComponent != null && parent.life > 0 && attacker.life > 0) {
                    mPossessionComponent.activate(parent);
                } else {
                    hitType = CollisionParameters.HitType.INVALID;
                }
                break;
            case CollisionParameters.HitType.LAUNCH:
                break;
            case CollisionParameters.HitType.DIALOG:
                parent.life = 0;
                break;
//////// PLATFORM - MID
            case CollisionParameters.HitType.PLATFORM:
                if (mPlatformComponent != null) {
                    mPlatformComponent.add(parent, attacker);
                }
                break;
//////// PLATFORM - END

            default:
                break;
        }


        if (hitType != CollisionParameters.HitType.INVALID) {
            spawnSoundOnTakeHit(hitType);
            mLastHitTime = gameTime;
            parent.setCurrentAction(ActionType.HIT_REACT);
            parent.lastReceivedHitType = hitType;

            spawnObjectOnTakeHit(hitType, parent, attacker);
        }

        return hitType != CollisionParameters.HitType.INVALID;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject)parent;
        TimeSystem time = sSystemRegistry.timeSystem;

        final float gameTime = time.getGameTime();

        if (mInvincible && mInvincibleTime > 0) {
            if (time.getGameTime() > mLastHitTime + mInvincibleTime) {
                mInvincible = false;
            }
        }

        // This means that the lastReceivedHitType will persist for two frames, giving all systems
        // a chance to react.
        if (gameTime - mLastHitTime > timeDelta) {
            parentObject.lastReceivedHitType = CollisionParameters.HitType.INVALID;
        }
    }

    private int spawnGameEventOnTakeHit(int hitType, float gameTime) {
        final int gameEvent = getOnTakeHitGameEvent(hitType);
        final int gameEventIndex = getOnTakeHitGameEventIndexData(hitType);

        if (gameEvent != GameFlowEvent.EVENT_INVALID) {
            if (mLastGameEventTime < 0.0f || (gameTime > mLastGameEventTime + EVENT_SEND_DELAY)) {
                LevelSystem level = sSystemRegistry.levelSystem;
                level.sendGameEvent(gameEvent, gameEventIndex, true);
            } else {
                // special case.  If we're waiting for a hit type to spawn an event and
                // another event has just happened, eat this hit so we don't miss
                // the chance to send the event.
                hitType = CollisionParameters.HitType.INVALID;
            }
            mLastGameEventTime = gameTime;
        }
        return hitType;
    }

    private int spawnGameEventOnDealHit(int hitType, float gameTime) {
        final int gameEvent = getOnDealHitGameEvent(hitType);
        final int gameEventIndex = getOnDealHitGameEventIndexData(hitType);

        if (gameEvent != GameFlowEvent.EVENT_INVALID) {
            if (mLastGameEventTime < 0.0f || (gameTime > mLastGameEventTime + EVENT_SEND_DELAY)) {
                LevelSystem level = sSystemRegistry.levelSystem;
                level.sendGameEvent(gameEvent, gameEventIndex, true);
// !!!! ???? TODO: want that for the "deal hit" ? ???? !!!!
/*
            } else {
                // special case.  If we're waiting for a hit type to spawn an event and
                // another event has just happened, eat this hit so we don't miss
                // the chance to send the event.
                hitType = CollisionParameters.HitType.INVALID;
*/
            }
            mLastGameEventTime = gameTime;
        }
        return hitType;
    }

    private void spawnSoundOnTakeHit(int hitType) {
        final SoundSystem.Sound sound = getOnTakeHitSound(hitType);
        if (sound != null) {
            final SoundSystem soundSystem = sSystemRegistry.soundSystem;
            if (soundSystem != null) {
            	soundSystem.play(sound, false, SoundSystem.PRIORITY_NORMAL);
            }
        }
    }

    private void spawnSoundOnDealHit(int hitType) {
        final SoundSystem.Sound sound = getOnDealHitSound(hitType);
        if (sound != null) {
            final SoundSystem soundSystem = sSystemRegistry.soundSystem;
            if (soundSystem != null) {
            	soundSystem.play(sound, false, SoundSystem.PRIORITY_NORMAL);
            }
        }
    }

    private void spawnObjectOnTakeHit(int hitType, GameObject parent, GameObject other) {
        final int spawnOrdinal = getOnTakeHitSpawnObjectOrdinal(hitType);
        if (spawnOrdinal != 0) {
            GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
            GameObjectManager manager = sSystemRegistry.gameObjectManager;

            if (factory != null) {
                final float x = getOnTakeHitSpawnObjectAlignToOtherX(hitType) ?
                                 other.getPosition().x : parent.getPosition().x;
                final float y = getOnTakeHitSpawnObjectAlignToOtherY(hitType) ?
                                 other.getPosition().y : parent.getPosition().y;

                GameObject object = factory.spawnFromOrdinal(spawnOrdinal, x, y, parent.facingDirection.x < 0.0f);
                if (object != null && manager != null) {
                    if (getOnTakeHitSpawnObjectKeepMovement(hitType)) {
                        object.getVelocity().add(parent.getVelocity());
                        object.getTargetVelocity().add(parent.getTargetVelocity());
                    }
                    object.getVelocity().add(getOnTakeHitSpawnObjectAdditionalMovementX(hitType), getOnTakeHitSpawnObjectAdditionalMovementY(hitType));
                    object.getTargetVelocity().add(getOnTakeHitSpawnObjectAdditionalMovementX(hitType), getOnTakeHitSpawnObjectAdditionalMovementY(hitType));

                    manager.add(object);
                }
            }
        }
    }

    private void spawnObjectOnDealHit(int hitType, GameObject parent, GameObject other) {
        final int spawnOrdinal = getOnDealHitSpawnObjectOrdinal(hitType);
        if (spawnOrdinal != 0) {
            GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
            GameObjectManager manager = sSystemRegistry.gameObjectManager;

            if (factory != null) {
                final float x = getOnDealHitSpawnObjectAlignToOtherX(hitType) ?
                                 other.getPosition().x : parent.getPosition().x;
                final float y = getOnDealHitSpawnObjectAlignToOtherY(hitType) ?
                                 other.getPosition().y : parent.getPosition().y;

                GameObject object = factory.spawnFromOrdinal(spawnOrdinal, x, y, parent.facingDirection.x < 0.0f);
                if (object != null && manager != null) {
                    if (getOnDealHitSpawnObjectKeepMovement(hitType)) {
                        object.getVelocity().add(parent.getVelocity());
                        object.getTargetVelocity().add(parent.getTargetVelocity());
                    }
                    object.getVelocity().add(getOnDealHitSpawnObjectAdditionalMovementX(hitType), getOnDealHitSpawnObjectAdditionalMovementY(hitType));
                    object.getTargetVelocity().add(getOnDealHitSpawnObjectAdditionalMovementX(hitType), getOnDealHitSpawnObjectAdditionalMovementY(hitType));

                    manager.add(object);
                }
            }
        }
    }



    public void setPauseOnAttack(boolean pause) {
        mPauseOnAttack = pause;
    }

    public void setPauseOnAttackTime(float seconds) {
        mPauseOnAttackTime = seconds;
    }

    public void setBounceOnTakeHit(boolean bounce) {
        mBounceOnTakeHit = bounce;
    }

    public void setBounceOnTakeMagnitude(float magnitude) {
        mBounceOnTakeMagnitude = magnitude;
    }

    public void setInvincibleTime(float time) {
        mInvincibleAfterHitTime = time;
    }

    public void setDieWhenCollected(boolean die) {
        mDieOnCollect = true;
    }

    public void setDieOnAttack(boolean die) {
        mDieOnAttack = die;
    }

    public void setInvincible(boolean invincible) {
        mInvincible = invincible;
    }

    public void setPossessionComponent(ChangeComponentsComponent component) {
        mPossessionComponent = component;
    }

    public void setInventoryUpdate(InventoryComponent.UpdateRecord update) {
        mInventoryUpdate = update;
    }

    public void setLauncherComponent(LauncherComponent component, int launchHitType) {
        mLauncherComponent = component;
        mLauncherHitType = launchHitType;
    }

//////// PLATFORM - MID
    public void setPlatformComponent(PlatformComponent component) {
    	mPlatformComponent = component;
    }
//////// PLATFORM - END

    public void setForceInvincible(boolean force) {
        mForceInvincibility = force;
    }

//////// HIT_PROCESS 20140424 - MID
    public void setHitCounter(HitCounterComponent hitCounter) {
        mHitCounter = hitCounter;
    }
//////// HIT_PROCESS 20140424 - END

//////// CONTROLLER - MID
// !!!! TODO: could give the boolean as parameter and add more complex handling of controller !!!!
    public void setControlledObject(TakeControlComponent controller) {
        mController = controller;
        mReleaseControlWhenHit = true;
    }
//////// CONTROLLER - END


    public abstract void setGameEventOnDealHit(int hitType, int gameFlowEventType, int indexData);
    protected abstract int getOnDealHitGameEvent(int hitType);
    protected abstract int getOnDealHitGameEventIndexData(int hitType);

    public abstract void setSoundOnDealHit(int hitType, SoundSystem.Sound sound);
    protected abstract SoundSystem.Sound getOnDealHitSound(int hitType);

    public abstract void setSpawnObjectOnDealHit(int hitType, int objectTypeOrdinal, boolean alignToOtherX, boolean alignToOtherY);
    public abstract void setSpawnObjectKeepMovementOnDealHit(int hitType, boolean keepMovement);
    public abstract void setSpawnObjectAdditionalMovementOnDealHit(int hitType, float movementX, float movementY);
    protected abstract int getOnDealHitSpawnObjectOrdinal(int hitType);
    protected abstract boolean getOnDealHitSpawnObjectAlignToOtherX(int hitType);
    protected abstract boolean getOnDealHitSpawnObjectAlignToOtherY(int hitType);
    protected abstract boolean getOnDealHitSpawnObjectKeepMovement(int hitType);
    protected abstract float getOnDealHitSpawnObjectAdditionalMovementX(int hitType);
    protected abstract float getOnDealHitSpawnObjectAdditionalMovementY(int hitType);


    public abstract void setGameEventOnTakeHit(int hitType, int gameFlowEventType, int indexData);
    protected abstract int getOnTakeHitGameEvent(int hitType);
    protected abstract int getOnTakeHitGameEventIndexData(int hitType);

    public abstract void setSoundOnTakeHit(int hitType, SoundSystem.Sound sound);
    protected abstract SoundSystem.Sound getOnTakeHitSound(int hitType);

    public abstract void setSpawnObjectOnTakeHit(int hitType, int objectTypeOrdinal, boolean alignToOtherX, boolean alignToOtherY);
    public abstract void setSpawnObjectKeepMovementOnTakeHit(int hitType, boolean keepMovement);
    public abstract void setSpawnObjectAdditionalMovementOnTakeHit(int hitType, float movementX, float movementY);
    protected abstract int getOnTakeHitSpawnObjectOrdinal(int hitType);
    protected abstract boolean getOnTakeHitSpawnObjectAlignToOtherX(int hitType);
    protected abstract boolean getOnTakeHitSpawnObjectAlignToOtherY(int hitType);
    protected abstract boolean getOnTakeHitSpawnObjectKeepMovement(int hitType);
    protected abstract float getOnTakeHitSpawnObjectAdditionalMovementX(int hitType);
    protected abstract float getOnTakeHitSpawnObjectAdditionalMovementY(int hitType);
}
