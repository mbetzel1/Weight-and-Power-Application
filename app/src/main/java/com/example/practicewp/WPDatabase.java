package com.example.practicewp;

import androidx.room.Database;
import androidx.room.RoomDatabase;



@Database(entities = {Configuration.class}, version = 3, exportSchema = false)
public abstract class WPDatabase extends RoomDatabase {
    public abstract WPDao wpDao();
}
