package com.example.badiefarzandiassignment2.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UserWithStories {
    @Embedded public User user;
    @Relation(
            parentColumn = "id",
            entityColumn = "userId"
    )
    public List<Story> stories;
}
