package com.example.badiefarzandiassignment2.data.async.commands;

import android.content.Context;

import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.User;

public class UpdateUserCommand implements  DbCommand<User>{
    private final UserDao userDao;
    private final User user;

    public UpdateUserCommand(Context context, User user) {
        DbManager dbManager = DbManager.getInstance(context);

        this.user = user;
        this.userDao = dbManager.userDao();
    }

    @Override
    public DbResult<User> execute() {
        DbResult<User> dbResult = new DbResult<>();

        int affectedRows = userDao.update(user);

        if(affectedRows > 0) {
            dbResult.setResult(user);
        } else {
            Error error = new Error("Updating user failed");
            dbResult.setError(error);
        }
        return dbResult;
    }
}
