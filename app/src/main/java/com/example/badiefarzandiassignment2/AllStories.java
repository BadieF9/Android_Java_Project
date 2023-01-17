package com.example.badiefarzandiassignment2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.badiefarzandiassignment2.databinding.ActivityAllStoriesBinding;

public class AllStories extends AppCompatActivity {

    private ActivityAllStoriesBinding binding;
    private StoryAdapter storyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllStoriesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

        storyAdapter = new StoryAdapter(this, Main.stories, new StoryAdapter.StoryAdapterCallback() {
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
}