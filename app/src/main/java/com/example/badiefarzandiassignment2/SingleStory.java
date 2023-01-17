package com.example.badiefarzandiassignment2;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.databinding.ActivityMyStoriesBinding;
import com.example.badiefarzandiassignment2.databinding.ActivitySingleStoryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SingleStory extends AppCompatActivity {

    public static List<Story> filteredStories;
    private ActivitySingleStoryBinding binding;
    private StoryAdapter storyAdapter;
    private SharedPreferences preferences;
    private Intent intent;
    private TextToSpeech textToSpeech;
    private boolean isPlaying = false;

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
    protected void onDestroy() {
        super.onDestroy();
        this.textToSpeech.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingleStoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        preferences = getSharedPreferences("SignUp", Context.MODE_PRIVATE);
        intent = getIntent();

        binding.singleStoryTitle.setText(intent.getStringArrayExtra(StoryAdapter.SINGLE_STORY_DATA)[0]);
        binding.singleStoryAgeGroup.setText(intent.getStringArrayExtra(StoryAdapter.SINGLE_STORY_DATA)[1]);
        binding.singleStoryPublishedDate.setText(intent.getStringArrayExtra(StoryAdapter.SINGLE_STORY_DATA)[2]);
        binding.singleStoryEstimateTime.setText(intent.getStringArrayExtra(StoryAdapter.SINGLE_STORY_DATA)[3]);
        binding.singleStoryDescription.setText(intent.getStringArrayExtra(StoryAdapter.SINGLE_STORY_DATA)[4]);
        if(intent.getStringArrayExtra(StoryAdapter.SINGLE_STORY_DATA)[5] != null) {
            binding.singleStoryImage.setImageURI(Uri.parse(intent.getStringArrayExtra(StoryAdapter.SINGLE_STORY_DATA)[5]));
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        binding.speakerTtsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying) {
                    onPauseSpeak();
                    isPlaying = false;
                } else {
                    textToSpeech.speak(intent.getStringArrayExtra(StoryAdapter.SINGLE_STORY_DATA)[4], TextToSpeech.QUEUE_FLUSH, null, "someUniqueId");
                    isPlaying = true;
                }
            }
        });

    }

    private void onPauseSpeak() {
        if(this.textToSpeech !=null){
            this.textToSpeech.stop();
        }
        super.onPause();
    }
}