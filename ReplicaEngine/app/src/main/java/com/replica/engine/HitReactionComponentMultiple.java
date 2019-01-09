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

/** 
 * A general-purpose component that responds to dynamic collision notifications.  This component
 * may be configured to produce common responses to hit (taking damage, being knocked back, etc), or
 * it can be derived for entirely different responses.  This component must exist on an object for
 * that object to respond to dynamic collisions.
 */
public class HitReactionComponentMultiple extends HitReactionComponent {
    private byte[] mOnDealHitGameEvents;
    private byte[] mOnDealHitGameEventIndexDatas;

    private SoundSystem.Sound[] mOnDealHitSounds;

// !!!! TODO: change to "int" if have more than 128 different object types !!!!
    private byte[] mOnDealHitSpawnObjectTypeOrdinals;
    private boolean[] mOnDealHitSpawnObjectAlignToOtherXs;
    private boolean[] mOnDealHitSpawnObjectAlignToOtherYs;
    private boolean[] mOnDealHitSpawnObjectKeepMovements;
    private float[] mOnDealHitSpawnObjectAdditionalMovementXs;
    private float[] mOnDealHitSpawnObjectAdditionalMovementYs;


    private byte[] mOnTakeHitGameEvents;
    private byte[] mOnTakeHitGameEventIndexDatas;

    private SoundSystem.Sound[] mOnTakeHitSounds;

// !!!! TODO: change to "int" if have more than 128 different object types !!!!
    private byte[] mOnTakeHitSpawnObjectTypeOrdinals;
    private boolean[] mOnTakeHitSpawnObjectAlignToOtherXs;
    private boolean[] mOnTakeHitSpawnObjectAlignToOtherYs;
    private boolean[] mOnTakeHitSpawnObjectKeepMovements;
    private float[] mOnTakeHitSpawnObjectAdditionalMovementXs;
    private float[] mOnTakeHitSpawnObjectAdditionalMovementYs;

    public HitReactionComponentMultiple() {
        super();
    }

    @Override
    public void reset() {
        super.reset();

        final byte nbTypes = HitType.NBTYPES;

        mOnDealHitGameEvents = new byte[nbTypes];
        mOnDealHitGameEventIndexDatas = new byte[nbTypes];

        mOnDealHitSounds = new SoundSystem.Sound[nbTypes];

        mOnDealHitSpawnObjectTypeOrdinals = new byte[nbTypes];
        mOnDealHitSpawnObjectAlignToOtherXs = new boolean[nbTypes];
        mOnDealHitSpawnObjectAlignToOtherYs = new boolean[nbTypes];
        mOnDealHitSpawnObjectKeepMovements = new boolean[nbTypes];
        mOnDealHitSpawnObjectAdditionalMovementXs = new float[nbTypes];
        mOnDealHitSpawnObjectAdditionalMovementYs = new float[nbTypes];

        for (int i = 0; i < nbTypes; ++i) {
        	mOnDealHitGameEvents[i] = -1;
        	mOnDealHitGameEventIndexDatas[i] = 0;

        	mOnDealHitSounds[i] = null;

        	mOnDealHitSpawnObjectTypeOrdinals[i] = 0;
        	mOnDealHitSpawnObjectAlignToOtherXs[i] = false;
        	mOnDealHitSpawnObjectAlignToOtherYs[i] = false;
            mOnDealHitSpawnObjectKeepMovements[i] = false;
        	mOnDealHitSpawnObjectAdditionalMovementXs[i] = 0.0f;
        	mOnDealHitSpawnObjectAdditionalMovementYs[i] = 0.0f;
        }

        mOnTakeHitGameEvents = new byte[nbTypes];
        mOnTakeHitGameEventIndexDatas = new byte[nbTypes];

        mOnTakeHitSounds = new SoundSystem.Sound[nbTypes];

        mOnTakeHitSpawnObjectTypeOrdinals = new byte[nbTypes];
        mOnTakeHitSpawnObjectAlignToOtherXs = new boolean[nbTypes];
        mOnTakeHitSpawnObjectAlignToOtherYs = new boolean[nbTypes];
        mOnTakeHitSpawnObjectKeepMovements = new boolean[nbTypes];
        mOnTakeHitSpawnObjectAdditionalMovementXs = new float[nbTypes];
        mOnTakeHitSpawnObjectAdditionalMovementYs = new float[nbTypes];

        for (int i = 0; i < nbTypes; ++i) {
        	mOnTakeHitGameEvents[i] = -1;
        	mOnTakeHitGameEventIndexDatas[i] = 0;

        	mOnTakeHitSounds[i] = null;

        	mOnTakeHitSpawnObjectTypeOrdinals[i] = 0;
        	mOnTakeHitSpawnObjectAlignToOtherXs[i] = false;
        	mOnTakeHitSpawnObjectAlignToOtherYs[i] = false;
            mOnTakeHitSpawnObjectKeepMovements[i] = false;
        	mOnTakeHitSpawnObjectAdditionalMovementXs[i] = 0.0f;
        	mOnTakeHitSpawnObjectAdditionalMovementYs[i] = 0.0f;
        }

    }

    @Override
    public void setGameEventOnDealHit(int hitType, int gameFlowEventType, int indexData) {
        if (hitType != HitType.INVALID) {
            if (gameFlowEventType != GameFlowEvent.EVENT_INVALID) {
                mOnDealHitGameEvents[hitType] = (byte)gameFlowEventType;
                mOnDealHitGameEventIndexDatas[hitType] = (byte)indexData;
            } else {
                mOnDealHitGameEvents[hitType] = GameFlowEvent.EVENT_INVALID;
                mOnDealHitGameEventIndexDatas[hitType] = 0;

                // The game event has been cleared, so reset the timer blocking a
                // subsequent event.
                mLastGameEventTime = -1.0f;
            }
        }
    }

    @Override
    protected int getOnDealHitGameEvent(int hitType) {
        return mOnDealHitGameEvents[hitType];
    }

    @Override
    protected int getOnDealHitGameEventIndexData(int hitType) {
        return mOnDealHitGameEventIndexDatas[hitType];
    }


    @Override
    public void setSoundOnDealHit(int hitType, SoundSystem.Sound sound) {
        if (hitType != HitType.INVALID) {
            mOnDealHitSounds[hitType] = sound;
        }
    }

    @Override
    protected SoundSystem.Sound getOnDealHitSound(int hitType) {
        return mOnDealHitSounds[hitType];
    }


    @Override
    public void setSpawnObjectOnDealHit(int hitType, int objectTypeOrdinal,
            boolean alignToOtherX, boolean alignToOtherY) {
        if (hitType != HitType.INVALID) {
            mOnDealHitSpawnObjectTypeOrdinals[hitType] = (byte)objectTypeOrdinal;
            mOnDealHitSpawnObjectAlignToOtherXs[hitType] = alignToOtherX;
            mOnDealHitSpawnObjectAlignToOtherYs[hitType] = alignToOtherY;
        }
    }

    @Override
    public void setSpawnObjectKeepMovementOnDealHit(int hitType, boolean keepMovement) {
        if (hitType != HitType.INVALID) {
            mOnDealHitSpawnObjectKeepMovements[hitType] = keepMovement;
        }
    }

    @Override
    public void setSpawnObjectAdditionalMovementOnDealHit(int hitType, float movementX, float movementY) {
        if (hitType != HitType.INVALID) {
            mOnDealHitSpawnObjectAdditionalMovementXs[hitType] = movementX;
            mOnDealHitSpawnObjectAdditionalMovementYs[hitType] = movementY;
        }
    }

    @Override
    protected int getOnDealHitSpawnObjectOrdinal(int hitType) {
        return mOnDealHitSpawnObjectTypeOrdinals[hitType];
    }

    @Override
    protected boolean getOnDealHitSpawnObjectAlignToOtherX(int hitType) {
        return mOnDealHitSpawnObjectAlignToOtherXs[hitType];
    }

    @Override
    protected boolean getOnDealHitSpawnObjectAlignToOtherY(int hitType) {
        return mOnDealHitSpawnObjectAlignToOtherYs[hitType];
    }

    @Override
    protected boolean getOnDealHitSpawnObjectKeepMovement(int hitType) {
        return mOnDealHitSpawnObjectKeepMovements[hitType];
    }

    @Override
    protected float getOnDealHitSpawnObjectAdditionalMovementX(int hitType) {
        return mOnDealHitSpawnObjectAdditionalMovementXs[hitType];
    }

    @Override
    protected float getOnDealHitSpawnObjectAdditionalMovementY(int hitType) {
        return mOnDealHitSpawnObjectAdditionalMovementYs[hitType];
    }


    @Override
    public void setGameEventOnTakeHit(int hitType, int gameFlowEventType, int indexData) {
        if (hitType != HitType.INVALID) {
            if (gameFlowEventType != GameFlowEvent.EVENT_INVALID) {
                mOnTakeHitGameEvents[hitType] = (byte)gameFlowEventType;
                mOnTakeHitGameEventIndexDatas[hitType] = (byte)indexData;
            } else {
                mOnTakeHitGameEvents[hitType] = GameFlowEvent.EVENT_INVALID;
                mOnTakeHitGameEventIndexDatas[hitType] = 0;

                // The game event has been cleared, so reset the timer blocking a
                // subsequent event.
                mLastGameEventTime = -1.0f;
            }
        }
    }

    @Override
    protected int getOnTakeHitGameEvent(int hitType) {
        return mOnTakeHitGameEvents[hitType];
    }

    @Override
    protected int getOnTakeHitGameEventIndexData(int hitType) {
        return mOnTakeHitGameEventIndexDatas[hitType];
    }


    @Override
    public void setSoundOnTakeHit(int hitType, SoundSystem.Sound sound) {
        if (hitType != HitType.INVALID) {
            mOnTakeHitSounds[hitType] = sound;
        }
    }

    @Override
    protected SoundSystem.Sound getOnTakeHitSound(int hitType) {
        return mOnTakeHitSounds[hitType];
    }


    @Override
    public void setSpawnObjectOnTakeHit(int hitType, int objectTypeOrdinal,
            boolean alignToOtherX, boolean alignToOtherY) {
        if (hitType != HitType.INVALID) {
            mOnTakeHitSpawnObjectTypeOrdinals[hitType] = (byte)objectTypeOrdinal;
            mOnTakeHitSpawnObjectAlignToOtherXs[hitType] = alignToOtherX;
            mOnTakeHitSpawnObjectAlignToOtherYs[hitType] = alignToOtherY;
        }
    }

    @Override
    public void setSpawnObjectKeepMovementOnTakeHit(int hitType, boolean keepMovement) {
        if (hitType != HitType.INVALID) {
            mOnTakeHitSpawnObjectKeepMovements[hitType] = keepMovement;
        }
    }

    @Override
    public void setSpawnObjectAdditionalMovementOnTakeHit(int hitType, float movementX, float movementY) {
        if (hitType != HitType.INVALID) {
            mOnTakeHitSpawnObjectAdditionalMovementXs[hitType] = movementX;
            mOnTakeHitSpawnObjectAdditionalMovementYs[hitType] = movementY;
        }
    }

    @Override
    protected int getOnTakeHitSpawnObjectOrdinal(int hitType) {
        return mOnTakeHitSpawnObjectTypeOrdinals[hitType];
    }

    @Override
    protected boolean getOnTakeHitSpawnObjectAlignToOtherX(int hitType) {
        return mOnTakeHitSpawnObjectAlignToOtherXs[hitType];
    }

    @Override
    protected boolean getOnTakeHitSpawnObjectAlignToOtherY(int hitType) {
        return mOnTakeHitSpawnObjectAlignToOtherYs[hitType];
    }

    @Override
    protected boolean getOnTakeHitSpawnObjectKeepMovement(int hitType) {
        return mOnTakeHitSpawnObjectKeepMovements[hitType];
    }

    @Override
    protected float getOnTakeHitSpawnObjectAdditionalMovementX(int hitType) {
        return mOnTakeHitSpawnObjectAdditionalMovementXs[hitType];
    }

    @Override
    protected float getOnTakeHitSpawnObjectAdditionalMovementY(int hitType) {
        return mOnTakeHitSpawnObjectAdditionalMovementYs[hitType];
    }
}
