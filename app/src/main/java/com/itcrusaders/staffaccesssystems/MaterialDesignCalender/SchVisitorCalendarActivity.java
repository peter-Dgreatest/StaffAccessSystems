package com.itcrusaders.staffaccesssystems.MaterialDesignCalender;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itcrusaders.staffaccesssystems.Admin.DateManipulation;
import com.itcrusaders.staffaccesssystems.Admin.VisitorDashboard;
import com.itcrusaders.staffaccesssystems.Admin.VisitorLoginRecords;
import com.itcrusaders.staffaccesssystems.Admin.VisitorsAdapterSch;
import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;
import com.itcrusaders.staffaccesssystems.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class SchVisitorCalendarActivity extends AppCompatActivity
        implements VisitorsAdapterSch.VisitorMembersAdapterOnClickHandler{

  TextView mNextMth, mPrevMth, mMth,mNoDataFound;
  String date2Send;

  private RecyclerView mRecyclerView;
  private VisitorsAdapterSch mvisitorMembersAdapter;

  private ProgressDialog processDialog;
  Context mContext;

  View view;
  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_vistor_calendar);

    Date date = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM");

    mNoDataFound = findViewById(R.id.tv_nodata_message_display);

    mNextMth = findViewById(R.id.next_att_mnth);
    mPrevMth = findViewById(R.id.prev_att_mnth);
    mMth = findViewById(R.id.att_mnth);

    // set the month number
    mMth.setTag(R.id.mnthcntTag, dateFormat.format(date));

    // set the full date text
    dateFormat = new SimpleDateFormat("dd MMM YYYY");
    mMth.setTag(R.id.fulldate, dateFormat.format(date));

    // set the Text in TextView
    mMth.setText(dateFormat.format(date));

    dateFormat = new SimpleDateFormat("dd");
    mMth.setTag(R.id.dayTag, dateFormat.format(date));

    dateFormat = new SimpleDateFormat("YYYY");
    mMth.setTag(R.id.yrTag, dateFormat.format(date));


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
  }

  private void setNewDate(String type){
    int day = Integer.parseInt(mMth.getTag(R.id.dayTag).toString());
    int month = Integer.parseInt(mMth.getTag(R.id.mnthcntTag).toString());
    int year = Integer.parseInt(mMth.getTag(R.id.yrTag).toString());

    Date date1 = new Date();
    SimpleDateFormat dateFormat1;

    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR,year);
    calendar.set(Calendar.MONTH,month-1);
    calendar.set(Calendar.DATE,day);


    if (type.equalsIgnoreCase("next")) {
      if (day == DateManipulation.DateCountNum(month,year)) {
        if (month == 12) {
          year++;// = month++;
          calendar.set(Calendar.YEAR,year);
          calendar.set(Calendar.MONTH,0);
          calendar.set(Calendar.DAY_OF_MONTH,1);

          //Log.d("Day incresin year:",day+" "+month);
        } else {
          calendar.set(Calendar.YEAR,year);
          calendar.set(Calendar.MONTH,month);
          calendar.set(Calendar.DAY_OF_MONTH,1);
          //Log.d("Day incresin month:",day+" "+month);
        }
        //Log.d("Day greater than :",day+" "+month);
      } else {
        calendar.set(Calendar.DAY_OF_MONTH,day+1);

        //Log.d("Day less than :",day+" "+month);
        //Log.d("Day incresin day:",day+" "+month);
      }
    }else if(type.equalsIgnoreCase("prev")){
      if (day == 1) {
        if (month == 1) {
          year--;// = month++;
          calendar.set(Calendar.YEAR,year);
          calendar.set(Calendar.MONTH,11);
          calendar.set(Calendar.DAY_OF_MONTH,31);
          //Log.d("Day reducing year:",day+" "+month);
        } else {
          calendar.set(Calendar.YEAR,year);
          calendar.set(Calendar.MONTH,month-2);
          calendar.set(Calendar.DAY_OF_MONTH,DateManipulation.DateCountNum(month-1,year));

        }

        //Log.d("Day li equals to :",day+" "+month);
      } else {
        calendar.set(Calendar.DAY_OF_MONTH,day-1);

        //Log.d("Day li less than :",day+" "+month);

        //Log.d("Day reducing day:",day+" "+month);
      }
    }

    dateFormat1 = new SimpleDateFormat("dd MMM yyyy");

    //Toast.makeText(getContext(),calendar.get(Calendar.MONTH)+" ", Toast.LENGTH_LONG).show();

    date1 =calendar.getTime();
    mMth.setText(dateFormat1.format(date1).toString());
    //Toast.makeText(getContext(), mMth.getTag(R.id.yrTag).toString(), Toast.LENGTH_LONG).show();


    dateFormat1 = new SimpleDateFormat("MM");
    // set the month number
    mMth.setTag(R.id.mnthcntTag, dateFormat1.format(date1));

    //Toast.makeText(getContext(), date1.toString(), Toast.LENGTH_LONG).show();

    // set the full date text
    dateFormat1 = new SimpleDateFormat("dd MMM YYYY");
    mMth.setTag(R.id.fulldate, dateFormat1.format(date1));

    // set the Text in TextView
    mMth.setText(dateFormat1.format(date1));

    dateFormat1 = new SimpleDateFormat("dd");
    mMth.setTag(R.id.dayTag, dateFormat1.format(date1));

    dateFormat1 = new SimpleDateFormat("YYYY");
    mMth.setTag(R.id.yrTag, dateFormat1.format(date1));


    date2Send = mMth.getTag(R.id.fulldate).toString();

    mvisitorMembersAdapter = new VisitorsAdapterSch(this);
    loadAttendance();
  }

  private void loadAttendance() {

    mRecyclerView = (RecyclerView) findViewById(R.id.rcyview_attendance);
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
    mvisitorMembersAdapter = new VisitorsAdapterSch(this);

    /* Setting the adapter attaches it to the RecyclerView in our layout. */
    mRecyclerView.setAdapter(mvisitorMembersAdapter);

    /*
     * The ProgressBar that will indicate to the user that we are loading data. It will be
     * hidden when no data is loading.
     *
     * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
     * circle. We didn't make the rules (or the names of Views), we just follow them.
     */

    mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

    /* Once all of our views are setup, we can load the weather data. */
    //loadMessagesData();
    //SQLConnection.connect();

    new getVisitorInfoFromInternet(this).execute(date2Send);

    linearLayout = findViewById(R.id.forsnack);
    Snackbar snackbar = Snackbar.make(linearLayout,
            "Grey background shows visitors' appointment that was denied\r" +
                    " blue background shows appointment that had been approved",Snackbar.LENGTH_LONG);

    snackbar.show();
  }

  LinearLayout linearLayout;
  private ProgressBar mLoadingIndicator;
  private String apiPath = ApiClass.getBaseUrl() + "visitorlist/getbydatesch/";
  private int success = 0;

  private JSONArray restulJsonArray;

  @Override
  public void clickHandler(String Params) {
      Intent intent = new Intent(SchVisitorCalendarActivity.this, VisitorDashboard.class);
      intent.putExtra("id",Params);

      startActivity(intent);
  }

  class getVisitorInfoFromInternet extends AsyncTask<String, Void, String> {

    String response = "";
    HashMap<String, String> postDataParams;



    public getVisitorInfoFromInternet(Context context) {
      mContext = context;
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
    protected String doInBackground(String... arg0) {

      postDataParams = new HashMap<String, String>();
      postDataParams.put("HTTP_ACCEPT", "application/json");
      ////Log.d("ddddd",arg0[0]);
      postDataParams.put("datee", arg0[0]);

      //Log.d("kllllllll",  arg0[0]+"lllkk");
      HttpConnectionService service = new HttpConnectionService(mContext);
      response = service.sendRequest(apiPath, postDataParams);
      try {
        success = 1;
        //Log.d("kllllllll", response.length()+"");
        ////Log.d("kllllllll",apiPath+idd);
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
          ArrayList<VisitorLoginRecords> visitorLoginRecords = new ArrayList<>();
          for (int i = 0; i < restulJsonArray.length(); i++) {
            try {
              ////Log.d("here1","sjhss");

              JSONObject jsonObject = restulJsonArray.getJSONObject(i);
              //Log.d("JSONINFO",jsonObject.toString()+"dddd");
              VisitorLoginRecords vistorDAO =
                      new VisitorLoginRecords(jsonObject.get("id").toString(),
                              jsonObject.get("name").toString(),
                              jsonObject.get("mobile").toString(),
                              jsonObject.get("appoin").toString(),
                              jsonObject.get("purpose").toString(),
                              jsonObject.get("pic").toString(),
                              jsonObject.get("church").toString(),
                              jsonObject.get("gGroup").toString(),
                              jsonObject.get("who2see").toString(),
                              jsonObject.get("appoinTime").toString());

              vistorDAO.vmail = jsonObject.get("vemail").toString();
              vistorDAO.appoindate = jsonObject.get("appoinDate").toString();

              visitorLoginRecords.add(vistorDAO);
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
          displayVisitorInfo(visitorLoginRecords);
          mNoDataFound.setVisibility(View.INVISIBLE);
        }
      }else mNoDataFound.setVisibility(View.VISIBLE);
    }
  }

  private void displayVisitorInfo(ArrayList<VisitorLoginRecords> visitorLoginRecords) {
    mvisitorMembersAdapter.setMessageData(visitorLoginRecords);
    mvisitorMembersAdapter.notifyDataSetChanged();
  }


}