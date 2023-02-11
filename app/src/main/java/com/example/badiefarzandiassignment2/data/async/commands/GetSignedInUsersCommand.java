package com.example.badiefarzandiassignment2.data.async.commands;

import android.content.Context;

import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.db.DbManager;
import com.example.badiefarzandiassignment2.data.db.dao.SignedInUserDao;
import com.example.badiefarzandiassignment2.data.model.SignedInUser;

import java.util.List;

public class GetSignedInUsersCommand implements DbCommand<List<SignedInUser>>{

    private final SignedInUserDao signedInUserDao;

    public GetSignedInUsersCommand(Context context) {
        DbManager dbManager = DbManager.getInstance(context);
        this.signedInUserDao = dbManager.signedInUserDao();
    }

    @Override
    public DbResult<List<SignedInUser>> execute() {
        DbResult<List<SignedInUser>> dbResult = new DbResult<>();

        List<SignedInUser> signedInUsers = signedInUserDao.getAll();

        if(signedInUsers.toArray().length > 0) {
            dbResult.setResult(signedInUsers);
        } else {
            Error error = new Error("No user is logged in!");
            dbResult.setError(error);
        }
        return dbResult;
    }
}
