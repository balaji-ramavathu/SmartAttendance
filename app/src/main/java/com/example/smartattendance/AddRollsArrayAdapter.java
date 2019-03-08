package com.example.smartattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.smartattendance.model.RollLimits;
import com.example.smartattendance.model.RollNumber;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddRollsArrayAdapter extends ArrayAdapter {
    ArrayList<RollNumber> rolls;
    Context context;

    public AddRollsArrayAdapter(Context context, int viewResourceId, ArrayList<RollNumber> rolls) {
        super(context, viewResourceId);
        this.rolls = rolls;
        this.context = context;
    }

    @Override
    public int getCount() {
        return rolls.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.add_rolls_item, parent, false);
        }

        return v;
    }
}
