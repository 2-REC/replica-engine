package derek.android.gamephone;

//////// TOP VIEW - BEGIN
import derek.android.gamephone.AnimationComponentSide.PlayerAnimations;
//////// TOP VIEW - MID
//import derek.android.gamephone.AnimationComponentTop.PlayerAnimations;
//////// TOP VIEW - END

import derek.android.engine.FixedSizeArray;
import derek.android.engine.BaseObject;
import derek.android.engine.GameObject;
import derek.android.engine.GameObject.ActionType;
import derek.android.engine.GameObject.Team;
//import derek.android.engine.ContextParameters;
import derek.android.engine.SortConstants;
import derek.android.engine.TextureLibrary;
//import derek.android.engine.DrawableBitmap;
import derek.android.engine.GameObjectFactory;
import derek.android.engine.GameFlowEvent;
import derek.android.engine.GameComponent;
import derek.android.engine.ChannelSystem;
//import derek.android.engine.PatrolComponentTop;
import derek.android.engine.RenderComponent;
import derek.android.engine.SpriteComponent;
import derek.android.engine.MovementComponent;
import derek.android.engine.PhysicsComponent;
import derek.android.engine.GravityComponent;
import derek.android.engine.InventoryComponent;
import derek.android.engine.ChangeComponentsComponent;
import derek.android.engine.LifetimeComponent;
import derek.android.engine.HitPlayerComponent;
import derek.android.engine.HitReactionComponent;
import derek.android.engine.BackgroundCollisionComponent;
import derek.android.engine.DynamicCollisionComponent;
import derek.android.engine.Vector2;
//////// BALLS - BEGIN
//import derek.android.engine.SimplePhysicsComponent;
//////// BALLS - MID
import derek.android.engine.BallPhysicsComponent;
//////// BALLS - END
import derek.android.engine.PatrolComponent;
import derek.android.engine.EnemyAnimationComponent;
import derek.android.engine.EnemyAnimationComponent.EnemyAnimations;
//////// PANG - MID
import derek.android.engine.LaunchProjectileComponent;
import derek.android.engine.LaunchChainComponent;
import derek.android.engine.SimpleCollisionComponent;
//////// PANG - END
//////// dialogs - m
import derek.android.engine.SelectDialogComponent;
//////// dialogs - e
//////// NPC - MID
import derek.android.engine.NPCComponent;
//////// NPC - END
import derek.android.engine.PlaySingleSoundComponent;
import derek.android.engine.EventRecorder;
import derek.android.engine.CollisionVolume;
import derek.android.engine.AABoxCollisionVolume;
import derek.android.engine.CollisionParameters.HitType;
import derek.android.engine.AnimationFrame;
import derek.android.engine.SphereCollisionVolume;
import derek.android.engine.SpriteAnimation;
import derek.android.engine.CameraSystem;
import derek.android.engine.SoundSystem;
import derek.android.engine.Utils;
//////// DOOR - MID
import derek.android.engine.ButtonAnimationComponent;
import derek.android.engine.DoorAnimationComponent;
import derek.android.engine.SolidSurfaceComponent;
//////// DOOR - END

import derek.android.engine.DebugLog;



/*
 A class for generating game objects at runtime.
 This should really be replaced with something that is data-driven,
  but it is hard to do data parsing quickly at runtime.
 For the moment this class is full of large functions that just patch pointers
  between objects, but in the future those functions should either be 
   a) generated from data at compile time,
   or
   b) described by data at runtime.
*/

public class GameObjectFactoryImpl extends GameObjectFactory
{
    // TODO: set to appropriate value depending on game
    private final static int MAX_GAME_OBJECTS = 384;

//////// GAME - BEGIN
    private final static String sRedButtonChannel = "RED BUTTON";
/*
    private final static String sBlueButtonChannel = "BLUE BUTTON";
    private final static String sGreenButtonChannel = "GREEN BUTTON";
    private final static String sSurprisedNPCChannel = "SURPRISED";
*/
//////// GAME - END


    public GameObjectFactoryImpl()
    {
        super( MAX_GAME_OBJECTS );

        ComponentClass[] componentTypes = {
            new ComponentClass( BackgroundCollisionComponent.class, 192 ),
            new ComponentClass( ChangeComponentsComponent.class, 256 ),
            new ComponentClass( DynamicCollisionComponent.class, 256 ),
            new ComponentClass( EnemyAnimationComponent.class, 256 ),
            new ComponentClass( GravityComponent.class, 128 ),
            new ComponentClass( HitPlayerComponent.class, 256 ),
            new ComponentClass( HitReactionComponent.class, 256 ),
            new ComponentClass( InventoryComponent.class, 128 ),
            new ComponentClass( LifetimeComponent.class, 384 ),
            new ComponentClass( MovementComponent.class, 128 ),
            new ComponentClass( PatrolComponent.class, 256 ),
            new ComponentClass( PhysicsComponent.class, 8 ),
            new ComponentClass( PlaySingleSoundComponent.class, 128 ),
            new ComponentClass( RenderComponent.class, 384 ),
//            new ComponentClass( SimplePhysicsComponent.class, 256 ),
            new ComponentClass( SpriteComponent.class, 384 ),
//////// TOP VIEW - BEGIN
            new ComponentClass( AnimationComponentSide.class, 1),
            new ComponentClass( PlayerComponentSide.class, 1),
//////// TOP VIEW - MID
/*
            new ComponentClass( AnimationComponentTop.class, 1),
            new ComponentClass( PlayerComponentTop.class, 1),
*/
//////// TOP VIEW - END
//            new ComponentClass( FrameRateWatcherComponent.class, 1),
//////// dialogs - m
            new ComponentClass( SelectDialogComponent.class, 8 ),
//////// dialogs - e
//////// NPC - MID
//...???
//////// NPC - END
//new ComponentClass( PatrolComponentTop.class, 32 ),
//////// BALLS - MID
            new ComponentClass( BallPhysicsComponent.class, 32 ),
//////// BALLS - END
//////// PANG - MID
            new ComponentClass( LaunchProjectileComponent.class, 32 ),
            new ComponentClass( LaunchChainComponent.class, 32 ),
            new ComponentClass( SimpleCollisionComponent.class, 64 ),
//////// PANG - END
//////// DOOR - MID
            new ComponentClass( ButtonAnimationComponent.class, 32 ),
            new ComponentClass( DoorAnimationComponent.class, 256 ),
            new ComponentClass( SolidSurfaceComponent.class, 16 ),
//////// DOOR - END
        };
        setComponentClasses( componentTypes );
    }

    protected int getObjectTypeCount()
    {
        return GameObjectType.OBJECT_COUNT.ordinal();
    }

    protected boolean isPlayer( int index )
    {
        return ( index == GameObjectType.PLAYER.index() );
    }

/*
    @Override
    public void reset()
    {
    }
*/

    private FixedSizeArray<BaseObject> getStaticData( GameObjectType type )
    {
        return mStaticData.get( type.ordinal() );
    }

    private void setStaticData( GameObjectType type, FixedSizeArray<BaseObject> data )
    {
        setStaticData( type.ordinal(), data );
    }

    private void addStaticData( GameObjectType type, GameObject object, SpriteComponent sprite )
    {
        addStaticData( type.ordinal(), object, sprite );
    }

    ////////////////////////////////////////////////////////////////////////////

    // List of game objects that can be spawned at runtime.
    // The objects' indices must match the order of the object tileset in the level editor.
    public enum GameObjectType
    {
        INVALID( -1 ),

//////// GAME - BEGIN
        PLAYER ( 0 ),

        // Collectables
        COIN ( 1 ),
//        PICKUP_LIFE ( 2 ),
        PICKUP_SPEEDUP ( 3 ),

        // Characters
//////// dialogs - m
        KYLE_DEAD( 10 ),
//////// dialogs - e
//////// NPC - MID
//        EVIL_KABOCHA( ... ); ???
//////// NPC - END
        //...,

        // AI
        BROBOT ( 16 ),
        //...,

        // Objects
//////// DOOR - MID
        DOOR_RED ( 32 ),
        BUTTON_RED ( 35 ),
//////// DOOR - END
        //...,

        // Effects
        DUST( 48 ),
/*
        EXPLOSION_SMALL(49),
        EXPLOSION_LARGE(50),
*/
        EXPLOSION_GIANT( 51 ),

        // Special Spawnable
//        ANIMATION_PLAYER( 55 ),

/*
        CAMERA_BIAS(56),
*/
//        FRAMERATE_WATCHER( 57 ),
/*
        INFINITE_SPAWNER(58),
*/

        // Projectiles
//////// PANG - MID
        HARPOON( 60 ),
        HARPOON_HEAD( 61 ),
//////// PANG - END
        //...,

        // Special Objects -- Not spawnable normally
/*
        SMOKE_BIG(-1),
        SMOKE_SMALL(-1),
*/
        CRUSH_FLASH( -1 ),
/*
        FLASH(-1),
        SMOKE_POOF(-1),
*/
//////// GAME - END

        // End
        OBJECT_COUNT( -1 );


        private final int mIndex;
        GameObjectType( int index )
        {
            this.mIndex = index;
        }

        public int index()
        {
            return mIndex;
        }

// !!!! ???? TODO: is there a better way to do that ? ???? !!!!
        public static GameObjectType indexToType( int index )
        {
            final GameObjectType[] valuesArray = values();
            GameObjectType foundType = INVALID;
            for ( int x = 0; x < valuesArray.length; x++ )
            {
                GameObjectType type = valuesArray[ x ];
                if ( type.mIndex == index )
                {
                    foundType = type;
                    break;
                }
            }
            return foundType;
        }
    }

    public void preloadEffects()
    {
        // These textures appear in every level, so they are long-term.
        TextureLibrary textureLibrary = sSystemRegistry.longTermTextureLibrary;

//////// GAME - BEGIN
        textureLibrary.allocateTexture( R.drawable.dust01 );
        textureLibrary.allocateTexture( R.drawable.dust02 );
        textureLibrary.allocateTexture( R.drawable.dust03 );
        textureLibrary.allocateTexture( R.drawable.dust04 );
        textureLibrary.allocateTexture( R.drawable.dust05 );

/*
        textureLibrary.allocateTexture( R.drawable.effect_energyball01 );
        textureLibrary.allocateTexture( R.drawable.effect_energyball02 );
        textureLibrary.allocateTexture( R.drawable.effect_energyball03 );
        textureLibrary.allocateTexture( R.drawable.effect_energyball04 );

        textureLibrary.allocateTexture( R.drawable.effect_explosion_small01 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_small02 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_small03 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_small04 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_small05 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_small06 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_small07 );
*/

        textureLibrary.allocateTexture( R.drawable.effect_explosion_big01 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_big02 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_big03 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_big04 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_big05 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_big06 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_big07 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_big08 );
        textureLibrary.allocateTexture( R.drawable.effect_explosion_big09 );

/*
        textureLibrary.allocateTexture( R.drawable.effect_smoke_big01 );
        textureLibrary.allocateTexture( R.drawable.effect_smoke_big02 );
        textureLibrary.allocateTexture( R.drawable.effect_smoke_big03 );
        textureLibrary.allocateTexture( R.drawable.effect_smoke_big04 );
        textureLibrary.allocateTexture( R.drawable.effect_smoke_big05 );

        textureLibrary.allocateTexture( R.drawable.effect_smoke_small01 );
        textureLibrary.allocateTexture( R.drawable.effect_smoke_small02 );
        textureLibrary.allocateTexture( R.drawable.effect_smoke_small03 );
        textureLibrary.allocateTexture( R.drawable.effect_smoke_small04 );
        textureLibrary.allocateTexture( R.drawable.effect_smoke_small05 );
*/
        textureLibrary.allocateTexture( R.drawable.effect_crush_back01 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_back02 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_back03 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_front01 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_front02 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_front03 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_front04 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_front05 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_front06 );
        textureLibrary.allocateTexture( R.drawable.effect_crush_front07 );
//////// GAME - END
    }

    public GameObject spawnFromIndex( int index, float x, float y, boolean horzFlip )
    {
        GameObject object = null;
        GameObjectType type = GameObjectType.indexToType( index );
        if ( type != GameObjectType.INVALID )
        {
            object = spawn( type, x, y, horzFlip );
        }
        return object;
    }

    public GameObject spawnFromOrdinal( int ordinal, float x, float y, boolean horzFlip )
    {
        GameObject object = null;
        if ( ( ordinal > 0 )
            && ( ordinal < GameObjectType.OBJECT_COUNT.ordinal() ) )
        {
            GameObjectType type = GameObjectType.values()[ ordinal ];
            if ( type != GameObjectType.INVALID )
            {
                object = spawn( type, x, y, horzFlip );
            }
        }
        return object;
    }

    public GameObject spawn( GameObjectType type, float x, float y, boolean horzFlip )
    {
        GameObject newObject = null;
        switch ( type )
        {
//////// GAME - BEGIN
        case PLAYER:
            newObject = spawnPlayer( x, y );
            break;

//////// dialogs - m
        case KYLE_DEAD:
            newObject = spawnKyleDead( x, y );
            break;
//////// dialogs - e

//////// NPC - MID
//        case EVIL_KABOCHA:
//            newObject = spawnEnemyEvilKabocha( x, y, horzFlip );
//////// NPC - END

        case COIN:
            newObject = spawnCoin( x, y );
            break;
/*
        case PICKUP_LIFE:
            newObject = spawnPickupLife( x, y );
            break;
*/
        case PICKUP_SPEEDUP:
            newObject = spawnPickupSpeedUp( x, y );
            break;

        case BROBOT:
            newObject = spawnEnemyBrobot( x, y, horzFlip );
            break;

//////// DOOR - MID
        case DOOR_RED:
            newObject = spawnObjectDoor( x, y, GameObjectType.DOOR_RED, ( type == GameObjectType.DOOR_RED ) );
            break;

        case BUTTON_RED:
            newObject = spawnObjectButton( x, y, GameObjectType.BUTTON_RED );
            break;
//////// DOOR - END

        case DUST:
            newObject = spawnDust( x, y, horzFlip );
            break;
/*
        case EXPLOSION_SMALL:
        newObject = spawnEffectExplosionSmall(x, y);
        break;
        case EXPLOSION_LARGE:
        newObject = spawnEffectExplosionLarge(x, y);
        break;
*/
        case EXPLOSION_GIANT:
            newObject = spawnEffectExplosionGiant( x, y );
            break;
/*
        case ANIMATION_PLAYER:
            newObject = spawnAnimationPlayer( x, y );
            break;
*/
/*
        case CAMERA_BIAS:
        newObject = spawnCameraBias(x, y);
        break;
*/
/*
        case FRAMERATE_WATCHER:
            newObject = spawnFrameRateWatcher(x, y);
            break;
*/
/*
        case INFINITE_SPAWNER:
        newObject = spawnObjectInfiniteSpawner(x, y);
        break;
*/
//////// PANG - MID
        case HARPOON:
            newObject = spawnHarpoon( x, y, horzFlip );
            break;
            
        case HARPOON_HEAD:
            newObject = spawnHarpoonHead( x, y, horzFlip );
            break;
//////// PANG - END
/*
        case SMOKE_BIG:
        newObject = spawnEffectSmokeBig(x, y);
        break;
        case SMOKE_SMALL:
        newObject = spawnEffectSmokeSmall(x, y);
        break;
*/
        case CRUSH_FLASH:
            newObject = spawnEffectCrushFlash( x, y );
            break;
/*
        case FLASH:
        newObject = spawnEffectFlash(x, y);
        break;
*/
/*        
        case SMOKE_POOF:
        newObject = spawnSmokePoof(x, y);
        break;
*/
//////// GAME - END

        case INVALID:
        case OBJECT_COUNT:
            DebugLog.e( "GameObjectFactoryImpl", "Invalid value in spawn" );
            break;
        }

        return newObject;
    }


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

//////// TOP VIEW - BEGIN
    public GameObject spawnPlayer( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

//////// PANG - MID
        // load textures related to weapons
//        textureLibrary.allocateTexture( R.drawable.harpoon );
        textureLibrary.allocateTexture( R.drawable.effect_harpoon01 );
        textureLibrary.allocateTexture( R.drawable.effect_harpoon02 );
//////// PANG - END

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 64;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PLAYER );

        if ( staticData == null )
        {
//            final int staticObjectCount = 13;
            final int staticObjectCount = 12;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );
            PhysicsComponent physics = ( PhysicsComponent )allocateComponent( PhysicsComponent.class );

// !!!! TODO : adapt values ... !!!!
// => make it less "bouncy" ... (done here ?)
            physics.setMass( 9.1f ); // ~90kg w/ earth gravity
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
            SpriteAnimation idle = new SpriteAnimation( PlayerAnimations.IDLE.ordinal(), 1 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stand ),
                                               1.0f,
                                               pressAndCollectVolume,
                                               basicVulnerabilityVolume ) );

            SpriteAnimation angle = new SpriteAnimation( PlayerAnimations.MOVE.ordinal(), 1 );
            angle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_diag01 ),
                                                0.0416f,
                                                pressAndCollectVolume,
                                                basicVulnerabilityVolume ) );

            SpriteAnimation extremeAngle = new SpriteAnimation( PlayerAnimations.MOVE_FAST.ordinal(), 1 );
            extremeAngle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_diagmore01 ),
                                                       0.0416f,
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

            SpriteAnimation up = new SpriteAnimation( PlayerAnimations.JUMP_UP.ordinal(), 2 );
            up.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jumpup01 ),
                                             Utils.framesToTime( 24, 1 ),
                                             pressAndCollectVolume,
                                             basicVulnerabilityVolume ) );
            up.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jumpup02 ),
                                             Utils.framesToTime( 24, 1 ),
                                             pressAndCollectVolume,
                                             basicVulnerabilityVolume ) );
            up.setLoop( true );

            SpriteAnimation boostAngle = new SpriteAnimation( PlayerAnimations.JUMP_MOVE.ordinal(), 2 );
            boostAngle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jumpdiag01 ),
                                                     Utils.framesToTime( 24, 1 ),
                                                     pressAndCollectVolume,
                                                     basicVulnerabilityVolume ) );
            boostAngle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_jumpdiag02 ),
                                                     Utils.framesToTime( 24, 1 ),
                                                     pressAndCollectVolume,
                                                     basicVulnerabilityVolume ) );
            boostAngle.setLoop( true );

// !!!! TODO : add falling states/anims !!!!
////            SpriteAnimation boostExtremeAngle = new SpriteAnimation( PlayerAnimations.BOOST_MOVE_FAST.ordinal(), 2 );
//            SpriteAnimation boostExtremeAngle = new SpriteAnimation( PlayerAnimations.JUMP_MOVE_FAST.ordinal(), 2 );
//            boostExtremeAngle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.andou_diagmore02 ),
//                                                            Utils.framesToTime( 24, 1 ),
//                                                            pressAndCollectVolume,
//                                                            basicVulnerabilityVolume ) );
//            boostExtremeAngle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.andou_diagmore03 ),
//                                                            Utils.framesToTime( 24, 1 ),
//                                                            pressAndCollectVolume,
//                                                            basicVulnerabilityVolume ) );
//            boostExtremeAngle.setLoop( true );

            FixedSizeArray<CollisionVolume> stompAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
            stompAttackVolume.add( new AABoxCollisionVolume( 16, -5.0f, 32, 37, HitType.HIT ) );
            stompAttackVolume.add( pressCollisionVolume );
            stompAttackVolume.add( collectionVolume );

            SpriteAnimation stomp = new SpriteAnimation( PlayerAnimations.STOMP.ordinal(), 4 );
            stomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stomp01 ),
                                                Utils.framesToTime( 24, 1 ),
                                                stompAttackVolume,
                                                null ) );
            stomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stomp02 ),
                                                Utils.framesToTime( 24, 1 ),
                                                stompAttackVolume,
                                                null ) );
            stomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stomp03 ),
                                                Utils.framesToTime( 24, 1),
                                                stompAttackVolume,
                                                null ) );
            stomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stomp04 ),
                                                Utils.framesToTime( 24, 1 ),
                                                stompAttackVolume,
                                                null ) );

            SpriteAnimation hitReactAnim = new SpriteAnimation( PlayerAnimations.HIT_REACT.ordinal(), 1 );
            hitReactAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_hit ),
                                                       0.1f,
                                                       pressAndCollectVolume,
                                                       null ) );

            SpriteAnimation deathAnim = new SpriteAnimation( PlayerAnimations.DEATH.ordinal(), 16 );
            AnimationFrame death1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die01 ),
                                                        Utils.framesToTime( 24, 1 ),
                                                        null,
                                                        null );
            AnimationFrame death2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die02 ),
                                                        Utils.framesToTime( 24, 1 ),
                                                        null,
                                                        null );
            deathAnim.addFrame( death1 );
            deathAnim.addFrame( death2 );
            deathAnim.addFrame( death1 );
            deathAnim.addFrame( death2 );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode01 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode02 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode03 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode04 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode05 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode06 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode07 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode08 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode09 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode10 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode11 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode12 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );

            SpriteAnimation frozenAnim = new SpriteAnimation( PlayerAnimations.FROZEN.ordinal(), 1 );
            // Frozen has no frames!


            // Save static data
            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );

            staticData.add( idle );
            staticData.add( angle );
            staticData.add( extremeAngle );
            staticData.add( up );
            staticData.add( boostAngle );
//            staticData.add( boostExtremeAngle );
            staticData.add( stomp );
            staticData.add( hitReactAnim );
            staticData.add( deathAnim );
            staticData.add( frozenAnim );

            setStaticData( GameObjectType.PLAYER, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.PLAYER );
        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgcollision.setSize( 32, 48 );
        bgcollision.setOffset( 16, 0 );

        PlayerComponentSide player = ( PlayerComponentSide )allocateComponent( PlayerComponentSide.class );
        AnimationComponentSide animation = ( AnimationComponentSide )allocateComponent( AnimationComponentSide.class );
        animation.setPlayer( player );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            animation.setLandThump( sound.load( R.raw.thump ) );
//            animation.setRocketSound( sound.load( R.raw.rockets ) );
//            animation.setRubySounds( sound.load( R.raw.gem1 ), sound.load( R.raw.gem2 ), sound.load( R.raw.gem3 ) );
            animation.setExplosionSound( sound.load( R.raw.sound_explode ) );
        }

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );
        animation.setSprite( sprite );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        hitReact.setBounceOnTakeHit( true );
        hitReact.setPauseOnAttack( true );
        hitReact.setInvincibleTime( 3.0f );
        hitReact.setSpawnOnDealHit( HitType.HIT, GameObjectType.CRUSH_FLASH.ordinal(), false, true );

        if ( sound != null )
        {
            hitReact.setTakeHitSound( HitType.HIT, sound.load( R.raw.deep_clang ) );
        }

        dynamicCollision.setHitReactionComponent( hitReact );
        player.setHitReactionComponent( hitReact );

        InventoryComponent inventory = ( InventoryComponent )allocateComponent( InventoryComponent.class );
        UpdateRecordImpl record = new UpdateRecordImpl();
        record.resetCoinsCounter();
        inventory.setInventory( record );
        player.setInventory( inventory );
//        animation.setInventory( inventory );

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
//////// PANG - MID
        LaunchProjectileComponent harpoon = ( LaunchProjectileComponent )allocateComponent( LaunchProjectileComponent.class );
        harpoon.setRequiredAction( ActionType.ATTACK );
        harpoon.enableProjectileTracking( 1 );
        harpoon.setSetsPerActivation( 1 );
        harpoon.setShotsPerSet( 1 );
        harpoon.setObjectTypeToSpawn( GameObjectType.HARPOON.ordinal() );
//        harpoon.setOffsetX( 32 );
//        harpoon.setOffsetY( 15 );
//        harpoon.setVelocityX( -150.0f );
//        harpoon.setVelocityY( 100.0f );
//        harpoon.setThetaError( 0.1f );
//        LifetimeComponent lifetime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
//////// PANG - END

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

        object.life = EventRecorderImpl.getDifficultyConstants().getPlayerMaxLife();
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
//////// PANG - MID
        object.add( harpoon );
//        object.add( lifetime );
//////// PANG - END

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
//////// TOP VIEW - MID
/*
    public GameObject spawnPlayer( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 64;
        object.height = 64;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.PLAYER );

        if ( staticData == null )
        {
//            final int staticObjectCount = 13;
            final int staticObjectCount = 12;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

//            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );
            PhysicsComponent physics = ( PhysicsComponent )allocateComponent( PhysicsComponent.class );

// !!!! TODO : adapt values ... !!!!
// => make it less "bouncy" ... (done here ?)
            physics.setMass( 9.1f ); // ~90kg w/ earth gravity
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
            SpriteAnimation idle = new SpriteAnimation( PlayerAnimations.IDLE.ordinal(), 1 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stand ),
                                               1.0f,
                                               pressAndCollectVolume,
                                               basicVulnerabilityVolume ) );

            SpriteAnimation angle = new SpriteAnimation( PlayerAnimations.MOVE.ordinal(), 1 );
            angle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_diag01 ),
                                                0.0416f,
                                                pressAndCollectVolume,
                                                basicVulnerabilityVolume ) );

            SpriteAnimation extremeAngle = new SpriteAnimation( PlayerAnimations.MOVE_FAST.ordinal(), 1 );
            extremeAngle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_diagmore01 ),
                                                       0.0416f,
                                                       pressAndCollectVolume,
                                                       basicVulnerabilityVolume ) );

// !!!! TODO : add falling states/anims !!!!
////            SpriteAnimation boostExtremeAngle = new SpriteAnimation( PlayerAnimations.BOOST_MOVE_FAST.ordinal(), 2 );
//            SpriteAnimation boostExtremeAngle = new SpriteAnimation( PlayerAnimations.JUMP_MOVE_FAST.ordinal(), 2 );
//            boostExtremeAngle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.andou_diagmore02 ),
//                                                            Utils.framesToTime( 24, 1 ),
//                                                            pressAndCollectVolume,
//                                                            basicVulnerabilityVolume ) );
//            boostExtremeAngle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.andou_diagmore03 ),
//                                                            Utils.framesToTime( 24, 1 ),
//                                                            pressAndCollectVolume,
//                                                            basicVulnerabilityVolume ) );
//            boostExtremeAngle.setLoop( true );

//            FixedSizeArray<CollisionVolume> stompAttackVolume = new FixedSizeArray<CollisionVolume>( 3 );
//            stompAttackVolume.add( new AABoxCollisionVolume( 16, -5.0f, 32, 37, HitType.HIT ) );
//            stompAttackVolume.add( pressCollisionVolume );
//            stompAttackVolume.add( collectionVolume );
//
//            SpriteAnimation stomp = new SpriteAnimation( PlayerAnimations.STOMP.ordinal(), 4 );
//            stomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stomp01 ),
//                                                Utils.framesToTime( 24, 1 ),
//                                                stompAttackVolume,
//                                                null ) );
//            stomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stomp02 ),
//                                                Utils.framesToTime( 24, 1 ),
//                                                stompAttackVolume,
//                                                null ) );
//            stomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stomp03 ),
//                                                Utils.framesToTime( 24, 1),
//                                                stompAttackVolume,
//                                                null ) );
//            stomp.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_stomp04 ),
//                                                Utils.framesToTime( 24, 1 ),
//                                                stompAttackVolume,
//                                                null ) );

            SpriteAnimation hitReactAnim = new SpriteAnimation( PlayerAnimations.HIT_REACT.ordinal(), 1 );
            hitReactAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_hit ),
                                                       0.1f,
                                                       pressAndCollectVolume,
                                                       null ) );

            SpriteAnimation deathAnim = new SpriteAnimation( PlayerAnimations.DEATH.ordinal(), 16 );
            AnimationFrame death1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die01 ),
                                                        Utils.framesToTime( 24, 1 ),
                                                        null,
                                                        null );
            AnimationFrame death2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_die02 ),
                                                        Utils.framesToTime( 24, 1 ),
                                                        null,
                                                        null );
            deathAnim.addFrame( death1 );
            deathAnim.addFrame( death2 );
            deathAnim.addFrame( death1 );
            deathAnim.addFrame( death2 );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode01 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode02 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode03 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode04 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode05 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode06 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode07 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode08 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode09 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode10 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode11 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );
            deathAnim.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.player_explode12 ),
                                                    Utils.framesToTime( 24, 1 ),
                                                    null,
                                                    null ) );

            SpriteAnimation frozenAnim = new SpriteAnimation( PlayerAnimations.FROZEN.ordinal(), 1 );
            // Frozen has no frames!


            // Save static data
//            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );

            staticData.add( idle );
            staticData.add( angle );
            staticData.add( extremeAngle );
//            staticData.add( up );
//            staticData.add( boostAngle );
//            staticData.add( boostExtremeAngle );
//            staticData.add( stomp );
            staticData.add( hitReactAnim );
            staticData.add( deathAnim );
            staticData.add( frozenAnim );

            setStaticData( GameObjectType.PLAYER, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.PLAYER );
        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgcollision.setSize( 32, 48 );
        bgcollision.setOffset( 16, 0 );

        PlayerComponentTop player = ( PlayerComponentTop )allocateComponent( PlayerComponentTop.class );
        AnimationComponentTop animation = ( AnimationComponentTop )allocateComponent( AnimationComponentTop.class );
        animation.setPlayer( player );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
//            animation.setLandThump( sound.load( R.raw.thump ) );
//            animation.setRocketSound( sound.load( R.raw.rockets ) );
//            animation.setRubySounds( sound.load( R.raw.gem1 ), sound.load( R.raw.gem2 ), sound.load( R.raw.gem3 ) );
            animation.setExplosionSound( sound.load( R.raw.sound_explode ) );
        }

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );
        animation.setSprite( sprite );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        hitReact.setBounceOnTakeHit( true );
        hitReact.setPauseOnAttack( true );
        hitReact.setInvincibleTime( 3.0f );
        hitReact.setSpawnOnDealHit( HitType.HIT, GameObjectType.CRUSH_FLASH.ordinal(), false, true );

        if ( sound != null )
        {
            hitReact.setTakeHitSound( HitType.HIT, sound.load( R.raw.deep_clang ) );
        }

        dynamicCollision.setHitReactionComponent( hitReact );
        player.setHitReactionComponent( hitReact );

        InventoryComponent inventory = ( InventoryComponent )allocateComponent( InventoryComponent.class );
        UpdateRecordImpl record = new UpdateRecordImpl();
        record.resetCoinsCounter();
        inventory.setInventory( record );
        player.setInventory( inventory );
//        animation.setInventory( inventory );

        ChangeComponentsComponent damageSwap = ( ChangeComponentsComponent )allocateComponent( ChangeComponentsComponent.class );
        animation.setDamageSwap( damageSwap );

        object.life = EventRecorderImpl.getDifficultyConstants().getPlayerMaxLife();
        object.team = Team.PLAYER;

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

        addStaticData( GameObjectType.PLAYER, object, sprite );

        sprite.playAnimation( PlayerAnimations.IDLE.ordinal() );


        CameraSystem camera = sSystemRegistry.cameraSystem;
        if ( camera != null )
        {
            camera.setTarget( object );
        }

        return object;
    }
*/
//////// TOP VIEW - END

    public GameObject spawnCoin( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

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

            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = null;
/*
            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = null; /* new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.COLLECT );
*/

            SpriteAnimation idle = new SpriteAnimation( 0, 5 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_coin01),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_coin02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_coin03),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_coin04),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_coin05 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.setLoop( true );

            UpdateRecordImpl addCoin = new UpdateRecordImpl();
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

// !!!! ???? TODO : what is that for ? ???? !!!!
//        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
//        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            hitReact.setTakeHitSound( HitType.COLLECT, sound.load( R.raw.ding ) );
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
        life.setIncrementEventCounter( EventRecorderImpl.COUNTER_COINS_COLLECTED, value );

        object.life = 1;

        object.add( render );
        object.add( sprite );
//        object.add( dynamicCollision );
        object.add( hitPlayer );
        object.add( hitReact );
        object.add( life );

        addStaticData( GameObjectType.COIN, object, sprite );
        sprite.playAnimation( 0 );

        EventRecorder recorder = sSystemRegistry.eventRecorder;
        recorder.incrementEventCounter( EventRecorderImpl.COUNTER_COINS_TOTAL, value );

        return object;
    }

/*
    public GameObject spawnPickupLife( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

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

            UpdateRecordImpl addBonus = new UpdateRecordImpl();
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

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            hitReact.setTakeHitSound( HitType.COLLECT, sound.load( R.raw.object_pickup_1up ) );
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
        life.setIncrementEventCounter( EventRecorderImpl.COUNTER_LIVES );

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
*/

//////// SPEEDUP - BEGIN
    public GameObject spawnPickupSpeedUp( float positionX, float positionY )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

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

            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = null;
//            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
//            basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
//            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.COLLECT );

            SpriteAnimation idle = new SpriteAnimation( 0, 4 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_speedup_01 ),
                                               Utils.framesToTime( 24, 30 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_speedup_02 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_speedup_03 ),
                                               Utils.framesToTime( 24, 2 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.object_pickup_speedup_04 ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               basicVulnerabilityVolume ) );
            idle.setLoop( true );

            UpdateRecordImpl addBonus = new UpdateRecordImpl();
            addBonus.mSpeedUpCount = 1;

            staticData.add( addBonus );
            staticData.add( idle );

            setStaticData( GameObjectType.PICKUP_SPEEDUP, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.GENERAL_OBJECT );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

//        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
//        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        hitReact.setDieWhenCollected( true );
        hitReact.setInvincible( true );

        HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
        hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            hitReact.setTakeHitSound( HitType.COLLECT, sound.load( R.raw.object_pickup_speedup ) );
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
        life.setIncrementEventCounter( EventRecorderImpl.COUNTER_SPEEDUP, 1 );

        object.life = 1;

        object.add( render );
        object.add( sprite );
//        object.add( dynamicCollision );
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
/*
            basicAttackVolume.add( new AABoxCollisionVolume( 16, 0, 32, 48, HitType.HIT ) );
            basicAttackVolume.add( new AABoxCollisionVolume( 16, 1, 32, 16, HitType.DEPRESS ) );
*/

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

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
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
/*
        GhostComponent ghost = ( GhostComponent )allocateComponent( GhostComponent.class );
        ghost.setMovementSpeed( 500.0f );
        ghost.setAcceleration( 1000.0f );
        ghost.setJumpImpulse( 300.0f );
        ghost.setKillOnRelease( true );
        ghost.setDelayOnRelease( 1.5f );

        SoundSystem sound = BaseObject.sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            ghost.setAmbientSound( sound.load( R.raw.sound_possession ) );
        }

        ChangeComponentsComponent ghostSwap = ( ChangeComponentsComponent )allocateComponent( ChangeComponentsComponent.class );
        ghostSwap.addSwapInComponent( ghost );
        ghostSwap.addSwapOutComponent( patrol );

        SimplePhysicsComponent ghostPhysics = ( SimplePhysicsComponent )allocateComponent( SimplePhysicsComponent.class );
        ghostPhysics.setBounciness( 0.0f );
*/

        object.add( render);
        object.add( sprite);

        object.add( bgcollision );
        object.add( animation );
        object.add( patrol );
        object.add( collision );
        object.add( hitReact );
        object.add( lifetime );
// !!!! TODO : check if want to keep this !!!!
/*
        object.add( ghostSwap );
*/

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
/*
        SimplePhysicsComponent normalPhysics = object.findByClass( SimplePhysicsComponent.class );
        if ( normalPhysics != null )
        {
            ghostSwap.addSwapOutComponent( normalPhysics );
        }
        ghostSwap.addSwapInComponent( ghostPhysics );
*/

        sprite.playAnimation( 0 );

// !!!! TODO : check if want to keep this !!!!
/*
        // Sparks
        setupEnemySparks();

        RenderComponent sparksRender = ( RenderComponent )allocateComponent( RenderComponent.class );
        sparksRender.setPriority( render.getPriority() + 1 );
        SpriteComponent sparksSprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sparksSprite.setSize( 64, 64 );
        sparksSprite.setRenderComponent( sparksRender );

        addStaticData( GameObjectType.ENEMY_SPARKS, object, sparksSprite );

        sparksSprite.playAnimation( 0 );

        ghostSwap.addSwapInComponent( sparksSprite );
        ghostSwap.addSwapInComponent( sparksRender );

        hitReact.setPossessionComponent( ghostSwap );
*/

        return object;
    }

/*
public GameObject spawnBrobotBullet(float positionX, float positionY, boolean flipHorizontal) {
TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;


GameObject object = mGameObjectPool.allocate();
object.getPosition().set(positionX, positionY);
object.activationRadius = mTightActivationRadius;
object.width = 64;
object.height = 64;

FixedSizeArray<BaseObject> staticData = getStaticData(GameObjectType.BROBOT_BULLET);
if (staticData == null) {
final int staticObjectCount = 2;
staticData = new FixedSizeArray<BaseObject>(staticObjectCount);

GameComponent movement = allocateComponent(MovementComponent.class);

SpriteAnimation idle = new SpriteAnimation(0, 3);
idle.addFrame(new AnimationFrame(
textureLibrary.allocateTexture(R.drawable.enemy_brobot_walk01), 
Utils.framesToTime(24, 1), null, null));
idle.addFrame(new AnimationFrame(
textureLibrary.allocateTexture(R.drawable.enemy_brobot_walk02), 
Utils.framesToTime(24, 1), null, null));
idle.addFrame(new AnimationFrame(
textureLibrary.allocateTexture(R.drawable.enemy_brobot_walk03), 
Utils.framesToTime(24, 1), null, null));
idle.setLoop(true);

staticData.add(movement);
staticData.add(idle);

setStaticData(GameObjectType.BROBOT_BULLET, staticData);
}

RenderComponent render = (RenderComponent)allocateComponent(RenderComponent.class);
render.setPriority(SortConstants.PROJECTILE);

LifetimeComponent lifetime = (LifetimeComponent)allocateComponent(LifetimeComponent.class);
lifetime.setTimeUntilDeath(3.0f);

SpriteComponent sprite = (SpriteComponent)allocateComponent(SpriteComponent.class);
sprite.setSize((int)object.width, (int)object.height);
sprite.setRenderComponent(render);

object.life = 1;
object.team = Team.ENEMY;
object.destroyOnDeactivation = true;

if (flipHorizontal) {
object.facingDirection.x = -1.0f;
}

object.add(lifetime);
object.add(render);
object.add(sprite);


addStaticData(GameObjectType.BROBOT_BULLET, object, sprite);

sprite.playAnimation(0);

return object;
}
*/


//////// PANG - MID
    public GameObject spawnHarpoon( float positionX, float positionY, boolean flipHorizontal )
    {
DebugLog.e( "FACTORY", "Harpoon" );

        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;
// This is pretty heavy-handed.
// TODO: figure out a general solution for objects that depend on other objects.
        textureLibrary.allocateTexture( R.drawable.effect_harpoon01 );
        textureLibrary.allocateTexture( R.drawable.effect_harpoon02 );

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 8;
        object.height = 8;

// !!!! TODO: could have visual data if want to see harpoon ... !!!!
/*
        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.HARPOON );
        if ( staticData == null )
        {
            final int staticObjectCount = 1;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
// !!!! ???? TODO: sphere or rectangle ? ???? !!!!
            basicVulnerabilityVolume.add( new SphereCollisionVolume( 32, 32, 32 ) );
// !!!! ???? TODO: possess ? ???? !!!!
            basicVulnerabilityVolume.get( 0 ).setHitType( HitType.POSSESS );

            SpriteAnimation idle = new SpriteAnimation( 0, 1 );
            idle.addFrame(
                new AnimationFrame(
                    textureLibrary.allocateTexture( R.drawable.harpoon ),
                                                    1.0f,
                                                    null,
                                                    basicVulnerabilityVolume ) );

            staticData.add( idle );

            setStaticData( GameObjectType.HARPOON, staticData );
        }
*/

//        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
//        render.setPriority( SortConstants.GENERAL_OBJECT );

//        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
//        sprite.setSize( ( int )object.width, ( int )object.height );
//        sprite.setRenderComponent( render );

//        DynamicCollisionComponent collision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
//        sprite.setCollisionComponent( collision );

//        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
//        collision.setHitReactionComponent( hitReact );

        LaunchChainComponent spawner = ( LaunchChainComponent )allocateComponent( LaunchChainComponent.class );
        spawner.setParent( object );
        spawner.setObjectTypeToSpawn( GameObjectType.HARPOON_HEAD.ordinal() );
//        spawner.setDelayBeforeFirstSet( 1.0f );
        spawner.setDelayBetweenShots( 0.1f );
        spawner.setOffsetX( 36 );
        spawner.setOffsetY( 32 );
        spawner.setVelocityY( 100.0f );
        spawner.enableProjectileTracking( 50 );

        LifetimeComponent lifetime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );

        object.team = Team.PLAYER;

        if ( flipHorizontal )
        {
            object.facingDirection.x = -1.0f;
        }
        else
        {
            object.facingDirection.x = 1.0f;
        }

//        object.add( render );
//        object.add( sprite );
        object.add( spawner );
//        object.add( collision );
//        object.add( hitReact );
        object.add( lifetime );

//         addStaticData( GameObjectType.HARPOON, object, sprite );
         addStaticData( GameObjectType.HARPOON, object, null );

        object.commitUpdates();

//        sprite.playAnimation(0);

        return object;
    }

    public GameObject spawnHarpoonHead( float positionX, float positionY, boolean flipHorizontal )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mAlwaysActive;
        object.width = 16;
        object.height = 16;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.HARPOON_HEAD );
        if ( staticData == null )
        {
            final int staticObjectCount = 2;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent movement = allocateComponent( MovementComponent.class );

            FixedSizeArray<CollisionVolume> basicAttackVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicAttackVolume.add( new SphereCollisionVolume( 8, 8, 8, HitType.HIT ) );

            SpriteAnimation idle = new SpriteAnimation( 0, 2 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.effect_harpoon01 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.effect_harpoon02 ),
                                               Utils.framesToTime( 24, 1 ),
                                               basicAttackVolume,
                                               null ) );
            idle.setLoop( true );

            staticData.add( movement );
            staticData.add( idle );

            setStaticData( GameObjectType.HARPOON_HEAD, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.PROJECTILE );

        LifetimeComponent lifetime = ( LifetimeComponent )allocateComponent( LifetimeComponent.class );
        lifetime.setTimeUntilDeath( 3.0f );
        lifetime.setDieOnHitBackground( true );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        hitReact.setDieOnAttack( true );

        dynamicCollision.setHitReactionComponent( hitReact );

        SimpleCollisionComponent collision = ( SimpleCollisionComponent )allocateComponent( SimpleCollisionComponent.class );

        object.life = 1;
        object.team = Team.PLAYER;
        object.destroyOnDeactivation = true;

        if ( flipHorizontal )
        {
            object.facingDirection.x = -1.0f;
        }

        object.add( lifetime );
        object.add( render );
        object.add( sprite );
        object.add( dynamicCollision );
        object.add( hitReact );
        object.add( collision );

        addStaticData( GameObjectType.HARPOON_HEAD, object, sprite );

        sprite.playAnimation( 0 );

        return object;
    }
//////// PANG - END

//////// NPC - MID
/*
    public GameObject spawnEnemyEvilKabocha( float positionX, float positionY, boolean flipHorizontal )
    {
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;

        GameObject object = mGameObjectPool.allocate();
        object.getPosition().set( positionX, positionY );
        object.activationRadius = mNormalActivationRadius;
        object.width = 128;
        object.height = 128;

        FixedSizeArray<BaseObject> staticData = getStaticData( GameObjectType.EVIL_KABOCHA );
        if ( staticData == null )
        {
            final int staticObjectCount = 8;
            staticData = new FixedSizeArray<BaseObject>( staticObjectCount );

            GameComponent gravity = allocateComponent( GravityComponent.class );
            GameComponent movement = allocateComponent( MovementComponent.class );

            SimplePhysicsComponent physics = ( SimplePhysicsComponent )allocateComponent( SimplePhysicsComponent.class );
            physics.setBounciness( 0.0f );

            FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
            basicVulnerabilityVolume.add( new AABoxCollisionVolume( 52, 5, 26, 80, HitType.HIT ) );

            SpriteAnimation idle = new SpriteAnimation( NPCAnimationComponent.IDLE, 1 );
            idle.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_stand ),
                                               Utils.framesToTime( 24, 1 ),
                                               null,
                                               basicVulnerabilityVolume ) );

            AnimationFrame walkFrame1 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_walk01 ),
                                                            Utils.framesToTime( 24, 3 ),
                                                            null,
                                                            basicVulnerabilityVolume );
            AnimationFrame walkFrame2 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_walk02 ),
                                                            Utils.framesToTime( 24, 3 ),
                                                            null,
                                                            basicVulnerabilityVolume );
            AnimationFrame walkFrame3 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_walk03 ),
                                                            Utils.framesToTime( 24, 3 ),
                                                            null,
                                                            basicVulnerabilityVolume );
            AnimationFrame walkFrame4 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_walk04 ),
                                                            Utils.framesToTime( 24, 3 ),
                                                            null,
                                                            basicVulnerabilityVolume );
            AnimationFrame walkFrame5 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_walk05 ),
                                                            Utils.framesToTime( 24, 3 ),
                                                            null,
                                                            basicVulnerabilityVolume );
            AnimationFrame walkFrame6 = new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_walk06 ),
                                                            Utils.framesToTime( 24, 3 ),
                                                            null,
                                                            basicVulnerabilityVolume );

            SpriteAnimation walk = new SpriteAnimation( NPCAnimationComponent.WALK, 6 );
            walk.addFrame( walkFrame1 );
            walk.addFrame( walkFrame2 );
            walk.addFrame( walkFrame3 );
            walk.addFrame( walkFrame4 );
            walk.addFrame( walkFrame5 );
            walk.addFrame( walkFrame6 );

            walk.setLoop( true );


            SpriteAnimation surprised = new SpriteAnimation( NPCAnimationComponent.SURPRISED, 1 );
            surprised.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_surprised ),
                                                    4.0f,
                                                    null,
                                                    null ) );


            SpriteAnimation hit = new SpriteAnimation( NPCAnimationComponent.TAKE_HIT, 2 );
            hit.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_hit01 ),
                                              Utils.framesToTime( 24, 1 ),
                                              null,
                                              null ) );
            hit.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_hit02 ),
                                              Utils.framesToTime( 24, 10 ),
                                              null,
                                              null ) );

            SpriteAnimation die = new SpriteAnimation( NPCAnimationComponent.DEATH, 5 );
            die.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_die01 ),
                                              Utils.framesToTime( 24, 6 ),
                                              null,
                                              null ) );
            die.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_stand ),
                                              Utils.framesToTime( 24, 2 ),
                                              null,
                                              null ) );
            die.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_die02 ),
                                              Utils.framesToTime( 24, 2 ),
                                              null,
                                              null ) );
            die.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_die03 ),
                                              Utils.framesToTime( 24, 2 ),
                                              null,
                                              null ) );
            die.addFrame( new AnimationFrame( textureLibrary.allocateTexture( R.drawable.enemy_kabocha_evil_die04 ),
                                              Utils.framesToTime( 24, 6 ),
                                              null,
                                              null ) );

            staticData.add( gravity );
            staticData.add( movement );
            staticData.add( physics );
            staticData.add( idle );
            staticData.add( walk );
            staticData.add( surprised );
            staticData.add( hit );
            staticData.add( die );

            setStaticData( GameObjectType.EVIL_KABOCHA, staticData );
        }

        RenderComponent render = ( RenderComponent )allocateComponent( RenderComponent.class );
        render.setPriority( SortConstants.NPC );

        BackgroundCollisionComponent bgcollision = ( BackgroundCollisionComponent )allocateComponent( BackgroundCollisionComponent.class );
        bgcollision.setSize( 38, 82 );
        bgcollision.setOffset( 45, 5 );

        SpriteComponent sprite = ( SpriteComponent )allocateComponent( SpriteComponent.class );
        sprite.setSize( ( int )object.width, ( int )object.height );
        sprite.setRenderComponent( render );

        NPCAnimationComponent animation = ( NPCAnimationComponent )allocateComponent( NPCAnimationComponent.class );
        animation.setSprite( sprite );

        ChannelSystem.Channel surpriseChannel = null;
        ChannelSystem channelSystem = BaseObject.sSystemRegistry.channelSystem;
        surpriseChannel = channelSystem.registerChannel( sSurprisedNPCChannel );
        animation.setChannel( surpriseChannel );
        animation.setChannelTrigger( NPCAnimationComponent.SURPRISED );

        NPCComponent patrol = ( NPCComponent )allocateComponent( NPCComponent.class );
        patrol.setSpeeds( 50.0f, 50.0f, 0.0f, -10.0f, 200.0f );
        patrol.setReactToHits( true );
        patrol.setGameEvent( GameFlowEvent.EVENT_SHOW_ANIMATION, AnimationPlayerActivity.ROKUDOU_ENDING, true );

        DynamicCollisionComponent collision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( collision );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        collision.setHitReactionComponent( hitReact );

        SoundSystem sound = sSystemRegistry.soundSystem;
        if ( sound != null )
        {
            hitReact.setTakeHitSound( HitType.HIT, sound.load( R.raw.sound_kabocha_hit ) );
        }

        patrol.setHitReactionComponent( hitReact );

        object.team = Team.ENEMY;
        object.life = 3;

        if ( flipHorizontal )
        {
            object.facingDirection.x = -1.0f;
        }

        object.add( render );
        object.add( sprite );
        object.add( bgcollision );
        object.add( animation );
        object.add( patrol );
        object.add( collision );
        object.add( hitReact );

        addStaticData( GameObjectType.EVIL_KABOCHA, object, sprite );


        sprite.playAnimation( 0 );

        return object;
    }
*/
//////// NPC - END

//////// dialogs - m
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

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
        dynamicCollision.setHitReactionComponent( hitReact );
        hitReact.setSpawnGameEventOnTakeHit( HitType.DIALOG,
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

/*
            final int blue_frames[] = { R.drawable.object_door_blue01,
                                        R.drawable.object_door_blue02,
                                        R.drawable.object_door_blue03,
                                        R.drawable.object_door_blue04 };

            final int green_frames[] = { R.drawable.object_door_green01,
                                         R.drawable.object_door_green02,
                                         R.drawable.object_door_green03,
                                         R.drawable.object_door_green04 };
*/

            int frames[] = red_frames;
/*
            if ( type == GameObjectType.DOOR_GREEN )
            {
                frames = green_frames;
            }
            else if ( type == GameObjectType.DOOR_BLUE )
            {
                frames = blue_frames;
            }
*/

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
/*
        case DOOR_BLUE:
            doorChannel = channelSystem.registerChannel( sBlueButtonChannel );
            break;
        case DOOR_GREEN:
            doorChannel = channelSystem.registerChannel( sGreenButtonChannel );
            break;
*/
        }
        doorAnim.setChannel( doorChannel );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
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

/*
            final int blue_frames[] = { R.drawable.object_button_blue,
                                        R.drawable.object_button_pressed_blue };

            final int green_frames[] = { R.drawable.object_button_green,
                                         R.drawable.object_button_pressed_green };
*/

            int frames[] = red_frames;
/*
            if ( type == GameObjectType.BUTTON_GREEN )
            {
                frames = green_frames;
            }
            else if ( type == GameObjectType.BUTTON_BLUE )
            {
                frames = blue_frames;
            }
*/

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
/*
        case BUTTON_BLUE:
            buttonChannel = channelSystem.registerChannel( sBlueButtonChannel );
            break;
        case BUTTON_GREEN:
            buttonChannel = channelSystem.registerChannel( sGreenButtonChannel );
            break;
*/
        }
        button.setChannel( buttonChannel );

        DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
        sprite.setCollisionComponent( dynamicCollision );

        HitReactionComponent hitReact = ( HitReactionComponent )allocateComponent( HitReactionComponent.class );
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

//////// GAME - END

}
