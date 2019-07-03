package com.example.smartattendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

public class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder> {
    Context context;
    TeacherDashboardActivity activity;
    private ArrayList<dbCourse> courseList;
    private MaterialCardView cardView;
    private int mRecentlyDeletedItemPosition;
    private dbCourse mRecentlyDeletedItem;



    private String[] mColorArray = {"dark_green", "dark_orange", "light_kaki", "light_red",
            "dark_gray", "dark_red", "chocolate", "dark_pink", "violet",
            "dark_kaki", "black"};

    public DashboardRecyclerAdapter(Context context,TeacherDashboardActivity activity, ArrayList<dbCourse> list) {
        this.context = context;
        this.activity=activity;
        this.courseList = list;
    }

    public class DashboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseCode, tvCourseName;

        public DashboardViewHolder(View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
        }
    }


    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dashboard_recycler_item, parent, false);
        cardView = (MaterialCardView) view.getRootView();
        DashboardViewHolder viewHolder = new DashboardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final DashboardViewHolder holder, int position) {

        String color = mColorArray[position % mColorArray.length];
        int colorIdentifier = context.getResources()
                .getIdentifier(color, "color", context.getPackageName());
        int colorResource = ContextCompat.getColor(context, colorIdentifier);
        cardView.setCardBackgroundColor(colorResource);

        final String courseCode = courseList.get(position).courseid;
        String courseName = courseList.get(position).name;
        holder.tvCourseCode.setText(courseCode);
        holder.tvCourseName.setText(courseName);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(context, CourseDetails.class);
            intent.putExtra("courseCode", courseCode);
            context.startActivity(intent);

            }
        });


    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = courseList.get(position);
        mRecentlyDeletedItemPosition = position;
        courseList.remove(position);
        Paper.book().write("Courses",courseList);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    public void showUndoSnackbar() {
        View view = activity.findViewById(R.id.coordinatorTeacherDashboard);
        Snackbar snackbar = Snackbar.make(view, "Course Deleted",
                Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete();
            }
        });
        snackbar.setActionTextColor(context.getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }
    private void undoDelete() {
        courseList.add(mRecentlyDeletedItemPosition,mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
        Paper.book().write("Courses",courseList);
        notifyDataSetChanged();
    }


    public ArrayList<dbCourse> getCourseList() {
        return this.courseList;
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

}
