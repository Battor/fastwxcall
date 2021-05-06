package com.battor.fastwxcall;

import android.content.Context;
import android.content.SharedPreferences;

public class AccessibilityServiceFlagUtil {

    public static final String IS_RUNNING_KEY = "isRunning";
    public static final String NAME_KEY = "name";
    public static final String DATA_FILE_NAME = "data";

    public static void setTargetContactName(Context context, String name){
        SharedPreferences.Editor editor =
                context.getSharedPreferences(DATA_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(NAME_KEY, name);
        editor.apply();
    }

    public static void setAccessibilityIsRunning(Context context, boolean isRunning){
        SharedPreferences.Editor editor =
                context.getSharedPreferences(DATA_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(IS_RUNNING_KEY, isRunning);
        editor.apply();
    }

    public static boolean getAccessibilityIsRunning(Context context){
        return context.getSharedPreferences(DATA_FILE_NAME, Context.MODE_PRIVATE)
                        .getBoolean(IS_RUNNING_KEY, false);
    }

    public static void setAccessibilityIsRunningByNameExisting(Context context){
        SharedPreferences sp = context.getSharedPreferences(DATA_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(IS_RUNNING_KEY ,!"".equals(sp.getString(NAME_KEY, "")));
        spe.apply();
    }

    public static String getTargetContactName(Context context){
        SharedPreferences sp = context.getSharedPreferences(DATA_FILE_NAME, Context.MODE_PRIVATE);
        String name = sp.getString(NAME_KEY, "");
        if(sp.getBoolean(IS_RUNNING_KEY, false)
            && !"".equals(name)){
            return name;
        }else{
            return null;
        }
    }
}
