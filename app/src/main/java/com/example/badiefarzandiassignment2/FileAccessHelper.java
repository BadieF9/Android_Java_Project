package com.example.badiefarzandiassignment2;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Pair;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.async.GetAllUsersAsyncTask;
import com.example.badiefarzandiassignment2.data.async.GetStoryAsyncTask;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.Story;
import com.example.badiefarzandiassignment2.data.model.UserWithStories;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileAccessHelper {
    public static final int EXTERNAL_STORAGE_NOT_READY_ERROR_CODE = 1;
    public static final int FILE_DOES_NOT_EXIST_ERROR_CODE = 2;
    public static final int FILE_READ_ERROR_CODE = 3;
    public static final int FILE_WRITE_ERROR_CODE = 4;
    public static final int FILE_PATH_NOT_PROVIDED_ERROR_CODE = 5;

    private static FileAccessHelper instance = null;

    private Context context = null;

    private FileAccessHelper(Context context) {
        this.context = context;
    }

    public static FileAccessHelper getInstance(Context context) {
        if(instance == null) {
            instance = new FileAccessHelper(context);
        }
        return instance;
    }

    private boolean isExternalStorageAvailable() {
        return (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED));
    }

    public Pair<String, Integer> readFile(Uri fileUri) {
        if(!isExternalStorageAvailable()) {
            return new Pair<String, Integer>(null, EXTERNAL_STORAGE_NOT_READY_ERROR_CODE);
        }

        String wholeText = "";
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line = "";
            while((line = reader.readLine()) != null) {
                wholeText = wholeText + line + "\n";
            }

            reader.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new Pair<String, Integer>(null, FILE_DOES_NOT_EXIST_ERROR_CODE);
        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<String, Integer>(null, FILE_READ_ERROR_CODE);
        }
        return new Pair<String, Integer>(wholeText, 0);
    }

    public int writeFile(Uri fileUri) {
        final int[] returnCode = {0};
        if(!isExternalStorageAvailable()) {
            return EXTERNAL_STORAGE_NOT_READY_ERROR_CODE;
        }

        GetStoryAsyncTask getStoryAsyncTask = new GetStoryAsyncTask(context, new DbResponse<List<Story>>() {
            @Override
            public void onSuccess(List<Story> stories) {
                try {
                    ParcelFileDescriptor pdf = context.getContentResolver().openFileDescriptor(fileUri, "w");
                    FileOutputStream outputStream = new FileOutputStream(pdf.getFileDescriptor());
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    outputStream.write(gson.toJson(stories).getBytes(StandardCharsets.UTF_8));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    returnCode[0] = FILE_DOES_NOT_EXIST_ERROR_CODE;
                } catch (IOException e) {
                    e.printStackTrace();
                    returnCode[0] = FILE_WRITE_ERROR_CODE;
                }
            }

            @Override
            public void onError(Error error) {

            }
        });
        getStoryAsyncTask.execute();

        return returnCode[0];
    }

    public String getFileErrorMessage(int errorCode, Context context) {
        String errorMessage = "";
        switch(errorCode) {
            case EXTERNAL_STORAGE_NOT_READY_ERROR_CODE:
                errorMessage = "External storage is not ready to use!";
                break;
            case FILE_DOES_NOT_EXIST_ERROR_CODE:
                errorMessage = "File does not exist!";
                break;
            case FILE_WRITE_ERROR_CODE:
                errorMessage = "An error accured while writing to a file!";
                break;
            case FILE_PATH_NOT_PROVIDED_ERROR_CODE:
                errorMessage = "File path is not provided!";
                break;
        }
        return errorMessage;
    }

}
