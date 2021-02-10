package derek.android.gameouya;

import derek.android.engine.CameraSystem;
import derek.android.engine.EventRecorder;
import derek.android.engine.GameObject;
import derek.android.engine.GameObjectManager;
import derek.android.engine.HudSystem;
import derek.android.engine.InputButton;
import derek.android.engine.InputXY;
import derek.android.engine.InventoryComponent;
import derek.android.engine.PlayerComponentSide;
import derek.android.engine.Utils;
import derek.android.engine.Vector2;


/*
*/

public class PlayerComponentSideSpecific extends PlayerComponentSide
{
//////// PEDRO 20140821 - BEGIN

    private float mAttackShakeTime;
    private float mAttackShakeMagnitude;
//    private float mAttackVibrateTime;

    private float mStompAttackVelocity;
    private float mStompAttackHangTime; // time waiting between start of attack, and stomping (going down)

    private float mDashAttackVelocity;
    private float mDashAttackDuration;
    private float mDashAttackHangTime; // time waiting between start of attack, and dashing (going side)

    private boolean mStompAttack;

//////// PEDRO 20140821 - END


    @Override
    public void reset()
    {
        super.reset();

//////// PEDRO 20140821 - BEGIN
        mAttackShakeTime = 0.15f;
        setAttackDelayTime( mAttackShakeTime );
        mAttackShakeMagnitude = 50.0f;
//        mAttackVibrateTime = 0.05f;

        mStompAttack = false;
//        mStompAttackVelocity = -500.0f;
        mStompAttackVelocity = -50.0f;
        mStompAttackHangTime = 0.0f;

        mDashAttackVelocity = 500.0f;
        mDashAttackDuration = 0.25f;
        mDashAttackHangTime = 0.0f;
//////// PEDRO 20140821 - END
    }

/*
    @Override
    protected void preUpdateProcess( GameObject parent )
    {
        super.preUpdateProcess( parent );
    }

    @Override
    protected void postUpdateProcess( GameObject parent )
    {
    }
*/

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

//// SWING - BEGIN
/*
final GameObjectFactoryImpl factory = ( GameObjectFactoryImpl )sSystemRegistry.gameObjectFactory;
final GameObjectManager manager = sSystemRegistry.gameObjectManager;
if ( ( factory != null ) && ( manager != null ) )
{
    final float x = parentObject.getPosition().x;
    final float y = parentObject.getPosition().y;

//pendulum with swinging chain elements (nice movement, but never stabilises)
//    GameObject previousSwing = parentObject;
//    GameObject swing = factory.spawnPendulum( x, y, 4.0f, true, 2.0f );
//    manager.add( swing );
//    previousSwing = swing;
//    for ( int i=0; i<10; ++i )
//    {
//        swing = factory.spawnSwingCoin( previousSwing, 4.0f );
//        manager.add( swing );
//        previousSwing = swing;
//    }

//pendulum with "in between" chain elements
    GameObject swing = factory.spawnPendulum( x, y, 100.0f, true, 100.0f );
//    GameObject swing = factory.spawnSwingCoin( parentObject, 100.0f );
    manager.add( swing );
    final int nb = 5;
    for ( int i=0; i<nb; ++i )
    {
    	final float ratio = i / ( float )nb;
        GameObject between = factory.spawnSwingBetweenFixed( x, y, swing, ratio );
//        GameObject between = factory.spawnSwingBetween( parentObject, swing, ratio );
        manager.add( between );
    }
}
*/
//// SWING - END
    }

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
/*
            final VibrationSystem vibrator = sSystemRegistry.vibrationSystem;
            if ( vibrator != null )
            {
                vibrator.vibrate( ATTACK_VIBRATE_TIME );
            }
*/

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
//////// FOLLOW CAMERA - BEGIN
DebugLog.e("PLAYER", "LAVA: x: " + x + ", y: " + y);
GameObject lava = factory.spawnLava(0.0f);
manager.add( lava );
GameObject lavaWall = factory.spawnLavaWall(0.0f);
manager.add( lavaWall );
//////// FOLLOW CAMERA - END
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
/*
                final VibrationSystem vibrator = sSystemRegistry.vibrationSystem;
                if ( vibrator != null )
                {
                    vibrator.vibrate( ATTACK_VIBRATE_TIME );
                }
*/
            }
        }
    }

    @Override
    protected void finishAttack( GameObject parentObject )
    {
        parentObject.positionLocked = false;
    }

/*
    @Override
    protected boolean dieCondition()
    {
        return mTouchingGround;
    }
*/

    @Override
    protected boolean shouldDieAtPosition( GameObject parentObject )
    {
        return ( parentObject.getPosition().y < -parentObject.height );
    }

    @Override
    protected void die( GameObject parentObject )
    {
        parentObject.getVelocity().zero();
        parentObject.getTargetVelocity().zero();
    }

//////// WIN - BEGIN
    @Override
    protected void win( GameObject parentObject )
    {
        parentObject.getVelocity().zero();
        parentObject.getTargetVelocity().zero();

// !!!! TODO: could play a "win" animation ... !!!!
    }
//////// WIN - END

    @Override
    protected void updateHudLife( float percentage )
    {
        final HudSystemImpl hud = ( HudSystemImpl )sSystemRegistry.hudSystem;
        if ( hud != null )
        {
            hud.setLifePercent( percentage );
        }
    }

    @Override
    protected void updateLifeCounter( EventRecorder recorder, HudSystem hud )
    {
        final EventRecorderImpl rec = ( EventRecorderImpl )recorder;
        rec.decrementEventCounter( EventRecorderImpl.COUNTER_LIVES, 1 );
        ( ( HudSystemImpl) hud ).setLifeCounter( rec.getNbLives() );
    }

    @Override
    protected void processInventory( InventoryComponent.UpdateRecord inventory, GameObject parent, float time )
    {
//        final UpdateRecordImpl record = ( UpdateRecordImpl )inventory;

//////// SPEEDUP - MID
/*
        if ( record.mSpeedUpCount >= 1 )
        {
            record.mSpeedUpCount = 0;
            mInventory.setChanged();

            final float duration = EventRecorderImpl.getDifficultyConstants().getSpeedUpDuration();

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


/*
    @Override
    protected float getMaxLife()
    {
// !!!! TODO: shouldn't be necessary to test if null !!!!
// => EventRecorder must be initialised before GameObjectFactory in Game::bootstrap
        return BaseObject.sSystemRegistry.eventRecorder.getDifficultyConstants().getPlayerMaxLife();
    }
*/

    public void setAttackShakeTime( float shakeTime )
    {
    	mAttackShakeTime = shakeTime;
    }

    public void setAttackShakeMagnitude( float shakeMagnitude )
    {
    	mAttackShakeMagnitude = shakeMagnitude;
    }

    public void setStompAttackVelocity( float attackVelocity )
    {
    	mStompAttackVelocity = attackVelocity;
    }

    public void setStompAttackHangTime( float hangTime )
    {
    	mStompAttackHangTime = hangTime;
    }

    public void setDashAttackVelocity( float attackVelocity )
    {
    	mDashAttackVelocity = attackVelocity;
    }

    public void setDashAttackHangTime( float hangTime )
    {
    	mDashAttackHangTime = hangTime;
    }

    public void setDashAttackDuration( float durationTime )
    {
    	mDashAttackDuration = durationTime;
    }


////////PEDRO 20140821 - BEGIN

    public boolean isStompAttack()
    {
        return mStompAttack;
    }

////////PEDRO 20140821 - END

}
