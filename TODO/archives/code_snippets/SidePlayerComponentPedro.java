package derek.android.gameouya;

import derek.android.engine.InventoryComponent;
import derek.android.engine.GameObject;
import derek.android.gameouya.GameObjectFactoryImpl.GameObjectType;

//import derek.android.engine.DebugLog;


/*
*/

public class PlayerComponentSidePedro extends PlayerComponentSideSpecific
{
//////// SPEEDUP - BEGIN
//    private float mSpeedUpEndTime;
//    private float mSpeedUpFactor;
//////// SPEEDUP - END


    @Override
    public void reset()
    {
        super.reset();

//////// SPEEDUP - BEGIN
//        mSpeedUpEndTime = -1.0f;
//        mSpeedUpFactor = 1.0f;
//////// SPEEDUP - END
    }

    @Override
    protected void preUpdateProcess( GameObject parent )
    {
        super.preUpdateProcess( parent );

//////// SPEEDUP - BEGIN
/*
        final TimeSystem timeSystem = sSystemRegistry.timeSystem;
        if ( mSpeedUpEndTime > timeSystem.getGameTime() )
        {
// !!!! TODO: hack ... should find other way ... !!!!
            mTouchingGround = mTouchingGround
                               && ( timeSystem.getGameTime() < ( parentObject.getLastTouchedFloorTime() + ( parentObject.getCollisionSurfaceDecayTime() / mSpeedUpFactor ) ) );

            GameObjectFactoryImpl factory = ( GameObjectFactoryImpl )sSystemRegistry.gameObjectFactory;
            GameObjectManager manager = sSystemRegistry.gameObjectManager;
            if ( ( factory != null ) && ( manager != null ) )
            {
                final float x = parentObject.getPosition().x;
                final float y = parentObject.getPosition().y;
                GameObject smoke1 = factory.spawnDust( x, y - 16, true );
                manager.add( smoke1 );
            }
        }
*/
//////// SPEEDUP - END
    }

/*
    @Override
    protected void getMovement( float time, Vector2 impulse )
    {
        final InputGameInterfaceImpl input = ( InputGameInterfaceImpl )sSystemRegistry.inputGameInterface;
        if ( input != null )
        {
            final InputXY dpad = input.getMovementPad();
            if ( dpad.getPressed() )
            {
                impulse.set( dpad.getX(), 0.0f );
            }

            final InputButton jumpButton = input.getJumpButton();
            if ( jumpButton.getPressed() )
            {
                if ( jumpButton.getTriggered( time ) && mTouchingGround )
                {
                    impulse.y = 1.0f;
                }
            }
        }
    }
*/
/*
    @Override
    protected boolean isAttacking( float time )
    {
        final InputGameInterfaceImpl input = ( InputGameInterfaceImpl )sSystemRegistry.inputGameInterface;
        final InputButton attackButton = input.getAttackButton();

//        if ( attackButton.getTriggered( time ) && !mTouchingGround )
        if ( attackButton.getTriggered( time ) )
        {
            return true;
        }
        return false;
    }
*/
/*
    @Override
    protected void startAttack( GameObject parentObject )
    {
// !!!! TODO: should depend on whether the player is pressing a direction or not !!!!
//        mStompAttack = !mTouchingGround;
        final InputGameInterfaceImpl input = ( InputGameInterfaceImpl )sSystemRegistry.inputGameInterface;
        final InputXY dpad = input.getMovementPad();
        mStompAttack = ( ( !mTouchingGround ) && ( Math.abs( dpad.getX() ) == 0.0f ) );

        setAttackHoldTime( mStompAttack ? mStompAttackHangTime : mDashAttackHangTime );

        parentObject.getImpulse().zero();
        parentObject.getVelocity().set( 0.0f, 0.0f );
        parentObject.positionLocked = true;
    }
*/
/*
    @Override
    protected void attackBegin( GameObject parentObject )
    {
        if ( mStompAttack )
        {
            parentObject.getVelocity().set( 0.0f, mStompAttackVelocity );
            parentObject.positionLocked = false;
        }
        else
        {
        	final float attackVelocity = mDashAttackVelocity * Utils.sign( parentObject.facingDirection.x );
            parentObject.getVelocity().set( attackVelocity, 0.0f );
            parentObject.positionLocked = false;
        }
    }
*/
/*
    @Override
    protected boolean attackEndCondition( float time )
    {
        if ( mStompAttack )
        {
            return mTouchingGround;
        }
        else
        {
            return ( ( time - mTimer ) > mDashAttackDuration );
        }
    }
*/
/*
    @Override
    protected void attackEnd( GameObject parentObject )
    {
        if ( mStompAttack )
        {
            final CameraSystem camera = sSystemRegistry.cameraSystem;
            if ( camera != null )
            {
                camera.shake( mAttackShakeTime, mAttackShakeMagnitude );
            }

//            final VibrationSystem vibrator = sSystemRegistry.vibrationSystem;
//            if ( vibrator != null )
//            {
//                vibrator.vibrate( ATTACK_VIBRATE_TIME );
//            }

            final GameObjectFactoryImpl factory = ( GameObjectFactoryImpl )sSystemRegistry.gameObjectFactory;
            final GameObjectManager manager = sSystemRegistry.gameObjectManager;
            if ( ( factory != null ) && ( manager != null ) )
            {
                final float x = parentObject.getPosition().x;
                final float y = parentObject.getPosition().y;
                GameObject smoke1 = factory.spawnDust( x, y - 16, true );
                GameObject smoke2 = factory.spawnDust( x + 32, y - 16, false );
                manager.add( smoke1 );
                manager.add( smoke2 );
            }
        }
        else
        {
            if ( parentObject.touchingLeftWall() || parentObject.touchingRightWall() )
            {
                final CameraSystem camera = sSystemRegistry.cameraSystem;
                if ( camera != null )
                {
                    camera.shake( mAttackShakeTime, mAttackShakeMagnitude );
                }

//                final VibrationSystem vibrator = sSystemRegistry.vibrationSystem;
//                if ( vibrator != null )
//                {
//                    vibrator.vibrate( ATTACK_VIBRATE_TIME );
//                }
            }
        }
    }
*/
/*
    @Override
    protected void finishAttack( GameObject parentObject )
    {
        parentObject.positionLocked = false;
    }
*/
/*
    @Override
    protected boolean dieCondition()
    {
        return mTouchingGround;
    }
*/
/*
    @Override
    protected boolean shouldDieAtPosition( GameObject parentObject )
    {
        return ( parentObject.getPosition().y < -parentObject.height );
    }
*/

    @Override
    protected void die( GameObject parentObject )
    {
        super.die( parentObject );

//////// SPEEDUP - BEGIN
//        mSpeedUpEndTime = -1.0f;
//        mSpeedUpFactor = 1.0f;
//        parentObject.setTimeScale( mSpeedUpFactor );
//////// SPEEDUP - END
    }

//////// WIN - BEGIN
    @Override
    protected void win( GameObject parentObject )
    {
        super.win( parentObject );

////////SPEEDUP - BEGIN
//        mSpeedUpEndTime = -1.0f;
//        mSpeedUpFactor = 1.0f;
//        parentObject.setTimeScale( mSpeedUpFactor );
//////// SPEEDUP - END

// !!!! TODO: could play a "win" animation ... !!!!
    }
//////// WIN - END

    @Override
    protected void processInventory( InventoryComponent.UpdateRecord inventory, GameObject parent, float time )
    {
//        super.processInventory( inventory, parent, time );

        final UpdateRecordImpl record = ( UpdateRecordImpl )inventory;

//////// VEHICLE 20140902 - BEGIN

        if ( record.mControllerId > 0 )
        {
/*
DebugLog.e("PEDRO", "controller id: " + controllerId );
DebugLog.e("PEDRO", "controller life: " + record.mControllerLife );
DebugLog.e("PEDRO", "controller time: " + record.mControllerTime );
*/
            final int controllerId = record.mControllerId;
            final int controllerLife = record.mControllerLife;
            final float controllerTime = record.mControllerTime;

            record.resetVehicle();
            mInventory.setChanged();

            spawnController( controllerId, parent, controllerLife, controllerTime );
        }


        if ( record.mSpeedUpCount >= 1 )
        {
            final int controllerLife = record.mControllerLife;
            final float controllerTime = record.mControllerTime;

            record.mSpeedUpCount = 0;
// !!!! TODO: could have a problem if pick up a vehicle just before !!!!
            record.resetVehicle();
            mInventory.setChanged();

            spawnController( GameObjectType.PLAYER2.ordinal(), parent, controllerLife, controllerTime );
        }


//////// SPEEDUP - MID
/*
        if ( record.mSpeedUpCount >= 1 )
        {
            record.mSpeedUpCount = 0;
            mInventory.setChanged();

            final EventRecorderImpl evtRec = ( EventRecorderImpl )sSystemRegistry.eventRecorder;
            final float duration = evtRec.getDifficultyConstants().getSpeedUpDuration();

            if ( mSpeedUpEndTime < time )
            {
//                m...Swap.activate( parent );
/*
// !!!! TODO: change that, and use a "swap component" !!!!
// !!!! TODO: change objects ... !!!!
GameObjectFactoryImpl factory = ( GameObjectFactoryImpl )sSystemRegistry.gameObjectFactory;
GameObjectManager manager = sSystemRegistry.gameObjectManager;
if ( ( factory != null ) && ( manager != null ) )
{
final float x = parentObject.getPosition().x;
final float y = parentObject.getPosition().y;
GameObject smoke1 = factory.spawnDust( x, y - 16, true );
manager.add( smoke1 );
}
*/
/*
                parent.getVelocity().zero();
                parent.getTargetVelocity().zero();
                parent.getAcceleration().zero();
                parent.getImpulse().zero();

                mSpeedUpEndTime = time + ( duration / SPEEDUP_FACTOR );
                final TimeSystem timeSystem = sSystemRegistry.timeSystem;
                timeSystem.applyScale( 1 / SPEEDUP_FACTOR, duration, false );

                mSpeedUpFactor = SPEEDUP_FACTOR;
                parent.setTimeScale( mSpeedUpFactor );
            }
            else
            {
                // already active => extend it
                mSpeedUpEndTime = time + ( duration / SPEEDUP_FACTOR );
                final TimeSystem timeSystem = sSystemRegistry.timeSystem;
                timeSystem.extendScale( duration );
            }
        }
*/
//////// SPEEDUP - END

// !!!! TODO: add behavior when all balls are "dead" !!!!
// !!!! TODO: add behavior when picked up a "time" bonus !!!!
/*
        if ( inventory.coinCount >= mNbCoinsToInvincible )
        {
            inventory.coinCount = 0;
            mInventory.setChanged();
            parent.life = mDifficultyConstants.getMaxPlayerLife();
            if ( mInvincibleEndTime < time )
            {
                mInvincibleSwap.activate( parent );
                mInvincibleEndTime = time + mDifficultyConstants.getGlowDuration();
                if ( mHitReaction != null )
                {
                    mHitReaction.setForceInvincible( true );
                }
            }
            else
            {
                // invincibility is already active, extend it
                mInvincibleEndTime = time + mDifficultyConstants.getGlowDuration();
                // HACK HACK HACK.  This really doesn't go here.
                // To extend the invincible time we need to increment the value above (easy)
                // and also tell the component managing the glow sprite to reset its
                // timer (not easy).  Next time, make a shared value system for this
                // kind of case!!
                if ( mInvincibleFader != null )
                {
                    mInvincibleFader.resetPhase();
                }
            }
        }
*/

/*
        if ( inventory.rubyCount >= MAX_GEMS_PER_LEVEL )
        {
            gotoWin( gameTime );
        }
*/
    }

    @Override
    protected void additionalProcess( GameObject parent, float time )
    {
        super.additionalProcess( parent, time );

//////// SPEEDUP - BEGIN
/*
        if ( ( mSpeedUpEndTime > 0.0f ) && ( ( mSpeedUpEndTime < time ) || ( mState == State.DEAD ) ) )
        {
//            m...Swap.activate( parentObject );

            mSpeedUpEndTime = -1.0f;
            mSpeedUpFactor = 1.0f;
            parent.setTimeScale( mSpeedUpFactor );

            parent.getVelocity().zero();
            parent.getTargetVelocity().zero();
            parent.getAcceleration().zero();
            parent.getImpulse().zero();
        }
*/
//////// SPEEDUP - END

/*
        if ( ( mInvincibleEndTime > 0.0f ) && ( ( mInvincibleEndTime < gameTime ) || ( mState == State.DEAD ) ) )
        {
            mInvincibleSwap.activate( parentObject );
            mInvincibleEndTime = 0.0f;
            if ( mHitReaction != null )
            {
                mHitReaction.setForceInvincible( false );
            }
        }
*/
    }

}
