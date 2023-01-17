package com.example.badiefarzandiassignment2.data.async;

import android.content.Context;
import android.os.AsyncTask;

import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;

import java.util.List;

public class UserLoginAsyncTask extends AsyncTask<String, Void, List<UserWithStories>> {

    private Context context;
    private UserDao userDao;
    private UserWithStories user;
    DbResponse<List<UserWithStories>> dbResponse;

    public UserLoginAsyncTask(Context context, DbResponse<List<UserWithStories>> dbResponse) {
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
        String username = strings[0];
        String password = strings[1];
        return userDao.login(username, password);
    }

    @Override
    protected void onPostExecute(List<UserWithStories> users) {
        super.onPostExecute(users);
        if(users.size() > 0) {
            dbResponse.onSuccess(users);
        } else {
            Error error = new Error("User not found with this credentials!!!");
            dbResponse.onError(error);
        }
    }
}
