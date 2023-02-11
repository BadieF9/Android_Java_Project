package com.example.badiefarzandiassignment2.data.async;

import android.os.AsyncTask;

import com.example.badiefarzandiassignment2.data.async.commands.DbCommand;
import com.example.badiefarzandiassignment2.data.db.DbResponse;

public class DbAsyncTask<T> extends AsyncTask<DbCommand<T>, Void, DbResult<T>> {

    private final DbResponse<DbResult<T>> dbResponse;

    public DbAsyncTask(DbResponse<DbResult<T>> dbResponse) {
        this.dbResponse = dbResponse;
    }

    @Override
    protected DbResult<T> doInBackground(DbCommand<T>... dbCommands) {
        return dbCommands[0].execute();
    }

    @Override
    protected void onPostExecute(DbResult<T> dbResult) {
        super.onPostExecute(dbResult);
        if(dbResult.getError() == null) {
            dbResponse.onSuccess(dbResult);
        } else {
            dbResponse.onError(dbResult.getError());
        }

    }
}
