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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.async.GetStoryAsyncTask;
import com.example.badiefarzandiassignment2.data.async.UserLoginAsyncTask;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    public static final String USER_ID = "com.example.badiefarzandiassignment2.userId";
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

        initialWidgets();
//        preferences = getSharedPreferences("SignUp", Context.MODE_PRIVATE);
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
        UserLoginAsyncTask userLoginAsyncTask = new UserLoginAsyncTask(this, new DbResponse<List<UserWithStories>>() {
            @Override
            public void onSuccess(List<UserWithStories> userWithStories) {
                GetStoryAsyncTask getStoryAsyncTask = new GetStoryAsyncTask(getApplicationContext(), new DbResponse<List<Story>>() {
                    @Override
                    public void onSuccess(List<Story> stories) {
                        Main.stories = stories;
                        Intent intent = new Intent(MainActivity.this, Main.class);
                        intent.putExtra(USER_ID, Long.toString(userWithStories.get(0).user.getId() ));
                        activityResultLauncher.launch(intent);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
                getStoryAsyncTask.execute();
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        userLoginAsyncTask.execute(emailEdt.getText().toString(), passwordEdt.getText().toString());

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