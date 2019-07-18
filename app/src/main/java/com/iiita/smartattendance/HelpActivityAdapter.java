package com.iiita.smartattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class HelpActivityAdapter extends BaseExpandableListAdapter {

    ArrayList<String> headings,infos;
    Context mcontext;

    private String[] mColorArray = {"dark_green", "dark_orange", "light_kaki", "light_red",
            "dark_gray", "dark_red", "chocolate", "dark_pink", "violet",
            "dark_kaki", "black"};



    public HelpActivityAdapter(Context context,
                               ArrayList<String> headings,ArrayList<String> infos) {
        this.headings = headings;
        mcontext = context;
        this.infos =infos;
    }



    /*@Override
    public int getCount() {
        return headings.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v==null)
            v = LayoutInflater.from(mcontext).inflate(R.layout.help_list_item, parent, false);
        final TextView tvHeading =  v.findViewById(R.id.tvHelpHeading);
        final TextView tvInfo = v.findViewById(R.id.tvHelpInfo);
        tvHeading.setText(headings.get(position));
        tvInfo.setText(infos.get(position));

        return v;
    }*/


    @Override
    public int getGroupCount() {
        return headings.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headings.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return infos.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v==null)
            v = LayoutInflater.from(mcontext).inflate(R.layout.help_list_item, parent, false);
        final TextView tvHeading =  v.findViewById(R.id.tvHelpHeading);
        final ImageView imgArrow = v.findViewById(R.id.imgArrow);
        tvHeading.setText(headings.get(groupPosition));
        String color = mColorArray[groupPosition % mColorArray.length];
        int colorIdentifier = mcontext.getResources()
                .getIdentifier(color, "color", mcontext.getPackageName());
        int colorResource = ContextCompat.getColor(mcontext, colorIdentifier);
        tvHeading.setTextColor(colorResource);
        if (isExpanded) {
            imgArrow.setImageResource(R.drawable.ic_arrow_up_black_24dp);
        }
        else {
            imgArrow.setImageResource(R.drawable.ic_arrow_down_black_24dp);
        }
        return v;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v==null)
            v = LayoutInflater.from(mcontext).inflate(R.layout.help_list_info_item, parent, false);
        final TextView tvInfo = v.findViewById(R.id.tvHelpInfo);
        tvInfo.setText(infos.get(groupPosition));
        return v;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
