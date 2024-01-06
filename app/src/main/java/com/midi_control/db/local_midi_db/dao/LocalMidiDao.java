package com.midi_control.db.local_midi_db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.midi_control.db.local_midi_db.table.LocalMidi;
import com.midi_control.db.settings_db.table.GlobalSettings;

import java.util.List;

@Dao
public interface LocalMidiDao {
    @Query("SELECT * FROM local_midi")
    List<LocalMidi> getAll();

    @Query("SELECT * FROM local_midi WHERE uid IN (:userIds)")
    List<LocalMidi> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM local_midi WHERE title LIKE :title LIMIT 1")
    LocalMidi findByTitle(String title);

    @Insert
    void insertAll(LocalMidi... users);

    @Delete
    void delete(LocalMidi user);
}
