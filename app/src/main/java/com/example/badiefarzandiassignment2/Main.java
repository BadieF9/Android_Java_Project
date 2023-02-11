package com.example.badiefarzandiassignment2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.async.DbAsyncTask;
import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.async.GetUserAsyncTask;
import com.example.badiefarzandiassignment2.data.async.commands.DeleteSignedInUserCommand;
import com.example.badiefarzandiassignment2.data.async.commands.GetSignedInUsersCommand;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;
import com.example.badiefarzandiassignment2.databinding.ActivityMainBinding;
import com.example.badiefarzandiassignment2.databinding.ProfileDialogBinding;
import com.example.badiefarzandiassignment2.network.NetworkHelper;

import java.util.List;

public class Main extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NetworkHelper networkHelper;
    private SharedPreferences preferences;
    public static String userId;
    View allStoriesV;
    View myStoriesV;
    View favoriteStoriesV;
    View importAndExportV;
    View profileV;
    View logoutV;
    TextView mainWelcomeTv;
    private Intent intent;
    private AppData appData;

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
        setContentView(R.layout.activity_main);
        this.networkHelper = NetworkHelper.getInstance(Main.this);

        initialWidgets();
        preferences = getSharedPreferences("SignUp", Context.MODE_PRIVATE);
        intent = getIntent();
        this.appData = (AppData) getApplication();
        mainWelcomeTv.setText("Welcome " + preferences.getString(Main.userId + "_firstName", ""));
        if(Main.userId == null) Main.userId = intent.getStringExtra(MainActivity.USER_ID);
    }

    private void initialWidgets() {
        allStoriesV = findViewById(R.id.main_all_stories_v);
        myStoriesV = findViewById(R.id.main_my_stories_v);
        favoriteStoriesV = findViewById(R.id.main_favorite_stories_v);
        importAndExportV = findViewById(R.id.main_import_export_v);
        profileV = findViewById(R.id.main_profile_v);
        logoutV = findViewById(R.id.main_logout_v);
        mainWelcomeTv = findViewById(R.id.main_welcome_tv);

        allStoriesV.setOnClickListener(this::allStories);
        myStoriesV.setOnClickListener(this::myStories);
        favoriteStoriesV.setOnClickListener(this::favoriteStories);
        importAndExportV.setOnClickListener(this::importExport);
        logoutV.setOnClickListener(this::logout);
        profileV.setOnClickListener(this::profileOnClickHandler);
    }

    public void myStories(View view) {
        networkHelper.getUserStories(preferences.getString(Constant.USER_SESSION_TOKEN, ""), appData.getCurrentUser(), new DbResponse<List<Story>>() {
            @Override
            public void onSuccess(List<Story> stories) {
                AppData appData = (AppData) getApplication();
                appData.setMyStories(stories);
                Intent newintent = new Intent(Main.this, MyStories.class);
                newintent.putExtra(MainActivity.USER_ID, Main.userId);
                activityResultLauncher.launch(newintent);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(Main.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
//        GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(this, new DbResponse<UserWithStories>() {
//            @Override
//            public void onSuccess(UserWithStories userWithStories) {
//                AppData appData = (AppData) getApplication();
//                appData.setMyStories(userWithStories.stories);
//                Intent newintent = new Intent(Main.this, MyStories.class);
//                newintent.putExtra(MainActivity.USER_ID, Main.userId);
//                activityResultLauncher.launch(newintent);
//            }
//
//            @Override
//            public void onError(Error error) {
//                Toast.makeText(Main.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        getUserAsyncTask.execute(Main.userId);
    }

    public void allStories(View view) {
        Intent newintent = new Intent(Main.this, AllStories.class);
        activityResultLauncher.launch(newintent);
    }

    public void favoriteStories(View view) {
        Intent newintent = new Intent(Main.this, FavoriteStories.class);
        activityResultLauncher.launch(newintent);
    }    
    
    public void importExport(View view) {
        Intent newintent = new Intent(Main.this, ImportExport.class);
        activityResultLauncher.launch(newintent);
    }

    public void logout(View view) {
        DeleteSignedInUserCommand deleteSignedInUserCommand = new DeleteSignedInUserCommand(Main.this, appData.getSignedInUser().getId());
        DbAsyncTask<Long> dbAsyncTask = new DbAsyncTask<>(new DbResponse<DbResult<Long>>() {
            @Override
            public void onSuccess(DbResult<Long> stringDbResult) {
                Toast.makeText(Main.this, "You logged out successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(Main.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        dbAsyncTask.execute(deleteSignedInUserCommand);
        finish();
    }

    public void profileOnClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ProfileDialogBinding profileDialogBinding = ProfileDialogBinding.inflate(getLayoutInflater());
        View dialogRoot = profileDialogBinding.getRoot();

        profileDialogBinding.profileDialogEmailTv.setText(appData.getCurrentUser().getUsername());
        profileDialogBinding.profileDialogGenderTv.setText(appData.getCurrentUser().getGender());
        profileDialogBinding.profileDialogNameTv.setText(appData.getCurrentUser().getFirstName() + " " + appData.getCurrentUser().getLastName());
        profileDialogBinding.profileDialogSendToTv.setText(appData.getCurrentUser().getSendingType());
        builder.setView(dialogRoot);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}