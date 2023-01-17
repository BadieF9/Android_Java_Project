package com.example.badiefarzandiassignment2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.async.InsertStoryAsynctask;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.databinding.ActivityAddStoryBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddStory extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityAddStoryBinding binding;
    private String[] items;
    private String selectedItem;
    private SharedPreferences preferences;
    private Intent intent;
    private String currentPhotoPath;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        preferences = getSharedPreferences("SignUp", Context.MODE_PRIVATE);
        intent = getIntent();

        items = new String[]{"Early Readers", "Chapter Books", "Middle Grade Fiction", "Young adult"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        binding.addStoryS.setAdapter(adapter);
        binding.addStoryS.setOnItemSelectedListener(this);
        binding.addStoryCalendarIv.setOnClickListener(this::onCalendarClick);
        binding.addStorySaveBtn.setOnClickListener(this::submitHandler);
        binding.addStoryPickImageBtn.setOnClickListener(this::onPickImageClick);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        selectedItem = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != MY_CAMERA_PERMISSION_CODE){
            return;
        }

        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
        {
            startCameraIntent();
        }
        else
        {
            Toast.makeText(AddStory.this, "camera permission denied", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            binding.addStoryImageIv.setImageURI(Uri.parse(this.currentPhotoPath));
        }
    }


    public void onPickImageClick(View view) {
        if (ContextCompat.checkSelfPermission(AddStory.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddStory.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            startCameraIntent();
        }
    }

    public void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(AddStory.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "com.example.badiefarzandiassignment2",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpeg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        this.currentPhotoPath = image.getAbsolutePath();
        Log.i("mamad", this.currentPhotoPath);
        return image;
    }

    public void onCalendarClick(View view) {
        Calendar mCalendar = Calendar.getInstance();

        int year = mCalendar.get(Calendar.YEAR);

        int month = mCalendar.get(Calendar.MONTH);

        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                binding.addStoryDateTv.setText(year + "/" + monthOfYear + "/" + dayOfMonth);
            }
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    public void submitHandler(View view) {
        if(!binding.addStoryTitleEdt.getText().toString().isEmpty()) {
           if(!binding.addStoryTextEdt.getText().toString().isEmpty()) {
               if(!selectedItem.isEmpty()) {
                   if(!binding.addStoryDateTv.getText().toString().isEmpty()) {
                       Log.i("mamad", "AddStory: " + intent.getStringExtra(MainActivity.USER_ID));
                       InsertStoryAsynctask insertStoryAsynctask = new InsertStoryAsynctask(this, new DbResponse<Story>() {
                           @Override
                           public void onSuccess(Story story) {
                               Main.stories.add(story);
                               MyStories.filteredStories.add(story);
                               Intent intent = new Intent();
                               intent.putExtra(Constant.MYSTORY_CREATE_SUCCESS, "true");
                               setResult(Activity.RESULT_OK, intent);
                               finish();
                           }

                           @Override
                           public void onError(Error error) {
                               sendToast("Something went wrong! \nAdding story failed!");
                           }
                       });
                       insertStoryAsynctask.execute(
                               binding.addStoryTitleEdt.getText().toString(),
                               binding.addStoryTextEdt.getText().toString(),
                               selectedItem,
                               intent.getStringExtra(MainActivity.USER_ID),
                               binding.addStoryDateTv.getText().toString(),
                               this.currentPhotoPath,
                               Boolean.toString(false));
                   } else {
                       sendToast("Please select the published date");
                   }
               } else {
                   sendToast("Please Select reader's age");
               }
           } else {
               sendToast("Please enter a text");
           }
        } else {
            sendToast("Please enter the title");
        }
    }

    public void sendToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}