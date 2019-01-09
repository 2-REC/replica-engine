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
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.KeyEvent;
//import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.xmlpull.v1.XmlPullParser;
import java.util.ArrayList;


public abstract class AnimationPlayerActivity extends Activity {
	public static final int INVALID = -1;
	public static final int IMAGES_SEQUENCE = 0;
	public static final int MOVING_IMAGES = 1;
	public static final int MOVING_TEXT = 2;
	public static final int VIDEO = 3;
	
	private AnimationDrawable mAnimation;
	private int mAnimationId;
	protected long mAnimationEndTime;
	private long mAnimationDuration;
	
	private KillActivityHandler mKillActivityHandler = new KillActivityHandler();

    class KillActivityHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
        	AnimationPlayerActivity.this.finish();
        	UtilsActivities.startTransition(AnimationPlayerActivity.this, "activity_fade_in", "activity_fade_out");
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    class AnimationInfo {
        public int mType;
        public int mMusic;
        public int mAnim;

        private class AnimLayer {
            public int mDrawable;
            public float mSizeFactorX;
            public float mSizeFactorY;
            public float mRatio;
            public boolean mKeepWidth;
            public float mPosX;
            public float mPosY;
            public int mAnim;

            public AnimLayer(int drawable, float sizeFactorX, float sizeFactorY, float x, float y, int anim) {
                mDrawable = drawable;
                mSizeFactorX = sizeFactorX;
                mSizeFactorY = sizeFactorY;
                mRatio = 0.0f;
                mKeepWidth = false;
                mPosX = x;
                mPosY = y;
                mAnim = anim;
            }

            public void setRatio(int width, int height, boolean keepWidth) {
                mKeepWidth = keepWidth;
                mRatio = width / (float) height;
            }
        };

        public ArrayList<AnimLayer> listLayers = new ArrayList<AnimLayer>();

        public AnimationInfo(Context context, int animationId) {
            XmlResourceParser parser = (context.getResources().getXml(animationId));

            try {
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.getName().equals("animation")) {
                            for (int i=0; i<parser.getAttributeCount(); i++) {
                                if (parser.getAttributeName(i).equals("type")) {
                                    mType = parser.getAttributeIntValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("music")) {
                                    mMusic = parser.getAttributeResourceValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("anim")) {
                                	mAnim = parser.getAttributeResourceValue(i, -1);
                                }
                            }
                        } else if (parser.getName().equals("layer")) {
                            int drawable = -1;
                            float sizeFactorX = 1.0f;
                            float sizeFactorY = 1.0f;
                            int width = 0;
                            int height = 0;
                            boolean keepWidth = true;
                            float x = 0.5f;
                            float y = 0.5f;
                            int anim = -1;

                            for (int i=0; i<parser.getAttributeCount(); i++) {
                                if (parser.getAttributeName(i).equals("drawable")) {
                                	drawable = parser.getAttributeResourceValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("sizeFactorX")) {
                                	sizeFactorX = parser.getAttributeFloatValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("sizeFactorY")) {
                                	sizeFactorY = parser.getAttributeFloatValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("width")) {
                                	width = parser.getAttributeIntValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("height")) {
                                	height = parser.getAttributeIntValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("ratio")) {
                                    if (parser.getAttributeValue(i).equals("width")) {
                                    	keepWidth = true;
                                    } else if (parser.getAttributeValue(i).equals("height")) {
                                    	keepWidth = false;
                                    }
                                } else if (parser.getAttributeName(i).equals("x")) {
                                    x = parser.getAttributeFloatValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("y")) {
                                    y = parser.getAttributeFloatValue(i, -1);
                                } else if (parser.getAttributeName(i).equals("anim")) {
                                	anim = parser.getAttributeResourceValue(i, -1);
                                }
                            }

                            if (drawable != -1) { // if no drawable => skip
                                final AnimLayer layer = new AnimLayer(drawable, sizeFactorX, sizeFactorY, x, y, anim);
                                if ((width != 0) && (height != 0)) {
                                    layer.setRatio(width, height, keepWidth);
                                }
                                listLayers.add(layer);
                            }
                        }
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                DebugLog.e("AnimationPlayerActivity", e.getStackTrace().toString());
            } finally {
                parser.close();
            }
        }
    };

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent callingIntent = getIntent();
        mAnimationId = callingIntent.getIntExtra("animation", INVALID);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (mAnimationId != INVALID) {
            final Context context = this.getApplicationContext();
            final AnimationInfo animInfo = new AnimationInfo(this, mAnimationId);

            MusicManager.setResource(MusicManager.MUSIC_ANIMATION, animInfo.mMusic);

            if (animInfo.mType == IMAGES_SEQUENCE) {
                setContentView(UtilsResources.getResourceIdByName(context, "layout", "animation_images_sequence"));
                final ImageView canvasImage = (ImageView) findViewById(UtilsResources.getResourceIdByName(context, "id", "animation_canvas"));
                canvasImage.setImageResource(animInfo.mAnim);
                canvasImage.setOnKeyListener(
                    new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            finish();
                            return true;
                        }
                    }
                );
                mAnimation = (AnimationDrawable) canvasImage.getDrawable();
                for (int i=0; i<mAnimation.getNumberOfFrames(); ++i) {
                	mAnimationDuration += mAnimation.getDuration(i);
                }
            } else if (animInfo.mType == MOVING_IMAGES) {
                final int nbLayers = animInfo.listLayers.size();

                if (nbLayers > 0) {
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                    final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    setContentView(UtilsResources.getResourceIdByName(context, "layout", "animation_moving_images"));

                    ViewGroup parent = (ViewGroup) findViewById(UtilsResources.getResourceIdByName(context, "id", "relative_layout"));
// !!!! ???? TODO: OK on parent ? ???? !!!!
                    parent.setOnKeyListener(
                        new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                finish();
                                return true;
                            }
                        }
                    );

                    mAnimationDuration = 0;
                    for (int i=0; i<nbLayers; ++i) {
                        final AnimationInfo.AnimLayer animLayer = animInfo.listLayers.get(i);

                        final FrameLayout frameLayout = (FrameLayout) inflater.inflate(UtilsResources.getResourceIdByName(context, "layout", "animation_moving_image"),
                                                                                         (ViewGroup) parent,
                                                                                         false);

                        int width = (int) (animLayer.mSizeFactorX * dm.widthPixels);
                        int height = (int) (animLayer.mSizeFactorY * dm.heightPixels);
                        if (animLayer.mRatio != 0.0f) {
                            if (animLayer.mKeepWidth) {
                                height = (int) ((animLayer.mSizeFactorX * dm.widthPixels) / animLayer.mRatio);
                            } else {
                                width = (int) ((animLayer.mSizeFactorY * dm.heightPixels) * animLayer.mRatio);
                            }
                        }

                        final ImageView view = (ImageView) frameLayout.findViewById(UtilsResources.getResourceIdByName(context, "id", "image_view"));
                        UtilsResources.setAnimatableImageResource(view, animLayer.mDrawable);
                        view.setLayoutParams(new FrameLayout.LayoutParams(width, height));
                        parent.addView(frameLayout);

                        if (animLayer.mAnim != -1) {
                            final Animation animation = AnimationUtils.loadAnimation(this, animLayer.mAnim);
                            frameLayout.startAnimation(animation);
                            final long duration = animation.getDuration() + animation.getStartOffset();
                            if (mAnimationDuration < duration) {
                            	mAnimationDuration = duration;
                            }
                        } else {
                            // if no animation, set the position (else will be set by animation)
                            // (if setting the position when have an animation, problems occur ...)
                            frameLayout.setX((animLayer.mPosX * dm.widthPixels) - (width / 2.0f));
                            frameLayout.setY((animLayer.mPosY * dm.heightPixels) - (height / 2.0f));
                        }
                    }
                    mAnimationEndTime = mAnimationDuration + System.currentTimeMillis();
                } else {
                    mAnimationEndTime = System.currentTimeMillis();
                }
                animInfo.listLayers.clear();
// !!!! TODO : animation with a sliding text !!!!
/*
            } else if (animInfo.mType == MOVING_TEXT) {
                float startX = 0.0f;
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                setContentView(R.layout.bad_ending_animation);
                startX = 200 * metrics.density;

                // HACK
                // the TranslateAnimation system doesn't support device independent pixels.
                // So for the Wanda ending and Kabocha endings, in which the game over text
                // scrolls in horizontally, compute the size based on the actual density of
                // the display and just generate the anim in code.  The Rokudou animation
                // can be safely loaded from a file.
                Animation gameOverAnim = new TranslateAnimation(startX, 0, 0, 0);
                gameOverAnim.setDuration(6000);
                gameOverAnim.setFillAfter(true);
????                gameOverAnim.setFillEnabled(true);
                gameOverAnim.setStartOffset(8000);
!!!!
gameOverAnim.setInterpolater(new DecelerateInterpolator());
//gameOverAnim.setInterpolater(new AccelerateInterpolator());
!!!!

                View background = findViewById(R.id.animation_background);
                View foreground = findViewById(R.id.animation_foreground);
                View gameOver = findViewById(R.id.game_over);

                Animation foregroundAnim = AnimationUtils.loadAnimation(this, R.anim.horizontal_layer2_slide);
                Animation backgroundAnim = AnimationUtils.loadAnimation(this, R.anim.horizontal_layer1_slide);

                background.startAnimation(backgroundAnim);
                foreground.startAnimation(foregroundAnim);
                gameOver.startAnimation(gameOverAnim);

                mAnimationEndTime = gameOverAnim.getDuration() + System.currentTimeMillis();
*/
            } else if (animInfo.mType == VIDEO) {
                setContentView(UtilsResources.getResourceIdByName(context, "layout", "animation_video"));
                final VideoView videoView = (VideoView) findViewById(UtilsResources.getResourceIdByName(context, "id", "video_canvas"));
// !!!! ???? TODO: need to fix ? ???? !!!!
DebugLog.d("APP", "VIDEO: " + "android.resource://" + getPackageName() + "/" + animInfo.mAnim);
                videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + animInfo.mAnim));

                final MediaController mc = new MediaController(this);
                videoView.setMediaController(mc);
                mc.setAnchorView(videoView);

                // finish activity when video playback is completed
                videoView.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            finish();
                        }
                    }
                );

                // hide all children when view is ready (to hide control buttons)
                videoView.setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            int childs = mc.getChildCount();
                            for (int i=0; i<childs; ++i) {
                                View child = mc.getChildAt(i);
                                child.setVisibility(View.GONE);
                            }
                            mAnimationDuration = videoView.getDuration();
                            mAnimationEndTime = mAnimationDuration + System.currentTimeMillis();
                            videoView.requestFocus();
                            videoView.start();
                        }
                    }
                );

                // finish animation when press a button
                videoView.setOnKeyListener(
                    new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            finish();
                            return true;
                        }
                    }
                );
            } else {
                //ERROR!
                DebugLog.e("AnimationPlayerActivity", "Invalid animation type !");
                finish();
            }
        }

        // Pass the calling intent back so that we can figure out which animation just played
        setResult(RESULT_OK, callingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.start(this, MusicManager.MUSIC_ANIMATION);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.stop();
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus && mAnimation != null) {
			mAnimation.start();
			mKillActivityHandler.sleep(mAnimationDuration);
		}
	}
	
}
