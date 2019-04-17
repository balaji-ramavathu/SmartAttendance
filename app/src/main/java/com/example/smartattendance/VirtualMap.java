package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.paperdb.Paper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.lang.StrictMath.max;

public class VirtualMap extends AppCompatActivity {
    int rows,columns;
    RecyclerView.Adapter adapter;
    MaterialButton btnScan;
    ImageButton btnAddRoll;
    TextInputEditText etAddRoll;
    HorizontalScrollView scrollView;
    RecyclerView recyclerView;
    BluetoothAdapter mBluetoothAdapter;
    Set<String> bluetoothList = new HashSet<String>();

    String network,courseCode;
    WifiManager wifiManager;
    int count;
    TextView tvProgress;
    TextInputEditText etWeight;
    VirtualMapHelper helper;

//  Testing Virtual Map
    VirtualMapHelper virtualMapHelper;
    ArrayList<ArrayList<ArrayList<Student>>> VMap;

    private List<ScanResult> results;
    Set<String> wifiList = new HashSet<String>();
    ArrayList<String> newWifiList = new ArrayList<String>();
    Encoder tool;


    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Finding devices
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                bluetoothList.add(device.getName());
            }

        }
    };

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            Log.d("entered","wifireciever:"+results.size());
            for (ScanResult scanResult : results) {
                wifiList.add(scanResult.SSID);
                Log.d("entered" , "" + scanResult.SSID + "$");
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_map);
        VirtualMapHelper helper=new VirtualMapHelper();
        ArrayList<Integer> columnWidth=helper.getColumnWidth();
        network=getIntent().getStringExtra("network");
        if(!Utils.isBlank(getIntent().getStringExtra("courseCode"))) {
            courseCode=getIntent().getStringExtra("courseCode");
        }
        btnScan=findViewById(R.id.btnScan);
        btnAddRoll=findViewById(R.id.btnAddRoll);
        etAddRoll=findViewById(R.id.etAddRoll);

        count=0;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        if(network.equals("bluetooth")) {

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            switchOnBluetooth();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
        }
        else if(network.equals("wifi")) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }


        recyclerView=findViewById(R.id.rvVirtualMap);
        tvProgress=findViewById(R.id.tvProgress);
        etWeight=findViewById(R.id.etWeight);
        virtualMapHelper = new VirtualMapHelper();
        tool = new Encoder();
    }

    public void switchOnBluetooth() {
        mBluetoothAdapter.enable();
        mBluetoothAdapter.setName("test");
    }
    public void switchOffBluetooth() {
        mBluetoothAdapter.disable();
    }

    public void startBluetoothScanning() {
        switchOnBluetooth();
        mBluetoothAdapter.startDiscovery();
    }

    public void stopBluetoothScanning() {
        Log.d("entered", "scanbluetooth");
        Iterator<String> i = bluetoothList.iterator();
        ArrayList<String> students = new ArrayList<String>();
        while (i.hasNext()) {
            String next = i.next();
            if(!Utils.isBlank(next)) {
                String student = next;
                Log.d("entered",student);
                //condition to be added
                //student = tool.Decode(student);
                if(student.contains(courseCode)) {
                    students.add(student);
                }
            }
        }

        virtualMapHelper.Update(students);
        switchOffBluetooth();
    }
    public void startWifiScanning() {
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Enabling Wifi", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        Scan();
    }
    private boolean check_row_col(String row, String col)
    {
        for (int i = 0; i < row.length(); i++)
        {
            if(!(row.charAt(i) >= '0' && row.charAt(i) <= '9'))
            {
                return false;
            }
        }
        for (int i = 0; i < col.length(); i++)
        {
            if(!(col.charAt(i) >= '0' && col.charAt(i) <= '9'))
            {
                return false;
            }
        }
        return true;
    }
    private void filterresults() {

        Log.d("entered","filterresults");
        for (String result : wifiList) {
            result = tool.Decode(result);
            Log.d("entered",result);
            String fields[] = result.split("_");
            Log.d("entered ", " " + result + " $");
            if(fields.length != 4)
                continue;
            Log.d("entered" + result, "valid");
            if(check_row_col(fields[2], fields[3]) && fields[0].equals(courseCode)) {
                newWifiList.add(result);
            }
        }
    }
    public void Scan(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(wifiManager.isWifiEnabled()) {
                    scanWifi();
                    Scan();
                    Log.d("timeloop", "every 7 seconds");
                }
                else
                {
                    Log.d("Stopped", "gone");
                }
            }
        }, 7*1000);
    }
    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
        wifiManager.startScan();
    }
    private void stopWifiScanning() {
        wifiManager.setWifiEnabled(false);
        filterresults();
        Log.d("stopwifiscan", "checker" + newWifiList.size());
        for (String s : newWifiList){
            Log.d("newboom", "name" + s);
        }
//        newWifiList.add("SMAT330C_IIT2016001_1_1_abc");
//        newWifiList.add("SMAT330C_IIT2016001_1_4_abc");
//        newWifiList.add("SMAT330C_IIT2016001_4_1_abc");
//        newWifiList.add("SMAT330C_IIT2016001_4_4_abc");
//        newWifiList.add("SMAT330C_IIT2016001_1_3_abc");
//        newWifiList.add("SMAT330C_IIT2016001_2_3_abc");
//        newWifiList.add("SMAT330C_IIT2016002_1_3_abc");
//        newWifiList.add("SMAT330C_IIT2016002_2_3_abc");

        virtualMapHelper.Update(newWifiList);
        VMap = virtualMapHelper.getVMap();
        Log.d("rows", " " + virtualMapHelper.getMaxRow());
        Log.d("rows", " " + virtualMapHelper.getMaxColumn());

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        if (network.equals("bluetooth")) {
            unregisterReceiver(mReceiver);
        }
//        else {
//            unregisterReceiver(wifiReceiver);
//        }
    }


    public void onClickBtnScan(View view) {
        if(network.equals("bluetooth")) {
            count = 1 - count;
            if (count == 1) {
                tvProgress.setText("Started Scanning");
                tvProgress.setTextColor(getResources().getColor(R.color.colorAccent));
                btnScan.setText("Stop Scanning");
                startBluetoothScanning();
            } else {
                tvProgress.setText("Stopped");
                tvProgress.setTextColor(getResources().getColor(R.color.dark_green));
                btnScan.setText("Start Scanning");
                stopBluetoothScanning();

                setAdapter();
            }
        }
        else {
            count = 1 - count;
            if (count == 1) {
                tvProgress.setText("Started Scanning");
                tvProgress.setTextColor(getResources().getColor(R.color.colorAccent));
                btnScan.setText("Stop Scanning");
                startWifiScanning();
            } else {
                tvProgress.setText("Stopped");
                tvProgress.setTextColor(getResources().getColor(R.color.dark_green));
                btnScan.setText("Start Scanning");
                stopWifiScanning();
                Log.d("Size : ",newWifiList.size()+"");
                for (int i=0;i<newWifiList.size();i++) {
                    Log.d("newWifiList : ",newWifiList.get(i));
                }
                rows = max(1, virtualMapHelper.getMaxRow());
                columns = max(1, virtualMapHelper.getColumnWidthSize());
                GridLayoutManager layoutManager = new GridLayoutManager(this, rows, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new VirtualMapAdapter(VirtualMap.this, this, rows, columns, virtualMapHelper, courseCode);
                recyclerView.setAdapter(adapter);
            }
        }
    }
    public int getWeight() {
        return Integer.valueOf(etWeight.getText().toString());
    }
    public void onClickSaveAttendance(View view) {
        List<String> presentRolls=virtualMapHelper.getPresentStudents();

        ArrayList<dbCourse> _dbCourse = Paper.book().read("Courses",new ArrayList<dbCourse>());
        ArrayList<dbRollnumber> enrolledRollNumbers = new ArrayList<dbRollnumber>();
        for(dbCourse ele : _dbCourse){
            if(ele.courseid.equals(courseCode)){
                enrolledRollNumbers = ele.rollnumbers;
            }
        }
        String date=Utils.getDate();
        int weight=getWeight();
        dbAttendance attendance=new dbAttendance();
        attendance.courseId=courseCode;
        attendance.date=date;
        attendance.weight=weight;
        attendance.isSynced=0;

        Log.d("sheetUpdateVM","from courses" + Integer.toString(enrolledRollNumbers.size()));
        for(int i = 0; i < enrolledRollNumbers.size(); ++i){
            for(int j = i; j < enrolledRollNumbers.size(); ++j){
                if(enrolledRollNumbers.get(i).rollnumber.compareTo(enrolledRollNumbers.get(j).rollnumber) > 0){
                    dbRollnumber _tmp = enrolledRollNumbers.get(i);
                    enrolledRollNumbers.set(i, enrolledRollNumbers.get(j));
                    enrolledRollNumbers.set(j, _tmp);
                }
            }
        }


        for(int i=0;i<enrolledRollNumbers.size();i++) {
            for(int j=0;j<presentRolls.size();j++) {
                if(enrolledRollNumbers.get(i).rollnumber.equals(presentRolls.get(j))) {
                    enrolledRollNumbers.get(i).isPresent = 1;
                }
            }
        }

        attendance.rollnumbers = enrolledRollNumbers;
        Log.d("sheetUpdateVMAP",Integer.toString(enrolledRollNumbers.size()));
        ArrayList<dbAttendance> _at = Paper.book().read("Attendance", new ArrayList<dbAttendance>());
        _at.add(attendance);
        Paper.book().write("Attendance", _at);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("refresh", true);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void setAdapter() {
        Log.d("entered","setAdapter");
        rows = max(1, virtualMapHelper.getMaxRow());
        columns = max(1, virtualMapHelper.getColumnWidthSize());
        GridLayoutManager layoutManager = new GridLayoutManager(this, rows, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new VirtualMapAdapter(VirtualMap.this, this, rows, columns, virtualMapHelper,courseCode);
        recyclerView.setAdapter(adapter);
    }


}


