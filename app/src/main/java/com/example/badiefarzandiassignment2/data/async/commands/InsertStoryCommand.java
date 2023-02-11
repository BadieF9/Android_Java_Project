package com.example.badiefarzandiassignment2.data.async.commands;

import android.content.Context;

import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.dao.StoryDao;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.User;

public class InsertStoryCommand implements DbCommand<Story>{
    private final StoryDao storyDao;
    private final Story story;

    public InsertStoryCommand(Context context, Story story) {
        DbManager dbManager = DbManager.getInstance(context);
        this.story = story;
        this.storyDao = dbManager.storyDao();
    }

    @Override
    public DbResult<Story> execute() {
        DbResult<Story> dbResult = new DbResult<>();

        Long userId = storyDao.insert(story);

        if(userId > 0) {
            story.setId(userId.toString());
            dbResult.setResult(story);
        } else {
            dbResult.setError(new Error("Inserting user failed!!!"));
        }
        return dbResult;
    }
}
