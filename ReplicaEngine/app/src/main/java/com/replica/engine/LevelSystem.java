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

import org.xmlpull.v1.XmlPullParser;

import android.content.res.XmlResourceParser;

/**
 * Manages information about the current level, including setup, deserialization, and tear-down.
 */
public class LevelSystem extends BaseObject {

    public int mWidthInTiles;
    public int mHeightInTiles;
    public int mTileWidth;
    public int mTileHeight;
    public GameObject mBackgroundObject;
    public ObjectManager mRoot;
    private TiledWorld mSpawnLocations;
    private GameFlowEvent mGameFlowEvent;
    private int mAttempts;
    private LevelTree.Level mCurrentLevel;
    
    public LevelSystem() {
        super();
        mGameFlowEvent = new GameFlowEvent();
        reset();
    }
    
    @Override
    public void reset() {
        if (mBackgroundObject != null && mRoot != null) {
            mBackgroundObject.removeAll();
            mBackgroundObject.commitUpdates();
            mRoot.remove(mBackgroundObject);
            mBackgroundObject = null;
            mRoot = null;
        }
        mSpawnLocations = null;
        mAttempts = 0;
        mCurrentLevel = null;
    }
    
    public float getLevelWidth() {
        return mWidthInTiles * mTileWidth;
    }
    
    public float getLevelHeight() {
        return mHeightInTiles * mTileHeight;
    }
    
    public void sendRestartEvent() {
        mGameFlowEvent.post(GameFlowEvent.EVENT_RESTART_LEVEL, 0,
                sSystemRegistry.contextParameters.context);
    }
    
    public void sendNextLevelEvent() {
        mGameFlowEvent.post(GameFlowEvent.EVENT_GO_TO_NEXT_LEVEL, 0,
                sSystemRegistry.contextParameters.context);
    }
    
    public void sendGameEvent(int type, int index, boolean immediate) {
        if (immediate) {
        	mGameFlowEvent.postImmediate(type, index,
                sSystemRegistry.contextParameters.context);
        } else {
        	mGameFlowEvent.post(type, index,
                    sSystemRegistry.contextParameters.context);
        }
    }
    
    /**
     * Loads a level from a binary file.  The file consists of several layers, including background
     * tile layers and at most one collision layer.  Each layer is used to bootstrap related systems
     * and provide them with layer data.
     * @param stream  The input stream for the level file resource.
     * @param tiles   A tile library to use when constructing tiled background layers.
     * @param background  An object to assign background layer rendering components to.
     * @return
     */
    public boolean loadLevel(LevelTree.Level level, ObjectManager root) {
        boolean success = false;
        mCurrentLevel = level;
        ContextParameters params = sSystemRegistry.contextParameters;
        XmlResourceParser parser = params.context.getResources().getXml(level.resource);
        try {
            LevelBuilder builder = sSystemRegistry.levelBuilder;

            mRoot = root;
            mTileWidth = -1;
            mTileHeight = -1;
            int levelWidth = -1;
            int levelHeight = -1;

            int currentTileWidth = -1;
            int currentTileHeight = -1;

            int currentLayerResource = -1;
            int currentTilesetResource = -1;

            int currentBackgroundPriority = SortConstants.BACKGROUND_START;
            int currentForegroundPriority = SortConstants.FOREGROUND_START;

            int backgroundResource = -1;
            float backgroundSizeFactorX = 1.0f;
            float backgroundSizeFactorY = 1.0f;
            int backgroundWidth = 0;
            int backgroundHeight = 0;
            boolean backgroundKeepWidth = true;

            int dialogsResource = -1;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("background")) {
                        // Background
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("resource")) {
                                backgroundResource = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("sizeFactorX")) {
                                backgroundSizeFactorX = parser.getAttributeFloatValue(i, -1);
                                if (backgroundSizeFactorX < 1.0f) {
                                    backgroundSizeFactorX = 1.0f;
                                }
                            } else if (parser.getAttributeName(i).equals("sizeFactorY")) {
                                backgroundSizeFactorY = parser.getAttributeFloatValue(i, -1);
                                if (backgroundSizeFactorY < 1.0f) {
                                    backgroundSizeFactorY = 1.0f;
                                }
                            } else if (parser.getAttributeName(i).equals("width")) {
                            	backgroundWidth = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("height")) {
                            	backgroundHeight = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("ratio")) {
                                if (parser.getAttributeValue(i).equals("width")) {
                                    backgroundKeepWidth = true;
                                } else if (parser.getAttributeValue(i).equals("height")) {
                                    backgroundKeepWidth = false;
                                }
                            }
                        }

                    } else if (parser.getName().equals("layerMain")) {
                        // Main Layer
                        int collisionResource = -1;
                        int objectsResource = -1;
                        int hotspotsResource = -1;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("tilesheet")) {
                                currentTilesetResource = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("tileWidth")) {
                                currentTileWidth = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("tileHeight")) {
                                currentTileHeight = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("resource")) {
                                currentLayerResource = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("collisionResource")) {
// !!!! TODO : could use tilesheet of different size for the collision layer !!!!
                                collisionResource = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("objectsResource")) {
                                objectsResource = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("hotspotsResource")) {
                                hotspotsResource = parser.getAttributeResourceValue(i, -1);
                            }
                        }

                        assert backgroundResource != -1;
                        assert mBackgroundObject == null;

                        assert currentTileWidth != -1;
                        assert currentTileHeight != -1;
                        assert currentTilesetResource != -1;

                        assert currentLayerResource != -1;
                        assert objectsResource != -1;
                        assert hotspotsResource != -1;

                        if (collisionResource == -1) {
                            collisionResource = currentLayerResource;
                        }

                        // Builds the Main Layer
                        TiledWorld worldMain = new TiledWorld(params.context.getResources().openRawResource(currentLayerResource));

                        mTileWidth = currentTileWidth;
                        mTileHeight = currentTileHeight;

                        mWidthInTiles = worldMain.getWidth();
                        mHeightInTiles = worldMain.getHeight();

                        levelWidth = mWidthInTiles * mTileWidth;
                        levelHeight = mHeightInTiles * mTileHeight;

                        if (backgroundWidth != 0 && backgroundHeight != 0) {
                            mBackgroundObject = builder.buildBackground2(backgroundResource,
                                    levelWidth, levelHeight, backgroundSizeFactorX, backgroundSizeFactorY,
                                    (backgroundWidth / backgroundHeight), backgroundKeepWidth);

                        } else {
                            mBackgroundObject = builder.buildBackground(backgroundResource,
                                    levelWidth, levelHeight, backgroundSizeFactorX, backgroundSizeFactorY);
                        }

                        root.add(mBackgroundObject);

                        builder.addMainTileMapLayer(mBackgroundObject,
                                params.gameWidth, params.gameHeight, mTileWidth, mTileHeight,
// !!!! TODO : handle size factor for main layer !!!!
// => and propagate it to collision, objects, hot spots layers !
                                1.0f, 1.0f, worldMain, currentTilesetResource);

                        // Builds the Collision Layer
                        TiledWorld worldCollision = new TiledWorld(params.context.getResources().openRawResource(collisionResource));

                        assert mWidthInTiles == worldCollision.getWidth();
                        assert mHeightInTiles == worldCollision.getHeight();

                        CollisionSystem collision = sSystemRegistry.collisionSystem;
                        if (collision != null) {
                            collision.initialize(worldCollision, mTileWidth, mTileHeight);
                        }

                        // Builds the Objects Layer
                        TiledWorld worldObjects = new TiledWorld(params.context.getResources().openRawResource(objectsResource));

                        assert mWidthInTiles == worldObjects.getWidth();
                        assert mHeightInTiles == worldObjects.getHeight();

                        mSpawnLocations = worldObjects;
                        spawnObjects();

                        // Builds the Hot Spots Layer
                        TiledWorld worldHotspots = new TiledWorld(params.context.getResources().openRawResource(hotspotsResource));

                        assert mWidthInTiles == worldHotspots.getWidth();
                        assert mHeightInTiles == worldHotspots.getHeight();

                        HotSpotSystem hotSpots = sSystemRegistry.hotSpotSystem;
                        if (hotSpots != null) {
                            hotSpots.setWorld(worldHotspots);
                        }

                    } else if (parser.getName().equals("layer")) {
                        // Additional Layer
                        assert mBackgroundObject != null;

                        boolean foreground = false;
                        float scrollSpeedX = 1.0f;
                        float scrollSpeedY = 1.0f;
                        float sizeFactorX = 1.0f;
                        float sizeFactorY = 1.0f;
                        float movingSpeedX = 0.0f;
                        float movingSpeedY = 0.0f;

// !!!! TODO : could use the same data as layerMain ... !!!!
                        currentLayerResource = -1;
                        currentTilesetResource = -1;
                        currentTileWidth = -1;
                        currentTileHeight = -1;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("tilesheet")) {
                                currentTilesetResource = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("tileWidth")) {
                                currentTileWidth = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("tileHeight")) {
                                currentTileHeight = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("resource")) {
                                currentLayerResource = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("foreground")) {
                                if (parser.getAttributeValue(i).equals("true")) {
                                    foreground = true;
                                }
                            } else if (parser.getAttributeName(i).equals("sizeFactorX")) {
                                sizeFactorX = parser.getAttributeFloatValue(i, -1);
                                if (sizeFactorX <= 0.0f) {
                                	sizeFactorX = 0.1f;
                                }
                            } else if (parser.getAttributeName(i).equals("sizeFactorY")) {
                                sizeFactorY = parser.getAttributeFloatValue(i, -1);
                                if (sizeFactorY <= 0.0f) {
                                	sizeFactorY = 0.1f;
                                }
                            } else if (parser.getAttributeName(i).equals("movingSpeedX")) {
                                movingSpeedX = parser.getAttributeFloatValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("movingSpeedY")) {
                                movingSpeedY = parser.getAttributeFloatValue(i, -1);
                            }
                        }

                        assert currentLayerResource != -1;
                        assert currentTilesetResource != -1;
                        assert currentTileWidth != -1;
                        assert currentTileHeight != -1;

                        int currentPriority;
                        if (foreground) {
                            currentPriority = ++currentForegroundPriority;
                        } else {
                            currentPriority = ++currentBackgroundPriority;
                        }

                        // Builds an additional Visual Layer
                        TiledWorld world = new TiledWorld(params.context.getResources().openRawResource(currentLayerResource));

// !!!! TODO : check it's ok in every situation !!!!
                        final float width = currentTileWidth * world.getWidth() * sizeFactorX;
                        final float sub_width = width - params.gameWidth;
                        final float main_width = levelWidth - params.gameWidth;
                        if (main_width <= 0.0f || sub_width <= 0.0f) {
// !!!! TODO : if only "sub_width" is <0 should resize layer to screen width !!!!
                            scrollSpeedX = 0.0f;
                        } else {
                            scrollSpeedX = sub_width / main_width;
                        }

                        final float height = currentTileHeight * world.getHeight() * sizeFactorY;
                        final float sub_height = height - params.gameHeight;
                        final float main_height = levelHeight - params.gameHeight;
                        if (main_height <= 0 || sub_height <= 0) {
// !!!! TODO : if only "sub_height" is <0 should resize layer to screen height !!!!
                            scrollSpeedY = 0.0f;
                        } else {
                            scrollSpeedY = sub_height / main_height;
                        }

                        builder.addTileMapLayer(mBackgroundObject, currentPriority, scrollSpeedX, scrollSpeedY,
                                params.gameWidth, params.gameHeight, currentTileWidth, currentTileHeight,
                                sizeFactorX, sizeFactorY,
//////// speed - m
                                movingSpeedX, movingSpeedY,
//////// speed - e
                                world, currentTilesetResource);

                    } else if (parser.getName().equals("dialogs")) {
                        // Dialogs
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("resource")) {
                                dialogsResource = parser.getAttributeResourceValue(i, -1);
                            }
                        }

                        if (dialogsResource != -1) {
                            LevelTree.loadLevelDialog(params.context, mCurrentLevel, dialogsResource);
                        }
                    }
                }
                eventType = parser.next(); 
            }

        } catch (Exception e) {
            //TODO: figure out the best way to deal with this.  Assert?
        	DebugLog.e("LevelSystem",  "EXCEPTION while reading XML!");
        }

        return success;
    }
    
    public void spawnObjects() {
        GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
        if (factory != null && mSpawnLocations != null) {
            DebugLog.d("LevelSystem", "Spawning Objects!");
// !!!! TODO : could handle different tiles size !!!!
// => and give a spawn position inside the tile, and resize it
            factory.spawnFromWorld(mSpawnLocations, mTileWidth, mTileHeight);
        }
    }

    public void incrementAttemptsCount() {
        mAttempts++;
    }
    
    public int getAttemptsCount() {
        return mAttempts;
    }
    
    public LevelTree.Level getCurrentLevel() {
    	return mCurrentLevel;
    }
}
