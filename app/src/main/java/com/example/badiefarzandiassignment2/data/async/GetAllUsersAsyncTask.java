package com.example.badiefarzandiassignment2.data.async;

import android.content.Context;
import android.os.AsyncTask;

import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;

import java.util.List;

public class GetAllUsersAsyncTask extends AsyncTask<String, Void, List<UserWithStories>> {

    private Context context;
    private UserDao userDao;
    private Story story;
    DbResponse<List<UserWithStories>> dbResponse;

    public GetAllUsersAsyncTask(Context context, DbResponse<List<UserWithStories>> dbResponse) {
        this.context = context;
        this.dbResponse = dbResponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DbManager dbManager = DbManager.getInstance(context);
        userDao = dbManager.userDao();
    }

    @Override
    protected List<UserWithStories> doInBackground(String... strings) {
        return userDao.getAll();
    }

    @Override
    protected void onPostExecute(List<UserWithStories> usersWithStories) {
        super.onPostExecute(usersWithStories);
        if(usersWithStories != null) {
            dbResponse.onSuccess(usersWithStories);
        } else {
            Error error = new Error("Getting users failed!!!");
            dbResponse.onError(error);
        }
    }
}
