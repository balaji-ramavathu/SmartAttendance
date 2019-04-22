package com.example.smartattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.paperdb.Paper;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.ceil;
import static java.lang.Math.log;

public class StudentCourseDetails extends AppCompatActivity {
    MaterialButton btnMarkAttendance;
    MaterialButton btnAddSheet;
    MaterialButton btnShowAttendance;
    RadioGroup radioGroup;
    RadioButton rbWifi, rbBluetooth;
    String network, courseCode;
    ClipboardManager clipboard;
    WifiManager wifiManager;
    BluetoothAdapter mBluetoothAdapter;
    SessionManager sessionManager;
    int row, column;
    EditText trow, tcolumn;
    ArrayList<dbCourseStudent> _dbCourse;
    dbCourseStudent CurrentCourse;

    /* For Google Sheets */
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    ProgressDialog mProgress;
    /* For Google Sheets */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course_details);
        if (!Utils.isBlank(getIntent().getStringExtra("courseCode"))) {
            courseCode = getIntent().getStringExtra("courseCode");
        }

        Toolbar toolbar = findViewById(R.id.toolbarCourseDetails);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(this.courseCode);

        radioGroup = findViewById(R.id.rgNetwork);
        rbWifi = findViewById(R.id.rbWifi);
        trow = findViewById(R.id.etRow);
        tcolumn = findViewById(R.id.etColumn);
        rbBluetooth = findViewById(R.id.rbBluetooth);
        btnMarkAttendance = findViewById(R.id.btnMarkAttendence);
        btnAddSheet = findViewById(R.id.btnaddsheet);
        btnShowAttendance = findViewById(R.id.btnshowattendance);
        _dbCourse = Paper.book().read("SCourses", new ArrayList<dbCourseStudent>());
        Log.d("update", courseCode);
        Log.d("update", Integer.toString(_dbCourse.size()));
        for (dbCourseStudent crse : _dbCourse) {
            if (crse.Course.equals(courseCode)) {
                Log.d("update", "Course initalised");
                this.CurrentCourse = crse;
                break;
            }
        }
        if (CurrentCourse.Spreadsheetlink == null) {
            btnAddSheet.setText("Add Sheet");
        } else {
            btnAddSheet.setText("Update Sheet");
        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sessionManager = new SessionManager(getApplicationContext());
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


        /* Google Sheets*/
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Updating Attendance");
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        /* Google Sheets*/
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

    public void onClickbtnShowAttendancec(View view) {
        if (this.CurrentCourse.Spreadsheetlink == null) {
            AlertDialogManager _dialog = new AlertDialogManager();
            _dialog.showAlertDialog(this, "Sheet not added", "Please add the sheet link first", false);
            return;
        }
        getResultsFromApi();
    }

    public void onClickbtnAddSheet(View view) {
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
                Log.d("name", name);

            } else {
                network = "wifi";
                ClipData clip = ClipData.newPlainText("name", name);
                clipboard.setPrimaryClip(clip);
                startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
            }
        }
    }

    private String GetSpreadsheetID(String link) {
        String[] components = link.split("/");
        int index = -1;
        for (int i = 0; i < components.length; ++i) {
            if (components[i].equals("d")) {
                index = i;
            }
        }
        if (index == -1 || index == components.length - 1) {
            return null;
        }
        return components[index + 1];
    }

    public void showDialog(String sheetlink) {

        final Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.update_sheet_dialog, null);
        final TextInputEditText tvSheetlink = view.findViewById(R.id.tvSheetLink);
        MaterialButton btnok = view.findViewById(R.id.btnaddsheetok);
        MaterialButton btncancel = view.findViewById(R.id.btnaddsheetcancel);
        if (sheetlink != null) {
            tvSheetlink.setText(sheetlink);
        }
        dialog.setContentView(view);
        dialog.show();
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sheetLink = tvSheetlink.getText().toString();
                String id = GetSpreadsheetID(sheetLink);
                if (id == null) {
                    Toast.makeText(getApplicationContext(), "Invalid link", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < _dbCourse.size(); ++i) {
                    if (_dbCourse.get(i).Course.equals(courseCode)) {
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
        if (TextUtils.isEmpty(trow.getText().toString())) {
            Toast.makeText(this, "Enter valid bench number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tcolumn.getText().toString())) {
            Toast.makeText(this, "Enter valid column number", Toast.LENGTH_SHORT).show();
            return false;
        }
        String _row = trow.getText().toString();
        String _col = tcolumn.getText().toString();
        for (int i = 0; i < _row.length(); i++) {
            if (!(_row.charAt(i) >= '0' && _row.charAt(i) <= '9')) {
                Toast.makeText(this, "Enter valid bench number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        for (int i = 0; i < _col.length(); i++) {
            if (!(_col.charAt(i) >= '0' && _col.charAt(i) <= '9')) {
                Toast.makeText(this, "Enter valid column number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        int irow = Integer.parseInt(_row);
        int icol = Integer.parseInt(_col);
        if (irow > 20 || irow < 0) {
            Toast.makeText(this, "Enter valid bench number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (icol > 4 || icol < 0) {
            Toast.makeText(this, "Enter valid column number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }








    /*For Google Sheets */

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            AlertDialogManager _dialog = new AlertDialogManager();
            _dialog.showAlertDialog(this, "No Internet", "To Create a Subject, Turn on Your Internet", false);
            Log.d("Spread", "No network connection available.");
        } else {
            new StudentCourseDetails.MakeRequestTask(mCredential, "Googleapp", this).execute();
            Log.d("Spread", "Successsss ");
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                StudentCourseDetails.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                Log.d("Spread", "chooseAccount:");
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
                Log.d("Spread", "chooseAccount: ::");
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.d("Spread", "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {
        Context context;
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private double Percentage = 0;
        int status;
        private Exception mLastError = null;

        // The constructor
        MakeRequestTask(GoogleAccountCredential credential, String title, Context mcontext) {
            this.context = mcontext;
            this.status = -1;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Android spreadsheet client")
                    .build();
        }

        protected Void doInBackground(Void... params) {

            // function to create the spreadsheet
            try {
                putDataFromApi();
            } catch (IOException e) {
                mLastError = e;
                cancel(true);
                e.printStackTrace();
            }

            return null;
        }

        private String getColumn(int num) {
            int mod = num % 26;
            if (mod == 0) {
                mod = 27;
            }
            int start = (int) ceil(num / 27);
            String ret = "";
            while (num > 0) {
                // Find remainder
                int rem = num % 26;
                // If remainder is 0, then a 'Z' must be there in output
                if (rem == 0) {
                    ret = ret + "Z";
                    num = (num / 26) - 1;
                } else {
                    char letter = (char) ((rem - 1) + 'A');
                    ret = ret + Character.toString(letter);
                    num = num / 26;
                }
            }
            return ret;
        }

        private void putDataFromApi() throws IOException {
            String spreadsheetId = GetSpreadsheetID(CurrentCourse.Spreadsheetlink);

            String range = "Sheet1";
            ValueRange result = mService.spreadsheets().values().get(spreadsheetId, range).execute();

            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            if (numRows == 0) {
                status = 1;
            } else {
                List<List<Object>> values = result.getValues();
                ArrayList<ArrayList<String>> values2 = new ArrayList<>();
                for (List<Object> row : values) {
                    ArrayList<String> tmp = new ArrayList<>();
                    for (Object val : row) {
                        tmp.add(val.toString());
                        Log.d("sheets", val.toString());
                    }
                    values2.add(tmp);
                }
                int rollnumberRow = -1;
                if (values.get(0).size() < 1) {
                    status = 1;

                } else {
                    for (int i = 0; i < values2.size(); ++i) {
                        if (values.get(i).get(0).equals(sessionManager.getKeyName().toUpperCase())) {
                            rollnumberRow = i;
                            break;
                        }
                    }
                    if (rollnumberRow == -1) {
                        status = 2;
                    } else {
                        status = 3;
                        double total = 0;
                        double attended = 0;
                        for (int i = 1; i < values2.get(rollnumberRow).size(); ++i) {
                            total += 1;
                            if (values2.get(rollnumberRow).get(i).equals("1"))
                                attended += 1;
                        }
                        if (total > 0) {
                            Percentage = attended / total * 100;
                        }
                        Log.d("sheets", String.format("%.2f", Percentage));
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
            mProgress.setMessage("Geting Attendance");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgress.hide();
//            runOnUiThread(new Runnable(){
//                public void run(){
            //show AlertDialog
            if (status == 1) {
                AlertDialogManager _dialog = new AlertDialogManager();
                _dialog.showAlertDialog(context, "Empty or Invalid sheet", "Sheet is either empty or invalid", false);
            }
            if (status == 2) {
                AlertDialogManager _dialog = new AlertDialogManager();
                _dialog.showAlertDialog(context, "Student not Found", "Roll Number : " + sessionManager.getKeyName() + ", not found in google sheets", false);
            }
            if (status == 3) {
                AlertDialogManager _dialog = new AlertDialogManager();
                _dialog.showAlertDialog(context, "Percentage", "Attendance Percentage is : " + String.format("%.2f", Percentage), true);
            }
//                }
//            });
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            Log.d("Spread", "onCancelled: ");
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            StudentCourseDetails.REQUEST_AUTHORIZATION);
                } else {
                    Log.d("Spread", "The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.d("Spread", "Request cancelled.");
            }
        }
    }
    /* For  Google Sheets */

}