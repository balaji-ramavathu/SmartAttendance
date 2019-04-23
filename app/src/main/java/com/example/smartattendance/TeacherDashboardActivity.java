package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TeacherDashboardActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    DashboardRecyclerAdapter adapter;
    FloatingActionButton fab;
    ArrayList<dbCourse> _dbCourse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

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
                        Intent intent =new Intent(TeacherDashboardActivity.this,HelpActivity.class);
                        startActivity(intent);
                }
                // close drawer when item is tapped
                drawerLayout.closeDrawers();
                return true;
            }

        });
        TextView nav_text = findViewById(R.id.nav_name);
        nav_text.setText(new SessionManager(this).getKeyName());
        _dbCourse = Paper.book().read("Courses", new ArrayList<dbCourse>());
        adapter = new DashboardRecyclerAdapter(this,TeacherDashboardActivity.this, _dbCourse);
        recyclerView = findViewById(R.id.rvCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);


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
                    ArrayList<dbCourse> tmp = Paper.book().read("Courses", new ArrayList<dbCourse>());
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

        Intent intent = new Intent(TeacherDashboardActivity.this, AddCourseActivity.class);
        startActivityForResult(intent, 2);

    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private DashboardRecyclerAdapter mAdapter;
        private Drawable icon;
        private final ColorDrawable background;

        public SwipeToDeleteCallback(DashboardRecyclerAdapter adapter) {
            super(0, ItemTouchHelper.LEFT);
            mAdapter = adapter;
            icon = ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.ic_delete_24dp);
            background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mAdapter.deleteItem(position);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX,
                    dY, actionState, isCurrentlyActive);

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20;

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                        itemView.getBottom());

            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            }
            else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }
            background.draw(c);
            icon.draw(c);
        }
    }


}
