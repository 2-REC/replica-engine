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

import android.content.Context;
import android.view.MotionEvent;

/**
 * High-level setup object for the AndouKun game engine.
 * This class sets up the core game engine objects and threads.  It also passes events to the
 * game thread from the main UI thread.
 */
public abstract class Game extends AllocationGuard {
    private GameThread mGameThread;
    private Thread mGame;
    private ObjectManager mGameRoot;

    private GameRenderer mRenderer;
    private GLSurfaceView mSurfaceView;
    protected boolean mRunning;
    protected boolean mBootstrapComplete;
    private LevelTree.Level mPendingLevel;
    private LevelTree.Level mCurrentLevel;
    private boolean mGLDataLoaded;
    private ContextParameters mContextParameters;
//////// RESUME - MID
    private boolean mPaused;
//////// RESUME - END

    public Game() {
        super();
        mRunning = false;
        mBootstrapComplete = false;
        mGLDataLoaded = false;
        mContextParameters = new ContextParameters();
//////// RESUME - MID
        mPaused = false;
//////// RESUME - END
    }

    /** 
     * Creates core game objects and constructs the game engine object graph.  Note that the
     * game does not actually begin running after this function is called (see start() below).
     * Also note that textures are not loaded from the resource pack by this function, as OpenGl
     * isn't yet available.
     * @param context
     */
    public void bootstrap(Context context, int viewWidth, int viewHeight, int gameWidth, int gameHeight, int difficulty, int version) {
        if (!mBootstrapComplete) {
            mRenderer = new GameRenderer(context, this, gameWidth, gameHeight);

            // Create core systems
            BaseObject.sSystemRegistry.openGLSystem = new OpenGLSystem(null);

            BaseObject.sSystemRegistry.customToastSystem = new CustomToastSystem(context);

            ContextParameters params = mContextParameters;
            params.viewWidth = viewWidth;
            params.viewHeight = viewHeight;
            params.gameWidth = gameWidth;
            params.gameHeight = gameHeight;
            params.viewScaleX = (float)viewWidth / gameWidth;
            params.viewScaleY = (float)viewHeight / gameHeight;
            params.context = context;
            params.difficulty = difficulty;
            BaseObject.sSystemRegistry.contextParameters = params;

            EventRecorder eventRecorder = getEventRecorder();
            BaseObject.sSystemRegistry.eventRecorder = eventRecorder;
            BaseObject.sSystemRegistry.registerForReset(eventRecorder);

            // Short-term textures are cleared between levels.
            TextureLibrary shortTermTextureLibrary = new TextureLibrary();
            BaseObject.sSystemRegistry.shortTermTextureLibrary = shortTermTextureLibrary;

            // Long-term textures persist between levels.
            TextureLibrary longTermTextureLibrary = new TextureLibrary();
            BaseObject.sSystemRegistry.longTermTextureLibrary = longTermTextureLibrary;

            // The buffer library manages hardware VBOs.
            BaseObject.sSystemRegistry.bufferLibrary = new BufferLibrary();



            BaseObject.sSystemRegistry.soundSystem = new SoundSystem();

            // The root of the game graph.
            MainLoop gameRoot = new MainLoop();

            // must be before InputGameInterface
            InputSystem input = getInputSystem(context);
            BaseObject.sSystemRegistry.inputSystem = input;
            BaseObject.sSystemRegistry.registerForReset(input);

            // need to create it before creating the InputGameInterface, as will be used by it
            HudSystem hud = getHudSystem(context);
            hud.setDrawables(context);

            BaseObject.sSystemRegistry.hudSystem = hud;
            if (version < 0) {
//////// FPS - BEGIN
//                hud.setShowFPS(true);
//////// FPS - END
            }
            gameRoot.add(hud);

            InputGameInterface inputInterface = getInputGameInterface();
            gameRoot.add(inputInterface);
            BaseObject.sSystemRegistry.inputGameInterface = inputInterface;

            LevelSystem level = new LevelSystem();
            BaseObject.sSystemRegistry.levelSystem = level;

            CollisionSystem collision = new CollisionSystem();
            BaseObject.sSystemRegistry.collisionSystem = collision;
            BaseObject.sSystemRegistry.hitPointPool = new HitPointPool();
            collision.loadCollisionTiles(context.getResources().openRawResource(UtilsResources.getResourceIdByName(context, "raw", "collision" )));

            GameObjectManager gameManager = new GameObjectManager(params.viewWidth * 2);
            BaseObject.sSystemRegistry.gameObjectManager = gameManager;

            GameObjectFactory objectFactory = getGameObjectFactory();
            BaseObject.sSystemRegistry.gameObjectFactory = objectFactory;

            BaseObject.sSystemRegistry.hotSpotSystem = new HotSpotSystem();

            BaseObject.sSystemRegistry.levelBuilder = new LevelBuilder();

            BaseObject.sSystemRegistry.channelSystem = new ChannelSystem();
            BaseObject.sSystemRegistry.registerForReset(BaseObject.sSystemRegistry.channelSystem);

            CameraSystem camera = new CameraSystem();


            BaseObject.sSystemRegistry.cameraSystem = camera;
            BaseObject.sSystemRegistry.registerForReset(camera);

            gameRoot.add(gameManager);

            // Camera must come after the game manager so that the camera target moves before the camera 
            // centers.

            gameRoot.add(camera);


            // More basic systems.

            GameObjectCollisionSystem dynamicCollision = new GameObjectCollisionSystem();
            gameRoot.add(dynamicCollision);
            BaseObject.sSystemRegistry.gameObjectCollisionSystem = dynamicCollision;


            RenderSystem renderer = new RenderSystem();
            BaseObject.sSystemRegistry.renderSystem = renderer;
            BaseObject.sSystemRegistry.vectorPool = new VectorPool();
            BaseObject.sSystemRegistry.drawableFactory = new DrawableFactory();

            gameRoot.add(collision);

            // debug systems
            //BaseObject.sSystemRegistry.debugSystem = new DebugSystem(context, longTermTextureLibrary);
            //dynamicCollision.setDebugPrefs(false, true);


            objectFactory.preloadEffects();

            mGameRoot = gameRoot;

            mGameThread = new GameThread(mRenderer);
            mGameThread.setGameRoot(mGameRoot);


            mCurrentLevel = null;

// !!!! TODO: may need this !!!!
//            initSpecific();

            mBootstrapComplete = true;
        }
    }


    protected synchronized void stopLevel() {
        stop();
        GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
        manager.destroyAll();
        manager.commitUpdates();

        //TODO: it's not strictly necessary to clear the static data here, but if I don't do it
        // then two things happen: first, the static data will refer to junk Texture objects, and
        // second, memory that may not be needed for the next level will hang around.  One solution
        // would be to break up the texture library into static and non-static things, and
        // then selectively clear static game components based on their usefulness next level,
        // but this is way simpler.
        GameObjectFactory factory = BaseObject.sSystemRegistry.gameObjectFactory;
        factory.clearStaticData();
        factory.sanityCheckPools();

        // Reset the level
        BaseObject.sSystemRegistry.levelSystem.reset();

        // Ensure sounds have stopped.
        BaseObject.sSystemRegistry.soundSystem.stopAll();
//////// NEW_SOUND - MID
        MusicManager.stop();
//////// NEW_SOUND - END

        // Reset systems that need it.
        BaseObject.sSystemRegistry.reset();

        // Dump the short-term texture objects only.
        mSurfaceView.flushTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
        BaseObject.sSystemRegistry.shortTermTextureLibrary.removeAll();
        mSurfaceView.flushBuffers(BaseObject.sSystemRegistry.bufferLibrary);
        BaseObject.sSystemRegistry.bufferLibrary.removeAll();
    }

    public synchronized void requestNewLevel() {
        // tell the Renderer to call us back when the
        // render thread is ready to manage some texture memory.
        mRenderer.requestCallback();
    }

    public synchronized void restartLevel() {
        final LevelTree.Level level = mCurrentLevel;
        stop();

        // Destroy all game objects and respawn them.  No need to destroy other systems.
        GameObjectManager manager = BaseObject.sSystemRegistry.gameObjectManager;
        manager.destroyAll();
        manager.commitUpdates();

        // Ensure sounds have stopped.
        BaseObject.sSystemRegistry.soundSystem.stopAll();
//////// NEW_SOUND - MID
        MusicManager.stop();
//////// NEW_SOUND - END

        // Reset systems that need it.
        BaseObject.sSystemRegistry.reset();

        LevelSystem levelSystem = BaseObject.sSystemRegistry.levelSystem;
        levelSystem.incrementAttemptsCount();
        levelSystem.spawnObjects();

        BaseObject.sSystemRegistry.hudSystem.startFade(true, 0.2f);

        mCurrentLevel = level;
        mPendingLevel = null;
        start();
    }

    public synchronized void resumeLevel() {
        HudSystem hud = BaseObject.sSystemRegistry.hudSystem;
        if (hud != null) {
            hud.startFade(true, 1.0f);
        }
    }

    protected synchronized void goToLevel(LevelTree.Level level) {

        ContextParameters params = BaseObject.sSystemRegistry.contextParameters;
        BaseObject.sSystemRegistry.levelSystem.loadLevel(level, mGameRoot);

        Context context = params.context;
        mRenderer.setContext(context);
        mSurfaceView.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
        mSurfaceView.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
        mSurfaceView.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);

        mGLDataLoaded = true;

        mCurrentLevel = level;
        mPendingLevel = null;

//////// NEW_SOUND - MID
        MusicManager.setResource(MusicManager.MUSIC_GAME, mCurrentLevel.musicResource);
//////// NEW_SOUND - END

        TimeSystem time = BaseObject.sSystemRegistry.timeSystem;
        time.reset();

        HudSystem hud = BaseObject.sSystemRegistry.hudSystem;
        if (hud != null) {
            hud.startFade(true, 1.0f);
        }

        start();
    }

    /** Starts the game running. */
    public void start() {
//////// NEW_SOUND - MID
        MusicManager.start(BaseObject.sSystemRegistry.contextParameters.context, MusicManager.MUSIC_GAME);
//////// NEW_SOUND - END
        if (!mRunning) {
            assert mGame == null;
            // Now's a good time to run the GC.
            Runtime r = Runtime.getRuntime();
            r.gc();

            mGame = new Thread(mGameThread);
            mGame.setName("Game");
            mGame.start();
            mRunning = true;
//            AllocationGuard.sGuardActive = false;
        } else {
            mGameThread.resumeGame();
        }
    }

    public void stop() {
        if (mRunning) {

            if (mGameThread.getPaused()) {
                mGameThread.resumeGame();
            }
            mGameThread.stopGame();
            try {
                mGame.join();
            } catch (InterruptedException e) {
                mGame.interrupt();
            }
            mGame = null;
            mRunning = false;
            mCurrentLevel = null;
//            AllocationGuard.sGuardActive = false;
        }
    }

// !!!! ???? TODO: OK ? ???? !!!!
    public boolean onOrientationEvent(float x, float y, float z) {
        return true;
    }

// !!!! ???? TODO: OK ? ???? !!!!
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        boolean result = false;
        if (mRunning) {
            BaseObject.sSystemRegistry.inputSystem.motion(event);
        }
        return result;
    }



    public boolean onKeyDownEvent(int keyCode) {
        boolean result = false;
        if (mRunning) {
            BaseObject.sSystemRegistry.inputSystem.keyDown(keyCode);
        }
        return result;
    }

    public boolean onKeyUpEvent(int keyCode) {
        boolean result = false;
        if (mRunning) {
            BaseObject.sSystemRegistry.inputSystem.keyUp(keyCode);
        }
        return result;
    }

    public GameRenderer getRenderer() {
        return mRenderer;
    }  

    public void onPause() {
//////// RESUME - MID
        mPaused = true;
//////// RESUME - END
////////NEW_SOUND - MID
        MusicManager.pause();
////////NEW_SOUND - END
        if (mRunning) {
//////// NEW_SOUND - MID
//            MusicManager.pause();
//////// NEW_SOUND - END
            mGameThread.pauseGame();
        }
    }

    public void onResume(Context context, boolean force) {
//////// RESUME - MID
        mPaused = false;
//////// RESUME - END
        if (force && mRunning) {
//////// NEW_SOUND - MID
            MusicManager.start(context, MusicManager.MUSIC_PREVIOUS);
//////// NEW_SOUND - END
            mGameThread.resumeGame();
        } else {
            mRenderer.setContext(context);
            // Don't explicitly resume the game here.  We'll do that in
            // the SurfaceReady() callback, which will prevent the game
            // starting before the render thread is ready to go.
            BaseObject.sSystemRegistry.contextParameters.context = context;
        }
    }

    public void onSurfaceReady() {
        if (mPendingLevel != null && mPendingLevel != mCurrentLevel) {
            if (mRunning) {
                stopLevel();
            }
            goToLevel(mPendingLevel);
//////// RESUME - BEGIN
/*
        } else if (mGameThread.getPaused() && mRunning) {
            mGameThread.resumeGame();
        }
*/
//////// RESUME - MID
        } else if (!mPaused && mGameThread.getPaused() && mRunning) {
//////// NEW_SOUND - MID
            MusicManager.start(BaseObject.sSystemRegistry.contextParameters.context, MusicManager.MUSIC_PREVIOUS);
//////// NEW_SOUND - END
            mGameThread.resumeGame();
        }
//////// RESUME - END
    }

    public void setSurfaceView(GLSurfaceView view) {
        mSurfaceView = view;
    }

    public void onSurfaceLost() {
        BaseObject.sSystemRegistry.shortTermTextureLibrary.invalidateAll();
        BaseObject.sSystemRegistry.longTermTextureLibrary.invalidateAll();
        BaseObject.sSystemRegistry.bufferLibrary.invalidateHardwareBuffers();

        mGLDataLoaded = false;
    }

    public void onSurfaceCreated() {
        // TODO: this is dumb.  SurfaceView doesn't need to control everything here.
        // GL should just be passed to this function and then set up directly.

        if (!mGLDataLoaded && mGameThread.getPaused() && mRunning && mPendingLevel == null) {

            mSurfaceView.loadTextures(BaseObject.sSystemRegistry.longTermTextureLibrary);
            mSurfaceView.loadTextures(BaseObject.sSystemRegistry.shortTermTextureLibrary);
            mSurfaceView.loadBuffers(BaseObject.sSystemRegistry.bufferLibrary);
            mGLDataLoaded = true;
        }
    }

// hack to force reloading of level when replaying the same one
// ( after selection in "LevelSelectActivity" )
    public void setPendingLevel(LevelTree.Level level, boolean reset) {
        mPendingLevel = level;
        if (reset) {
            mCurrentLevel = null;
        }
    }

    public void setSoundEnabled(boolean soundEnabled) {
        BaseObject.sSystemRegistry.soundSystem.setSoundEnabled(soundEnabled);
    }

//////// NEW_SOUND - MID
    public void setSoundVolume(final int soundVolume) {
        BaseObject.sSystemRegistry.soundSystem.setSoundVolume(soundVolume / 100.0f);
    }
//////// NEW_SOUND - END

    public void setControlOptions(
            boolean tiltControls, int tiltSensitivity, int movementSensitivity, boolean onScreenControls) {
        BaseObject.sSystemRegistry.inputGameInterface.useOrientation(tiltControls);
        BaseObject.sSystemRegistry.inputGameInterface.setOrientationSensitivity((tiltSensitivity / 100.0f));
        BaseObject.sSystemRegistry.inputGameInterface.setMovementSensitivity((movementSensitivity / 100.0f));
        BaseObject.sSystemRegistry.inputGameInterface.useOnScreenControls(onScreenControls);
    }

// !!!! ???? TODO : remove safe mode handling ? ???? !!!!
// => should be useless ...
/*
    public void setSafeMode(boolean safe) {
        mSurfaceView.setSafeMode(safe);
    }
*/

    public float getGameTime() {
        return BaseObject.sSystemRegistry.timeSystem.getGameTime();
    }

    public Vector2 getLastDeathPosition() {
        return BaseObject.sSystemRegistry.eventRecorder.getLastDeathPosition();
    }

    public void setLastEnding(int ending) {
        BaseObject.sSystemRegistry.eventRecorder.setLastEnding(ending);
    }

    public int getLastEnding() {
        return BaseObject.sSystemRegistry.eventRecorder.getLastEnding();
    }

    public boolean isPaused() {
        return (mRunning && mGameThread != null && mGameThread.getPaused());
    }

// !!!! TODO : change for gamepad !!!!
/*
    public void setKeyConfig(int leftKey, int rightKey, int jumpKey, int attackKey) {
        BaseObject.sSystemRegistry.inputGameInterface.setKeys(leftKey, rightKey, jumpKey, attackKey);
    }
*/

//////// CONTINUE 20140411 - MID
    public int getNbContinues() {
        return 0; // no continues by default
    }
//////// CONTINUE 20140411 - END

    public int getNbLives() {
        return -1; // inifinite lives by default
    }

    ////////////////////////////////////////////////////////////////////////////

    protected abstract InputSystem getInputSystem(Context context);

    protected abstract InputGameInterface getInputGameInterface();

    protected abstract GameObjectFactory getGameObjectFactory();

    protected abstract HudSystem getHudSystem(Context context);

    protected abstract EventRecorder getEventRecorder();

}
