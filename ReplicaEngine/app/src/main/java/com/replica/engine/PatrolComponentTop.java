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
import com.replica.engine.HotSpotSystem.HotSpotType;

/**
 * This component implements the "patrolling" behavior for AI characters.
 * If "random" is not set:
 * Patrolling characters will walk forward on the map until they hit a direction hot spot or a wall,
 * in which case they may change direction.
 * Alternately, if "random" is set:
 * Patrolling characters will walk forward on the map until the timer expires,
 * in which case they may change direction (after pausing until another timer expires).
 * Patrollers can also be configured via this component to attack the player
 * if appropriate conditions are met.
 */
public class PatrolComponentTop extends GameComponent {
    private float mMaxSpeedX;
    private float mMaxSpeedY;

    private boolean mGoLeft;
    private boolean mGoRight;
    private boolean mGoDown;
    private boolean mGoUp;

    private float mAccelerationX;
    private float mAccelerationY;

    private boolean mAttack;
    private float mAttackAtDistance;
    private boolean mAttackStopsMovement;
    private float mAttackDuration;
    private float mAttackDelay;
    private boolean mTurnToFacePlayer;

    private boolean mRandom;
    private float mRandomDelay;
    private float mRandomTimer;
    private float mPauseDelay;
    private float mPauseTimer;

    private float mLastAttackTime;
    Vector2 mWorkingVector;
    Vector2 mWorkingVector2;


    public PatrolComponentTop() {
        super();
        mWorkingVector = new Vector2();
        mWorkingVector2 = new Vector2();

        reset();
        setPhase(GameComponent.ComponentPhases.THINK.ordinal());
    }

    @Override
    public void reset() {
        mTurnToFacePlayer = false;

        mMaxSpeedX = 0.0f;
        mMaxSpeedY = 0.0f;

        mGoLeft = false;
        mGoRight = false;
        mGoDown = false;
        mGoUp = false;

        mAccelerationX = 0.0f;
        mAccelerationY = 0.0f;

        mRandom = false;
        mRandomDelay = 0.0f;
        mRandomTimer = 0.0f;
        mPauseDelay = 0.0f;
        mPauseTimer = 0.0f;

        mAttack = false;
        mAttackAtDistance = 0.0f;
        mAttackStopsMovement = false;
        mAttackDuration = 0.0f;
        mAttackDelay = 0.0f;
        mWorkingVector.zero();
        mWorkingVector2.zero();
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject) parent;

        if (parentObject.getCurrentAction() == ActionType.INVALID
                || parentObject.getCurrentAction() == ActionType.HIT_REACT) {
            parentObject.setCurrentAction(GameObject.ActionType.MOVE);
        }

        if (parentObject.life <= 0) {
            return;
        }

        final GameObjectManager manager = sSystemRegistry.gameObjectManager;
        GameObject player = null;
        if (manager != null) {
            player = manager.getPlayer();
        }

        if (mAttack) {
            updateAttack(player, parentObject);
        }

        if (parentObject.getCurrentAction() == GameObject.ActionType.MOVE
            && (mMaxSpeedX != 0.0f || mMaxSpeedY != 0.0f) ) {
            boolean goLeft = false;
            boolean goRight = false;
            boolean goUp = false;
            boolean goDown = false;
            boolean pauseX = (mMaxSpeedX == 0.0f);
            boolean pauseY = (mMaxSpeedY == 0.0f);

            if (mRandom) {
                if (mRandomTimer > 0.0f) {
                    mRandomTimer -= timeDelta;
                    if (mRandomTimer <= 0.0f) {
                        mPauseTimer = mPauseDelay;
                        mRandomTimer = 0.0f;
                        pauseX = true;
                        pauseY = true;
                    }
                } else {
                    mPauseTimer -= timeDelta;
                    if (mPauseTimer <= 0.0f) {
                        float random = (float)Math.random();
                        if (random < 0.25f) {
                            goLeft = true;
                        } else if ((random >= 0.25f) && (random < 0.5f)) {
                            goRight = true;
                        } else if ((random >= 0.5f) && (random < 0.75f)) {
                            goUp = true;
                        } else {
                            goDown = true;
                        }
                        mPauseTimer = 0.0f;
                        mRandomTimer = mRandomDelay;
                    } else {
                        pauseX = true;
                        pauseY = true;
                    }
                }
            } else {
                int hotSpot = HotSpotSystem.HotSpotType.NONE;
                HotSpotSystem hotSpotSystem = sSystemRegistry.hotSpotSystem;
                if (hotSpotSystem != null) {
                    // TODO: ack, magic number
//                    hotSpot = hotSpotSystem.getHotSpot(parentObject.getCenteredPositionX(),
//                        parentObject.getPosition().y + 10.0f);
                    hotSpot = hotSpotSystem.getHotSpot(parentObject.getCenteredPositionX(),
                        parentObject.getCenteredPositionY());
                }

                goLeft = (parentObject.touchingRightWall() || (hotSpot == HotSpotType.GO_LEFT));
                goRight = (parentObject.touchingLeftWall() || (hotSpot == HotSpotType.GO_RIGHT));

                goUp = (parentObject.touchingGround() || (hotSpot == HotSpotType.GO_UP));
                goDown = (parentObject.touchingCeiling() || (hotSpot == HotSpotType.GO_DOWN));
            }

            final float targetVelocityX = parentObject.getTargetVelocity().x;
            final float targetVelocityY = parentObject.getTargetVelocity().y;

            goLeft = goLeft && (targetVelocityX >= 0.0f);
            goRight = goRight && (targetVelocityX <= 0.0f);
            goUp = goUp && (targetVelocityY <= 0.0f);
            goDown = goDown && (targetVelocityY >= 0.0f);

            if (mTurnToFacePlayer && player != null && player.life > 0) {
                final float deltaX = player.getCenteredPositionX() - parentObject.getCenteredPositionX();
                final int targetFacingDirectionX = Utils.sign(deltaX);
                final float closestDistanceX = player.width / 2.0f;

                if (targetFacingDirectionX < 0.0f) {
                    // we want to turn left
                    if (goRight) {
                        goRight = false;
                        pauseX = true;
                    } else if (targetFacingDirectionX != Utils.sign(parentObject.facingDirection.x)) {
                        goLeft = true;
                    }
                } else if (targetFacingDirectionX > 0.0f) {
                    // we want to turn right
                    if (goLeft) {
                        goLeft = false;
                        pauseX = true;
                    } else if (targetFacingDirectionX != Utils.sign(parentObject.facingDirection.x)) {
                        goRight = true;
                    }
                }

                if (Math.abs(deltaX) < closestDistanceX) {
                    goRight = false;
                    goLeft = false;
                    pauseX = true;
                }


                final float deltaY = player.getCenteredPositionX() - parentObject.getCenteredPositionX();
                final int targetFacingDirectionY = Utils.sign(deltaY);
                final float closestDistanceY = player.width / 2.0f;

                if (targetFacingDirectionY < 0.0f) {
                    // we want to turn down
                    if (goUp) {
                        goUp = false;
                        pauseY = true;
                    } else if (targetFacingDirectionY != Utils.sign(parentObject.facingDirection.y)) {
                        goDown = true;
                    }
                } else if (targetFacingDirectionY > 0.0f) {
                    // we want to turn to the right
                    if (goDown) {
                        goDown = false;
                        pauseY = true;
                    } else if (targetFacingDirectionY != Utils.sign(parentObject.facingDirection.y)) {
                        goUp = true;
                    }
                }

                if (Math.abs(deltaY) < closestDistanceY) {
                    goUp = false;
                    goDown = false;
                    pauseY = true;
                }
            }

            if (goRight) {
                parentObject.getTargetVelocity().x = mMaxSpeedX;
                parentObject.getAcceleration().x = mAccelerationX;
                mGoLeft = false;
                mGoRight = true;
            } else if (goLeft) {
                parentObject.getTargetVelocity().x = -mMaxSpeedX;
                parentObject.getAcceleration().x = mAccelerationX;
            }
            else if (pauseX) {
                parentObject.getTargetVelocity().x = 0;
                parentObject.getAcceleration().x = mAccelerationX;
            } else {
                if (mGoLeft) {
                    parentObject.getTargetVelocity().x = -mMaxSpeedX;
                    parentObject.getAcceleration().x = mAccelerationX;
                    mGoLeft = false;
                } else if (mGoRight) {
                    parentObject.getTargetVelocity().x = mMaxSpeedX;
                    parentObject.getAcceleration().x = mAccelerationX;
                    mGoRight = false;
                }
            }

            if (goUp) {
                parentObject.getTargetVelocity().y = mMaxSpeedY;
                parentObject.getAcceleration().y = mAccelerationY;
            } else if (goDown) {
                parentObject.getTargetVelocity().y = -mMaxSpeedY;
                parentObject.getAcceleration().y = mAccelerationY;
            } else if (pauseY) {
                parentObject.getTargetVelocity().y = 0;
                parentObject.getAcceleration().y = mAccelerationY;
            } else {
                if (mGoDown) {
                    parentObject.getTargetVelocity().y = -mMaxSpeedY;
                    parentObject.getAcceleration().y = mAccelerationY;
                    mGoDown = false;
                } else if (mGoUp) {
                    parentObject.getTargetVelocity().y = mMaxSpeedY;
                    parentObject.getAcceleration().y = mAccelerationY;
                    mGoUp = false;
                }
            }
        }
    }

    private void updateAttack(GameObject player, GameObject parentObject) {
        TimeSystem time = sSystemRegistry.timeSystem;
        final float gameTime = time.getGameTime();

        boolean visible = true;
        CameraSystem camera = sSystemRegistry.cameraSystem;
        ContextParameters context = sSystemRegistry.contextParameters;
        final float dx = Math.abs(parentObject.getCenteredPositionX() - camera.getFocusPositionX());
        final float dy = Math.abs(parentObject.getCenteredPositionY() - camera.getFocusPositionY());
        if ((dx > (context.gameWidth / 2.0f)) || (dy > (context.gameHeight / 2.0f))) {
            visible = false;
        }
        if (visible && (parentObject.getCurrentAction() == GameObject.ActionType.MOVE)) {
            boolean closeEnough = false;
            boolean timeToAttack = (gameTime - mLastAttackTime) > mAttackDelay;
            if (mAttackAtDistance > 0 && player != null && player.life > 0 && timeToAttack) {
                // only attack if we are facing the player
                if ((Utils.sign(player.getPosition().x - parentObject.getPosition().x) == Utils.sign(parentObject.facingDirection.x))
                    || (Utils.sign(player.getPosition().y - parentObject.getPosition().y) == Utils.sign(parentObject.facingDirection.y))) {
                    mWorkingVector.x = parentObject.getCenteredPositionX();
                    mWorkingVector.y = parentObject.getCenteredPositionY();
                    mWorkingVector2.x = player.getCenteredPositionX();
                    mWorkingVector2.y = player.getCenteredPositionY();
                    if (mWorkingVector2.distance2(mWorkingVector) < (mAttackAtDistance * mAttackAtDistance)) {
                        closeEnough = true;
                    }
                }
            } else {
                closeEnough = true; // If no distance has been set, don't worry about the player's position.
            }

            if (timeToAttack && closeEnough) {
                // Time to attack.
                parentObject.setCurrentAction(GameObject.ActionType.ATTACK);
                mLastAttackTime = gameTime;
                if (mAttackStopsMovement) {
                    parentObject.getVelocity().zero();
                    parentObject.getTargetVelocity().zero();
                }
            }
        } else if (parentObject.getCurrentAction() == GameObject.ActionType.ATTACK) {
            if (gameTime - mLastAttackTime > mAttackDuration) {
                parentObject.setCurrentAction(GameObject.ActionType.MOVE);
                if (mAttackStopsMovement) {
                    parentObject.getTargetVelocity().x = mMaxSpeedX * Utils.sign(parentObject.facingDirection.x);
                    parentObject.getAcceleration().x = mAccelerationX;
                    parentObject.getTargetVelocity().y = mMaxSpeedY * Utils.sign(parentObject.facingDirection.y);
                    parentObject.getAcceleration().y = mAccelerationY;
                }
            }
        }
    }

    public void setMovement(float speedX, float speedY, float accelerationX, float accelerationY) {
        if (speedX > 0.0f) {
            mGoLeft = false;
            mGoRight = true;
            mMaxSpeedX = speedX;
        } else if (speedX < 0.0f) {
            mGoLeft = true;
            mGoRight = false;
            mMaxSpeedX = -speedX;
        } else {
            mGoLeft = false;
            mGoRight = false;
            mMaxSpeedX = 0.0f;
        }

        if (speedY > 0.0f) {
            mGoDown = false;
            mGoUp = true;
            mMaxSpeedY = speedY;
        } else if (speedY < 0.0f) {
        	mGoDown = true;
        	mGoUp = false;
            mMaxSpeedY = -speedY;
        } else {
        	mGoDown = false;
        	mGoUp = false;
            mMaxSpeedY = 0.0f;
        }

        mAccelerationX = accelerationX;
        mAccelerationY = accelerationY;
    }

    public void setupAttack(float distance, float duration, float delay, boolean stopMovement) {
        mAttack = true;
        mAttackAtDistance = distance;
        mAttackStopsMovement = stopMovement;
        mAttackDuration = duration;
        mAttackDelay = delay;
    }

    public void setTurnToFacePlayer(boolean turn) {
        mTurnToFacePlayer = turn;
    }

    public void setRandom(float randomDelay, float pauseDelay) {
        assert randomDelay > 0;

        mRandom = true;
        mRandomDelay = randomDelay;
        mRandomTimer = 0.0f;
        mPauseDelay = pauseDelay;
        mPauseTimer = pauseDelay;

    }

}
