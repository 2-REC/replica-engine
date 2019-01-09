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
import com.replica.engine.SoundSystem.Sound;

/**
 * Player Animation game object component.  Responsible for selecting an animation to describe the 
 * player's current state.  Requires the object to contain a SpriteComponent to play animations.
 */
public abstract class AnimationComponent extends GameComponent {

    protected SpriteComponent mSprite;

    protected GameObject.ActionType mPreviousAction;

    protected ChangeComponentsComponent mDamageSwap;
    private int mLifeDamageSwap;

//  private InventoryComponent mInventory;

    private boolean mVisible;
    private float mOpacity;

    private float mFlickerInterval;
    private float mFlickerDuration;

    private float mLastFlickerTime;
    private boolean mFlickerOn;
    private float mFlickerTimeRemaining;

    protected boolean mHideDeath;
    private Sound mDeathSound;


    public AnimationComponent() {
        super();
        reset();
        setPhase(ComponentPhases.ANIMATION.ordinal());
    }

    @Override
    public void reset() {
        mPreviousAction = ActionType.INVALID;
        mSprite = null;

        mLifeDamageSwap = 1;
        mDamageSwap = null;

//        mInventory = null;

        mVisible = true;
        mOpacity = 1.0f;

        mFlickerInterval = 0.15f;
        mFlickerDuration = 3.0f;

        mLastFlickerTime = 0.0f;
        mFlickerOn = false;
        mFlickerTimeRemaining = 0.0f;

        mHideDeath = false;
        mDeathSound = null;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        if (mSprite != null) {

            GameObject parentObject = (GameObject) parent;
            final ActionType currentAction = parentObject.getCurrentAction();

            final TimeSystem time = sSystemRegistry.timeSystem;
            final float gameTime = time.getGameTime();

            if (currentAction != ActionType.HIT_REACT && mPreviousAction == ActionType.HIT_REACT) {
                mFlickerTimeRemaining = mFlickerDuration;
            }


            mVisible = true;

            // Turn on visual effects when the player's life reaches "mLifeDamageSwap".
            if (mDamageSwap != null) {
                if (parentObject.life == mLifeDamageSwap && !mDamageSwap.getCurrentlySwapped()) {
                    mDamageSwap.activate(parentObject);
                } else if (parentObject.life != mLifeDamageSwap && mDamageSwap.getCurrentlySwapped()) {
                    mDamageSwap.activate(parentObject);
                }
            }

            handleExtra(parentObject, gameTime);

            if (currentAction == ActionType.MOVE) {
                handleMove(parentObject, gameTime);
            } else if (currentAction == ActionType.ATTACK) {
                handleAttack(parentObject, gameTime);
            } else if (currentAction == ActionType.HIT_REACT) {
                handleHitReact(parentObject, gameTime);
            } else if (currentAction == ActionType.DEATH) {
                handleDeath(parentObject, gameTime);
            } else if (currentAction == ActionType.FROZEN) {
                handleFrozen(parentObject, gameTime);
            }

            if (mFlickerTimeRemaining > 0.0f) {
                mFlickerTimeRemaining -= timeDelta;
                if (gameTime > mLastFlickerTime + mFlickerInterval) {
                    mLastFlickerTime = gameTime;
                    mFlickerOn = !mFlickerOn;
                }
                mSprite.setVisible(mFlickerOn);
            } else {
                mSprite.setVisible(mVisible);
                mSprite.setOpacity(mOpacity);
            }

            mPreviousAction = currentAction;
        }
    }

// !!!! TODO: should move to a more generic/common class !!!!
    protected void spawnObject(int index, float x, float y) {
        final GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        GameObjectManager manager = sSystemRegistry.gameObjectManager;
        if (factory != null && manager != null) {
            GameObject object = factory.spawnFromIndex(index, x ,y, false);
            if (object != null) {
                manager.add(object);
            }
        }
    }

    protected void handleDeath(GameObject parentObject, float gameTime) {
        if (mPreviousAction != ActionType.DEATH) {
            if (mDeathSound != null) {
                final SoundSystem sound = sSystemRegistry.soundSystem;
                if (sound != null) {
                    sound.play(mDeathSound, false, SoundSystem.PRIORITY_NORMAL);
                }
            }

            if (parentObject.lastReceivedHitType == HitType.DEATH) {
        	    deathByHit(parentObject);
            } else if (dieHotSpot(parentObject)) {
        	    deathByHotSpot(parentObject);
            } else {
                death(parentObject);
            }

            mFlickerTimeRemaining = 0.0f;
        }

        if (mHideDeath) {
            mVisible = false;
        }
    }

    protected boolean dieHotSpot(GameObject parentObject) {
        final HotSpotSystem hotSpot = sSystemRegistry.hotSpotSystem;
        if (hotSpot != null) {
            if (hotSpot.getHotSpot(parentObject.getCenteredPositionX(), parentObject.getCenteredPositionY())
                  == HotSpotSystem.HotSpotType.DIE) {
                return true;
            }
        }
        return false;
    }


    protected boolean mainAttack(float gameTime, GameObject parentObject) {
        return true;
    }


    public void setSprite(SpriteComponent sprite) {
        mSprite = sprite;
    }

    public final void setDamageSwap(ChangeComponentsComponent damageSwap) {
        mDamageSwap = damageSwap;
    }

/*
    public void setInventory(InventoryComponent inventory) {
        mInventory = inventory;
    }
*/

    public void setLifeDamageSwap(int value) {
        mLifeDamageSwap = value;
    }

    public void setFlickerInterval(float value) {
        mFlickerInterval = value;
    }

    public void setFlickerDuration(float value) {
        mFlickerDuration = value;
    }


    public void setDeathSound(Sound sound) {
    	mDeathSound = sound;
    }

    ////////////////////////////////////////////////////////////////

    protected abstract void getFacingDirection(GameObject parentObject, Vector2 impulse);

    protected abstract void handleMove(GameObject parentObject, float gameTime);

    protected abstract void handleAttack(GameObject parentObject, float gameTime);

    protected abstract void handleHitReact(GameObject parentObject, float gameTime);

    protected abstract void handleFrozen(GameObject parentObject, float gameTime);

    protected void handleExtra(GameObject parentObject, float gameTime) {}

    ////////////////////////////////////////////////////////////////

    protected void deathByHit(GameObject parentObject) {
    	death(parentObject);
    }
    protected void deathByHotSpot(GameObject parentObject) {
    	death(parentObject);
    }

    protected abstract void death(GameObject parentObject);

    ////////////////////////////////////////////////////////////////

}
