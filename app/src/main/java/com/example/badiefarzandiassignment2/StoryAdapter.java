package com.example.badiefarzandiassignment2;

import android.content.Context;
import android.content.Intent;
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
import com.example.badiefarzandiassignment2.utils.Action;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Context context;
    private List<Story> stories;
    private StoryAdapterCallback callback;
    private LayoutInflater layoutInflater;
    public static final String SINGLE_STORY_DATA = "SINGLE_STORY_DATA";

    public StoryAdapter(Context context, List<Story> stories, StoryAdapterCallback callback) {
        this.context = context;
        this.stories = stories;
        this.callback = callback;
        this.layoutInflater = LayoutInflater.from(context);
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
        StoryCudAsyncTask storyCudAsyncTask = new StoryCudAsyncTask(context, Action.DELETE_ACTION, new DbResponse<Story>() {
            @Override
            public void onSuccess(Story story) {
                stories.remove(removePosition);
                notifyItemRemoved(removePosition);
                notifyItemRangeChanged(removePosition, getItemCount());
                Toast.makeText(context, "Story removed successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        Story story = stories.get(removePosition);
        storyCudAsyncTask.execute(
                Long.toString(story.getId()),
                story.getTitle(),
                story.getDescription(),
                story.getAge(),
                Long.toString(story.getUserId()),
                story.getPublishedDate(),
                story.getPhotoPath(),
                Boolean.toString(story.getIsFavorite())
        );

    }

    public void toggleFavorite(int storyPosition) {
        stories.get(storyPosition).setFavorite(!stories.get(storyPosition).getIsFavorite());
        StoryCudAsyncTask storyCudAsyncTask = new StoryCudAsyncTask(context, Action.UPDATE_ACTION, new DbResponse<Story>() {
            @Override
            public void onSuccess(Story story) {
                Toast.makeText(context, "Story added to favorites", Toast.LENGTH_SHORT).show();
                notifyItemChanged(storyPosition);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        Story story = stories.get(storyPosition);
        storyCudAsyncTask.execute(
                Long.toString(story.getId()),
                story.getTitle(),
                story.getDescription(),
                story.getAge(),
                Long.toString(story.getUserId()),
                story.getPublishedDate(),
                story.getPhotoPath(),
                Boolean.toString(story.getIsFavorite())
        );
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
