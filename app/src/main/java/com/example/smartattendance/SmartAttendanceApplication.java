package com.example.smartattendance;

import android.app.Application;
import android.util.Log;


import io.paperdb.Paper;

public class SmartAttendanceApplication extends Application {
    private static SmartAttendanceApplication application;

    public static SmartAttendanceApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("enteredOncreate","hh");
        Paper.init(this);

        application=this;

    }


}
