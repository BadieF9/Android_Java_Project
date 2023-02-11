package com.example.badiefarzandiassignment2.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.User;

import java.util.List;

@Dao
public interface StoryDao {
    @Insert
    Long insert(Story story);

    @Update
    int update(Story story);

    @Query("SELECT * FROM story")
    List<Story> getAll();

    @Query("Delete FROM story WHERE id=:id")
    int delete(String id);
}
