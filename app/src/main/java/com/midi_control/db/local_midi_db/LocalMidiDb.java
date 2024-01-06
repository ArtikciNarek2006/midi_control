package com.midi_control.db.local_midi_db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.midi_control.db.local_midi_db.dao.LocalMidiDao;
import com.midi_control.db.local_midi_db.table.LocalMidi;

@Database(entities = {LocalMidi.class}, version = 1)
public abstract class LocalMidiDb extends RoomDatabase {
    public abstract LocalMidiDao globalSettingsDao();
}