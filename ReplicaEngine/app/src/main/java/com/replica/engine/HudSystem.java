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

import java.util.ArrayList;
import android.content.Context;

/*
TODO:
- init the drawables here !!!!
- should have the values coming from a file (XML) !!!!
 - should handle "side switching" ( to switch from left to right handed controls  - only for input elements )

*/

/**
 * A very simple manager for orthographic in-game UI elements.
 * TODO: This should probably manage a number of hud objects in keeping with the component-centric
 * architecture of this engine.  The current code is monolithic and should be refactored.
 */
public class HudSystem extends BaseObject {
//    public static final boolean SIZE_RELATIVE = true;
//    public static final boolean SIZE_ABSOLUTE = false;

//// DIGITS - BEGIN
/*
    private DrawableBitmap[] mDigitDrawables;
    private float mDigitsFactorWidth;
    private float mDigitsFactorHeight;
//////// inch - m
    private boolean mDigitsRelativeSize;
//////// inch - e
*/
//// DIGITS - MID

//////// FPS - BEGIN
// !!!! TODO : should add it only for debug purpose !!!!
// => create it as a separate object, only used when asked ...
/*
    private int     mFPS;
    private Vector2 mFPSLocation;
    private int[]   mFPSDigits;
    private boolean mFPSDigitsChanged;
    private boolean mShowFPS;
*/
//////// FPS - END

    private Texture mFadeTexture;
    private float mFadeStartTime;
    private float mFadeDuration;
    private boolean mFadeIn;
    private boolean mFading;
    private int mFadePendingEventType;
	private int mFadePendingEventIndex;

// !!!! ???? TODO : OK to use ArrayList ? ???? !!!!
// => not too slow ? ( as accessed every frame ... )
    private ArrayList<HudElement> mElements = new ArrayList<HudElement>();



    public HudSystem() {
        super();

//// DIGITS - BEGIN
/*
        mDigitDrawables = new DrawableBitmap[10];
        mDigitsFactorWidth = 0.1f;
        mDigitsFactorHeight = 0.1f;
*/
//// DIGITS - MID

//////// FPS - BEGIN
/*
        mFPSLocation = new Vector2();
        mFPSDigits = new int[MAX_DIGITS];
*/
//////// FPS - END

//        reset();
    }


    @Override
    public void reset() {
//// DIGITS - BEGIN
/*
        for (int x = 0; x < mDigitDrawables.length; x++) {
            mDigitDrawables[x] = null;
        }
*/
//// DIGITS - MID

//////// FPS - BEGIN
/*
        mFPS = 0;
        mFPSDigits[0] = 0;
        mFPSDigits[1] = -1;
        mFPSDigitsChanged = true;
        mShowFPS = false;
*/
//////// FPS - END

        mFadeTexture = null;
        mFading = false;
        mFadePendingEventType = GameFlowEvent.EVENT_INVALID;
        mFadePendingEventIndex = 0;

        for (int i = 0; i < mElements.size(); ++i) {
/*
            final HudElement hudElt = mElements.get(i);
            if (hudElt.isResetable()) {
                hudElt.reset();
            }
*/
            mElements.get(i).reset();
        }
    }


    public void addElement(HudElement element) {
        mElements.add(element);
    }

    public void removeElement(HudElement element) {
// !!!! ???? TODO : OK ? ???? !!!!
// => or need to copy all but this element in an other list ?
        mElements.remove(element);
    }

//////// FPS - BEGIN
/*
    public void setFPS(int fps) {
        mFPSDigitsChanged = (fps != mFPS);
        mFPS = fps;
    }

    public void setShowFPS(boolean show) {
        mShowFPS = show;
    }
*/
//////// FPS - END

// !!!! ???? TODO : should get drawables as parameters ? ???? !!!!
    public void setDrawables(Context context) {
        TextureLibrary longTermTextureLibrary = BaseObject.sSystemRegistry.longTermTextureLibrary;
// !!!! ???? TODO : should check that pointer is not null ? ???? !!!!

// !!!! TODO : could move all this out of the engine ... !!!!
// => look at "HudSystemImpl.java"
        setFadeTexture(longTermTextureLibrary.allocateTexture(UtilsResources.getResourceIdByName(context, "drawable", "fade_color")));

//// DIGITS - BEGIN
/*
        Texture[] digitTextures = {
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_0" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_1" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_2" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_3" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_4" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_5" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_6" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_7" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_8" ) ),
            longTermTextureLibrary.allocateTexture( UtilsResources.getResourceIdByName( packageName, "drawable", "ui_9" ) )
        };
        DrawableBitmap[] digits = {
            new DrawableBitmap( digitTextures[ 0 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 1 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 2 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 3 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 4 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 5 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 6 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 7 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 8 ], 0, 0 ),
            new DrawableBitmap( digitTextures[ 9 ], 0, 0 )
        };
        setDigitDrawables( digits );
*/
//// DIGITS - MID
    }

    public void setFadeTexture(Texture texture) {
        mFadeTexture = texture;
    }

    public void startFade(boolean in, float duration) {
        mFadeStartTime = sSystemRegistry.timeSystem.getRealTime();
        mFadeDuration = duration;
        mFadeIn = in;
        mFading = true;
    }

    public void clearFade() {
        mFading = false;
    }

    public boolean isFading() {
        return mFading;
    }

//// DIGITS - BEGIN
/*
    public void setDigitDrawables(DrawableBitmap[] digits) {
        for (int x = 0; x < mDigitDrawables.length && x < digits.length; x++) {
            mDigitDrawables[x] = digits[x];
// !!!! ???? TODO : do that here or in caller ? ???? !!!!
            mDigitDrawables[x].setCropUse(false);
        }
    }
*/
//// DIGITS - MID

//// DIGITS - BEGIN
/*
    protected void setDigitsSize(boolean relativeSize, float widthFactor, float heightFactor) {
        assert (widthFactor != 0.0f || heightFactor != 0.0f);

        mDigitsRelativeSize = relativeSize;
        mDigitsFactorWidth = widthFactor;
        mDigitsFactorHeight = heightFactor;
    }
*/
//// DIGITS - MID

    @Override
    public void update(float timeDelta, BaseObject parent) {
        final ContextParameters params = sSystemRegistry.contextParameters;
        final RenderSystem render = sSystemRegistry.renderSystem;
        final DrawableFactory factory = sSystemRegistry.drawableFactory;

        final GameObjectManager manager = sSystemRegistry.gameObjectManager;

// !!!! TODO : should separate "playing" and "non playing" hud elements !!!!
// => & only draw player-specific HUD elements when there's a player.
        if (manager != null && manager.getPlayer() != null) {
            for (int i = 0; i < mElements.size(); ++i) {
                mElements.get(i).update(timeDelta, parent);
            }
        }


//////// FPS - BEGIN
/*
        if (mShowFPS) {
            if (mFPSDigitsChanged) {
                int count = intToDigitArray(mFPS, mFPSDigits);
                mFPSDigitsChanged = false;
                mFPSLocation.set(params.gameWidth - 10.0f - ((count + 1) * (mDigitDrawables[0].getWidth() / 2.0f)), 10.0f);
            }
            drawNumber(mFPSLocation, mFPSDigits, false);
        }
*/
//////// FPS - END

        if (mFading && factory != null) {
            final float time = sSystemRegistry.timeSystem.getRealTime();
            final float fadeDelta = (time - mFadeStartTime);

            float percentComplete = 1.0f;
            if (fadeDelta < mFadeDuration) {
                percentComplete = (fadeDelta / mFadeDuration);
            } else if (mFadeIn) {
                // We've faded in.  Turn fading off.
                mFading = false;
            }

            if (percentComplete < 1.0f || !mFadeIn) {
                float opacityValue = percentComplete;
                if (mFadeIn) {
                    opacityValue = 1.0f - percentComplete;
                }

                DrawableBitmap bitmap = factory.allocateDrawableBitmap();
                if (bitmap != null) {
                    bitmap.setWidth(params.gameWidth);
                    bitmap.setHeight(params.gameHeight);
                    bitmap.setTexture(mFadeTexture);
                    bitmap.setCrop(0, mFadeTexture.height, mFadeTexture.width, mFadeTexture.height);
                    bitmap.setOpacity(opacityValue);
                    render.scheduleForDraw(bitmap, Vector2.ZERO, SortConstants.FADE, false);
                }
            }

            if (percentComplete >= 1.0f &&
                mFadePendingEventType != GameFlowEvent.EVENT_INVALID) {
// !!!! ???? TODO : why does it have to go via "LevelSystem" ? ???? !!!!
                LevelSystem level = sSystemRegistry.levelSystem;
                if (level != null) {
                    level.sendGameEvent(mFadePendingEventType, mFadePendingEventIndex, false);
                    mFadePendingEventType = GameFlowEvent.EVENT_INVALID;
                    mFadePendingEventIndex = 0;
                }
            }
        }
    }

//// DIGITS - BEGIN
/*
    private void initDigits() {
        final ContextParameters params = sSystemRegistry.contextParameters;

        final Texture tex = mDigitDrawables[0].getTexture();
        final float ratio = (float)tex.width / tex.height;

        float width; 
        float height;

        if (mDigitsRelativeSize) {
            width = params.viewWidth;
            height = params.viewHeight;
        } else {
            final DisplayMetrics dm = params.context.getResources().getDisplayMetrics();
            width = dm.xdpi;
            height = dm.ydpi;
        }

        if (mDigitsFactorWidth != 0.0f) {
            width *= mDigitsFactorWidth;
            if (mDigitsFactorHeight != 0.0f) {
                height *= mDigitsFactorHeight;
            } else {
                height = width / ratio;
            }
        } else {
            height *= mDigitsFactorHeight;
            width = height * ratio;
        }

        width /= params.viewScaleX;
        height /= params.viewScaleY;

        final int w = ( int )width;
        final int h = ( int )height;


        for (int x = 0; x < mDigitDrawables.length; x++) {
            mDigitDrawables[x].resize(w, h);
        }
    }

    public void drawNumber(Vector2 location, int[] digits, boolean drawX) {
        final RenderSystem render = sSystemRegistry.renderSystem;

        if (mDigitDrawables[0].getWidth() == 0) {
            // first time init
            initDigits();
        }

        final float characterWidth = mDigitDrawables[0].getWidth() / 2.0f;
        float offset = 0.0f;

        for (int x = 0; x < digits.length && digits[x] != -1; x++) {
            int index = digits[x];
            DrawableBitmap digit = mDigitDrawables[index];
            if (digit != null) {
                render.scheduleForDraw(digit, location, SortConstants.HUD, false);
                location.x += characterWidth;
                offset += characterWidth;
            }
        }
        location.x -= offset;
    }

    public int getDigitWidth() {
        assert mDigitDrawables[0] != null;

// !!!! ???? TODO : OK to do it here ? ???? !!!!
        if (mDigitDrawables[0].getWidth() == 0) {
            // first time init
            initDigits();
        }

        return (int)(mDigitDrawables[0].getWidth() * sSystemRegistry.contextParameters.viewScaleX);
    }

    public int getDigitHeight() {
        assert mDigitDrawables[0] != null;

// !!!! ???? TODO : OK to do it here ? ???? !!!!
        if (mDigitDrawables[0].getWidth() == 0) {
            // first time init
            initDigits();
        }

        return (int)(mDigitDrawables[0].getHeight() * sSystemRegistry.contextParameters.viewScaleY);
    }

// !!!! TODO : could be moved to "utils" !!!!
    public int intToDigitArray(int value, int[] digits) {
        int characterCount = 1;
// !!!! TODO : make something more generic !!!!
// ( to handle numbers of arbitrary digits number )
        if (value >= 1000) {
            characterCount = 4;
        } else if (value >= 100) {
            characterCount = 3;
        } else if (value >= 10) {
            characterCount = 2;
        }

        int remainingValue = value;
        int count = 0;
        do {
            int index = (remainingValue != 0) ? (remainingValue % 10) : 0;
            remainingValue /= 10;
            digits[characterCount - 1 - count] = index;
            count++;
        }
        while (remainingValue > 0 && count < digits.length);

        if (count < digits.length) {
            digits[count] = -1;
        }
        return characterCount;
    }
*/
//// DIGITS - MID

    public void sendGameEventOnFadeComplete(int eventType, int eventIndex) {
        mFadePendingEventType = eventType;
        mFadePendingEventIndex = eventIndex;
    }
}
