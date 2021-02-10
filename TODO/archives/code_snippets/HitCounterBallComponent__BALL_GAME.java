package derek.android.gamephone;

import derek.android.engine.HitCounterComponent;

import derek.android.engine.GameObject;
import derek.android.engine.TimeSystem;


// !!!! TODO: could add a "spawning" handling for bonuses !!!!
// => every "mBonusStep", spawn a bonus at other's position ...

// !!!! TODO: this could be used to handle an more complex speed management !!!!
// (eg: speed down when hit several times, etc.)

public class HitCounterBallComponent extends HitCounterComponent
{
    private float mSpeedFactor;


    public HitCounterBallComponent()
    {
        super();
    }

    @Override
    public void reset()
    {
//        super.reset();
        mSpeedFactor = 1.0f;
    }


    @Override
    protected void dealHitProcess( GameObject parent, GameObject victim, TimeSystem time )
    {
        parent.getVelocity().set( parent.getVelocity().x * mSpeedFactor, parent.getVelocity().y * mSpeedFactor );
        parent.getTargetVelocity().set( parent.getTargetVelocity().x * mSpeedFactor, parent.getTargetVelocity().y * mSpeedFactor );
    }

    @Override
    protected void takeHitProcess( GameObject parent, GameObject attacker, TimeSystem time )
    {
    }

    public void setSpeedFactor( float speedFactor )
    {
        mSpeedFactor = speedFactor;
    }

}
