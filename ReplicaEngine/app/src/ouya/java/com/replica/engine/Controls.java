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
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import tv.ouya.console.api.OuyaController;

/**
 * Holds the controls configuration for OUYA.
 */
public class Controls {
    private static final int BUTTON_NONE = -1;

    private Context context;
    private List<ButtonData> listButtonsData;

    public final static class ButtonData {
        public String key;
        public int defaultButton;
        public int currentButton;
        public int name;

        public ButtonData(final String key, final int defaultButton, final int name) {
            this.key = key;
            this.defaultButton = defaultButton;
            this.currentButton = defaultButton;
            this.name = name;
        }
    }


    public Controls(final Context context) {
        this.context = context;
        listButtonsData = new ArrayList<ButtonData>();

        readButtonsData();
        updateButtons();
    }

    private final void readButtonsData() {
        final int buttonsFile = UtilsResources.getResourceIdByName(context, "xml", "buttons");
        XmlResourceParser parser = (context.getResources().getXml(buttonsFile));

        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("button")) {
                        String key = null;
                        int defaultButton = -1;
                        int name = -1;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("key")) {
                                key = parser.getAttributeValue(i);
                            } else if (parser.getAttributeName(i).equals("default")) {
                                defaultButton = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("name")) {
                                name = parser.getAttributeResourceValue(i, -1);
                            }
                        }

                        if (key != null && !key.equals( "" ) &&
// !!!! ???? TODO: allow no default value ? ???? !!!!
//                                defaultButton != -1) {
                                name != -1) {
                            listButtonsData.add(new ButtonData(key, defaultButton, name));
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            DebugLog.e("ControlsOuya", e.getStackTrace().toString());
        } finally {
            parser.close();
        }
    }

    public final void updateButtons() {
        final String gameName = context.getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));

        SharedPreferences prefs = context.getSharedPreferences(gameName + PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        for (ButtonData button : listButtonsData) {
            button.currentButton = prefs.getInt(button.key, button.defaultButton);
        }
    }

    public final boolean checkButtons() {
        for (Controls.ButtonData button : listButtonsData) {
            if (button.currentButton == Controls.BUTTON_NONE) {
                return false;
            }
        }
        return true;
    }

    public final void setDefaults() {
        final String gameName = context.getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));

        SharedPreferences prefs = context.getSharedPreferences(gameName + PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        for (ButtonData button : listButtonsData) {
            prefsEditor.putInt(button.key, button.defaultButton);
            button.currentButton = button.defaultButton;
        }
        prefsEditor.commit();
    }

    public final boolean removeDuplicates(final ButtonData button) {
        boolean changed = false;
        SharedPreferences prefs = null;
        for (ButtonData otherButton : listButtonsData) {
            if (otherButton != button) {
                if (otherButton.currentButton == button.currentButton) {
                    if (prefs == null) {
                        final String gameName = context.getString(UtilsResources.getResourceIdByName(context, "string", "app_name"));
                        prefs = context.getSharedPreferences(gameName + PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    }
                	otherButton.currentButton = BUTTON_NONE;
                    prefs.edit().putInt(otherButton.key, BUTTON_NONE).commit();
                    changed = true;
                }
            }
        }
        return changed;
    }

    public final ButtonData getButton(final String key) {
        for (int i = 0; i < listButtonsData.size(); ++i) {
            final ButtonData button = listButtonsData.get(i);
            if (button.key.equals(key)) {
                return button;
            }
        }
        return null;
    }

    public final List<ButtonData> getButtons() {
        return listButtonsData;
    }


    public static final String convertKeyCode(final int keyCode) {
        String result = Integer.toString(keyCode);

        switch (keyCode) {
            case OuyaController.BUTTON_MENU:
                result = "MENU";
                break;
            case OuyaController.BUTTON_DPAD_UP:
                result = "UP";
                break;
            case OuyaController.BUTTON_DPAD_RIGHT:
                result = "RIGHT";
                break;
            case OuyaController.BUTTON_DPAD_DOWN:
                result = "DOWN";
                break;
            case OuyaController.BUTTON_DPAD_LEFT:
                result = "LEFT";
                break;
            case OuyaController.BUTTON_O:
//            case KeyEvent.KEYCODE_DPAD_CENTER:
                result = "O";
                break;
            case OuyaController.BUTTON_U:
                result = "U";
                break;
            case OuyaController.BUTTON_Y:
                result = "Y";
                break;
            case OuyaController.BUTTON_A:
//            case KeyEvent.KEYCODE_BACK:
                result = "A";
                break;
            case OuyaController.BUTTON_L1:
                result = "L1";
                break;
            case OuyaController.BUTTON_L3:
                result = "L3";
                break;
            case OuyaController.BUTTON_R1:
                result = "R1";
                break;
            case OuyaController.BUTTON_R3:
                result = "R3";
                break;
// !!!! ???? TODO: want that ? ???? !!!!
// => should use "BUTTON_L2" & "BUTTON_R2" but they are deprecated
            case OuyaController.AXIS_L2:
                result = "L2";
                break;
            case OuyaController.AXIS_R2:
                result = "R2";
                break;
            case BUTTON_NONE:
                result = "-";
                break;
        }

        return result;
    }

}
