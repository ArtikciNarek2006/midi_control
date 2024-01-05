package com.midi_control.settings_db;

import android.content.Context;

import androidx.room.Room;

public class DbClient {
    private static DbClient mInstance;

    private SettingsDatabase appDatabase;

    private DbClient(Context mCtx) {
        appDatabase = Room.databaseBuilder(mCtx, SettingsDatabase.class, "midi-control-settings")
                .allowMainThreadQueries()
                .build();
    }

    public static synchronized DbClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DbClient(mCtx);
        }
        return mInstance;
    }

    public SettingsDatabase getAppDatabase() {
        return appDatabase;
    }
}