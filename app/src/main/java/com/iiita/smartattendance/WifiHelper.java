package com.iiita.smartattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WifiHelper extends AppCompatActivity {

//    TextView tv;
    private WifiManager wifiManager;
    private ListView listView;
    private Button startscan, stopscan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> finalList = new ArrayList<>();
    private ArrayList<String> newfinalList = new ArrayList<>();
    private ArrayAdapter adapter;
    Set<String> list = new HashSet<String>();
    Map<String, Integer> mp = new HashMap<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_helper);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);

        /*startscan = (Button) findViewById(R.id.button1);
        stopscan = (Button) findViewById(R.id.button2);*/
//        tv = findViewById(R.id.mytextText);

        listView = findViewById(R.id.wifiList);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);



        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, finalList);
        listView.setAdapter(adapter);

        startscan.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                if (!wifiManager.isWifiEnabled()) {
//                    Toast.makeText(this, "Enabling Wifi", Toast.LENGTH_LONG).show();
                    wifiManager.setWifiEnabled(true);
                }
                Scan();
            }
        });

        stopscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToast();
                wifiManager.setWifiEnabled(false);
                adapter.notifyDataSetChanged();
                filterresults();
            }
        });

    }
    private boolean checkRollno(String s)
    {
        if(s.length() != 10)
            return false;
        String course[] = {"IIT", "BIM", "IHM"};
        String year[] = {"2015", "2016", "2017", "2018"};
        String t;
        boolean validRoll = false, validYear = false, validNum = false;
        t = s.substring(0, 3);
        for (String x : course)
        {
            if(x.equals(t))
            {
                validRoll = true;
                break;
            }
        }
        t = s.substring(3, 7);
        for (String x : course)
        {
            if(x.equals(t))
            {
                validYear = true;
                break;
            }
        }
        t = s.substring(7, 10);
        for (int i = 0; i < t.length(); i++)
        {
            if(!(t.charAt(i) >= '0' && t.charAt(i) <= '9'))
            {
                validNum = false;
            }
        }
        return (validRoll && validYear && validNum);
    }

    private void filterresults() {

        for (String result : finalList)
        {
            String fields[] = result.split("_");
            Log.d("" + result, " " + result.length());
            if(fields.length != 4)
                continue;
            if(checkRollno(fields[1]))
            {
                newfinalList.add(result);
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
    public List<String> getFinalList() {
        return newfinalList;
    }


    private void scanWifi() {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
        wifiManager.startScan();
    }
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            //unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                if(!mp.containsKey(scanResult.SSID))
                {
                    mp.put(scanResult.SSID, 1);
                    finalList.add(scanResult.SSID);
                    Log.d("new Wifi", ""+scanResult.SSID);
                }
                Log.d("" + scanResult.SSID, "WIFI");
            }
        };
    };
    private void callToast() {
        Toast.makeText(this,"Disabling Wifi", Toast.LENGTH_SHORT).show();
    }

}
