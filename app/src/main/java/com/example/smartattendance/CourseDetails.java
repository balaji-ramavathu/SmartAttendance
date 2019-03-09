package com.example.smartattendance;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseDetails extends AppCompatActivity {
    Button btngooglesheets;

    MaterialButton btnTakeAttendance;
    RadioGroup radioGroup;
    RadioButton rbWifi,rbBluetooth;
    String network,courseCode;

    /* For Google Sheets */
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    /* For Google Sheets */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        ArrayList<String> al = new ArrayList<String>();
        radioGroup=findViewById(R.id.rgNetwork);
        rbWifi=findViewById(R.id.rbWifi);
        rbBluetooth=findViewById(R.id.rbBluetooth);
        btnTakeAttendance=findViewById(R.id.btnTakeAttendence);
        if(!Utils.isBlank(getIntent().getStringExtra("courseCode"))) {
            courseCode=getIntent().getStringExtra("courseCode");
        }




        for(int i = 0; i < 20; i++)
        {
            al.add(i+"-12-19");
        }

        CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this, R.layout.list_item_buttons, al,courseCode);
        ListView listView = (ListView) findViewById(R.id.datelist);
        listView.setAdapter(arrayAdapter);
        /* Google Sheets */
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        btngooglesheets = findViewById(R.id.googlesheets);
        btngooglesheets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Spread", "Button clicked");
                getResultsFromApi();
                Log.d("Spread", "Account" + mCredential.getSelectedAccountName());

            }
        });
        /* Google Sheets */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*View parentView=(View) parent.getParent();*/
                Log.d("entered","sj");
                TextView dateView=(TextView) view.findViewById(R.id.date);
                String date=dateView.getText().toString();
                Log.d("date",date);
            }
        });
    }

    public void UpdateSheets(View v){


        //reset all the listView items background colours
        //before we set the clicked one..

        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        Button btnChild = (Button)vwParentRow.getChildAt(1);
        Log.d("childbutton", child.getText().toString());
        vwParentRow.refreshDrawableState();
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
        startActivity(intent);

    }


    /*For Google Sheets */

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
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
        String title;
        // The constructor
        MakeRequestTask(GoogleAccountCredential credential, String title) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Android spreadsheet client")
                    .build();
            this.title = title;
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

        // creates a new spreadsheet
        private void createSpreadSheet(String title) throws IOException {
            com.google.api.services.sheets.v4.model.Spreadsheet mSpreadsheet, newSpreadSheet;
            mSpreadsheet = new Spreadsheet();
            SpreadsheetProperties spreadsheetProperties = new SpreadsheetProperties();
            spreadsheetProperties.setTitle(title);// name of your spreadsheet
            mSpreadsheet = mSpreadsheet.setProperties(spreadsheetProperties);


            newSpreadSheet = mService.spreadsheets()
                    .create(mSpreadsheet)
                    .execute();
            Log.d("Spread", "SpreadSheetID: "+ newSpreadSheet.getSpreadsheetId());
            // this 'newSpreadsheet' is ready to use for write/read operation.
        }

        private void putDataFromApi() throws IOException {
            //String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
            String spreadsheetId="14ERBKI3hb1MFwhCJCyv7t5p03hkCapA7zG06zjgooMY";
            String range = "Sheet1!A1:E1";



            List<List<Object>> data = Arrays.asList(
                    Arrays.asList(
                            (Object) "Roll Number","IIT2016085","IIT2016086","IIT2016087","IIT2016088"
                    )
            );
            ValueRange body = new ValueRange()
                    .setValues(data);
            List<String> results = new ArrayList<String>();
            UpdateValuesResponse response = this.mService.spreadsheets().values()
                    .update(spreadsheetId, range,body)
                    .setValueInputOption("RAW")
                    .execute();

            results.add(response.getUpdatedCells().toString());
        }
        @Override
        protected void onCancelled() {
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