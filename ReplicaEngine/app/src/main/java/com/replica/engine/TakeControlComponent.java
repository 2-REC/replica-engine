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

import com.replica.engine.GameObject.ActionType;

public class TakeControlComponent extends GameComponent {

    private float mLifeTime;
//    private float mDelayOnRelease;
    private boolean mDieOnRelease;
    private int mSpawnOnReleaseTypeOrdinal;

    private boolean mShareLife;
//////// RECORD - MID
    private int mPlayerLife;
//////// RECORD - END

    private GameObject mControlledObject;

    private InventoryComponent mInventory;

//    private boolean mChangeActionOnButton;
//    private GameObject.ActionType mButtonPressedAction;


    public TakeControlComponent() {
        super();
        setPhase(GameComponent.ComponentPhases.THINK.ordinal());
        reset();
    }

    @Override
    public void reset() {
        mLifeTime = 0.0f;
//        mDelayOnRelease = 0.0f;
        mDieOnRelease = true;
        mSpawnOnReleaseTypeOrdinal = -1;

        mShareLife = false;
//////// RECORD - MID
        mPlayerLife = 0;
//////// RECORD - END

        mInventory = null;

        mControlledObject = null;

//        mChangeActionOnButton = false;
//        mButtonPressedAction = GameObject.ActionType.INVALID;
    }

//private float time = 1.0f;
    @Override
    public void update(float timeDelta, BaseObject parent) {
        GameObject parentObject = (GameObject)parent;

        boolean timeToRelease = false;
// !!!! TODO: handle buttons to release vehicles !!!!
// => done in PlayerComponent or here ?
//        final InputGameInterface input = sSystemRegistry.inputGameInterface;
//        final CameraSystem camera = sSystemRegistry.cameraSystem;

DebugLog.e("CONTROLLER",  "lifeTime : " + mLifeTime);

/*
// "hack" to remove life every "n" seconds ... (to test "lifeTime" stuff)
time -= timeDelta;
if (time < 0) {
    time = 1.0f;
    parentObject.life -= 1;
    timeToRelease = true;
}
DebugLog.e("CONTROLLER",  "life : " + parentObject.life);
*/

        if (parentObject.life > 0) {
            if (mLifeTime > 0.0f) {
                mLifeTime -= timeDelta;
                if (mLifeTime <= 0.0f) {
                    timeToRelease = true;
// !!!! TODO: handle blinking or fading !!!!
/*
                } else if (mLifeTime < 1.0f) {
                    // Do we have a sprite we can fade out?
                    SpriteComponent sprite = parentObject.findByClass(SpriteComponent.class);
                    if (sprite != null) {
                        sprite.setOpacity(mLifeTime);
                    }
*/
                }
            }

//?            parentObject.setCurrentAction(mTargetAction);
/*
            if (camera != null) {
                camera.setTarget(parentObject);
            }
*/
        }

// !!!! TODO: handle different ways to release ... !!!!
// button push ? ... ?
//        ...

// !!!! ???? TODO: OK ? ???? !!!!
//        if (parentObject.getCurrentAction() == ActionType.DEATH) {
        if (parentObject.life <= 0) {
            timeToRelease = true;
            mDieOnRelease = true;
        }

        if (timeToRelease) {
            releaseControl(parentObject);
        }
    }

    public final void releaseControl(GameObject parentObject) {
// !!!! ???? TODO: OK to only check for "WIN" state ? ???? !!!!
        // avoid releasing controller after having won the level (as controlled object will be destroyed)
        if (parentObject.getCurrentAction() == ActionType.WIN) {
            return;
        }

        if (mControlledObject != null) {
            final GameObject player = mControlledObject;
            mControlledObject = null;

            final CameraSystem camera = sSystemRegistry.cameraSystem;
            if (camera != null) {
                camera.setTarget(null);
            }


// !!!! TODO: change to also allow Player to stay where it was, and the camera to move !!!!
// => as in "GhostComponent"
/*
            PlayerComponent control = player.findByClass(PlayerComponent.class);
            if (camera.pointVisible(player.getPosition(), player.width)) {
                control.deactivateGhost(0.0f);
            } else {
                control.deactivateGhost(mDelayOnRelease);
            }
*/

// !!!! TODO: add member variables (+setters) to specify position & velocity of player when giving control back !!!!
            player.getPosition().x = parentObject.getPosition().x;
            player.getPosition().y = parentObject.getPosition().y;

// !!!! ???? TODO: OK ? ???? !!!!
// => doesn't cause a problem when life is 0 ?
            if (mShareLife) {
                player.life = parentObject.life;
//////// RECORD - MID
            } else {
                player.life = mPlayerLife;
//////// RECORD - END
            }


//////// RECORD - BEGIN
/*
            if (mInventory != null) {
                // remove inventory to avoid it being reseted when removing parentObject
                parentObject.remove(mInventory);
                mInventory = null;
            }
*/
//////// RECORD - MID
            if (mInventory != null) {
// !!!! TODO: all this is not very optimal ... !!!!
                // update player's inventory
            	final FixedSizeArray<BaseObject> playerObjects = player.getObjects();
                final int count = playerObjects.getCount();
                for (int i = 0; i < count; i++) {
                    final BaseObject entry = playerObjects.get(i);
                    if (entry != null) {
                        if (entry instanceof InventoryComponent) {
DebugLog.e("TAKECONTROL", "inventory");
                            final InventoryComponent playerInventory = (InventoryComponent) entry;
                            playerInventory.copy(mInventory);
                            break;
                        }
                    }
                }

                // remove component so it doesn't update when destroying object
                final FixedSizeArray<BaseObject> parentObjects = parentObject.getObjects();
                final int countParent = parentObjects.getCount();
                for (int i = 0; i < countParent; i++) {
                    final BaseObject entry = parentObjects.get(i);
                    if (entry != null) {
                        if (entry instanceof PlayerComponent) {
DebugLog.e("TAKECONTROL", "player");
                            final PlayerComponent parentPlayer = (PlayerComponent)entry;
                            parentPlayer.setInventory(null);
                            break;
                        }
                    }
                }
                parentObject.remove(mInventory);
                mInventory = null;
            }
//////// RECORD - END

            final GameObjectManager manager = sSystemRegistry.gameObjectManager;
            manager.add(player);
            manager.setPlayer(player);

            if (camera != null) {
                camera.setTarget(player);
            }

            if (mDieOnRelease) {
                parentObject.life = 0;
            } else {
DebugLog.e("TAKECONTROL", "life : " + parentObject.life);
DebugLog.e("TAKECONTROL", "time : " + mLifeTime);
                if (mSpawnOnReleaseTypeOrdinal != -1) {
                    final GameObjectFactory factory = sSystemRegistry.gameObjectFactory;
                    GameObject object = factory.spawnControllerFromOrdinal(mSpawnOnReleaseTypeOrdinal,
                            parentObject.getPosition().x - 32, parentObject.getPosition().y,
                            parentObject, parentObject.life, mLifeTime);
                    manager.add(object);
                }

                // remove object without killing it
                manager.destroy(parentObject);
            }
        }
    }

    public final void setControlledObject(GameObject controlledObject) {
        mControlledObject = controlledObject;
//////// RECORD - MID
        mPlayerLife = mControlledObject.life;
//////// RECORD - END
    }

    public final void setLifeTime(float lifeTime) {
        mLifeTime = lifeTime;
    }

    public final void setDieOnRelease(boolean dieOnRelease) {
// !!!! TODO: should add tests !!!!
// (eg: if mShareLife => don't allow false)
        mDieOnRelease = dieOnRelease;
    }

    public final void setObjectToSpawnOnRelease(int typeOrdinal) {
        mSpawnOnReleaseTypeOrdinal = typeOrdinal;
        mDieOnRelease = false;
    }

    public final void setShareLife(boolean shareLife) {
        mShareLife = shareLife;
// !!!! ???? TODO: OK ? ???? !!!!
// => no sense to share life & not die on release ...
        if (mShareLife) {
            mDieOnRelease = true;
        }
    }

    public final void destroyControlledObject() {
        if (mControlledObject != null) {
            final GameObjectManager manager = sSystemRegistry.gameObjectManager;
            manager.destroy(mControlledObject);
            mControlledObject = null;
        }
    }

    public void setControlledObjectStatus(GameObject.ActionType actionType) {
        mControlledObject.setCurrentAction(actionType);
    }

    public boolean releaseWithHit(GameObject parent, GameObject attacker, int hitType) {
DebugLog.e("TAKECONTROL", "releaseWithHit");
        HitReactionComponent playerHitReact = null;

        final GameObject player = mControlledObject;
        if (player != null) {
        	final FixedSizeArray<BaseObject> playerObjects = player.getObjects();
            final int count = playerObjects.getCount();
            for (int i = 0; i < count; i++) {
                final BaseObject entry = playerObjects.get(i);
                if (entry != null) {
                    if (entry instanceof HitReactionComponent) {
DebugLog.e("TAKECONTROL", "hitreaction");
                        playerHitReact = (HitReactionComponent) entry;
                        break;
                    }
                }
            }
        }

        releaseControl(parent);

        if (playerHitReact != null) {
DebugLog.e( "TAKECONTROL", "transfer hit");
            playerHitReact.receivedHit(player, attacker, hitType);
        }

        // get hit even though releasing, if not dying on release and not spawning an object
        return (!mDieOnRelease && (mSpawnOnReleaseTypeOrdinal == -1));
    }

    public final void setInventory(InventoryComponent inventory) {
        mInventory = inventory;
    }

}
