package com.itcrusaders.staffaccesssystems.MaterialDesignCalender;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.decorators.CIrcleDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Shows off the most basic usage
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class BasicActivity extends AppCompatActivity {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    @BindView(R.id.textView)
    TextView textView;

    Context mContext;

    String staffId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ButterKnife.bind(this);

        //widget.setOnDateChangedListener(this);
        //widget.setOnDateLongClickListener(this);
        //widget.setOnMonthChangedListener(this);

        widget= findViewById(R.id.calendarView);
        //Setup initial text
        textView= findViewById(R.id.textView);
        textView.setText("No Selection");

        if(!(getIntent() == null)) {
            Intent thisIntent = getIntent();
            staffId = thisIntent.getStringExtra("staffId");
        }
        mContext=this;
        new getDatesPresent().execute();

        Log.d("lekus",staffId+"jmm");

    }

    private ProgressDialog processDialog;
    private String apiPath = ApiClass.getBaseUrl()+"stafflist/getdates/";

    private int success = 0;

    public class getDatesPresent extends AsyncTask<Void,Void,Void>{

        private Activity mActivity;
        String response = "";
        HashMap<String, String> postDataParams;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(mContext);
            processDialog.setMessage("Please  Wait ...");
            processDialog.setCancelable(false);
            processDialog.show();

        }
        private JSONArray restulJsonArray;


        @Override
        protected Void doInBackground(Void... voids) {
            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");
            postDataParams.put("staffId", staffId);

            HttpConnectionService service = new HttpConnectionService(BasicActivity.this);
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (processDialog.isShowing()) {
                processDialog.dismiss();
            }

            if (success == 1) {
                if (null != restulJsonArray) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);


                    ArrayList<CalendarDay> staffDates = new ArrayList<>();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                    LocalDate localDate;

                    //List<EventDay> events = new ArrayList<>();

                    MaterialCalendarView calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
                    //calendarView.setEvents(events);
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            //Log.d("here1","sjhss");

                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            localDate = LocalDate.parse(jsonObject.getString("date").toString(),formatter);
                            //Calendar calendar = Calendar.getInstance();
                            //calendar.set(localDate.getYear(),localDate.getMonthValue()-1,localDate.getDayOfMonth());
                            //events.add(new EventDay(calendar, R.drawable.selector));
                            CalendarDay calendar = CalendarDay.from(localDate);
                            staffDates.add(calendar);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    displayStaffDates(staffDates);
                }
            }
        }
    }

    private void displayStaffDates(ArrayList<CalendarDay> staffDates) {



        widget.addDecorator(new CIrcleDecorator(this,R.drawable.selector,staffDates));

    }
}
