package com.example.smartattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VirtualMapAdapter extends RecyclerView.Adapter<VirtualMapAdapter.VirtualMapViewHolder> {
    Context context;
    int rows, columns;
    VirtualMapHelper helper;
    ArrayList<ArrayList<ArrayList<Student>>> VMap;
    Activity activity;
    String courseCode;


    public VirtualMapAdapter(Activity activity,Context context, int rows, int columns, VirtualMapHelper helper, String courseCode) {
        this.activity=activity;
        this.context = context;
        this.rows = rows;
        this.columns = columns;
        this.helper = helper;
        this.courseCode=courseCode;

    }


    @NonNull
    @Override
    public VirtualMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.virtual_map_item, parent, false);
        VirtualMapViewHolder viewHolder = new VirtualMapViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final VirtualMapViewHolder holder, int position) {

        VMap = helper.getVMap();
        final int row = position / rows;
        final int column = position % rows;


        if(VMap.size()!=0){
//            final int studentCount = VMap.get(row).get(columns - column - 1).size();
//            final ArrayList<Student> studentsInBench = VMap.get(row).get(columns - column - 1);
            final int studentCount = VMap.get(row).get(rows - column - 1).size();
            final List<Student> studentsInBench = VMap.get(row).get(rows - column - 1);
//            holder.seat.setText(String.valueOf(position) + String.valueOf(row) + String.valueOf(column));
            holder.seat.setText(String.valueOf(studentsInBench.size()));
            holder.seat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(studentCount > 0) {
                        showDialog(studentsInBench,holder,row,column);
                    }
                }
            });
        }
    }
    public void showDialog(final List<Student> studentsInBench,final VirtualMapViewHolder holder, final int row, final int column) {

        final Dialog dialog = new Dialog(context);
        View view = activity.getLayoutInflater().inflate(R.layout.student_list_dialog, null);
        ListView lv = view.findViewById(R.id.lvRollsDialog);
        MaterialButton btnOK = view.findViewById(R.id.btnDialogOK);
        MaterialButton btnCancel = view.findViewById(R.id.btnDialogCancel);
        final TextInputEditText  etAddRoll = view.findViewById(R.id.etAddRoll);
        final ImageButton btnAddRoll = view.findViewById(R.id.btnAddRollManually);
        final StudentsListDialog adapter = new StudentsListDialog(activity,context, studentsInBench,courseCode);
        lv.setAdapter(adapter);
        dialog.setContentView(view);
        dialog.show();
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.seat.setText(String.valueOf(studentsInBench.size()));
                List<String> rollNumbers=new ArrayList<>();
                for(int i=0;i<studentsInBench.size();i++) {
                    rollNumbers.add(studentsInBench.get(i).RollNumber);
                }
                dialog.dismiss();
            }
        });

        btnAddRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etAddRoll.getText()!=null){
                    String roll = etAddRoll.getText().toString();
                    Student student=new Student(courseCode,roll,row,column,roll);
                    studentsInBench.add(student);
                    adapter.notifyDataSetChanged();

                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

    }

    @Override
    public int getItemCount() {
        return rows * columns;
    }

    public class VirtualMapViewHolder extends RecyclerView.ViewHolder {
        Button seat;

        public VirtualMapViewHolder(View itemView) {
            super(itemView);
            seat = itemView.findViewById(R.id.seat);
        }
    }
}
