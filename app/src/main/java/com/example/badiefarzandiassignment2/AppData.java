package com.example.badiefarzandiassignment2;

import android.app.Application;

import com.example.badiefarzandiassignment2.data.model.SignedInUser;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.User;

import java.util.List;

public class AppData extends Application {

    private static final String TAG = "AppData";
    private User currentUser = null;
    private SignedInUser signedInUser = null;
    private List<Story> stories = null;
    private List<Story> myStories = null;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public SignedInUser getSignedInUser() { return signedInUser; }

    public void setSignedInUser(SignedInUser signedInUser) { this.signedInUser = signedInUser; }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public List<Story> getMyStories() {
        return myStories;
    }

    public void setMyStories(List<Story> myStories) {
        this.myStories = myStories;
    }
}
