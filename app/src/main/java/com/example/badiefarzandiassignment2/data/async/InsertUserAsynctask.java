package com.example.badiefarzandiassignment2.data.async;

import android.content.Context;
import android.os.AsyncTask;

import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.User;

public class InsertUserAsynctask extends AsyncTask<String, Void, Long> {

    private Context context;
    private UserDao userDao;
    private User user;
    DbResponse<User> dbResponse;

    public InsertUserAsynctask(Context context, DbResponse<User> dbResponse) {
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
    protected Long doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];
        String firstName = strings[2];
        String lastName = strings[3];
        String gender = strings[4];
        String sendingType = strings[5];

        user = new User(username, password, firstName, lastName, gender, sendingType);
        return userDao.insert(user);
    }

    @Override
    protected void onPostExecute(Long userId) {
        super.onPostExecute(userId);
        if(userId > 0) {
            user.setId(userId);
            dbResponse.onSuccess(user);
        } else {
            Error error = new Error("Inserting user failed!!!");
            dbResponse.onError(error);
        }
    }
}
