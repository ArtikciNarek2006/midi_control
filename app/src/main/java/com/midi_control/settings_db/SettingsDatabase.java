package com.midi_control.settings_db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.midi_control.settings_db.dao.GlobalSettingsDao;
import com.midi_control.settings_db.table.GlobalSettings;

@Database(entities = {GlobalSettings.class}, version = 1)
public abstract class SettingsDatabase extends RoomDatabase {
    public abstract GlobalSettingsDao globalSettingsDao();
}