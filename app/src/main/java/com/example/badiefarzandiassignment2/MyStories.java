package com.example.badiefarzandiassignment2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.databinding.ActivityMyStoriesBinding;

import java.util.ArrayList;
import java.util.List;

public class MyStories extends AppCompatActivity {

    public static List<Story> filteredStories;
    private ActivityMyStoriesBinding binding;
    private StoryAdapter storyAdapter;
    private SharedPreferences preferences;
    private Intent intent;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK) {
                String storyCreated = result.getData().getStringExtra(Constant.MYSTORY_CREATE_SUCCESS);
                if(storyCreated.equals("true")) {
                    storyAdapter.notifyItemInserted(MyStories.filteredStories.size() -1);
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyStoriesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        preferences = getSharedPreferences("SignUp", Context.MODE_PRIVATE);
        intent = getIntent();
        MyStories.filteredStories = new ArrayList<>();

        AppData appData = (AppData) getApplication();
        for (Story story : appData.getMyStories()) {
            filteredStories.add(story);
        }

        storyAdapter = new StoryAdapter(this, filteredStories, new StoryAdapter.StoryAdapterCallback() {
            @Override
            public void onStoryClicked(int position) {
                storyAdapter.openSingleStory(position);
            }

            @Override
            public void onTrashcanClicked(int position) {
                storyAdapter.remove(position);
            }

            @Override
            public void onStoryFavoriteClicked(int position) { storyAdapter.toggleFavorite(position); }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(storyAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_stories_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Top Menu add icon onSelect function
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.my_stories_menu_plus_icon) {
            // User chose the "Settings" item, show the app settings UI...
            Intent newIntent = new Intent(MyStories.this, AddStory.class);
            newIntent.putExtra(MainActivity.USER_ID, intent.getStringExtra(MainActivity.USER_ID));
            activityResultLauncher.launch(newIntent);
            return true;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }
}