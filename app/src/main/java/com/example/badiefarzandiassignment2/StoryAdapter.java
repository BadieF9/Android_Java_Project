package com.example.badiefarzandiassignment2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.badiefarzandiassignment2.data.async.StoryCudAsyncTask;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.databinding.ActivitySingleStoryBinding;
import com.example.badiefarzandiassignment2.databinding.ItemStoryBinding;
import com.example.badiefarzandiassignment2.network.NetworkHelper;
import com.example.badiefarzandiassignment2.utils.Action;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private NetworkHelper networkHelper;
    private Context context;
    private List<Story> stories;
    private StoryAdapterCallback callback;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;
    public static final String SINGLE_STORY_DATA = "SINGLE_STORY_DATA";

    public StoryAdapter(Context context, List<Story> stories, StoryAdapterCallback callback) {
        this.networkHelper = NetworkHelper.getInstance(context);
        this.context = context;
        this.stories = stories;
        this.callback = callback;
        this.layoutInflater = LayoutInflater.from(context);
        this.sharedPreferences = context.getSharedPreferences(Constant.USER_SHARED_PREFRENCES, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryBinding binding = ItemStoryBinding.inflate(layoutInflater, parent, false);
        return new StoryAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void update(List<Story> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    public void remove(int removePosition) {
        Story story = stories.get(removePosition);
        AppData appData = (AppData) context.getApplicationContext();
        networkHelper.deleteStory(story, appData.getCurrentUser(), sharedPreferences.getString(Constant.USER_SESSION_TOKEN, ""), new DbResponse<String>() {
            @Override
            public void onSuccess(String s) {
                stories.remove(removePosition);
                notifyItemRemoved(removePosition);
                notifyItemRangeChanged(removePosition, getItemCount());
                Toast.makeText(context, "Story removed successfully!", Toast.LENGTH_SHORT).show();

                networkHelper.getStories(sharedPreferences.getString(Constant.USER_SESSION_TOKEN, ""), new DbResponse<List<Story>>() {
                    @Override
                    public void onSuccess(List<Story> fetchedStories) {
                        appData.setStories(fetchedStories);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                networkHelper.getUserStories(sharedPreferences.getString(Constant.USER_SESSION_TOKEN, ""), appData.getCurrentUser(), new DbResponse<List<Story>>() {
                    @Override
                    public void onSuccess(List<Story> stories) {
                        appData.setMyStories(stories);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toggleFavorite(int storyPosition) {
        stories.get(storyPosition).setFavorite(!stories.get(storyPosition).getIsFavorite());
        Story story = stories.get(storyPosition);
        networkHelper.updateStory(story, sharedPreferences.getString(Constant.USER_SESSION_TOKEN, ""), new DbResponse<Story>() {
            @Override
            public void onSuccess(Story story) {
                Toast.makeText(context, "Story" + (stories.get(storyPosition).getIsFavorite() ? " added to " : " removed from ") + "favorites", Toast.LENGTH_SHORT).show();
                notifyItemChanged(storyPosition);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openSingleStory(int position) {
//        ActivitySingleStoryBinding binding =  ActivitySingleStoryBinding.inflate(layoutInflater);
        Story story = stories.get(position);
        Intent newIntent = new Intent(context.getApplicationContext(), SingleStory.class);
        newIntent.putExtra(SINGLE_STORY_DATA, new String[]{story.getTitle(), story.getAge(), story.getPublishedDate(), story.getReadingTime(), story.getDescription(), story.getPhotoPath()});
//        binding.singleStoryTitle.setText(story.getTitle());
//        binding.singleStoryAgeGroup.setText(story.getAge());
//        binding.singleStoryPublishedDate.setText(story.getPublishedDate());
//        binding.singleStoryEstimateTime.setText(story.getReadingTime());
//        binding.singleStoryDescription.setText(story.getDescription());
        context.startActivity(newIntent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemStoryBinding binding;
        private int position;

        public ViewHolder(@NonNull ItemStoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(int position) {
            this.position = position;
            Story story = stories.get(position);

            binding.itemStoryNameTv.setText(story.getTitle());
            binding.itemStoryDescTv.setText(story.getDescription());
            binding.itemStoryTimeTv.setText(story.getReadingTime());
            binding.itemStoryPicNameV.setText(story.getAge().equals("Early Readers") ? "E" : story.getAge().equals("Chapter Books") ? "C" : story.getAge().equals("Middle Grade Fiction") ? "M" : "Y");
            binding.mainLay.setOnClickListener(v -> callback.onStoryClicked(position));
            binding.trashcanIv.setOnClickListener(v -> callback.onTrashcanClicked(position));
            binding.favoriteIv.setOnClickListener(v -> callback.onStoryFavoriteClicked(position));
            if(story.getIsFavorite()) {
                binding.favoriteIv.setImageResource(R.drawable.favorite_red_full);
            }
        }
    }



    public interface StoryAdapterCallback {

        void onStoryClicked(int position);

        void onTrashcanClicked(int position);

        void onStoryFavoriteClicked(int position);
    }
}
