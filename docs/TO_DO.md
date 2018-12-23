(!!!! TODO: MERGE WITH OTHER TODO FILE !!!!)

- change project/directory name in Android Studio and adapt

- publish on GitHub

- handle overlay layers!!!
  => in level files, change the boolean field "foreground" to an enum field "type" (?), allowing the values "background", "foreground" & "overlay",

- finish fixing GameObjectFactoryImpl

- adapt HUD

- clean (remove all commented code and new stuff - BUT keep it somewhere!) and debug code

Make a basic game:
- recreate 2-3 levels (from original game)
    => To showcase most functionalities (& that the engine is not broken)
    - Andou (playability + swapables with rockets, ...)
        BUT:
        - no diary
            => Should be done later (but shouldnt be a separate activity - as is in original?)
        - no Ghost
        - no invincibility
            => Problem with rockets simultaneously as also using swapables ...
            => SEE IF CAN BE COMBINED ... ?
        - no difficulty adaptation
            => Should be done later
        - no ruby
            => Should be done later: look for "ruby" in code (pickup 3 to end level)
                => Required changes in "ProcessInventory" of "PlayerComponent..."? (+ hud and inventory)
    - doors
    - ...
- fix Dialogs and animations for each level
- adapt menus, settings, etc. (As in original)

LEVELS:
!
- Intro (with credits and title)
    ? => Should change/adapt credits.
    ? => Uses overlay layer?
    - wait message
    - NPC
    - dialogs

!
- Map / level selection
    => See if can easily do the same as original (else make new different one).

!
- Memory 001 - Part 1
  ! => Need to adapt end of level => don't use gems, but a single "end level" item.
  - Andou basic controls (move, fly, stomp)
  - HUD
  - ennemies (brobot)
  - doors + switches
  - dalogs
  - pickables (coins)

?
- Memory 001 - Part 2
    => Check what can do with invincibility ...
    - Invincibility

?
- Memory 002 - Part 1
    => Check if can use "take control" & "energy ball" (charge from Andou)
    - take control
    - orientation sensors
    - ennemy cannon
    => Or do "Memory 012" instead?

!
- Memory 002 - Part 2
    - timed doors + switches
    - different doors + switches colours

!
- Memory 012
    => IF CAN DO TAKE CONTROL
    - take control
    - breakable blocks
    (- race with NPC)


Create new levels to showcase new functionalities:
=> ADAPT EXISTING LEVELS. 
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
- different spawners depending on event types (?)
- continues (when game over)
- moving platforms
- level selection / map
- force (?)
- "state graph" (+animations and result screens)
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

Adapted levels:
- Memory 021:
    - controller/swappable + 2d top view: add "climber" stuff between big holes ("2d crawling grids")
    - force (follower?): add vertical lava wall (horizontal force)
    - moving platforms: add platforms in some big holes
    ?- follower: add a small follower... (fairy?)
    - launch chain: add boss at end, throwing chains... (?)
    - controller (vehicle): add vehicle at end to fight boss...
    ! - CHECK IF ALSO CRASHES ON TABLET!
- 


- create new resources
