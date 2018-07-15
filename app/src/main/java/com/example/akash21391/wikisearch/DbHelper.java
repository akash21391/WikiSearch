package com.example.akash21391.wikisearch;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "wikidatabase";
    public static final String TABLE_NAME = "DATATABLE";
    private static final int DATABASE_VERSION = 1;
    private static final String UID = "_id";
    public static final String TITLE = "Title";
    public static final String WIKIDATA = "WikiData";
    public static final String WEBURL = "WebUrl";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + UID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " VARCHAR(255), "
            + WIKIDATA + " VARCHAR(255), " + WEBURL + " VARCHAR(255));";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        android.util.Log.i(TAG, "DatabaseHelper onCreate called");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        android.util.Log.i(TAG, "DatabaseHelper onUpgrade called");
        onCreate(db);
    }
}
