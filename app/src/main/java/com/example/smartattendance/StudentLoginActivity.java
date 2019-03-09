package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.text.TextUtils;
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
//        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username from EditText
                if (TextUtils.isEmpty(txtUsername.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Enter valid rollNumber ", Toast.LENGTH_SHORT).show();
                } else {
                    String rollnumber = txtUsername.getText().toString();
                    // Check if username, password is filled
                    session.createLoginSession(rollnumber, "student");

                    // Staring MainActivity
                    Intent i = new Intent(getApplicationContext(), StudentDashboardActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });
    }
}