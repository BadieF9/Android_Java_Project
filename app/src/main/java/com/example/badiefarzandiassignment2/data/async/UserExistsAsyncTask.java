
package com.example.badiefarzandiassignment2.data.async;

        import android.content.Context;
        import android.os.AsyncTask;
        import android.util.Log;

        import com.example.badiefarzandiassignment2.data.db.DbManager;
        import com.example.badiefarzandiassignment2.data.db.DbResponse;
        import com.example.badiefarzandiassignment2.data.db.dao.UserDao;
        import com.example.badiefarzandiassignment2.data.model.UserWithStories;

        import java.util.List;

public class UserExistsAsyncTask extends AsyncTask<String, Void, List<UserWithStories>> {

    private Context context;
    private UserDao userDao;
    private UserWithStories user;
    DbResponse<Boolean> dbResponse;

    public UserExistsAsyncTask(Context context, DbResponse<Boolean> dbResponse) {
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
        return userDao.userExists(username);
    }

    @Override
    protected void onPostExecute(List<UserWithStories> users) {
        super.onPostExecute(users);
        if(users.size() > 0) {
            Error error = new Error("User exists with this credentials!!!");
            dbResponse.onError(error);
        } else {
            dbResponse.onSuccess(true);
        }
    }
}
