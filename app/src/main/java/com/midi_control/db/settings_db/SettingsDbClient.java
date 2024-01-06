package com.midi_control.db.settings_db;

import android.content.Context;

import androidx.room.Room;

public class SettingsDbClient {
    private static SettingsDbClient mInstance;

    private SettingsDb appDatabase;

    private SettingsDbClient(Context mCtx) {
        appDatabase = Room.databaseBuilder(mCtx, SettingsDb.class, "midi-control-settings")
                .allowMainThreadQueries()
                .build();
    }

    public static synchronized SettingsDbClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SettingsDbClient(mCtx);
        }
        return mInstance;
    }

    public SettingsDb getAppDatabase() {
        return appDatabase;
    }
}