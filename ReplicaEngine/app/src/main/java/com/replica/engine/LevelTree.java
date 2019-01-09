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

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.XmlResourceParser;

public final class LevelTree {
	public static class LevelGroup {
		public ArrayList<Level> levels = new ArrayList<Level>();
	}
	
    public static class Level {
    	public int resource;
        public int musicResource;
        public DialogEntry dialogResources;
        public String name;
        public String number;
        public int introAnim;
        public int outroAnim;
        public boolean completed;
        public boolean diaryCollected;
//        public boolean selectable;
        public int selectable;
        public boolean restartable;
//////// SKIPPABLE 20140410 - MID
        public boolean skippable;
//////// SKIPPABLE 20140410 - END
        public boolean showWaitMessage;
        public int levelEndResource;

        public Level(int level, DialogEntry dialogs, String title, String nb,
        		int intro, int outro, int music, int select, boolean restartOnDeath,
//////// SKIPPABLE 20140410 - MID
                boolean skip,
//////// SKIPPABLE 20140410 - END
                boolean waitMessage, int levelEnd) {
            resource = level;
            musicResource = music;
            dialogResources = dialogs;
            name = title;
            number = nb;
            introAnim = intro;
            outroAnim = outro;
            completed = false;
            selectable = select;
            diaryCollected = false;
            restartable = restartOnDeath;
//////// SKIPPABLE 20140410 - MID
            skippable = skip;
//////// SKIPPABLE 20140410 - END
            showWaitMessage = waitMessage;
            levelEndResource = levelEnd;
        }
    }

    public static class DialogEntry {
        public int characterEntry = 0;
        public ArrayList<ConversationUtils.Conversation> characterConversations;

        public ConversationUtils.Conversation getConversation(int index) {
            for (int i = 0; i < characterConversations.size(); ++i) {
            	ConversationUtils.Conversation conv = characterConversations.get(i);
                if (conv.id == index) {
                    return conv;
                }
            }
            return null;
        }
    }
    public final static ArrayList<LevelGroup> levels = new ArrayList<LevelGroup>();
    private static boolean mLoaded = false;
    private static int mLoadedResource = 0;
    
    public static final Level get(int row, int index) {
    	return levels.get(row).levels.get(index);
    }
    
    public static final boolean isLoaded(int resource) {
    	return mLoaded && mLoadedResource == resource;
    }
    
    public static final void loadLevelTree(int resource, Context context) {
        if (levels.size() > 0 && mLoadedResource == resource) {
        	// already loaded
        	return;
        }
        
    	XmlResourceParser parser = context.getResources().getXml(resource);
        
        levels.clear();
        
        LevelGroup currentGroup = null;
        Level currentLevel = null;
        
        try { 
            int eventType = parser.getEventType(); 
            while (eventType != XmlPullParser.END_DOCUMENT) { 
                if(eventType == XmlPullParser.START_TAG) { 
                	if (parser.getName().equals("group")) {
                		currentGroup = new LevelGroup();
                		levels.add(currentGroup);
                		currentLevel = null;
                	}
                	
                    if (parser.getName().equals("level") && currentGroup != null) {
                    	int levelResource = 0;
                    	int musicResource = 0;
                    	String titleString = null;
                    	String number = null;
                    	int selectable = 0;
                    	int introResource = -1;
                    	int outroResource = -1;
                        boolean restartable = true;
//////// SKIPPABLE 20140410 - MID
                        boolean skippable = true;
//////// SKIPPABLE 20140410 - END
                        boolean showWaitMessage = false;
                        int levelEndResource = 0;
                        for(int i=0; i < parser.getAttributeCount(); i++) {
                    		if (parser.getAttributeName(i).equals("selectable")) {
                            	selectable = parser.getAttributeIntValue(i, 0);
                            } else if (parser.getAttributeName(i).equals("restartable")) {
                                if (parser.getAttributeValue(i).equals("false")) {
                                    restartable = false;
                                }
//////// SKIPPABLE 20140410 - MID
                    	    } else if (parser.getAttributeName(i).equals("skippable")) {
                                if (parser.getAttributeValue(i).equals("false")) {
                                    skippable = false;
                                }
//////// SKIPPABLE 20140410 - END
                            } else if (parser.getAttributeName(i).equals("waitmessage")) {
                                if (parser.getAttributeValue(i).equals("true")) {
                                    showWaitMessage = true;
                                }
                            } else {
                                final int value = parser.getAttributeResourceValue(i, -1);
                                if (value != -1) {
                                    if (parser.getAttributeName(i).equals("resource")) {
                                        levelResource = value;
                                    } else if (parser.getAttributeName(i).equals("music")) {
                                        musicResource = value;
                                    } else if (parser.getAttributeName(i).equals("title")) {
                                        titleString = context.getString(value);
                                    } else if (parser.getAttributeName(i).equals("number")) {
                                        number = context.getString(value);
                                    } else if (parser.getAttributeName(i).equals("intro")) {
                                        introResource = value;
                                    } else if (parser.getAttributeName(i).equals("outro")) {
                                        outroResource = value;
                                    } else if (parser.getAttributeName(i).equals("resultScreen")) {
                                        levelEndResource = value;
                                    }
                                }
                            }
                        }
//////// SKIPPABLE 20140410 - BEGIN
//                        currentLevel = new Level(levelResource, null, titleString, number, introResource, outroResource, musicResource, selectable, restartable, showWaitMessage);
//////// SKIPPABLE 20140410 - MID
//                        currentLevel = new Level(levelResource, null, titleString, number, introResource, outroResource, musicResource, selectable, restartable, skippable, showWaitMessage);
                        currentLevel = new Level(levelResource, null, titleString, number, introResource, outroResource, musicResource, selectable, restartable, skippable, showWaitMessage, levelEndResource);
//////// SKIPPABLE 20140410 - END
                        currentGroup.levels.add(currentLevel);
                    }

/*
// !!!! TODO : see what to do with "diary" !!!!
                    if (parser.getName().equals("dialog") && currentLevel != null) {
                        currentDialog = new DialogEntry();
                        currentLevel.dialogResources = currentDialog;
                    }

                    if (parser.getName().equals("diary") && currentDialog != null) {
                        for (int i=0; i < parser.getAttributeCount(); i++) {
                            final int value = parser.getAttributeResourceValue(i, -1);
                            if (value != -1) {
                                if (parser.getAttributeName(i).equals("resource")) {
                                	currentDialog.diaryEntry = value;
                                }
                               
                            }
                    	}
                    }
*/
                } 
                eventType = parser.next(); 
            } 
        } catch(Exception e) { 
                DebugLog.e("LevelTree", e.getStackTrace().toString()); 
        } finally { 
            parser.close(); 
        } 
        mLoaded = true;
        mLoadedResource = resource;
    }
    
    public final static void loadLevelDialog(Context context,
            Level level, int dialogsResource) {
    	if (level != null && dialogsResource != -1) {
    		DialogEntry dialog = new DialogEntry();
    		dialog.characterEntry = dialogsResource;
    		dialog.characterConversations = ConversationUtils.loadDialog(dialogsResource, context);

    		level.dialogResources = dialog;
    	}
    }

	public final static void updateCompletedState(int levelRow, int completedLevels) {
//////// REPLAY 20140326 - BEGIN
		final int rowCount = levels.size();
		for (int x = 0; x < rowCount; x++) {
//////// REPLAY 20140326 - MID
//		for (int x = 0; x <= levelRow; x++) {
//////// REPLAY 20140326 - END
			final LevelGroup group = levels.get(x);
			final int levelCount = group.levels.size();
			for (int y = 0; y < levelCount; y++) {
				final Level level = group.levels.get(y);
				if (x < levelRow) {
					level.completed = true;
				} else if (x == levelRow) {
					if ((completedLevels & (1 << y)) != 0) {
						level.completed = true;
					}
				} else {
					level.completed = false;
				}
			}
		}
		
	}

	public final static int packCompletedLevels(int levelRow) {
		int completed = 0;
		final LevelGroup group = levels.get(levelRow);
		final int levelCount = group.levels.size();
		for (int y = 0; y < levelCount; y++) {
			final Level level = group.levels.get(y);
			if (level.completed) {
				completed |= 1 << y;
			}
		}
		return completed;
	}

	public static boolean levelIsValid(int row, int index) {
		boolean valid = false;
		if (row >= 0 && row < levels.size()) {
			final LevelGroup group = levels.get(row);
			if (index >=0 && index < group.levels.size()) {
				valid = true;
			}
		}
		
		return valid;
	}
	
	public static boolean rowIsValid(int row) {
		boolean valid = false;
		if (row >= 0 && row < levels.size()) {
			valid = true;
		}
		
		return valid;
	}
    
}
