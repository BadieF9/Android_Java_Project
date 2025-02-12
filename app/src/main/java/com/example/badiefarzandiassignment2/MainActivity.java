package com.example.badiefarzandiassignment2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.async.DbAsyncTask;
import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.async.GetStoryAsyncTask;
import com.example.badiefarzandiassignment2.data.async.UserLoginAsyncTask;
import com.example.badiefarzandiassignment2.data.async.commands.GetSignedInUsersCommand;
import com.example.badiefarzandiassignment2.data.async.commands.InsertSignedInUserCommand;
import com.example.badiefarzandiassignment2.data.async.commands.UpdateUserCommand;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.SignedInUser;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.User;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;
import com.example.badiefarzandiassignment2.network.NetworkHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    public static final String USER_ID = "com.example.badiefarzandiassignment2.userId";
    private SharedPreferences preferences;
    private NetworkHelper networkHelper;
    private AppData appData;
    private Button signupBtn;
    private Button signinBtn;
    private EditText emailEdt;
    private EditText passwordEdt;

//    private SharedPreferences preferences;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK) {
//                String url = result.getData().getStringExtra(Constant.MESSAGE_EXTRA);
//                textView.setText(url);
//                textView.setVisibility(View.VISIBLE);
//                openUrlButton.setVisibility(View.VISIBLE);
            }
        }


    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        networkHelper = NetworkHelper.getInstance(getApplicationContext());
        preferences = getSharedPreferences(Constant.USER_SHARED_PREFRENCES, Context.MODE_PRIVATE);
        appData = (AppData) getApplication();
        initialWidgets();

        GetSignedInUsersCommand getSignedInUsersCommand = new GetSignedInUsersCommand(getApplicationContext());
        DbAsyncTask<List<SignedInUser>> dbAsyncTask = new DbAsyncTask<>(new DbResponse<DbResult<List<SignedInUser>>>() {
            @Override
            public void onSuccess(DbResult<List<SignedInUser>> listDbResult) {
                SignedInUser signedInUser = listDbResult.getResult().get(0);
                appData.setSignedInUser(signedInUser);
                networkHelper.getUserById(signedInUser.getUserId(), new DbResponse<User>() {
                    @Override
                    public void onSuccess(User user) {
                        networkHelper.getStories(signedInUser.getSessionToken(), new DbResponse<List<Story>>() {
                            @Override
                            public void onSuccess(List<Story> stories) {
                                appData.setCurrentUser(user);
                                appData.setStories(stories);
                                Toast.makeText(MainActivity.this, "You have been logged in automatically", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Main.class);
                                intent.putExtra(USER_ID, signedInUser.getSessionToken());
                                activityResultLauncher.launch(intent);
                            }

                            @Override
                            public void onError(Error error) {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(Error error) {}
        });
        dbAsyncTask.execute(getSignedInUsersCommand);
    }

    private void initialWidgets() {
        signupBtn = findViewById(R.id.signup_btn);
        signinBtn = findViewById(R.id.signin_btn);
        emailEdt = findViewById(R.id.email_edt);
        passwordEdt = findViewById(R.id.password_edt);

        signupBtn.setOnClickListener(this::openSignUpPage);
        signinBtn.setOnClickListener(this::logIn);
    }

    public void openSignUpPage(View view) {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        activityResultLauncher.launch(intent);
    }

    public void logIn(View view) {
        String username = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.login_invalid_inputs, Toast.LENGTH_LONG).show();
            return;
        }

        User userInfo = new User(username, password);
        networkHelper.signinUser(userInfo, new DbResponse<User>() {
            @Override
            public void onSuccess(User user) {
                networkHelper.getStories(preferences.getString(Constant.USER_SESSION_TOKEN, ""), new DbResponse<List<Story>>() {
                    @Override
                    public void onSuccess(List<Story> stories) {
                        InsertSignedInUserCommand insertSignedInUserCommand = new InsertSignedInUserCommand(MainActivity.this, new SignedInUser(user.getId(), user.getSessionToken()));
                        DbAsyncTask<SignedInUser> dbAsyncTask = new DbAsyncTask<>(new DbResponse<DbResult<SignedInUser>>() {
                            @Override
                            public void onSuccess(DbResult<SignedInUser> signedInUserDbResult) {
                                appData.setSignedInUser(signedInUserDbResult.getResult());
                                return;
                            }

                            @Override
                            public void onError(Error error) {
                                error.printStackTrace();
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        dbAsyncTask.execute(insertSignedInUserCommand);

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Constant.USER_SESSION_TOKEN, user.getSessionToken());
                        editor.putString(Constant.USER_ID, user.getId());
                        editor.apply();

                        appData.setCurrentUser(user);
                        appData.setStories(stories);

                        Intent intent = new Intent(MainActivity.this, Main.class);
                        intent.putExtra(USER_ID, preferences.getString(Constant.USER_SESSION_TOKEN, ""));
                        activityResultLauncher.launch(intent);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

//        if(preferences.contains(emailEdt.getText().toString())) {
//            if(preferences.contains(emailEdt.getText().toString() + "_password") &&
//                    passwordEdt.getText().toString().equals(preferences.getString(emailEdt.getText().toString() + "_password", ""))) {
//                Intent intent = new Intent(MainActivity.this, Main.class);
//                intent.putExtra(USER_EMAIL, emailEdt.getText().toString());
//                activityResultLauncher.launch(intent);
//            } else {
//                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        } else {
//            Toast toast = Toast.makeText(getApplicationContext(), "Email not found", Toast.LENGTH_SHORT);
//            toast.show();
//        }
    }
}