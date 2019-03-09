package com.example.smartattendance;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.ArrayList;

import io.paperdb.Paper;

public class CustomArrayAdapter extends ArrayAdapter {

    ArrayList<String> al = new ArrayList<>();
    Context mcontext;
    String CourseCode;


    public CustomArrayAdapter(Context context, int textViewResourceId, ArrayList<String> objects, String CourseCode) {
        super(context, textViewResourceId, objects);
        al = objects;
        mcontext = context;
        this.CourseCode = CourseCode;
    }



    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v==null)
            v = LayoutInflater.from(mcontext).inflate(R.layout.list_item_buttons, parent, false);
        final TextView textView = (TextView) v.findViewById(R.id.date);
        Button updateBtn=v.findViewById(R.id.updatebtn);
        /*updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSheets(textView.getText().toString());
            }
        });*/
        textView.setText(al.get(position));

        return v;
    }
    public void UpdateSheets(String date){
        ArrayList<dbAttendance> _dbAttendance = Paper.book().read("Attendance", new ArrayList<dbAttendance>());
        ArrayList<dbRollnumber> _rollnumbers = new ArrayList<>();
        for(dbAttendance element : _dbAttendance){
            if(element.courseId.equals(CourseCode) && element.date.equals(date)){
                _rollnumbers = element.rollnumbers;
            }
        }

    }



}
