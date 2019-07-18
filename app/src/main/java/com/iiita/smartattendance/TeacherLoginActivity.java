package com.iiita.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class TeacherLoginActivity extends AppCompatActivity {
    MaterialButton btnLogin;
    SessionManager session;
    EditText txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        btnLogin = findViewById(R.id.btnTeacherLogin);
        session = new SessionManager(getApplicationContext());
        txtUsername = findViewById(R.id.etTeacherUsername);
    }

    public void onClickBtnTeacherLogin(View view) {

        if (TextUtils.isEmpty(txtUsername.getText().toString())) {
            Toast.makeText(this, "Enter valid username ", Toast.LENGTH_SHORT).show();
        } else {
            String username = txtUsername.getText().toString();
            // Check if username, password is filled
            session.createLoginSession(username, "teacher");

            // Staring MainActivity
            Intent i = new Intent(getApplicationContext(), TeacherDashboardActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }
}
