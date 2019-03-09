package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class TeacherLoginActivity extends AppCompatActivity {
    MaterialButton btnLogin;
    SessionManager session;
    EditText txtUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        btnLogin=findViewById(R.id.btnTeacherLogin);
        session = new SessionManager(getApplicationContext());
        txtUsername = findViewById(R.id.etTeacherUsername);
    }

    public void onClickBtnTeacherLogin(View view) {
        String username = txtUsername.getText().toString();
        // Check if username, password is filled
        session.createLoginSession(username, "teacher");

        // Staring MainActivity
        Intent i = new Intent(getApplicationContext(), TeacherDashboardActivity.class);
        startActivity(i);
        finish();
    }
}
