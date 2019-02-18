package com.appbox.mep;

import android.content.Context;
import android.preference.PreferenceManager;

class QueryPreferences {
    private static final String TEXT_ID = "2";
    static void setPreferences(String text, Context context){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(TEXT_ID,text)
                .apply();
    }
    static String getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(TEXT_ID,"");
    }
}
