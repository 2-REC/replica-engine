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
 * Top Animation game object component ( 2D RPG ).
 */
public class AnimationComponentTop extends AnimationComponent {

    public enum PlayerAnimations {
        IDLE,
        MOVE,
        MOVE_FAST,
//        JUMP_UP,
//        JUMP_DOWN,
        ATTACK,
        ATTACK_ALTERNATE,
        HIT_REACT,
        DEATH,
        DEATH_BY_HIT,
        DEATH_BY_HOTSPOT,
        FROZEN
    }

    private float mMoveInitSpeed;
    private float mMoveFastSpeed;


    @Override
    public void reset() {
        super.reset();

        mMoveInitSpeed = 30.0f;
        mMoveFastSpeed = 300.0f;
    }

    public void setMoveInitSpeed(float value) {
    	mMoveInitSpeed = value;
    }

    public void setMoveFastSpeed(float value) {
    	mMoveFastSpeed = value;
    }

    @Override
    protected void getFacingDirection(GameObject parentObject, Vector2 impulse) {
        final InputGameInterface input = sSystemRegistry.inputGameInterface;
        final InputXY dpad = input.getMovementPad();
        if (dpad.getX() < 0.0f) {
            impulse.x = -1.0f; 
        } else if (dpad.getX() > 0.0f) {
        	impulse.x = 1.0f;
        }
        if (dpad.getY() < 0.0f) {
            impulse.y = -1.0f; 
        } else if (dpad.getY() > 0.0f) {
        	impulse.y = 1.0f;
        }
    }

    @Override
    protected void handleMove(GameObject parentObject, float gameTime) {
        final VectorPool pool = sSystemRegistry.vectorPool;
        if (pool != null) {
            Vector2 impulse = pool.allocate();
            getFacingDirection(parentObject, impulse);
            if (impulse.x != 0.0f) {
                parentObject.facingDirection.x = impulse.x > 0.0f ? 1.0f : - 1.0f;
            }
            if (impulse.y != 0.0f) {
                parentObject.facingDirection.y = impulse.y > 0.0f ? 1.0f : - 1.0f;
            }
            pool.release(impulse);
        }

        final float velocityX = parentObject.getVelocity().x;
        final float velocityY = parentObject.getVelocity().y;

// !!!! TODO: add all directions !!!!
// => handle different animations for each direction ... (at least for up and down)
//        if (touchingGround) {
/*
        if (Utils.close(velocityX, 0.0f, mMoveInitSpeed)) {
            mSprite.playAnimation(PlayerAnimations.IDLE.ordinal());
        } else if (Math.abs(velocityX) > mMoveFastSpeed) {
            mSprite.playAnimation(PlayerAnimations.MOVE_FAST.ordinal());
        } else {
            mSprite.playAnimation(PlayerAnimations.MOVE.ordinal());
        }
*/
        final boolean idle = Utils.close(velocityX, 0.0f, mMoveInitSpeed) && Utils.close(velocityY, 0.0f, mMoveInitSpeed);
        final boolean moveFast = (Math.abs(velocityX) > mMoveFastSpeed) || (Math.abs(velocityY) > mMoveFastSpeed);
        if (idle) {
            mSprite.playAnimation(PlayerAnimations.IDLE.ordinal());
        } else if (moveFast) {
            mSprite.playAnimation(PlayerAnimations.MOVE_FAST.ordinal());
        } else {
            mSprite.playAnimation(PlayerAnimations.MOVE.ordinal());
        }
    }

    @Override
    protected void handleAttack(GameObject parentObject, float gameTime) {
        if (mainAttack(gameTime, parentObject)) {
            mSprite.playAnimation(PlayerAnimations.ATTACK.ordinal());
        } else {
            mSprite.playAnimation(PlayerAnimations.ATTACK_ALTERNATE.ordinal());
        }
    }

    @Override
    protected void handleHitReact(GameObject parentObject, float gameTime) {
        mSprite.playAnimation(PlayerAnimations.HIT_REACT.ordinal());

// !!!! TODO: should call a method to make it easily modifiable for derived classes !!!!
        final float velocityX = parentObject.getVelocity().x;
        if (velocityX > 0.0f) {
            parentObject.facingDirection.x = -1.0f;  
        }
        else if (velocityX < 0.0f) {
            parentObject.facingDirection.x = 1.0f;
        }

        final float velocityY = parentObject.getVelocity().y;
        if (velocityY > 0.0f) {
            parentObject.facingDirection.y = -1.0f;  
        } else if (velocityY < 0.0f) {
            parentObject.facingDirection.y = 1.0f;
        }
    }

    @Override
    protected void handleFrozen(GameObject parentObject, float gameTime) {
        mSprite.playAnimation(PlayerAnimations.FROZEN.ordinal());
    }


    @Override
    protected void death(GameObject parentObject) {
        mSprite.playAnimation(PlayerAnimations.DEATH.ordinal());
        mHideDeath = false;
    }

    @Override
    protected void deathByHit(GameObject parentObject) {
        mSprite.playAnimation(PlayerAnimations.DEATH_BY_HIT.ordinal());
        mHideDeath = false;
    }

    @Override
    protected void deathByHotSpot(GameObject parentObject) {
        mSprite.playAnimation(PlayerAnimations.DEATH_BY_HOTSPOT.ordinal());
        mHideDeath = false;
    }

}
