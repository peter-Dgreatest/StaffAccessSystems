package com.itcrusaders.staffaccesssystems;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import com.itcrusaders.staffaccesssystems.Admin.Dashboard;

import java.util.HashMap;

public class AdminLogin extends AppCompatActivity {

    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;
    VideoView videoview;

    EditText mUsername,mPassword;
    String username,password;
    Button submitButton;

    FireBaseTokenManipulator tokenManipulator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        SharedPreferences sharedPreferences =getSharedPreferences("com.itcrusaders.staffaccesssystems.Admin",
                MODE_PRIVATE);


        String clearanceLevel = sharedPreferences.getString("clearanceLevel","");

        String pastorLog = sharedPreferences.getString("adminlogin2","");

        String peterLog = sharedPreferences.getString("adminlogin3","");

        Intent intent = new Intent(AdminLogin.this, Dashboard.class);

        if(clearanceLevel==null){
            sharedPreferences.edit().clear().apply();
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle("Info");
            builder.setMessage("Please login in again!\r this is an updated version of the app\r Thank you.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AdminLogin.this.finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        if(clearanceLevel.equalsIgnoreCase("secretary")) {
            startActivity(intent);
            //System.exit(1);
        }else if(clearanceLevel.equalsIgnoreCase("humanrescource")) {
            //Intent intent = new Intent(AdminLogin.this, Dashboard.class);
            startActivity(intent);
        }else if(pastorLog.equalsIgnoreCase("success")) {
            tokenManipulator = new FireBaseTokenManipulator();
            tokenManipulator.getInstance(this);
            startActivity(intent);
            //System.exit(1);
        }else if(peterLog.equalsIgnoreCase("success")) {
            tokenManipulator = new FireBaseTokenManipulator();
            tokenManipulator.getInstance(this);
            startActivity(intent);
        }
        videoview = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://"+getPackageName()
                +"/"
                +R.raw.fandf);
        videoview.setVideoURI(uri);
        videoview.start();

        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    mp.setVolume(0f, 0f);
                    mp.setLooping(true);
                    if(mCurrentVideoPosition!=0){
                        mMediaPlayer.seekTo(mCurrentVideoPosition);
                        mMediaPlayer.start();
                    }
                    mp.setVolume(0f,0f);
                } catch (Exception e) {
                    e.printStackTrace();
                }    mMediaPlayer=mp;

                //mMediaPlayer.setLooping(true);


            }
        });

        mUsername = findViewById(R.id.txt_admin_username);
        mPassword = findViewById(R.id.txt_admin_password);

        submitButton = findViewById(R.id.btn_admin_login);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                if(username.equalsIgnoreCase("pastor")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminLogin.this);
                    builder.setTitle("Login");
                    builder.setMessage("Are you sure you want to log in on this device? \r" +
                            "You will start receiving notifications of visitors' appointment if you do. \r" +
                            "Proceed?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new CheckLoginStatus(AdminLogin.this).execute();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }else{
                    new CheckLoginStatus(AdminLogin.this).execute();
                }
            }
        });
    }


    @Override
    protected void onPause() {
        try {
            super.onPause();
            mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
            videoview.pause();
        }catch(Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoview.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mMediaPlayer.reset();
            //mMediaPlayer= null;
        }catch (IllegalStateException e){

        }

    }


    ProgressDialog progressDialog;
    String apiPath = ApiClass.getBaseUrl()+"adminlogin/";

    private class CheckLoginStatus extends AsyncTask<String,Void,String>{
        ProgressDialog processDialog;
        String response = "",result="";
        HashMap<String, String> postDataParams;
        String messageType;
        int success=0;

        private Context mContext;

        CheckLoginStatus(Context mContext){
            this.mContext=mContext;
        }

        @Override
        protected String doInBackground(String... strings) {
            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");
            postDataParams.put("userxxname",username);
            postDataParams.put("passxxword",password);

            HttpConnectionService service = new HttpConnectionService(mContext);

//            Log.d("Response",apiPath+" "+postDataParams.toString());
            response = service.sendRequest(apiPath, postDataParams);
  //          Log.d("Response",response);
            success=1;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(result);


            if (processDialog!=null && processDialog.isShowing()) {
                processDialog.dismiss();
            }

            if (response.contains("success")) {

                SharedPreferences sharedPreferences =getSharedPreferences("com.itcrusaders.staffaccesssystems.Admin",
                        MODE_PRIVATE);

                if(username.equalsIgnoreCase("pastor")){
                    sharedPreferences.edit().putString("adminlogin2","success").apply();
                    sharedPreferences.edit().putString("clearanceLevel",username).apply();
                }else {
                    sharedPreferences.edit().putString("adminlogin1", "success").apply();
                    sharedPreferences.edit().putString("clearanceLevel",username).apply();
                }
                Intent intent = new Intent(AdminLogin.this, Dashboard.class);
                startActivity(intent);
            }
            else if(response.contains("empty")){
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Invalid User Details Provided!");
                builder.setTitle("Error");

                builder.show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            processDialog = new ProgressDialog(mContext);
            processDialog.setMessage("Please  Wait ...");
            processDialog.setCancelable(false);
            processDialog.show();
        }
    }

}
