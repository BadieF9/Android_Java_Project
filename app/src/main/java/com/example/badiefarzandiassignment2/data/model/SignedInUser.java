package com.example.badiefarzandiassignment2.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class SignedInUser {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;
    private String userId;
    private String sessionToken;

    public SignedInUser(Long id, String userId, String sessionToken) {
        this.id = id;
        this.userId = userId;
        this.sessionToken = sessionToken;
    }

    @Ignore
    public SignedInUser(String userId, String sessionToken) {
        this.userId = userId;
        this.sessionToken = sessionToken;
    }


    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
