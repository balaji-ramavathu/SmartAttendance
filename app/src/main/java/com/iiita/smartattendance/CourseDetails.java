package com.iiita.smartattendance;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import androidx.appcompat.widget.Toolbar;
import io.paperdb.Paper;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.sheets.v4.SheetsScopes;

import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;


public class CourseDetails extends AppCompatActivity {
    Button btngooglesheets;

    MaterialButton btnTakeAttendance;
    RadioGroup radioGroup;
    RadioButton rbWifi,rbBluetooth;
    String network;
    String courseCode;
    ArrayList<dbCourse> _dbCourse;
    ArrayList<dbAttendance> _dbAttendance;
    /* For Google Sheets */
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    CustomArrayAdapter arrayAdapter;
    ArrayList<String> al;
    ProgressDialog mProgress;
    String updateDate;
    String updateSpreadsheetID;
    ArrayList<String> updateAttendance;
    int updateSyncedct;
    int updateIndex;
    int updateWeight;
    /* For Google Sheets */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        if(!Utils.isBlank(getIntent().getStringExtra("courseCode"))) {
            this.courseCode=getIntent().getStringExtra("courseCode");
        }


        Toolbar toolbar = findViewById(R.id.toolbarCourseDetails);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(this.courseCode);


        al = new ArrayList<String>();
        radioGroup=findViewById(R.id.rgNetwork);
        rbWifi=findViewById(R.id.rbWifi);
        rbBluetooth=findViewById(R.id.rbBluetooth);
        btnTakeAttendance=findViewById(R.id.btnTakeAttendence);

        _dbCourse = Paper.book().read("Courses", new ArrayList<dbCourse>());
        _dbAttendance = Paper.book().read("Attendance", new ArrayList<dbAttendance>());
        this.updateSyncedct = 0;
        for(dbAttendance element : _dbAttendance){
            if(element.isSynced == 0 && element.courseId.equals(this.courseCode)){
                al.add(element.date);
            }
            else{
                this.updateSyncedct++;
            }
        }
        for(dbCourse element : _dbCourse){
            if(element.courseid.equals(courseCode)){
                this.updateSpreadsheetID = element.spreadsheetID;
            }
        }
        arrayAdapter = new CustomArrayAdapter(this, R.layout.list_item_buttons, al,courseCode);
        ListView listView = (ListView) findViewById(R.id.datelist);
        listView.setAdapter(arrayAdapter);
        /* Google Sheets */
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Updating Attendance");
        updateAttendance = new ArrayList<String>();
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        btngooglesheets = findViewById(R.id.googlesheets);
        btngooglesheets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<dbCourse> _dbCourse = Paper.book().read("Courses", new ArrayList<dbCourse>());
                String spreadsheeturl = "https://docs.google.com/spreadsheet/";
                for(dbCourse ele: _dbCourse){
                    if(ele.courseid.equals(courseCode)){
                        spreadsheeturl ="https://docs.google.com/spreadsheets/d/"+ele.spreadsheetID+"/edit#gid=0";
                    }
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(spreadsheeturl));
                startActivity(browserIntent);
                Log.d("Spread", "Account" + mCredential.getSelectedAccountName());

            }
        });
        /* Google Sheets */

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

    public void UpdateSheets(View v){


        //reset all the listView items background colours
        //before we set the clicked one..

        LinearLayout parent = (LinearLayout)v.getParent();
        LinearLayout pparent = (LinearLayout)parent.getParent();
        TextView c = (TextView)pparent.getChildAt(0);
        updateDate = c.getText().toString();
        int _id = 0;
        updateAttendance.clear();
        for(dbAttendance element: _dbAttendance){
            if(element.date.equals(updateDate) && element.isSynced == 0){
                updateIndex = _id;
                updateWeight = element.weight;
                for(dbRollnumber element2 : element.rollnumbers){
                    updateAttendance.add(Integer.toString(element2.isPresent));
                }
                break;
            }
            _id++;
        }

        getResultsFromApi();
    }
    public void onClickBtnTakeAttendance(View view) {

        if(radioGroup.getCheckedRadioButtonId()==rbBluetooth.getId()) {
            network="bluetooth";
            /*Log.d("enteredBle","bluetoot");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);*/
        } else {
            network="wifi";
        }

        Intent intent=new Intent(CourseDetails.this,VirtualMap.class);
        intent.putExtra("network",network);
        if(courseCode!=null) {
            intent.putExtra("courseCode",courseCode);
        }
        startActivityForResult(intent, 2);


    }


    /*For Google Sheets */

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            AlertDialogManager _dialog = new AlertDialogManager();
            _dialog.showAlertDialog(this,"No Internet","To Create a Subject, Turn on Your Internet", false);
            Log.d("Spread", "No network connection available.");
        } else {
            new MakeRequestTask(mCredential, "Googleapp").execute();
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
                CourseDetails.this,
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
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    Boolean refresh = data.getExtras().getBoolean("refresh");
                    Log.d("VMAPreturn", refresh + "");
                    if (refresh) {
                        /*courseList.clear();
                        courseList.addAll(courseDao.loadAll());*/
                        ArrayList<dbAttendance> _tmpat = new ArrayList<dbAttendance>();
                        ArrayList<String> _tmpdate = new ArrayList<String>();
                        for(dbAttendance element : _dbAttendance){
                            _tmpat.add(element);
                            if(element.isSynced == 0){
                                _tmpdate.add(element.date);
                            }
                            else{
                                this.updateSyncedct++;
                            }
                        }

                        Intent i = new Intent(CourseDetails.this,CourseDetails.class);
                        i.putExtra("courseCode", courseCode);
                        startActivity(i);
                        finish();
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        // The constructor
        MakeRequestTask(GoogleAccountCredential credential, String title) {
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
            }
            catch (IOException e) {
                mLastError = e;
                cancel(true);
                e.printStackTrace();
            }

            return null;
        }

        private String getColumn(int num){
            int mod = num % 26;
            if(mod == 0){
                mod = 27;
            }
            int start = (int) ceil(num / 27);
            String ret = "";
            while (num>0) {
                // Find remainder
                int rem = num%26;
                // If remainder is 0, then a 'Z' must be there in output
                if (rem==0){
                    ret = ret+"Z";
                    num = (num/26)-1;
                }
                else{
                    char letter = (char)((rem-1) + 'A');
                    ret = ret + Character.toString(letter);
                    num = num/26;
                }
            }
            return ret;
        }

        private void putDataFromApi() throws IOException {
            String spreadsheetId=updateSpreadsheetID;
            String range = "Sheet1";
            ValueRange result = mService.spreadsheets().values().get(spreadsheetId, range).execute();
            int numRows = result.getValues() != null ? result.getValues().size() : 0;
            int columnsFilled = 0;
            if(numRows > 0){
                List<List<Object>> values = result.getValues();
                for(List<Object> row : values){
                    columnsFilled = max(columnsFilled, row.size());
                }
            }
            range = "Sheet1!"+getColumn(columnsFilled+1)+"1:"+getColumn(columnsFilled+1+updateWeight-1)+Integer.toString(updateAttendance.size()+1);
            Log.d("sheetsUpdate",range);
            List<List<Object>> data;
            ArrayList<List<Object>> _tmp = new ArrayList<List<Object>>();
            List<Object> __tmp = new ArrayList<>();
            for(int i = 0; i < updateWeight; ++i) {
                __tmp.add(updateDate);
            }
            _tmp.add(__tmp);
            for(Object roll : updateAttendance){
                __tmp = new ArrayList<>();
                for(int i = 0; i < updateWeight; ++i) {
                    __tmp.add(roll);
                }
                Log.d("sheetsUpdate SZ",Integer.toString(__tmp.size()));
                _tmp.add(__tmp);
            }

            Log.d("sheetsUpdate",Integer.toString(_tmp.size()));
            data = _tmp;
            ValueRange body = new ValueRange()
                    .setValues(data);
            List<String> results = new ArrayList<String>();
            UpdateValuesResponse response = this.mService.spreadsheets().values()
                    .update(spreadsheetId, range,body)
                    .setValueInputOption("RAW")
                    .execute();
            Log.d("Spread", spreadsheetId+"for adding");
            results.add(response.getUpdatedCells().toString());
        }
        @Override
        protected void onPreExecute() {
            mProgress.show();
            mProgress.setMessage("Updating Attendance");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgress.hide();
            _dbAttendance.get(updateIndex).isSynced = 1;
            Paper.book().write("Attendance", _dbAttendance);
            Intent returnIntent = new Intent(CourseDetails.this,CourseDetails.class);
            returnIntent.putExtra("courseCode", courseCode);
            startActivity(returnIntent);
            finish();

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
                            CourseDetails.REQUEST_AUTHORIZATION);
                } else {
                    Log.d("Spread","The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.d("Spread","Request cancelled.");
            }
        }
    }
    /* For  Google Sheets */
}