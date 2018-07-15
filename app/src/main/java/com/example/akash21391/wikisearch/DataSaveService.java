package com.example.akash21391.wikisearch;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

public class DataSaveService extends IntentService {
    private static final String ACTION_SAVE_DATA_IN_DB = "com.example.akash21391.wikisearch.action.savedata";
    private static final String DATA_LIST = "datalist";

    public DataSaveService() {
        super("DataSaveService");
    }

    public static void saveDataInDB(Context context, ArrayList<WikiData> wikiDataArrayList) {
        Intent intent = new Intent(context, DataSaveService.class);
        intent.setAction(ACTION_SAVE_DATA_IN_DB);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA_LIST, wikiDataArrayList);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_DATA_IN_DB.equals(action)) {
                Bundle b = intent.getExtras();
                ArrayList<WikiData> wikiDataArrayList = (ArrayList<WikiData>) b.getSerializable(DATA_LIST);
                DatabaseAdapter.getInstance().insert(wikiDataArrayList);
            }
        }
    }
}
