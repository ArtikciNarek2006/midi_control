package com.midi_control.settings_db.table;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "global_settings")
public class GlobalSettings {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "param")
    public String param;

    @ColumnInfo(name = "value")
    public String value;
}
