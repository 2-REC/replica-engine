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

// !!!! TODO: should check that "mInventory" is not null in each function !!!!

public class InventoryComponent extends GameComponent {
    private UpdateRecord mInventory;
    private boolean mInventoryChanged = false;
    
    public InventoryComponent() {
        super();
        setPhase(ComponentPhases.FRAME_END.ordinal());
    }
    
    @Override
    public void reset() {
        mInventoryChanged = true;
        mInventory.reset();
    }
    
    public void applyUpdate(UpdateRecord record) {
        mInventory.add(record);
        mInventoryChanged = true;
    }
    
    @Override
    public void update(float timeDelta, BaseObject parent) {
        if (mInventoryChanged) {
            mInventory.updateChange();
            mInventoryChanged = false;
        }
    }
    
    public void copy(InventoryComponent other) {
        mInventory.copy(other.getRecord());
        mInventoryChanged = true;
    }
    
    public void setInventory(UpdateRecord inventory) {
        mInventory = inventory;
    }
    
    public UpdateRecord getRecord() {
        return mInventory;
    }
    
    public void setChanged() {
        mInventoryChanged = true;
    }
    
    public abstract static class UpdateRecord extends GameComponent {
        public UpdateRecord() {
            super();
        }

        @Override
        public void update(float timeDelta, BaseObject parent) {
        }

        public abstract void copy(UpdateRecord other);

        public abstract void add(UpdateRecord other);

        public abstract void updateChange();
    }
}
