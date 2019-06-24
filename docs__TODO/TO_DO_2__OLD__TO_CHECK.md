!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

TODO:
CHECK WHAT IS STILL VALID!

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


CHECK WHAT TO DO
=> INCLUDE TO PROJECT?

- Engine:
  - CrusherAndouComponent.java
    => Should be renamed.

  - GhostComponent.java
    => Add file & adapt code ...
	- MOVE TO APP/GAME (NOT IN ENGINE!)
	=> SEE WHAT CAN BE DONE, SHOULD BE MANAGEABLE BY SWAPPERS/CONTROLLERS
(keep?)
    - OrbitalMagnetComponent.java
      => Check ...
        !!!! RENAME !!!!
(keep?)
    - TheSourceComponent.java
      => Check ...
        ???? move to game?

======================================


TO TRY:
=======

- constantly walking character: set gravity with non zero X component

- add time spent in a level in level results activity ("updateSpecifics"), with "game.getGameTime()

- when objects get out of level, they are set "inactive", but not destroyed (unless "GameObject.destroyOnDeactivation" is set)
	=> this may cause problem, so could add a parameter to specify to destroy when get out of level !


================================================================================

- prohibit "PAUSE" when dying (to avoid restarting game and cheating)
?- need to init/reset inputs when starting/resuming?
!- sometimes crash with dialogs if movement + dialogs at the same time!
    ("onTouchEvent" should "pause" after every input?)
- add system to save level's state (persistency, so can quit activity and come back later restoring the state of the level & objects)
    (just need to save the objects layer & load it if exists)
- clean Main, Game, MainMenu, etc
- add preceeding "0"s in counters (eg: 0001)
    (give possibility to specify how many)
- finish/correct HudCounter (doesn't work when no icon?)
- fix problem with slider (when change size, button and base, sizes/positions don't correspond anymore)
- add "extras" menu (link to website, credits)
- TakeControlComponent: add possibility to give veocity and position to Player when releasing controller
?- add debug system (with collision boxes)
- remove/comment speedup stuff


================================================================================

BUGS:
=====

TO TEST!
- can have a crash if touching screen when dialog appears
	=> "mText" is null in "mTv.getRemainingTime()"
	=> FIXED ? ( shitty hack ... )

- why animation "startOffset" not handled on "phone" ?
	=> seems like OK on OUYA ... (?)
	=> on phone, need to specify "startOffset" both on "set" level and on trasformation level
		( on ouya, seems ok if only on "set" level )

- when lose a life and restart level, dialogs are not restarted, but only the last part of it
	=> probably need to be "resetted" as not going back through "onCreate"


================================================================================

TESTS:
======

- check that the "end game" stuff is ok !
	( that activity is not restarting )
	=> if yes, need to switch back how it was and make different cases for phone & ouya ...

- check inputs in "logo" & "title" screens ( both "phone" & "ouya" )
	=> might override "onKeyUp" to do nothing ?

- test that pause reacts as expected in every situation
	( also with HOME without pause, with or without dialogs )

- test & make sure there's no bug with the "Top/Side" stuff
	=> PlayerComponents & AnimationComponents" OK ?

- test if ok with multi touch & orientation support ( phone )

- ok to use?
		xmlns:derek="http://schemas.android.com/apk/res-auto"

	instead of
		xmlns:derek="http://schemas.android.com/apk/res/com.replica.game"
		(doesn't work)


- see if should allow min sdk version to 11 for phone, & 12 for ouya (12 needed for controls/axis stuff)

- check all differences from original & make documentation

!!!!
- check correct lifecycle
- check Logs
- check if go in restoreState (if not, why?)


================================================================================

TODO:
=====

- set menu buttons sizes in dp in layout files !

- see how to get screen DPI

- try putting drawables in "drawable-nodpi" (to avoid rescaling)
(+ check how to have bitmaps at correct expected sizes)

- add DebugSystem for collision (same as in Replica Island)

- prohibit "Pause" menu when player dies (to avoid quitting game & restarting level without losing life)

- handle more than 1 dialog resource per level

- finish adaptation to top scroller :
 - see what needs to be adapted :
  - "PhysicsComponent" ( could be derived from a generic class )
  - "BackgroundCollisionComponent" ?
  - EnnemyAnimationComponent
  - EnnemyComponent ? PatrolComponent ?
  - ...
 - change GameObjectFactoryImpl.java to handle "direction" instead of "horizFlip"
	( as well as the level loading system )


- add NPC handling

- see how to make moving blocks/platforms
	( & see what to do for "top scrollers" if want to handle moving platforms )

(
- look at CPMStar ( game advertisement )
)


- need to reset ouya pad every time we come back from pause menu ?
	( and other activities ? )
	=> to avoid movement problems encountered when unpausing


- sounds/music
	=> should use "Audio Focus" ? ( look at http://android-developers.blogspot.be/2013/08/respecting-audio-focus.html )

- add "update" mechanism ( for new versions )

- add ouya payment stuff ...

- clean code ...

- add optionnal "x" in "icon counters" ( between numbers & icon )

- should call garbage collector after playing an animation ?
	( or before ? )


- no need to handle hud configuration changes in "useOrientation" ( in InputGameInterface )
	A game is either in landscape or portrait - no changes
	=> remove stuff related to that in "InputGameInterface"

- read the Hud configuration from an XML file ( look at comments in "InputGameInterfaceImpl" )
- add controls configuration activity where can specify positions and sizes of controls,
	and save to an XML file

- "DiaryActivity.java" & diary related stuff :
	=> want to keep diary stuff or can delete everything related to it ?

- Options/Settings management :
	- Options activity
	- entries in menus
	- load/save
	- etc.
  + controls setup ( mostly ouya ! )
	=> try to find ouya snes emulator's sources & see how its done

- see what channels are for, and how they are used

- Orientation Sensor :
	- check all stuff related to orientation sensor
	- in MainPhone.java : TYPE_ORIENTATION deprecated ... => what to use ?
	- check how to activate/deactivate easily sensor stuff


- "EventRecorder" : ok to set all synchronized methods as "public" ?

- HudSystem.java :
	=> need the "init" abstract method ?
- HudSystemImpl.java :
	=> need to override the "init" method ?

- keep or remove play time ?

- check all use of AllocationGuard
	=> see if want to keep it - could use it for debug, then remove/comment them ...
	=> check that all the classes derived from AllocationGuard are ok (calling super in constructor, ...)

- add a "letter" system ( like the "digits" one )

- rename some classes ...

- LevelSelectActivity.java :
	=> should make the class abstract and derive it to make it game specific
		( so that can have something like "Super Mario World", or like "Replica Island", etc. )
	=> should handled the "replay/selectable" stuff depending on a setting (instead of in code)
		( so that can have games easily specifying if want to have completed levels selectable or not )

- make DifficultyMenuActivity generic/customizable
	=> specify how many difficulty levels we want (instead of 3 hardcoded)
	=> use an XML file, where for each difficulty, there's an entry with info such as : name, drawable, description, related class, etc.

- change input system to handle several sliders

- move stuff from "InputGameInterfaceImpl" to generic class for "InputGameInterface" ( like in "HudSystem" )
	( makes it lighter, and easier to add new types ... )
	=> handle list of controls, and in functions apply treatment to every element ( "reset", "update", etc. )

- make a generic class for "input" stuff ( both "InputButtons" & "InputSliders" ) and use it in "InputGameInterface"

- make system to save level's state ( when eg playing an ingame animation )
	On OUYA, when starting another activity, the current one is destroyed.
	=> when returning to Activity, the level is restarted ...
	( => need to save every objects state )
	( !!!! also make sure the game is paused when playing an animation - especially on phone )

- add a "continue" system
	=> which adds a "number of attemps", and handle it in level/game stats


- in GameObjectFactoryImpl.java :
	- setComponentClasses :
            // TODO: I wish there was a way to do this automatically, but the ClassLoader doesn't seem
            // to provide access to the currently loaded class list.  There's some discussion of walking
            // the actual class file objects and using forName() to instantiate them, but that sounds
            // really heavy-weight.  For now I'll rely on (sucky) manual enumeration.


- manifests:
	- check if activity stuff can be removed (when different sources with engines, & strings.xml values => to remove)
		- string required (engine calls game):
			<string name="class_name_mainmenu">derek.android.gamephone.MainMenuActivitySpecific</string>
				(engine/StartupActivity)
			<string name="class_name_main">derek.android.gamephone.MainImpl</string>
				(engine/MainMenuActivity)
				(engine/DifficultyMenuActivity)
			<string name="class_name_settings">derek.android.gamephone.SetPreferencesActivityImpl</string>
				(engine/MainMenuActivity)
			<string name="class_name_levelresults">derek.android.gamephone.ResultsLevelActivityImpl</string>
				(engine/Main)
			<string name="class_name_gameover">derek.android.gamephone.ResultsGameOverActivityImpl</string>
				(engine/Main)

		- direct class call (engine -> engine):
			<string name="class_name_animationplayer">derek.android.enginephone.AnimationPlayerActivityPhone</string>
				(engine/EndingActivity)
				(engine/Main)
				(engine/StartupActivity)
			<string name="class_name_difficultymenu">derek.android.enginephone.DifficultyMenuActivityPhone</string>
				(engine/MainMenuActivity)
			<string name="class_name_levelselect">derek.android.enginephone.LevelSelectActivityPhone</string>
				(engine/Main)

		- unused => removed:
			<string name="class_name_pausemenu">derek.android.enginephone.PauseMenuActivityPhone</string>
			<string name="class_name_options">derek.android.gamephone.OptionsActivityImpl</string>

	- check if merged manifest are ok
		- intent-filter "split"
			=> if not ok, have intent-filter in each manifest? (except to level)

- check gradle files
	- need minifyEnabled=true ?



ENGINE:

- move as much code as possible from Game to Engine
	& from specific to generic

- see all the resources that can be removed if desired (titles, backgrounds, xml, etc)
	=> might require code changes
	(+rename some ... eg in main menu)

- use "Multiple APK Support" ?
		http://developer.android.com/google/play/publishing/multiple-apks.html
	=> if yes:
		- same "applicationId"
		- give each variant a different "versionCode"
	=> if no:
		- assign different "applicationId" to each variant


GAME:

- PreferencesFragmentControls.java
	=> different for OUYA & PHONE
	? => could be same? (if don't need "onPreferenceTreeClick")
		(in this case, move source file from "GamePhone" to "Game")




!!!! TODO !!!!


=====================================================================================================
GameActivity.java:
=====================================================================================================

general :
---------
- accelerometer : implement a separate class for accelerometer handling (remove from activity) (like in MyGame)
    (check that it's not worse in perf)

- should have a "wait message" at beginning of each level, when game is loading


????
onPause :
---------
- should handle pausing in here ? (pause the thread and toggle pause menu ?)
  (maybe easier to handle it here than when pushing HOME button or else ?)
????


onCreateDialog :
----------------
?- replace the QUIT Dialog with a customized "QuitMenuView" ?
  (so can have a completely customized menu, with the game "theme")



=====================================================================================================

=====================================================================================================
GameThread.java:
=====================================================================================================

!!!! ????
SHOULD ADD A "STATE_LOADING" when init, and maybe a "STATE_READY" ?
=> could init the game with STATE_LOADING (in startGame and restoreState),
   and set it to STATE_RUNNING in doStart (called in surfaceCreated)
!!!! could have a problem if leave the app before init is completed ... !!!!

=> initialise the STATE to READY or LOADING !

- see what is best to use for mLastTime ...


GameThread(GameView gameView) :
-------------------------------
  ?- remove game fields inits from here ? (no real impact though)
    => done later
  ?- initialise width and height to 1 (or ok if done at variables declaration)
    => or better to initialise to "desired" size, and set all variables and bitmaps sizes according to it ?


!!!!
GameThread(GameView gameView, GameThread oldThread) :
-----------------------------------------------------
!  - test if works better when assigning oldThread.mHandler to new mHandler
	mHandler = oldThread.mHandler;

!  - check that oldThread.mMode is always equal to STATE_PAUSE
    => could force it to STATE_PAUSE

  ?- remove game fields inits from here ?
    => done later


cleanup :
---------
  ?- check if anything else to free/release


initGame :
-----------
  - initialise the fields depending on default values (or restored/loaded values)
    (? remove game fields inits from here ?)
    => it's done later


!!!!
run :
-----
  - test with "if (mMode == STATE_RUNNING)" just at beginning of while

  - add an else case with a "while(mMode == STATE_PAUSE) mPauseLock.wait()"

  - check if OK if "processInput" and "doDraw" are inside the running test

  - add a Log to check if in here even when on PAUSE !!!!
    (or even when out of Activity !)


setSurfaceSize(int width, int height) :
---------------------------------------
  - rescale all bitmaps (!!!! attention to division by 0 or invalid image sizes !!!!)

  - move updates of fields related to canvas to a separate function (ie: "applyNewSize")


make new applyNewSize function (update fields related to the canvas size)
----


!!!!
- add a pause lock like in Andoukun
  - in run, use a while(pause) mPauseLock.wait()
    (add else case for RUNNING test, and have a "while(mMode == STATE_PAUSE) mPauseLock.wait()")
  - update it in pause, stop, resume, ...
  - ...
  (+ profile difference when using a pause lock or not)



=====================================================================================================

=====================================================================================================
GameView.java:
=====================================================================================================

Member variables :
------------------
  ?- need to set GameThread as volatile ?
	private volatile GameThread mGameThread;

  (- could have variables as public instead of private and avoid getters and setters ...)
    => check what is better / faster


Constructors :
--------------
  ?- need to have the 3 constructors ?
    - GameView(Context context)
    - GameView(Context context, AttributeSet attrs)
    - GameView(Context context, AttributeSet attrs, int defStyle)


cleanup :
  ?- could remove mGameThread.setRunning(false) & mGameThread.cleanup() and call them in Activity.onDestroy ?

  - check if anything more to free/release


surfaceCreated :
----------------
ok  !?- add call to "mGameThread.doStart" at end of function (check if ok ...)

  (- can move the first call to Thread.setRunning after the test, just before Thread.start)
    => any impact if not calling it here ?

=> if yes, could move the startGame function before the doStart call, and fuse the 2 functions


surfaceDestroyed :
------------------
  ?- add a call to "mGameThread.interrupt()" when catch InterruptedException ?


onWindowFocusChanged :
----------------------
  - trace this function to see when called



=====================================================================================================


TO TEST:
--------

- GameThread :
  - run
    - test if (mMode == STATE_RUNNING) can be set at beginning of function
    - check if OK if processInput and doDraw are inside the running test !
    - add a Log to check if in here even when on PAUSE !!!!
      (or even when out of Activity !)
      (+ profile difference when using a pause lock or not)

- when in PAUSE menu,
  - if press MENU button => back to game ?
  - if press BACK button => opens QUIT dialog ?

- when in QUIT menu,
  - if press MENU button => ignored ?
  - if press BACK button => ignored ? (or back to PAUSE menu ?)
  - if press HOME button => what happens ? what to do ?
    => shouldn't do anything, as game is already paused ?


TRACES:
-------
- continue with the init traces
  - Activity: constructor, startgame, ...
  - View: surfaceCreated, SurfaceChanged, surfaceDestroyed, ...
  - Thread: constructors, startGame, setState, ...

- check when are called :
  - Activity.onSaveInstanceState
  - Thread.saveState (called in Activity.onSaveInstanceState)
  - Thread.restoreState (called in Activity.startGame)

- check when the Activity constructor is called
  - when press HOME and then get back in game (via "recent" shortcut), it's not called
  - only called when Activity is killed ? (removed from mem)
(+ check that onSaveInstanceState is also only called when Activity is destroyed)
=> 


PROFILING:
----------
- test overhead of PAUSE menu View
  - when displayed, hidden, and when removed from app
  => to see impact of using a View for a menu

- check difference of perf when handling inputs in View instead of Activity
  => faster in View or neglectable ?

