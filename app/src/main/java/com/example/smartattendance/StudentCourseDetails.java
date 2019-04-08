package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class StudentCourseDetails extends AppCompatActivity {
    MaterialButton btnMarkAttendance;
    MaterialButton btnAddSheet;
    MaterialButton btnShowAttendance;
    RadioGroup radioGroup;
    RadioButton rbWifi,rbBluetooth;
    String network,courseCode;
    ClipboardManager clipboard;
    WifiManager wifiManager;
    BluetoothAdapter mBluetoothAdapter;
    SessionManager sessionManager;
    int row,column;
    EditText trow,tcolumn;
    ArrayList<dbCourseStudent> _dbCourse;
    dbCourseStudent CurrentCourse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course_details);
        if(!Utils.isBlank(getIntent().getStringExtra("courseCode"))) {
            courseCode=getIntent().getStringExtra("courseCode");
        }
        radioGroup=findViewById(R.id.rgNetwork);
        rbWifi=findViewById(R.id.rbWifi);
        trow=findViewById(R.id.etRow);
        tcolumn=findViewById(R.id.etColumn);
        rbBluetooth=findViewById(R.id.rbBluetooth);
        btnMarkAttendance=findViewById(R.id.btnMarkAttendence);
        btnAddSheet=findViewById(R.id.btnaddsheet);
        btnShowAttendance=findViewById(R.id.btnshowattendance);
        _dbCourse = Paper.book().read("SCourses", new ArrayList<dbCourseStudent>());
        Log.d("update", courseCode);
        Log.d("update", Integer.toString(_dbCourse.size()));
        for(dbCourseStudent crse : _dbCourse){
            if(crse.Course.equals(courseCode)){
                Log.d("update", "Course initalised");
                this.CurrentCourse = crse;
                break;
            }
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sessionManager = new SessionManager(getApplicationContext());
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

    }

    public void onClickbtnShowAttendancec(View view){

    }
    public void onClickbtnAddSheet(View view){
        showDialog(this.CurrentCourse.Spreadsheetlink);
    }
    public void onClickBtnMarkAttendance(View view) {
        if (verify()) {
            row = Integer.parseInt(trow.getText().toString());
            column = Integer.parseInt(tcolumn.getText().toString());
            String name = courseCode.toUpperCase() + "_" + sessionManager.getKeyName().toUpperCase() + "_" + row + "_" + column;
            Log.d("encrypt", "Before : " + name);
            Encoder tool = new Encoder();

            name = tool.Encode(name);
            Log.d("encrypt", "After : " + name);
            if (radioGroup.getCheckedRadioButtonId() == rbBluetooth.getId()) {
                network = "bluetooth";
                mBluetoothAdapter.enable();
                mBluetoothAdapter.setName(name);
                Log.d("name",name);

            } else {
                network = "wifi";
                ClipData clip = ClipData.newPlainText("name", name);
                clipboard.setPrimaryClip(clip);
                startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
            }
        }
    }


    public void showDialog(String sheetlink) {

        final Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.update_sheet_dialog, null);
        final TextInputEditText tvSheetlink = view.findViewById(R.id.tvSheetLink);
        MaterialButton btnok = view.findViewById(R.id.btnaddsheetok);
        MaterialButton btncancel = view.findViewById(R.id.btnaddsheetcancel);
        if(sheetlink != null){
            tvSheetlink.setText(sheetlink);
        }
        dialog.setContentView(view);
        dialog.show();
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sheetLink = tvSheetlink.getText().toString();
                for(int i = 0; i < _dbCourse.size(); ++i){
                    if(_dbCourse.get(i).Course.equals(courseCode)){
                        _dbCourse.get(i).Spreadsheetlink = sheetLink;
                        CurrentCourse = _dbCourse.get(i);
                        Paper.book().write("SCourses", _dbCourse);
                        break;
                    }
                }
                Log.d("updatesheet", sheetLink);
                dialog.dismiss();
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    public boolean verify() {
        if(TextUtils.isEmpty(trow.getText().toString())) {
            Toast.makeText(this, "Enter valid bench number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(tcolumn.getText().toString())) {
            Toast.makeText(this, "Enter valid column number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}