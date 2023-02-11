package com.example.badiefarzandiassignment2.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.badiefarzandiassignment2.data.db.dao.SignedInUserDao;
import com.example.badiefarzandiassignment2.data.db.dao.StoryDao;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.SignedInUser;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.User;

@Database(entities = {Story.class, User.class, SignedInUser.class}, version = 1)
public abstract class DbManager extends RoomDatabase {

    private static final String DB_NAME = "Stories";
    private static DbManager dbManager;

    public abstract UserDao userDao();
    public abstract StoryDao storyDao();
    public abstract SignedInUserDao signedInUserDao();
    public static DbManager getInstance(Context context) {
        if(dbManager == null) {
            dbManager = Room.databaseBuilder(context, DbManager.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return dbManager;
    }
}
