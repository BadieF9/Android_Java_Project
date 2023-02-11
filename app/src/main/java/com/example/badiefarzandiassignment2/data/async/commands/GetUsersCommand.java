package com.example.badiefarzandiassignment2.data.async.commands;

import android.content.Context;

import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;

import java.util.List;

public class GetUsersCommand implements DbCommand<List<UserWithStories>> {

    private final UserDao userDao;

    public GetUsersCommand(Context context) {
        DbManager dbManager = DbManager.getInstance(context);
        this.userDao = dbManager.userDao();
    }

    @Override
    public DbResult<List<UserWithStories>> execute() {
        DbResult<List<UserWithStories>> dbResult = new DbResult<>();

        List<UserWithStories> users = userDao.getAll();

        if(users != null) {
            dbResult.setResult(users);
        } else {
            Error error = new Error("Getting users failed!!!");
            dbResult.setError(error);
        }
        return dbResult;
    }
}
