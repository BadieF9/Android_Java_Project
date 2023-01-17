package com.example.badiefarzandiassignment2.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.badiefarzandiassignment2.data.model.User;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    int update(User user);

    @Query("Delete FROM user WHERE id=:id")
    int delete(int id);

    @Transaction
    @Query("SELECT * FROM user WHERE id=:id")
    UserWithStories getUserById(long id);

    @Transaction
    @Query("SELECT * FROM user")
    List<UserWithStories> getAll();

    @Transaction
    @Query("SELECT * FROM user WHERE username=:username AND password=:password")
    List<UserWithStories> login(String username, String password);

    @Transaction
    @Query("SELECT * FROM user WHERE username=:username")
    List<UserWithStories> userExists(String username);
}
