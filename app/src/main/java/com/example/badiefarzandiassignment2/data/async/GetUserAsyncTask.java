package com.example.badiefarzandiassignment2.data.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.db.dao.StoryDao;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;
import com.example.badiefarzandiassignment2.utils.UserAction;

import java.util.List;

public class GetUserAsyncTask extends AsyncTask<String, Void, UserWithStories> {

    private Context context;
    private UserDao userDao;
    private Story story;
    DbResponse<UserWithStories> dbResponse;
    String userAction;

    public GetUserAsyncTask(Context context, DbResponse<UserWithStories> dbResponse) {
        this.context = context;
        this.dbResponse = dbResponse;
        this.userAction = userAction;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DbManager dbManager = DbManager.getInstance(context);
        userDao = dbManager.userDao();
    }

    @Override
    protected UserWithStories doInBackground(String... strings) {
        return userDao.getUserById(strings[0]);
    }

    @Override
    protected void onPostExecute(UserWithStories userWithStories) {
        super.onPostExecute(userWithStories);
        if(userWithStories != null) {
            dbResponse.onSuccess(userWithStories);
        } else {
            Error error = new Error("Getting user failed!!!");
            dbResponse.onError(error);
        }
    }
}
