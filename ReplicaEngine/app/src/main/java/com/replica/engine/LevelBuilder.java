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

public class LevelBuilder extends BaseObject {
    
    public LevelBuilder() {
        super();
    }
    
    @Override
    public void reset() {
    }
    
    
    public GameObject buildBackground(int backgroundResource, int levelWidth, int levelHeight,
            float sizeFactorX, float sizeFactorY) {
        GameObject background = null;

        if (backgroundResource > -1) {
            TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;
            if (textureLibrary != null) {
                // Generate the scrolling background.
                background = new GameObject();

                RenderComponent backgroundRender = new RenderComponent();
                backgroundRender.setPriority(SortConstants.BACKGROUND_START);

                ContextParameters params = sSystemRegistry.contextParameters;


                final int width = (int)(params.gameWidth * sizeFactorX);
                final int height = (int)(params.gameHeight * sizeFactorY);

                ScrollerComponent scroller3 =
                        new ScrollerComponent(0.0f, 0.0f, width, height,
                            textureLibrary.allocateTexture(backgroundResource));
                scroller3.setRenderComponent(backgroundRender);

                // Scroll speeds such that the background will evenly match the beginning and end of the level.
                float scrollSpeedX = 0.0f;
                final float deltaWidth = (params.gameWidth * sizeFactorX) - params.gameWidth;
                final int main_width = levelWidth - params.gameWidth;
                if (main_width > 0) {
                    scrollSpeedX = deltaWidth / main_width;
                    if (scrollSpeedX < 0.0f)
                        scrollSpeedX = 0.0f;
                }

                float scrollSpeedY = 0.0f;
                final float deltaHeight = (params.gameHeight * sizeFactorY) - params.gameHeight;
                final int main_height = levelHeight - params.gameHeight;
                if (main_height > 0) {
                    scrollSpeedY = deltaHeight / main_height;
                    if (scrollSpeedY < 0.0f)
                        scrollSpeedY = 0.0f;
                }

                scroller3.setScrollSpeed(scrollSpeedX, scrollSpeedY);

                backgroundRender.setCameraRelative(false);

                background.add(scroller3);
                background.add(backgroundRender);
            }
        }
        return background;
    }

    /*
     buildBackground2:
      Creates the Background keeping the image aspect ratio.
     */
    public GameObject buildBackground2(int backgroundResource, int levelWidth, int levelHeight,
            float sizeFactorX, float sizeFactorY, float ratio, boolean keepWidth) {
        GameObject background = null;

       if (backgroundResource > -1) {
           TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;
           if (textureLibrary != null) {
               // Generate the scrolling background.
               background = new GameObject();

               RenderComponent backgroundRender = new RenderComponent();
               backgroundRender.setPriority(SortConstants.BACKGROUND_START);

               ContextParameters params = sSystemRegistry.contextParameters;

               int width;
               int height;
               if (keepWidth) {
                   width = (int)(params.gameWidth * sizeFactorX);
                   height = (int)((params.gameWidth * sizeFactorX) / ratio);
               } else {
            	   height = (int)(params.gameHeight * sizeFactorY);
                   width = (int)((params.gameHeight * sizeFactorY) * ratio);
               }

               ScrollerComponent scroller3 =
                       new ScrollerComponent(0.0f, 0.0f, width, height,
                           textureLibrary.allocateTexture(backgroundResource));
               scroller3.setRenderComponent(backgroundRender);

               // Scroll speeds such that the background will evenly match the beginning and end of the level.
               float scrollSpeedX = width - params.gameWidth;
               final int main_width = levelWidth - params.gameWidth;
               if (main_width > 0) {
                   scrollSpeedX /= main_width;
                   if (scrollSpeedX < 0.0f)
                       scrollSpeedX = 0.0f;
               } else {
                   scrollSpeedX = 0.0f;
               }

               float scrollSpeedY = height - params.gameHeight;
               final int main_height = levelHeight - params.gameHeight;
               if (main_height > 0) {
                   scrollSpeedY /= main_height;
                   if (scrollSpeedY < 0.0f)
                       scrollSpeedY = 0.0f;
               } else {
                   scrollSpeedY = 0.0f;
               }

               scroller3.setScrollSpeed(scrollSpeedX, scrollSpeedY);

               backgroundRender.setCameraRelative(false);

               background.add(scroller3);
               background.add(backgroundRender);
           }
       }
       return background;
   }

    public void addMainTileMapLayer(GameObject background, int width, int height, int tileWidth, int tileHeight,
            float sizeFactorX, float sizeFactorY, TiledWorld world, int tileMapIndex) {
        RenderComponent backgroundRender = new RenderComponent();
        backgroundRender.setPriority(SortConstants.MAIN);

        // Vertex Buffer Code
        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;
        TiledVertexGrid bg = new TiledVertexGrid(textureLibrary.allocateTexture(tileMapIndex),
                width, height, tileWidth, tileHeight, sizeFactorX, sizeFactorY);
        bg.setWorld(world);

        ScrollerComponent scroller = new ScrollerComponent(1.0f, 1.0f, width, height, bg);
        scroller.setRenderComponent(backgroundRender);

        background.add(scroller);
        background.add(backgroundRender);
        backgroundRender.setCameraRelative(false);
    }

    public void addTileMapLayer(GameObject background, int priority, float scrollSpeedX, float scrollSpeedY,
            int screenWidth, int screenHeight, int tileWidth, int tileHeight,
            float sizeFactorX, float sizeFactorY,
//////// speed - m
            float movingSpeedX, float movingSpeedY,
//////// speed - e
            TiledWorld world, int tileMapIndex) {
        RenderComponent backgroundRender = new RenderComponent();
        backgroundRender.setPriority(priority);

        TextureLibrary textureLibrary = sSystemRegistry.shortTermTextureLibrary;
//////// speed - b
/*
        TiledVertexGrid bg = new TiledVertexGrid(textureLibrary.allocateTexture(tileMapIndex),
            screenWidth, screenHeight, tileWidth, tileHeight, sizeFactorX, sizeFactorY);
*/
//////// speed - m
        TiledVertexGrid bg;
        if (movingSpeedX == 0.0f && movingSpeedY == 0.0f) {
            bg = new TiledVertexGrid(textureLibrary.allocateTexture(tileMapIndex),
                    screenWidth, screenHeight, tileWidth, tileHeight, sizeFactorX, sizeFactorY);
        } else {
            bg = new TiledVertexGridPatch(textureLibrary.allocateTexture(tileMapIndex),
                    screenWidth, screenHeight, tileWidth, tileHeight, sizeFactorX, sizeFactorY, movingSpeedX, movingSpeedY);
        }
//////// speed - e
        bg.setWorld(world);

        ScrollerComponent scroller = new ScrollerComponent(scrollSpeedX, scrollSpeedY, screenWidth, screenHeight, bg);
        scroller.setRenderComponent(backgroundRender);

        background.add(scroller);
        background.add(backgroundRender);
        backgroundRender.setCameraRelative(false);
    }

}
