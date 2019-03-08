package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class TeacherLoginActivity extends AppCompatActivity {
    MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
        btnLogin=findViewById(R.id.btnTeacherLogin);


    }

    public void onClickBtnTeacherLogin(View view) {
        Intent intent=new Intent(TeacherLoginActivity.this,TeacherDashboardActivity.class);
        startActivity(intent);
    }
}
