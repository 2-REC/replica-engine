
=> should be in "SidePlayerPEDRO..." (specfic to game, most derived player)

public class PlayerComponentSideSpecific extends PlayerComponentSide
{
//////// SPEEDUP - BEGIN
// !!!! TODO: shouldn't be hardcoded here !!!!
//    private static final float SPEEDUP_FACTOR = 5.0f;
//////// SPEEDUP - END

    ////////////////////////////////////////////////////////////////

//////// SPEEDUP - BEGIN
//    private float mSpeedUpEndTime;
//    private float mSpeedUpFactor;
//////// SPEEDUP - END

    ////////////////////////////////////////////////////////////////

    @Override
    public void reset()
    {
        super.reset();

//////// SPEEDUP - BEGIN
//        mSpeedUpEndTime = -1.0f;
//        mSpeedUpFactor = 1.0f;
//////// SPEEDUP - END
    }

/*
    @Override
    protected void preUpdateProcess( GameObject parent )
    {
        super.preUpdateProcess( parent );

//////// SPEEDUP - BEGIN
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
//////// SPEEDUP - END
    }
*/

    @Override
    protected void die( GameObject parentObject )
    {
        parentObject.getVelocity().zero();
        parentObject.getTargetVelocity().zero();

//////// SPEEDUP - BEGIN
/*
        mSpeedUpEndTime = -1.0f;
        mSpeedUpFactor = 1.0f;
        parentObject.setTimeScale( mSpeedUpFactor );
*/
//////// SPEEDUP - END
    }

//////// WIN - BEGIN
    @Override
    protected void win( GameObject parentObject )
    {
        parentObject.getVelocity().zero();
        parentObject.getTargetVelocity().zero();

////////SPEEDUP - BEGIN
/*
        mSpeedUpEndTime = -1.0f;
        mSpeedUpFactor = 1.0f;
        parentObject.setTimeScale( mSpeedUpFactor );
*/
//////// SPEEDUP - END

// !!!! TODO: could play a "win" animation ... !!!!
    }
//////// WIN - END

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
    }

}
