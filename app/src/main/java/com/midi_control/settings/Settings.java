package com.midi_control.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    public static final Boolean auto_clear = true;
    public static final String[][] defaults = {
            {"theme", "dark"}
    };

    public static final String myPrefName = "midi-control-settings";
    private SharedPreferences sharedPreferences;

    public String get_default(String key) {
        String default_val = null;
        for (String[] aDefault : defaults) {
            if (aDefault[0].equals(key)) {
                default_val = aDefault[1];
                break;
            }
        }
        return default_val;
    }

    public void set_param(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public String get_param(String key, String default_val) {
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getString(key, default_val);
        }
        set_param(key, default_val);
        return default_val;
    }

    public String get_param(String key) {
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getString(key, "Contains Key But returns DEFAULT");
        }
        String default_val = get_default(key);
        set_param(key, (default_val != null) ? default_val : "null");
        return default_val;
    }

    public Boolean contains_param(String key){
        return sharedPreferences.contains(key);
    }
    public void set_default_values() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(auto_clear){
            editor.clear();
            editor.apply();
            editor.commit();
        }

        for (String[] aDefault : defaults)
            editor.putString(aDefault[0], aDefault[1]);
        editor.apply();
        editor.commit();
    }

    private Settings(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences(myPrefName, Context.MODE_PRIVATE);

        if (get_param("Settings: is_first", "No").equals("No")) {
            set_default_values();
            set_param("Settings: is_first", "Yes");
        }
    }


    // single tone
    private static Settings mInstance;

    public static synchronized Settings getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new Settings(mCtx);
        }
        return mInstance;
    }
}
