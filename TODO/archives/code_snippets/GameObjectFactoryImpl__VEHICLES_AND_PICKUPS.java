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

package com.replica.game;


import com.replica.engine.BaseObject;
import com.replica.engine.FixedSizeArray;
import com.replica.engine.GameObject;
import com.replica.engine.GameObjectFactory;
import com.replica.engine.SpriteComponent;
import com.replica.engine.TextureLibrary;

import com.replica.engine.DebugLog;


//////// GAME - BEGIN
// !!!! TODO: clean unused imports !!!!
import com.replica.engine.ChangeComponentsComponent;
import com.replica.engine.ChannelSystem;
//////// FOLLOW CAMERA - BEGIN
import com.replica.engine.FollowCameraComponent;
//////// FOLLOW CAMERA - END
//////// FOLLOW - BEGIN
//import com.replica.engine.FollowComponent;
import com.replica.engine.FollowPlayerComponent;
//////// FOLLOW - END
//import com.replica.engine.ForceComponentSide;
import com.replica.engine.GameComponent;
import com.replica.engine.GameObject.Team;
import com.replica.engine.GravityComponent;
import com.replica.engine.GravityComponentTop;
import com.replica.engine.HitPlayerComponent;
import com.replica.engine.HotSpotSystem;
import com.replica.engine.InventoryComponent;
import com.replica.engine.LifetimeComponent;
import com.replica.engine.MovementComponent;
import com.replica.engine.PatrolComponentTop;
import com.replica.engine.PhysicsComponent;
import com.replica.engine.PhysicsComponentTop;
import com.replica.engine.RenderComponent;
import com.replica.engine.AnimationComponentSide.PlayerAnimations;
import com.replica.engine.SimplePhysicsComponent;
import com.replica.engine.SortConstants;
import com.replica.engine.TakeControlComponent;
import com.replica.engine.AnimationComponentTop;
//// SWING - BEGIN
//import com.replica.engine.FollowBetweenComponent;
//import com.replica.engine.FollowBetweenFixedComponent;
//import com.replica.engine.SwingerComponent;
//import com.replica.engine.SwingerFixedComponent;
//// SWING - END
//import com.replica.engine.HitReactionComponent;
import com.replica.engine.HitReactionComponentSingle;
import com.replica.engine.HitReactionComponentMultiple;
import com.replica.engine.BackgroundCollisionComponent;
import com.replica.engine.DynamicCollisionComponent;
import com.replica.engine.Vector2;
import com.replica.engine.PatrolComponent;
import com.replica.engine.EnemyAnimationComponent;
//////// dialogs - m
//import com.replica.engine.SelectDialogComponent;
//////// dialogs - e
//////// NPC - MID
//import com.replica.engine.NPCComponent;
//import com.replica.engine.NPCAnimationComponent;
//////// NPC - END
import com.replica.engine.PlaySingleSoundComponent;
import com.replica.engine.EventRecorder;
import com.replica.engine.CollisionVolume;
import com.replica.engine.AABoxCollisionVolume;
import com.replica.engine.CollisionParameters.HitType;
import com.replica.engine.AnimationFrame;
import com.replica.engine.SphereCollisionVolume;
import com.replica.engine.SpriteAnimation;
//////// PLATFORM - MID
import com.replica.engine.GenericAnimationComponent;
import com.replica.engine.SleeperComponent;
import com.replica.engine.PlatformComponent;
//////// PLATFORM - END
import com.replica.engine.CameraSystem;
import com.replica.engine.SoundSystem;
import com.replica.engine.Utils;
//////// DOOR - MID
import com.replica.engine.ButtonAnimationComponent;
import com.replica.engine.DoorAnimationComponent;
import com.replica.engine.SolidSurfaceComponent;
//////// DOOR - END
//////// CLIMBING - MID
import com.replica.engine.LevelSystem;
//////// CLIMBING - END

//////// BALLS 20140328 - MID
//import com.replica.engine.LaunchProjectileComponent;
//////// BALLS 20140328 - END

//////// GAME - END

/** A class for generating game objects at runtime.
 * This should really be replaced with something that is data-driven, but it is hard to do data
 * parsing quickly at runtime.  For the moment this class is full of large functions that just
 * patch pointers between objects, but in the future those functions should either be 
 * a) generated from data at compile time, or b) described by data at runtime.
 */
public class GameObjectFactoryImpl extends GameObjectFactory {
// !!!! TODO: set to appropriate value depending on game !!!!
    private final static int MAX_GAME_OBJECTS = 384;

//////// GAME - BEGIN

    private final static String sRedButtonChannel = "RED BUTTON";
//    private final static String sBlueButtonChannel = "BLUE BUTTON";
//    private final static String sGreenButtonChannel = "GREEN BUTTON";
//    private final static String sSurprisedNPCChannel = "SURPRISED";

//////// GAME - END


    public GameObjectFactoryImpl() {
        super(MAX_GAME_OBJECTS, GameObjectType.OBJECT_COUNT.ordinal());

        ComponentClass[] componentTypes = {
//////// GAME - BEGIN
// !!!! TODO: check & add required components !!!!

            new ComponentClass(BackgroundCollisionComponent.class, 192),
            new ComponentClass(ChangeComponentsComponent.class, 256),
            new ComponentClass(DynamicCollisionComponent.class, 256),
            new ComponentClass(EnemyAnimationComponent.class, 256),
//////// FOLLOW - BEGIN
//            new ComponentClass(FollowComponent.class, 256),
            new ComponentClass(FollowPlayerComponent.class, 256),
//////// FOLLOW - END
//////// FOLLOW CAMERA - BEGIN
            new ComponentClass(FollowCameraComponent.class, 32),
//////// FOLLOW CAMERA - END
            new ComponentClass(GravityComponent.class, 128),
            new ComponentClass(GravityComponentTop.class, 128),
//            new ComponentClass(ForceComponentSide.class, 2),
            new ComponentClass(HitPlayerComponent.class, 256),
            new ComponentClass(HitReactionComponentSingle.class, 256),
            new ComponentClass(HitReactionComponentMultiple.class, 256),
            new ComponentClass(InventoryComponent.class, 128),
            new ComponentClass(LifetimeComponent.class, 384),
            new ComponentClass(MovementComponent.class, 128),
            new ComponentClass(PatrolComponent.class, 256),
            new ComponentClass(PhysicsComponent.class, 8),
            new ComponentClass(PhysicsComponentTop.class, 8),
            new ComponentClass(PlaySingleSoundComponent.class, 128),
            new ComponentClass(RenderComponent.class, 384),
//////// PLATFORM - MID
            new ComponentClass(GenericAnimationComponent.class, 256),
            new ComponentClass(PlatformComponent.class, 256),
            new ComponentClass(SimplePhysicsComponent.class, 256),
            new ComponentClass(SleeperComponent.class, 256),
//////// PLATFORM - END
            new ComponentClass(SpriteComponent.class, 384),
            new ComponentClass(AnimationComponentSideImpl.class, 2),
            new ComponentClass(PlayerComponentSideImpl.class, 1),
            new ComponentClass(PlayerComponentSideVehicle.class, 1),
//////// SWAPPERS 20141106 - MID
            new ComponentClass(PlayerComponentTopClimber.class, 1),
            new ComponentClass(AnimationComponentTopImpl.class, 1),
//////// SWAPPERS 20141106 - END
            new ComponentClass(TakeControlComponent.class, 1),
            new ComponentClass(UpdateRecordImpl.class, 128),
            new ComponentClass(UpdateRecordHudImpl.class, 2), // enough?
//            new ComponentClass(FrameRateWatcherComponent.class, 1),
//////// dialogs - m
//            new ComponentClass(SelectDialogComponent.class, 8),
//////// dialogs - e
//////// NPC - MID
//...???
//////// NPC - END
            new ComponentClass(PatrolComponentTop.class, 32),
//////// DOOR - MID
            new ComponentClass(ButtonAnimationComponent.class, 32),
            new ComponentClass(DoorAnimationComponent.class, 256),
            new ComponentClass(SolidSurfaceComponent.class, 16),
//////// DOOR - END

//// SWING - BEGIN
//            new ComponentClass(SwingerComponent.class, 50),
//            new ComponentClass(SwingerFixedComponent.class, 50),
//            new ComponentClass(FollowBetweenComponent.class, 50),
//            new ComponentClass(FollowBetweenFixedComponent.class, 50),
//// SWING - END

//////// GAME - END
        };
        setComponentClasses(componentTypes);
    }

    protected boolean isPlayer(int index) {

        boolean ok = false;

//////// GAME - BEGIN
// !!!! TODO: add a condition for each object that can be player !!!!

        ok = (index == GameObjectType.PLAYER.index());

//////// GAME - END
        return ok;
    }

/*
    @Override
    public void reset() {
    }
*/

    private FixedSizeArray<BaseObject> getStaticData(GameObjectType type) {
        return mStaticData.get(type.ordinal());
    }

    private void setStaticData(GameObjectType type, FixedSizeArray<BaseObject> data) {
        setStaticData(type.ordinal(), data);
    }

    private void addStaticData(GameObjectType type, GameObject object, SpriteComponent sprite) {
        addStaticData(type.ordinal(), object, sprite);
    }


    // List of game objects that can be spawned at runtime.
    // The objects' indices must match the order of the object tileset in the level editor.
    public enum GameObjectType {
        INVALID (-1),

//////// GAME - BEGIN

        PLAYER (0),

        // Collectables
        COIN (1),
        PICKUP_LIFE (2),
//////// SPEEDUP - BEGIN
        PICKUP_SPEEDUP (3),
//////// SPEEDUP - END

//////// VEHICLE 20140902 - BEGIN
        PICKUP_VEHICLE_1 (4),
//////// VEHICLE 20140902 - END

        //// SWING - BEGIN
//        COIN_SWING (300),
//        PENDULUM (301),
//        COIN_BETWEEN (302),
//        COIN_BETWEEN_FIXED (303),
//// SWING - END
//////// FOLLOW CAMERA - BEGIN
        LAVA(128),
        LAVA_WALL(129),
//////// FOLLOW CAMERA - END

        // Characters
//////// dialogs - m
//        KYLE_DEAD (10),
//////// dialogs - e
//////// NPC - MID
//        EVIL_KABOCHA (...); ???
//////// NPC - END
        //...,

        // AI
//        BROBOT (16),
//////// PLATFORM - MID
        PINK_NAMAZU (22),
//////// PLATFORM - END
        //...,

        // Objects
//////// DOOR - MID
        DOOR_RED (32),
        BUTTON_RED (35),
//////// DOOR - END
//////// BREAKABLE - BEGIN
        BREAKABLE_BLOCK(36),
//        BREAKABLE_BLOCK(3),
//////// BREAKABLE - END
//////// CLIMBING 20141024 - MID
        CLIMBING_TOP(37),
//////// CLIMBING 20141024 - END
        //...,

        // Effects
        DUST (48),
//        EXPLOSION_SMALL (49),
//        EXPLOSION_LARGE (50),
        EXPLOSION_GIANT (51),

        // Special Spawnable
//        ANIMATION_PLAYER (55),

//        CAMERA_BIAS (56),

//        FRAMERATE_WATCHER (57),

//        INFINITE_SPAWNER (58),


        // Projectiles
        //...,

        // Special Objects -- Not spawnable normally
//////// VEHICLE 20140902 - BEGIN
        PLAYER2 (-1),
        VEHICLE_1 (-1),
//////// VEHICLE 20140902 - END
//////// SWAPPERS 20141106 - MID
        PLAYER_CLIMB (-1),
//        PLAYER_LADDER (-1),
//        PLAYER_SWIM (-1),
//        PLAYER_FLY (-1),
//////// SWAPPERS 20141106 - END

//        SMOKE_BIG (-1),
//        SMOKE_SMALL (-1),

        CRUSH_FLASH (-1),

//        FLASH (-1),
//        SMOKE_POOF (-1),

//////// GAME - END

        // End
        OBJECT_COUNT (-1);

        private final int mIndex;

        GameObjectType(int index) {
            this.mIndex = index;
        }

        public int index() {
            return mIndex;
        }

        public static GameObjectType indexToType(int index) {
            final GameObjectType[] valuesArray = values();
            GameObjectType foundType = INVALID;
            for (int x = 0; x < valuesArray.length; x++) {
                GameObjectType type = valuesArray[x];
                if (type.mIndex == index) {
                    foundType = type;
                    break;
                }
            }
            return foundType;
        }
    }

    public void preloadEffects() {
        TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

//////// GAME - BEGIN
// Textures that appear in every level (long term)

        textureLibrary.allocateTexture(R.drawable.dust01);
        textureLibrary.allocateTexture(R.drawable.dust02);
        textureLibrary.allocateTexture(R.drawable.dust03);
        textureLibrary.allocateTexture(R.drawable.dust04);
        textureLibrary.allocateTexture(R.drawable.dust05);

//        textureLibrary.allocateTexture(R.drawable.effect_energyball01);
//        textureLibrary.allocateTexture(R.drawable.effect_energyball02);
//        textureLibrary.allocateTexture(R.drawable.effect_energyball03);
//        textureLibrary.allocateTexture(R.drawable.effect_energyball04);
//
//        textureLibrary.allocateTexture(R.drawable.effect_explosion_small01);
//        textureLibrary.allocateTexture(R.drawable.effect_explosion_small02);
//        textureLibrary.allocateTexture(R.drawable.effect_explosion_small03);
//        textureLibrary.allocateTexture(R.drawable.effect_explosion_small04);
//        textureLibrary.allocateTexture(R.drawable.effect_explosion_small05);
//        textureLibrary.allocateTexture(R.drawable.effect_explosion_small06);
//        textureLibrary.allocateTexture(R.drawable.effect_explosion_small07);

        textureLibrary.allocateTexture(R.drawable.effect_explosion_big01);
        textureLibrary.allocateTexture(R.drawable.effect_explosion_big02);
        textureLibrary.allocateTexture(R.drawable.effect_explosion_big03);
        textureLibrary.allocateTexture(R.drawable.effect_explosion_big04);
        textureLibrary.allocateTexture(R.drawable.effect_explosion_big05);
        textureLibrary.allocateTexture(R.drawable.effect_explosion_big06);
        textureLibrary.allocateTexture(R.drawable.effect_explosion_big07);
        textureLibrary.allocateTexture(R.drawable.effect_explosion_big08);
        textureLibrary.allocateTexture(R.drawable.effect_explosion_big09);

//        textureLibrary.allocateTexture(R.drawable.effect_smoke_big01);
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_big02);
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_big03);
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_big04);
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_big05);
//
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_small01);
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_small02);
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_small03);
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_small04);
//        textureLibrary.allocateTexture(R.drawable.effect_smoke_small05);

        textureLibrary.allocateTexture(R.drawable.effect_crush_back01);
        textureLibrary.allocateTexture(R.drawable.effect_crush_back02);
        textureLibrary.allocateTexture(R.drawable.effect_crush_back03);
        textureLibrary.allocateTexture(R.drawable.effect_crush_front01);
        textureLibrary.allocateTexture(R.drawable.effect_crush_front02);
        textureLibrary.allocateTexture(R.drawable.effect_crush_front03);
        textureLibrary.allocateTexture(R.drawable.effect_crush_front04);
        textureLibrary.allocateTexture(R.drawable.effect_crush_front05);
        textureLibrary.allocateTexture(R.drawable.effect_crush_front06);
        textureLibrary.allocateTexture(R.drawable.effect_crush_front07);

//////// GAME - END
    }

    @Override
    public GameObject spawnFromIndex(int index, float x, float y, boolean horzFlip) {
        GameObject object = null;
        GameObjectType type = GameObjectType.indexToType(index);
        if (type != GameObjectType.INVALID) {
            object = spawn(type, x, y, horzFlip);
        }
        return object;
    }

    @Override
    public GameObject spawnFromOrdinal(int ordinal, float x, float y, boolean horzFlip) {
        GameObject object = null;
        if (ordinal > 0 &&
                ordinal < GameObjectType.OBJECT_COUNT.ordinal()) {
            GameObjectType type = GameObjectType.values()[ordinal];
            if (type != GameObjectType.INVALID) {
                object = spawn(type, x, y, horzFlip);
            }
        }
        return object;
    }

//////// VEHICLE 20140902 - BEGIN
    @Override
    public GameObject spawnControllerFromOrdinal(int ordinal, float x, float y, GameObject parent, int life, float time) {
        GameObject object = null;
        if (ordinal > 0 &&
                ordinal < GameObjectType.OBJECT_COUNT.ordinal()) {
            GameObjectType type = GameObjectType.values()[ordinal];
            if (type != GameObjectType.INVALID) {
                object = spawnController(type, x, y, parent, life, time);
            }
        }
        return object;
    }
//////// VEHICLE 20140902 - END

    public GameObject spawn(GameObjectType type, float x, float y, boolean horzFlip) {
        GameObject newObject = null;
        switch (type) {
//////// GAME - BEGIN

            case PLAYER:
                newObject = spawnPlayer(x, y);
                break;

//////// dialogs - m
//            case KYLE_DEAD:
//                newObject = spawnKyleDead(x, y);
//                break;
//////// dialogs - e

//////// NPC - MID
//            case EVIL_KABOCHA:
//                newObject = spawnEnemyEvilKabocha(x, y, horzFlip);
//////// NPC - END

            case COIN:
                newObject = spawnCoin(x, y);
//                newObject = spawnFollowCoin(x, y);
                break;

            case LAVA:
                newObject = spawnLava(y);
                break;

            case LAVA_WALL:
                newObject = spawnLavaWall(x);
                break;

            case PICKUP_LIFE:
                newObject = spawnPickupLife(x, y);
                break;

//////// SPEEDUP - BEGIN
            case PICKUP_SPEEDUP:
                newObject = spawnPickupSpeedUp(x, y);
                break;
//////// SPEEDUP - END

//////// VEHICLE 20140902 - BEGIN
            case PICKUP_VEHICLE_1:
                newObject = spawnPickupVehicle1(x, y, -1);
                break;
//////// VEHICLE 20140902 - END

//            case BROBOT:
//                newObject = spawnEnemyBrobot(x, y, horzFlip);
//                break;

//////// PLATFORM - MID
           case PINK_NAMAZU:
               newObject = spawnEnemyPinkNamazu(x, y, horzFlip);
               break;
//////// PLATFORM - END

//////// DOOR - MID
            case DOOR_RED:
                newObject = spawnObjectDoor(x, y, GameObjectType.DOOR_RED, (type == GameObjectType.DOOR_RED));
                break;

            case BUTTON_RED:
                newObject = spawnObjectButton(x, y, GameObjectType.BUTTON_RED);
                break;
//////// DOOR - END

//////// BREAKABLE - BEGIN
            case BREAKABLE_BLOCK:
                newObject = spawnBreakableBlock(x, y);
                break;
//////// BREAKABLE - END

//////// CLIMBING 20141024 - MID
            case CLIMBING_TOP:
                newObject = spawnClimbingWallTop(x, y);
                break;
//////// CLIMBING 20141024 - END

            case DUST:
                newObject = spawnDust(x, y, horzFlip);
                break;
//            case EXPLOSION_SMALL:
//                newObject = spawnEffectExplosionSmall(x, y);
//                break;
//            case EXPLOSION_LARGE:
//                newObject = spawnEffectExplosionLarge(x, y);
//                break;
            case EXPLOSION_GIANT:
                newObject = spawnEffectExplosionGiant(x, y);
                break;
//            case ANIMATION_PLAYER:
//                newObject = spawnAnimationPlayer(x, y);
//                break;
//            case CAMERA_BIAS:
//                newObject = spawnCameraBias(x, y);
//                break;
//            case FRAMERATE_WATCHER:
//                newObject = spawnFrameRateWatcher(x, y);
//                break;
//            case INFINITE_SPAWNER:
//                newObject = spawnObjectInfiniteSpawner(x, y);
//                break;

//////// VEHICLE 20140902 - BEGIN
// !!!! TODO: remove ... !!!!
//            case VEHICLE_1:
//                newObject = spawnVehicle1(x, y);
//                break;
//////// VEHICLE 20140902 - END

//            case SMOKE_BIG:
//                newObject = spawnEffectSmokeBig(x, y);
//                break;
//            case SMOKE_SMALL:
//                newObject = spawnEffectSmokeSmall(x, y);
//                break;
            case CRUSH_FLASH:
                newObject = spawnEffectCrushFlash(x, y);
                break;
//            case FLASH:
//                newObject = spawnEffectFlash(x, y);
//                break;
//            case SMOKE_POOF:
//                newObject = spawnSmokePoof(x, y);
//                break;

//////// GAME - END

            case INVALID:
            case OBJECT_COUNT:
            default:
                DebugLog.e("GameObjectFactoryImpl", "Invalid value in spawn: " + type);
                break;
        }

        return newObject;
    }

//////// VEHICLE 20140902 - BEGIN
    public GameObject spawnController(GameObjectType type, float x, float y,
            GameObject parent, int life, float time) {
DebugLog.e("FACTORY", "type: " + type);

        GameObject newObject = null;
        switch (type) {
//////// GAME - BEGIN
// !!!! TODO: could uniformise the parameters (life & time for all) !!!!

            case PLAYER2:
                newObject = spawnPlayer2(x, y, parent, time);
                break;

//////// CLIMBING 20141024 - MID
            case PLAYER_CLIMB:
                newObject = spawnPlayerClimb(x, y, parent, life);
                break;
//////// CLIMBING 20141024 - END

            case VEHICLE_1:
                newObject = spawnVehicle1(x, y, parent, life);
                break;

            case PICKUP_VEHICLE_1:
                newObject = spawnPickupVehicle1(x, y, life);
                break;

//////// GAME - END

            case INVALID:
            case OBJECT_COUNT:
            default:
                DebugLog.e("GameObjectFactoryImpl", "Invalid value in spawnController: " + type);
                break;
        }

        return newObject;
    }
//////// VEHICLE 20140902 - END

    ////////////////////////////////////////////////////////////////

/*
    public GameObject spawnFrameRateWatcher( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;
        ContextParameters params = sSystemRegistry.contextParameters;

        GameObject object = mGameObjectPool.allocate();
// !!!! ???? TODO : why these values ? ???? !!!!
        object.getPosition().set( 250, 0 );	// HACK!
        object.activationRadius = mAlwaysActive;
        object.width = params.gameWidth;
        object.height = params.gameHeight;

        DrawableBitmap indicator = new DrawableBitmap( textureLibrary.allocateTexture( R.drawable.framerate_warning ),
                                                       ( int )object.width,
                                                       ( int )object.height );

// !!!! ???? TODO : why these values ? ???? !!!!
        indicator.setCrop( 0, 8, 8, 8 ); // hack!  this shouldn't be hard-coded.

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.OVERLAY );
        render.setCameraRelative( false );

        FrameRateWatcherComponent watcher = ( FrameRateWatcherComponent )allocateComponent( FrameRateWatcherComponent.class );
        watcher.setup( render, indicator );

        object.add( render );
        object.add( watcher );

        return object;
    }
*/


//////// GAME - BEGIN

    public GameObject spawnPlayer( float positionX, float positionY )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        // load textures related to weapons/items/etc.
        // ( if not done here, drawables won't be visible ...)
//////// FOLLOW CAMERA - BEGIN
textureLibrary.allocateTexture( R.drawable.lava1 );
textureLibrary.allocateTexture( R.drawable.lava2 );
textureLibrary.allocateTexture( R.drawable.lavawall1 );
textureLibrary.allocateTexture( R.drawable.lavawall2 );
//////// FOLLOW CAMERA - END
//////// CLIMBING 20141028 - MID
        // HAVE TO DO THAT HERE, ELSE DRAWABLES WILL NOT BE VISIBLE WHEN CLIMBER IS SPAWNED
        textureLibrary.allocateTexture( R.drawable.player_climb_idle_01 );
        textureLibrary.allocateTexture( R.drawable.player_climb_idle_02 );
        textureLibrary.allocateTexture( R.drawable.player_climb_idle_03 );
        textureLibrary.allocateTexture( R.drawable.player_climb_move_01 );
        textureLibrary.allocateTexture( R.drawable.player_climb_move_02 );
        textureLibrary.allocateTexture( R.drawable.player_climb_move_03 );
        textureLibrary.allocateTexture( R.drawable.player_climb_move_fast_01 );
        textureLibrary.allocateTexture( R.drawable.player_climb_move_fast_02 );
        textureLibrary.allocateTexture( R.drawable.player_climb_move_fast_03 );
//        textureLibrary.allocateTexture( R.drawable.player_attack_stomp_01 );
//        textureLibrary.allocateTexture( R.drawable.player_attack_stomp_02 );
//        textureLibrary.allocateTexture( R.drawable.player_attack_stomp_03 );
//        textureLibrary.allocateTexture( R.drawable.player_attack_stomp_04 );
//        textureLibrary.allocateTexture( R.drawable.player_attack_dash_01 );
//        textureLibrary.allocateTexture( R.drawable.player_attack_dash_02 );
//        textureLibrary.allocateTexture( R.drawable.player_attack_dash_03 );
//        textureLibrary.allocateTexture( R.drawable.player_attack_dash_04 );
//        textureLibrary.allocateTexture( R.drawable.player_hit_01 );
//        textureLibrary.allocateTexture( R.drawable.player_hit_02 );
        textureLibrary.allocateTexture( R.drawable.player_climb_die_01 );
//////// CLIMBING 20141028 - END

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 64;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PLAYER );

        if ( staticData == null )
        {
            final int staticObjectCount = 19;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );

            PhysicsComponent physics = ( PhysicsComponent )allocateComponent( PhysicsComponent.class );

// !!!! TODO : adapt values ... !!!!
//            physics.setMass( 9.1f ); // ~90kg w/ earth gravity
            physics.setMass( 1.0f ); // ~10kg w/ earth gravity
            physics.setDynamicFrictionCoeffecient( 0.2f );
            physics.setStaticFrictionCoeffecient( 0.01f );
//?            physics.setBounciness( ? );

// !!!! TODO : adapt values ... !!!!
//            ForceComponentSide force = ( ForceComponentSide )allocateComponent( ForceComponentSide.class );
//            force.setMass( 1.0f ); // ~10kg w/ earth gravity
//            force.setDynamicFrictionCoeffecient( 0.2f );
//            force.setStaticFrictionCoeffecient( 0.01f );
//            force.setForce( 50.0f );

            // Animation Data
            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 16, 32, 32 ) );

//////// PLATFORM - BEGIN
//            FixedSizeArray<CollisionVolume> pressAndCollectVolume = new FixedSizeArray<CollisionVolume>( 2 );
//////// PLATFORM - MID
            FixedSizeArray<CollisionVolume> pressAndCollectVolume = new FixedSizeArray<CollisionVolume>( 3 );
//////// PLATFORM - END
            AABoxCollisionVolume collectionVolume = new AABoxCollisionVolume( 16, 0, 32, 48 );
            collectionVolume.setHitType( HitType.COLLECT );
            pressAndCollectVolume.add( collectionVolume );
            AABoxCollisionVolume pressCollisionVolume = new AABoxCollisionVolume( 16, 0, 32, 16 );
            pressCollisionVolume.setHitType( HitType.DEPRESS );
            pressAndCollectVolume.add( pressCollisionVolume );
//////// PLATFORM - MID
            AABoxCollisionVolume platformCollisionVolume = new AABoxCollisionVolume( 16, 0, 32, 16 );
//            AABoxCollisionVolume platformCollisionVolume = new AABoxCollisionVolume( 16, 0, 32, 64 );
            platformCollisionVolume.setHitType( HitType.PLATFORM );
            pressAndCollectVolume.add( platformCollisionVolume );
//////// PLATFORM - END

// !!!! TODO : make different animations ( with more frames ) !!!!
            SpriteAnimation animIdle = new SpriteAnimation( PlayerAnimations.IDLE.ordinal(), 4 );
            final AnimationFrame frameIdle1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stand_01 ),
                                                                  Utils.framesToTime( 24, 30 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            final AnimationFrame frameIdle2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stand_02 ),
                                                                  Utils.framesToTime( 24, 1 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            final AnimationFrame frameIdle3 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stand_03 ),
                                                                  Utils.framesToTime( 24, 1 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            animIdle.addFrame( frameIdle1 );
            animIdle.addFrame( frameIdle2 );
            animIdle.addFrame( frameIdle3 );
            animIdle.addFrame( frameIdle2 );
            animIdle.setLoop( true );

            SpriteAnimation animWalk = new SpriteAnimation( PlayerAnimations.MOVE.ordinal(), 4 );
            final AnimationFrame frameWalk1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_walk_01 ),
                                                                  Utils.framesToTime( 24, 6 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            final AnimationFrame frameWalk2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_walk_02 ),
                                                                  Utils.framesToTime( 24, 6 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            final AnimationFrame frameWalk3 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_walk_03 ),
                                                                  Utils.framesToTime( 24, 6 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            animWalk.addFrame( frameWalk1 );
            animWalk.addFrame( frameWalk2 );
            animWalk.addFrame( frameWalk3 );
            animWalk.addFrame( frameWalk2 );
            animWalk.setLoop( true );

            SpriteAnimation animRun = new SpriteAnimation( PlayerAnimations.MOVE_FAST.ordinal(), 4 );
            final AnimationFrame frameRun1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_run_01 ),
                                                                 Utils.framesToTime( 24, 6 ),
                                                                 pressAndCollectVolume,
                                                                 basicVulnerabilityVolume );
            final AnimationFrame frameRun2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_run_02 ),
                                                                 Utils.framesToTime( 24, 6 ),
                                                                 pressAndCollectVolume,
                                                                 basicVulnerabilityVolume );
            final AnimationFrame frameRun3 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_run_03 ),
                                                                 Utils.framesToTime( 24, 6 ),
                                                                 pressAndCollectVolume,
                                                                 basicVulnerabilityVolume );
            animRun.addFrame( frameRun1 );
            animRun.addFrame( frameRun2 );
            animRun.addFrame( frameRun3 );
            animRun.addFrame( frameRun2 );
            animRun.setLoop( true );

            SpriteAnimation animJumpIdle = new SpriteAnimation( PlayerAnimations.JUMP_IDLE.ordinal(), 2 );
            animJumpIdle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_idle_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );
            animJumpIdle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_idle_02 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );
//            animJumpIdle.setLoop( true );

            SpriteAnimation animJumpMove = new SpriteAnimation( PlayerAnimations.JUMP_MOVE.ordinal(), 2 );
            animJumpMove.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_move_01 ),
                                                     Utils.framesToTime( 24, 12 ),
                                                     pressAndCollectVolume,
                                                     basicVulnerabilityVolume ) );
            animJumpMove.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_move_02 ),
                                                     Utils.framesToTime( 24, 12 ),
                                                     pressAndCollectVolume,
                                                     basicVulnerabilityVolume ) );
//            animJumpMove.setLoop( true );

// !!!! TODO: set correct animation !!!!
            SpriteAnimation animJumpMoveFast = new SpriteAnimation( PlayerAnimations.JUMP_MOVE_FAST.ordinal(), 2 );
            animJumpMoveFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_move_fast_01 ),
                                                           Utils.framesToTime( 24, 12 ),
                                                           pressAndCollectVolume,
                                                           basicVulnerabilityVolume ) );
            animJumpMoveFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_move_fast_02 ),
                                                           Utils.framesToTime( 24, 12 ),
                                                           pressAndCollectVolume,
                                                           basicVulnerabilityVolume ) );
//            animJumpMoveFast.setLoop( true );

// !!!! TODO: set correct animation !!!!
            SpriteAnimation animFallIdle = new SpriteAnimation( PlayerAnimations.FALL_IDLE.ordinal(), 2 );
            animFallIdle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_idle_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );
            animFallIdle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_idle_02 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );
//            animFallIdle.setLoop( true );

// !!!! TODO: set correct animation !!!!
            SpriteAnimation animFallMove = new SpriteAnimation( PlayerAnimations.FALL_MOVE.ordinal(), 2 );
            animFallMove.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_move_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );
            animFallMove.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_move_02 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );
//            animFallMove.setLoop( true );

// !!!! TODO: set correct animation !!!!
            SpriteAnimation animFallMoveFast = new SpriteAnimation( PlayerAnimations.FALL_MOVE_FAST.ordinal(), 2 );
            animFallMoveFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_move_fast_01 ),
                                                           Utils.framesToTime( 24, 12 ),
                                                           pressAndCollectVolume,
                                                           basicVulnerabilityVolume ) );
            animFallMoveFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_move_fast_02 ),
                                                           Utils.framesToTime( 24, 12 ),
                                                           pressAndCollectVolume,
                                                           basicVulnerabilityVolume ) );
//            animFallMoveFast.setLoop( true );

// !!!! TODO: set correct animation !!!!
            SpriteAnimation animFallFast = new SpriteAnimation( PlayerAnimations.FALL_FAST.ordinal(), 2 );
            animFallFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_fast_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );
            animFallFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_fast_02 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );
//            animFallFast.setLoop( true );

//////// PLATFORM - BEGIN
//            FixedSizeArray<CollisionVolume> stompAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
//////// PLATFORM - MID
            FixedSizeArray<CollisionVolume> stompAttackVolume = new FixedSizeArray<CollisionVolume>( 4 );
//////// PLATFORM - END
            stompAttackVolume.add( new AABoxCollisionVolume( 16, -5.0f, 32, 37, HitType.HIT ) );
            stompAttackVolume.add( pressCollisionVolume );
            stompAttackVolume.add( collectionVolume );
//////// PLATFORM - MID
            stompAttackVolume.add( platformCollisionVolume );
//////// PLATFORM - END

            SpriteAnimation animAttackStomp = new SpriteAnimation( PlayerAnimations.ATTACK.ordinal(), 4 );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_01 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_02 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_03 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_04 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );

            FixedSizeArray<CollisionVolume> dashAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
// !!!! TODO: adapt attack volume !!!!
            dashAttackVolume.add( new AABoxCollisionVolume( 16, 0.0f, 32, 64, HitType.HIT ) );
            dashAttackVolume.add( pressCollisionVolume );
            dashAttackVolume.add( collectionVolume );

            SpriteAnimation animAttackDash = new SpriteAnimation( PlayerAnimations.ATTACK_ALTERNATE.ordinal(), 4 );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_01 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_02 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_03 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_04 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );

            SpriteAnimation animHit = new SpriteAnimation( PlayerAnimations.HIT_REACT.ordinal(), 2 );
            animHit.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_hit_01 ),
                                                  Utils.framesToTime( 24, 6 ),
                                                  pressAndCollectVolume,
                                                  null ) );
            animHit.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_hit_02 ),
                                                  Utils.framesToTime( 24, 12 ),
                                                  pressAndCollectVolume,
                                                  null ) );

            SpriteAnimation animDeath = new SpriteAnimation( PlayerAnimations.DEATH.ordinal(), 12 );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_01 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_02 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_03 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_04 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_05 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_06 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_07 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_08 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_09 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_10 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_11 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_12 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );

            SpriteAnimation animFrozen = new SpriteAnimation( PlayerAnimations.FROZEN.ordinal(), 1 );
            // Frozen has no frames!

//             UpdateRecordHudImpl record = ( UpdateRecordHudImpl )allocateComponent( UpdateRecordHudImpl.class );
             GameComponent record = allocateComponent( UpdateRecordHudImpl.class );

            // Save static data
            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );
//            staticData.add( force );

            staticData.add( animIdle );
            staticData.add( animWalk );
            staticData.add( animRun );
            staticData.add( animJumpIdle );
            staticData.add( animJumpMove );
            staticData.add( animJumpMoveFast );
            staticData.add( animFallIdle );
            staticData.add( animFallMove );
            staticData.add( animFallMoveFast );
            staticData.add( animFallFast );
            staticData.add( animAttackStomp );
            staticData.add( animAttackDash );
            staticData.add( animHit );
            staticData.add( animDeath );
            staticData.add( animFrozen );
            staticData.add( record );

            setStaticData( GameObjectType.PLAYER, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.PLAYER );
        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgcollision.setSize( 32, 48 );
        bgcollision.setOffset( 16, 0 );

        PlayerComponentSideImpl player = ( PlayerComponentSideImpl )allocateComponent( PlayerComponentSideImpl.class );
//////// SWAPPERS 20141106 - MID
        player.setHotSpotSwapperId(HotSpotSystem.HotSpotType.SWAP_CLIMB, GameObjectType.PLAYER_CLIMB.ordinal());
//        player.setHotSpotSwapperId(HotSpotSystem.HotSpotType.SWAP_LADDER, GameObjectType.PLAYER_LADDER.ordinal());
//        player.setHotSpotSwapperId(HotSpotSystem.HotSpotType.SWAP_SWIM, GameObjectType.PLAYER_SWIM.ordinal());
//        player.setHotSpotSwapperId(HotSpotSystem.HotSpotType.SWAP_FLY, GameObjectType.PLAYER_FLY.ordinal());
//////// SWAPPERS 20141106 - END
        AnimationComponentSideImpl animation = ( AnimationComponentSideImpl )allocateComponent( AnimationComponentSideImpl.class );
        animation.setPlayer( player );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            animation.setLandThump( sound.load( R.raw.thump ) );
            animation.setDeathSound( sound.load( R.raw.sound_explode ) );
        }

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );
        animation.setSprite( sprite );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setBounceOnTakeHit( true );
        hitReact.setPauseOnAttack( true );
        hitReact.setInvincibleTime( 3.0f );
        hitReact.setSpawnObjectOnDealHit( HitType.HIT, GameObjectType.CRUSH_FLASH.ordinal(), false, true );

        if ( sound != null )
        {
            hitReact.setSoundOnTakeHit( HitType.HIT, sound.load( R.raw.deep_clang ) );
        }

        dynamicCollision.setHitReactionComponent( hitReact );
        player.setHitReactionComponent( hitReact );

// !!!! TODO: check if ok, and if can keep data between levels ... !!!!
        InventoryComponent inventory = ( InventoryComponent )allocateComponent( InventoryComponent.class );
        final int staticDataSize = staticData.getCount();
        for ( int x = 0; x < staticDataSize; x++ )
        {
            final BaseObject entry = staticData.get( x );
            if ( entry instanceof UpdateRecordHudImpl )
            {
                final UpdateRecordHudImpl record = ( UpdateRecordHudImpl )entry;
                record.resetRecord();
                inventory.setInventory( record );
                player.setInventory( inventory );
//                animation.setInventory( inventory );
                break;
            }
        }

        ChangeComponentsComponent damageSwap = ( ChangeComponentsComponent )allocateComponent( ChangeComponentsComponent.class );
        animation.setDamageSwap( damageSwap );

// !!!! ???? TODO : want this ? ???? !!!!
//        LaunchProjectileComponent smokeGun = ( LaunchProjectileComponent )allocateComponent( LaunchProjectileComponent.class );
//        smokeGun.setDelayBetweenShots( 0.25f );
//        smokeGun.setObjectTypeToSpawn( GameObjectType.SMOKE_BIG );
//        smokeGun.setOffsetX( 32 );
//        smokeGun.setOffsetY( 15 );
//        smokeGun.setVelocityX( -150.0f );
//        smokeGun.setVelocityY( 100.0f );
//        smokeGun.setThetaError( 0.1f );

//
//        LaunchProjectileComponent smokeGun2 = ( LaunchProjectileComponent )allocateComponent( LaunchProjectileComponent.class );
//        smokeGun2.setDelayBetweenShots( 0.35f );
//        smokeGun2.setObjectTypeToSpawn( GameObjectType.SMOKE_SMALL );
//        smokeGun2.setOffsetX( 16 );
//        smokeGun2.setOffsetY( 15 );
//        smokeGun2.setVelocityX( -150.0f );
//        smokeGun2.setVelocityY( 150.0f );
//        smokeGun2.setThetaError( 0.1f ); 
//
//        damageSwap.addSwapInComponent( smokeGun );
//        damageSwap.addSwapInComponent( smokeGun2 );
//        damageSwap.setPingPongBehavior( true );
//
//        ChangeComponentsComponent invincibleSwap = ( ChangeComponentsComponent )allocateComponent( ChangeComponentsComponent.class );
//        invincibleSwap.setPingPongBehavior( true );
//        player.setInvincibleSwap( invincibleSwap );

        final EventRecorderImpl evtRec = ( EventRecorderImpl )sSystemRegistry.eventRecorder;
        object.life = evtRec.getDifficultyConstants().getPlayerMaxLife();
        object.team = Team.PLAYER;

// !!!! ???? TODO : want this ? ???? !!!!
//        // Very very basic DDA.  Make the game easier if we've died on this level too much.
//        LevelSystem level = sSystemRegistry.levelSystem;
//        if ( level != null )
//        {
//            player.adjustDifficulty( object, level.getAttemptsCount() );
//        }

        object.add( player );
        object.add( inventory );
        object.add( bgcollision );
        object.add( render );
        object.add( animation );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitReact );
// !!!! ???? TODO : want this ? ???? !!!!
//        object.add( damageSwap );
//        object.add( invincibleSwap );
//        object.add( lifetime );

        addStaticData( GameObjectType.PLAYER, object, sprite );

        sprite.playAnimation( PlayerAnimations.IDLE.ordinal() );

// !!!! ???? TODO : want this ? ???? !!!!
//=> Jets ?

// !!!! ???? TODO : want this ? ???? !!!!
//=> Sparks ?

// !!!! ???? TODO : want this ? ???? !!!!
//=> Glow ?

        CameraSystem camera = sSystemRegistry.cameraSystem;
        if ( camera != null )
        {
            camera.setTarget( object );
        }

        return object;
    }

    public GameObject spawnCoin( float positionX, float positionY )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        final int value = 1;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.COIN );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.COLLECT );

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_01),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_03),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_04),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                             basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_05 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                             basicVulnerabilityVolume ) );
            idle.setLoop( true );

            UpdateRecordImpl addCoin = ( UpdateRecordImpl )allocateComponent( UpdateRecordImpl.class );
            addCoin.mCoinsCount = value;

            staticData.add( addCoin );
            staticData.add( idle );

            setStaticData( GameObjectType.COIN, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );
// !!!! ???? TODO: need to add "action" to "object" ? ???? !!!!

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            hitReact.setSoundOnTakeHit( HitType.COLLECT, sound.load( R.raw.object_pickup_coin ) );
        }

        // TODO: this is pretty dumb.  The static data binding needs to be made generic.
        final int staticDataSize = staticData.getCount();
        for ( int x = 0; x < staticDataSize; x++ )
        {
            final BaseObject entry = staticData.get( x );
            if ( entry instanceof UpdateRecordImpl )
            {
                hitReact.setInventoryUpdate( ( UpdateRecordImpl )entry );
                break;
            }
        }

        dynamicCollision.setHitReactionComponent( hitReact );

        LifetimeComponent life = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
        life.setIncrementEventCounter( EventRecorderImpl.COUNTER_COINS_COLLECTED, value );

        object.life = 1;

        object.add( render );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitPlayer );
        object.add( hitReact );
        object.add( life );

        addStaticData( GameObjectType.COIN, object, sprite );
        sprite.playAnimation( 0 );

        EventRecorder recorder = sSystemRegistry.eventRecorder;
        recorder.incrementEventCounter( EventRecorderImpl.COUNTER_COINS_TOTAL, value );

        return object;
    }

// !!!! TODO: finish that ... !!!!
    public GameObject spawnFollowCoin( float positionX, float positionY )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        final int value = 1;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.COIN );
        if ( staticData == null )
        {
            final int staticObjectCount = 3;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent movement = allocateComponent( MovementComponent.class );
            staticData.add( movement );

            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.COLLECT );

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_01),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               null ) );
//                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
//                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_03),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
//                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_04),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               null ) );
//                                             basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_05 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
//                                             basicVulnerabilityVolume ) );
            idle.setLoop( true );

            UpdateRecordImpl addCoin = ( UpdateRecordImpl )allocateComponent( UpdateRecordImpl.class );
            addCoin.mCoinsCount = value;

            staticData.add( addCoin );
            staticData.add( idle );

            setStaticData( GameObjectType.COIN, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );
// !!!! ???? TODO: need to add "action" to "object" ? ???? !!!!

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            hitReact.setSoundOnTakeHit( HitType.COLLECT, sound.load( R.raw.object_pickup_coin ) );
        }

        // TODO: this is pretty dumb.  The static data binding needs to be made generic.
        final int staticDataSize = staticData.getCount();
        for ( int x = 0; x < staticDataSize; x++ )
        {
            final BaseObject entry = staticData.get( x );
            if ( entry instanceof UpdateRecordImpl )
            {
                hitReact.setInventoryUpdate( ( UpdateRecordImpl )entry );
                break;
            }
        }

        dynamicCollision.setHitReactionComponent( hitReact );

        LifetimeComponent life = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
        life.setIncrementEventCounter( EventRecorderImpl.COUNTER_COINS_COLLECTED, value );

        object.life = 1;

        FollowPlayerComponent follow = ( FollowPlayerComponent )allocateComponent( FollowPlayerComponent.class );
//follow.setAttached( true );
        follow.setImpulse( 50.0f );
        follow.setMaxSpeed( 200.0f );
        object.add( follow );

        object.add( render );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitPlayer );
        object.add( hitReact );
        object.add( life );

        addStaticData( GameObjectType.COIN, object, sprite );
        sprite.playAnimation( 0 );

        EventRecorder recorder = sSystemRegistry.eventRecorder;
        recorder.incrementEventCounter( EventRecorderImpl.COUNTER_COINS_TOTAL, value );

        return object;
    }

//// SWING - BEGIN
/*
    public GameObject spawnSwingCoin( GameObject followedObject, float distance )
    {
DebugLog.d("FACTORY", "SWINGER");
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( followedObject.getPosition().x, followedObject.getPosition().y );
        object.activationRadius = mAlwaysActive;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.COIN_SWING );
        if ( staticData == null )
        {
//            final int staticObjectCount = 3;
            final int staticObjectCount = 4;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            staticData.add( gravity );

// !!!! ???? TODO: has any effect ? ???? !!!!
            PhysicsComponent physics = ( PhysicsComponent )allocateComponent( PhysicsComponent.class );
            physics.setMass( 1.0f );
            staticData.add( physics );

            GameComponent movement = allocateComponent( MovementComponent.class );
            staticData.add( movement );

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_01 ),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_03 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_05 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.setLoop( true );

            staticData.add( idle );

            setStaticData( GameObjectType.COIN_SWING, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        // "ghost ball"
        //BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        //bgcollision.setSize( 16, 16 );

        object.life = 1;

        SwingerComponent swing = ( SwingerComponent )allocateComponent( SwingerComponent.class );
        swing.setFollowedObject( followedObject, distance );
//        swing.setPerpetual( true, 100.0f );
//        swing.setPositionOffsetX( followedObject.width, false );
//        swing.setPositionOffsetY( followedObject.height, true );

        object.add( render );
        object.add( sprite );
        object.add( swing );
        //object.add( bgcollision );

        addStaticData( GameObjectType.COIN_SWING, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }

    public GameObject spawnPendulum( final float x, final float y, float distance, final boolean perpetual, final float speed )
    {
DebugLog.d("FACTORY", "PENDULUM");
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( x, y );
        object.activationRadius = mAlwaysActive;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PENDULUM );
        if ( staticData == null )
        {
            final int staticObjectCount = 4;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );

// !!!! ???? TODO: has any effect ? ???? !!!!
            PhysicsComponent physics = ( PhysicsComponent )allocateComponent( PhysicsComponent.class );
            physics.setMass( 1.0f );

            GameComponent movement = allocateComponent( MovementComponent.class );

            FixedSizeArray<CollisionVolume> attackVolume = new FixedSizeArray<CollisionVolume>( 1 );
            attackVolume.add( new SphereCollisionVolume( 8, 8, 8, HitType.HIT ) );

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_01 ),
                                               Utils.framesToTime( 24, 30 ),
                                               attackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               attackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_03 ),
                                               Utils.framesToTime( 24, 2 ),
                                               attackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               attackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_05 ),
                                               Utils.framesToTime( 24, 2 ),
                                               attackVolume,
                                               null ) );
            idle.setLoop( true );

            staticData.add( gravity );
            staticData.add( physics );
            staticData.add( movement );
            staticData.add( idle );

            setStaticData( GameObjectType.PENDULUM, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

//        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
//        sprite.setCollisionComponent( dynamicCollision );
//
//        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
//        hitReact.setInvincible( true );
//        dynamicCollision.setHitReactionComponent( hitReact );
//
//        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
//        hitPlayer.setup( 8, hitReact, HitType.HIT, true );

        object.life = 1;

        SwingerFixedComponent swing = ( SwingerFixedComponent )allocateComponent( SwingerFixedComponent.class );
        swing.setAttachPoint( x, y, distance );
        swing.setPerpetual( perpetual, speed );

        object.add( render );
        object.add( sprite );
        object.add( swing );
//        object.add( dynamicCollision );
//        object.add( hitReact );
//        object.add( hitPlayer );

        addStaticData( GameObjectType.PENDULUM, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }

    public GameObject spawnSwingBetween( final GameObject target1, final GameObject target2, final float ratio )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( target1.getPosition().x, target1.getPosition().y );
        object.activationRadius = mAlwaysActive;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.COIN_BETWEEN );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent movement = allocateComponent( MovementComponent.class );
            staticData.add( movement );

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_01 ),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_03 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_05 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.setLoop( true );

            staticData.add( idle );

            setStaticData( GameObjectType.COIN_BETWEEN, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        object.life = 1;

        FollowBetweenComponent between = ( FollowBetweenComponent )allocateComponent( FollowBetweenComponent.class );
        between.setTargets( target1, target2, ratio );
//        swing.setPositionOffsetX( followedObject.width, false );
//        swing.setPositionOffsetY( followedObject.height, true );

        object.add( render );
        object.add( sprite );
        object.add( between );

        addStaticData( GameObjectType.COIN_BETWEEN, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }

    public GameObject spawnSwingBetweenFixed( final float x, final float y, final GameObject target, final float ratio )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( x, y );
        object.activationRadius = mAlwaysActive;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.COIN_BETWEEN_FIXED );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent movement = allocateComponent( MovementComponent.class );
            staticData.add( movement );

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_01 ),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_03 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_05 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               null ) );
            idle.setLoop( true );

            staticData.add( idle );

            setStaticData( GameObjectType.COIN_BETWEEN_FIXED, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        object.life = 1;

        FollowBetweenFixedComponent between = ( FollowBetweenFixedComponent )allocateComponent( FollowBetweenFixedComponent.class );
        between.setTargets( x, y, target, ratio );
//        swing.setPositionOffsetX( followedObject.width, false );
//        swing.setPositionOffsetY( followedObject.height, true );

        object.add( render );
        object.add( sprite );
        object.add( between );

        addStaticData( GameObjectType.COIN_BETWEEN_FIXED, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }
*/

//// SWING - END

//////// FOLLOW CAMERA - BEGIN
    public GameObject spawnLava( float y )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.activationRadius = mAlwaysActive;
        object.getPosition().set( 0.0f, y );

//        ContextParameters params = sSystemRegistry.contextParameters;
//        object.width = params.gameWidth;
        object.width = 800;
        object.height = 100;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.LAVA );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent movement = allocateComponent( MovementComponent.class );

            FixedSizeArray<CollisionVolume> attackVolume = new FixedSizeArray<CollisionVolume>( 1 );
            attackVolume.add( new AABoxCollisionVolume( 0, 0, object.width, object.height, HitType.DEATH ) );


            SpriteAnimation anim = new SpriteAnimation( 0, 3 );
            final AnimationFrame frame1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.lava1 ),
                                                              Utils.framesToTime( 24, 30 ),
                                                              attackVolume,
                                                              null );
            final AnimationFrame frame2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.lava2 ),
                                                              Utils.framesToTime( 24, 30 ),
                                                              attackVolume,
                                                              null );
            anim.addFrame( frame1 );
            anim.addFrame( frame2 );
            anim.addFrame( frame1 );
            anim.setLoop( true );

            staticData.add( movement );
            staticData.add( anim );

            setStaticData( GameObjectType.LAVA, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
// !!!! TODO: need to set good rendering priority, depending on what we want !!!!
        render.setPriority( SortConstants.FOREGROUND_OBJECT );
//        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setInvincible( true );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.DEATH, false );

        dynamicCollision.setHitReactionComponent( hitReact );

        FollowCameraComponent followCamera = ( FollowCameraComponent )allocateComponent( FollowCameraComponent.class );
        followCamera.setFollowX( true );

        PatrolComponentTop patrol = ( PatrolComponentTop )allocateComponent( PatrolComponentTop.class );
        patrol.setMovement( 0.0f, 25.0f, 0.0f, 1000.0f );

        object.add( followCamera );
        object.add( render );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitPlayer );
        object.add( hitReact );
        object.add( patrol );

        addStaticData( GameObjectType.LAVA, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }

    public GameObject spawnLavaWall( float x )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.activationRadius = mAlwaysActive;
        object.getPosition().set( x, 0.0f );

        object.width = 100;
        object.height = 600;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.LAVA_WALL );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent movement = allocateComponent( MovementComponent.class );

            FixedSizeArray<CollisionVolume> attackVolume = new FixedSizeArray<CollisionVolume>( 1 );
            attackVolume.add( new AABoxCollisionVolume( 0, 0, object.width, object.height, HitType.DEATH ) );


            SpriteAnimation anim = new SpriteAnimation( 0, 2 );
            final AnimationFrame frame1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.lavawall1 ),
                                                              Utils.framesToTime( 24, 16 ),
                                                              attackVolume,
                                                              null );
            final AnimationFrame frame2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.lavawall2 ),
                                                              Utils.framesToTime( 24, 8 ),
                                                              attackVolume,
                                                              null );
            anim.addFrame( frame1 );
            anim.addFrame( frame2 );
            anim.setLoop( true );

            staticData.add( movement );
            staticData.add( anim );

            setStaticData( GameObjectType.LAVA_WALL, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
// !!!! TODO: need to set good rendering priority, depending on what we want !!!!
        render.setPriority( SortConstants.FOREGROUND_OBJECT );
//        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setInvincible( true );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.DEATH, false );

        dynamicCollision.setHitReactionComponent( hitReact );

        FollowCameraComponent followCamera = ( FollowCameraComponent )allocateComponent( FollowCameraComponent.class );
        followCamera.setFollowY( true );

        PatrolComponentTop patrol = ( PatrolComponentTop )allocateComponent( PatrolComponentTop.class );
        patrol.setMovement( 25.0f, 0.0f, 1000.0f, 0.0f );

        object.add( followCamera );
        object.add( render );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitPlayer );
        object.add( hitReact );
        object.add( patrol );

        addStaticData( GameObjectType.LAVA_WALL, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }
//////// FOLLOW CAMERA - END

    public GameObject spawnPickupLife( float positionX, float positionY )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PICKUP_LIFE );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = null;
//            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
//            basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
//            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.COLLECT );

            SpriteAnimation idle = new SpriteAnimation( 0, 4 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_life_01 ),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_life_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_life_03 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_life_04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.setLoop( true );

            UpdateRecordImpl addBonus = ( UpdateRecordImpl )allocateComponent( UpdateRecordImpl.class );
            addBonus.mLifeCount = 1;

            staticData.add( addBonus );
            staticData.add( idle );

            setStaticData( GameObjectType.PICKUP_LIFE, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

//        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
//        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            hitReact.setSoundOnTakeHit( HitType.COLLECT, sound.load( R.raw.object_pickup_life ) );
        }

        // TODO: this is pretty dumb.  The static data binding needs to be made generic.
        final int staticDataSize = staticData.getCount();
        for ( int x = 0; x < staticDataSize; x++ )
        {
            final BaseObject entry = staticData.get( x );
            if ( entry instanceof UpdateRecordImpl )
            {
                hitReact.setInventoryUpdate( ( UpdateRecordImpl )entry );
                break;
            }
        }

//        dynamicCollision.setHitReactionComponent( hitReact );

        LifetimeComponent life = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
        life.setIncrementEventCounter( EventRecorderImpl.COUNTER_LIVES, 1 );

        object.life = 1;

        object.add( render );
        object.add( sprite );
//        object.add( dynamicCollision );
        object.add( hitPlayer );
        object.add( hitReact );
        object.add( life );

        addStaticData( GameObjectType.PICKUP_LIFE, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }

//////// VEHICLE 20140902 - BEGIN

    public GameObject spawnPlayer2( float positionX, float positionY, GameObject parentObject, float time )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 64;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PLAYER2 );
        if ( staticData == null )
        {
            final int staticObjectCount = 19;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );
            PhysicsComponent physics = ( PhysicsComponent )allocateComponent( PhysicsComponent.class );

// !!!! TODO : adapt values ... !!!!
// => make it less "bouncy" ... (done here ?)
//            physics.setMass( 9.1f ); // ~90kg w/ earth gravity
            physics.setMass( 5.0f ); // ~10kg w/ earth gravity
            physics.setDynamicFrictionCoeffecient( 0.2f );
            physics.setStaticFrictionCoeffecient( 0.01f );

            // Animation Data
            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 16, 32, 32 ) );

            FixedSizeArray<CollisionVolume> pressAndCollectVolume = new FixedSizeArray<CollisionVolume>( 2 );
            AABoxCollisionVolume collectionVolume = new AABoxCollisionVolume( 16, 0, 32, 48 );
            collectionVolume.setHitType( HitType.COLLECT );
            pressAndCollectVolume.add( collectionVolume );
            AABoxCollisionVolume pressCollisionVolume = new AABoxCollisionVolume( 16, 0, 32, 16 );
            pressCollisionVolume.setHitType( HitType.DEPRESS );
            pressAndCollectVolume.add( pressCollisionVolume );

            SpriteAnimation animIdle = new SpriteAnimation( PlayerAnimations.IDLE.ordinal(), 1 );
            final AnimationFrame frameIdle1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stand_01 ),
                                                                  Utils.framesToTime( 24, 30 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            animIdle.addFrame( frameIdle1 );

            SpriteAnimation animWalk = new SpriteAnimation( PlayerAnimations.MOVE.ordinal(), 1 );
            final AnimationFrame frameWalk1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_walk_01 ),
                                                                  Utils.framesToTime( 24, 6 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            animWalk.addFrame( frameWalk1 );

            SpriteAnimation animRun = new SpriteAnimation( PlayerAnimations.MOVE_FAST.ordinal(), 1 );
            final AnimationFrame frameRun1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_run_01 ),
                                                                 Utils.framesToTime( 24, 6 ),
                                                                 pressAndCollectVolume,
                                                                 basicVulnerabilityVolume );
            animRun.addFrame( frameRun1 );

            SpriteAnimation animJumpIdle = new SpriteAnimation( PlayerAnimations.JUMP_IDLE.ordinal(), 1 );
            animJumpIdle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_idle_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            SpriteAnimation animJumpMove = new SpriteAnimation( PlayerAnimations.JUMP_MOVE.ordinal(), 1 );
            animJumpMove.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_move_01 ),
                                                     Utils.framesToTime( 24, 12 ),
                                                     pressAndCollectVolume,
                                                     basicVulnerabilityVolume ) );

            SpriteAnimation animJumpMoveFast = new SpriteAnimation( PlayerAnimations.JUMP_MOVE_FAST.ordinal(), 1 );
            animJumpMoveFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_move_fast_01 ),
                                                           Utils.framesToTime( 24, 12 ),
                                                           pressAndCollectVolume,
                                                           basicVulnerabilityVolume ) );

            SpriteAnimation animFallIdle = new SpriteAnimation( PlayerAnimations.FALL_IDLE.ordinal(), 1 );
            animFallIdle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_idle_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            SpriteAnimation animFallMove = new SpriteAnimation( PlayerAnimations.FALL_MOVE.ordinal(), 1 );
            animFallMove.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_move_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            SpriteAnimation animFallMoveFast = new SpriteAnimation( PlayerAnimations.FALL_MOVE_FAST.ordinal(), 1 );
            animFallMoveFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_move_fast_01 ),
                                                           Utils.framesToTime( 24, 12 ),
                                                           pressAndCollectVolume,
                                                           basicVulnerabilityVolume ) );

            SpriteAnimation animFallFast = new SpriteAnimation( PlayerAnimations.FALL_FAST.ordinal(), 1 );
            animFallFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_fast_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            FixedSizeArray<CollisionVolume> stompAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
            stompAttackVolume.add( new AABoxCollisionVolume( 16, -5.0f, 32, 37, HitType.HIT ) );
            stompAttackVolume.add( pressCollisionVolume );
            stompAttackVolume.add( collectionVolume );

            SpriteAnimation animAttackStomp = new SpriteAnimation( PlayerAnimations.ATTACK.ordinal(), 1 );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_01 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );

            FixedSizeArray<CollisionVolume> dashAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
            dashAttackVolume.add( new AABoxCollisionVolume( 16, 0.0f, 32, 64, HitType.HIT ) );
            dashAttackVolume.add( pressCollisionVolume );
            dashAttackVolume.add( collectionVolume );

            SpriteAnimation animAttackDash = new SpriteAnimation( PlayerAnimations.ATTACK_ALTERNATE.ordinal(), 1 );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_01 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );

            SpriteAnimation animHit = new SpriteAnimation( PlayerAnimations.HIT_REACT.ordinal(), 1 );
            animHit.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_hit_01 ),
                                                  Utils.framesToTime( 24, 6 ),
                                                  pressAndCollectVolume,
                                                  null ) );

            SpriteAnimation animDeath = new SpriteAnimation( PlayerAnimations.DEATH.ordinal(), 1 );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_01 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );

            SpriteAnimation animFrozen = new SpriteAnimation( PlayerAnimations.FROZEN.ordinal(), 1 );
            // Frozen has no frames!

//            UpdateRecordHudImpl record = ( UpdateRecordHudImpl )allocateComponent( UpdateRecordHudImpl.class );
            GameComponent record = allocateComponent( UpdateRecordHudImpl.class );

            // Save static data
            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );
            staticData.add( record );

            staticData.add( animIdle );
            staticData.add( animWalk );
            staticData.add( animRun );
            staticData.add( animJumpIdle );
            staticData.add( animJumpMove );
            staticData.add( animJumpMoveFast );
            staticData.add( animFallIdle );
            staticData.add( animFallMove );
            staticData.add( animFallMoveFast );
            staticData.add( animFallFast );
            staticData.add( animAttackStomp );
            staticData.add( animAttackDash );
            staticData.add( animHit );
            staticData.add( animDeath );
            staticData.add( animFrozen );

            setStaticData( GameObjectType.PLAYER2, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.PLAYER );
        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgcollision.setSize( 32, 48 );
        bgcollision.setOffset( 16, 0 );

        PlayerComponentSideVehicle player = ( PlayerComponentSideVehicle )allocateComponent( PlayerComponentSideVehicle.class );

        AnimationComponentSideImpl animation = ( AnimationComponentSideImpl )allocateComponent( AnimationComponentSideImpl.class );
        animation.setPlayer( player );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            animation.setLandThump( sound.load( R.raw.thump ) );
            animation.setDeathSound( sound.load( R.raw.sound_explode ) );
        }

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );
        animation.setSprite( sprite );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setBounceOnTakeHit( true );
        hitReact.setPauseOnAttack( true );
        hitReact.setInvincibleTime( 3.0f );
        hitReact.setSpawnObjectOnDealHit( HitType.HIT, GameObjectType.CRUSH_FLASH.ordinal(), false, true );

        if ( sound != null )
        {
            hitReact.setSoundOnTakeHit( HitType.HIT, sound.load( R.raw.deep_clang ) );
        }

        dynamicCollision.setHitReactionComponent( hitReact );
        player.setHitReactionComponent( hitReact );

        InventoryComponent inventory = ( InventoryComponent )allocateComponent( InventoryComponent.class );

        // find parentObject inventory's record
        UpdateRecordHudImpl parentObjectRecord = null;
        final FixedSizeArray<BaseObject> parentObjects = parentObject.getObjects();
        final int count = parentObjects.getCount();
        for ( int y = 0; y < count; y++ )
        {
            final BaseObject entry = parentObjects.get( y );
            if ( entry != null )
            {
                if ( entry instanceof InventoryComponent )
                {
                    parentObjectRecord = ( UpdateRecordHudImpl )( ( InventoryComponent )entry ).getRecord();
                    break;
                }
            }
        }

        // TODO: this is pretty dumb.  The static data binding needs to be made generic.
        final int staticDataSize = staticData.getCount();
        for ( int x = 0; x < staticDataSize; x++ )
        {
            final BaseObject entry = staticData.get( x );
            if ( entry instanceof UpdateRecordHudImpl )
            {
                final UpdateRecordHudImpl record = ( UpdateRecordHudImpl )entry;
                if ( parentObjectRecord != null )
                {
                    record.copy( parentObjectRecord );
                }
                inventory.setInventory( record );
                player.setInventory( inventory );
//                animation.setInventory( inventory );
                hitReact.setInventoryUpdate( record );
                break;
            }
        }

        TakeControlComponent controller = ( TakeControlComponent )allocateComponent( TakeControlComponent.class );
        if ( time == 0.0f )
        {
//            final EventRecorderImpl evtRec = ( EventRecorderImpl )sSystemRegistry.eventRecorder;
//            controller.setLifeTime( evtRec.getDifficultyConstants().getPLAYER2????LifeTime() );
        }
        else
        {
            controller.setLifeTime( time ); // don't set it if wan't unlimited time
        }
        controller.setControlledObject( parentObject );
        player.setTakeControlComponent( controller );
        if ( inventory != null )
        {
            controller.setInventory( inventory );
        }

//        ChangeComponentsComponent damageSwap = ( ChangeComponentsComponent )allocateComponent( ChangeComponentsComponent.class );
//        animation.setDamageSwap( damageSwap );

// !!!! ???? TODO: should add a LifeTime component to spawn objects & sound at end ? ???? !!!!
        LifetimeComponent lifetime = (LifetimeComponent)allocateComponent(LifetimeComponent.class);
        lifetime.addObjectToSpawnOnDeath( GameObjectType.EXPLOSION_GIANT.ordinal() );

// !!!! TODO: depending on type of object !!!!
// => eg: vehicle has its own life, "modified character" has the same life as player (maybe full life ?)
//        final EventRecorderImpl evtRec = ( EventRecorderImpl )sSystemRegistry.eventRecorder;
//        final int lifeMax = evtRec.getDifficultyConstants().getPLAYER2????LifeTime();
//        player.setLifeMax( lifeMax );
//        object.life = lifeMax;
        object.life = parentObject.life;
//?        controller.setShareLife( true );

        object.team = Team.PLAYER;

        object.add( player );
        if ( inventory != null )
        {
            object.add( inventory );
        }
        object.add( bgcollision );
        object.add( render );
        object.add( animation );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitReact );
        object.add( lifetime );
        object.add( controller );

        addStaticData( GameObjectType.PLAYER2, object, sprite );

        sprite.playAnimation( PlayerAnimations.IDLE.ordinal() );

// !!!! ???? TODO : want this ? ???? !!!!
//=> Jets ?

// !!!! ???? TODO : want this ? ???? !!!!
//=> Sparks ?

// !!!! ???? TODO : want this ? ???? !!!!
//=> Glow ?

// !!!! TODO: set facing direction !!!!

        final CameraSystem camera = sSystemRegistry.cameraSystem;
        if ( camera != null )
        {
            camera.setTarget( object );
        }

        object.commitUpdates();

        return object;
    }

    public GameObject spawnPickupVehicle1( float positionX, float positionY, int life )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        final int value = GameObjectType.VEHICLE_1.ordinal();

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
// !!!! TODO: change that !!!!
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PICKUP_VEHICLE_1 );
        if ( staticData == null )
        {
            final int staticObjectCount = 3;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );

// !!!! TODO: change that !!!!
            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.COLLECT );

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_01 ),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_03 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_coin_05 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.setLoop( true );

            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( idle );

            setStaticData( GameObjectType.PICKUP_VEHICLE_1, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        BackgroundCollisionComponent bgCollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgCollision.setSize( 32, 16 );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );
// !!!! ???? TODO: need to add "action" to "object" ? ???? !!!!

        dynamicCollision.setHitReactionComponent( hitReact );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
// !!!! TODO: change that !!!!
            hitReact.setSoundOnTakeHit( HitType.COLLECT, sound.load( R.raw.object_pickup_coin ) );
        }

        UpdateRecordImpl controllerData = ( UpdateRecordImpl )allocateComponent( UpdateRecordImpl.class );
        controllerData.mControllerId = value; // these values don't change so can be set here
        controllerData.mControllerTime = 0.0f;
        if ( life > 0 )
        {
        	controllerData.mControllerLife = life;
        }
        else
        {
//            final EventRecorderImpl evtRec = ( EventRecorderImpl )sSystemRegistry.eventRecorder;
//            controllerData.mControllerLife = evtRec.getDifficultyConstants().getVEHICLE1????MaxLife();
        	controllerData.mControllerLife = 5;
        }
        hitReact.setInventoryUpdate( controllerData );

        LifetimeComponent lifeTime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
//        lifeTime.setIncrementEventCounter( EventRecorderImpl.COUNTER_COINS_COLLECTED, value );

        object.life = 1;

        object.add( render );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( bgCollision );
        object.add( hitPlayer );
        object.add( hitReact );
        object.add( lifeTime );
        object.add( controllerData );

        addStaticData( GameObjectType.PICKUP_VEHICLE_1, object, sprite );
        sprite.playAnimation( 0 );

//        EventRecorder recorder = sSystemRegistry.eventRecorder;
//        recorder.incrementEventCounter( EventRecorderImpl.COUNTER_COINS_TOTAL, value );

        object.commitUpdates();

        return object;
    }

    public GameObject spawnVehicle1( float positionX, float positionY, GameObject parentObject, int life )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 64;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.VEHICLE_1 );

        if ( staticData == null )
        {
            final int staticObjectCount = 19;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );
            PhysicsComponent physics = ( PhysicsComponent )allocateComponent( PhysicsComponent.class );

// !!!! TODO : adapt values ... !!!!
// => make it less "bouncy" ... (done here ?)
//            physics.setMass( 9.1f ); // ~90kg w/ earth gravity
            physics.setMass( 5.0f ); // ~10kg w/ earth gravity
            physics.setDynamicFrictionCoeffecient( 0.2f );
            physics.setStaticFrictionCoeffecient( 0.01f );

            // Animation Data
            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 16, 32, 32 ) );

            FixedSizeArray<CollisionVolume> pressAndCollectVolume = new FixedSizeArray<CollisionVolume>( 2 );
            AABoxCollisionVolume collectionVolume = new AABoxCollisionVolume( 16, 0, 32, 48 );
            collectionVolume.setHitType( HitType.COLLECT );
            pressAndCollectVolume.add( collectionVolume );
            AABoxCollisionVolume pressCollisionVolume = new AABoxCollisionVolume( 16, 0, 32, 16 );
            pressCollisionVolume.setHitType( HitType.DEPRESS );
            pressAndCollectVolume.add( pressCollisionVolume );

            SpriteAnimation animIdle = new SpriteAnimation( PlayerAnimations.IDLE.ordinal(), 1 );
            final AnimationFrame frameIdle1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stand_01 ),
                                                                  Utils.framesToTime( 24, 30 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            animIdle.addFrame( frameIdle1 );

            SpriteAnimation animWalk = new SpriteAnimation( PlayerAnimations.MOVE.ordinal(), 1 );
            final AnimationFrame frameWalk1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_walk_01 ),
                                                                  Utils.framesToTime( 24, 6 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            animWalk.addFrame( frameWalk1 );

            SpriteAnimation animRun = new SpriteAnimation( PlayerAnimations.MOVE_FAST.ordinal(), 1 );
            final AnimationFrame frameRun1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_run_01 ),
                                                                 Utils.framesToTime( 24, 6 ),
                                                                 pressAndCollectVolume,
                                                                 basicVulnerabilityVolume );
            animRun.addFrame( frameRun1 );

            SpriteAnimation animJumpIdle = new SpriteAnimation( PlayerAnimations.JUMP_IDLE.ordinal(), 1 );
            animJumpIdle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_idle_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            SpriteAnimation animJumpMove = new SpriteAnimation( PlayerAnimations.JUMP_MOVE.ordinal(), 1 );
            animJumpMove.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_move_01 ),
                                                     Utils.framesToTime( 24, 12 ),
                                                     pressAndCollectVolume,
                                                     basicVulnerabilityVolume ) );

            SpriteAnimation animJumpMoveFast = new SpriteAnimation( PlayerAnimations.JUMP_MOVE_FAST.ordinal(), 1 );
            animJumpMoveFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jump_move_fast_01 ),
                                                           Utils.framesToTime( 24, 12 ),
                                                           pressAndCollectVolume,
                                                           basicVulnerabilityVolume ) );

            SpriteAnimation animFallIdle = new SpriteAnimation( PlayerAnimations.FALL_IDLE.ordinal(), 1 );
            animFallIdle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_idle_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            SpriteAnimation animFallMove = new SpriteAnimation( PlayerAnimations.FALL_MOVE.ordinal(), 1 );
            animFallMove.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_move_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            SpriteAnimation animFallMoveFast = new SpriteAnimation( PlayerAnimations.FALL_MOVE_FAST.ordinal(), 1 );
            animFallMoveFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_move_fast_01 ),
                                                           Utils.framesToTime( 24, 12 ),
                                                           pressAndCollectVolume,
                                                           basicVulnerabilityVolume ) );

            SpriteAnimation animFallFast = new SpriteAnimation( PlayerAnimations.FALL_FAST.ordinal(), 1 );
            animFallFast.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_fall_fast_01 ),
                                                       Utils.framesToTime( 24, 12 ),
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            FixedSizeArray<CollisionVolume> stompAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
            stompAttackVolume.add( new AABoxCollisionVolume( 16, -5.0f, 32, 37, HitType.HIT ) );
            stompAttackVolume.add( pressCollisionVolume );
            stompAttackVolume.add( collectionVolume );

            SpriteAnimation animAttackStomp = new SpriteAnimation( PlayerAnimations.ATTACK.ordinal(), 1 );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_01 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );

            FixedSizeArray<CollisionVolume> dashAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
            dashAttackVolume.add( new AABoxCollisionVolume( 16, 0.0f, 32, 64, HitType.HIT ) );
            dashAttackVolume.add( pressCollisionVolume );
            dashAttackVolume.add( collectionVolume );

            SpriteAnimation animAttackDash = new SpriteAnimation( PlayerAnimations.ATTACK_ALTERNATE.ordinal(), 1 );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_01 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );

            SpriteAnimation animHit = new SpriteAnimation( PlayerAnimations.HIT_REACT.ordinal(), 1 );
            animHit.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_hit_01 ),
                                                  Utils.framesToTime( 24, 6 ),
                                                  pressAndCollectVolume,
                                                  null ) );

            SpriteAnimation animDeath = new SpriteAnimation( PlayerAnimations.DEATH.ordinal(), 1 );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die_01 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );

            SpriteAnimation animFrozen = new SpriteAnimation( PlayerAnimations.FROZEN.ordinal(), 1 );
            // Frozen has no frames!

//            UpdateRecordHudImpl record = ( UpdateRecordHudImpl )allocateComponent( UpdateRecordHudImpl.class );
            GameComponent record = allocateComponent( UpdateRecordHudImpl.class );

            // Save static data
            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );
            staticData.add( record );

            staticData.add( animIdle );
            staticData.add( animWalk );
            staticData.add( animRun );
            staticData.add( animJumpIdle );
            staticData.add( animJumpMove );
            staticData.add( animJumpMoveFast );
            staticData.add( animFallIdle );
            staticData.add( animFallMove );
            staticData.add( animFallMoveFast );
            staticData.add( animFallFast );
            staticData.add( animAttackStomp );
            staticData.add( animAttackDash );
            staticData.add( animHit );
            staticData.add( animDeath );
            staticData.add( animFrozen );

            setStaticData( GameObjectType.VEHICLE_1, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.PLAYER );
        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgcollision.setSize( 32, 48 );
        bgcollision.setOffset( 16, 0 );

        PlayerComponentSideVehicle player = ( PlayerComponentSideVehicle )allocateComponent( PlayerComponentSideVehicle.class );
        AnimationComponentSideImpl animation = ( AnimationComponentSideImpl )allocateComponent( AnimationComponentSideImpl.class );
        animation.setPlayer( player );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            animation.setLandThump( sound.load( R.raw.thump ) );
            animation.setDeathSound( sound.load( R.raw.sound_explode ) );
        }

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );
        animation.setSprite( sprite );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setBounceOnTakeHit( true );
        hitReact.setPauseOnAttack( true );
        hitReact.setInvincibleTime( 3.0f );
        hitReact.setSpawnObjectOnDealHit( HitType.HIT, GameObjectType.CRUSH_FLASH.ordinal(), false, true );

        if ( sound != null )
        {
            hitReact.setSoundOnTakeHit( HitType.HIT, sound.load( R.raw.deep_clang ) );
        }

        dynamicCollision.setHitReactionComponent( hitReact );
        player.setHitReactionComponent( hitReact );

        InventoryComponent inventory = ( InventoryComponent )allocateComponent( InventoryComponent.class );
        // find parentObject inventory's record
        UpdateRecordHudImpl parentObjectRecord = null;
        final FixedSizeArray<BaseObject> parentObjects = parentObject.getObjects();
        final int count = parentObjects.getCount();
        for ( int y = 0; y < count; y++ )
        {
            final BaseObject entry = parentObjects.get( y );
            if ( entry != null )
            {
                if ( entry instanceof InventoryComponent )
                {
                    parentObjectRecord = ( UpdateRecordHudImpl )( ( InventoryComponent )entry ).getRecord();
                    break;
                }
            }
        }

        // TODO: this is pretty dumb.  The static data binding needs to be made generic.
        final int staticDataSize = staticData.getCount();
        for ( int x = 0; x < staticDataSize; x++ )
        {
            final BaseObject entry = staticData.get( x );
            if ( entry instanceof UpdateRecordHudImpl )
            {
                final UpdateRecordHudImpl record = ( UpdateRecordHudImpl )entry;
                if ( parentObjectRecord != null )
                {
                    record.copy( parentObjectRecord );
                }
                inventory.setInventory( record );
                player.setInventory( inventory );
                hitReact.setInventoryUpdate( record );
                break;
            }
        }

        TakeControlComponent controller = ( TakeControlComponent )allocateComponent( TakeControlComponent.class );
        controller.setControlledObject( parentObject );
        player.setTakeControlComponent( controller );
        if ( inventory != null )
        {
            controller.setInventory( inventory );
        }
        controller.setObjectToSpawnOnRelease( GameObjectType.PICKUP_VEHICLE_1.ordinal() );

//        ChangeComponentsComponent damageSwap = ( ChangeComponentsComponent )allocateComponent( ChangeComponentsComponent.class );
//        animation.setDamageSwap( damageSwap );

// !!!! ???? TODO: should add a LifeTime component to spawn objects & sound at end ? ???? !!!!
//        ...
        LifetimeComponent lifeTime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );

//        final EventRecorderImpl evtRec = ( EventRecorderImpl )sSystemRegistry.eventRecorder;
//        final int lifeMax = evtRec.getDifficultyConstants().getVEHICLE1????MaxLife();
        final int lifeMax = 5;
        player.setLifeMax( lifeMax );
        if ( life == 0 )
        {
            object.life = lifeMax;
        }
        else
        {
            object.life = life;
        }

        object.team = Team.PLAYER;

        object.add( player );
        object.add( inventory );
        object.add( bgcollision );
        object.add( render );
        object.add( animation );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitReact );
        object.add( lifeTime );
        object.add( controller );

        addStaticData( GameObjectType.VEHICLE_1, object, sprite );

        sprite.playAnimation( PlayerAnimations.IDLE.ordinal() );

// !!!! ???? TODO : want this ? ???? !!!!
//=> Jets ?

// !!!! ???? TODO : want this ? ???? !!!!
//=> Sparks ?

// !!!! ???? TODO : want this ? ???? !!!!
//=> Glow ?

// !!!! TODO: set facing direction !!!!

        CameraSystem camera = sSystemRegistry.cameraSystem;
        if ( camera != null )
        {
            camera.setTarget( object );
        }

        object.commitUpdates();

        return object;
    }

//////// VEHICLE 20140902 - END

    public GameObject spawnPickupSpeedUp( float positionX, float positionY )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PICKUP_SPEEDUP );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

//            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = null;
            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.COLLECT );

            SpriteAnimation idleAnim = new SpriteAnimation( 0, 4 );
            idleAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_speedup_01 ),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idleAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_speedup_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idleAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_speedup_03 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idleAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_speedup_04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idleAnim.setLoop( true );

            UpdateRecordImpl addBonus = ( UpdateRecordImpl )allocateComponent( UpdateRecordImpl.class );
            addBonus.mSpeedUpCount = 1;

            staticData.add( addBonus );
            staticData.add( idleAnim );

            setStaticData( GameObjectType.PICKUP_SPEEDUP, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            hitReact.setSoundOnTakeHit( HitType.COLLECT, sound.load( R.raw.object_pickup_speedup ) );
        }

        // TODO: this is pretty dumb.  The static data binding needs to be made generic.
        final int staticDataSize = staticData.getCount();
        for ( int x = 0; x < staticDataSize; x++ )
        {
            final BaseObject entry = staticData.get( x );
            if ( entry instanceof UpdateRecordImpl )
            {
                hitReact.setInventoryUpdate( ( UpdateRecordImpl )entry );
                break;
            }
        }

        dynamicCollision.setHitReactionComponent( hitReact );

        LifetimeComponent life = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
        life.setIncrementEventCounter( EventRecorderImpl.COUNTER_SPEEDUP, 1 );

        object.life = 1;

        object.add( render );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitPlayer );
        object.add( hitReact );
        object.add( life );

        addStaticData( GameObjectType.PICKUP_SPEEDUP, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }
//////// SPEEDUP - END

/*
// Sparks are used by more than one enemy type, so the setup for them is abstracted.
private void setupEnemySparks() {
FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.ENEMY_SPARKS);
if (staticData == null) {
staticData = new FixedSizeArray<BaseObject>(1);
TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

SpriteAnimation sparksAnim = new SpriteAnimation(0, 13);
AnimationFrame frame1 = 
new AnimationFrame(textureLibrary.allocateTexture(R.drawable.spark01), 
    Utils.framesToTime(24, 1));
AnimationFrame frame2 = 
new AnimationFrame(textureLibrary.allocateTexture(R.drawable.spark02), 
    Utils.framesToTime(24, 1));
AnimationFrame frame3 = 
new AnimationFrame(textureLibrary.allocateTexture(R.drawable.spark03), 
    Utils.framesToTime(24, 1));
sparksAnim.addFrame(frame1);
sparksAnim.addFrame(frame2);
sparksAnim.addFrame(frame3);
sparksAnim.addFrame(frame1);
sparksAnim.addFrame(frame2);
sparksAnim.addFrame(frame3);
sparksAnim.addFrame(frame1);
sparksAnim.addFrame(frame2);
sparksAnim.addFrame(frame3);
sparksAnim.addFrame(frame1);
sparksAnim.addFrame(frame2);
sparksAnim.addFrame(frame3);
sparksAnim.addFrame(new AnimationFrame(null, 3.0f));
sparksAnim.setLoop(true);

staticData.add(sparksAnim);
setStaticData(GameObjectType.ENEMY_SPARKS, staticData);
}

}
*/

/*
 // !!!! TODO : change !!!!
    public GameObject spawnEnemyBrobot( float positionX, float positionY, boolean flipHorizontal )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mNormalActivationRadius;
        object.width = 64;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.BROBOT );
        if ( staticData == null )
        {
            final int staticObjectCount = 5;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );
//////// BALLS - BEGIN
//            SimplePhysicsComponent physics = ( SimplePhysicsComponent )allocateComponent( SimplePhysicsComponent.class );
//            physics.setBounciness( 0.4f );
//////// BALLS - MID
            BallPhysicsComponent physics = ( BallPhysicsComponent )allocateComponent( BallPhysicsComponent.class );
            physics.setMaxSpeed( 200.0f );
            physics.setMinSpeed( 100.0f );
            physics.setBounciness( 1.0f );
//////// BALLS - END

            // Animations
            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 16, 32, 32 ) );

            FixedSizeArray<CollisionVolume> basicAttackVolume = new FixedSizeArray<CollisionVolume>( 2 );
            basicAttackVolume.add( new SphereCollisionVolume( 16, 32, 32, HitType.HIT ) );
// !!!! TODO: if want "depress" take care of collision volumes !!!!
// => like this, won't get hit if enemy falls on player ...
//            basicAttackVolume.add( new AABoxCollisionVolume( 16, 0, 32, 48, HitType.HIT ) );
//            basicAttackVolume.add( new AABoxCollisionVolume( 16, 1, 32, 16, HitType.DEPRESS ) );

            SpriteAnimation idle = new SpriteAnimation( EnemyAnimations.IDLE.ordinal(), 4 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_brobot_idle01 ),
                                               Utils.framesToTime( 24, 3 ),
                                               basicAttackVolume,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_brobot_idle02 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_brobot_idle03 ),
                                               Utils.framesToTime( 24, 3 ),
                                               basicAttackVolume,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_brobot_idle02 ),
                                               Utils.framesToTime( 24, 3 ),
                                               basicAttackVolume,
                                               basicVulnerabilityVolume ) );
            idle.setLoop( true );

            SpriteAnimation walk = new SpriteAnimation( EnemyAnimations.MOVE.ordinal(), 3 );
            walk.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_brobot_walk01 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               basicVulnerabilityVolume ) );
            walk.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_brobot_walk02 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               basicVulnerabilityVolume ) );
            walk.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_brobot_walk03 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               basicVulnerabilityVolume ) );
            walk.setLoop(true);

            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );
            staticData.add( idle );
            staticData.add( walk );

            setStaticData( GameObjectType.BROBOT, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_ENEMY );

        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgcollision.setSize( 32, 48 );
        bgcollision.setOffset( 16, 0 );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        EnemyAnimationComponent animation = ( EnemyAnimationComponent )allocateComponent( EnemyAnimationComponent.class );
        animation.setSprite( sprite );

        PatrolComponent patrol = ( PatrolComponent )allocateComponent( PatrolComponent.class );
        patrol.setMovementSpeed( 50.0f, 1000.0f );
//PatrolComponentTop patrol = ( PatrolComponentTop )allocateComponent( PatrolComponentTop.class );
//patrol.setMovement( 10.0f, -100.0f, 750.0f, 750.0f );

        DynamicCollisionComponent collision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( collision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        collision.setHitReactionComponent( hitReact );
//////// STRENGTH - MID
        hitReact.setBounceOnTakeHit( true );
        hitReact.setBounceOnTakeMagnitude( 500.0f );
        hitReact.setInvincibleTime( 1.0f );
//        hitReact.setSpawnOnDealHit( HitType.HIT, GameObjectType.CRUSH_FLASH.ordinal(), false, true );
//////// STRENGTH - END


        LifetimeComponent lifetime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
        lifetime.addObjectToSpawnOnDeath( GameObjectType.EXPLOSION_GIANT.ordinal() );
        lifetime.setVulnerableToDeathTiles( true );
        lifetime.setIncrementEventCounter( EventRecorderImpl.COUNTER_ENEMIES_KILLED, 1 );

// !!!! TODO : check if want to keep this !!!!
// => and what it does exactly !
//        GhostComponent ghost = ( GhostComponent )allocateComponent( GhostComponent.class );
//        ghost.setMovementSpeed( 500.0f );
//        ghost.setAcceleration( 1000.0f );
//        ghost.setJumpImpulse( 300.0f );
//        ghost.setKillOnRelease( true );
//        ghost.setDelayOnRelease( 1.5f );
//
//        SoundSystem sound = BaseObject.sSystemRegistry.soundSystem;
//        if ( sound != null )
//        {
//            ghost.setAmbientSound( sound.load( R.raw.sound_possession ) );
//        }
//
//        ChangeComponentsComponent ghostSwap = ( ChangeComponentsComponent )allocateComponent( ChangeComponentsComponent.class );
//        ghostSwap.addSwapInComponent( ghost );
//        ghostSwap.addSwapOutComponent( patrol );
//
//        SimplePhysicsComponent ghostPhysics = ( SimplePhysicsComponent )allocateComponent( SimplePhysicsComponent.class );
//        ghostPhysics.setBounciness( 0.0f );

        object.add( render);
        object.add( sprite);

        object.add( bgcollision );
        object.add( animation );
        object.add( patrol );
        object.add( collision );
        object.add( hitReact );
        object.add( lifetime );
// !!!! TODO : check if want to keep this !!!!
//        object.add( ghostSwap );

        object.team = Team.ENEMY;
//////// STRENGTH - MID
//        object.life = 3;
        object.strength = 2;
//////// STRENGTH - END

        if ( flipHorizontal )
        {
            object.facingDirection.x = -1.0f;
        }

        addStaticData( GameObjectType.BROBOT, object, sprite );

        object.commitUpdates();

// !!!! TODO : check if want to keep this !!!!
//        SimplePhysicsComponent normalPhysics = object.findByClass( SimplePhysicsComponent.class );
//        if ( normalPhysics != null )
//        {
//            ghostSwap.addSwapOutComponent( normalPhysics );
//        }
//        ghostSwap.addSwapInComponent( ghostPhysics );

        sprite.playAnimation( 0 );

// !!!! TODO : check if want to keep this !!!!
//        // Sparks
//        setupEnemySparks();
//
//        RenderComponent sparksRender = ( RenderComponent )allocateComponent( RenderComponent.class );
//        sparksRender.setPriority( render.getPriority() + 1 );
//        SpriteComponent sparksSprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
//        sparksSprite.setSize( 64, 64 );
//        sparksSprite.setRenderComponent( sparksRender );
//
//        addStaticData( GameObjectType.ENEMY_SPARKS, object, sparksSprite );
//
//        sparksSprite.playAnimation( 0 );
//
//        ghostSwap.addSwapInComponent( sparksSprite );
//        ghostSwap.addSwapInComponent( sparksRender );
//
//        hitReact.setPossessionComponent( ghostSwap );

        return object;
    }
*/



//////// dialogs - m
/*
// Example of objects starting a dialog when touched.
// The id of the dialog to display is specified in "setSpawnGameEventOnHit" as 3rd parameter
//  ( "conversation" position in xml file, starting from 0 ).
// Make sure that the corresponding dialog exists in the corresponding "xml/dialogs" file !
// !!!! If want the same object to use different dialogs:
//        the dialog index can be specified by using an additional HotSpot at the same position as the Object,
//        and adding a "SelectDialogComponent" to the object ( and setting its HitReaction component as the one of the object.
//        ( in that case, the index given to "setSpawnGameEventOnTakeHit" is ignored )
    public GameObject spawnKyleDead( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 128;
        object.height = 32;


        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.KYLE_DEAD );
        if ( staticData == null )
        {
            final int staticObjectCount = 1;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
//            basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
//            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.DIALOG );
            basicVulnerabilityVolume.add( new AABoxCollisionVolume( 32, 5, 64, 32, HitType.DIALOG ) );


            SpriteAnimation idle = new SpriteAnimation( 0, 1 );
            AnimationFrame frame1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.kyle_dead ),
                                                        Utils.framesToTime( 24, 1 ),
                                                        null,
                                                        basicVulnerabilityVolume );

            idle.addFrame( frame1 );
            idle.setLoop( true );

            staticData.add( idle );
            setStaticData( GameObjectType.KYLE_DEAD, staticData );
        }


        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        dynamicCollision.setHitReactionComponent( hitReact );
        hitReact.setGameEventOnTakeHit( HitType.DIALOG,
                                        GameFlowEvent.EVENT_SHOW_DIALOG_CHARACTER,
                                        0 );
                                        // 0 == HotSpotSystem.HotSpotType.NPC_SELECT_DIALOG - HotSpotSystem.HotSpotType.NPC_SELECT_DIALOG
        hitReact.setInvincible( true );

        // interaction with the player
        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.DIALOG, false );

        // so that object disappears once collision done
        LifetimeComponent life = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );

        object.life = 1;

// !!!! ???? TODO: only for hotspots ? ???? !!!!
        SelectDialogComponent dialogSelect = ( SelectDialogComponent )allocateComponent( SelectDialogComponent.class );
        dialogSelect.setHitReact( hitReact );

        // Since this object doesn't have gravity or background collision, adjust down to simulate the position
        // at which a bounding volume would rest.
        object.getPosition().y -= 5.0f;

        object.add( life );
        object.add( dialogSelect );
        object.add( render );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitReact );
        object.add( hitPlayer );

        addStaticData( GameObjectType.KYLE_DEAD, object, sprite );
        sprite.playAnimation( 0 );

        return object;
    }
*/
//////// dialogs - e


/*
public GameObject spawnDiary(float positionX, float positionY) {
TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

LevelSystem level = sSystemRegistry.levelSystem;
if (level != null) {
final LevelTree.Level currentLevel = level.getCurrentLevel();
if (currentLevel != null && currentLevel.diaryCollected) {
return null;
}
}

GameObject object = mGameObjectPool.allocate();
object.getPosition().set(positionX, positionY);
object.activationRadius = mTightActivationRadius;
object.width = 32;
object.height = 32;

FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.DIARY);
if (staticData == null) {
final int staticObjectCount = 2;
staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>(1);
basicVulnerabilityVolume.add(new SphereCollisionVolume(16, 16, 16));
basicVulnerabilityVolume.get(0).setHitType(HitType.COLLECT);

SpriteAnimation idle = new SpriteAnimation(0, 8);
AnimationFrame frame1 = new AnimationFrame(textureLibrary.allocateTexture(R.drawable.object_diary01), 
Utils.framesToTime(24, 2), null, basicVulnerabilityVolume);
AnimationFrame frame2 = new AnimationFrame(textureLibrary.allocateTexture(R.drawable.object_diary02), 
Utils.framesToTime(24, 2), null, basicVulnerabilityVolume);

idle.addFrame(new AnimationFrame(textureLibrary.allocateTexture(R.drawable.object_diary01), 
1.0f, null, basicVulnerabilityVolume));
idle.addFrame(frame2);
idle.addFrame(frame1);
idle.addFrame(frame2);
idle.addFrame(new AnimationFrame(textureLibrary.allocateTexture(R.drawable.object_diary03), 
Utils.framesToTime(24, 2), null, basicVulnerabilityVolume));
idle.addFrame(new AnimationFrame(textureLibrary.allocateTexture(R.drawable.object_diary04), 
Utils.framesToTime(24, 2), null, basicVulnerabilityVolume));
idle.addFrame(new AnimationFrame(textureLibrary.allocateTexture(R.drawable.object_diary05), 
Utils.framesToTime(24, 2), null, basicVulnerabilityVolume));
idle.addFrame(new AnimationFrame(textureLibrary.allocateTexture(R.drawable.object_diary06), 
Utils.framesToTime(24, 2), null, basicVulnerabilityVolume));

idle.setLoop(true);

//// BEGIN
//InventoryComponent.UpdateRecord addDiary = new InventoryComponent.UpdateRecord();
//addDiary.diaryCount = 1;
//// MID
// OK?
UpdateRecordImpl addDiary = new UpdateRecordImpl();
//addDiary.coinCount = 1;
//// END

staticData.add(addDiary);

staticData.add(idle);

setStaticData(GameObjectType.DIARY, staticData);
}

RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
render.setPriority(SortConstants.GENERAL_OBJECT);

SpriteComponent sprite = (SpriteComponent)allocateComponent(SpriteComponent.class);
sprite.setSize((int)object.width, (int)object.height);
sprite.setRenderComponent(render);

DynamicCollisionComponent dynamicCollision = (DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
sprite.setCollisionComponent(dynamicCollision);

HitReactionComponent hitReact = (HitReactionComponent)allocateComponent(HitReactionComponent.class);
hitReact.setDieWhenCollected(true);
hitReact.setInvincible(true);
hitReact.setSpawnGameEventOnHit(CollisionParameters.HitType.COLLECT, 
GameFlowEvent.EVENT_SHOW_DIARY, 0);
// TODO: this is pretty dumb.  The static data binding needs to be made generic.
final int staticDataSize = staticData.getCount();
for (int x = 0; x < staticDataSize; x++) {
final BaseObject entry = staticData.get(x);
//// BEGIN
//if (entry instanceof InventoryComponent.UpdateRecord) {
//hitReact.setInventoryUpdate((InventoryComponent.UpdateRecord)entry);
//break;
//}
//// MID
// OK?
if (entry instanceof UpdateRecordImpl ) {
hitReact.setInventoryUpdate((UpdateRecordImpl)entry);
break;
}
//// END
}

dynamicCollision.setHitReactionComponent(hitReact);

LifetimeComponent life = (LifetimeComponent)allocateComponent(LifetimeComponent.class);

object.life = 1;

object.add(render);
object.add(sprite);
object.add(dynamicCollision);
object.add(hitReact);
object.add(life);

addStaticData(GameObjectType.DIARY, object, sprite);
sprite.playAnimation(0);

return object;
}
*/

//////// DOOR - MID
    public GameObject spawnObjectDoor( float positionX, float positionY, GameObjectType type, boolean solid )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 32;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( type );
        if ( staticData == null )
        {
            final int staticObjectCount = 5;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            final int red_frames[] = { R.drawable.object_door_red01,
                                       R.drawable.object_door_red02,
                                       R.drawable.object_door_red03,
                                       R.drawable.object_door_red04 };

//            final int blue_frames[] = { R.drawable.object_door_blue01,
//                                        R.drawable.object_door_blue02,
//                                        R.drawable.object_door_blue03,
//                                        R.drawable.object_door_blue04 };
//
//            final int green_frames[] = { R.drawable.object_door_green01,
//                                         R.drawable.object_door_green02,
//                                         R.drawable.object_door_green03,
//                                         R.drawable.object_door_green04 };

            int frames[] = red_frames;
//            if ( type == GameObjectType.DOOR_GREEN )
//            {
//                frames = green_frames;
//            }
//            else if ( type == GameObjectType.DOOR_BLUE )
//            {
//                frames = blue_frames;
//            }

            FixedSizeArray<CollisionVolume> vulnerabilityVolume = null;

            AnimationFrame frame1 = new AnimationFrame( textureLibrary.allocateTexture( frames[ 0 ] ),
                                                        Utils.framesToTime( 24, 1 ),
                                                        null,
                                                        vulnerabilityVolume );
            AnimationFrame frame2 = new AnimationFrame( textureLibrary.allocateTexture( frames[ 1 ] ),
                                                        Utils.framesToTime( 24, 2 ) );
            AnimationFrame frame3 = new AnimationFrame( textureLibrary.allocateTexture( frames[ 2 ] ),
                                                        Utils.framesToTime( 24, 2 ) );
            AnimationFrame frame4 = new AnimationFrame( textureLibrary.allocateTexture( frames[ 3 ] ),
                                                        Utils.framesToTime( 24, 1 ) );

            // one frame of closing is deadly
            FixedSizeArray<CollisionVolume> attackVolume = new FixedSizeArray<CollisionVolume>( 1 );
            attackVolume.add( new AABoxCollisionVolume( 12, 8, 8, 56 ) );
            attackVolume.get( 0 ).setHitType( HitType.DEATH );

            AnimationFrame closeFrame2 = new AnimationFrame( textureLibrary.allocateTexture( frames[ 1 ] ),
                                                             Utils.framesToTime( 24, 2 ),
                                                             attackVolume,
                                                             vulnerabilityVolume );

            SpriteAnimation idle_closed = new SpriteAnimation( DoorAnimationComponent.Animation.CLOSED, 1 );
            idle_closed.addFrame( frame1 );

            SpriteAnimation idle_open = new SpriteAnimation( DoorAnimationComponent.Animation.OPEN, 1 );
            idle_open.addFrame( frame4 );

            SpriteAnimation open = new SpriteAnimation( DoorAnimationComponent.Animation.OPENING, 2 );
            open.addFrame( frame2 );
            open.addFrame( frame3 );

            SpriteAnimation close = new SpriteAnimation( DoorAnimationComponent.Animation.CLOSING, 2 );
            close.addFrame( frame3 );
            close.addFrame( closeFrame2 );


            SolidSurfaceComponent solidSurface = ( SolidSurfaceComponent )allocateComponent( SolidSurfaceComponent.class );
            solidSurface.inititalize( 4 );

            // box shape:
            // ___       ___1
            // | |      2| |3
            // ---       ---4
            Vector2 surface1Start = new Vector2( 0, object.height );
            Vector2 surface1End = new Vector2( object.width, object.height );
            Vector2 surface1Normal = new Vector2( 0.0f, -1.0f );
            surface1Normal.normalize();

            Vector2 surface2Start = new Vector2( 0, object.height );
            Vector2 surface2End = new Vector2( 0, 0 );
            Vector2 surface2Normal = new Vector2( -1.0f, 0.0f );
            surface2Normal.normalize();

            Vector2 surface3Start = new Vector2( object.width, object.height );
            Vector2 surface3End = new Vector2( object.width, 0 );
            Vector2 surface3Normal = new Vector2( 1.0f, 0 );

            Vector2 surface4Start = new Vector2( 0, 0 );
            Vector2 surface4End = new Vector2( object.width, 0 );
            Vector2 surface4Normal = new Vector2( 0, 1.0f );

            solidSurface.addSurface( surface1Start, surface1End, surface1Normal );
            solidSurface.addSurface( surface2Start, surface2End, surface2Normal );
            solidSurface.addSurface( surface3Start, surface3End, surface3Normal );
            solidSurface.addSurface( surface4Start, surface4End, surface4Normal );

            staticData.add( idle_open );
            staticData.add( idle_closed );
            staticData.add( open );
            staticData.add( close );
            staticData.add( solidSurface );
            setStaticData( type, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.FOREGROUND_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DoorAnimationComponent doorAnim = ( DoorAnimationComponent )allocateComponent( DoorAnimationComponent.class );
        doorAnim.setSprite( sprite );

        SoundSystem sound = BaseObject.sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            doorAnim.setSounds( sound.load( R.raw.sound_open ), sound.load( R.raw.sound_close ) );
        }

        ChannelSystem.Channel doorChannel = null;
        ChannelSystem channelSystem = BaseObject.sSystemRegistry.channelSystem;
        switch ( type )
        {
        case DOOR_RED:
            doorChannel = channelSystem.registerChannel( sRedButtonChannel );
            break;
//        case DOOR_BLUE:
//            doorChannel = channelSystem.registerChannel( sBlueButtonChannel );
//            break;
//        case DOOR_GREEN:
//            doorChannel = channelSystem.registerChannel( sGreenButtonChannel );
//            break;
        default:
            break;
        }
        doorAnim.setChannel( doorChannel );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        dynamicCollision.setHitReactionComponent( hitReact );

        object.add( render );
        object.add( sprite );
        object.add( doorAnim );
        object.add( dynamicCollision );
        object.add( hitReact );
        addStaticData( type, object, sprite );

        object.commitUpdates();

        SolidSurfaceComponent solidSurface = object.findByClass( SolidSurfaceComponent.class );
        if ( solid )
        {
            doorAnim.setSolidSurface( solidSurface );
        }
        else
        {
            object.remove( solidSurface );
            object.commitUpdates();
        }

        sprite.playAnimation( 0 );

        return object;
    }

    public GameObject spawnObjectButton( float positionX, float positionY, GameObjectType type )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 32;
        object.height = 32;

        FixedSizeArray<BaseObject> staticData = getStaticData( type );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            final int red_frames[] = { R.drawable.object_button_red,
                                       R.drawable.object_button_pressed_red };

//            final int blue_frames[] = { R.drawable.object_button_blue,
//                                        R.drawable.object_button_pressed_blue };
//
//            final int green_frames[] = { R.drawable.object_button_green,
//                                         R.drawable.object_button_pressed_green };

            int frames[] = red_frames;
//            if ( type == GameObjectType.BUTTON_GREEN )
//            {
//                frames = green_frames;
//            }
//            else if ( type == GameObjectType.BUTTON_BLUE )
//            {
//                frames = blue_frames;
//            }

            FixedSizeArray<CollisionVolume> vulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            vulnerabilityVolume.add( new AABoxCollisionVolume( 0, 0, 32, 16 ) );
            vulnerabilityVolume.get( 0 ).setHitType( HitType.DEPRESS );

            AnimationFrame frame1 = new AnimationFrame( textureLibrary.allocateTexture( frames[ 0 ] ),
                                                        Utils.framesToTime( 24, 1 ),
                                                        null,
                                                        vulnerabilityVolume );
            AnimationFrame frame2 = new AnimationFrame( textureLibrary.allocateTexture( frames[ 1 ] ),
                                                        Utils.framesToTime( 24, 1 ),
                                                        null,
                                                        vulnerabilityVolume );

            SpriteAnimation idle = new SpriteAnimation( ButtonAnimationComponent.Animation.UP, 1 );
            idle.addFrame( frame1 );

            SpriteAnimation pressed = new SpriteAnimation( ButtonAnimationComponent.Animation.DOWN, 1 );
            pressed.addFrame( frame2 );

            staticData.add( idle );
            staticData.add( pressed );

            setStaticData( type, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        ButtonAnimationComponent button = ( ButtonAnimationComponent )allocateComponent( ButtonAnimationComponent.class );
        button.setSprite( sprite );

        SoundSystem sound = BaseObject.sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            button.setDepressSound( sound.load( R.raw.sound_button ) );
        }

        ChannelSystem.Channel buttonChannel = null;
        ChannelSystem channelSystem = BaseObject.sSystemRegistry.channelSystem;
        switch ( type )
        {
        case BUTTON_RED:
            buttonChannel = channelSystem.registerChannel( sRedButtonChannel );
            break;
//        case BUTTON_BLUE:
//            buttonChannel = channelSystem.registerChannel( sBlueButtonChannel );
//            break;
//        case BUTTON_GREEN:
//            buttonChannel = channelSystem.registerChannel( sGreenButtonChannel );
//            break;
        default:
            break;
        }
        button.setChannel( buttonChannel );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        hitReact.setInvincible( false );

        dynamicCollision.setHitReactionComponent( hitReact );

        object.team = Team.NONE;

        object.add( render );
        object.add( sprite );
        object.add( button );
        object.add( dynamicCollision );
        object.add( hitReact );
        addStaticData( type, object, sprite );

        sprite.playAnimation( 0 );

        return object;
    }
//////// DOOR - END

    public GameObject spawnDust( float positionX, float positionY, boolean flipHorizontal )
    {
        TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 32;
        object.height = 32;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.DUST );
        if ( staticData == null )
        {
            final int staticObjectCount = 1;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.dust01 ),
                                               Utils.framesToTime( 24, 1 ) ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.dust02 ),
                                               Utils.framesToTime( 24, 1 ) ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.dust03 ),
                                               Utils.framesToTime( 24, 1 ) ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.dust04 ),
                                               Utils.framesToTime( 24, 1 ) ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.dust05 ),
                                               Utils.framesToTime( 24, 1 ) ) );
            staticData.add( idle );
            setStaticData( GameObjectType.DUST, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.EFFECT );

        LifetimeComponent lifetime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
        lifetime.setTimeUntilDeath( 0.30f );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        if ( flipHorizontal )
        {
            object.facingDirection.x = -1.0f;
        }
        object.destroyOnDeactivation = true;

        object.add( lifetime );
        object.add( render );
        object.add( sprite );

        addStaticData( GameObjectType.DUST, object, sprite );

        sprite.playAnimation( 0 );

        return object;
    }

/*
public GameObject spawnEffectExplosionSmall(float positionX, float positionY) {
TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

GameObject object = mGameObjectPool.allocate();
object.getPosition().set(positionX, positionY);
object.activationRadius = mAlwaysActive;
object.width = 32;
object.height = 32;

FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.EXPLOSION_SMALL);
if (staticData == null) {
final int staticObjectCount = 1;
staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

FixedSizeArray<CollisionVolume> basicAttackVolume = 
new FixedSizeArray<CollisionVolume>(1);
basicAttackVolume.add(new SphereCollisionVolume(16, 16, 16, HitType.HIT));

SpriteAnimation idle = new SpriteAnimation(0, 7);
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_small01), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_small02), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_small03), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_small04), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_small05), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_small06), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_small07), 
Utils.framesToTime(24, 1), basicAttackVolume, null));

staticData.add(idle);
setStaticData(GameObjectType.EXPLOSION_SMALL, staticData);
}

RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
render.setPriority(SortConstants.EFFECT);

SpriteComponent sprite = (SpriteComponent)allocateComponent(SpriteComponent.class);
sprite.setSize((int)object.width, (int)object.height);
sprite.setRenderComponent(render);


LifetimeComponent lifetime = (LifetimeComponent)allocateComponent(LifetimeComponent.class);

DynamicCollisionComponent dynamicCollision = (DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
sprite.setCollisionComponent(dynamicCollision);
  
object.add(dynamicCollision);
object.add(lifetime);
object.add(render);
object.add(sprite);

addStaticData(GameObjectType.EXPLOSION_SMALL, object, sprite);

final SpriteAnimation idle = sprite.findAnimation(0);
if (idle != null) {
lifetime.setTimeUntilDeath(idle.getLength());
}

sprite.playAnimation(0);

return object;
}
*/
/*
public GameObject spawnEffectExplosionLarge(float positionX, float positionY) {
TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

GameObject object = mGameObjectPool.allocate();
object.getPosition().set(positionX, positionY);
object.activationRadius = mAlwaysActive;
object.width = 64;
object.height = 64;

FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.EXPLOSION_LARGE);
if (staticData == null) {
final int staticObjectCount = 1;
staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

FixedSizeArray<CollisionVolume> basicAttackVolume = 
new FixedSizeArray<CollisionVolume>(1);
basicAttackVolume.add(new SphereCollisionVolume(32, 32, 32, HitType.HIT));

SpriteAnimation idle = new SpriteAnimation(0, 9);
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big01), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big02), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big03), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big04), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big05), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big06), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big07), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big08), 
Utils.framesToTime(24, 1), basicAttackVolume, null));
idle.addFrame(new AnimationFrame(
textureLibrary.getTextureByResource(R.drawable.effect_explosion_big09), 
Utils.framesToTime(24, 1), basicAttackVolume, null));


staticData.add(idle);            
setStaticData(GameObjectType.EXPLOSION_LARGE, staticData);
}

RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
render.setPriority(SortConstants.EFFECT);

SpriteComponent sprite = (SpriteComponent)allocateComponent(SpriteComponent.class);
sprite.setSize((int)object.width, (int)object.height);
sprite.setRenderComponent(render);


LifetimeComponent lifetime = (LifetimeComponent)allocateComponent(LifetimeComponent.class);

DynamicCollisionComponent dynamicCollision = 
(DynamicCollisionComponent)allocateComponent(DynamicCollisionComponent.class);
sprite.setCollisionComponent(dynamicCollision);

PlaySingleSoundComponent soundEffect = (PlaySingleSoundComponent)allocateComponent(PlaySingleSoundComponent.class);
soundEffect.setSound(sSystemRegistry.soundSystem.load(R.raw.quick_explosion));


object.add(soundEffect);            
object.add(dynamicCollision);
object.add(lifetime);
object.add(render);
object.add(sprite);

addStaticData(GameObjectType.EXPLOSION_LARGE, object, sprite);

final SpriteAnimation idle = sprite.findAnimation(0);
if (idle != null) {
lifetime.setTimeUntilDeath(idle.getLength());
}

sprite.playAnimation(0);

return object;
}
*/

    public GameObject spawnEffectExplosionGiant( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 64;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.EXPLOSION_GIANT );
        if ( staticData == null )
        {
            final int staticObjectCount = 4;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            FixedSizeArray<CollisionVolume> basicAttackVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicAttackVolume.add( new SphereCollisionVolume( 64, 32, 32, HitType.HIT ) );

            SpriteAnimation idle = new SpriteAnimation( 0, 9 );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big01 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big02 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big03 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big05 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big06 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big07 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big08 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_big09 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );


            AnimationFrame smallFrame1 = new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_small01 ),
                                                             Utils.framesToTime( 24, 1 ),
                                                             null,
                                                             null );
            AnimationFrame smallFrame2 = new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_small02 ),
                                                             Utils.framesToTime( 24, 1 ),
                                                             null,
                                                             null );
            AnimationFrame smallFrame3 = new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_small03 ),
                                                             Utils.framesToTime( 24, 1 ),
                                                             null,
                                                             null );
            AnimationFrame smallFrame4 = new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_small04 ),
                                                             Utils.framesToTime( 24, 1 ),
                                                             null,
                                                             null );
            AnimationFrame smallFrame5 = new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_small05 ),
                                                             Utils.framesToTime( 24, 1 ),
                                                             null,
                                                             null );
            AnimationFrame smallFrame6 = new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_small06 ),
                                                             Utils.framesToTime( 24, 1 ),
                                                             null,
                                                             null );
            AnimationFrame smallFrame7 = new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_explosion_small07 ),
                                                             Utils.framesToTime( 24, 1 ),
                                                             null,
                                                             null );

            SpriteAnimation smallBlast1 = new SpriteAnimation( 0, 7 );
            smallBlast1.addFrame( smallFrame1 );
            smallBlast1.addFrame( smallFrame2 );
            smallBlast1.addFrame( smallFrame3 );
            smallBlast1.addFrame( smallFrame4 );
            smallBlast1.addFrame( smallFrame5 );
            smallBlast1.addFrame( smallFrame6 );
            smallBlast1.addFrame( smallFrame7 );

            SpriteAnimation smallBlast2 = new SpriteAnimation( 0, 8 );
            smallBlast2.addFrame( new AnimationFrame( null, Utils.framesToTime( 24, 4 ), null, null ) );
            smallBlast2.addFrame( smallFrame1 );
            smallBlast2.addFrame( smallFrame2 );
            smallBlast2.addFrame( smallFrame3 );
            smallBlast2.addFrame( smallFrame4 );
            smallBlast2.addFrame( smallFrame5 );
            smallBlast2.addFrame( smallFrame6 );
            smallBlast2.addFrame( smallFrame7 );

            SpriteAnimation smallBlast3 = new SpriteAnimation( 0, 8 );
            smallBlast3.addFrame( new AnimationFrame( null, Utils.framesToTime( 24, 8 ), null, null ) );
            smallBlast3.addFrame( smallFrame1 );
            smallBlast3.addFrame( smallFrame2 );
            smallBlast3.addFrame( smallFrame3 );
            smallBlast3.addFrame( smallFrame4 );
            smallBlast3.addFrame( smallFrame5 );
            smallBlast3.addFrame( smallFrame6 );
            smallBlast3.addFrame( smallFrame7 );


            staticData.add( idle );
            staticData.add( smallBlast1 );
            staticData.add( smallBlast2 );
            staticData.add( smallBlast3 );

            setStaticData( GameObjectType.EXPLOSION_GIANT, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.EFFECT );
        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        // Hack.  Use static data differently for this object so we can share three animations
        // amongst three separate sprites.

        final SpriteAnimation idle = ( SpriteAnimation )staticData.get( 0 );
        final SpriteAnimation smallBlast1 = ( SpriteAnimation )staticData.get( 1 );
        final SpriteAnimation smallBlast2 = ( SpriteAnimation )staticData.get( 2 );
        final SpriteAnimation smallBlast3 = ( SpriteAnimation )staticData.get( 3 );

        sprite.addAnimation( idle );
        sprite.playAnimation( 0 );

        RenderComponent blast1Render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.EFFECT );
        SpriteComponent blast1Sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        blast1Sprite.setSize( 32, 32 );
        blast1Sprite.setRenderComponent( blast1Render );
        blast1Render.setDrawOffset( 40, 50 );
        blast1Sprite.addAnimation( smallBlast1 );
        blast1Sprite.playAnimation( 0 );

        RenderComponent blast2Render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.EFFECT );
        SpriteComponent blast2Sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        blast2Sprite.setSize( 32, 32 );
        blast2Sprite.setRenderComponent( blast2Render );
        blast2Render.setDrawOffset( -10, 0 );
        blast2Sprite.addAnimation( smallBlast2 );
        blast2Sprite.playAnimation( 0 );

        RenderComponent blast3Render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.EFFECT );
        SpriteComponent blast3Sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        blast3Sprite.setSize( 32, 32 );
        blast3Sprite.setRenderComponent( blast3Render );
        blast3Render.setDrawOffset( 0, 32 );
        blast3Sprite.addAnimation( smallBlast3 );
        blast3Sprite.playAnimation( 0 );

        LifetimeComponent lifetime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
        lifetime.setTimeUntilDeath( Math.max( Math.max( Math.max( idle.getLength(),
                                                                  smallBlast1.getLength() ),
                                                        smallBlast2.getLength()),
                                              smallBlast3.getLength() ) );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        PlaySingleSoundComponent soundEffect = ( PlaySingleSoundComponent )allocateComponent( PlaySingleSoundComponent.class );
        soundEffect.setSound( sSystemRegistry.soundSystem.load( R.raw.quick_explosion ) );

        object.team = Team.PLAYER; // Maybe this should be an argument to this function.

        object.add( dynamicCollision );
        object.add( lifetime );
        object.add( render );
        object.add( sprite );
        object.add( soundEffect );

        object.add( blast1Render );
        object.add( blast1Sprite );

        object.add( blast2Render );
        object.add( blast2Sprite );

        object.add( blast3Render );
        object.add( blast3Sprite );

        return object;
    }

/*
public GameObject spawnEffectSmokeBig(float positionX, float positionY) {
TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

GameObject object = null;
// This is just an effect, so we can live without it if our pools are exhausted.
if (componentAvailable(RenderComponent.class, 1)) { 
object = mGameObjectPool.allocate();

object.getPosition().set(positionX, positionY);
object.activationRadius = mTightActivationRadius;
object.width = 32;
object.height = 32;

FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.SMOKE_BIG);
if (staticData == null) {
final int staticObjectCount = 6;
staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

GameComponent movement = allocateComponent(MovementComponent.class);


AnimationFrame frame2 = new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big02), 
  Utils.framesToTime(24, 1), null, null);

AnimationFrame frame3 = new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big03), 
  Utils.framesToTime(24, 1), null, null);

AnimationFrame frame4 = new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big04), 
  Utils.framesToTime(24, 1), null, null);

AnimationFrame frame5 = new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big05), 
  Utils.framesToTime(24, 1), null, null);

SpriteAnimation idle = new SpriteAnimation(0, 5);
idle.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big01), 
  Utils.framesToTime(24, 10), null, null));
idle.addFrame(frame2);
idle.addFrame(frame3);
idle.addFrame(frame4);
idle.addFrame(frame5);

SpriteAnimation idle2 = new SpriteAnimation(1, 5);
idle2.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big01), 
  Utils.framesToTime(24, 13), null, null));
idle2.addFrame(frame2);
idle2.addFrame(frame3);
idle2.addFrame(frame4);
idle2.addFrame(frame5);

SpriteAnimation idle3 = new SpriteAnimation(2, 5);
idle3.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big01), 
  Utils.framesToTime(24, 8), null, null));
idle3.addFrame(frame2);
idle3.addFrame(frame3);
idle3.addFrame(frame4);
idle3.addFrame(frame5);

SpriteAnimation idle4 = new SpriteAnimation(3, 5);
idle4.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big01), 
  Utils.framesToTime(24, 5), null, null));
idle4.addFrame(frame2);
idle4.addFrame(frame3);
idle4.addFrame(frame4);
idle4.addFrame(frame5);

SpriteAnimation idle5 = new SpriteAnimation(4, 5);
idle5.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_big01), 
  Utils.framesToTime(24, 15), null, null));
idle5.addFrame(frame2);
idle5.addFrame(frame3);
idle5.addFrame(frame4);
idle5.addFrame(frame5);

staticData.add(idle);
staticData.add(idle2);
staticData.add(idle3);
staticData.add(idle4);
staticData.add(idle5);
staticData.add(movement);
setStaticData(GameObjectType.SMOKE_BIG, staticData);
}

RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
render.setPriority(SortConstants.EFFECT);

SpriteComponent sprite = (SpriteComponent)allocateComponent(SpriteComponent.class);
sprite.setSize((int)object.width, (int)object.height);
sprite.setRenderComponent(render);

LifetimeComponent lifetime = (LifetimeComponent)allocateComponent(LifetimeComponent.class);
lifetime.setDieWhenInvisible(true);
    
object.destroyOnDeactivation = true;

object.add(lifetime);
object.add(render);
object.add(sprite);

addStaticData(GameObjectType.SMOKE_BIG, object, sprite);

final int animIndex = (int)(Math.random() * sprite.getAnimationCount());
final SpriteAnimation idle = sprite.findAnimation(animIndex);
if (idle != null) {
lifetime.setTimeUntilDeath(idle.getLength());
sprite.playAnimation(animIndex);
}


}
return object;
}
*/
/*
public GameObject spawnEffectSmokeSmall(float positionX, float positionY) {
TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

GameObject object = null;
// This is just an effect, so we can live without it if our pools are exhausted.
if (componentAvailable(RenderComponent.class, 1)) {
object = mGameObjectPool.allocate();
object.getPosition().set(positionX, positionY);
object.activationRadius = mAlwaysActive;
object.width = 16;
object.height = 16;

FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.SMOKE_SMALL);
if (staticData == null) {
final int staticObjectCount = 2;
staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

GameComponent movement = allocateComponent(MovementComponent.class);

SpriteAnimation idle = new SpriteAnimation(0, 5);
idle.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_small01), 
  Utils.framesToTime(24, 10), null, null));
idle.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_small02), 
  Utils.framesToTime(24, 1), null, null));
idle.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_small03), 
  Utils.framesToTime(24, 1), null, null));
idle.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_small04), 
  Utils.framesToTime(24, 1), null, null));
idle.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_smoke_small05), 
  Utils.framesToTime(24, 1), null, null));

staticData.add(idle);
staticData.add(movement);
setStaticData(GameObjectType.SMOKE_SMALL, staticData);
}

RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
render.setPriority(SortConstants.EFFECT);

SpriteComponent sprite = (SpriteComponent)allocateComponent(SpriteComponent.class);
sprite.setSize((int)object.width, (int)object.height);
sprite.setRenderComponent(render);

LifetimeComponent lifetime = (LifetimeComponent)allocateComponent(LifetimeComponent.class);
lifetime.setDieWhenInvisible(true);
    
object.destroyOnDeactivation = true;

object.add(lifetime);
object.add(render);
object.add(sprite);

addStaticData(GameObjectType.SMOKE_SMALL, object, sprite);

final SpriteAnimation idle = sprite.findAnimation(0);
if (idle != null) {
lifetime.setTimeUntilDeath(idle.getLength());
}

sprite.playAnimation(0);
}

return object;
}
*/

// !!!! TODO : rename stuff and change images !!!!
    public GameObject spawnEffectCrushFlash( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

        GameObject object = null;
        // This is just an effect, so we can live without it if our pools are exhausted.
        if ( componentAvailable( RenderComponent.class, 1 ) )
        {
            object = mGameObjectPool.allocate();
            object.getPosition().set( positionX, positionY );
            object.activationRadius = mAlwaysActive;
            object.width = 64;
            object.height = 64;

            FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.CRUSH_FLASH );
            if ( staticData == null )
            {
                final int staticObjectCount = 2;
                staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

                SpriteAnimation back = new SpriteAnimation( 0, 3 );
                back.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_back01 ),
                                                   Utils.framesToTime( 24, 1 ),
                                                   null,
                                                   null ) );
                back.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_back02 ),
                                                   Utils.framesToTime( 24, 1 ),
                                                   null,
                                                   null ) );
                back.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_back03 ),
                                                   Utils.framesToTime( 24, 1 ),
                                                   null,
                                                   null ) );

                SpriteAnimation front = new SpriteAnimation( 1, 7 );
                front.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_front01 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
                front.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_front02 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
                front.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_front03 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
                front.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_front04 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
                front.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_front05 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
                front.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_front06 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
                front.addFrame( new AnimationFrame( textureLibrary.getTextureByResource( R.drawable.effect_crush_front07 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );

                staticData.add( back );
                staticData.add( front );
                setStaticData( GameObjectType.CRUSH_FLASH, staticData );
            }

            RenderComponent backRender = ( RenderComponent )allocateComponent( RenderComponent.class );
            backRender.setPriority( SortConstants.EFFECT );

            SpriteComponent backSprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
            backSprite.setSize( ( int )object.width, ( int )object.height );
            backSprite.setRenderComponent( backRender );

            RenderComponent foreRender = ( RenderComponent )allocateComponent( RenderComponent.class );
            foreRender.setPriority( SortConstants.FOREGROUND_EFFECT );

            SpriteComponent foreSprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
            foreSprite.setSize( ( int )object.width, ( int )object.height );
            foreSprite.setRenderComponent( foreRender );

            LifetimeComponent lifetime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );

            object.add( lifetime );
            object.add( backRender );
            object.add( foreRender );
            object.add( backSprite );
            object.add( foreSprite );

// !!!! ???? TODO : why "null" parameter for "foreSprite" ? ???? !!!!
            addStaticData( GameObjectType.CRUSH_FLASH, object, backSprite );
            addStaticData( GameObjectType.CRUSH_FLASH, null, foreSprite );

// !!!! ???? TODO : what is this ? ???? !!!!!
            final SpriteAnimation idle = foreSprite.findAnimation( 1 );
            if ( idle != null )
            {
                lifetime.setTimeUntilDeath( idle.getLength() );
            }

            backSprite.playAnimation( 0 );
            foreSprite.playAnimation( 1 );
        }
        return object;
    }

/*
public GameObject spawnEffectFlash(float positionX, float positionY) {
TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;
GameObject object = null;
// This is just an effect, so we can live without it if our pools are exhausted.
if (componentAvailable(RenderComponent.class, 1)) {
object = mGameObjectPool.allocate();
object.getPosition().set(positionX, positionY);
object.activationRadius = mAlwaysActive;
object.width = 64;
object.height = 64;

FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.FLASH);
if (staticData == null) {
final int staticObjectCount = 1;
staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

SpriteAnimation back = new SpriteAnimation(0, 3);
back.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_crush_back01), 
  Utils.framesToTime(24, 1), null, null));
back.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_crush_back02), 
  Utils.framesToTime(24, 1), null, null));
back.addFrame(new AnimationFrame(
  textureLibrary.getTextureByResource(R.drawable.effect_crush_back03), 
  Utils.framesToTime(24, 1), null, null));


staticData.add(back);
setStaticData(GameObjectType.FLASH, staticData);
}


RenderComponent backRender = (RenderComponent)allocateComponent(RenderComponent.class);
backRender.setPriority(SortConstants.EFFECT);

SpriteComponent backSprite = (SpriteComponent)allocateComponent(SpriteComponent.class);
backSprite.setSize((int)object.width, (int)object.height);
backSprite.setRenderComponent(backRender);



LifetimeComponent lifetime = (LifetimeComponent)allocateComponent(LifetimeComponent.class);


object.add(lifetime);
object.add(backRender);
object.add(backSprite);

addStaticData(GameObjectType.FLASH, object, backSprite);


final SpriteAnimation idle = backSprite.findAnimation(0);
if (idle != null) {
lifetime.setTimeUntilDeath(idle.getLength());
}

backSprite.playAnimation(0);
}

return object;
}
*/

/*
public GameObject spawnSmokePoof(float positionX, float positionY) {

GameObject object = null;
// This is just an effect, so we can live without it if our pools are exhausted.
if (componentAvailable(LaunchProjectileComponent.class, 2)) {
object = mGameObjectPool.allocate();
object.getPosition().set(positionX, positionY);
object.activationRadius = mTightActivationRadius;
object.width = 1;
object.height = 1;

LifetimeComponent lifetime = (LifetimeComponent)allocateComponent(LifetimeComponent.class);
lifetime.setTimeUntilDeath(0.5f);

LaunchProjectileComponent smokeGun 
= (LaunchProjectileComponent)allocateComponent(LaunchProjectileComponent.class);
smokeGun.setSetsPerActivation(1);
smokeGun.setShotsPerSet(3);
smokeGun.setDelayBetweenShots(0.0f);
smokeGun.setObjectTypeToSpawn(GameObjectType.SMOKE_BIG);
smokeGun.setVelocityX(200.0f);
smokeGun.setVelocityY(200.0f);
smokeGun.setOffsetX(16);
smokeGun.setOffsetY(16);
smokeGun.setThetaError(1.0f);

LaunchProjectileComponent smokeGun2 
= (LaunchProjectileComponent)allocateComponent(LaunchProjectileComponent.class);
smokeGun2.setSetsPerActivation(1);
smokeGun2.setShotsPerSet(3);
smokeGun2.setDelayBetweenShots(0.0f);
smokeGun2.setObjectTypeToSpawn(GameObjectType.SMOKE_SMALL);
smokeGun2.setVelocityX(200.0f);
smokeGun2.setVelocityY(200.0f);
smokeGun2.setThetaError(1.0f); 
smokeGun2.setOffsetX(16);
smokeGun2.setOffsetY(16);

object.life = 1;    
object.destroyOnDeactivation = true;

object.add(lifetime);
object.add(smokeGun);
object.add(smokeGun2);
}
return object;
}
*/

/*
    public GameObject spawnAnimationPlayer( float positionX, float positionY )
    {
        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 16;
//        object.height = 16;
        object.height = 128;

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );
// !!!! TODO : handle more than 1 animation per level !!!!
// => do like the dialogs system, with different index for each animation, then in an xml file, refer each index to an anim file ...
        hitReact.setSpawnGameEventOnHit( HitType.COLLECT, GameFlowEvent.EVENT_SHOW_ANIMATION, R.anim.... );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        object.add( sprite );
        object.add( hitPlayer );
        object.add( hitReact );

        return object;
    }
*/


//////// PLATFORM - MID
//static float vel = 0.0f;
static float vel = 100.0f;

    public GameObject spawnEnemyPinkNamazu( float positionX, float positionY, boolean flipHorizontal )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = 128;
        object.height = 128;

        final float top = 75.0f; // height of the platform (floor)
//        final float top = 50.0f;
        final float height = 1000.0f; // size of collision zone => must be big to "catch" stomping, centered around "top"

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PINK_NAMAZU );
        if ( staticData == null )
        {
            final int staticObjectCount = 7;
//            final int staticObjectCount = 6;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );

            GameComponent physics = allocateComponent( SimplePhysicsComponent.class );

            // solid surface not needed for jump-through platforms
            SolidSurfaceComponent solidSurface = ( SolidSurfaceComponent )allocateComponent( SolidSurfaceComponent.class );
            solidSurface.inititalize( 5 );
//            solidSurface.inititalize( 1 );
            // circle shape:
            //  __        __3
            // /  \      2/ \4
            // |   |     1|  |5
            /*
                0:12,6:22,52:0.98058067569092,-0.19611613513818
                0:22,52:50,75:-0.62580046626293,0.77998318983495
                0:50,75:81,75:0,1
                0:81,75:104,49:0.74038072228541,0.67218776102228
                0:104,49:104,6:-0.99997086544204,-0.00763336538505
             */
            Vector2 surface1Start = new Vector2( 12, 3 );
            Vector2 surface1End = new Vector2( 22, 52 );
            Vector2 surface1Normal = new Vector2( -0.98058067569092f, -0.19611613513818f );
            surface1Normal.normalize();

            Vector2 surface2Start = new Vector2( 22, 52 );
            Vector2 surface2End = new Vector2( 50, 75 );
            Vector2 surface2Normal = new Vector2( -0.62580046626293f, 0.77998318983495f );
            surface2Normal.normalize();


            Vector2 surface3Start = new Vector2( 50, 75 );
//            Vector2 surface3Start = new Vector2( 0, top );
            Vector2 surface3End = new Vector2( 81, 75 );
//            Vector2 surface3End = new Vector2( 104, top );
            Vector2 surface3Normal = new Vector2( 0, 1 );

            Vector2 surface4Start = new Vector2( 81, 75 );
            Vector2 surface4End = new Vector2( 104, 49 );
            Vector2 surface4Normal = new Vector2( 0.74038072228541f, 0.67218776102228f );

            Vector2 surface5Start = new Vector2( 104, 49 );
            Vector2 surface5End = new Vector2( 104, 3 );
            Vector2 surface5Normal = new Vector2( 1.0f, 0.0f );

            solidSurface.addSurface( surface1Start, surface1End, surface1Normal );
            solidSurface.addSurface( surface2Start, surface2End, surface2Normal );
            solidSurface.addSurface( surface3Start, surface3End, surface3Normal );
            solidSurface.addSurface( surface4Start, surface4End, surface4Normal );
            solidSurface.addSurface( surface5Start, surface5End, surface5Normal );


            // collision volume to handle platform collisions => objects interacting must have an attacking platform volume!
            FixedSizeArray<CollisionVolume> platformVolume = new FixedSizeArray<CollisionVolume>( 1 );
//            platformVolume.add( new AABoxCollisionVolume( 0, top, object.width, height ) );
            platformVolume.add( new AABoxCollisionVolume( 15, top - height, 100, 2 * height ) );
            platformVolume.get( 0 ).setHitType( HitType.PLATFORM );

            SpriteAnimation idle = new SpriteAnimation( GenericAnimationComponent.Animation.IDLE, 4 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_pinkdude_stand ),
                                               Utils.framesToTime( 24, 8 ),
                                               null,
                                               platformVolume ) );
            AnimationFrame idle1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_pinkdude_sleep01 ),
                                                       Utils.framesToTime( 24, 3 ),
                                                       null,
                                                       platformVolume );
            AnimationFrame idle2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_pinkdude_sleep02 ),
                                                       Utils.framesToTime( 24, 8 ),
                                                       null,
                                                       platformVolume );
            idle.addFrame( idle1 );
            idle.addFrame( idle2 );
            idle.addFrame( idle1 );
            idle.setLoop( true );


            SpriteAnimation wake = new SpriteAnimation( GenericAnimationComponent.Animation.MOVE, 4 );
            AnimationFrame wake1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_pinkdude_eyeopen ),
                                                       Utils.framesToTime( 24, 3 ),
                                                       null,
                                                       platformVolume );
            AnimationFrame wake2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_pinkdude_stand ),
                                                       Utils.framesToTime( 24, 3 ),
                                                       null,
                                                       platformVolume );
            wake.addFrame( wake1 );
            wake.addFrame( wake2 );
            wake.addFrame( wake1 );
            wake.addFrame( wake2 );

//            FixedSizeArray<CollisionVolume> crushAttackVolume = new FixedSizeArray<CollisionVolume>( 1 );
//            crushAttackVolume.add( new AABoxCollisionVolume( 32, 0, 64, 32, HitType.HIT ) );

            SpriteAnimation attack = new SpriteAnimation( GenericAnimationComponent.Animation.ATTACK, 1 );
            attack.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_pinkdude_jump ),
                                                 Utils.framesToTime( 24, 2 ),
//                                                 crushAttackVolume,
                                                 null,
                                                 platformVolume ) );


            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );
            staticData.add( solidSurface );
            staticData.add( idle );
            staticData.add( wake );
            staticData.add( attack );

            setStaticData( GameObjectType.PINK_NAMAZU, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_ENEMY );

        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
//        bgcollision.setSize( 100, 75 );
        bgcollision.setSize( 100, (int)top );
        bgcollision.setOffset( 12, 5 );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        GenericAnimationComponent animation = ( GenericAnimationComponent )allocateComponent( GenericAnimationComponent.class );
        animation.setSprite( sprite );

        SleeperComponent sleeper = ( SleeperComponent )allocateComponent( SleeperComponent.class );
//        sleeper.setAttackImpulse( 100.0f, 170.0f );
        sleeper.setAttackImpulse( vel / 10.0f, vel + 70.0f );
vel+= 100.0f;
        sleeper.setSlam( 0.3f, 25.0f );

        DynamicCollisionComponent collision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( collision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        collision.setHitReactionComponent( hitReact );

        // platform component, has to be initialised with object's position & platform height
        PlatformComponent platform = ( PlatformComponent )allocateComponent( PlatformComponent.class );
        platform.init( positionX, top );
        hitReact.setPlatformComponent( platform );


        object.team = Team.NONE;
        object.life = 1;
        if ( flipHorizontal )
        {
            object.facingDirection.x = -1.0f;
        }

        object.add( render );
        object.add( sprite );
        object.add( bgcollision );
        object.add( animation );
        object.add( collision );
        object.add( hitReact );
        object.add( sleeper );
        object.add( platform );

        addStaticData( GameObjectType.PINK_NAMAZU, object, sprite );

        final SpriteAnimation wakeUp = sprite.findAnimation( GenericAnimationComponent.Animation.MOVE );
        if ( wakeUp != null )
        {
            sleeper.setWakeUpDuration( wakeUp.getLength() + 1.0f );
        }

        sprite.playAnimation( GenericAnimationComponent.Animation.IDLE );

        return object;
    }
//////// PLATFORM - END

//////// BREAKABLE - BEGIN
    public GameObject spawnBreakableBlock( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius; //?
        object.width = 32;
        object.height = 32;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.BREAKABLE_BLOCK );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );


            // animations
            FixedSizeArray<CollisionVolume> vulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
// !!!! TODO: vulnerability volume should be a bit bigger than object !!!!
// (unless player's attack volumes are bigger than collision volume)
// (could also have 2 rectangular volumes perpendicular (to control each side of the square)
            vulnerabilityVolume.add( new AABoxCollisionVolume( -2.0f, -2.0f, object.width + 2.0f, object.height + 2.0f, HitType.HIT ) );

// !!!! ???? TODO: why not using ".ordinal()" here ? ???? !!!!
            SpriteAnimation idle = new SpriteAnimation( 0, 4 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.block01 ),
                                               Utils.framesToTime( 24, 3 ),
                                               null,
                                               vulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.block02 ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               vulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.block03 ),
                                               Utils.framesToTime( 24, 3 ),
                                               null,
                                               vulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.block02 ),
                                               Utils.framesToTime( 24, 3 ),
                                               null,
                                               vulnerabilityVolume ) );
            idle.setLoop( true );


            SolidSurfaceComponent solidSurface = ( SolidSurfaceComponent )allocateComponent( SolidSurfaceComponent.class );
            solidSurface.inititalize( 4 );

            // box shape:
            // ___       ___1
            // | |      2| |3
            // ---       ---4
            Vector2 surface1Start = new Vector2( 0, object.height );
            Vector2 surface1End = new Vector2( object.width, object.height );
            Vector2 surface1Normal = new Vector2( 0.0f, 1.0f );

            Vector2 surface2Start = new Vector2( 0, object.height );
            Vector2 surface2End = new Vector2( 0, 0 );
            Vector2 surface2Normal = new Vector2( -1.0f, 0.0f );
            surface2Normal.normalize();

            Vector2 surface3Start = new Vector2( object.width, object.height );
            Vector2 surface3End = new Vector2( object.width, 0 );
            Vector2 surface3Normal = new Vector2( 1.0f, 0 );

            Vector2 surface4Start = new Vector2( 0, 0 );
            Vector2 surface4End = new Vector2( object.width, 0 );
            Vector2 surface4Normal = new Vector2( 0, -1.0f );
            surface4Normal.normalize();

            solidSurface.addSurface( surface1Start, surface1End, surface1Normal );
            solidSurface.addSurface( surface2Start, surface2End, surface2Normal );
            solidSurface.addSurface( surface3Start, surface3End, surface3Normal );
            solidSurface.addSurface( surface4Start, surface4End, surface4Normal );


            staticData.add( idle );
            staticData.add( solidSurface );

            setStaticData( GameObjectType.BREAKABLE_BLOCK, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

//useless?
//        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
//        bgcollision.setSize( 32, 32 );
//        bgcollision.setOffset( 0, 0 );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent collision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( collision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        collision.setHitReactionComponent( hitReact );
//sounds, spawns, etc. (here or in lifetime component
//        hitReact.setSpawnObjectOnTakeHit(hitType, objectTypeOrdinal, alignToOtherX, alignToOtherY);

        LifetimeComponent lifetime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
//        lifetime.setDeathSound(deathSound);
//        lifetime.addObjectToSpawnOnDeath( GameObjectType.EXPLOSION_GIANT.ordinal() );


        object.add( render);
        object.add( sprite);
//?        object.add( bgcollision );
        object.add( collision );
        object.add( hitReact );
        object.add( lifetime );

        object.team = Team.NONE;
//?        object.strength = 2;

        addStaticData( GameObjectType.BREAKABLE_BLOCK, object, sprite );

        object.commitUpdates();

        sprite.playAnimation( 0 );

        return object;
    }
//////// BREAKABLE - END

//////// CLIMBING 20141024 - MID
    public GameObject spawnPlayerClimb( float positionX, float positionY, GameObject parentObject, int life )
    {
        final TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

//        // load textures related to weapons
//        // ( if not done here, drawables won't be visible ...)
//        textureLibrary.allocateTexture( R.drawable.harpoon );

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 64;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PLAYER_CLIMB );
        if ( staticData == null )
        {
//            staticData = new FixedSizeArray<BaseObject>( 12 );
            staticData = new FixedSizeArray<BaseObject>( 9 );

            GameComponent gravity = allocateComponent( GravityComponentTop.class );
            GameComponent movement = allocateComponent( MovementComponent.class );
            PhysicsComponentTop physics = ( PhysicsComponentTop )allocateComponent( PhysicsComponentTop.class );

// !!!! TODO : adapt values ... !!!!
// => make it less "bouncy" ... (done here ?)
//            physics.setMass( 9.1f ); // ~90kg w/ earth gravity
            physics.setMass( 1.0f ); // ~10kg w/ earth gravity
            physics.setDynamicFrictionCoeffecient( 0.2f );
            physics.setStaticFrictionCoeffecient( 0.01f );

            // Animation Data
            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 16, 32, 32 ) );

            FixedSizeArray<CollisionVolume> pressAndCollectVolume = new FixedSizeArray<CollisionVolume>( 2 );
            AABoxCollisionVolume collectionVolume = new AABoxCollisionVolume( 16, 0, 32, 48 );
            collectionVolume.setHitType( HitType.COLLECT );
            pressAndCollectVolume.add( collectionVolume );
            AABoxCollisionVolume pressCollisionVolume = new AABoxCollisionVolume( 16, 0, 32, 16 );
            pressCollisionVolume.setHitType( HitType.DEPRESS );
            pressAndCollectVolume.add( pressCollisionVolume );

// !!!! TODO : make different animations ( with more frames ) !!!!
            SpriteAnimation animIdle = new SpriteAnimation( AnimationComponentTop.PlayerAnimations.IDLE.ordinal(), 4 );
            final AnimationFrame frameIdle1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_idle_01 ),
                                                                  Utils.framesToTime( 24, 30 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            final AnimationFrame frameIdle2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_idle_02 ),
                                                                  Utils.framesToTime( 24, 1 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            final AnimationFrame frameIdle3 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_idle_03 ),
                                                                  Utils.framesToTime( 24, 1 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            animIdle.addFrame( frameIdle1 );
            animIdle.addFrame( frameIdle2 );
            animIdle.addFrame( frameIdle3 );
            animIdle.addFrame( frameIdle2 );
            animIdle.setLoop( true );

            SpriteAnimation animWalk = new SpriteAnimation( AnimationComponentTop.PlayerAnimations.MOVE.ordinal(), 4 );
            final AnimationFrame frameWalk1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_move_01 ),
                                                                  Utils.framesToTime( 24, 6 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            final AnimationFrame frameWalk2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_move_02 ),
                                                                  Utils.framesToTime( 24, 6 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            final AnimationFrame frameWalk3 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_move_03 ),
                                                                  Utils.framesToTime( 24, 6 ),
                                                                  pressAndCollectVolume,
                                                                  basicVulnerabilityVolume );
            animWalk.addFrame( frameWalk1 );
            animWalk.addFrame( frameWalk2 );
            animWalk.addFrame( frameWalk3 );
            animWalk.addFrame( frameWalk2 );
            animWalk.setLoop( true );

            SpriteAnimation animRun = new SpriteAnimation( AnimationComponentTop.PlayerAnimations.MOVE_FAST.ordinal(), 4 );
            final AnimationFrame frameRun1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_move_fast_01 ),
                                                                 Utils.framesToTime( 24, 6 ),
                                                                 pressAndCollectVolume,
                                                                 basicVulnerabilityVolume );
            final AnimationFrame frameRun2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_move_fast_02 ),
                                                                 Utils.framesToTime( 24, 6 ),
                                                                 pressAndCollectVolume,
                                                                 basicVulnerabilityVolume );
            final AnimationFrame frameRun3 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_move_fast_03 ),
                                                                 Utils.framesToTime( 24, 6 ),
                                                                 pressAndCollectVolume,
                                                                 basicVulnerabilityVolume );
            animRun.addFrame( frameRun1 );
            animRun.addFrame( frameRun2 );
            animRun.addFrame( frameRun3 );
            animRun.addFrame( frameRun2 );
            animRun.setLoop( true );

/*
            FixedSizeArray<CollisionVolume> stompAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
            stompAttackVolume.add( new AABoxCollisionVolume( 16, -5.0f, 32, 37, HitType.HIT ) );
            stompAttackVolume.add( pressCollisionVolume );
            stompAttackVolume.add( collectionVolume );

            SpriteAnimation animAttackStomp = new SpriteAnimation( AnimationComponentTop.PlayerAnimations.ATTACK.ordinal(), 4 );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_01 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_02 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_03 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );
            animAttackStomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_stomp_04 ),
                                                          Utils.framesToTime( 24, 2 ),
                                                          stompAttackVolume,
                                                          null ) );

            FixedSizeArray<CollisionVolume> dashAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
// !!!! TODO: adapt attack volume !!!!
            dashAttackVolume.add( new AABoxCollisionVolume( 16, 0.0f, 32, 64, HitType.HIT ) );
            dashAttackVolume.add( pressCollisionVolume );
            dashAttackVolume.add( collectionVolume );

            SpriteAnimation animAttackDash = new SpriteAnimation( AnimationComponentTop.PlayerAnimations.ATTACK_ALTERNATE.ordinal(), 4 );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_01 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_02 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_03 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );
            animAttackDash.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_attack_dash_04 ),
                                                         Utils.framesToTime( 24, 2 ),
                                                         stompAttackVolume,
                                                         null ) );
*/
/*
            SpriteAnimation animHit = new SpriteAnimation( AnimationComponentTop.PlayerAnimations.HIT_REACT.ordinal(), 2 );
            animHit.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_hit_01 ),
                                                  Utils.framesToTime( 24, 6 ),
                                                  pressAndCollectVolume,
                                                  null ) );
            animHit.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_hit_02 ),
                                                  Utils.framesToTime( 24, 12 ),
                                                  pressAndCollectVolume,
                                                  null ) );
*/

// !!!! ???? TODO: death ? ???? !!!!
            SpriteAnimation animDeath = new SpriteAnimation( AnimationComponentTop.PlayerAnimations.DEATH.ordinal(), 1 );
            animDeath.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_climb_die_01 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );

            SpriteAnimation animFrozen = new SpriteAnimation( AnimationComponentTop.PlayerAnimations.FROZEN.ordinal(), 1 );
            // Frozen has no frames!

            GameComponent record = allocateComponent( UpdateRecordHudImpl.class );

            // Save static data
            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );

            staticData.add( animIdle );
            staticData.add( animWalk );
            staticData.add( animRun );
//            staticData.add( animAttackStomp );
//            staticData.add( animAttackDash );
//            staticData.add( animHit );
            staticData.add( animDeath );
            staticData.add( animFrozen );
            staticData.add( record );

            setStaticData( GameObjectType.PLAYER_CLIMB, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.PLAYER );
        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgcollision.setSize( 32, 48 );
        bgcollision.setOffset( 16, 0 );

//////// SWAPPERS 20141106 - BEGIN
//        PlayerComponentTopSpecific player = ( PlayerComponentTopSpecific )allocateComponent( PlayerComponentTopSpecific.class );
//////// SWAPPERS 20141106 - MID
        PlayerComponentTopClimber player = ( PlayerComponentTopClimber )allocateComponent( PlayerComponentTopClimber.class );
// !!!! TODO: FIX THIS !!!!
// => use "HotSpotType...." & "GameObjectType.....ordinal()", but which ones?
        player.setHotSpotSwapperId( 0, 1 );
//        player.setHotSpotSwapperId( 1, 1 );
//////// SWAPPERS 20141106 - END
        AnimationComponentTopImpl animation = ( AnimationComponentTopImpl )allocateComponent( AnimationComponentTopImpl.class );
//        animation.setPlayer( player );

// !!!! ???? TODO: "death" sound ? ???? !!!!
/*
        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            animation.setDeathSound( sound.load( R.raw.sound_explode ) );
        }
*/

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );
        animation.setSprite( sprite );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
//        hitReact.setBounceOnTakeHit( true );
//        hitReact.setPauseOnAttack( true );
//        hitReact.setInvincibleTime( 3.0f );
//        hitReact.setSpawnObjectOnDealHit( HitType.HIT, GameObjectType.CRUSH_FLASH.ordinal(), false, true );

// !!!! ???? TODO: "hit" sound ? ???? !!!!
/*
        if ( sound != null )
        {
            hitReact.setSoundOnTakeHit( HitType.HIT, sound.load( R.raw.deep_clang ) );
        }
*/

        dynamicCollision.setHitReactionComponent( hitReact );
        player.setHitReactionComponent( hitReact );


        InventoryComponent inventory = ( InventoryComponent )allocateComponent( InventoryComponent.class );
        // find parentObject inventory's record
        UpdateRecordHudImpl parentObjectRecord = null;
        final FixedSizeArray<BaseObject> parentObjects = parentObject.getObjects();
        final int count = parentObjects.getCount();
        for ( int y = 0; y < count; y++ )
        {
            final BaseObject entry = parentObjects.get( y );
            if ( entry != null )
            {
                if ( entry instanceof InventoryComponent )
                {
                    parentObjectRecord = ( UpdateRecordHudImpl )( ( InventoryComponent )entry ).getRecord();
                    break;
                }
            }
        }
        final int staticDataSize = staticData.getCount();
        for ( int x = 0; x < staticDataSize; x++ )
        {
            final BaseObject entry = staticData.get( x );
            if ( entry instanceof UpdateRecordHudImpl )
            {
                final UpdateRecordHudImpl record = ( UpdateRecordHudImpl )entry;
                if ( parentObjectRecord != null )
                {
                    record.copy( parentObjectRecord );
                }
                inventory.setInventory( record );
                player.setInventory( inventory );
                hitReact.setInventoryUpdate( record );
                break;
            }
        }

        TakeControlComponent controller = ( TakeControlComponent )allocateComponent( TakeControlComponent.class );
        controller.setControlledObject( parentObject );
        player.setTakeControlComponent( controller );
        if ( inventory != null )
        {
            controller.setInventory( inventory );
        }
        controller.setDieOnRelease( false ); // to avoid problem of hud life bar changing when releasing


// !!!! ???? TODO: lifeTime component ? ???? !!!!
        LifetimeComponent lifeTime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
//        lifetime.addObjectToSpawnOnDeath( GameObjectType.EXPLOSION_GIANT.ordinal() );

        final EventRecorderImpl evtRec = ( EventRecorderImpl )sSystemRegistry.eventRecorder;
        player.setLifeMax( evtRec.getDifficultyConstants().getPlayerMaxLife() );
        object.life = parentObject.life;
//?        controller.setShareLife( true );

        object.team = Team.PLAYER;

        object.add( player );
        object.add( inventory );
        object.add( bgcollision );
        object.add( render );
        object.add( animation );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitReact );
        object.add( lifeTime );
        object.add( controller );

        addStaticData( GameObjectType.PLAYER_CLIMB, object, sprite );

        sprite.playAnimation( AnimationComponentTop.PlayerAnimations.IDLE.ordinal() );


        CameraSystem camera = sSystemRegistry.cameraSystem;
        if ( camera != null )
        {
            camera.setTarget( object );
        }

        return object;
    }

    public GameObject spawnClimbingWallTop( float positionX, float positionY )
    {
        final LevelSystem levelSystem = sSystemRegistry.levelSystem;
        final int tileWidth = levelSystem.mTileWidth;
        final int tileHeight = levelSystem.mTileHeight;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mTightActivationRadius;
        object.width = tileWidth;
        object.height = tileHeight;

//        final float top = tileHeight; // height of the platform (floor)
        final float top = 0; // height of the platform (floor)
        final float height = 20 * tileHeight; // size of collision zone => must be big to "catch" stomping, centered around "top"

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.CLIMBING_TOP );
        if ( staticData == null )
        {
            staticData = new FixedSizeArray<BaseObject>( 1 );

            // collision volume to handle platform collisions => objects interacting must have an attacking platform volume!
            FixedSizeArray<CollisionVolume> platformVolume = new FixedSizeArray<CollisionVolume>( 1 );
            platformVolume.add( new AABoxCollisionVolume( 0, top - height, tileWidth, 2 * height ) );
            platformVolume.get( 0 ).setHitType( HitType.PLATFORM );

            SpriteAnimation idle = new SpriteAnimation( 0, 1 );
            idle.addFrame( new AnimationFrame( null, // no visual
                                               1.0f,
                                               null,
                                               platformVolume ) );
            staticData.add( idle );

            setStaticData( GameObjectType.CLIMBING_TOP, staticData );
        }

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( tileWidth, tileHeight );

        DynamicCollisionComponent collision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( collision );

        HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
        collision.setHitReactionComponent( hitReact );

        // platform component, has to be initialised with object's position & platform height
        PlatformComponent platform = ( PlatformComponent )allocateComponent( PlatformComponent.class );
        platform.init( positionX, top );
        hitReact.setPlatformComponent( platform );


        object.team = Team.NONE;
        object.life = 1;

        object.add( sprite );
        object.add( collision );
        object.add( hitReact );
        object.add( platform );

        addStaticData( GameObjectType.CLIMBING_TOP, object, sprite );

        object.commitUpdates();

        sprite.playAnimation( 0 );

        return object;
    }
//////// CLIMBING 20141024 - END

//////// GAME - END

}
