package com.example.smartattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class HelpActivity extends AppCompatActivity {
    ExpandableListView lvHelp;
    ArrayList<String> headings,infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = findViewById(R.id.toolbarHelp);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("How to use");

        lvHelp =findViewById(R.id.lvHelp);
        headings=new ArrayList<>();
        infos=new ArrayList<>();
        String[] headingArray=getResources().getStringArray(R.array.help_headings);
        String[] infoArray=getResources().getStringArray(R.array.help_infos);

        headings.addAll(Arrays.asList(headingArray));
        infos.addAll(Arrays.asList(infoArray));


        HelpActivityAdapter adapter =new HelpActivityAdapter(this,headings,infos);
        lvHelp.setAdapter(adapter);





    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
