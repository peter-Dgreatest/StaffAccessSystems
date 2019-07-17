package com.itcrusaders.staffaccesssystems;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itcrusaders.staffaccesssystems.MaterialDesignCalender.BasicActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class StaffDashboard extends AppCompatActivity {

    private ProgressDialog processDialog;
    private String apiPath = ApiClass.getBaseUrl()+"stafflist/get1/";
    StaffMembersDAO membersDAO;

    private int success = 0;

    de.hdodenhof.circleimageview.CircleImageView mStaffImageView;
    private JSONArray restulJsonArray;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        getWindow().setLayout((int) (width*0.8),(int) (height*0.7));




        mStaffInfo= findViewById(R.id.txt_staffInfo);
        mStaffName = findViewById(R.id.txt_staffname);

        mStaffImageView = findViewById(R.id.staff_img);

        mStaffSignIn = findViewById(R.id.btn_staffLogin);
        mStaffSignOut= findViewById(R.id.btn_staffLogout);
        mStaffAssign = findViewById(R.id.btn_staffAssign);
        mStaffAttendanceRecords = findViewById(R.id.btn_view_reports);

        if(!(getIntent() == null)) {
            Intent thisIntent = getIntent();
            id = thisIntent.getStringExtra("id");

        }

        mstaffID=id;
        retrieveStaffInfo(id);

    }

    private String mstaffID;

    class getStaffInfoFromInternet extends AsyncTask<String,Void, String> {

        private Context mContext;
        private Activity mActivity;
        String response = "";
        HashMap<String, String> postDataParams;

        public getStaffInfoFromInternet(Context context, Activity activity) {
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

            HttpConnectionService service = new HttpConnectionService(StaffDashboard.this);
            response = service.sendRequest(apiPath+idd, postDataParams);
            try {
                success = 1;
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
                try {
                    processDialog.dismiss();
                }catch (Exception e){

                }
            }

            if (success == 1) {
                if (null != restulJsonArray) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);


                    //ArrayList<StaffMembersDAO> staffMembers = new ArrayList<>();
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            //Log.d("here1","sjhss");

                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            //Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            StaffMembersDAO tmessagesDAO =
                                    new StaffMembersDAO(jsonObject.get("staff_xxid").toString(),
                                            jsonObject.get("staff_xxname").toString(),
                                            jsonObject.get("staff_xxphone").toString(),
                                            jsonObject.get("staff_xxstatus").toString(),
                                            jsonObject.get("staff_xxpic").toString(),
                                            jsonObject.get("staff_xxlogin_time").toString());
                            tmessagesDAO.staff_mail = jsonObject.get("staff_xxmail").toString();

                            membersDAO= tmessagesDAO;
                            //Log.d("lalalal",jsonObject.get("staff_xxname").toString());

                            //listViewAdapter.add(jsonObject.get("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    displayStaffInfo();
                }
            }
        }
    }

    public void retrieveStaffInfo(String id){
        new getStaffInfoFromInternet(this,this).execute(id);
    }

    public TextView mStaffName,mStaffInfo;
    public Button mStaffSignIn,mStaffSignOut,mStaffAssign,mStaffAttendanceRecords;

    public void displayStaffInfo(){



        mStaffName.setText(membersDAO.staff_name);

        new downloadImageFromNet().execute(ApiClass.getImageBaseUrl()+membersDAO.staff_pic);

        String info = "";
        if(membersDAO.login_status.toString().equalsIgnoreCase("in")){
            info= "Logged in since "+membersDAO.logintime;
            mStaffSignIn.setClickable(false);
            //Toast.makeText(StaffDashboard.this,info,Toast.LENGTH_LONG).show();
        }else if(membersDAO.login_status.toString().equalsIgnoreCase("out")){
            info= "Logged out since "+membersDAO.logintime;
            mStaffSignOut.setClickable(false);
            mStaffAssign.setClickable(false);
            //Toast.makeText(StaffDashboard.this,info,Toast.LENGTH_LONG).show();
        }else if(membersDAO.login_status.toString().equalsIgnoreCase("sp")){
            info= "Went on Special Assignment since "+membersDAO.logintime;
            mStaffSignOut.setClickable(false);
            mStaffAssign.setClickable(false);
            //Toast.makeText(StaffDashboard.this,info,Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(StaffDashboard.this,"here"+membersDAO.login_status+"haha",Toast.LENGTH_LONG).show();

       // mStaffInfo.setText("Hello");
        mStaffInfo.setText(info);

        bindButtons();

    }

    String staffId;

    public class downloadImageFromNet extends AsyncTask<String,Void, Bitmap>{

        StaffMembersAdapter.StaffMembersViewHolder viewHolder;

        @Override
        protected Bitmap doInBackground(String... lists) {
            String imageURL = lists[0];

            Bitmap bimage = null;
            ArrayList arrayList = new ArrayList();
            return getBitmapFromUrl(imageURL);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mStaffImageView.setImageBitmap(bitmap);
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


    public void bindButtons(){
        //String status = membersDAO.login_status;
        mStaffInfo= findViewById(R.id.txt_staffInfo);
        mStaffName = findViewById(R.id.txt_staffname);

        mStaffSignIn = findViewById(R.id.btn_staffLogin);
        mStaffSignOut= findViewById(R.id.btn_staffLogout);
        mStaffAssign = findViewById(R.id.btn_staffAssign);

        mStaffSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StaffDashboard.this,"Hello",Toast.LENGTH_LONG).show();
                if(!membersDAO.login_status.equalsIgnoreCase("in")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(StaffDashboard.this);

                    builder.setTitle("Send Verification Code :");
                    builder.setMessage("Proceed to Login ?");
                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSMSVerification("in");
                        }
                    });


                    builder.show();
                }
                else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(StaffDashboard.this);
                    builder2.setTitle("Invalid Request");
                    builder2.setMessage("User Already Logged In \n Not you? Contact Admin");

                    builder2.show();
                }
            }
        });


        mStaffSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!membersDAO.login_status.equalsIgnoreCase("out")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(StaffDashboard.this);

                    builder.setTitle("Send Verification Code :");
                    builder.setMessage("Proceed to Log Out ?");
                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSMSVerification("out");
                        }
                    });


                    builder.show();

                }
                else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(StaffDashboard.this);
                    builder2.setTitle("Invalid Request");
                    builder2.setMessage("User Already Logged Out \n Not you? Contact Admin");
                    builder2.show();
                }
            }
        });

        mStaffAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!membersDAO.login_status.equalsIgnoreCase("sp")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(StaffDashboard.this);

                    builder.setTitle("Send Verification Code :");
                    builder.setMessage("Proceed to Lodge Special Assignment?");
                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSMSVerification("sp");
                        }
                    });

                    builder.show();
                }
                else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(StaffDashboard.this);
                    builder2.setTitle("Invalid Request");
                    builder2.setMessage("User Already left for special Assignment \n Not you? Contact Admin");
                    builder2.show();
                }
            }
        });
        mStaffAttendanceRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDashboard.this,BasicActivity.class);
                //Log.d("staffId",mstaffID);
                intent.putExtra("staffId",mstaffID);
                startActivity(intent);
            }
        });
    }

    private void openSMSVerification(String login) {
        Intent intent = new Intent(StaffDashboard.this,SMSVerificationActivity.class);
        intent.putExtra("logtype",login);
        intent.putExtra("staffId",membersDAO.staffId);
        intent.putExtra("staffName",mStaffName.getText());
        intent.putExtra("staffMobile",membersDAO.staff_phone);
        intent.putExtra("staffMail",membersDAO.staff_mail);


        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.staff_view_att, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.staff_view_att_menu) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
