package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StudentLoginActivity extends AppCompatActivity {

    // Email, password edittext
    EditText txtUsername, txtPassword;

    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Username input text
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String testuser = "testube";
                // Get username from EditText
                String username = txtUsername.getText().toString();
                // Check if username, password is filled
                if(username.trim().length() > 0 ){

                    if(username.equals(testuser)){

                        // Creating user login session
                        session.createLoginSession(testuser, "student");

                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), StudentLoginActivity.class);
                        startActivity(i);
                        finish();

                    }else{
                        // username
                        alert.showAlertDialog(StudentLoginActivity.this, "Login failed..", "Username/Password is incorrect", false);
                    }
                }else{
                    // user didn't entered username
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(StudentLoginActivity.this, "Login failed..", "Please enter username and password", false);
                }

            }
        });
    }
}
