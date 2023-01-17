package com.example.badiefarzandiassignment2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.async.InsertUserAsynctask;
import com.example.badiefarzandiassignment2.data.async.StoryCudAsyncTask;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.User;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;
import com.example.badiefarzandiassignment2.databinding.ActivityImportExportBinding;
import com.example.badiefarzandiassignment2.utils.Action;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ImportExport extends AppCompatActivity {

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() != Activity.RESULT_OK) {
                Toast.makeText(ImportExport.this, "File path not found!", Toast.LENGTH_LONG).show();
                return;
            }

            fileUri = (result.getData() != null) ? result.getData().getData() : null;
            if(fileUri == null) {
                Toast.makeText(ImportExport.this, "File's path not defined!", Toast.LENGTH_SHORT).show();
            }

            if(isImport) {
                checkReadPermission();
            } else {
                checkWritePermission();
            }
        }
    });

    private ActivityImportExportBinding binding;
    private static final String TAG = "ReadyActivity";
    private static final int READ_PERMISSION_REQUEST_CODE = 1;
    private static final int READ_FILE_PICKER_REQUEST_CODE = 1;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 1;
    private static final int WRITE_FILE_PICKER_REQUEST_CODE = 1;
    private static boolean isImport = true;
    FileAccessHelper fileAccessHelper = null;
    private Uri fileUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImportExportBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        fileAccessHelper = FileAccessHelper.getInstance(getApplicationContext());
        binding.importBtn.setOnClickListener((v) -> {
            openReadFilePicker();
            isImport = true;
        });
        binding.exportBtn.setOnClickListener((v) -> {
            openWriteFilePicker();
            isImport = false;
        });
    }

    private void openReadFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        activityResultLauncher.launch(intent);
    }

    private void openWriteFilePicker() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.putExtra(Intent.EXTRA_TITLE, "test");
        activityResultLauncher.launch(intent);
    }


    private void checkReadPermission() {
        if(ContextCompat.checkSelfPermission(ImportExport.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            readFileProcess();
        } else {
            ActivityCompat.requestPermissions(ImportExport.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkWritePermission() {
        if(ContextCompat.checkSelfPermission(ImportExport.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            writeFileProcess();
        } else {
            ActivityCompat.requestPermissions(ImportExport.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != READ_PERMISSION_REQUEST_CODE) {
            return;
        }

        if((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            if(isImport) {
                readFileProcess();
            } else {
                writeFileProcess();
            }
        } else {
            Toast.makeText(ImportExport.this, "You are not allowed to " + (isImport ? "read from" : "write to") + " this file!", Toast.LENGTH_LONG).show();
        }
    }

    private void writeFileProcess() {
        if(fileUri == null) {
            Toast.makeText(ImportExport.this, "Path is not defined!", Toast.LENGTH_LONG).show();
            return;
        }
        
        int errorCode = fileAccessHelper.writeFile(fileUri);
        if(errorCode > 0) {
            String errorMessage = fileAccessHelper.getFileErrorMessage(errorCode, ImportExport.this);
            Toast.makeText(ImportExport.this, errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(ImportExport.this, "Export completed!", Toast.LENGTH_SHORT).show();
    }

    private void readFileProcess() {
        String filePath = (fileUri != null) ? fileUri.getPath() : null;
        if(fileUri == null) {
            Toast.makeText(ImportExport.this, "Path is not defined!", Toast.LENGTH_LONG).show();
            return;
        }

        Pair<String, Integer> result = fileAccessHelper.readFile(fileUri);
        String fileContents = result.first;
        int errorCode = result.second;
        if(fileContents == null) {
            String errorMessage = fileAccessHelper.getFileErrorMessage(errorCode, ImportExport.this);
            Toast.makeText(ImportExport.this, errorMessage, Toast.LENGTH_LONG).show();
            return;
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        List<Story> stories = gson.fromJson(fileContents, new TypeToken<List<Story>>(){}.getType());
        Log.i("mamad", stories.toString());

        for (Story story : stories) {
            StoryCudAsyncTask storyCudAsyncTask = new StoryCudAsyncTask(this, Action.INSERT_ACTION, new DbResponse<Story>() {
                @Override
                public void onSuccess(Story story) {
                    Toast.makeText(ImportExport.this, "Story added successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Error error) {
                    Toast.makeText(ImportExport.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            storyCudAsyncTask.execute(
                    story.getTitle(),
                    story.getDescription(),
                    story.getAge(),
                    Long.toString(story.getUserId()),
                    story.getPublishedDate(),
                    Boolean.toString(story.getIsFavorite())
            );
        }
        Toast.makeText(ImportExport.this, "Reading from file completed successfully!\nPlease restart the app to see the changes", Toast.LENGTH_SHORT).show();
    }
}