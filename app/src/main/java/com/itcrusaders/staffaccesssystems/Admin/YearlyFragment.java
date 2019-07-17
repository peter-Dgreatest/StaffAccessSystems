package com.itcrusaders.staffaccesssystems.Admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.StaffMembersDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class YearlyFragment extends Fragment implements StaffAttendanceAdapter.StaffAttendanceAdapterOnClickHandler{

    TextView mNextMth, mPrevMth, mMth,mNoDataFound;
    String date2Send;

    private RecyclerView mRecyclerView;
    private StaffAttendanceAdapter mstaffMembersAdapter;

    private ProgressDialog processDialog;
    Context mContext;

    public YearlyFragment(){

    }

    public YearlyFragment newInstance(int sectionNumber, Context mContext1) {
        YearlyFragment fragment = new YearlyFragment();
        Bundle args = new Bundle();
        this.mContext = mContext1;
        fragment.setArguments(args);
        return fragment;
    }

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_staff_members_attendance,container,false);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY");


        mNoDataFound = view.findViewById(R.id.tv_nodata_message_display);

        mNextMth = view.findViewById(R.id.next_att_mnth);
        mPrevMth = view.findViewById(R.id.prev_att_mnth);
        mMth = view.findViewById(R.id.att_mnth);

        mMth.setTag(R.id.yrTag, dateFormat.format(date));


        mMth.setTag(R.id.fulldate, dateFormat.format(date));

        dateFormat = new SimpleDateFormat("YYYY");
        mMth.setTag(R.id.yrTag, dateFormat.format(date));


        mMth.setText(dateFormat.format(date));


        date2Send = mMth.getTag(R.id.fulldate).toString();


        loadAttendance();
        mNextMth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewDate("next");
            }
        });

        mPrevMth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNewDate("prev");
            }
        });
        return view;
    }

    private void setNewDate(String type) {
        int year = Integer.parseInt(mMth.getTag(R.id.yrTag).toString());

        Date date1;
        SimpleDateFormat dateFormat1;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);

        if (type.equalsIgnoreCase("next")) {
            year++;// = month++;
            calendar.set(Calendar.YEAR, year);
        }
        else if(type.equalsIgnoreCase("prev")) {
            year--;// = month++;
            calendar.set(Calendar.YEAR, year);
        }

        dateFormat1 = new SimpleDateFormat("yyyy");

        //Toast.makeText(getContext(),calendar.get(Calendar.MONTH)+" ", Toast.LENGTH_LONG).show();

        date1 =calendar.getTime();
        mMth.setText(dateFormat1.format(date1).toString());
        //Toast.makeText(getContext(), mMth.getTag(R.id.yrTag).toString(), Toast.LENGTH_LONG).show();

        // set the full date text
        dateFormat1 = new SimpleDateFormat("YYYY");
        mMth.setTag(R.id.fulldate, dateFormat1.format(date1));

        // set the Text in TextView
        mMth.setText(dateFormat1.format(date1));

        dateFormat1 = new SimpleDateFormat("YYYY");
        mMth.setTag(R.id.yrTag, dateFormat1.format(date1));


        date2Send = mMth.getTag(R.id.fulldate).toString();

        mstaffMembersAdapter = new StaffAttendanceAdapter(this);
        loadAttendance();

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
                    mNoDataFound.setVisibility(View.INVISIBLE);
                }
            }else mNoDataFound.setVisibility(View.VISIBLE);
        }
    }

    private void displayStaffInfo(ArrayList<StaffMembersDAO> staffMembers) {
        mstaffMembersAdapter.setMessageData(staffMembers);
        mstaffMembersAdapter.notifyDataSetChanged();
    }

}