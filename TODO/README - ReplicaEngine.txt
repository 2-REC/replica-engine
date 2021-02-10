(!!!! TODO: CHANGE TO WORD/DOC FILE !!!!)

OVERVIEW
(List of chapters)

INTRODUCTION
(=> Application overview and utility)

In order to learn how its components based game engine was working, and to make it more generic, I decided to modify the Replica Island's source code. This would hopefully make it easier to create new games using the engine by having less code to write or modify, as well as provide new functionalities.

Many changes have been made to the original source code, but only to make it more generic and to add new functionalities. Its core has not been modified nor optimised.
It is an unfinished work, so some parts should be rewritten and optimised, and some bugs should be fixed. It thus serves more as a showcase of things that can be done, instead of how they should be done.
Additionally, some functionalities have been removed or are not working anymore. (!! Provide List !!)


NEW/ENHANCED FUNCTIONALITIES
...
- top/side stuff (climber, etc.)
- controllers (swappables):
  - vehicles
  - climber
  - swimmer
  - bonus
  - ...
- followers
  - camera
  - player
  - between
- continues (when game over)
- moving platforms
- level selection / map
- force (?)
- level results
- game results
(- launch chain)
- swingers (+fixed): pendulum, ...
- animations (different types and configs) (+win and lose)
- sounds/images/musics: config files
- dialogs (if working!?)
- level files structure (layers, moving speeds, format, ...)
- preferences (with fragments)
- HUD new structure
- Inputs (ouya...)
- 


========
TODO!


INFOS:
======

- Debug:
  - Debug menu entries in PAUSE menu:
    If game version (defined in "value/strings" as "app_version") is < 0, debug mode is activated.
    From PAUSE menu (in game):
      - level selection
      - method tracing
     (- collision boxes display) - !!!! TODO !!!!
         (uses "engine/DebugSystem.java")

    To make this possible, the buttons must be defined in "layout/main" (as "pauseSelectLevelButton", "pauseMethodTracingButton" and "pauseQuitButton"),
    & the corresponding drawables must be present (can be an xml specifying different states for the button).

  - Logs:
    Defined in engine "DebugLog.java".
    Logs are enabled if "setDebugLogging" has been called with "true".
    In "engine/main.java" it is the case when:
      - VERSION < 0 (defined in "value/strings" as "app_version")
      or
      - if PREFERENCE_ENABLE_DEBUG is set to "true" in the Preferences - !!!! TODO !!!! (how? where?)

  - Assertions:
    If "DEBUG" is set to "true" (boolean) in "engine/DebugChecks.java", assertions are executed.
    An assertion is defined using "assertCondition(<expression>)" from "engine/DebugChecks.java".

  - OpenGL:
    Can enable OpenGL debug flags - !!!! TODO !!!!
    ("setDebugFlags")


- GameObjectFactory :
 - MAX_GAME_OBJECTS is the maximum number of objects that can have in a level.
	=> change it if want levels with lots of objects.


- collectables:
	To be able to be collected, an object must have the following:
		FixedSizeArray<CollisionVolume> basicVulnerabilityVolume = new FixedSizeArray<CollisionVolume>( 1 );
		basicVulnerabilityVolume.add( new SphereCollisionVolume( 8, 8, 8 ) );
		basicVulnerabilityVolume.get( 0 ).setHitType( HitType.COLLECT );
		...

		SpriteAnimation idle = new SpriteAnimation( 0, 5 );
		idle.addFrame( new AnimationFrame( texture..., time..., ..., basicVulnerabilityVolume ) );

		DynamicCollisionComponent dynamicCollision = ( DynamicCollisionComponent )allocateComponent( DynamicCollisionComponent.class );
		sprite.setCollisionComponent( dynamicCollision );
		HitReactionComponentSingle hitReact = ( HitReactionComponentSingle )allocateComponent( HitReactionComponentSingle.class );
		hitReact.setDieWhenCollected( true );
		hitReact.setInvincible( true );
		HitPlayerComponent hitPlayer = ( HitPlayerComponent )allocateComponent( HitPlayerComponent.class );
		hitPlayer.setup( 32, hitReact, HitType.COLLECT, false );
		dynamicCollision.setHitReactionComponent( hitReact );


- Continues:
	Allows to have continues ( when lose all lives, can still use the "CONTINUE" button )
	( look for "//////// CONTINUE 20140411 tags )


- Level Selection & Replay :
	Allows levels already completed to be replayed, even after the game has been completed.
	( look for "//////// REPLAY 20140326" tags )
		- Main.java in engine
		- LevelTree.java in engine
		- LevelSelectActivity.java in engine

		!!!! dependent on "CONTINUE" handling, with stuff in:
			- MainMenuActivity.java in engine
			- PreferenceConstants.java in engine
!!!! CHECK IF OK WITH THE "SKIPPABLE" STUFF !!!!
Look for "//////// SKIPPABLE 20140410" & "//////// REPLAY 20140326"

- Skippable levels :
	Allows to go back to level selection or to previously selectalble level ( or first )
		when die in a "non skippable" level
		( if level is "skippable", goes to next level )
	=> in "level_tree.xml" :
		- restartable:
			- true  => automatically restart level when die (default)
			- false => depending on other fields go to another level
		- selectable:
			- true  => selectable in LevelSelectActivity
			- false => not selectable in LevelSelectActivity (default)
		- skippable:
			- true  => when die, go to next level (default)
			- false => when die, go to previous selectable level
	( look for "//////// SKIPPABLE 20140410" tags )
		- LevelTree.java in engine
		- Main.java in engine

- Level Results :
	At the end of each level, can have a panel displaying level results.
	Enable:
	( look for "//////// lev res" tags )
		- LevelResultsActivity.java in engine
		- LevelResultsActivityImpl.java in game
		- level_results.xml in layout ( + related drawables & strings ) ( in game resources )
		- define implemented class in Manifest ( in game ) :
	        	<activity android:name="LevelResultsActivityImpl"
        		    android:screenOrientation="landscape"
        		    android:configChanges="keyboardHidden|orientation"
		        />
		- in GameFlowEvent.java :
			- define EVENT_SHOW_LEVEL_RESULTS
		- in Main.java ( engine ) :
			- define ACTIVITY_LEVEL_RESULTS
			- in onActivityResult :
				- replace calls to "onGameFlowEvent" with param "EVENT_GO_TO_NEXT_LEVEL" by param "EVENT_SHOW_LEVEL_RESULTS"
				- add else case with "ACTIVITY_LEVEL_RESULTS" to call "onGameFlowEvent" with param "EVENT_GO_TO_NEXT_LEVEL"
			- in onGameFlowEvent :
				- add switch case with "ACTIVITY_LEVEL_RESULTS" to call the activity ( with result )
			- add abstract method "addLevelResultsData" called in "onGameFlowEvent"
		- in MainImpl.java ( game ) :
			- define method "addLevelResultsData" with desired data to display at end of level
=> TODO: should rename the classes:
	- LevelResultsActivity to ResultsLevelActivity
	- GameOverActivity to ResultsGameActivity


- Game over animations :
	- Win Animation :
		At the end of the game, can have an animation before the end of game results.
		Enable:
		( look for "//////// gameover_win" tags )
			- in GameFlowEvent.java :
				- define EVENT_SHOW_END_GOOD_ANIMATION
			- in Main.java ( engine ) :
				- in onActivityResult :
					- add switch case with "EVENT_SHOW_END_GOOD_ANIMATION" to call "onGameFlowEvent( GameFlowEvent.EVENT_SHOW_RESULTS_GAME, 0 )"
				- in onGameFlowEvent :
					- in switch case "EVENT_GAME_WON", replace "onGameFlowEvent( GameFlowEvent.EVENT_SHOW_RESULTS_GAME, 0 )" with "onGameFlowEvent( GameFlowEvent.EVENT_SHOW_END_GOOD_ANIMATION, index )"
					- add switch case with "EVENT_SHOW_END_GOOD_ANIMATION" with other animation cases
					- in switch case "EVENT_GO_TO_NEXT_LEVEL", replace "onGameFlowEvent( GameFlowEvent.EVENT_GAME_WON, 0 )" with "onGameFlowEvent( GameFlowEvent.EVENT_GAME_WON, <END_ID> ) )"
						where <END_ID> corresponds to a xml resource file describing the end animation
						!!!! TODO : should handle possibility to specify the file differently ... so can have different endings )
			- set required resources files/data

	- Lose Animation :
		At the end of the game, when lost all lives, can have an animation before the end of game results.
		Enable:
		( look for "//////// gameover_lost" tags )
			- in GameFlowEvent.java :
				- define EVENT_SHOW_END_BAD_ANIMATION
			- in Main.java ( engine ) :
				- in onActivityResult :
					- add switch case with "EVENT_SHOW_END_BAD_ANIMATION" to call "onGameFlowEvent( GameFlowEvent.EVENT_SHOW_RESULTS_GAME, -1 )"
				- in onGameFlowEvent :
					- in switch case "EVENT_GAME_LOST", replace "onGameFlowEvent( GameFlowEvent.EVENT_SHOW_RESULTS_GAME, -1 )" with "onGameFlowEvent( GameFlowEvent.EVENT_SHOW_END_BAD_ANIMATION, index )"
					- add switch case with "EVENT_SHOW_END_BAD_ANIMATION" with other animation cases
			- in MainImpl.java ( game ) :
				- in onGameFlowEvent, case "EVENT_PLAYER_DIE" :
					- replace "onGameFlowEvent( GameFlowEvent.EVENT_GAME_LOST, 0 )" with "onGameFlowEvent( GameFlowEvent.EVENT_GAME_LOST, <END_ID> )"
						where <END_ID> corresponds to a xml resource file describing the end animation
						!!!! TODO : should handle possibility to specify the file differently ... so can have different endings )
			- set required resources files/data


- Dialogs :
	During a level, collision with an object can trigger a conversation.

	General ( to enable/disable ) :
	- Main.java :
		=> handling of the dialogs in game ( as a View )
		( can be removed if don't want to use dialogs in game : look for "//////// DIALOGS - ..." tags )

	- layout/main.xml :
		=> definition of the Views used for dialogs
		( can be removed if don't want to use dialogs in game : look for "<!-- DIALOGS - ... -->" tags )

	- TypewriterTextView.java :
		=> definition of View displaying text progressively written
		( file can be deleted if don't want to use dialogs in game )

	- MainPhone.java :
		=> handling of the dialogs inputs ( touch )
		( can be removed if don't want to use dialogs in game : look for "//////// DIALOGS - ..." tags )

	- MainOuya.java :
		=> handling of the dialogs inputs ( buttons )
		( can be removed if don't want to use dialogs in game : look for "//////// DIALOGS - ..." tags )


	Specific ( for each dialog ) :
	- GameObjectFactoryImpl.java :
		( look for "//////// dialogs" tags )
		- import derek.android.engine.SelectDialogComponent;
		- new ComponentClass( SelectDialogComponent.class, 8 ),
			=> needed to be able to allocate dialog components
		- add the object id in enum "GameObjectType"
		- add corresponding "case" in "spawn" with function to call to create object
		- add object creation function with :
			- HitType.COLLECT for the "basicVulnerabilityVolume
			- hitReact.setSpawnGameEventOnHit( HitType.COLLECT,
                                                           GameFlowEventSpecific.EVENT_SHOW_DIALOG_CHARACTER,
                                                           nb );
				where "nb" = index of dialog in level data ( corresponding to "id" )
				( if using SelectDialogComponent, the dialog is specified by the index of the HotSpot at that position )
				( !!!! this needs to add a "SelectDialogComponent" to the Object !!!! )
			- and all required stuff ( drawables, collisions, animations, sounds, etc. )

	- raw/<layer_objects>.bin :
		=> add the object triggering dialog
	- raw/<layer_hotspots>.bin :
		=> add the index of dialog to trigger ( must be greater or equal to "NPC_SELECT_DIALOG" & "NPC_SELECT_DIALOG - value" gives the index in the dialog xml file "id" )

	- xml/<level>.xml :
		=> add "dialogs" tag with reference "xml/<dialog>.xml"

	- xml/<dialog>.xml :
		=> define all dialogs.
		For each conversation :
!!!! TODO: to be checked !!!!
=> seesm like 0 is ok ... (?)
		- id : starting from 1 ( ! starting from 0 in code ! )
		- pages.
			For each page :
			- image : drawable to be displayed
			- text : text to be displayed, defined as "string" in "values/dialogs_<character>.xml"
			- title : title of dialog ( generally character's name ), defined as "string" in "values/strings.xml"
!?!?
CHECK:
dialogs_x.xml : list of dialog entries (could have 1 per character) (referred in "xml/dialog_x" files)




- Layers :
( TODO: explain how it works - size factors, moving speeds, etc. )


- Animations :
( TODO: explain how it works - types, data, files, etc. )
both "moving images" & "images sequence"

animation files (in "anim"):
- animation-list: images sequence (can be set as a drawable for an "ImageView")
- set: transformations set (can be set as animation for a "Layout")


+ video playback!
(XML file in "res/xml" folder, with: type=3, anim="@raw/video")
=> format auto detected (?)



- Hud :
( TODO: explain how it works - relative vs inches sizes, HudElements & derived, parameters, etc. )


- Inputs :
 - both "Touch" & "Pad" controls
	=> instead of having them derived from a class, could have 2 independent classes with same name, and switch them when using OUYA or touch devices ...
	( !?!? Try the touch interface with OUYA, if can use it with the touchpad ... !?!? )

	- HudSystemImpl:
		=> define hud elements ( buttons, counters, bars, etc. )
			- constants ( position, size, etc.)
			- hud elements ( HudButton, HudBar, etc. )
			- in constructor:
				- initialise the elements with corresponding constants
				- add elements to hud with "addElement( element )"
				- setDigitsSize if desired
			- in "setDrawables":
				- set needed drawable for each element
			- create get/set methods for desired elements

	- InputGameInterfaceImpl:
		=> define controls
			- control objects ( sliders, buttons, etc. )
			- in constructor:
				- initialise the objects and associate them with hud elements
			- in "reset":
				- reset the objects
			- in "update":
				- update objects
			- "getMovementPad":
				=> keep that ? generic enough ?
			- "getAttackButton", etc. :
				=> make that more generic: "getActionButton( ATTACK )"
			- in "useOrientation":
				- call "useOrientation" of sliders if want to use orientation
			- in "setOrientationSensitivity":
				- call "setOrientationSensitivity" of sliders if want to use orientation
			- in "setMovementSensitivity":
				- call "setSensitivity" of sliders if want to use orientation


- Controllers:
( TODO: continue & explain more ... )
 - possible to spawn an object that takes the control
	=> allows completeley different object to be controlled ( different PlayerComponent, animation, etc. )
	- can take control for a certain time
	- can take control as long as alive
	- can share life with player
 - when an object takes control, it "freezes" the player ( remove from manager ), and restores it when dies
 - when a controller releases control, if not dead ( !KillOnRelease ), another object is spawned, a pickable that will cause an identical controller to be spawned when collected
 - uses UpdateRecord to keep and transmit info (such as controllerId, controllerLife & controllerTime )

 - inventory is not shared, but the record ( UpdateRecord ) is copied from one to the other when control changes
 - if the controller is not the player when finishes the level, the player is destroyed by the controller ( to avoid objects not being released )
 - ...

...
	start:
		- HotSpot
		- Pickup
	stop:
		- timeout (from TakeControlComponent)
		- releaseSwapper (from PlayerComponent... - derived impl class)
			- leave specific "swap HotSpot"
		- button? (TODO)


================

THREAD INIT WORKFLOWS


1A. Activity.onCreate :
    savedInstanceState == null

 => - new GameThread(GameView gameView)
      - mMode = STATE_INIT
      - load resources (? should be done here ? - depends on difficulty, level, etc.)
    - initGame
      - set variables
      (- mMode = STATE_READY) (? should have that ?)



1B. Activity.onCreate
    savedInstanceState != null
    mGameThread != null

 => - restoreState
      - get mMode from savedState (should be != STATE_RUNNING => in that case should set it to == STATE_PAUSE)
      - set variables



1C. Activity.onCreate
    savedInstanceState != null
    mGameThread == null

 => - new GameThread(GameView gameView)
      - mMode = STATE_INIT
      - load resources (? should be done here ? - depends on difficulty, level, etc.)
    - restoreState
      - get mMode from savedState (should be != STATE_RUNNING => in that case should set it to == STATE_PAUSE)
      - set variables



----------------

2A. View.surfaceCreated
    mGameThread.getState() == Thread.State.NEW

 => - startGame
      - mRun = true
      - start()
    - doStart
      - if (mMode == STATE_INIT) mMode = STATE_RUNNING



2B. View.surfaceCreated
    mGameThread.getState() == Thread.State.TERMINATED

 => - new GameThread(GameView gameView, GameThread oldThread)
      - mMode = oldThread.mMode
      - set variables
    - startGame
      - mRun = true
      - start()
    - doStart
      - if (mMode == STATE_INIT) mMode = STATE_RUNNING



----------------

3. View.surfaceChanged
 => - setSurfaceSize
      - update variables (related to canvas size)
      (-rescale bitmaps) (!!!!)





