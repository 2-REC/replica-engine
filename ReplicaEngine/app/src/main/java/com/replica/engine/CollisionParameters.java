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

/** 
 * CollisionParamaters defines global parameters related to dynamic (object vs object) collisions.
 */
public final class CollisionParameters {
    // HitType describes the type of hit that a victim object receives.  Victims may choose to 
    // react differently to the intersection depending on the hit type.
	// TODO: Make this a bit field so that objects can support multiple hit types.
    public final class HitType {
        public final static int INVALID = -1;   // No type.
        public final static int HIT = 0;        // Standard hit type.  Life is reduced by 1.
        public final static int DEATH = 1;      // Causes instant death.
        public final static int COLLECT = 2;    // Causes collectable objects to be collected by the attacker.
        public final static int POSSESS = 3;    // Causes possessable objects to become possessed.
        public final static int DEPRESS = 4;    // A hit indicating that the attacker is pressing into the victim.
        public final static int LAUNCH = 5;     // A hit indicating that the attacker will launch the victim.
        public final static int DIALOG = 6;     // indicates the attacker has touched a dialog object
//////// PLATFORM - BEGIN
//        public final static int NBTYPES = 7;    // number of different hit types
//////// PLATFORM - MID
        public final static int PLATFORM = 7;   // indicates the attacker has landed on victim's platform
        public final static int NBTYPES = 8;    // number of different hit types
//////// PLATFORM - END
    }
    
   
}
