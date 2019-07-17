package com.itcrusaders.staffaccesssystems;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class StaffLogin extends AppCompatActivity implements StaffMembersAdapter.StaffMembersAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private StaffMembersAdapter mstaffMembersAdapter;

    private String apiPath = ApiClass.getBaseUrl()+"stafflist/getallactive/";
    private ProgressDialog processDialog;
    private JSONArray restulJsonArray;
    private int success = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);


        mRecyclerView = findViewById(R.id.staffmembers_ry_view);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        // CompletedTODO (11) Pass in 'this' as the ForecastAdapterOnClickHandler
        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */

        mstaffMembersAdapter = new StaffMembersAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mstaffMembersAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */

        /* Once all of our views are setup, we can load the weather data. */
        loadMessagesData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mstaffMembersAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mstaffMembersAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public void clickHandler(String Params) {
        Intent intent = new Intent(StaffLogin.this, StaffDashboard.class);
        intent.putExtra("id",Params);

        startActivity(intent);
    }

    private void loadMessagesData() {
        new ServiceStubAsyncTask(this,this).execute();
    }

    private class ServiceStubAsyncTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private Activity mActivity;
        String response = "";
        HashMap<String, String> postDataParams;

        public ServiceStubAsyncTask(Context context, Activity activity) {
            mContext = context;
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(mContext);
            processDialog.setMessage("Please  Wait ...");
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");

            HttpConnectionService service = new HttpConnectionService(StaffLogin.this);
            response = service.sendRequest(apiPath, postDataParams);
            try {
                success = 1;
                //Log.d("kllllllll",response);
                JSONObject resultJsonObject = new JSONObject(response);
                restulJsonArray = resultJsonObject.getJSONArray("output");
            } catch (Exception e) {
                success = 0;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (processDialog.isShowing()) {
                processDialog.dismiss();
            }

            if (success == 1) {
                if (null != restulJsonArray) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);
                    ArrayList<StaffMembersDAO> staffMembers = new ArrayList<>();
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            //Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            StaffMembersDAO messagesDAO =
                                    new StaffMembersDAO(jsonObject.get("staff_xxid").toString(),
                                            jsonObject.get("staff_xxname").toString(),
                                            jsonObject.get("staff_xxphone").toString(),
                                            jsonObject.get("staff_xxstatus").toString(),
                                            jsonObject.get("staff_xxpic").toString(),
                                            jsonObject.get("staff_xxlogin_time").toString());
                            staffMembers.add(messagesDAO);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mstaffMembersAdapter.setMessageData(staffMembers);
                }
            }
        }
    }//end of async task
}