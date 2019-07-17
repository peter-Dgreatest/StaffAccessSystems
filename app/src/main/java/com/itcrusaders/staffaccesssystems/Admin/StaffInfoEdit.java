package com.itcrusaders.staffaccesssystems.Admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;


import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.StaffMembersDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



public class StaffInfoEdit extends AppCompatActivity implements StaffMembersAdapter.StaffMembersAdapterOnClickHandler{
    private RecyclerView mRecyclerView;
    private StaffMembersAdapter mstaffMembersAdapter;
    private ProgressBar mLoadingIndicator;

    private String apiPath = ApiClass.getBaseUrl()+"stafflist/getall/";
    private ProgressDialog processDialog;
    private JSONArray restulJsonArray;
    private int success = 0;
    FloatingActionButton mAddStaff;


    TextView snackDisplayDIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_info_edit);


        snackDisplayDIv = findViewById(R.id.tv_error_message_display);

        Snackbar snackbar = Snackbar.make(snackDisplayDIv,
                "Grey background means staff member has been deactivated!",Snackbar.LENGTH_LONG);

        snackbar.show();

        mRecyclerView = (RecyclerView) findViewById(R.id.staffmembers_ry_view);
        // load all unsent messages from online db


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

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mAddStaff = findViewById(R.id.btn_add_staff_info);

        mAddStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffInfoEdit.this,StaffAddInfo.class);
                startActivity(intent);
            }
        });
        /* Once all of our views are setup, we can load the weather data. */
        loadMessagesData();
        //SQLConnection.connect();
    }

    @Override
    public void clickHandler(String Params) {


        Intent intent = new Intent(StaffInfoEdit.this, EditStaffInfo.class);
        intent.putExtra("id",Params);

        startActivity(intent);
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


    private void loadMessagesData() {
        new StaffInfoEdit.ServiceStubAsyncTask(this,this).execute();
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

            HttpConnectionService service = new HttpConnectionService(StaffInfoEdit.this);
            response = service.sendRequest(apiPath, postDataParams);
            try {
                success = 1;
                //Log.d("kllllllll",response);
                JSONObject resultJsonObject = new JSONObject(response);
                restulJsonArray = resultJsonObject.getJSONArray("output");
            } catch (JSONException e) {
                success = 0;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);


            if (processDialog.isShowing()) {
                try {
                    processDialog.dismiss();
                }catch (Exception e){

                }
            }

            if (success == 1) {
                if (null != restulJsonArray) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);




                    ArrayList<StaffMembersDAO> staffMembers = new ArrayList<>();
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            //Log.d("here1","sjhss");

                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            //Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            StaffMembersDAO messagesDAO =
                                    new StaffMembersDAO(jsonObject.get("staff_xxid").toString(),
                                            jsonObject.get("staff_xxname").toString(),
                                            jsonObject.get("staff_xxphone").toString(),
                                            jsonObject.get("staff_xxstatus").toString(),
                                            jsonObject.get("staff_xxpic").toString(),
                                            jsonObject.get("staff_xxlogin_time").toString());

                            messagesDAO.active = jsonObject.get("active").toString();
                            staffMembers.add(messagesDAO);

                            //Log.d("lalalal",jsonObject.get("staff_xxname").toString());

                            //listViewAdapter.add(jsonObject.get("name"));
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