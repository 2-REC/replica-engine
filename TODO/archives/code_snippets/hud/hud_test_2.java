package derek.android.engine;

import android.content.Context;
import java.util.ArrayList;


/*
 Manager for orthographic in-game UI elements.
*/
public class HudSystem extends BaseObject
{
//protected?
    private final static int LAYOUT_DEFAULT = 0;

//////// GAME_SPECIFIC - BEGIN

// !!!! TODO: move to game (phone only?) !!!!
    private final static int LAYOUT_BUTTONS = 0;
    private final static int LAYOUT_TILT = 1;

    // must correspond to IDs in "xml/hud"
    private static final int LIFE_BAR = 0;
    private static final int LIFE_COUNTER = 1;
    private static final int COINS_COUNTER = 2;
    private static final int DPAD = 3;
    private static final int LEFT_BUTTON = 4;
    private static final int RIGHT_BUTTON = 5;
    private static final int UP_BUTTON = 6;
    private static final int DOWN_BUTTON = 7;
    private static final int JUMP_BUTTON = 8;
    private static final int ATTACK_BUTTON = 9;

//////// GAME_SPECIFIC - END

    // must correspond to types in "xml/hud"
    private static final int TYPE_BAR = 0;          // HudBar
    private static final int TYPE_BUTTON = 1;       // HudButton
    private static final int TYPE_SLIDER = 2;       // HudButtonSlider
    private static final int TYPE_ICON = 3;         // HudIcon
    private static final int TYPE_ICON_COUNTER = 4; // HudIconCounter
    private static final int TYPE_COUNTER = 5;      // HudCounter


    private class Layout
    {
        public int id;
        public ArrayList<HudElement> elements;
        public ArrayList<boolean> hiddens;

        public Layout( final int id )
        {
            this.id = id;
            elements = new ArrayList<HudElement>();
            hiddens = new ArrayList<boolean>();
        }

        public void add( final HudElement element, final boolean hidden )
        {
            elements.add( element );
            hiddens.add( hidden );
        }
    }


    final String packageName;

    private Texture fadeTexture;
    private float fadeStartTime;
    private float fadeDuration;
    private boolean fadeIn;
    private boolean fading;
    private int fadePendingEventType;
    private int fadePendingEventIndex;

//////// FPS - BEGIN
// !!!! TODO : create a separate object "HudFps" !!!!
// => using a HudDigit?
/*
    private int     fps;
    private Vector2 fpsLocation;
    private int[]   fpsDigits;
    private boolean fpsDigitsChanged;
    private boolean fpsShow;
*/
//////// FPS - END

    private HudDigits digits;

// !!!! ???? TODO : OK to use ArrayList ? ???? !!!!
// => not too slow ? ( as accessed every frame ... )
    private ArrayList<HudElement> elements;
    private ArrayList<boolean> hiddens;
    private ArrayList<Layout> layouts;


//////// GAME_SPECIFIC - BEGIN

    private HudBar mLifeBar;
    private HudIconCounter mLifeCounter;

    private HudIconCounter mCoinsCounter;
//    private HudCounter mCoinsCounter;

//////// SYSTEM_SPECIFIC - BEGIN

    private HudButtonSlider mDPad;

    private HudButton mLeftButton;
    private HudButton mRightButton;
//////// CLIMBING 20141024 - MID
    private HudButton mUpButton;
    private HudButton mDownButton;
//////// CLIMBING 20141024 - END

    private HudButton mJumpButton;
    private HudButton mAttackButton;

//////// SYSTEM_SPECIFIC - END

//////// GAME_SPECIFIC - END


    public HudSystem( Context context )
    {
        super();

        packageName = context.getPackageName();

//////// FPS - BEGIN
/*
        fpsLocation = new Vector2();
        fpsDigits = new int[ MAX_DIGITS ];
*/
//////// FPS - END


        layouts = new ArrayList<Layout>();

        readHudData();

        elements = layouts.get( LAYOUT_DEFAULT ).elements; // first layout as default
        hiddens = layouts.get( LAYOUT_DEFAULT ).hiddens;
    }


//////// GAME_SPECIFIC - BEGIN
MOVE TO GAME!!!!
    public HudSystemImpl()
    {
        super();

//////// GAME_SPECIFIC - BEGIN

        mLifeBar = ( HudBar )elements.get( LIFE_BAR );
        mLifeCounter = ( HudIconCounter )elements.get( LIFE_COUNTER );
        mCoinsCounter = HudIconCounter )elements.get( COINS_COUNTER );

//////// SYSTEM_SPECIFIC - BEGIN

        mDPad = ( HudButtonSlider )elements.get( DPAD );

        mLeftButton = ( HudButton )elements.get( LEFT_BUTTON );

        mRightButton = ( HudButton )elements.get( RIGHT_BUTTON );

//////// CLIMBING 20141024 - MID
        mUpButton = ( HudButton )elements.get( UP_BUTTON );

        mDownButton = ( HudButton )elements.get( DOWN_BUTTON );
//////// CLIMBING 20141024 - END

        mJumpButton = ( HudButton )elements.get( JUMP_BUTTON );
        mAttackButton = ( HudButton )elements.get( ATTACK_BUTTON );

//////// SYSTEM_SPECIFIC - END

//////// GAME_SPECIFIC - END

        reset();
    }


    @Override
    public void reset()
    {
        fadeTexture = null;
        fading = false;
        fadePendingEventType = GameFlowEvent.EVENT_INVALID;
        fadePendingEventIndex = 0;

//////// FPS - BEGIN
/*
        fps = 0;
        fpsDigits[0] = 0;
        fpsDigits[1] = -1;
        fpsDigitsChanged = true;
        fpsShow = false;
*/
//////// FPS - END

?        digits.reset();

        for ( int i = 0; i < elements.size(); ++i )
        {
            elements.get( i ).reset();
        }
    }

    @Override
    public void update( float timeDelta, BaseObject parent )
    {
        final ContextParameters params = sSystemRegistry.contextParameters;
        final RenderSystem render = sSystemRegistry.renderSystem;
        final DrawableFactory factory = sSystemRegistry.drawableFactory;

        final GameObjectManager manager = sSystemRegistry.gameObjectManager;

// !!!! TODO : should separate "playing" and "non playing" hud elements !!!!
// => & only draw player-specific HUD elements when there's a player.
        if ( ( manager != null ) && ( manager.getPlayer() != null ) )
        {
            for ( int i = 0; i < elements.size(); ++i )
            {
                elements.get( i ).update( timeDelta, parent );
            }
        }

//////// FPS - BEGIN
/*
        if ( fpsShow )
        {
            if ( fpsDigitsChanged )
            {
                int count = intToDigitArray( fps, fpsDigits );
                fpsDigitsChanged = false;
                fpsLocation.set( params.gameWidth - 10.0f
                                  - ( ( count + 1 ) * ( digits.getDigitWidth() / 2.0f ) ),
                                  10.0f );
            }
            drawNumber( fpsLocation, fpsDigits, false );
        }
*/
//////// FPS - END

        if ( fading && ( factory != null ) )
        {
            final float time = sSystemRegistry.timeSystem.getRealTime();
            final float fadeDelta = ( time - fadeStartTime );

            float percentComplete = 1.0f;
            if ( fadeDelta < mFadeDuration )
            {
                percentComplete = ( fadeDelta / fadeDuration );
            }
            else if ( fadeIn )
            {
                // We've faded in.  Turn fading off.
                fading = false;
            }

            if ( ( percentComplete < 1.0f ) || !fadeIn )
            {
                float opacityValue = percentComplete;
                if ( fadeIn )
                {
                    opacityValue = 1.0f - percentComplete;
                }

                DrawableBitmap bitmap = factory.allocateDrawableBitmap();
                if ( bitmap != null )
                {
                    bitmap.setWidth( params.gameWidth );
                    bitmap.setHeight( params.gameHeight );
                    bitmap.setTexture( fadeTexture );
                    bitmap.setCrop( 0,
                                    fadeTexture.height,
                                    fadeTexture.width,
                                    fadeTexture.height );
                    bitmap.setOpacity( opacityValue );
                    render.scheduleForDraw( bitmap,
                                            Vector2.ZERO,
                                            SortConstants.FADE,
                                            false );
                }
            }

            if ( ( percentComplete >= 1.0f )
                && ( fadePendingEventType != GameFlowEvent.EVENT_INVALID ) )
            {
// !!!! ???? TODO : why does it have to go via "LevelSystem" ? ???? !!!!
                LevelSystem level = sSystemRegistry.levelSystem;
                if ( level != null )
                {
                    level.sendGameEvent( fadePendingEventType,
                                         fadePendingEventIndex,
                                         false );
                    fadePendingEventType = GameFlowEvent.EVENT_INVALID;
                    fadePendingEventIndex = 0;
                }
            }
        }
    }

//////// FPS - BEGIN
/*
    public void setFPS( int newFps )
    {
        fpsDigitsChanged = ( newFps != fps );
        fps = newFps;
    }

    public void setShowFPS( boolean show )
    {
        fpsShow = show;
    }
*/
//////// FPS - END

    public void setFadeTexture( Texture texture )
    {
        fadeTexture = texture;
    }

    public void startFade( boolean in, float duration )
    {
        fadeStartTime = sSystemRegistry.timeSystem.getRealTime();
        fadeDuration = duration;
        fadeIn = in;
        fading = true;
    }

    public void clearFade()
    {
        fading = false;
    }

    public boolean isFading()
    {
        return fading;
    }

    public void sendGameEventOnFadeComplete( int eventType, int eventIndex )
    {
        fadePendingEventType = eventType;
        fadePendingEventIndex = eventIndex;
    }


// not used anymore
/*
    public void addElement( HudElement element )
    {
        mElements.add( element );
    }

    public void removeElement( HudElement element )
    {
// !!!! ???? TODO : OK ? ???? !!!!
// => or need to copy all but this element in an other list ?
        mElements.remove( element );
    }
*/

    private final void readHudData()
    {
        TextureLibrary longTermTextureLibrary = BaseObject.sSystemRegistry.longTermTextureLibrary;
        setFadeTexture( longTermTextureLibrary.allocateTexture( UtilsResources.getResourseIdByName( packageName, "drawable", "fade_color" ) ) );

        final int hudFile = UtilsResources.getResourseIdByName( packageName, "xml", "hud" );
        XmlResourceParser parser = ( this.getResources().getXml( hudFile ) );
        SparseArray<HudElement> listHudElements;
        try
        {
            int eventType = parser.getEventType();
            while ( eventType != XmlPullParser.END_DOCUMENT )
            {
                if ( eventType == XmlPullParser.START_TAG )
                {
                    if ( parser.getName().equals( "digits" ) )
                    {
                        readHudDigits( parser );
                        eventType = parser.next();
                    }
                    else if ( parser.getName().equals( "elements" ) )
                    {
                        listHudElements = new SparseArray<HudElement>();
                        eventType = readHudElements( parser.next(), listHudElements );
                    }
                    else if ( parser.getName().equals( "layouts" ) )
                    {
                        assert( listHudElements != null );
                        eventType = readHudLayouts( parser.next(), listHudElements );
                        if ( layouts.size() == 0 )
                        {
                            Layout layout = new Layout( LAYOUT_DEFAULT );
                            for ( int i = 0; i < listHudElements.size(); ++i )
                            {
                                layout.add( listHudElements.get( i ) );
                            }
                            layouts.add( layout );
                        }
                        listHudElements.clear();
                        listHudElements = null;
                    }
                }
                else
                {
                    eventType = parser.next();
                }
            }
        }
        catch( Exception e )
        {
            DebugLog.e( "HudSystem", e.getStackTrace().toString() );
        }
        finally
        {
            parser.close();
        }
    }


    private final void readHudDigits( final XmlResourceParser parser ) // throws ????
    {
        final TextureLibrary longTermTextureLibrary = BaseObject.sSystemRegistry.longTermTextureLibrary;

        HudElement.Size size = HudElement.Size.ABSOLUTE;
        float width = 0.0f;
        float height = 0.0f;
        DrawableBitmap[] drawableDigits = new DrawableBitmap[ 10 ];

        for ( int i = 0; i < parser.getAttributeCount(); i++ )
        {
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
            else
            {
                final int drawable = parser.getAttributeResourceValue( i, -1 );
                if ( drawable != -1 )
                {
                    int index = -1;
                    if ( parser.getAttributeName( i ).equals( "drawable_0" ) )
                    {
                        index = 0;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_1" ) )
                    {
                        index = 1;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_2" ) )
                    {
                        index = 2;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_3" ) )
                    {
                        index = 3;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_4" ) )
                    {
                        index = 4;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_5" ) )
                    {
                        index = 5;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_6" ) )
                    {
                        index = 6;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_7" ) )
                    {
                        index = 7;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_8" ) )
                    {
                        index = 8;
                    }
                    else if ( parser.getAttributeName( i ).equals( "drawable_9" ) )
                    {
                        index = 9;
                    }

                    if ( index != -1 )
                    {
                        final int resource = UtilsResources.getResourseIdByName( packageName, "drawable", drawable );
                        drawableDigits[ index ] = new DrawableBitmap( longTermTextureLibrary.allocateTexture( resource ), 0, 0 );
                    }
                }
            }
        }

        digits = new HudDigits();
        digits.setSizeFactors( size, width, height );
        digits.setDrawables( drawableDigits, false );
    }

    private final int readHudElements( final XmlResourceParser parser, final SparseArray<> listHudElements ) // throws ????
    {
        final TextureLibrary longTermTextureLibrary = BaseObject.sSystemRegistry.longTermTextureLibrary;

        int eventType = parser.getEventType();
        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( parser.getName().equals( "element" ) )
                {
                    int index = -1;
                    int type = TYPE_NONE;
                    HudElement.Size size = HudElement.Size.ABSOLUTE;

                    float x = 0.0f;
                    float y = 0.0f;

                    HudElement.Alignment x_origin = HudElement.Alignment.CENTER;
                    HudElement.Alignment x_alignment = HudElement.Alignment.CENTER;
                    HudElement.Alignment y_origin = HudElement.Alignment.CENTER;
                    HudElement.Alignment y_alignment = HudElement.Alignment.CENTER;

                    float width = 0.0f;
                    float height = 0.0f;
                    float widthBase = 0.0f;
                    float heightBase = 0.0f;

                    float padding = 0.0f;
                    float paddingInside = 0.0f;
                    float speedDecrease = 0.0f;
                    float speedIncrease = 0.0f;
                    int nbDigits = 1;
                    boolean showZeroes = false;

                    int drawable = 0;
                    int drawableBase = 0;
                    int drawableDisabled = 0;
                    int drawablePressed = 0;

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
                            showZeroes = parser.getAttributeBooleanValue( i, false );
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
                        listHudElements.put( index, bar );
                        break;

                    case TYPE_COUNTER:
                        HudCounter counter = new HudCounter( size,
                                                             x, x_origin, x_alignment,
                                                             y, y_origin, y_alignment,
                                                             padding,
                                                             digits, nbDigits, showZeroes );
                        listHudElements.put( index, counter );
                        break;

                    case TYPE_ICON:
                        HudIcon icon = new HudIcon( size,
                                                    x, x_origin, x_alignment,
                                                    y, y_origin, y_alignment,
                                                    width, height,
                                                    padding );
                        final int resource = UtilsResources.getResourseIdByName( packageName, "drawable", drawable );
                        icon.setDrawable( new DrawableBitmap( longTermTextureLibrary.allocateTexture( resource ), 0, 0 ) );
                        listHudElements.put( index, icon );
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
                        listHudElements.put( index, slider );
                        break;
                    }
                }
                else
                {
                    // done with "elements" => leave
                    return eventType;
                }
            }
            eventType = parser.next();
        }
        return eventType; // == XmlPullParser.END_DOCUMENT
    }

    private final int readHudLayouts( final XmlResourceParser parser,
                                      final SparseArray<> listHudElements ) // throws ????
    {
        final TextureLibrary longTermTextureLibrary = BaseObject.sSystemRegistry.longTermTextureLibrary;

        Layout layout;

        int eventType = parser.getEventType();
        while ( eventType != XmlPullParser.END_DOCUMENT )
        {
            if ( eventType == XmlPullParser.START_TAG )
            {
                if ( parser.getName().equals( "layout" ) )
                {
                    for ( int i = 0; i < parser.getAttributeCount(); i++ )
                    {
                        if ( parser.getAttributeName( i ).equals( "id" ) )
                        {
                            int id = parser.getAttributeIntValue( i, -1 );
                            layout = new Layout( id );
                            layouts.add( layout );
                            break;
                        }
                    }
                }
                else if ( parser.getName().equals( "element" ) )
                {
                    int index = -1;
                    boolean hidden = false;
                    for ( int i = 0; i < parser.getAttributeCount(); i++ )
                    {
                        if ( parser.getAttributeName( i ).equals( "id" ) )
                        {
                            index = parser.getAttributeIntValue( i, -1 );
                        }
                        else if ( parser.getAttributeName( i ).equals( "hidden" ) )
                        {
                            hidden = parser.getAttributeBooleanValue( i, false );
                        }
                    }
                    if ( index != -1 )
                    {
// !!!! TODO: need to check index !!!!
                        HudElement element = listHudElements.get( index );
                        layout.add( element, hidden );
                    }
                }
                else
                {
                    return eventType;
                }
            }
            eventType = parser.next();
        }
        return eventType; // == XmlPullParser.END_DOCUMENT
    }

    public void switchLayout( final int layout )
    {
        for ( HudElement element : elements )
        {
            element.setVisible( false );
        }

        elements = layouts.get( layout ).elements;
        hiddens = layouts.get( layout ).hiddens;

        for ( int i=0; i<elements.size(); ++i )
        {
            final HudElement element = elements.get( i );
            if ( !hiddens.get( i ) )
            {
                element.setVisible( true );
            }
        }
    }

//////// GAME_SPECIFIC - BEGIN
MOVE TO GAME!!!!
    public void setLifePercent( float percent )
    {
        mLifeBar.setPercent( percent );
    }

/*
    public HudBar getLife()
    {
        return mLifeBar;
    }
*/
    public void setLifeCounter( int value )
    {
        mLifeCounter.updateCounter( value );
    }

    public HudIconCounter getLifeCounter()
    {
        return mLifeCounter;
    }

    public HudIconCounter getCoinsCounter()
//    public HudCounter getCoinsCounter()
    {
        return mCoinsCounter;
    }

//////// SYSTEM_SPECIFIC - BEGIN

    public HudButtonSlider getDPad()
    {
        return mDPad;
    }

    public HudButton getLeftButton()
    {
        return mLeftButton;
    }
    public HudButton getRightButton()
    {
        return mRightButton;
    }
//////// CLIMBING 20141024 - MID
    public HudButton getUpButton()
    {
        return mUpButton;
    }
    public HudButton getDownButton()
    {
        return mDownButton;
    }
//////// CLIMBING 20141024 - END

    public HudButton getJumpButton()
    {
        return mJumpButton1;
    }

    public HudButton getAttackButton()
    {
        return mAttackButton1;
    }

//////// SYSTEM_SPECIFIC - END

//////// GAME_SPECIFIC - END

}
