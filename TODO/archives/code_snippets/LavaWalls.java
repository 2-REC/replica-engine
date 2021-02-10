
//////// FOLLOW CAMERA - BEGIN
final GameObjectFactoryImpl factory = ( GameObjectFactoryImpl )sSystemRegistry.gameObjectFactory;
final GameObjectManager manager = sSystemRegistry.gameObjectManager;
DebugLog.e("PLAYER", "LAVA: x: " + x + ", y: " + y);
GameObject lava = factory.spawnLava(0.0f);
manager.add( lava );
GameObject lavaWall = factory.spawnLavaWall(0.0f);
manager.add( lavaWall );
//////// FOLLOW CAMERA - END
