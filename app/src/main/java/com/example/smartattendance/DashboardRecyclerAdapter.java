package com.example.smartattendance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder> {
    Context context;
    ArrayList<dbCourse> courseList;
    MaterialCardView cardView;

    private String[] mColorArray = {"dark_green", "dark_orange", "light_kaki", "light_red",
            "dark_gray", "dark_red", "chocolate", "dark_pink", "violet",
            "dark_kaki", "black"};

    public DashboardRecyclerAdapter(Context context, ArrayList<dbCourse> list) {
        this.context = context;
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
        RelativeLayout relativeLayout = (RelativeLayout) view.getRootView();
        cardView = (MaterialCardView) relativeLayout.getChildAt(0);
        DashboardViewHolder viewHolder = new DashboardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {

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
                SessionManager sessionManager = new SessionManager(context);
                String profession = sessionManager.getProfession();
                if(profession.equals("teacher")) {
                    Intent intent = new Intent(context, CourseDetails.class);
                    intent.putExtra("courseCode", courseCode);
                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, StudentCourseDetails.class);
                    intent.putExtra("courseCode", courseCode);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

}
