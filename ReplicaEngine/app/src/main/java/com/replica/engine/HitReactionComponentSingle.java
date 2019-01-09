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

public class HitReactionComponentSingle extends HitReactionComponent {
    private int mOnDealHitGameEventType;
    private byte mOnDealHitGameEvent;
    private byte mOnDealHitGameEventIndexData;

    private int mOnDealHitSoundType;
    private SoundSystem.Sound mOnDealHitSound;

    private int mOnDealHitSpawnType;
// !!!! TODO: change to "int" if have more than 128 different object types !!!!
    private byte mOnDealHitSpawnObjectTypeOrdinal;
    private boolean mOnDealHitSpawnObjectAlignToOtherX;
    private boolean mOnDealHitSpawnObjectAlignToOtherY;
    private boolean mOnDealHitSpawnObjectKeepMovement;
    private float mOnDealHitSpawnObjectAdditionalMovementX;
    private float mOnDealHitSpawnObjectAdditionalMovementY;


    private int mOnTakeHitGameEventType;
    private byte mOnTakeHitGameEvent;
    private byte mOnTakeHitGameEventIndexData;

    private int mOnTakeHitSoundType;
    private SoundSystem.Sound mOnTakeHitSound;

    private int mOnTakeHitSpawnType;
// !!!! TODO: change to "int" if have more than 128 different object types !!!!
    private byte mOnTakeHitSpawnObjectTypeOrdinal;
    private boolean mOnTakeHitSpawnObjectAlignToOtherX;
    private boolean mOnTakeHitSpawnObjectAlignToOtherY;
    private boolean mOnTakeHitSpawnObjectKeepMovement;
    private float mOnTakeHitSpawnObjectAdditionalMovementX;
    private float mOnTakeHitSpawnObjectAdditionalMovementY;


    public HitReactionComponentSingle() {
        super();
    }

    @Override
    public void reset() {
        super.reset();

        mOnDealHitGameEventType = HitType.INVALID;
        mOnDealHitGameEvent = GameFlowEvent.EVENT_INVALID;
        mOnDealHitGameEventIndexData = 0;

        mOnDealHitSoundType = HitType.INVALID;
        mOnDealHitSound = null;

        mOnDealHitSpawnType = HitType.INVALID;
        mOnDealHitSpawnObjectTypeOrdinal = 0; // -1 ?
        mOnDealHitSpawnObjectAlignToOtherX = false;
        mOnDealHitSpawnObjectAlignToOtherY = false;
        mOnDealHitSpawnObjectKeepMovement = false;
        mOnDealHitSpawnObjectAdditionalMovementX = 0.0f;
        mOnDealHitSpawnObjectAdditionalMovementY = 0.0f;


        mOnTakeHitGameEventType = HitType.INVALID;
        mOnTakeHitGameEvent = GameFlowEvent.EVENT_INVALID;
        mOnTakeHitGameEventIndexData = 0;

        mOnTakeHitSoundType = HitType.INVALID;
        mOnTakeHitSound = null;

        mOnTakeHitSpawnType = HitType.INVALID;
        mOnTakeHitSpawnObjectTypeOrdinal = 0; // -1 ?
        mOnTakeHitSpawnObjectAlignToOtherX = false;
        mOnTakeHitSpawnObjectAlignToOtherY = false;
        mOnTakeHitSpawnObjectKeepMovement = false;
        mOnTakeHitSpawnObjectAdditionalMovementX = 0.0f;
        mOnTakeHitSpawnObjectAdditionalMovementY = 0.0f;
    }


    @Override
    public void setGameEventOnDealHit(int hitType, int gameFlowEventType, int indexData) {
        if (hitType != HitType.INVALID && gameFlowEventType != GameFlowEvent.EVENT_INVALID) {
            mOnDealHitGameEventType = hitType;
            mOnDealHitGameEvent = (byte)gameFlowEventType;
            mOnDealHitGameEventIndexData = (byte)indexData;
        } else {
            mOnDealHitGameEventType = HitType.INVALID;
            mOnDealHitGameEvent = GameFlowEvent.EVENT_INVALID;
            mOnDealHitGameEventIndexData = 0;

            // The game event has been cleared, so reset the timer blocking a
            // subsequent event.
            mLastGameEventTime = -1.0f;
        }
    }

    @Override
    protected int getOnDealHitGameEvent(int hitType) {
        if (mOnDealHitGameEventType == hitType)
            return mOnDealHitGameEvent;
        return GameFlowEvent.EVENT_INVALID;
    }

    @Override
    protected int getOnDealHitGameEventIndexData(int hitType) {
        return mOnDealHitGameEventIndexData;
    }


    @Override
    public void setSoundOnDealHit(int hitType, SoundSystem.Sound sound) {
        mOnDealHitSoundType = hitType;
        mOnDealHitSound = sound;
    }

    @Override
    protected SoundSystem.Sound getOnDealHitSound(int hitType) {
        if (mOnDealHitSoundType == hitType)
            return mOnDealHitSound;
        return null;
    }


    @Override
    public void setSpawnObjectOnDealHit(int hitType, int objectTypeOrdinal,
            boolean alignToOtherX, boolean alignToOtherY) {
        mOnDealHitSpawnType = hitType;
        mOnDealHitSpawnObjectTypeOrdinal = (byte)objectTypeOrdinal;
        mOnDealHitSpawnObjectAlignToOtherX = alignToOtherX;
        mOnDealHitSpawnObjectAlignToOtherY = alignToOtherY;
    }

    @Override
    public void setSpawnObjectKeepMovementOnDealHit(int hitType, boolean keepMovement) {
        mOnDealHitSpawnObjectKeepMovement = keepMovement;
    }

    @Override
    public void setSpawnObjectAdditionalMovementOnDealHit(int hitType, float movementX, float movementY) {
        mOnDealHitSpawnObjectAdditionalMovementX = movementX;
        mOnDealHitSpawnObjectAdditionalMovementY = movementY;
    }

    @Override
    protected int getOnDealHitSpawnObjectOrdinal(int hitType) {
        if (mOnDealHitSpawnType == hitType)
            return mOnDealHitSpawnObjectTypeOrdinal;
        return 0;
    }

    @Override
    protected boolean getOnDealHitSpawnObjectAlignToOtherX(int hitType) {
        return mOnDealHitSpawnObjectAlignToOtherX;
    }

    @Override
    protected boolean getOnDealHitSpawnObjectAlignToOtherY(int hitType) {
        return mOnDealHitSpawnObjectAlignToOtherY;
    }

    @Override
    protected boolean getOnDealHitSpawnObjectKeepMovement(int hitType) {
        return mOnDealHitSpawnObjectKeepMovement;
    }

    @Override
    protected float getOnDealHitSpawnObjectAdditionalMovementX(int hitType) {
        return mOnDealHitSpawnObjectAdditionalMovementX;
    }

    @Override
    protected float getOnDealHitSpawnObjectAdditionalMovementY(int hitType) {
        return mOnDealHitSpawnObjectAdditionalMovementY;
    }


    @Override
    public void setGameEventOnTakeHit(int hitType, int gameFlowEventType, int indexData) {
        if (hitType != HitType.INVALID && gameFlowEventType != GameFlowEvent.EVENT_INVALID) {
            mOnTakeHitGameEventType = hitType;
            mOnTakeHitGameEvent = (byte)gameFlowEventType;
            mOnTakeHitGameEventIndexData = (byte)indexData;
        } else {
            mOnTakeHitGameEventType = HitType.INVALID;
            mOnTakeHitGameEvent = -1;
            mOnTakeHitGameEventIndexData = 0;

            // The game event has been cleared, so reset the timer blocking a
            // subsequent event.
            mLastGameEventTime = -1.0f;
        }
    }

    @Override
    protected int getOnTakeHitGameEvent(int hitType) {
        if (mOnTakeHitGameEventType == hitType)
            return mOnTakeHitGameEvent;
        return GameFlowEvent.EVENT_INVALID;
    }

    @Override
    protected int getOnTakeHitGameEventIndexData(int hitType) {
        return mOnTakeHitGameEventIndexData;
    }


    @Override
    public void setSoundOnTakeHit(int hitType, SoundSystem.Sound sound) {
        mOnTakeHitSoundType = hitType;
        mOnTakeHitSound = sound;
    }

    @Override
    protected SoundSystem.Sound getOnTakeHitSound(int hitType) {
        if (mOnTakeHitSoundType == hitType)
            return mOnTakeHitSound;
        return null;
    }


    @Override
    public void setSpawnObjectOnTakeHit(int hitType, int objectTypeOrdinal,
            boolean alignToOtherX, boolean alignToOtherY) {
        mOnTakeHitSpawnType = hitType;
        mOnTakeHitSpawnObjectTypeOrdinal = (byte)objectTypeOrdinal;
        mOnTakeHitSpawnObjectAlignToOtherX = alignToOtherX;
        mOnTakeHitSpawnObjectAlignToOtherY = alignToOtherY;
    }

    @Override
    public void setSpawnObjectKeepMovementOnTakeHit(int hitType, boolean keepMovement) {
        mOnTakeHitSpawnObjectKeepMovement = keepMovement;
    }

    @Override
    public void setSpawnObjectAdditionalMovementOnTakeHit(int hitType, float movementX, float movementY) {
        mOnTakeHitSpawnObjectAdditionalMovementX = movementX;
        mOnTakeHitSpawnObjectAdditionalMovementY = movementY;
    }

    @Override
    protected int getOnTakeHitSpawnObjectOrdinal(int hitType) {
        if (mOnTakeHitSpawnType == hitType)
            return mOnTakeHitSpawnObjectTypeOrdinal;
        return 0;
    }

    @Override
    protected boolean getOnTakeHitSpawnObjectAlignToOtherX(int hitType) {
        return mOnTakeHitSpawnObjectAlignToOtherX;
    }

    @Override
    protected boolean getOnTakeHitSpawnObjectAlignToOtherY(int hitType) {
        return mOnTakeHitSpawnObjectAlignToOtherY;
    }

    @Override
    protected boolean getOnTakeHitSpawnObjectKeepMovement(int hitType) {
        return mOnTakeHitSpawnObjectKeepMovement;
    }

    @Override
    protected float getOnTakeHitSpawnObjectAdditionalMovementX(int hitType) {
        return mOnTakeHitSpawnObjectAdditionalMovementX;
    }

    @Override
    protected float getOnTakeHitSpawnObjectAdditionalMovementY(int hitType) {
        return mOnTakeHitSpawnObjectAdditionalMovementY;
    }
}
