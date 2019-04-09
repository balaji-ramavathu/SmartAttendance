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
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

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


    public VirtualMapAdapter(Activity activity,Context context, int rows, int columns, VirtualMapHelper helper) {
        this.activity=activity;
        this.context = context;
        this.rows = rows;
        this.columns = columns;
        this.helper = helper;

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
        int row = position / rows;
        int column = position % rows;


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
                        showDialog(studentsInBench,holder);
                    }
                }
            });
        }
    }
    public void showDialog(final List<Student> studentsInBench,final VirtualMapViewHolder holder) {

        final Dialog dialog = new Dialog(context);
        View view = activity.getLayoutInflater().inflate(R.layout.student_list_dialog, null);
        ListView lv = view.findViewById(R.id.lvRollsDialog);
        MaterialButton btnOK = view.findViewById(R.id.btnDialogOK);
        MaterialButton btnCancel = view.findViewById(R.id.btnDialogCancel);
        final StudentsListDialog adapter = new StudentsListDialog(activity,context, studentsInBench);
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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

    }

    public int getRow(int tmp) {
        int row;
        tmp++;
        if (tmp % columns == 0) {
            row = tmp / columns;
        } else {
            row = tmp / columns + 1;
        }
        return row;
    }
    public int getColumn(int tmp) {
        tmp++;
        int column = tmp % columns;
        if (column == 0){
            column = columns;
        }
        return column;
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
