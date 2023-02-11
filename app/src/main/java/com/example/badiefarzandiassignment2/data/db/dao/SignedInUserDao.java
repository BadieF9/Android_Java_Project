package com.example.badiefarzandiassignment2.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.badiefarzandiassignment2.data.model.SignedInUser;
import com.example.badiefarzandiassignment2.data.model.Story;

import java.util.List;

@Dao
public interface SignedInUserDao {
    @Insert
    Long insert(SignedInUser signedInUser);

    @Update
    int update(SignedInUser signedInUser);

    @Query("SELECT * FROM SignedInUser")
    List<SignedInUser> getAll();

    @Query("Delete FROM signedinuser WHERE id=:id")
    int delete(Long id);
}
