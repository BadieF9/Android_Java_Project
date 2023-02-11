package com.example.badiefarzandiassignment2.data.async.commands;

import android.content.Context;
import android.util.Log;

import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.dao.SignedInUserDao;

public class DeleteSignedInUserCommand implements DbCommand<Long> {

    private final SignedInUserDao signedInUserDao;
    private final Long signedInUserId;

    public DeleteSignedInUserCommand(Context context, Long signedInUserId) {
        DbManager dbManager = DbManager.getInstance(context);

        this.signedInUserId = signedInUserId;
        this.signedInUserDao = dbManager.signedInUserDao();
    }

    @Override
    public DbResult<Long> execute() {
        DbResult<Long> dbResult = new DbResult<>();

        int affectedRows = signedInUserDao.delete(signedInUserId);
        if (affectedRows > 0) {
            dbResult.setResult(signedInUserId);
        } else {
            Error error = new Error("Deleting signed in user failed!");
            dbResult.setError(error);
        }

        return dbResult;
    }
}
