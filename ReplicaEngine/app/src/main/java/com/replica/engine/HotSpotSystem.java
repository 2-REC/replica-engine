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
 * A system for testing positions against "hot spots" embedded in the level tile map data.
 * A level may contain a layer of "hot spots," tiles that provide a hint to the game objects about
 * how to act in that particular area of the game world.  Hot spots are commonly used to direct AI
 * characters, or to define areas where special collision rules apply (e.g. regions that cause
 * instant death when entered).
 */
public class HotSpotSystem extends BaseObject {
    TiledWorld mWorld;
    
    public class HotSpotType {
        public static final int NONE = -1;
        public static final int GO_RIGHT = 0;
        public static final int GO_LEFT = 1;
        public static final int GO_UP = 2;
        public static final int GO_DOWN = 3;
        public static final int GO_UP_RIGHT = 4;
        public static final int GO_UP_LEFT = 5;
        public static final int GO_DOWN_LEFT = 6;
        public static final int GO_DOWN_RIGHT = 7;
        public static final int GO_TOWARDS_PLAYER = 8;
        public static final int GO_RANDOM = 9;
        
        public static final int WAIT_SHORT = 10;
        public static final int WAIT_MEDIUM = 11;
        public static final int WAIT_LONG = 12;

        public static final int ATTACK = 13;
        public static final int TALK = 14;
        public static final int DIE = 15;
        public static final int WALK_AND_TALK = 16;
        public static final int TAKE_CAMERA_FOCUS = 17;
        public static final int RELEASE_CAMERA_FOCUS = 18;
        public static final int END_LEVEL = 19;
        public static final int GAME_EVENT = 20;
        public static final int NPC_RUN_QUEUED_COMMANDS = 21;

        public static final int NPC_GO_RIGHT = 22;
        public static final int NPC_GO_LEFT = 23;
        public static final int NPC_GO_UP = 24;
        public static final int NPC_GO_DOWN = 25;
        public static final int NPC_GO_UP_RIGHT = 26;
        public static final int NPC_GO_UP_LEFT = 27;
        public static final int NPC_GO_DOWN_LEFT = 28;
        public static final int NPC_GO_DOWN_RIGHT = 29;
        public static final int NPC_GO_TOWARDS_PLAYER = 30;
        public static final int NPC_GO_RANDOM = 31;
        public static final int NPC_GO_UP_FROM_GROUND = 32;
        public static final int NPC_GO_DOWN_FROM_CEILING = 33;
        public static final int NPC_STOP = 34;
        public static final int NPC_SLOW = 35;

//////// CONTROLLER - MID
        public static final int SWAP_MIN = 50;
        public static final int SWAP_SWIM = SWAP_MIN;
        public static final int SWAP_CLIMB = SWAP_MIN + 1;
        public static final int SWAP_LADDER = SWAP_MIN + 2;
        public static final int SWAP_FLY = SWAP_MIN + 3;
        public static final int SWAP_VEHICLE = SWAP_MIN + 4;
        public static final int SWAP_MAX = SWAP_MIN + 5; // 5 different swaps possible (SWAP_MAX not accepted)
//////// CONTROLLER - END

//////// anim - m
/*
// !!!! TODO : change order and values !!!!
// !!!! TODO : could do as for dialogs, with an interval of values used for animations ...
// => but then need to convert index into a resource id
        public static final int PLAY_ANIMATION = 75;
*/
//////// anim - e

        // values >= NPC_SELECT_DIALOG will be considered as dialogs.
        // => could have a "NPC_SELECT_DIALOG_MAX" to limit the number of dialogs.
        public static final int NPC_SELECT_DIALOG = 100;

    }
    
    public HotSpotSystem() {
        super();
    }
    
    @Override
    public void reset() {
        mWorld = null;
    }
    
    public final void setWorld(TiledWorld world) {
        mWorld = world;
    }
    
    public int getHotSpot(float worldX, float worldY) {
        //TOOD: take a region?  how do we deal with multiple hot spot intersections?
        int result = HotSpotType.NONE;
        if (mWorld != null) {
            
            final int xTile = getHitTileX(worldX);
            final int yTile = getHitTileY(worldY);
            
            result = mWorld.getTile(xTile, yTile);
        }
        
        return result;
    }
    
    public int getHotSpotByTile(int tileX, int tileY) {
        //TOOD: take a region?  how do we deal with multiple hot spot intersections?
        int result = HotSpotType.NONE;
        if (mWorld != null) {     
            result = mWorld.getTile(tileX, tileY);
        }
        
        return result;
    }
    
    public final int getHitTileX(float worldX) {
        int xTile = 0;
        LevelSystem level = sSystemRegistry.levelSystem;
        if (mWorld != null && level != null) {
            final float worldPixelWidth = level.getLevelWidth();
            xTile = (int)Math.floor(((worldX) / worldPixelWidth) * mWorld.getWidth());
        }
        return xTile;
    }
    
    public final int getHitTileY(float worldY) {
        int yTile = 0;
        LevelSystem level = sSystemRegistry.levelSystem;
        if (mWorld != null && level != null) {
            final float worldPixelHeight = level.getLevelHeight();
            // TODO: it is stupid to keep doing this space conversion all over the code.  Fix this
            // in the TiledWorld code!
            final float flippedY = worldPixelHeight - (worldY);
            yTile = (int)Math.floor((flippedY / worldPixelHeight) * mWorld.getHeight());
        }
        return yTile;
    }
    
    public final float getTileCenterWorldPositionX(int tileX) {
        float worldX = 0.0f;
    	LevelSystem level = sSystemRegistry.levelSystem;
        if (mWorld != null && level != null) {
            final float tileWidth = level.getLevelWidth() / mWorld.getWidth();
            worldX = (tileX * tileWidth) + (tileWidth / 2.0f);
        }
        return worldX;
    }
    
}
