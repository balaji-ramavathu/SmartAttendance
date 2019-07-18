package com.iiita.smartattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        AddRollsViewHolder viewHolder;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.add_rolls_item, parent, false);

            viewHolder=new AddRollsViewHolder();
            viewHolder.tvSuffix=v.findViewById(R.id.tvSuffix);
            viewHolder.tvFrom=v.findViewById(R.id.tvFrom);
            viewHolder.tvTo=v.findViewById(R.id.tvTo);
            viewHolder.btnRemoveRolls=v.findViewById(R.id.btnRemoveRolls);
            v.setTag(viewHolder);
        } else {
            viewHolder=(AddRollsViewHolder) v.getTag();
        }
        viewHolder.tvSuffix.setText(rolls.get(position).getSuffix());
        viewHolder.tvFrom.setText(String.valueOf(rolls.get(position).getFrom()));
        viewHolder.tvTo.setText(String.valueOf(rolls.get(position).getTo()+""));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit
            }
        });
        viewHolder.btnRemoveRolls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rolls.remove(position);
                notifyDataSetChanged();
            }
        });


        return v;
    }

    public ArrayList<RollNumber> getRolls() {
        return this.rolls;
    }

    class AddRollsViewHolder {
        TextView tvSuffix;
        TextView tvFrom;
        TextView tvTo;
        ImageButton btnRemoveRolls;

    }

}
