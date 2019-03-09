package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Bluetooth_fun extends AppCompatActivity {
    BluetoothAdapter mBluetoothAdapter;
    Set<String> list = new HashSet<String>();
    String name="Test";
    VirtualMapHelper virtualMapHelper;
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Finding devices
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                list.add(device.getName());
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_fun);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

    }

    public void switchOnBluetooth(View view) {
        mBluetoothAdapter.enable();
        mBluetoothAdapter.setName(name);
    }

    public void scanBluetooth(View view) {
        Log.d("entered", "scanbluetooth");
        virtualMapHelper = new VirtualMapHelper();
        Iterator<String> i = list.iterator();
        ArrayList<String> students = new ArrayList<String>();
        while (i.hasNext()) {
            String student = i.next();
            Log.d("entered",student);
            students.add(student);
        }
        virtualMapHelper.Update(students);
        //should pass virtualmaphelper to the virtualmap class.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }
}
