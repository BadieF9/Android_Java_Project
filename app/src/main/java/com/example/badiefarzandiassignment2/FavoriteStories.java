package com.example.badiefarzandiassignment2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.databinding.ActivityFavoriteStoriesBinding;

import java.util.ArrayList;
import java.util.List;

public class FavoriteStories extends AppCompatActivity {

    private ActivityFavoriteStoriesBinding binding;
    private StoryAdapter storyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteStoriesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        List<Story> filteredStories = new ArrayList<>();
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

        for (Story story : Main.stories) {
            if (story.getIsFavorite()) {
                filteredStories.add(story);
            }
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
            public void onStoryFavoriteClicked(int position) {
                storyAdapter.toggleFavorite(position);
                storyAdapter.remove(position);
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(storyAdapter);
    }
}