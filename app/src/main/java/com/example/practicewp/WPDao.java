package com.example.practicewp;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

//look up a room tutorial this is getting outside of your wheelhouse
@Dao
public interface WPDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void saveConfig(Configuration config);

    @Query("SELECT * FROM saved WHERE configName = :input")
    public Configuration loadSelectedConfig(String input);

    @Query("SELECT configName FROM saved")
    public List<String> getSavedConfigs();

    @Query("DELETE FROM saved WHERE configName = :del")
    public void deleteConfig(String del);

}
