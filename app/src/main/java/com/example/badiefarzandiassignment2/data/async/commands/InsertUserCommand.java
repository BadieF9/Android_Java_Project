package com.example.badiefarzandiassignment2.data.async.commands;

import android.content.Context;

import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.User;

public class InsertUserCommand implements DbCommand<User> {
    private final UserDao userDao;
    private final User user;

    public InsertUserCommand(Context context, User user) {
        DbManager dbManager = DbManager.getInstance(context);
        this.user = user;
        this.userDao = dbManager.userDao();
    }

    @Override
    public DbResult<User> execute() {
        DbResult<User> dbResult = new DbResult<>();

        Long userId = userDao.insert(user);

        if(userId > 0) {
            user.setId(userId.toString());
            dbResult.setResult(user);
        } else {
            dbResult.setError(new Error("Inserting user failed!!!"));
        }
        return dbResult;
    }
}
