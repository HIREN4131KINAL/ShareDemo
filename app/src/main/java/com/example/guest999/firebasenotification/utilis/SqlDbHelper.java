package com.example.guest999.firebasenotification.utilis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import static android.R.attr.version;

public class SqlDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FILE_SHARING";

    private static final String  DATABASE_FILE_PATH = String.valueOf(Environment.getExternalStorageDirectory());

    private static final String SCRIPT_CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS USER_LIST(_id INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,PHON TEXT,COUNT TEXT)";

    SqlDbHelper(Context context, String databaseName, Object o, int databaseVersion) {
        super(context,DATABASE_FILE_PATH + "/.PLdb/" + DATABASE_NAME, null , version);
        // TODO Auto-generated constructor stub
        //context.getExternalFilesDir(null).getAbsolutePath() + "/" + DATABASE_NAME, null, DATABASE_VERSION
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SCRIPT_CREATE_DATABASE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        onCreate(db);
    }

}
