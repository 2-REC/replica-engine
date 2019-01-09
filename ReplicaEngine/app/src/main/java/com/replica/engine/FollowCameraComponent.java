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
 * Manages the position of an object based on the camera's position.
 */
public class FollowCameraComponent extends GameComponent {
    private boolean mFollowX;
    private float mOffsetX;
    private boolean mFollowY;
    private float mOffsetY;


    public FollowCameraComponent() {
        super();

// !!!! TODO: when "attached", small delay when object moves fast !!!!
// => should change priority? (or add target's velocity to position?
        setPhase(ComponentPhases.PHYSICS.ordinal());
    }

    @Override
    public void reset() {
        mFollowX = false;
        mOffsetX = 0.0f;
        mFollowY = false;
        mOffsetY = 0.0f;
    }

    @Override
    public void update(float timeDelta, BaseObject parent) {
        final CameraSystem camera = sSystemRegistry.cameraSystem;
        if (camera != null) {
DebugLog.e("FOLLOW",  "update");
            final GameObject parentObject = (GameObject)parent;
            if (mFollowX) {
                parentObject.getPosition().x = camera.getFocusPositionX() + mOffsetX - (parentObject.width / 2.0f);
DebugLog.e("FOLLOW",  "x: " + parentObject.getPosition().x);
            }

            if (mFollowY) {
                parentObject.getPosition().y = camera.getFocusPositionY() + mOffsetY - (parentObject.height / 2.0f);
DebugLog.e("FOLLOW",  "y: " + parentObject.getPosition().y);
            }
        }
    }

    public final void setFollowX(final boolean follow) {
        mFollowX = follow;
    }

    public final void setFollowY(final boolean follow) {
        mFollowY = follow;
    }

    public final void setOffsetX(final float offset) {
        mOffsetX = offset;
    }

    public final void setOffsetY(final float offset) {
        mOffsetY = offset;
    }

}
