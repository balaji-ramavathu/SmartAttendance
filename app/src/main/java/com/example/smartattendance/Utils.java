package com.example.smartattendance;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.smartattendance.model.DaoSession;

public class Utils {
    public static DaoSession getAppDaoSession(Context context) {
        return ((MainActivity) context.getApplicationContext()).getDaoSession();
    }

    public static DaoSession getDaoSession(Context context) {
        return MainActivity.daoSession;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isBlank(String str) {
        if (str != null && !str.equals("")) {
            return false;
        }
        return true;
    }
}
