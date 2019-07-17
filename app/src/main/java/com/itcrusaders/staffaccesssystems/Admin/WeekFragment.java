package com.itcrusaders.staffaccesssystems.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.StaffMembersDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class WeekFragment extends Fragment implements StaffAttendanceAdapter.StaffAttendanceAdapterOnClickHandler{

    TextView mNextMth, mPrevMth, mMth;
    String date2Send;

    private RecyclerView mRecyclerView;
    private StaffAttendanceAdapter mstaffMembersAdapter;

    private ProgressDialog processDialog;
    Context mContext;

    public WeekFragment(){

    }

    public WeekFragment newInstance(int sectionNumber, Context mContext1) {
        WeekFragment fragment = new WeekFragment();
        Bundle args = new Bundle();
        this.mContext = mContext1;
        fragment.setArguments(args);
        return fragment;
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.activity_staff_members_attendance,container,false);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");


        mNextMth = view.findViewById(R.id.next_att_mnth);
        mPrevMth = view.findViewById(R.id.prev_att_mnth);
        mMth = view.findViewById(R.id.att_mnth);
        mMth.setTag(R.id.mnthTag, dateFormat.format(date));
        dateFormat = new SimpleDateFormat("YYYY");
        mMth.setTag(R.id.yrTag, dateFormat.format(date));
        dateFormat = new SimpleDateFormat("MM");
        mMth.setTag(R.id.mnthcntTag, dateFormat.format(date));
        date2Send = mMth.getTag(R.id.mnthTag).toString() + " " + mMth.getTag(R.id.yrTag);


        loadAttendance();
        mNextMth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, mMth.getTag(R.id.yrTag).toString(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void loadAttendance() {

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rcyview_attendance);
        // load all unsent messages from online db


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

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
        mstaffMembersAdapter = new StaffAttendanceAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mstaffMembersAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */

        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the weather data. */
        //loadMessagesData();
        //SQLConnection.connect();

        new getStaffInfoFromInternet(mContext).execute(date2Send);
    }

    private ProgressBar mLoadingIndicator;
    private String apiPath = ApiClass.getBaseUrl() + "stafflist/getbydate/";
    private int success = 0;

    private JSONArray restulJsonArray;

    @Override
    public void clickHandler(String Params) {

    }

    class getStaffInfoFromInternet extends AsyncTask<String, Void, String> {

        String response = "";
        HashMap<String, String> postDataParams;


        public getStaffInfoFromInternet(Context context) {
            //mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(getContext());
            processDialog.setMessage("Please  Wait ...");
            processDialog.setCancelable(false);
            processDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");
            //Log.d("ddddd",arg0[0]);
            postDataParams.put("datee", arg0[0]);

            HttpConnectionService service = new HttpConnectionService(getContext());
            response = service.sendRequest(apiPath, postDataParams);
            try {
                success = 1;
                Log.d("kllllllll", response);
                //Log.d("kllllllll",apiPath+idd);
                JSONObject resultJsonObject = new JSONObject(response);
                restulJsonArray = resultJsonObject.getJSONArray("output");
            } catch (JSONException e) {
                success = 0;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);


            if (processDialog.isShowing()) {
                processDialog.dismiss();
            }

            if (success == 1) {
                if (null != restulJsonArray) {
                    ArrayList<StaffMembersDAO> staffMembers = new ArrayList<>();
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            //Log.d("here1","sjhss");

                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            StaffMembersDAO messagesDAO =
                                    new StaffMembersDAO(
                                            jsonObject.get("staff_xxname").toString(),
                                            jsonObject.get("staff_xxphone").toString(),
                                            Integer.parseInt(jsonObject.get("dd").toString()), 0,
                                            jsonObject.get("staff_xxid").toString());

                            staffMembers.add(messagesDAO);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    displayStaffInfo(staffMembers);
                }
            }
        }
    }

    private void displayStaffInfo(ArrayList<StaffMembersDAO> staffMembers) {
        mstaffMembersAdapter.setMessageData(staffMembers);
    }

}