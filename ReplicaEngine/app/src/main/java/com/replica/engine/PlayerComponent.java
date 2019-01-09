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

import com.replica.engine.Vector2;
import com.replica.engine.BaseObject;
import com.replica.engine.GameObject;
import com.replica.engine.GameComponent;
import com.replica.engine.HitReactionComponent;
import com.replica.engine.InventoryComponent;
import com.replica.engine.CollisionParameters;
import com.replica.engine.CollisionParameters.HitType;
import com.replica.engine.GameObject.ActionType;
import com.replica.engine.HudSystem;
import com.replica.engine.TimeSystem;
import com.replica.engine.HotSpotSystem;
import com.replica.engine.GameFlowEvent;


/**
 Abstract class defining a generic Player.
*/
public abstract class PlayerComponent extends GameComponent {

    public enum State {
        MOVE,
        ATTACK,
        HIT_REACT,
        DEAD,
        WIN,
//////// ANIM - MID
//        WAIT_ANIM,
//////// ANIM - END
        FROZEN,
    }

    protected State mState;
    protected InventoryComponent mInventory;
//?    private Vector2 mHotSpotTestPoint;
    protected HitReactionComponent mHitReaction;
//////// CONTROLLER - MID
    protected boolean mIsMainPlayer;
    protected TakeControlComponent mController;
// !!!! TODO: change to static constant !!!!
    private int mNbSwappers;
    private int[] mSwapperIds;
//////// CONTROLLER - END
    private float mLifeMax;
    private float mLife;
//    private float mLifeRefillSpeed;
    private float mHitReactionTime;
    private float mAttackDelayTime;
    private float mAttackHoldTime;
    protected float mTimer;
    private float mTimerEndAttack;

    public PlayerComponent() {
        super();
//        mHotSpotTestPoint = new Vector2();

//////// CONTROLLER - MID
        mNbSwappers = HotSpotSystem.HotSpotType.SWAP_MAX - HotSpotSystem.HotSpotType.SWAP_MIN;
        mSwapperIds = new int[mNbSwappers];
//////// CONTROLLER - END

        reset();
        setPhase(ComponentPhases.THINK.ordinal());
    }

    @Override
    public void reset() {
        mState = State.MOVE;

        mInventory = null;
//        mHotSpotTestPoint.zero();

        mHitReaction = null;

//////// CONTROLLER - MID
        mIsMainPlayer = true;
        mController = null;
        for (int i=0; i < mNbSwappers; ++i) {
            mSwapperIds[i] = -1;
        }
//////// CONTROLLER - END

        // !!!! => EventRecorder must be initialised before GameObjectFactory in Game::bootstrap !!!!
        mLifeMax = BaseObject.sSystemRegistry.eventRecorder.getDifficultyConstants().getPlayerMaxLife();
        mLife = mLifeMax;
//        mLifeRefillSpeed = getLifeRefillSpeed();

        mHitReactionTime = 0.5f;
        mAttackDelayTime = 0.15f;
        mAttackHoldTime = 0.0f;

        mTimer = 0.0f;
        mTimerEndAttack = 0.0f;
    }

    public void update(float timeDelta, BaseObject parent) {
        TimeSystem time = sSystemRegistry.timeSystem;
        GameObject parentObject = (GameObject)parent;

        preUpdateProcess(parentObject);

        final float gameTime = time.getGameTime();

        if (parentObject.getCurrentAction() == ActionType.INVALID) {
            gotoMove(parentObject);
        }

        if (mInventory != null && mState != State.WIN) {
            InventoryComponent.UpdateRecord inventory = mInventory.getRecord();
            processInventory(inventory, parentObject, gameTime);
        }

        additionalProcess(parentObject, gameTime);

        // watch for hit reactions or death interrupting the state machine
//////// ANIM - BEGIN
        if (mState != State.DEAD && mState != State.WIN) {
//////// ANIM - MID
//        if (mState != State.DEAD && mState != State.WIN && mState != State.WAIT_ANIM) {
//////// ANIM - END
            if (parentObject.life <= 0) {
                gotoDead(gameTime);
            } else if (shouldDieAtPosition(parentObject)) {
                parentObject.life = 0;
                gotoDead(gameTime);
            } else if (mState != State.HIT_REACT &&
                    parentObject.lastReceivedHitType != HitType.INVALID &&
                    parentObject.getCurrentAction() == ActionType.HIT_REACT) {
                gotoHitReact(parentObject, gameTime);
            } else {
                final int hotSpot = getHotSpot(parentObject);
                if (hotSpot == HotSpotSystem.HotSpotType.DIE) {
                    parentObject.life = 0;
                    gotoDead(gameTime);
                } else if (hotSpot == HotSpotSystem.HotSpotType.END_LEVEL) {
                    gotoWin(gameTime);
//////// ANIM - MID
//                } else if (hotSpot == HotSpotSystem.HotSpotType.PLAY_ANIMATION) {
//                    gotoWaitAnim(gameTime);
//////// ANIM - END
                }
            }
        }

//////// CONTROLLER - MID
        if (!swap(gameTime, parentObject)) {
//////// CONTROLLER - END
            switch(mState) {
            case MOVE:
                stateMove(gameTime, timeDelta, parentObject);
                break;
            case ATTACK:
                stateAttack(gameTime, timeDelta, parentObject);
                break;
            case HIT_REACT:
                stateHitReact(gameTime, timeDelta, parentObject);
                break;
            case DEAD:
                stateDead(gameTime, timeDelta, parentObject);
                break;
            case WIN:
                stateWin(gameTime, timeDelta, parentObject);
                break;
//////// ANIM - MID
/*
            case WAIT_ANIM:
                stateWaitAnim(gameTime, timeDelta, parentObject);
                break;
*/
//////// ANIM - END
            case FROZEN:
                stateFrozen(gameTime, timeDelta, parentObject);
                break;
            default:
                break;
            }
//////// CONTROLLER - MID
        }
//////// CONTROLLER - END

        mLife = parentObject.life;
        updateHudLife(mLife / mLifeMax);

        postUpdateProcess(parentObject);
    }

    private void move(float time, float timeDelta, GameObject parentObject) {
        VectorPool pool = sSystemRegistry.vectorPool;
        if (pool != null) {
            Vector2 impulse = pool.allocate();
            getMovement(time, impulse);

            if (impulse.x != 0 || impulse.y != 0) {
                handleMovement(parentObject, impulse, timeDelta);
                parentObject.getImpulse().add(impulse);
            }
            pool.release(impulse);
        }
    }

    private void gotoMove(GameObject parentObject) {
        parentObject.setCurrentAction(GameObject.ActionType.MOVE);
        mState = State.MOVE;
    }

    private void stateMove(float time, float timeDelta, GameObject parentObject) {
        move(time, timeDelta, parentObject);

        if (isAttacking(time)) {
            gotoAttack(parentObject);
        }
    }

    private void gotoAttack(GameObject parentObject) {
        parentObject.setCurrentAction(GameObject.ActionType.ATTACK);
        mState = State.ATTACK;
        mTimer = -1.0f;
    	mTimerEndAttack = -1.0f;

        startAttack(parentObject);
    }

    private void stateAttack(float time, float timeDelta, GameObject parentObject) {
        if (mTimer < 0.0f) {
            // first frame
            mTimer = time;
        }

        if (time - mTimer > mAttackHoldTime) {
            attackBegin(parentObject); // time?
        }

        if (attackEndCondition(time) && (mTimerEndAttack < 0.0f)) {
            mTimerEndAttack = time;
            attackEnd(parentObject); // time?
        }

        if (mTimerEndAttack > 0.0f && (time - mTimerEndAttack > mAttackDelayTime)) {
            finishAttack(parentObject); // time?
            gotoMove(parentObject);
        }
    }

    private void gotoHitReact(GameObject parentObject, float time) {
        if (parentObject.lastReceivedHitType == CollisionParameters.HitType.LAUNCH) {
            if (mState != State.FROZEN) {
                gotoFrozen(parentObject);
            }
        } else {
            mState = State.HIT_REACT;
            mTimer = time;
        }
    }

    private void stateHitReact(float time, float timeDelta, GameObject parentObject) {
        if (time - mTimer > mHitReactionTime) {
            gotoMove(parentObject);
        }
    }

    private void gotoDead(float time) {
        mState = State.DEAD;
        mTimer = time;
    }

    private void stateDead(float time, float timeDelta, GameObject parentObject) {
        if (dieCondition() && parentObject.getCurrentAction() != ActionType.DEATH) {
            parentObject.setCurrentAction(ActionType.DEATH);
            die(parentObject);
        }

        if (shouldDieAtPosition(parentObject)) {
            parentObject.setCurrentAction(ActionType.DEATH);
            die(parentObject);
        }

//////// CONTROLLER - MID
        if (mIsMainPlayer) {
//////// CONTROLLER - END
            if (parentObject.getCurrentAction() == ActionType.DEATH && mTimer > 0.0f) {
                final float elapsed = time - mTimer;
                final HudSystem hud = sSystemRegistry.hudSystem;
                if (hud != null && !hud.isFading()) {
                    if (elapsed > 2.0f) {
                        hud.startFade(false, 1.5f);
                        hud.sendGameEventOnFadeComplete(GameFlowEvent.EVENT_PLAYER_DIE, 0);

                        final EventRecorder recorder = (EventRecorder)sSystemRegistry.eventRecorder;
                        if (recorder != null) {
                            recorder.setLastDeathPosition(parentObject.getPosition());
                            updateLifeCounter(recorder, hud);
                        }
                    }
                }
            }
//////// CONTROLLER - MID
        }
//////// CONTROLLER - END
    }

    private void gotoWin(float time) {
        mState = State.WIN;
        TimeSystem timeSystem = sSystemRegistry.timeSystem;
        mTimer = timeSystem.getRealTime();
        timeSystem.applyScale(0.1f, 8.0f, true);
    }

    private void stateWin(float time, float timeDelta, GameObject parentObject) {
        if (mTimer > 0.0f) {
//////// WIN - BEGIN
            parentObject.setCurrentAction(ActionType.WIN);
            win(parentObject);
//////// WIN - END

//////// CONTROLLER - MID
            // force controlled object destruction
            if (mController != null) {
                mController.destroyControlledObject();
            }
//////// CONTROLLER - END

            TimeSystem timeSystem = sSystemRegistry.timeSystem;
            final float elapsed = timeSystem.getRealTime() - mTimer;
            HudSystem hud = sSystemRegistry.hudSystem;
            if (hud != null && !hud.isFading()) {
                if (elapsed > 2.0f) {
                    hud.startFade(false, 1.5f);
                    hud.sendGameEventOnFadeComplete(GameFlowEvent.EVENT_END_LEVEL, 0);
                }
            }
        }
    }

//////// ANIM - MID
/*
    private void gotoWaitAnim(float time) {
        mState = State.WAIT_ANIM;
        TimeSystem timeSystem = sSystemRegistry.timeSystem;
        mTimer = timeSystem.getRealTime();
        timeSystem.appyScale(0.1f, 8.0f, true);
    }

    private void stateWaitAnim(float time, float timeDelta, GameObject parentObject) {
        if (mTimer > 0.0f) {
            TimeSystem timeSystem = sSystemRegistry.timeSystem;
            final float elapsed = timeSystem.getRealTime() - mTimer;
            HudSystem hud = sSystemRegistry.hudSystem;
            if (hud != null && !hud.isFading()) {
                if (elapsed > 2.0f) {
                    hud.startFade(false, 1.5f);
// !!!! TODO : set correct number for animation ... !!!!
// => use different values for hot spots for each different animation, then store it in a local variable and get it here
                    hud.sendGameEventOnFadeComplete(GameFlowEvent.EVENT_SHOW_ANIMATION, 0);
                }
            }
        }
    }
*/
//////// ANIM - END

    private void gotoFrozen(GameObject parentObject) {
        mState = State.FROZEN;
        parentObject.setCurrentAction(ActionType.FROZEN);
    }

    private void stateFrozen(float time, float timeDelta, GameObject parentObject) {
        if (parentObject.getCurrentAction() == ActionType.MOVE) {
            gotoMove(parentObject);
        }
    }

// !!!! TODO: should move to a more generic/common class !!!!
    protected void spawnObject(int index, float x, float y, boolean flipHorizontal) {
        final GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        GameObjectManager manager = sSystemRegistry.gameObjectManager;
        if (factory != null && manager != null) {
            GameObject object = factory.spawnFromIndex(index, x, y, flipHorizontal);
            if (object != null) {
                manager.add(object);
            }
        }
    }

    protected void spawnController(int typeOrdinal, GameObject parent, int life, float time) {
        final GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        final GameObjectManager manager = sSystemRegistry.gameObjectManager;
        if (factory != null && manager != null) {
            final float x = parent.getPosition().x;
            final float y = parent.getPosition().y;

            final GameObject controller = factory.spawnControllerFromOrdinal(typeOrdinal, x, y, parent, life, time);
            if (controller != null) {
// !!!! TODO: could keep the speeds and transmit them to controller object !!!!
                parent.getVelocity().zero();
                parent.getTargetVelocity().zero();
                parent.getAcceleration().zero();
                parent.getImpulse().zero();
                parent.setCurrentAction( ActionType.MOVE );

                // give control to new object and stop player
                manager.remove(parent);
                manager.add(controller);
                manager.setPlayer(controller);
            }
        }
    }


//////// CONTROLLER - MID
    private final boolean swap(float gameTime, GameObject parentObject) {
        boolean swapped = false;
        if (mController == null) { // controlled object
            final int swapIndex = activateSwapper(gameTime, parentObject);
            if (swapIndex != -1) {
                final int swapperId = mSwapperIds[swapIndex];
                if (swapperId != -1) {
                    spawnController(swapperId, parentObject, parentObject.life, 0.0f);
                    swapped = true;
                }
            }
        } else { // controller object
            final GameObject.ActionType actionType = releaseSwapper(gameTime, parentObject);
            if (actionType != GameObject.ActionType.INVALID) {
                mController.setControlledObjectStatus(actionType);
                mController.releaseControl(parentObject);
                swapped = true;
            }
        }
        return swapped;
    }

    protected int activateSwapper(float gameTime, GameObject parentObject) {
        return -1;
    }

    protected GameObject.ActionType releaseSwapper(float gameTime, GameObject parentObject) {
        return GameObject.ActionType.INVALID;
    }

    // hot spot must be between SWAP_MIN & SWAP_MAX (included)
// !!!! TODO: could have "HotSpotType" as type for the first parameter !!!!
    public void setHotSpotSwapperId(int hotSpot, int id) {
        final int swapHotSpot = hotSpot - HotSpotSystem.HotSpotType.SWAP_MIN;
        if (swapHotSpot >= 0 && swapHotSpot < mNbSwappers) {
            mSwapperIds[swapHotSpot] = id;
        }
    }

    protected final boolean isSwapperHotSpot(int hotSpot) {
        final int swapHotSpot = hotSpot - HotSpotSystem.HotSpotType.SWAP_MIN;
        if (swapHotSpot >= 0 && swapHotSpot < mNbSwappers) {
            return (mSwapperIds[swapHotSpot] != -1);
        }
        return false;
    }

    protected final int getSwapIndex(int hotSpot) {
        int swapIndex = -1;
        if (hotSpot >= HotSpotSystem.HotSpotType.SWAP_MIN &&
                hotSpot < HotSpotSystem.HotSpotType.SWAP_MAX) {
            swapIndex = hotSpot - HotSpotSystem.HotSpotType.SWAP_MIN;
        }
        return swapIndex;
    }
//////// CONTROLLER - END

    protected int getHotSpot(GameObject parentObject) {
        final HotSpotSystem hotSpotSystem = sSystemRegistry.hotSpotSystem;
        if (hotSpotSystem != null) {
            return hotSpotSystem.getHotSpot(parentObject.getCenteredPositionX(),
                    parentObject.getCenteredPositionY());
        }
        return HotSpotSystem.HotSpotType.NONE;
    }


// !!!! TODO : handle DDA !!!!
// => move to game !
/*
    public final void adjustDifficulty(GameObject parent, int levelAttemps) {
        // Super basic DDA.
        // If we've tried this levels several times secretly increase our
        // hit points so the level gets easier.
        // Also make fuel refill faster in the air after we've died too many times.

        if (levelAttemps >= mDifficultyConstants.getDDAStage1Attempts()) {
            if (levelAttemps >= mDifficultyConstants.getDDAStage2Attempts()) {
                parent.life += mDifficultyConstants.getDDAStage2LifeBoost();
                mFuelAirRefillSpeed = mDifficultyConstants.getDDAStage2FuelAirRefillSpeed();
            } else {
                parent.life += mDifficultyConstants.getDDAStage1LifeBoost();
                mFuelAirRefillSpeed = mDifficultyConstants.getDDAStage1FuelAirRefillSpeed();
            }
        }
    }
*/


    public final void setTakeControlComponent(TakeControlComponent takeControl) {
    	mController = takeControl;
        mIsMainPlayer = false;
    }

    public final void setInventory(InventoryComponent inventory) {
        mInventory = inventory;
    }

    public final void setLifeMax(float lifeMax) {
        mLifeMax = lifeMax;
    }

    public final void setHitReactionComponent(HitReactionComponent hitReact) {
        mHitReaction = hitReact;
    }

    public final void setHitReactionTime(float hitReactionTime) {
        mHitReactionTime = hitReactionTime;
    }

    protected void setAttackDelayTime(float attackDelayTime) {
    	mAttackDelayTime = attackDelayTime;
    }

    protected void setAttackHoldTime(float attackHoldTime) {
    	mAttackHoldTime = attackHoldTime;
    }


    // want to attack
    protected abstract boolean isAttacking(float time);

    // attack initialisation
    protected abstract void startAttack(GameObject parentObject);

    // begin of attack
    protected abstract void attackBegin(GameObject parentObject);

    // condition for the attack to end
    protected abstract boolean attackEndCondition(float time);

    // end of attack
    protected abstract void attackEnd(GameObject parentObject);

    // attack finalisation
    protected abstract void finishAttack(GameObject parentObject);


    // specific condition to satisfy to enable death
    protected boolean dieCondition() {
        return true;
    }

    // specific condition to satisfy to die
    protected abstract boolean shouldDieAtPosition(GameObject parentObject);

    // die
    protected abstract void die(GameObject parentObject);

//////// WIN - BEGIN
    // win
    protected abstract void win(GameObject parentObject);
//////// WIN - END

    // update the hud life bar
    protected abstract void updateHudLife(float percentage);

    // update life counter (both in hud & event recorder)
    protected abstract void updateLifeCounter(EventRecorder recorder, HudSystem hud);

    // process particular stuff if inventory satisfy condition(s)
    protected abstract void processInventory(InventoryComponent.UpdateRecord inventory, GameObject parent, float time);

    // process additional stuff
    protected abstract void additionalProcess(GameObject parent, float time);

    protected void preUpdateProcess(GameObject parent) {
    }

    protected void postUpdateProcess(GameObject parent) {
    }

    protected abstract void getMovement(float time, Vector2 impulse);

    protected abstract void handleMovement(GameObject parentObject, Vector2 impulse, float timeDelta);

}
