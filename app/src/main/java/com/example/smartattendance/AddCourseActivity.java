package com.example.smartattendance;

import androidx.annotation.Nullable;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.card.MaterialCardView;
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
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class AddCourseActivity extends AppCompatActivity {
    TextInputEditText etCourseCode, etCourseName;
    ImageButton addRoll;
    ListView lvRolls;
    ArrayList<RollNumber> rolls;
    ArrayList<dbCourse> _dbCourse;
    ArrayList<dbCourseStudent> _SdbCourse;
    dbCourse _dbCourseSingle;
    dbCourseStudent _SdbCourseSingle;
    AddRollsArrayAdapter arrayAdapter;
    int OPENED_COUNT;
    /* For google sheets */
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    ArrayList <String> _rollnumbers;
    ProgressDialog mProgress;
    String CourseCode;
    SessionManager sessionManager;
    MaterialCardView cardView;
    /* For Google Sheets */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        Toolbar toolbar = findViewById(R.id.toolbarAddCourse);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        cardView=findViewById(R.id.cvStudentDetails);
        etCourseCode = findViewById(R.id.etCourseCode);
        etCourseName = findViewById(R.id.etCourseName);
        sessionManager =new SessionManager(getApplicationContext());
        if(sessionManager.getProfession().equals("student")) {
            cardView.setVisibility(View.GONE);
            this.OPENED_COUNT=0;
            _SdbCourse = Paper.book().read("SCourses", new ArrayList<dbCourseStudent>());
        } else {
            addRoll = findViewById(R.id.btnAddRoll);
            lvRolls = findViewById(R.id.lvRolls);
            rolls = new ArrayList<>();
            //first empty item
            RollNumber rollNumber = new RollNumber();
            rolls.add(rollNumber);


            arrayAdapter = new AddRollsArrayAdapter(this, R.layout.add_rolls_item, rolls);
            lvRolls.setAdapter(arrayAdapter);
            _dbCourse = Paper.book().read("Courses", new ArrayList<dbCourse>());
            Log.d("newDB", _dbCourse.size()+"");
            /* For Google Sheets */
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            _rollnumbers = new ArrayList<String>();
            this.OPENED_COUNT = 0;
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Creating Google Sheet");

        }




        /* For Google Sheets */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickBtnAddRoll(View view) {
        RollNumber rollNumber = new RollNumber();
        rolls.add(rollNumber);
        arrayAdapter.notifyDataSetChanged();
        Utils.hideKeyboard(this);

    }
    String makeValidroll(int num){
        String _num = Integer.toString(num);
        if(_num.length() == 1){
            return "00"+_num;
        }
        if(_num.length() == 2){
            return "0"+_num;
        }
        return _num;
    }
    public void onClickFabAddCourseOk(View view) {
        if(sessionManager.getProfession().equals("teacher")) {
            if(OPENED_COUNT == 0) {
                View v;
                EditText etSuffix, etFrom, etTo;
                if (Utils.isBlank(etCourseCode.getText().toString())) {
                    Toast.makeText(this, "Course Code should not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                this.CourseCode = etCourseCode.getText().toString().toUpperCase();
                //thisCourse.setCourseCode(etCourseCode.getText().toString().toUpperCase());
                if (Utils.isBlank(etCourseName.getText().toString())) {
                    Toast.makeText(this, "Course Name should not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                String _courseName = etCourseName.getText().toString().toUpperCase();
                //thisCourse.setCourseName(etCourseName.getText().toString().toUpperCase());
                int count = lvRolls.getChildCount();
                int notNullChildrenCount = count;
                Log.d("Roll", Integer.toString(count));
                ArrayList<String> Suffix = new ArrayList<String>();
                ArrayList<Integer> From = new ArrayList<Integer>();
                ArrayList<Integer> To = new ArrayList<Integer>();
                for (int i = 0; i < count; i++) {
                    v = lvRolls.getChildAt(i);
                    etSuffix = v.findViewById(R.id.etSuffix);
                    etFrom = v.findViewById(R.id.etRollFrom);
                    etTo = v.findViewById(R.id.etRollTo);
                    String suffix = etSuffix.getText().toString().toUpperCase();
                    String from = etFrom.getText().toString();
                    String to = etTo.getText().toString();
                    if (!(Utils.isBlank(suffix)||Utils.isBlank(from) || Utils.isBlank(to))) {
                        Suffix.add(suffix);
                        From.add(Integer.parseInt(from));
                        To.add(Integer.parseInt(to));
                        RollNumber rollNumber = new RollNumber(suffix,Integer.parseInt(from),Integer.parseInt(to));
                        Log.d("roll",suffix+from+to);
                        // rolls.add(rollNumber);
                    } else {
                        notNullChildrenCount--;
                    }
                }
                ArrayList<dbRollnumber> _dbRollnumber= new ArrayList<dbRollnumber>();
                for (int i = 0; i < Suffix.size(); i++) {
                    for (int j = From.get(i); j <= To.get(i); j++) {
                        _dbRollnumber.add(new dbRollnumber(Suffix.get(i).toUpperCase() + makeValidroll(j), 0));
                        _rollnumbers.add(Suffix.get(i).toUpperCase() + makeValidroll(j));
                    }
                }
                Collections.sort(_rollnumbers);
                //Log.d("entered", "Roll_size" + Integer.toString(thisRolls.size()));

                Log.d("Rol", Integer.toString(_rollnumbers.size()));
                _dbCourseSingle = new dbCourse(this.CourseCode,_courseName, _dbRollnumber);
                OPENED_COUNT++;
            }
            getResultsFromApi(_rollnumbers);
        } else {
            if (OPENED_COUNT == 0) {
                View v;
                EditText etSuffix, etFrom, etTo;
                if (Utils.isBlank(etCourseCode.getText().toString())) {
                    Toast.makeText(this, "Course Code should not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("text", " " + etCourseCode.getText());
                this.CourseCode = etCourseCode.getText().toString().toUpperCase();
                if (Utils.isBlank(etCourseName.getText().toString())) {
                    Toast.makeText(this, "Course Name should not be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                String _courseName = etCourseName.getText().toString().toUpperCase();
                _SdbCourseSingle = new dbCourseStudent();
                _SdbCourseSingle.Course = CourseCode;
                _SdbCourseSingle.name = _courseName;
                _SdbCourse.add(_SdbCourseSingle);
                Paper.book().write("SCourses", _SdbCourse);
                OPENED_COUNT++;
                Intent returnIntent = new Intent();
                returnIntent.putExtra("refresh", true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        }


    }

    /* For Google Sheets */

    private void getResultsFromApi(ArrayList<String> _rollnumbers) {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            AlertDialogManager _dialog = new AlertDialogManager();
            _dialog.showAlertDialog(this,"No Internet","To Create a Subject, Turn on Your Internet", false);
        } else {
            new AddCourseActivity.MakeRequestTask1(mCredential, this.CourseCode, _rollnumbers).execute();
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
                AddCourseActivity.this,
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
                getResultsFromApi(this._rollnumbers);
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
                    getResultsFromApi(this._rollnumbers);
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
                        getResultsFromApi(this._rollnumbers);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi(this._rollnumbers);
                }
                break;
        }
    }


    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class MakeRequestTask1 extends AsyncTask<Void, Void, Void> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;
        com.google.api.services.sheets.v4.model.Spreadsheet mSpreadsheet;
        String title;
        ArrayList <String> rollno;
        // The constructor
        MakeRequestTask1(GoogleAccountCredential credential, String title, ArrayList<String> _rollnumber) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Android spreadsheet client")
                    .build();
            this.title = title;
            this.rollno = _rollnumber;
        }

        protected Void doInBackground(Void... params) {

            // function to create the spreadsheet
            try {
                Log.d("Spread", "Entering Background");
                createSpreadSheet(title);
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
            com.google.api.services.sheets.v4.model.Spreadsheet newSpreadSheet;
            mSpreadsheet = new Spreadsheet();
            SpreadsheetProperties spreadsheetProperties = new SpreadsheetProperties();
            spreadsheetProperties.setTitle(title);// name of your spreadsheet
            mSpreadsheet = mSpreadsheet.setProperties(spreadsheetProperties);
            newSpreadSheet = mService.spreadsheets()
                    .create(mSpreadsheet)
                    .execute();

            Log.d("Spread", "SpreadSheetID: "+ newSpreadSheet.getSpreadsheetId());
            // this 'newSpreadsheet' is ready to use for write/read operation.

            //adding roll numbers


            String spreadsheetId=newSpreadSheet.getSpreadsheetId();
//            thisCourse.setSpreadSheetId(spreadsheetId);
            _dbCourseSingle.spreadsheetID = spreadsheetId;
            _dbCourse.add(_dbCourseSingle);

            String range = "Sheet1!A1:A"+Integer.toString(rollno.size()+1);

            List<List<Object>> data;
            ArrayList<List<Object>> _tmp = new ArrayList<List<Object>>();
            List<Object> __tmp = new ArrayList<>();
            __tmp.add("Roll Number");
            _tmp.add(__tmp);
            for(Object roll : rollno){
                __tmp = new ArrayList<>();
                __tmp.add(roll);
                _tmp.add(__tmp);
            }
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
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgress.hide();
            Paper.book().write("Courses", _dbCourse);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("refresh", true);
            setResult(Activity.RESULT_OK, returnIntent);
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
                            AddCourseActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.d("Spread","The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.d("Spread","Request cancelled.");
            }
        }
    }

    /* For Google Sheets */
}
