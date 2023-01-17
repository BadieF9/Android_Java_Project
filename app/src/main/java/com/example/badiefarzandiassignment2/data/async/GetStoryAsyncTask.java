package com.example.badiefarzandiassignment2.data.async;

import android.content.Context;
import android.os.AsyncTask;

import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.db.dao.StoryDao;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;

import java.util.List;

public class GetStoryAsyncTask extends AsyncTask<String, Void, List<Story>> {

    private Context context;
    private StoryDao storyDao;
    private Story story;
    DbResponse<List<Story>> dbResponse;

    public GetStoryAsyncTask(Context context, DbResponse<List<Story>> dbResponse) {
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
    protected List<Story> doInBackground(String... strings) {
        return storyDao.getAll();
    }

    @Override
    protected void onPostExecute(List<Story> stories) {
        super.onPostExecute(stories);
        if(stories != null) {
            dbResponse.onSuccess(stories);
        } else {
            Error error = new Error("Getting story failed!!!");
            dbResponse.onError(error);
        }
    }
}
