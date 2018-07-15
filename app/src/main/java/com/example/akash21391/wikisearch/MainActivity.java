package com.example.akash21391.wikisearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    SearchView mSearchView;
    RecyclerView mRecyclerView;

    WikiAdapter mAdapter;
    ArrayList<WikiData> wikiDataList = new ArrayList<>();
    TextView emptyTextView;

    private static ConnectivityManager mConnManager;

    private static final String WIKI_SEARCH_URL = "https://en.wikipedia.org/w/api.php?action=opensearch&search=";
    private static final String NAMESPACE_ARG = "&namespace=0";
    private static final String FORMAT_JSON_ARG = "&format=json";
    private static final String LIMIT_ARG = "&limit=100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchView = findViewById(R.id.search_view);
        mSearchView.setIconifiedByDefault(false);
        mRecyclerView = findViewById(R.id.recycler_view);
        emptyTextView = findViewById(R.id.empty_text);
        mConnManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(MainActivity.this, VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
        mAdapter = new WikiAdapter(mRecyclerView, MainActivity.this, wikiDataList);
        mRecyclerView.setAdapter(mAdapter);
        DatabaseAdapter.createInstance(MainActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.trim();
                if (!TextUtils.isEmpty(query)) {
                    if (isNetworkConnected()) {
                        new SearchOnlineTask(MainActivity.this).execute(query);
                    } else {
                        new SearchOfflineTask(MainActivity.this).execute(query);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public static boolean isNetworkConnected() {
        NetworkInfo networkInfo = mConnManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            android.util.Log.d(TAG, "Network Connected : true)");
            return true;
        }
        android.util.Log.d(TAG, "Network Connected : false)");
        return false;
    }

    private class SearchOfflineTask extends AsyncTask<String, Void, Void> {

        Context mContext;
        ProgressDialog progressDialog;

        SearchOfflineTask(Context context) {
            mContext = context;
            progressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Searching Offline");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String query = params[0];
            ArrayList<WikiData> temp = (ArrayList<WikiData>) DatabaseAdapter.getInstance().getSearchData(query);
            wikiDataList.clear();
            wikiDataList.addAll(temp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (wikiDataList.size() > 0) {
                emptyTextView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private class SearchOnlineTask extends AsyncTask<String, Void, Void> {

        Context mContext;
        ProgressDialog progressDialog;

        SearchOnlineTask(Context context) {
            mContext = context;
            progressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Searching");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            InputStream inputStream = downloadSearchData(url);
            String jsonString = convertStreamToString(inputStream);
            parseJsonString(jsonString);
            return null;
        }

        private void parseJsonString(String jsonStr) {
            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);

                    JSONArray titleArray = jsonArray.getJSONArray(1);
                    JSONArray dataArray = jsonArray.getJSONArray(2);
                    JSONArray urlArray = jsonArray.getJSONArray(3);

                    wikiDataList.clear();
                    // looping through All Contacts
                    for (int i = 1; i < titleArray.length(); i++) {
                        WikiData wikiData = new WikiData();
                        wikiData.title = titleArray.getString(i);
                        wikiData.data = dataArray.getString(i);
                        wikiData.webUrl = urlArray.getString(i);
                        wikiDataList.add(wikiData);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
        }

        private String convertStreamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }

        public InputStream downloadSearchData(String query) {

            StringBuilder sb = new StringBuilder(WIKI_SEARCH_URL).append(query).append(LIMIT_ARG).append(NAMESPACE_ARG).append(FORMAT_JSON_ARG);
            InputStream inputStream = null;

            try {
                URL url = new URL(sb.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(2500);
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                int response = urlConnection.getResponseCode();
                Log.d(TAG, "The response is: " + response);
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (wikiDataList.size() > 0) {
                emptyTextView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            }
            mAdapter.notifyDataSetChanged();
            DataSaveService.saveDataInDB(mContext, wikiDataList);
        }
    }
}
