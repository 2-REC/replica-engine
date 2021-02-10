

    private void readHudData( final int hudFile )
    {
        XmlResourceParser parser = ( this.getResources().getXml( hudFile ) );

        try
        {
            int eventType = parser.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT )
            {
                if ( eventType == XmlPullParser.START_TAG )
                {
                    final TextureLibrary longTermTextureLibrary = BaseObject.sSystemRegistry.longTermTextureLibrary;

                    if ( parser.getName().equals( "digits" ) )
                    {
                        HudElement.Size size = HudElement.Size.ABSOLUTE;
                        float width = 0.0f;
                        float height = 0.0f;
                        DrawableBitmap[] drawableDigits = new DrawableBitmap[10];

                        for ( int i = 0; i < parser.getAttributeCount(); i++ )
                        {
                            int index = -1;
                            int drawable = 0;

                            if ( parser.getAttributeName( i ).equals( "absolute" ) )
                            {
                                boolean absolute = parser.getAttributeBooleanValue( i, true );
                                if ( !absolute )
                                {
                                    size = HudElement.Size.RELATIVE;
                                }
                            }
                            else if ( parser.getAttributeName( i ).equals( "width" ) )
                            {
                                width = parser.getAttributeFloatValue( i, -1 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "height" ) )
                            {
                                height = parser.getAttributeFloatValue( i, -1 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_0" ) )
                            {
                                index = 0;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_1" ) )
                            {
                                index = 1;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_2" ) )
                            {
                                index = 2;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_3" ) )
                            {
                                index = 3;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_4" ) )
                            {
                                index = 4;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_5" ) )
                            {
                                index = 5;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_6" ) )
                            {
                                index = 6;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_7" ) )
                            {
                                index = 7;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_8" ) )
                            {
                                index = 8;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_9" ) )
                            {
                                index = 9;
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            if ( index != -1 )
                            {
                                final int resource = UtilsResources.getResourseIdByName( packageName, "drawable", drawable );
                                drawableDigits[ index ] = new DrawableBitmap( longTermTextureLibrary.allocateTexture( resource ), 0, 0 );
                            }
                        }
                        digits = new HudDigits();
                        digits.setSizeFactors( size, width, height );
                        digits.setDrawables( drawableDigits, false );
                    }
                    else if ( parser.getName().equals( "element" ) )
                    {
/*
      // general
      index
      type
      absolute
      x
      x_origin
      x_alignment
      y
      y_origin
      y_alignment
      width
      height
      padding
      drawable

      // HudBar
      speed_decrease
      speed_increase

      // HudIconCounter
      nb_digits
      show_zeroes

      // HudBar & HudIconCounter
      padding_inside

      // HudButtonSlider
      width_base
      height_base

      // HudButton
      drawable_pressed
      drawable_disabled

      // HudBar & HudButtonSlider
      drawable_base
*/

                        int index = -1;
                        int type = TYPE_NONE;

                        HudElement.Alignment x_origin = HudElement.Alignment.CENTER;
                        HudElement.Alignment x_alignment = HudElement.Alignment.CENTER;
                        HudElement.Alignment y_origin = HudElement.Alignment.CENTER;
                        HudElement.Alignment y_alignment = HudElement.Alignment.CENTER;

                        int drawable = 0;

                        for ( int i = 0; i < parser.getAttributeCount(); i++ )
                        {
                            if ( parser.getAttributeName( i ).equals( "index" ) )
                            {
                                index = parser.getAttributeIntValue( i, -1 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "type" ) )
                            {
                                type = parser.getAttributeIntValue( i, -1 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "absolute" ) )
                            {
                                boolean absolute = parser.getAttributeBooleanValue( i, true );
                                if ( !absolute )
                                {
                                    size = HudElement.Size.RELATIVE;
                                }
                            }
                            else if ( parser.getAttributeName( i ).equals( "x" ) )
                            {
                                x = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "x_origin" ) )
                            {
                                tmp = parser.getAttributeValue( i );
                                if ( tmp.equals( "LEFT" ) )
                                {
                                    x_origin = HudElement.Alignment.LEFT;
                                }
                                else if ( tmp.equals( "RIGHT" ) )
                                {
                                    x_origin = HudElement.Alignment.RIGHT;
                                }
                            }
                            else if ( parser.getAttributeName( i ).equals( "x_alignment" ) )
                            {
                                tmp = parser.getAttributeValue( i );
                                if ( tmp.equals( "LEFT" ) )
                                {
                                    x_alignment = HudElement.Alignment.LEFT;
                                }
                                else if ( tmp.equals( "RIGHT" ) )
                                {
                                    x_alignment = HudElement.Alignment.RIGHT;
                                }
                            }
                            else if ( parser.getAttributeName( i ).equals( "y" ) )
                            {
                                y = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "y_origin" ) )
                            {
                                tmp = parser.getAttributeValue( i );
                                if ( tmp.equals( "LEFT" ) )
                                {
                                    y_origin = HudElement.Alignment.LEFT;
                                }
                                else if ( tmp.equals( "RIGHT" ) )
                                {
                                    y_origin = HudElement.Alignment.RIGHT;
                                }
                            }
                            else if ( parser.getAttributeName( i ).equals( "y_alignment" ) )
                            {
                                tmp = parser.getAttributeValue( i );
                                if ( tmp.equals( "LEFT" ) )
                                {
                                    y_alignment = HudElement.Alignment.LEFT;
                                }
                                else if ( tmp.equals( "RIGHT" ) )
                                {
                                    y_alignment = HudElement.Alignment.RIGHT;
                                }
                            }
                            else if ( parser.getAttributeName( i ).equals( "width" ) )
                            {
                                width = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "height" ) )
                            {
                                height = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "width_base" ) )
                            {
// !!!! TODO: should check that type is "HudButtonSlider" !!!!
                                widthBase = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "height_base" ) )
                            {
// !!!! TODO: should check that type is "HudButtonSlider" !!!!
                                heightBase = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "padding" ) )
                            {
                                padding = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "padding_inside" ) )
                            {
// !!!! TODO: should check that type is "HudBar" or "HudIconCounter" !!!!
                                paddingInside = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "speed_decrease" ) )
                            {
// !!!! TODO: should check that type is "HudBar" !!!!
                                speedDecrease = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "speed_increase" ) )
                            {
// !!!! TODO: should check that type is "HudBar" !!!!
                                speedIncrease = parser.getAttributeFloatValue( i, 0.0f );
                            }
                            else if ( parser.getAttributeName( i ).equals( "nb_digits" ) )
                            {
// !!!! TODO: should check that type is "HudIconCounter" !!!!
                                nbDigits = parser.getAttributeIntValue( i, 1 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "show_zeroes" ) )
                            {
// !!!! TODO: should check that type is "HudIconCounter" !!!!
                                showZeroes = parser.getAttributeBooleanValue( i, 1 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable" ) )
                            {
                                drawable = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_pressed" ) )
                            {
// !!!! TODO: should check type ... !!!!
                                drawablePressed = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_disabled" ) )
                            {
// !!!! TODO: should check type ... !!!!
                                drawableDisabled = parser.getAttributeResourceValue( i, 0 );
                            }
                            else if ( parser.getAttributeName( i ).equals( "drawable_base" ) )
                            {
// !!!! TODO: should check type ... !!!!
                                drawableBase = parser.getAttributeResourceValue( i, 0 );
                            }
                        }

                        switch ( type )
                        {
                        case TYPE_BAR:
                            HudBar bar = new HudBar( size,
                                                     x, x_origin, x_alignment,
                                                     y, y_origin, y_alignment,
                                                     width, height,
                                                     padding, padding_inside,
                                                     speedDecrease, speedIncrease );
                            final int resource = UtilsResources.getResourseIdByName( packageName, "drawable", drawable );
                            final int resourceBase = UtilsResources.getResourseIdByName( packageName, "drawable", drawableBase );
                            bar.setDrawables( new DrawableBitmap( longTermTextureLibrary.allocateTexture( resource ), 0, 0 ),
                                              new DrawableBitmap( longTermTextureLibrary.allocateTexture( resourceBase ), 0, 0 ) );
//                            addElement( bar );
                            listHudElements.put( index, bar );
                            break;
                        case TYPE_COUNTER:
                            HudCounter counter = new HudCounter( size,
                                                                 x, x_origin, x_alignment,
                                                                 y, y_origin, y_alignment,
                                                                 padding,
                                                                 digits, nbDigits, showZeroes );
//                            addElement( counter );
                            listHudElements.put( index, counter );
                            break;
                        case TYPE_ICON_COUNTER:
                            HudIconCounter iconCounter = new HudIconCounter( size,
                                                                             x, x_origin, x_alignment,
                                                                             y, y_origin, y_alignment,
                                                                             width, height,
                                                                             padding, padding_inside,
                                                                             digits, nbDigits, showZeroes );
                            final int resource = UtilsResources.getResourseIdByName( packageName, "drawable", drawable );
                            iconCounter.setDrawable( new DrawableBitmap( longTermTextureLibrary.allocateTexture( resource ), 0, 0 ) );
//                            addElement( iconCounter );
                            listHudElements.put( index, iconCounter );
                            break;
                        case TYPE_BUTTON:
                            HudButton button = new HudButton( size,
                                                              x, x_origin, x_alignment,
                                                              y, y_origin, y_alignment,
                                                              width, height,
                                                              padding );
                            final int resource = UtilsResources.getResourseIdByName( packageName, "drawable", drawable );
                            final int resourceDisabled = UtilsResources.getResourseIdByName( packageName, "drawable", drawableDisabled );
                            final int resourcePressed = UtilsResources.getResourseIdByName( packageName, "drawable", drawablePressed );
                            button.setDrawables( new DrawableBitmap( longTermTextureLibrary.allocateTexture( resource ), 0, 0 ),
                                                 new DrawableBitmap( longTermTextureLibrary.allocateTexture( resourceDisabled ), 0, 0 ),
                                                 new DrawableBitmap( longTermTextureLibrary.allocateTexture( resourcePressed ), 0, 0 ) );
//                            addElement( button );
                            listHudElements.put( index, button );
                            break;
                        case TYPE_SLIDER:
                            HudButtonSlider slider = new HudButtonSlider( size,
                                                              x, x_origin, x_alignment,
                                                              y, y_origin, y_alignment,
                                                              widthBase, heightBase,
                                                              padding,
                                                              width, height );
                            final int resource = UtilsResources.getResourseIdByName( packageName, "drawable", drawable );
                            final int resourceBase = UtilsResources.getResourseIdByName( packageName, "drawable", drawableBase );
                            final int resourceDisabled = UtilsResources.getResourseIdByName( packageName, "drawable", drawableDisabled );
                            final int resourcePressed = UtilsResources.getResourseIdByName( packageName, "drawable", drawablePressed );
                            slider.setDrawables( new DrawableBitmap( longTermTextureLibrary.allocateTexture( resourceBase ), 0, 0 ),
                                                 new DrawableBitmap( longTermTextureLibrary.allocateTexture( resource ), 0, 0 ),
                                                 new DrawableBitmap( longTermTextureLibrary.allocateTexture( resourceDisabled ), 0, 0 ),
                                                 new DrawableBitmap( longTermTextureLibrary.allocateTexture( resourcePressed ), 0, 0 ) );
//                            addElement( slider );
                            listHudElements.put( index, slider );
                            break;
                        }
                    }
                    else if ( parser.getName().equals( "layout" ) )
                    {
                        for ( int i = 0; i < parser.getAttributeCount(); i++ )
                        {
                            if ( parser.getAttributeName( i ).equals( "id" ) )
                            {
                                id = parser.getAttributeIntValue( i, -1 );
                            }
                        }
                        eventType = parser.next();
                        if ( parser.getName().equals( "element" ) )
                        {
                            for ( int i = 0; i < parser.getAttributeCount(); i++ )
                            {
                                if ( parser.getAttributeName( i ).equals( "id" ) )
                                {
                                    id = parser.getAttributeIntValue( i, -1 );
                                }
                            }
                        }
WRONG!!!!
                        eventType = parser.next();
                    }
                }
                eventType = parser.next();
            }
        }
        catch( Exception e )
        {
!!
            DebugLog.e( "...", e.getStackTrace().toString() );
        }
        finally
        {
            parser.close();
        }
    }

}
