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

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import static android.R.attr.type;

public class UtilsResources {

    public static String getClassName(Context context, String name) {
        final String className = context.getString(getResourceIdByName(context, "string", name));
        final String packageName = context.getString(getResourceIdByName(context, "string", "package_name"));
        return (packageName + "." + className);
    }

    public static int getResourceIdByName(Context context, String type, String name) {
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }
/*
    public static int getResourceIdByName(String packageName, String className, String name) {
        Class r = null;
        int id = 0;
        try {
            r = Class.forName(packageName + ".R");

            Class[] classes = r.getClasses();
            Class desireClass = null;

            for (int i = 0; i < classes.length; i++) {
                if (classes[i].getName().split("\\$")[1].equals(className)) {
                    desireClass = classes[i];
                    break;
                }
            }

            if (desireClass != null) {
                id = desireClass.getField(name).getInt(desireClass);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return id;
    }
*/

    public static final int[] getResourceDeclareStyleableIntArray(Context context, String name) {
// !!!! TODO: FIX TO AVOID FLAVORS PROBLEMS !!!!
// => Should get the package name from the context, not from strings resources
// (but then have problems with the different flavors: getting "com.replica.game.phone.debug" instead of "com.replica.game")
        final String packageName = context.getString(getResourceIdByName(context, "string", "package_name"));
        try {
            //use reflection to access the resource class
            Field[] fields2 = Class.forName(packageName + ".R$styleable").getFields();

            //browse all fields
            for (Field f : fields2) {
                //pick matching field
                if (f.getName().equals(name)) {
                    //return as int array
                    int[] ret = (int[])f.get(null);
                    return ret;
                }
            }
        } catch (Throwable t) {
            DebugLog.e("UtilsResources", "getResourceDeclareStyleableIntArray exception! " + t);
        }

        return null;
    }

    public static final void setAnimatableImageResource(final ImageView view, final int resource) {
        view.setImageResource(resource);
        if (view.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) view.getDrawable()).start();
        }
    }

    public static final void startIfAnimatable(final View view) {
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView)view;
            if (imageView.getDrawable() instanceof AnimationDrawable) {
                ((AnimationDrawable) imageView.getDrawable()).start();
            }
        }
    }

}
