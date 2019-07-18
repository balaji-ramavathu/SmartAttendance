package com.iiita.smartattendance;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class StudentsListDialog extends BaseAdapter {
    Context context;
    List<Student> studentsList;
    LayoutInflater layoutInflater;
    Activity activity;
    String courseCode;
    public StudentsListDialog(Activity activity,Context context, List<Student> studentsList,
                              String courseCode) {
        this.studentsList=studentsList;
        layoutInflater=LayoutInflater.from(context);
        this.activity=activity;
        this.courseCode=courseCode;
    }

    static class ViewHolder {
        private TextView tvRollNumber;
        private ImageButton btnSubRoll;
    }



    @Override
    public int getCount() {
        return studentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return studentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = activity.getLayoutInflater();
            view = inflator.inflate(R.layout.student_list_dialog_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvRollNumber =  view.findViewById(R.id.tvRollNumberDialog);
            viewHolder.btnSubRoll =  view.findViewById(R.id.btnSubRollDialog);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvRollNumber.setText(studentsList.get(position).RollNumber);
        holder.btnSubRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentsList.remove(position);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public List<Student> getStudentsList() {return studentsList;}




}
