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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.SparseArray;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

// !!!! TODO: add menu handling to unlock all levels when in debug !!!!
// like in ReplicaIsland

public abstract class LevelSelectActivity extends Activity {
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private final static class LevelDataComparator implements Comparator<LevelMetaData> {
        public int compare(final LevelMetaData object1, final LevelMetaData object2) {
            int result = 0;
            if (object1 == null && object2 != null) {
                result = 1;
            } else if (object1 != null && object2 == null) {
                result = -1;
            } else if (object1 != null && object2 != null) {
                result = object1.level.number.compareTo(object2.level.number);
            }
            return result;
        }
    }


    private class MapLayerData {
        public int image;
        public float zoomFactor;
        public boolean fitScreen;
        public float alpha;

        public MapLayerData() {
            image = -1;
            zoomFactor = 1.0f;
            fitScreen = false;
            alpha = 1.0f;
        }
    }

    private class MapLevelData {
        public float x;
        public float y;
        public int background;
        public int foreground;
        public int description;

        public MapLevelData() {
            x = 0.0f;
            y = 0.0f;
            background = -1;
            foreground = -1;
            description = -1;
        }
    }


    private class LevelMetaData {
        public boolean enabled;
        public int x;
        public int y;
        public LevelTree.Level level;
        public MapLevelData mapLevelData;


        @Override
        public String toString() {
            return level.name;
        }
    }


    private class LevelsList {
        final private Context mContext;
        final private int mLevelResource;
        final private int mCompletedLevelResource;
        final private int mDisabledLevelResource;
        final private int mSelectedLevelResource;
        final private List<LevelMetaData> mLevelDataList;
        final private int mSize;


        public LevelsList(Context context, int resource,
                int completedResource, int disabledResource, int selectedResource,
                boolean unlockAllLevels, SparseArray<MapLevelData> listMapLevelData) {
            mContext = context;
            mLevelResource = resource;
            mCompletedLevelResource = completedResource;
            mDisabledLevelResource = disabledResource;
            mSelectedLevelResource = selectedResource;

            mLevelDataList = new ArrayList<LevelMetaData>();
            if (unlockAllLevels) {
                generateLevelList(false);
                for (LevelMetaData level : mLevelDataList) {
                    level.enabled = true;
                }
            } else {
                generateLevelList(true);
            }
// !!!! TODO: check that sort is done ok !!!!
            Collections.sort(mLevelDataList, new LevelDataComparator());
            mSize = mLevelDataList.size();

            // associate map data to level data
            for (int i = 0; i < mLevelDataList.size(); ++i) {
                LevelMetaData data = mLevelDataList.get(i);
                int selectId = Math.abs(data.level.selectable);
                data.mapLevelData = listMapLevelData.get(selectId);
                if (data.mapLevelData == null) {
                	data.mapLevelData = listMapLevelData.get(0);
                }
            }
        }

        protected void generateLevelList(final boolean onlySelectable) {
            final int count = LevelTree.levels.size();
            boolean oneBranchUnlocked = false;
            for (int x = 0; x < count; x++) {
                boolean anyUnlocksThisBranch = false;
                final LevelTree.LevelGroup group = LevelTree.levels.get(x);
                for (int y = 0; y < group.levels.size(); y++) {
                    LevelTree.Level level = group.levels.get(y);
                    boolean enabled = false;
                    if (!level.completed && !oneBranchUnlocked) {
                        enabled = true;
                        anyUnlocksThisBranch = true;
                    }
                    if (enabled || level.completed || !onlySelectable || (onlySelectable && (level.selectable > 0))) {
                        addItem(level, x, y, enabled);
                    }
                }
                if (anyUnlocksThisBranch) {
                    oneBranchUnlocked = true;
                }
            }
        }

        private void addItem(LevelTree.Level level, int x, int y, boolean enabled) {
            LevelMetaData data = new LevelMetaData();
            data.level = level;
            data.x = x;
            data.y = y;
            data.enabled = enabled;
            mLevelDataList.add(data);
        }

        public int size() {
            return mSize;
        }

        public LevelMetaData get(final int position) {
            return mLevelDataList.get(position);
        }

        public boolean isEnabled(int position) {
//////// REPLAY - BEGIN
            return mLevelDataList.get(position).enabled;
//////// REPLAY - MID
//            return (mLevelDataList.get(position).enabled || mLevelDataList.get(position).level.completed);
//////// REPLAY - END
        }

        public View createView(int position, ViewGroup parent) {
            View view = null;
            if (mLevelDataList.get(position).enabled) {
                view = LayoutInflater.from(mContext).inflate(mLevelResource, parent, false);
            } else if (mLevelDataList.get(position).level.completed) {
                view = LayoutInflater.from(mContext).inflate(mCompletedLevelResource, parent, false);
            } else {
                view = LayoutInflater.from(mContext).inflate(mDisabledLevelResource, parent, false);
            }

            ImageView image = (ImageView)view.findViewById(UtilsResources.getResourceIdByName(getApplicationContext(), "id", "image"));
            if (image.getDrawable() instanceof AnimationDrawable) {
                ((AnimationDrawable)image.getDrawable()).start();
            }

            view.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            LevelSelectActivity.this.initSelectLevel(v);
                            break;
                        }
                        return false; // leave the event handling to the Activity
                    }
                }
            );

            return view;
        }

        public View getSelectedView(ViewGroup parent) {
            View sourceView = LayoutInflater.from(mContext).inflate(mSelectedLevelResource, parent, false);

            ImageView image = (ImageView)sourceView.findViewById(UtilsResources.getResourceIdByName(getApplicationContext(), "id", "image"));
            if (image.getDrawable() instanceof AnimationDrawable) {
                ((AnimationDrawable)image.getDrawable()).start();
            }

            return sourceView;
        }

    }


    ////////////////////////////////////////////////////////////////

    private static final int INVALID_ID = -1;

    private LevelsList levelsList;

    private ImageView mapBackground;
    private ImageView mapMainLayer;
    private float levelPercentWidth;
    private float levelPercentHeight;
    private float zoomInit;
    private float zoomMin;
    private float zoomMax;

    private View selectedLevelView;
    private float selectedLevelPercentWidth;
    private float selectedLevelPercentHeight;

    private RelativeLayout selectedLevelLayout;
    private ImageView selectedLevelBackground;
    private ImageView selectedLevelForeground;
    private TextView selectedLevelTitle;
    private TextView selectedLevelDescription;

    private boolean startLevel;
    private ImageButton startButton;
    private View.OnClickListener startButtonListener;

    private Animation buttonFlickerAnimation;

    private ViewsManager viewsManager;
    private int mode;
    private Vector2 start;
    private Vector2 mid;
    private Vector2 init;
    private Vector2 tmp;
    private int activePointerId;
    private float oldDist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context context = getApplicationContext();

// !!!! ???? TODO: set styles, themes, etc. here ? ???? !!!!

        setContentView(UtilsResources.getResourceIdByName(context, "layout", "map"));
        ViewGroup parent = (ViewGroup)findViewById(UtilsResources.getResourceIdByName(context, "id", "mapLayout"));

        mapBackground = (ImageView)findViewById(UtilsResources.getResourceIdByName(context, "id", "mapBackground"));
        mapMainLayer = (ImageView)findViewById(UtilsResources.getResourceIdByName(context, "id", "mapMainLayer"));

        selectedLevelLayout = (RelativeLayout)findViewById(UtilsResources.getResourceIdByName(context, "id", "levelLayout"));
        selectedLevelLayout.setVisibility(View.INVISIBLE);
        selectedLevelBackground = (ImageView)selectedLevelLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "levelBackground"));
        selectedLevelForeground = (ImageView)selectedLevelLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "levelForeground"));
        selectedLevelTitle = (TextView)selectedLevelLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "levelTitle"));
        selectedLevelDescription = (TextView)selectedLevelLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "levelDescription"));

        startButton = (ImageButton)findViewById(UtilsResources.getResourceIdByName(context, "id", "levelGoButton"));
        startButtonListener =
            new View.OnClickListener() {
                public void onClick(View v) {
                startLevel();
            }
        };
        startButton.setOnClickListener(startButtonListener);

        buttonFlickerAnimation = AnimationUtils.loadAnimation(this, UtilsResources.getResourceIdByName(context, "anim", "button_flicker"));

        final int layerLayoutRsc = UtilsResources.getResourceIdByName(context, "layout", "map_layer");


        // load map data
        final int mapFile = UtilsResources.getResourceIdByName(context, "xml", "map");
        List<MapLayerData> listBackgroundMapLayerData = new ArrayList<MapLayerData>();
        List<MapLayerData> listForegroundMapLayerData = new ArrayList<MapLayerData>();
        SparseArray<MapLevelData> listMapLevelData = new SparseArray<MapLevelData>();
        readMapData(mapFile, listBackgroundMapLayerData, listForegroundMapLayerData, listMapLevelData);


        // generate levels data
        levelsList = new LevelsList(this,
                UtilsResources.getResourceIdByName(context, "layout", "map_level_enabled"),
                UtilsResources.getResourceIdByName(context, "layout", "map_level_completed"),
                UtilsResources.getResourceIdByName(context, "layout", "map_level_disabled"),
                UtilsResources.getResourceIdByName(context, "layout", "map_level_selected"),
                getIntent().getBooleanExtra("unlockAll", false),
                listMapLevelData);
        listMapLevelData.clear();

        final int nbLayers = listBackgroundMapLayerData.size() + listForegroundMapLayerData.size();
        viewsManager = new ViewsManager(parent, mapMainLayer, nbLayers, levelsList.size(), zoomInit, zoomMin, zoomMax);


        // background layers
        for (int i = 0; i < listBackgroundMapLayerData.size(); ++i) {
            final MapLayerData layerData = listBackgroundMapLayerData.get(i);

            ImageView layerView = (ImageView)LayoutInflater.from(this).inflate(layerLayoutRsc, parent, false);
            if (layerData.fitScreen) {
                layerView.setScaleType(ScaleType.FIT_XY);
            }
            layerView.setAlpha(layerData.alpha);
            UtilsResources.setAnimatableImageResource(layerView, layerData.image);

            if (viewsManager.addLayer(layerView, layerData.zoomFactor)) {
                parent.addView(layerView);
            }
        }
        listBackgroundMapLayerData.clear();

        mapMainLayer.bringToFront(); // force in front of background layers


        // get screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final float screenWidth = dm.widthPixels;
        final float screenHeight = dm.heightPixels;
        final float halfScreenWidth = (screenWidth / 2.0f);
        final float halfScreenHeight = (screenHeight / 2.0f);
DebugLog.e("LEVEL", "screenWidth : " + screenWidth);
DebugLog.e("LEVEL", "screenHeight: " + screenHeight);

        // get main layer image size
        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
// !!!! ???? TODO: what happens if "map_main == "" ? ???? !!!!
// => shouldn't check that "getResourceIdByName" not null?
        BitmapFactory.decodeResource(getResources(), UtilsResources.getResourceIdByName(context, "drawable", "map_main"), dimensions);
        final float mapWidth = dimensions.outWidth;
        final float mapHeight = dimensions.outHeight;
DebugLog.e("LEVEL", "map width  : " + mapWidth);
DebugLog.e("LEVEL", "map height : " + mapHeight);

        // get ratios
        final float widthRatio = screenWidth / mapWidth;
        final float heightRatio = screenHeight / mapHeight;
DebugLog.e("LEVEL", "widthRatio : " + widthRatio);
DebugLog.e("LEVEL", "heightRatio : " + heightRatio);

        // compute offsets & size ratio
        float ratio = 1.0f;
        float widthOffset = 0.0f;
        float heightOffset = 0.0f;
        if (widthRatio > heightRatio) {
            // fit height
            ratio = heightRatio;
            widthOffset = (screenWidth - (mapWidth * heightRatio)) / 2.0f;
        } else if (widthRatio < heightRatio) {
            // fit width
            ratio = widthRatio;
            heightOffset = (screenHeight - (mapHeight * widthRatio)) / 2.0f;
        }
DebugLog.e("LEVEL", "widthOffset : " + widthOffset);
DebugLog.e("LEVEL", "heightOffset : " + heightOffset);

        final float sizeRatio = ratio;
        final float offsetX = widthOffset;
        final float offsetY = heightOffset;

        // set levels sizes
        float levelWidth = levelPercentWidth * screenWidth;
        float levelHeight = levelPercentHeight * screenHeight;
        if (levelWidth == 0.0f) {
        	levelWidth = levelHeight;
        } else if (levelHeight == 0.0f) {
        	levelHeight = levelWidth;
        }
        viewsManager.setLevelsSize(levelWidth, levelHeight);

        // add level views
        for (int i = 0; i < levelsList.size(); ++i) {
            View view = levelsList.createView(i, parent);

            MapLevelData mapData = levelsList.get(i).mapLevelData;
            final float x = mapData.x * mapWidth;
            final float y = mapData.y * mapHeight;

            final float posX = (x * sizeRatio) + offsetX - halfScreenWidth;
            final float posY = (y * sizeRatio) + offsetY - halfScreenHeight;
DebugLog.e("LEVEL", "posX : " + posX);
DebugLog.e("LEVEL", "posY : " + posY);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)levelWidth, (int)levelHeight);
            params.leftMargin = (int)halfScreenWidth;
            params.topMargin = (int)halfScreenHeight;

            if (viewsManager.addLevel(view, posX, posY)) {
                parent.addView(view, params);
            }
        }


        // selected level
        float selectedLevelWidth = selectedLevelPercentWidth * screenWidth;
        float selectedLevelHeight = selectedLevelPercentHeight * screenHeight;
        if (selectedLevelWidth == 0.0f) {
            selectedLevelWidth = selectedLevelHeight;
        } else if (selectedLevelHeight == 0.0f) {
            selectedLevelHeight = selectedLevelWidth;
        }

        View levelSelectionView = levelsList.getSelectedView(parent);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)selectedLevelWidth, (int)selectedLevelHeight);
        params.leftMargin = (int)halfScreenWidth;
        params.topMargin = (int)halfScreenHeight;
        parent.addView(levelSelectionView, params);

        viewsManager.setSelectedView(levelSelectionView, selectedLevelWidth, selectedLevelHeight);


        // foreground layers
        for (int i = 0; i < listForegroundMapLayerData.size(); ++i) {
            final MapLayerData layerData = listForegroundMapLayerData.get(i);

            ImageView layerView = (ImageView)LayoutInflater.from(this).inflate(layerLayoutRsc, parent, false);
            if (layerData.fitScreen) {
                layerView.setScaleType(ScaleType.FIT_XY);
            }
            layerView.setAlpha(layerData.alpha);
            UtilsResources.setAnimatableImageResource(layerView, layerData.image);

            if (viewsManager.addLayer(layerView, layerData.zoomFactor)) {
                parent.addView(layerView);
            }
        }
        listForegroundMapLayerData.clear();



        // selection layout is in front of everything
        selectedLevelLayout.bringToFront();



        startLevel = false;
        selectedLevelView = null;

        start = new Vector2();
        init = new Vector2();
        tmp = new Vector2();

        mid = new Vector2(halfScreenWidth, halfScreenHeight); // centre of screen

        mode = NONE;
        activePointerId = INVALID_ID;
        oldDist = 1.0f;

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume() {
        super.onResume();
//?        MusicManager.start(this, MusicManager.MUSIC_LEVEL_SELECT, true);
        MusicManager.start(this, MusicManager.MUSIC_LEVEL_SELECT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.stop();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // start gesture
            {
                mode = DRAG;
                final int pointerIndex = event.getActionIndex(); 
                activePointerId = event.getPointerId(0);
                start.set(event.getX(pointerIndex), event.getY(pointerIndex));
                init.set(start);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: // second finger
            {
                cancelSelectLevel();
                oldDist = spacing(event);
                if (oldDist > 10.0f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                mode = NONE;
                activePointerId = INVALID_ID;
                selectLevel();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
            {
                mode = DRAG;
                final int pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == activePointerId) {
                    final int newPointerIndex = (pointerIndex == 0) ? 1 : 0;
                    start.set(event.getX(newPointerIndex), event.getY(newPointerIndex));
                    activePointerId = event.getPointerId(newPointerIndex);
                } else {
                    final int activePointerIndex = event.findPointerIndex(activePointerId);
                    start.set(event.getX(activePointerIndex), event.getY(activePointerIndex));
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                if (mode == DRAG) {
                    final int pointerIndex = event.findPointerIndex(activePointerId);
                    final float x = event.getX(pointerIndex);
                    final float y = event.getY(pointerIndex);

                    // allow level selection even if small pan
                    tmp.set(x - init.x, y - init.y);
                    if (tmp.length2() > 25.0f) {
                        cancelSelectLevel();
                    }

                    viewsManager.doPan(x - start.x, y - start.y);
                    start.set(x, y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10.0f) {
                        float scale = newDist / oldDist;
                        oldDist = newDist;
                        viewsManager.doZoom(scale, mid);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            {
                activePointerId = INVALID_ID;
                break;
            }
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = false;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelSelectLevel();
            result = true;
        } else {
            result = super.onKeyDown(keyCode, event);
        }

        return result;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean result = false;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            result = true;
        }
        return result;
    }


    ////////////////////////////////////////////////////////////////

    final protected void pan(final float x, final float y) {
        viewsManager.doPan(x, y);
    }

    final protected void zoom(final float scale) {
        viewsManager.doZoom(scale, mid);
    }

    final private float spacing(final MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt((x * x) + (y * y));
    }

    final private void midPoint(Vector2 point, final MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    ////////////////////////////////////////////////////////////////

    private void readMapData(final int mapFile,
            List<MapLayerData> listBackgroundMapLayerData, List<MapLayerData> listForegroundMapLayerData,
            SparseArray<MapLevelData> listMapLevelData) {
        XmlResourceParser parser = this.getResources().getXml(mapFile);

        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("background")) {
                        int backgroundRsc = -1;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("resource")) {
                                backgroundRsc = parser.getAttributeResourceValue(i, -1);
                            }
                        }

                        UtilsResources.setAnimatableImageResource(mapBackground, backgroundRsc);
                    } else if (parser.getName().equals("layerMain")) {
                        int mainRsc = -1;
                        zoomInit = 1.0f;
                        zoomMin = 1.0f;
                        zoomMax = 0.0f;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("resource")) {
                                mainRsc = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("levelWidth")) {
                                levelPercentWidth = parser.getAttributeFloatValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("levelHeight")) {
                                levelPercentHeight = parser.getAttributeFloatValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("selectedLevelWidth")) {
                                selectedLevelPercentWidth = parser.getAttributeFloatValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("selectedLevelHeight")) {
                                selectedLevelPercentHeight = parser.getAttributeFloatValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("zoomInit")) {
                                zoomInit = parser.getAttributeFloatValue(i, 1.0f);
                            } else if (parser.getAttributeName(i).equals("zoomMin")) {
                                zoomMin = parser.getAttributeFloatValue(i, 1.0f);
                            } else if (parser.getAttributeName(i).equals("zoomMax")) {
                                zoomMax = parser.getAttributeFloatValue(i, 0.0f);
                            }
                        }

                        UtilsResources.setAnimatableImageResource(mapMainLayer, mainRsc);

                        zoomMin = Math.max(zoomMin, 1.0f); // minimum 1.0f
                        zoomInit = Math.max(zoomInit, zoomMin); // minimum "zoomMin"
                        zoomMax = Math.max(zoomMax, zoomInit); // minimum "zoomInit"

                    } else if (parser.getName().equals("layer")) {
                        int image = -1;
                        float zoomFactor = 1.0f;
                        boolean foreground = true;
                        boolean fitScreen = false;
                        float alpha = 1.0f;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("resource")) {
                                image = parser.getAttributeResourceValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("zoomFactor")) {
                                zoomFactor = parser.getAttributeFloatValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("foreground")) {
                                foreground = parser.getAttributeBooleanValue(i, true);
                            } else if (parser.getAttributeName(i).equals("fitScreen")) {
                                fitScreen = parser.getAttributeBooleanValue(i, true);
                            } else if (parser.getAttributeName(i).equals("alpha")) {
                                alpha = parser.getAttributeFloatValue(i, -1);
                            }
                        }

                        MapLayerData layerData = new MapLayerData();
                        layerData.image = image;
                        layerData.zoomFactor = zoomFactor;
                        layerData.fitScreen = fitScreen;
                        layerData.alpha = alpha;
                        if (foreground) {
                            listForegroundMapLayerData.add(layerData);
                        } else {
                            listBackgroundMapLayerData.add(layerData);
                        }

                    } else if (parser.getName().equals("levelDefault")) {
                        int background = -1;
                        int foreground = -1;
                        int description = 0;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("background")) {
                                background = parser.getAttributeResourceValue(i, 0);
                            } else if (parser.getAttributeName(i).equals("foreground")) {
                                foreground = parser.getAttributeResourceValue(i, 0);
                            } else if (parser.getAttributeName(i).equals("description")) {
                                description = parser.getAttributeResourceValue(i, 0);
                            }
                        }

                        MapLevelData levelData = new MapLevelData();
                        levelData.x = -1.0f; // 0.0f ?
                        levelData.y = -1.0f; // 0.0f ?
                        levelData.background = background;
                        levelData.foreground = foreground;
                        levelData.description = description;
                        listMapLevelData.put(0, levelData);

                    } else if (parser.getName().equals("level")) {
                        int id = -1;
                        float x = 0.0f;
                        float y = 0.0f;
                        int background = -1;
                        int foreground = -1;
                        int description = 0;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("id")) {
                                id = parser.getAttributeIntValue(i, -1);
                            } else if (parser.getAttributeName(i).equals("x")) {
                                x = parser.getAttributeFloatValue(i, 0.0f);
                            } else if (parser.getAttributeName(i).equals("y")) {
                                y = parser.getAttributeFloatValue(i, 0.0f);
                            } else if (parser.getAttributeName(i).equals("background")) {
                                background = parser.getAttributeResourceValue(i, 0);
                            } else if (parser.getAttributeName(i).equals("foreground")) {
                                foreground = parser.getAttributeResourceValue(i, 0);
                            } else if (parser.getAttributeName(i).equals("description")) {
                                description = parser.getAttributeResourceValue(i, 0);
                            }
                        }

                        MapLevelData levelData = new MapLevelData();
                        levelData.x = x;
                        levelData.y = y;
                        levelData.background = background;
                        levelData.foreground = foreground;
                        levelData.description = description;
                        listMapLevelData.put(id, levelData);
                    }
                }
                eventType = parser.next();
            }

        } catch(Exception e) {
            DebugLog.e("LevelSelectActivity", e.getStackTrace().toString());
        } finally {
            parser.close();
        }
    }


    ////////////////////////////////////////////////////////////////

    private void initSelectLevel(final View selectedView) {
        selectedLevelView = selectedView;
    }

    private boolean selectLevel() {
        boolean selected = false;
        if (selectedLevelView != null) {
            int selectedLevelIndex = viewsManager.selectLevel(selectedLevelView);
            selectedLevelView = null;
            if (selectedLevelIndex == -1) {
                return false;
            }
            selected = true;

            updateLevelLayout(selectedLevelIndex);
        }
        return selected;
    }

    private boolean selectLevel(final int index) {
        boolean selected = false;

        int selectedLevelIndex = viewsManager.selectLevel(index);
        if (selectedLevelIndex == -1) {
            return false;
        }
        selected = true;

        updateLevelLayout(selectedLevelIndex);

        return selected;
    }

    protected void cancelSelectLevel() {
    	selectedLevelView = null;

        selectedLevelLayout.setVisibility(View.GONE);
        viewsManager.selectLevel(null);
    }

    protected void selectNextLevel() {
        final int selectedLevelIndex = viewsManager.getSelectedLevelIndex();
        if (selectedLevelIndex == -1) {
            selectLevel(levelsList.size() - 1);
        } else if (selectedLevelIndex < levelsList.size() - 1) {
            selectLevel(selectedLevelIndex + 1);
        }
    }

    protected void selectPreviousLevel() {
        final int selectedLevelIndex = viewsManager.getSelectedLevelIndex();
        if (selectedLevelIndex == -1) {
            selectLevel(0);
        } else if (selectedLevelIndex > 0) {
            selectLevel(selectedLevelIndex - 1);
        }
    }

    private void startLevel() {
        if (!startLevel) {
            int selectedLevelIndex = viewsManager.getSelectedLevelIndex();
            if (selectedLevelIndex == -1) {
                return;
            }

            LevelMetaData selectedLevel = levelsList.get(selectedLevelIndex);
//////// REPLAY - BEGIN
            if (selectedLevel.enabled) {
//////// REPLAY - MID
//            if (selectedLevel.enabled || selectedLevel.level.completed) {
//////// REPLAY - END
            	startLevel = true;

                //MusicManager.stop(); //=> if want to play another musical sound
                SoundManager.play(SoundManager.SOUND_START);

                Intent intent = new Intent();
                intent.putExtra("resource", selectedLevel.level.resource);
                intent.putExtra("row", selectedLevel.x);
                intent.putExtra("index", selectedLevel.y);

                startButton.startAnimation(buttonFlickerAnimation);
                buttonFlickerAnimation.setAnimationListener(new EndActivityAfterAnimation(intent));
            }
        }
    }

    private void updateLevelLayout(final int levelIndex) {
        final LevelMetaData levelMetaData = levelsList.get(levelIndex);
        final MapLevelData mapData = levelMetaData.mapLevelData;

        selectedLevelTitle.setText(levelMetaData.level.name);
        UtilsResources.setAnimatableImageResource(selectedLevelBackground, mapData.background);
        UtilsResources.setAnimatableImageResource(selectedLevelForeground, mapData.foreground);

        if (mapData.description != 0) {
            selectedLevelDescription.setText(getString(mapData.description));
        } else {
            selectedLevelDescription.setText("");
        }

        startButton.setEnabled(levelsList.isEnabled(levelIndex));
        selectedLevelLayout.setVisibility(View.VISIBLE);
    }


    ////////////////////////////////////////////////////////////////

    protected class EndActivityAfterAnimation implements Animation.AnimationListener {
        private Intent mIntent;

        EndActivityAfterAnimation(Intent intent) {
            mIntent = intent;
        }

        public void onAnimationEnd(Animation animation) {
// !!!! ???? TODO: anything else to do ? ???? !!!!

            setResult(RESULT_OK, mIntent);
            finish();
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }

    }


    ////////////////////////////////////////////////////////////////

    private class ViewsManager {
        private final int MAX_LAYERS;
        private final int MAX_LEVELS;
        private final float ZOOM_MIN;
        private final float ZOOM_MAX;

        private Vector2 currentPan;
        private float currentZoom;

        private View window;

        private View mainLayer;

        private FixedSizeArray<View> layers;
        private FixedSizeArray<Float> layersZooms;

        private FixedSizeArray<View> levels;
        private float levelsWidth;
        private float levelsHeight;
        private FixedSizeArray<Float> levelsXs;
        private FixedSizeArray<Float> levelsYs;
        private View selectedLevelOverlay;
        private int selectedLevelIndex;
        private float selectedLevelWidth;
        private float selectedLevelHeight;

        ViewsManager(final View container, final View map, final int nbLayers, final int nbLevels,
                final float initZoom, final float minZoom, final float maxZoom) {
            window = container;

            MAX_LAYERS = nbLayers;
            MAX_LEVELS = nbLevels;
            ZOOM_MIN = minZoom;
            ZOOM_MAX = maxZoom;

            layers = new FixedSizeArray<View>(MAX_LAYERS);
            layersZooms = new FixedSizeArray<Float>(MAX_LAYERS);

            levels = new FixedSizeArray<View>(MAX_LEVELS);
            levelsXs = new FixedSizeArray<Float>(MAX_LEVELS);
            levelsYs = new FixedSizeArray<Float>(MAX_LEVELS);

            currentPan = new Vector2(0.0f, 0.0f);
            currentZoom = initZoom;

            mainLayer = map;
            selectedLevelOverlay = null;
            selectedLevelIndex = -1;


            onPanZoomChanged();
            mainLayer.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    // This catches when the image bitmap changes, for some reason it doesn't recurse
                    public void onLayoutChange(View v,
                            int left, int top, int right, int bottom,
                            int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        onPanZoomChanged();
                    }
                }
            );
        }

        public boolean addLayer(final View view, final float zoom) {
            if (layers.getCount() == MAX_LAYERS) {
                return false;
            }

            layers.add(view);
            layersZooms.add(zoom);

            onPanZoomChanged();
            view.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    // This catches when the image bitmap changes, for some reason it doesn't recurse
                    public void onLayoutChange(View v,
                           int left, int top, int right, int bottom,
                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        onPanZoomChanged();
                    }
                }
            );

            return true;
        }

        public boolean addLevel(final View view, final float posX, final float posY) {
            if (levels.getCount() == MAX_LEVELS) {
                return false;
            }

            levels.add(view);
            levelsXs.add(posX);
            levelsYs.add(posY);

            onPanZoomChanged();

            return true;
        }

        public void setLevelsSize(final float width, final float height) {
            levelsWidth = width;
            levelsHeight = height;
        }

        public void setSelectedView(final View view, final float width, final float height) {
        	selectedLevelOverlay = view;
            if (selectedLevelOverlay != null) {
            	selectedLevelOverlay.setVisibility(View.GONE);
                selectedLevelWidth = width;
                selectedLevelHeight = height;
            }
            selectedLevelIndex = -1;
        }

        public int selectLevel(final View levelView) {
            selectedLevelIndex = -1;
            if (selectedLevelOverlay == null) {
                return -1;
            }

            selectedLevelOverlay.setVisibility(View.GONE);

            if (levelView != null) {
                selectedLevelIndex = levels.find(levelView, true);
                if (selectedLevelIndex != -1) {
                    selectedLevelOverlay.setVisibility(View.VISIBLE);
                }
            }

            return selectedLevelIndex;
        }

        public int selectLevel(final int levelIndex) {
            selectedLevelIndex = -1;
            if (selectedLevelOverlay == null) {
                return -1;
            }

            selectedLevelOverlay.setVisibility(View.GONE);

            if (levelIndex >= 0 && levelIndex < levels.getCount()) {
                selectedLevelIndex = levelIndex;
                selectedLevelOverlay.setVisibility(View.VISIBLE);
            }

            return selectedLevelIndex;
        }

        public int getSelectedLevelIndex() {
            return selectedLevelIndex;
        }

        public void doZoom(float scale, Vector2 zoomCenter) {
            float oldZoom = currentZoom;

            currentZoom *= scale;
            currentZoom = Math.max(ZOOM_MIN, currentZoom);
            currentZoom = Math.min(ZOOM_MAX, currentZoom);

            // adjust pan accordingly
            // => point under zoomCenter remains under zoom center after zoom
            float width = window.getWidth();
            float height = window.getHeight();
            float oldScaledWidth = width * oldZoom;
            float oldScaledHeight = height * oldZoom;
            float newScaledWidth = width * currentZoom;
            float newScaledHeight = height * currentZoom;

            float reqXPos = ((oldScaledWidth - width) * 0.5f + zoomCenter.x - currentPan.x) / oldScaledWidth;
            float reqYPos = ((oldScaledHeight - height) * 0.5f + zoomCenter.y - currentPan.y) / oldScaledHeight;
            float actualXPos = ((newScaledWidth - width) * 0.5f + zoomCenter.x - currentPan.x) / newScaledWidth;
            float actualYPos = ((newScaledHeight - height) * 0.5f + zoomCenter.y - currentPan.y) / newScaledHeight;

            currentPan.x += (actualXPos - reqXPos) * newScaledWidth;
            currentPan.y += (actualYPos - reqYPos) * newScaledHeight;

            onPanZoomChanged();
        }

        public void doPan(final float panX, final float panY) {
            currentPan.x += panX;
            currentPan.y += panY;
            onPanZoomChanged();
        }

/*
        public void reset() {
            currentZoom = ZOOM_MIN;
            currentPan.set(0f, 0f);
            onPanZoomChanged();
        }
*/

        public void onPanZoomChanged() {
            float winWidth = window.getWidth();
            float winHeight = window.getHeight();
            if (winWidth == 0 || winHeight == 0) {
                return;
            }

            if (currentZoom <= 1.0f) {
                currentPan.x = 0;
                currentPan.y = 0;
            } else {
                float maxPanX = (currentZoom - 1.0f) * window.getWidth() * 0.5f;
                float maxPanY = (currentZoom - 1.0f) * window.getHeight() * 0.5f;
                currentPan.x = Math.max(-maxPanX, Math.min(maxPanX, currentPan.x));
                currentPan.y = Math.max(-maxPanY, Math.min(maxPanY, currentPan.y));
            }


            float bmWidth = mainLayer.getWidth();
            float bmHeight = mainLayer.getHeight();

//////// - 20190110 - INFINITY_SCALE - MID
/*
!!!!  TODO: Fix properly !!!!
Horrible hack to avoid crash due to "mainLayer.getWidth()" or "mainLayer.getHeight()" equal to 0.
(was ~ok until SDK 27, but with 28 it crashes)
*/
            if ((bmWidth != 0.0) && (bmHeight != 0.0)) {
//////// - 20190110 - INFINITY_SCALE - END

            float fitToWindow = Math.min(winWidth / bmWidth, winHeight / bmHeight);
            float mainXOffset = (winWidth - (bmWidth * fitToWindow)) * 0.5f * currentZoom;
            float mainYOffset = (winHeight - (bmHeight * fitToWindow)) * 0.5f * currentZoom;

            // must call this else doesn't work properly
            mainLayer.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

            float mainZoom = currentZoom * fitToWindow;
            mainLayer.setScaleX(mainZoom);
            mainLayer.setScaleY(mainZoom);

            mainXOffset += currentPan.x;
            mainYOffset += currentPan.y;
            mainLayer.setTranslationX(mainXOffset);
            mainLayer.setTranslationY(mainYOffset);

//////// - 20190110 - INFINITY_SCALE - MID
            }
            else {
                DebugLog.e("LEVEL", "onPanZoomChanged - mainLayer size is 0.0 ");
            }
//////// - 20190110 - INFINITY_SCALE - END

            final int nbLayers = layers.getCount();
            for (int i=0; i<nbLayers; ++i) {
                final View layer = layers.get(i);
                if (layer != null) {
                    final float zoomFactor = layersZooms.get(i);
                    final float zoom = 1.0f + ((currentZoom - 1.0f) * zoomFactor); // so that zoom==1.0f => layer fits screen

                    bmWidth = layer.getWidth();
                    bmHeight = layer.getHeight();

//////// - 20190110 - INFINITY_SCALE - MID
                    if ((bmWidth != 0.0) && (bmHeight != 0.0)) {
//////// - 20190110 - INFINITY_SCALE - END

//////// - 20190110 - INFINITY_SCALE - BEGIN
//                    fitToWindow = Math.min(winWidth / bmWidth, winHeight / bmHeight);
//////// - 20190110 - INFINITY_SCALE - MID
                    float fitToWindow = Math.min(winWidth / bmWidth, winHeight / bmHeight);
//////// - 20190110 - INFINITY_SCALE - END
                    float xOffset = (winWidth - (bmWidth * fitToWindow)) * 0.5f * zoom;
                    float yOffset = (winHeight - (bmHeight * fitToWindow)) * 0.5f * zoom;

                    layer.setScaleX(zoom * fitToWindow);
                    layer.setScaleY(zoom * fitToWindow);
                    layer.setTranslationX((currentPan.x * zoomFactor) + xOffset);
                    layer.setTranslationY((currentPan.y * zoomFactor) + yOffset);

//////// - 20190110 - INFINITY_SCALE - MID
                    }
                    else {
                        DebugLog.e("LEVEL", "onPanZoomChanged - layer " + i + " size is 0.0 ");
                    }
//////// - 20190110 - INFINITY_SCALE - END
                }
            }


            final int nbLevels = levels.getCount();
            for (int i=0; i<nbLevels; ++i) {
                final View level = levels.get(i);
                if (level != null) {
                    level.setTranslationX(currentPan.x + (levelsXs.get(i) * currentZoom) - (levelsWidth / 2.0f));
                    level.setTranslationY(currentPan.y + (levelsYs.get(i) * currentZoom) - (levelsHeight / 2.0f));
                }
            }

            if (selectedLevelIndex != -1) {
                final float levelX = levelsXs.get(selectedLevelIndex);
                final float levelY = levelsYs.get(selectedLevelIndex);
                selectedLevelOverlay.setTranslationX(currentPan.x + (levelX * currentZoom) - (selectedLevelWidth / 2.0f));
                selectedLevelOverlay.setTranslationY(currentPan.y + (levelY * currentZoom) - (selectedLevelHeight / 2.0f));
            }
        }
    }
}
