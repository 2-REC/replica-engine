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


public class SelectDialogComponent extends GameComponent {
    private HitReactionComponent mHitReact;
    private Vector2 mLastPosition;
    
    public SelectDialogComponent() {
        super();
        setPhase(ComponentPhases.THINK.ordinal());
        mLastPosition = new Vector2();
    }
    
    @Override
    public void reset() {
    	mHitReact = null;
    	mLastPosition.zero();
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
    	HotSpotSystem hotSpot = sSystemRegistry.hotSpotSystem;
    	if (hotSpot != null && mHitReact != null) {
    		GameObject parentObject = (GameObject)parent;
    		final Vector2 currentPosition = parentObject.getPosition();
    		if (mLastPosition.distance2(parentObject.getPosition()) > 0.0f) {
    			mLastPosition.set(currentPosition);
    			
    			final int hitSpot = hotSpot.getHotSpot(parentObject.getCenteredPositionX(), currentPosition.y + 10);
                if (hitSpot >= HotSpotSystem.HotSpotType.NPC_SELECT_DIALOG) {
                    int event = GameFlowEvent.EVENT_SHOW_DIALOG_CHARACTER;
                    int index = hitSpot - HotSpotSystem.HotSpotType.NPC_SELECT_DIALOG;

// !!!! ???? TODO: "HitType.DIALOG" or "HitType.COLLECT" ? ???? !!!!
                    mHitReact.setGameEventOnTakeHit(HitType.DIALOG, event, index);
                }
    		}
    	}
    }

    public void setHitReact(HitReactionComponent hit) {
        mHitReact = hit;
    }
}
