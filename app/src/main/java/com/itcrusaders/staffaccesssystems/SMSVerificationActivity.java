package com.itcrusaders.staffaccesssystems;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.HashMap;

public class SMSVerificationActivity extends AppCompatActivity {

    String loginStatus,staffId,todo,staffName,info,staffMobile,staffMail;
    Button verificationButton;
    EditText EdtverificationCode;
    private ProgressDialog processDialog;
    private JSONObject restulJsonObject;

    private String apiPath = ApiClass.getBaseUrl()+"stafflist/set1/";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("smsent","no");


        savedInstanceState.putString("smsent","yes");
    }

    private int sendt =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*0.8),(int) (height*0.4));

        Intent intent = getIntent();
        loginStatus = intent.getStringExtra("logtype");
        staffId = intent.getStringExtra("staffId");
        staffName = intent.getStringExtra("staffName");
        staffMobile = intent.getStringExtra("staffMobile");
        staffMail = intent.getStringExtra("staffMail");

        SMSManipulation smsManipulation = new SMSManipulation(this,staffId,loginStatus,staffName,staffMobile,staffMail);

        Log.d("savedInsta", String.valueOf(savedInstanceState));
        if(savedInstanceState==null) {
            smsManipulation.sendMsg();
            //String info ="";
            //String todo="";
        }else{
            String smsent = savedInstanceState.getString("smsent");
            if (smsent.equalsIgnoreCase("no"))
                smsManipulation.sendMsg();
            /*
            String info ="";
            String todo="";
            */
        }
        verificationButton = findViewById(R.id.button_proceed);
        EdtverificationCode = findViewById(R.id.edt_verification_code);


        if(loginStatus.equalsIgnoreCase("in")){
            info= "Login";
            todo="in";
        }else if(loginStatus.equalsIgnoreCase("out")){
            info= "Logout";
            todo="out";
        }else if(loginStatus.equalsIgnoreCase("sp")){
            info= "Proceed";
            todo="sp";
        }

        verificationButton.setText(info);

        verificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationcode = EdtverificationCode.getText().toString();
                new updateLoginStatus(staffId,verificationcode).execute(todo,null,null);
            }
        });
    }



    class updateLoginStatus extends AsyncTask<String,Void,String>{

        String staffId;
        String response = "";
        HashMap<String, String> postDataParams;
        int success;
        String result="";
        String verificationCode;

        updateLoginStatus(String staffId,String verificationCode){
            this.staffId=staffId;
            this.verificationCode= verificationCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(SMSVerificationActivity.this);
            processDialog.setMessage("Please  Wait ...");
            processDialog.setCancelable(false);
            processDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            String todo = strings[0];
            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");
            postDataParams.put("id",staffId);
            postDataParams.put("todo",todo);
            postDataParams.put("code",verificationCode);

            HttpConnectionService service = new HttpConnectionService(SMSVerificationActivity.this);
            response = service.sendRequest(apiPath, postDataParams);
            try {
                success = 1;

                JSONObject resultJsonObject = new JSONObject(response);
                result = resultJsonObject.get("output").toString();

            } catch (Exception e) {
                success = 0;
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (processDialog.isShowing()) {
                processDialog.dismiss();
            }
            if (null != response) {

                if(result.contains("successfully")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SMSVerificationActivity.this);
                    builder.setTitle("Success");
                    builder.setMessage(info+" Successful");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent splashScreen = new Intent(SMSVerificationActivity.this,SplashScreen.class);
                            startActivity(splashScreen);
                            SMSVerificationActivity.this.finish();
                        }
                    });
                    builder.show();

                }else if(result.equalsIgnoreCase("empty")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SMSVerificationActivity.this);
                    builder.setTitle("Oops");
                    builder.setMessage(info+" UnSuccessful : "+" invalid verification code provided!");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //sNavUtils.navigateUpFromSameTask(SMSVerificationActivity.this);
                        }
                    });
                    builder.show();
                }
                Log.d("making results",response+"hgjg");
                refreshUserPage();
            }
        }
    }

    private void refreshUserPage() {
    }
}
