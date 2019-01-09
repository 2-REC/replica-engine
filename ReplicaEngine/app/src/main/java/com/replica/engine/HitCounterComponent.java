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
 * Component to count the number of hits of a particular type & then to do something when reach a target.
 * ( both dealt & taken hits )
 *  => Must be derived & used with a "HitReaction" component ( HitReactionComponent.setHitCounter() ) !
*/
public abstract class HitCounterComponent extends GameComponent {

    private int mDealHitType;
    private int mDealHitsCount;
    private int mDealHitsTarget;

    private int mTakeHitType;
    private int mTakeHitsCount;
    private int mTakeHitsTarget;


    public HitCounterComponent() {
        super();
        reset();
        setPhase(ComponentPhases.PRE_DRAW.ordinal());
    }

    @Override
    public void reset() {
        mDealHitType = HitType.INVALID;
        mDealHitsCount = 0;
        mDealHitsTarget = 0; // 0 means never

        mTakeHitType = HitType.INVALID;
        mTakeHitsCount = 0;
        mTakeHitsTarget = 0; // 0 means never
    }

    public void addDealHit(GameObject parent, GameObject victim,
            int hitType, TimeSystem time) {
//        if (hitType == mDealHitType) {
        if (mDealHitType == HitType.INVALID || hitType == mDealHitType) {
            ++mDealHitsCount;
            if (mDealHitsCount == mDealHitsTarget) {
                mDealHitsCount = 0;
                dealHitProcess(parent, victim, time);
            }
        }
    }

    public void addTakeHit(GameObject parent, GameObject attacker,
            int hitType, TimeSystem time) {
//        if (hitType == mTakeHitType) {
        if (mTakeHitType == HitType.INVALID || hitType == mTakeHitType) {
            ++mTakeHitsCount;
            if (mTakeHitsCount == mTakeHitsTarget) {
                mTakeHitsCount = 0;
                takeHitProcess(parent, attacker, time);
          }
      }
    }

    public void setDealHitType(int type) {
        mDealHitType = type;
    }

    public void setTakeHitType(int type) {
        mTakeHitType = type;
    }

    public void setDealHitsTarget(int target) {
        mDealHitsTarget = target;
        mDealHitsCount = 0;
    }

    public void setTakeHitsTarget(int target) {
        mTakeHitsTarget = target;
        mTakeHitsCount = 0;
    }


    protected abstract void dealHitProcess(GameObject parent, GameObject victim, TimeSystem time);
    protected abstract void takeHitProcess(GameObject parent, GameObject attacker, TimeSystem time);

}
