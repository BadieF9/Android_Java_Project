package com.example.badiefarzandiassignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.badiefarzandiassignment2.data.async.InsertUserAsynctask;
import com.example.badiefarzandiassignment2.data.async.UserExistsAsyncTask;
import com.example.badiefarzandiassignment2.data.db.DbResponse;
import com.example.badiefarzandiassignment2.data.model.User;

public class SignUp extends AppCompatActivity {

    private SharedPreferences preferences;
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
        preferences = getPreferences(Context.MODE_PRIVATE);
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
        UserExistsAsyncTask userExistsAsyncTask = new UserExistsAsyncTask(this, new DbResponse<Boolean>() {
            @Override
            public void onSuccess(Boolean userExists) {
                InsertUserAsynctask insertUserAsynctask = new InsertUserAsynctask(getApplicationContext(), new DbResponse<User>() {
                    @Override
                    public void onSuccess(User user) {
                        sendToast("User created successfully\n Please Login");
                        finish();
                    }

                    @Override
                    public void onError(Error error) {
                        sendToast("Something went wrong! \nRegister failed!");
                    }
                });
                insertUserAsynctask.execute(
                        emailEdt.getText().toString(),
                        passwordEdt.getText().toString(),
                        firstNameEdt.getText().toString(),
                        lastNameEdt.getText().toString(),
                        maleGenderRb.isChecked() ? "male" : "female",
                        email_chbox.isChecked() && sms_chbox.isChecked() ? "email,sms" : email_chbox.isChecked() ? "email" : "sms"
                );
            }

            @Override
            public void onError(Error error) {
                sendToast("User exists with this email.\nPlease choose another one");
            }
        });
        userExistsAsyncTask.execute(emailEdt.getText().toString());


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