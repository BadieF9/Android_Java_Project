package com.example.badiefarzandiassignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.async.DbAsyncTask;
import com.example.badiefarzandiassignment2.data.async.DbResult;
import com.example.badiefarzandiassignment2.data.async.InsertUserAsynctask;
import com.example.badiefarzandiassignment2.data.async.UserExistsAsyncTask;
import com.example.badiefarzandiassignment2.data.async.commands.InsertUserCommand;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.User;
import com.example.badiefarzandiassignment2.network.NetworkHelper;

public class SignUp extends AppCompatActivity {

//    private SharedPreferences preferences;
    private NetworkHelper networkHelper;
    private EditText firstNameEdt;
    private EditText lastNameEdt;
    private EditText emailEdt;
    private EditText passwordEdt;
    private EditText confirmPasswordEdt;
    private RadioButton maleGenderRb;
    private RadioButton femaleGenderRb;
    private CheckBox email_chbox;
    private CheckBox sms_chbox;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initialWidgets();
//        preferences = getPreferences(Context.MODE_PRIVATE);
        this.networkHelper = NetworkHelper.getInstance(getApplicationContext());
    }

    private void initialWidgets() {
        firstNameEdt = findViewById(R.id.signup_first_name_edt);
        lastNameEdt = findViewById(R.id.signup_last_name_edt);
        emailEdt = findViewById(R.id.signup_email_edt);
        passwordEdt = findViewById(R.id.signup_password_edt);
        confirmPasswordEdt = findViewById(R.id.signup_confirm_password_edt);
        maleGenderRb = findViewById(R.id.signup_male_gender_rb);
        femaleGenderRb = findViewById(R.id.signup_female_gender_rb);
        email_chbox = findViewById(R.id.signup_email_chb);
        sms_chbox = findViewById(R.id.signup_sms_chb);
        signupBtn = findViewById(R.id.signup_signup_btn);

        signupBtn.setOnClickListener(this::signUp);
    }

    public void signUp(View view) {
        if(!firstNameEdt.getText().toString().isEmpty()) {
            if(!lastNameEdt.getText().toString().isEmpty()) {
                if(!emailEdt.getText().toString().isEmpty()) {
                    if(!passwordEdt.getText().toString().isEmpty() && !confirmPasswordEdt.getText().toString().isEmpty()) {
                        if(passwordEdt.getText().toString().equals(confirmPasswordEdt.getText().toString())) {
                            if(maleGenderRb.isChecked() || femaleGenderRb.isChecked()) {
                                if(email_chbox.isChecked() || sms_chbox.isChecked()) {
                                    saveForm();
                                } else {
                                    sendToast("Please select at least one sending type");
                                }
                            } else {
                                sendToast("Please select your gender");
                            }
                        } else {
                            sendToast("Password and Confirmation Password fields are not equal");
                        }
                    } else {
                        sendToast("Please fill the Password and Confirmation Password fields");
                    }
                } else {
                    sendToast("Please fill the Email field");
                }
            } else {
                sendToast("Please fill the Last Name field");
            }
        } else {
            sendToast("Please fill the First Name field");
        }
    }

    public void sendToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void saveForm() {
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        String firstName = firstNameEdt.getText().toString();
        String lastName = lastNameEdt.getText().toString();
        String gender = maleGenderRb.isChecked() ? "male" : "female";
        String sendingType = email_chbox.isChecked() && sms_chbox.isChecked() ? "email,sms" : email_chbox.isChecked() ? "email" : "sms";
        final User newUser = new User(email, password, firstName, lastName, gender, sendingType);

        networkHelper.signupUser(newUser, new DbResponse<User>() {
            @Override
            public void onSuccess(User user) {
                newUser.setId(user.getId());
                InsertUserCommand insertUserCommand = new InsertUserCommand(getApplicationContext(), newUser);
                DbAsyncTask<User> dbAsyncTask = new DbAsyncTask(new DbResponse<DbResult<User>>() {
                    @Override
                    public void onSuccess(DbResult<User> dbResult) {
                        sendToast(getString(R.string.user_signup_success));
                        finish();
                    }

                    @Override
                    public void onError(Error error) {
                        error.printStackTrace();
                        sendToast("Something went wrong! \nRegister failed!");
                    }
                });
                dbAsyncTask.execute(insertUserCommand);
            }

            @Override
            public void onError(Error error) {
                error.printStackTrace();
                String errorMsg = error != null ? error.getMessage() : getString(R.string.user_signup_error);
                sendToast(errorMsg);
            }
        });

        //  signup in the local database
//        UserExistsAsyncTask userExistsAsyncTask = new UserExistsAsyncTask(this, new DbResponse<Boolean>() {
//            @Override
//            public void onSuccess(Boolean userExists) {
//                InsertUserAsynctask insertUserAsynctask = new InsertUserAsynctask(getApplicationContext(), new DbResponse<User>() {
//                    @Override
//                    public void onSuccess(User user) {
//                        sendToast("User created successfully\n Please Login");
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(Error error) {
//                        sendToast("Something went wrong! \nRegister failed!");
//                    }
//                });
//                insertUserAsynctask.execute(
//                        emailEdt.getText().toString(),
//                        passwordEdt.getText().toString(),
//                        firstNameEdt.getText().toString(),
//                        lastNameEdt.getText().toString(),
//                        maleGenderRb.isChecked() ? "male" : "female",
//                        email_chbox.isChecked() && sms_chbox.isChecked() ? "email,sms" : email_chbox.isChecked() ? "email" : "sms"
//                );
//            }
//
//            @Override
//            public void onError(Error error) {
//                sendToast("User exists with this email.\nPlease choose another one");
//            }
//        });
//        userExistsAsyncTask.execute(emailEdt.getText().toString());


        //  signup in the shared preferences
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(emailEdt.getText().toString() + "_firstName", firstNameEdt.getText().toString());
//        editor.putString(emailEdt.getText().toString() + "_lastName", lastNameEdt.getText().toString());
//        editor.putString(emailEdt.getText().toString(), emailEdt.getText().toString());
//        editor.putString(emailEdt.getText().toString() + "_password", passwordEdt.getText().toString());
//        editor.putString(emailEdt.getText().toString() + "_gender", maleGenderRb.isChecked() ? "male" : "female");
//        boolean bothSendTypesChecked = email_chbox.isChecked() && sms_chbox.isChecked();
//        if(bothSendTypesChecked) {
//            editor.putString(emailEdt.getText().toString() + "_sendingType", "email,sms");
//        } else {
//            editor.putString(emailEdt.getText().toString() + "_sendingType", email_chbox.isChecked() ? "email" : "sms");
//        }
//
//        editor.apply();
    }
}