package com.example.badiefarzandiassignment2.data.async.commands;

import android.content.Context;

import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.dao.SignedInUserDao;
import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
import com.example.badiefarzandiassignment2.data.model.SignedInUser;
import com.example.badiefarzandiassignment2.data.model.User;

public class InsertSignedInUserCommand implements DbCommand<SignedInUser>{
    private final SignedInUserDao signedInUserDao;
    private final SignedInUser signedInUser;

    public InsertSignedInUserCommand(Context context, SignedInUser signedInUser) {
        DbManager dbManager = DbManager.getInstance(context);
        this.signedInUser = signedInUser;
        this.signedInUserDao = dbManager.signedInUserDao();
    }

    @Override
    public DbResult<SignedInUser> execute() {
        DbResult<SignedInUser> dbResult = new DbResult<>();

        Long userId = signedInUserDao.insert(signedInUser);

        if(userId > 0) {
            dbResult.setResult(signedInUser);
        } else {
            dbResult.setError(new Error("Inserting user failed!!!"));
        }
        return dbResult;
    }
}
