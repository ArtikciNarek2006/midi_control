package com.midi_control.db.local_midi_db;

import android.content.Context;

import androidx.room.Room;

public class LocalMidiClient {
    private static LocalMidiClient mInstance;

    private LocalMidiDb appDatabase;

    private LocalMidiClient(Context mCtx) {
        appDatabase = Room.databaseBuilder(mCtx, LocalMidiDb.class, "local-midi-database")
                .allowMainThreadQueries()
                .build();
    }

    public static synchronized LocalMidiClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new LocalMidiClient(mCtx);
        }
        return mInstance;
    }

    public LocalMidiDb getAppDatabase() {
        return appDatabase;
    }
}