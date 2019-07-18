package com.iiita.smartattendance;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class Utils {


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

    public static String getDate() {
        DateFormat df = new android.text.format.DateFormat();
        return df.format("dd/MM/yyyy", new java.util.Date()).toString();

    }
}
