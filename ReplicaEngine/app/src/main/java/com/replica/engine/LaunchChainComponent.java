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
 * Modified "LaunchProjectileComponent" enabling the object to destroy itself
 *  and all it's spawned children when one of them dies.
 */
public class LaunchChainComponent extends LaunchProjectileComponent {

    private FixedSizeArray<GameObject> mSpawnedObjects;
    private GameObject mParentObject;


    public LaunchChainComponent() {
        super();
        reset();
    }

    @Override
    public void reset() {
        super.reset();

        mSpawnedObjects = null;
        mParentObject = null;
    }

    @Override
    public void enableProjectileTracking(int max) {
        assert (max > 0);
        super.enableProjectileTracking(max);

        mSpawnedObjects = new FixedSizeArray<GameObject>(mMaxTrackedProjectiles);
    }

    @Override
    public final void trackedProjectileDestroyed(GameObject projectile) {
    	mMaxTrackedProjectiles = 0; // stop firing

        mSpawnedObjects.remove(projectile, false);
        --mTrackedProjectileCount;

        if (mTrackedProjectileCount > 0) {
            mSpawnedObjects.get(0).life = 0;
        } else {
            if (mParentObject != null) {
                mParentObject.life = 0;
            }
        }

    }

    @Override
    protected void specialHandle(GameObject object) {
        if (mSpawnedObjects == null) {
            mSpawnedObjects = new FixedSizeArray<GameObject>(mMaxTrackedProjectiles);
        }
        mSpawnedObjects.add(object);
    }

    public final void setParent(GameObject parent) {
        mParentObject = parent;
    }

}
