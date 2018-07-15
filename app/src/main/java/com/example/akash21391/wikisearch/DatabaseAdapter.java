package com.example.akash21391.wikisearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    DbHelper mDbHelper;
    Context mContext;

    private static DatabaseAdapter instance;

    private DatabaseAdapter(Context context) {
        mContext = context;
        mDbHelper = new DbHelper(context);
    }

    public static void createInstance(Context context){
        instance = new DatabaseAdapter(context);
    }

    public static DatabaseAdapter getInstance() {
        return instance;
    }

    public long insert(List<WikiData> wikiDataList) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues cv;
        long id = 0;
        for (WikiData wikiData : wikiDataList) {
            cv = new ContentValues();
            cv.put(DbHelper.TITLE, wikiData.title);
            cv.put(DbHelper.WIKIDATA, wikiData.data);
            cv.put(DbHelper.WEBURL, wikiData.webUrl);
            id = db.insert(DbHelper.TABLE_NAME, null, cv);
        }
        db.close();
        return id;
    }

    public List<WikiData> getSearchData(String queryWord) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] columns = {DbHelper.TITLE,
                DbHelper.WIKIDATA, DbHelper.WEBURL};

        queryWord = "%" + queryWord + "%";

        String where = DbHelper.TITLE + " LIKE ?" + " OR " + DbHelper.WIKIDATA
                + " LIKE ?";
        String[] whereArgs = new String[]{queryWord, queryWord};

        Cursor cursor = db.query(DbHelper.TABLE_NAME, columns, where, whereArgs,
                null, null, null);
        ArrayList<WikiData> wikiDataArrayList = new ArrayList<>();
        WikiData wikiData;
        while (cursor.moveToNext()) {
            wikiData = new WikiData();
            wikiData.title = cursor.getString(0);
            wikiData.data = cursor.getString(1);
            wikiData.webUrl = cursor.getString(2);
            wikiDataArrayList.add(wikiData);
        }
        cursor.close();
        db.close();
        return wikiDataArrayList;
    }
}
