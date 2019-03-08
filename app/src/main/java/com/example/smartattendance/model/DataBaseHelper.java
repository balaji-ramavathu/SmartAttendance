package com.example.smartattendance.model;

import android.content.Context;
import android.util.Log;


import org.greenrobot.greendao.database.Database;

public class DataBaseHelper extends DaoMaster.OpenHelper {
    private DaoSession daoSession;

    public DataBaseHelper(Context context, String name) {
        super(context, name);
    }

    public DaoSession getDaoSession(Context context) {
        return daoSession;
    }

    @Override
    public void onCreate(Database db) {
        Log.i("greenDAO", "Creating tables for schema version " + DaoMaster.SCHEMA_VERSION);
        DaoMaster.createAllTables(db, true);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.i("greenDAO", "migrating schema from version " + oldVersion + " to " + newVersion);
        //dropAllTables(db, true);
        for (int migrateVersion = oldVersion + 1; migrateVersion <= newVersion; migrateVersion++) {
            upgrade(db, migrateVersion);
        }
    }


    private void upgrade(Database db, int migrateVersion) {
        Log.e("MIGRATE VERSION", "" + migrateVersion);
        /*switch (migrateVersion) {
            case 2:
                db.execSQL("ALTER TABLE send_product ADD COLUMN 'lang' TEXT NOT NULL DEFAULT 'fr';");
                break;
        }*/
    }


}
