package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class StudentDashboardActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    FloatingActionButton fab;
    ArrayList<dbCourseStudent> _dbCourse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionBar.setTitle("Courses");
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation);
        fab = findViewById(R.id.fabAddCourse);
        LayoutInflater.from(this).inflate(R.layout.td_navigation_header, navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                int id=menuItem.getItemId();
                switch (id){
                    case R.id.nav_help:
                        Intent intent =new Intent(StudentDashboardActivity.this,HelpActivity.class);
                        startActivity(intent);
                }
                // close drawer when item is tapped
                drawerLayout.closeDrawers();
                return true;
            }

        });
        TextView nav_text = findViewById(R.id.nav_name);
        nav_text.setText(new SessionManager(this).getKeyName());
        _dbCourse = Paper.book().read("SCourses", new ArrayList<dbCourseStudent>());
        adapter = new StudentDashboardAdapter(this, _dbCourse);
        recyclerView = findViewById(R.id.rvCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                Boolean refresh = data.getExtras().getBoolean("refresh");
                Log.d("enteredResult", refresh + "");
                if (refresh) {
                    /*courseList.clear();
                    courseList.addAll(courseDao.loadAll());*/
                    _dbCourse.clear();
                    ArrayList<dbCourseStudent> tmp = Paper.book().read("SCourses", new ArrayList<dbCourseStudent>());
                    _dbCourse.addAll(tmp);
                    adapter.notifyDataSetChanged();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickFabAddCourse(View view) {
        Intent intent = new Intent(StudentDashboardActivity.this, AddCourseActivity.class);
        startActivityForResult(intent, 2);

    }
}
