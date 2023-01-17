package com.example.badiefarzandiassignment2.data.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.db.dao.StoryDao;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.User;

public class InsertStoryAsynctask extends AsyncTask<String, Void, Long> {

    private Context context;
    private StoryDao storyDao;
    private Story story;
    DbResponse<Story> dbResponse;

    public InsertStoryAsynctask(Context context, DbResponse<Story> dbResponse) {
        this.context = context;
        this.dbResponse = dbResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DbManager dbManager = DbManager.getInstance(context);
        storyDao = dbManager.storyDao();
    }

    @Override
    protected Long doInBackground(String... strings) {
        String title = strings[0];
        String description = strings[1];
        String age = strings[2];
        String userId = strings[3];
        String publishedDate = strings[4];
        String photoPath = strings[5];
        String isFavorite = strings[6];
        story = new Story(title, description, age, Long.parseLong(userId), publishedDate, photoPath, Boolean.parseBoolean(isFavorite));
        return storyDao.insert(story);
    }

    @Override
    protected void onPostExecute(Long storyId) {
        super.onPostExecute(storyId);
        if(storyId > 0) {
            story.setId(storyId);
            dbResponse.onSuccess(story);
        } else {
            Error error = new Error("Inserting story failed!!!");
            dbResponse.onError(error);
        }
    }
}
