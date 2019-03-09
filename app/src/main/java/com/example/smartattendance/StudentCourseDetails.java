package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class StudentCourseDetails extends AppCompatActivity {
    MaterialButton btnMarkAttendance;
    RadioGroup radioGroup;
    RadioButton rbWifi,rbBluetooth;
    String network,courseCode;
    ClipboardManager clipboard;
    WifiManager wifiManager;
    BluetoothAdapter mBluetoothAdapter;
    SessionManager sessionManager;
    int row,column;
    EditText trow,tcolumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course_details);
        radioGroup=findViewById(R.id.rgNetwork);
        rbWifi=findViewById(R.id.rbWifi);
        trow=findViewById(R.id.etRow);
        tcolumn=findViewById(R.id.etColumn);
        rbBluetooth=findViewById(R.id.rbBluetooth);
        btnMarkAttendance=findViewById(R.id.btnMarkAttendence);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sessionManager = new SessionManager(getApplicationContext());
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(!Utils.isBlank(getIntent().getStringExtra("courseCode"))) {
            courseCode=getIntent().getStringExtra("courseCode");
        }
    }


    public void onClickBtnMarkAttendance(View view) {
        if (verify()) {
            row = Integer.parseInt(trow.getText().toString());
            column = Integer.parseInt(tcolumn.getText().toString());
            String name = courseCode.toUpperCase() + "_" + sessionManager.getKeyName().toUpperCase() + "_" + row + "_" + column + "_abc";
            if (radioGroup.getCheckedRadioButtonId() == rbBluetooth.getId()) {
                network = "bluetooth";
                mBluetoothAdapter.enable();
                mBluetoothAdapter.setName(name);
                Log.d("name",name);

            } else {
                network = "wifi";
                ClipData clip = ClipData.newPlainText("name", name);
                clipboard.setPrimaryClip(clip);
                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
            }
        }
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