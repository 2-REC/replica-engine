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

public abstract class DifficultyConstants {
//////// CONTINUE 20140411 - MID
    protected int PLAYER_NB_CONTINUES = 0;
//////// CONTINUE 20140411 - END
    protected int PLAYER_NB_LIVES = 1;
    protected int PLAYER_MAX_LIFE = 1;

/*
    public DifficultyConstants() {
    }
*/

//////// CONTINUE 20140411 - MID
    public int getPlayerNbContinues() {
        return PLAYER_NB_CONTINUES;
    }
//////// CONTINUE 20140411 - END

    public int getPlayerNbLives() {
        return PLAYER_NB_LIVES;
    }

    public int getPlayerMaxLife() {
        return PLAYER_MAX_LIFE;
    }
}
