package com.midi_control.db.settings_db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.midi_control.db.settings_db.table.GlobalSettings;

import java.util.List;

@Dao
public interface GlobalSettingsDao {
    @Query("SELECT * FROM global_settings")
    List<GlobalSettings> getAll();

    @Query("SELECT * FROM global_settings WHERE uid IN (:userIds)")
    List<GlobalSettings> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM global_settings WHERE param LIKE :param LIMIT 1")
    GlobalSettings findByParam(String param);

    @Insert
    void insertAll(GlobalSettings... users);

    @Delete
    void delete(GlobalSettings user);
}
