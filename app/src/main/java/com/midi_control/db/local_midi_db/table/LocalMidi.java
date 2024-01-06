package com.midi_control.db.local_midi_db.table;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_midi")
public class LocalMidi {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "rating")
    public String rating;

    @ColumnInfo(name = "creation_date")
    public String creation_date;

    @ColumnInfo(name = "last_play_date")
    public String last_play_date;

    @ColumnInfo(name = "duration")
    public String duration;

    @ColumnInfo(name = "play_count")
    public String play_count;

    @ColumnInfo(name = "difficulty")
    public String difficulty;

    @ColumnInfo(name = "location")
    public String location;
}
