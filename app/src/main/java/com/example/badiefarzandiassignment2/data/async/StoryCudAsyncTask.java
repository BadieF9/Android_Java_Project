package com.example.badiefarzandiassignment2.data.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.badiefarzandiassignment2.utils.Action;
import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.db.dao.StoryDao;
import com.example.badiefarzandiassignment2.data.model.Story;

public class StoryCudAsyncTask extends AsyncTask<String, Void, Long> {

    private Context context;
    private StoryDao storyDao;
    private Story story;
    DbResponse<Story> dbResponse;
    private String action;

    public StoryCudAsyncTask(Context context, String action, DbResponse<Story> dbResponse) {
        this.context = context;
        this.dbResponse = dbResponse;
        this.action = action;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DbManager dbManager = DbManager.getInstance(context);
        storyDao = dbManager.storyDao();
    }

    @Override
    protected Long doInBackground(String... strings) {
        switch (action) {
            case Action.INSERT_ACTION:
                return insertDoInBackground(strings);
            case Action.UPDATE_ACTION:
                return updateDoInBackground(strings);
            case Action.DELETE_ACTION:
                return deleteDoInBackground(strings);
        }
        return null;
    }

    public Long insertDoInBackground(String... strings) {
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
    public Long updateDoInBackground(String... strings) {
        String id = strings[0];
        String title = strings[1];
        String description = strings[2];
        String age = strings[3];
        String userId = strings[4];
        String publishedDate = strings[5];
        String photoPath = strings[6];
        String isFavorite = strings[7];
        story = new Story(Long.parseLong(id), title, description, age, Long.parseLong(userId), publishedDate, photoPath, Boolean.parseBoolean(isFavorite));
        long mamad = storyDao.update(story);
        Log.i("mamad", "storyDao.delete: " + mamad);
        return new Long(mamad);
    }

    public Long deleteDoInBackground(String... strings) {
        String id = strings[0];
        String title = strings[1];
        String description = strings[2];
        String age = strings[3];
        String userId = strings[4];
        String publishedDate = strings[5];
        String photoPath = strings[6];
        String isFavorite = strings[7];
        story = new Story(Long.parseLong(id), title, description, age, Long.parseLong(userId), publishedDate, photoPath, Boolean.parseBoolean(isFavorite));
        return (long) storyDao.delete(Long.parseLong(id));
    }

    @Override
    protected void onPostExecute(Long response) {
        super.onPostExecute(response);
        switch (action) {
            case Action.INSERT_ACTION:
                insertOnPostExecute(response);
                break;
            case Action.UPDATE_ACTION:
                updateOnPostExecute(response);
                break;
            case Action.DELETE_ACTION:
                deleteOnPostExecute(response);
                break;
        }
    }

    private void insertOnPostExecute(Long storyId) {
        if(storyId > 0) {
            story.setId(storyId);
            dbResponse.onSuccess(story);
        } else {
            Error error = new Error("Inserting story failed!!!");
            dbResponse.onError(error);
        }
    }

    private void updateOnPostExecute(Long affectedRows) {
        Log.i("mamad", "Hello world" + affectedRows);
        if(affectedRows > 0) {
            dbResponse.onSuccess(story);
        } else {
            Error error = new Error("updating story failed!!!");
            dbResponse.onError(error);
        }
    }

    private void deleteOnPostExecute(Long response) {
        if(response > 0) {
            dbResponse.onSuccess(story);
        } else {
            Error error = new Error("Deleting story failed!!!");
            dbResponse.onError(error);
        }
    }
}
