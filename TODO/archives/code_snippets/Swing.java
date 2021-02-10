
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
