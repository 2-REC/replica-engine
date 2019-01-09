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

import java.util.Comparator;

/** A class for generating game objects at runtime.
 * This should really be replaced with something that is data-driven, but it is hard to do data
 * parsing quickly at runtime.  For the moment this class is full of large functions that just
 * patch pointers between objects, but in the future those functions should either be 
 * a) generated from data at compile time, or b) described by data at runtime.
 */
public abstract class GameObjectFactory extends BaseObject {
    private final static ComponentPoolComparator sComponentPoolComparator = new ComponentPoolComparator();
    protected FixedSizeArray<FixedSizeArray<BaseObject>> mStaticData;
    private FixedSizeArray<GameComponentPool> mComponentPools;
    private GameComponentPool mPoolSearchDummy;
    protected GameObjectPool mGameObjectPool;

    protected float mTightActivationRadius;
    protected float mNormalActivationRadius;
    protected float mWideActivationRadius;
    protected float mAlwaysActive;


    protected class ComponentClass {
        public Class<?> type;
        public int poolSize;
        public ComponentClass(Class<?> classType, int size) {
            type = classType;
            poolSize = size;
        }
    }

    public GameObjectFactory(int maxNbObjects, int nbObjectTypes) {
        super();

        mGameObjectPool = new GameObjectPool(maxNbObjects);

        final int objectTypesCount = nbObjectTypes;
        mStaticData = new FixedSizeArray<FixedSizeArray<BaseObject>>(objectTypesCount);

        for (int x = 0; x < objectTypesCount; x++) {
            mStaticData.add(null);
        }

        final ContextParameters context = sSystemRegistry.contextParameters;
        final float halfHeight2 = (context.gameHeight * 0.5f) * (context.gameHeight * 0.5f);
        final float halfWidth2 = (context.gameWidth * 0.5f) * (context.gameWidth * 0.5f);
        final float screenSizeRadius = (float)Math.sqrt(halfHeight2 + halfWidth2);
        mTightActivationRadius = screenSizeRadius * 0.75f; //or + 128.0f;
        mNormalActivationRadius = screenSizeRadius * 1.25f;
        mWideActivationRadius = screenSizeRadius * 2.0f;
        mAlwaysActive = -1.0f;

    }

    protected void setComponentClasses(ComponentClass[] componentTypes) {
        mComponentPools = new FixedSizeArray<GameComponentPool>(componentTypes.length, sComponentPoolComparator);
        for (int x = 0; x < componentTypes.length; x++) {
            final ComponentClass component = componentTypes[x];
            mComponentPools.add(new GameComponentPool(component.type, component.poolSize));
        }
        mComponentPools.sort(true);

        mPoolSearchDummy = new GameComponentPool(Object.class, 1);

    }

    @Override
    public void reset() {

    }

    protected GameComponentPool getComponentPool(Class<?> componentType) {
        GameComponentPool pool = null;
        mPoolSearchDummy.objectClass = componentType;
        final int index = mComponentPools.find(mPoolSearchDummy, false);
        if (index != -1) {
            pool = mComponentPools.get(index);
        }
        return pool;
    }

    protected GameComponent allocateComponent(Class<?> componentType) {
        GameComponentPool pool = getComponentPool(componentType);
        assert pool != null;
        GameComponent component = null;
        if (pool != null) {
            component = pool.allocate();
        }
        return component;
    }

    protected void releaseComponent(GameComponent component) {
        GameComponentPool pool = getComponentPool(component.getClass());
        assert pool != null;
        if (pool != null) {
            component.reset();
            component.shared = false;
            pool.release(component);
        }
    }

    protected boolean componentAvailable(Class<?> componentType, int count) {
        boolean canAllocate = false;
        GameComponentPool pool = getComponentPool(componentType);
        assert pool != null;
        if (pool != null) {
            canAllocate = pool.getAllocatedCount() + count < pool.getSize();
        }
        return canAllocate;
    }

    public void destroy(GameObject object) {
        object.commitUpdates();
        final int componentCount = object.getCount();
        for (int x = 0; x < componentCount; x++) {
            GameComponent component = (GameComponent)object.get(x);
            if (!component.shared) {
                releaseComponent(component);
            }
        }
        object.removeAll();
        object.commitUpdates();
        mGameObjectPool.release(object);
    }


    private FixedSizeArray<BaseObject> getStaticData(int index) {
        return mStaticData.get(index);
    }

    protected void setStaticData(int index, FixedSizeArray<BaseObject> data) {
        assert mStaticData.get(index) == null;

        final int staticDataCount = data.getCount();

        for (int x = 0; x < staticDataCount; x++) {
            BaseObject entry = data.get(x);
            if (entry instanceof GameComponent) {
                ((GameComponent) entry).shared = true;
            }
        }

        mStaticData.set(index, data);
    }

    protected void addStaticData(int index, GameObject object, SpriteComponent sprite) {
        FixedSizeArray<BaseObject> staticData = getStaticData(index);
        assert staticData != null;

        if (staticData != null) {
            final int staticDataCount = staticData.getCount();

            for (int x = 0; x < staticDataCount; x++) {
                BaseObject entry = staticData.get(x);
                if (entry instanceof GameComponent && object != null) {
                    object.add((GameComponent)entry);
                } else if (entry instanceof SpriteAnimation && sprite != null) {
                    sprite.addAnimation((SpriteAnimation)entry);
                }
            }
        }
    }

    public void clearStaticData() {
        final int typeCount = mStaticData.getCount();
        for (int x = 0; x < typeCount; x++) {
            FixedSizeArray<BaseObject> staticData = mStaticData.get(x);
            if (staticData != null) {
                final int count = staticData.getCount();
                for (int y = 0; y < count; y++) {
                    BaseObject entry = staticData.get(y);
                    if (entry != null) {
                        if (entry instanceof GameComponent) {
                            releaseComponent((GameComponent)entry);
                        }
                    }
                }
                staticData.clear();
                mStaticData.set(x, null);
            }
        }
    }

    public void sanityCheckPools() {
        final int outstandingObjects = mGameObjectPool.getAllocatedCount();
        if (outstandingObjects != 0) {
            DebugLog.d("Sanity Check", "Outstanding game object allocations! ("
                    + outstandingObjects + ")");
            assert false;
        }

        final int componentPoolCount = mComponentPools.getCount();
        for (int x = 0; x < componentPoolCount; x++) {
            final int outstandingComponents = mComponentPools.get(x).getAllocatedCount();

            if (outstandingComponents != 0) {
                DebugLog.d("Sanity Check", "Outstanding "
                        + mComponentPools.get(x).objectClass.getSimpleName()
                        + " allocations! (" + outstandingComponents + ")");
                //assert false;
            }
        }
    }

    public void spawnFromWorld(TiledWorld world, int tileWidth, int tileHeight) {
        // Walk the world and spawn objects based on tile indexes
        final float worldHeight = world.getHeight() * tileHeight;
        GameObjectManager manager = sSystemRegistry.gameObjectManager;
        if (manager != null) {
            for (int y = 0; y < world.getHeight(); y++) {
                for (int x = 0; x < world.getWidth(); x++) {
                    int index = world.getTile(x, y);
                    if (index != -1) {
                        final float worldX = x * tileWidth;
                        final float worldY = worldHeight - ((y + 1) * tileHeight);
                        GameObject object = spawnFromIndex(index, worldX, worldY, false);
                        if (object != null) {
                            if (object.height < tileHeight) {
                                // make sure small objects are vertically centered in their
                                // tile.
                                object.getPosition().y += (tileHeight - object.height) / 2.0f;
                            }
                            if (object.width < tileWidth) {
                                object.getPosition().x += (tileWidth - object.width) / 2.0f;
                            } else if (object.width > tileWidth) {
                                object.getPosition().x -= (object.width - tileWidth) / 2.0f;
                            }
                            manager.add(object);
                            if (isPlayer(index)) {
                                manager.setPlayer(object);
                            }
                        }
                    }
                }
            }
        }
    }

    /** Comparator for game objects objects. */
    private final static class ComponentPoolComparator implements Comparator<GameComponentPool> {
        public int compare(final GameComponentPool object1, final GameComponentPool object2) {
            int result = 0;
            if (object1 == null && object2 != null) {
                result = 1;
            } else if (object1 != null && object2 == null) {
                result = -1;
            } else if (object1 != null && object2 != null) {
                result = object1.objectClass.hashCode() - object2.objectClass.hashCode();
            }
            return result;
        }
    }

    public class GameObjectPool extends TObjectPool<GameObject> {

        public GameObjectPool() {
            super();
        }

        public GameObjectPool(int size) {
            super(size);
        }

        @Override
        protected void fill() {
            for (int x = 0; x < getSize(); x++) {
                getAvailable().add(new GameObject());
            }
        }

        @Override
        public void release(Object entry) {
            ((GameObject)entry).reset();
            super.release(entry);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    // Override this if specific effects desired.
    //  (eg: textures appearing in every level => long-term)

    public void preloadEffects() {
    }

    ////////////////////////////////////////////////////////////////////////////////

    protected abstract boolean isPlayer(int index);

    public abstract GameObject spawnFromIndex(int index, float x, float y, boolean horzFlip);

    public abstract GameObject spawnFromOrdinal(int ordinal, float x, float y, boolean horzFlip);

    public abstract GameObject spawnControllerFromOrdinal(int ordinal, float x, float y, GameObject parent, int life, float time);

}
