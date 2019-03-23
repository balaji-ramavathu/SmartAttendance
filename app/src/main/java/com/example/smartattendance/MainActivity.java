package com.example.smartattendance;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.google.android.material.button.MaterialButton;


public class MainActivity extends AppCompatActivity {
    MaterialButton btnTeacher;
    MaterialButton btnStudent;

    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTeacher = findViewById(R.id.btn_teacher);
        btnStudent = findViewById(R.id.btn_student);
        session = new SessionManager(getApplicationContext());
        if (session.checkLogin()){
            String details = session.getProfession();
            if(details.equals("student")) {
                Intent i = new Intent(this, StudentDashboardActivity.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



                // Staring Login Activity
                startActivity(i);
                finish();
            }
            else {
                Intent i = new Intent(this, TeacherDashboardActivity.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Staring Login Activity
                startActivity(i);
                finish();
            }
        }
    }

    public void onClickBtnStudent(View view) {
        Intent intent = new Intent(MainActivity.this, StudentLoginActivity.class);
        startActivity(intent);
    }

    public void onClickBtnTeacher(View view) {
        Intent intent = new Intent(MainActivity.this, TeacherLoginActivity.class);
        startActivity(intent);
    }
}