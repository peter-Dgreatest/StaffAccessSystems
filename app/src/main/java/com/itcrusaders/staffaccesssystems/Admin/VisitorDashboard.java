package com.itcrusaders.staffaccesssystems.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.SMSVerificationActivity;
import com.itcrusaders.staffaccesssystems.VisitorsDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.N)
public class VisitorDashboard extends AppCompatActivity {
    private ProgressDialog processDialog;
    private String apiPath = ApiClass.getBaseUrl()+"visitorlist/get1/";

    private int success = 0;

    de.hdodenhof.circleimageview.CircleImageView mVisitorImageView;
    private JSONArray restulJsonArray;
    String id,appId,phone,name,vmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_dashboard);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*0.8),(int) (height*0.7));


        mVisitorInfo= findViewById(R.id.txt_vInfo);
        mVisitorName = findViewById(R.id.txt_vname);

        mVisitorImageView = findViewById(R.id.staff_img);

        mVisitorApprove = findViewById(R.id.btn_staffLogin);
        mVisitorDeny= findViewById(R.id.btn_staffLogout);
        mVisitorPostPhone = findViewById(R.id.btn_staffAssign);

        if(!(getIntent() == null)) {
            Intent thisIntent = getIntent();
            id = thisIntent.getStringExtra("id");

        }

        mstaffID=id;
        retrieveVisitorInfo(id);

    }

    private String mstaffID;

    class getVisitorInfoFromInternet extends AsyncTask<String,Void, String> {

        private Context mContext;
        private Activity mActivity;
        String response = "";
        HashMap<String, String> postDataParams;

        public getVisitorInfoFromInternet(Context context, Activity activity) {
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
        protected String doInBackground(String... arg0) {

            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");
            String idd = arg0[0];

            HttpConnectionService service = new HttpConnectionService(VisitorDashboard.this);
            response = service.sendRequest(apiPath+idd, postDataParams);
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
        protected void onPostExecute(String result) {

            super.onPostExecute(result);


            if (processDialog.isShowing()) {
                processDialog.dismiss();
            }

            if (success == 1) {
                if (null != restulJsonArray) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);


                    ArrayList<VisitorLoginRecords> staffMembers = new ArrayList<>();
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            //Log.d("here1","sjhss");

                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            //Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            VisitorLoginRecords messagesDAO =
                                    new VisitorLoginRecords(jsonObject.get("id").toString(),
                                            jsonObject.get("transId").toString(),
                                            jsonObject.get("name").toString(),
                                            jsonObject.get("mobile").toString(),
                                            jsonObject.get("pic").toString());

                            messagesDAO.vmail = jsonObject.get("vemail").toString();
                            staffMembers.add(messagesDAO);
                            //Log.d("lalalal",jsonObject.get("staff_xxname").toString());

                            //listViewAdapter.add(jsonObject.get("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    displayVisitorInfo(staffMembers.get(0));
                }
            }
        }
    }

    public void retrieveVisitorInfo(String id){
        new VisitorDashboard.getVisitorInfoFromInternet(this,this).execute(id);
    }

    public TextView mVisitorName,mVisitorInfo;
    public Button mVisitorApprove,mVisitorDeny,mVisitorPostPhone;

    public void displayVisitorInfo(VisitorLoginRecords staff){



        mVisitorName.setText(staff.vname);

        new VisitorDashboard.downloadImageFromNet().execute(ApiClass.getImageBaseUrl()+"rest/"+staff.vimgsrc);

        String info = staff.vmobile;
        phone=staff.vmobile;
        name=staff.vname;
        appId=staff.appId;
        vmail=staff.vmail;
        //Toast.makeText(VisitorDashboard.this,"here"+staff.login_status+"haha",Toast.LENGTH_LONG).show();

        // mVisitorInfo.setText("Hello");
        mVisitorInfo.setText(info);

        bindButtons(staff.vname,staff.vid,staff.appId);

    }

    String staffId;

    public class downloadImageFromNet extends AsyncTask<String,Void, Bitmap>{

       
        @Override
        protected Bitmap doInBackground(String... lists) {
            String imageURL = lists[0];

            Bitmap bimage = null;
            ArrayList arrayList = new ArrayList();
            return getBitmapFromUrl(imageURL);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mVisitorImageView.setImageBitmap(bitmap);
        }
    }

    public static Bitmap getBitmapFromUrl(String filePath)
    {
        Bitmap img = null;

        try{
            InputStream inputStream = new java.net.URL(filePath).openStream();
            img = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.d("dddfff",e.toString());
        }

        return img;
    }


    public void bindButtons(final String status, final String staffId,String appId){

        mVisitorApprove = findViewById(R.id.btn_staffLogin);
        mVisitorDeny= findViewById(R.id.btn_staffLogout);
        mVisitorPostPhone = findViewById(R.id.btn_staffAssign);

        mVisitorApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(VisitorDashboard.this,"Hello",Toast.LENGTH_LONG).show();
                if(!status.equalsIgnoreCase("in")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(VisitorDashboard.this);

                    builder.setTitle("Approve Visitor :"+mVisitorName.getText().toString());
                    builder.setMessage("Proceed ?");
                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                openSMSVerification("approve",staffId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    builder.show();
                }
                else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(VisitorDashboard.this);
                    builder2.setTitle("Invalid Request");
                    builder2.setMessage("User Already Logged In \n Not you? Contact Admin");

                    //builder2.show();
                }
            }
        });


        mVisitorDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences =getSharedPreferences("com.itcrusaders.staffaccesssystems.Admin",
                        MODE_PRIVATE);
                String pastorLog = sharedPreferences.getString("adminlogin2","");

                if(pastorLog.equalsIgnoreCase("success")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(VisitorDashboard.this);

                    builder.setTitle("Deny "+mVisitorName.getText().toString()+" Visitor's Appointment :");
                    builder.setMessage("Proceed ?");
                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                openSMSVerification("deny",staffId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    builder.show();

                }
                else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(VisitorDashboard.this);
                    builder2.setTitle("Invalid Request");
                    builder2.setMessage("Only Pastor has the ability to deny appointments \n Not you? Contact Admin");
                    builder2.show();
                }
            }
        });

        mVisitorPostPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences =getSharedPreferences("com.itcrusaders.staffaccesssystems.Admin",
                        MODE_PRIVATE);
                String pastorLog = sharedPreferences.getString("adminlogin2","");

                if(pastorLog.equalsIgnoreCase("success")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(VisitorDashboard.this);

                    builder.setTitle("Reschedule "+mVisitorName.getText().toString()+" appointment :");
                    builder.setMessage("Proceed?");
                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DatePickerDialog(VisitorDashboard.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                            // openSMSVerification("sp",staffId);
                        }
                    });
                    builder.show();


                }
                else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(VisitorDashboard.this);
                    builder2.setTitle("Invalid Request");
                    builder2.setMessage("Only Pastor has the ability to Reschedule appointments \n Not you? Contact Admin");
                    builder2.show();
                }
            }
        });

    }

    final Calendar myCalendar = Calendar.getInstance();

    final Calendar c = Calendar.getInstance();

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(DatePicker view, int year, final int monthOfYear,
                              final int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //myCalendar.set(Calendar.)
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(VisitorDashboard.this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            c.set(Calendar.MINUTE,minute);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd, MMMM");
                            SimpleDateFormat timFormat = new SimpleDateFormat("h:mma");

                            /**AlertDialog.Builder builder = new AlertDialog.Builder(VisitorDashboard.this);
                            builder.setMessage(mVisitorName.getText().toString()+" visitor's appointment rescheduled for : \r"+
                                        dateFormat.format(myCalendar.getTime())+" @ "+timFormat.format(c.getTime()));
                            builder.setTitle("Appointment Rescheduled");
                            builder.show();\

                             **/
                            ArrayList<String> _2upload = new ArrayList<String>();
                            _2upload.add(id);
                            _2upload.add(appId);
                            _2upload.add(name);
                            _2upload.add(phone);
                            _2upload.add(vmail);
                            _2upload.add(dateFormat.format(myCalendar.getTime()).toString());
                            _2upload.add(timFormat.format(c.getTime()).toString());

                            try {
                                new VisitorsAdminManipulation(VisitorDashboard.this,
                                                                VisitorDashboard.this,
                                                                _2upload).execute();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
            updateLabel();
        }

    };

    private void updateLabel() {
    }

    private void openSMSVerification(String login, String staffId) throws JSONException {
        ArrayList<String> _2upload = new ArrayList<String>();
        _2upload.add(id);
        _2upload.add(appId);
        _2upload.add(name);
        _2upload.add(phone);
        _2upload.add(vmail);
        new VisitorsAdminManipulation(this,this,_2upload,login).execute();
    }
}
