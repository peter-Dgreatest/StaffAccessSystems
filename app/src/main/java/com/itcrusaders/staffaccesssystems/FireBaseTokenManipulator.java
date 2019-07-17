package com.itcrusaders.staffaccesssystems;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.itcrusaders.staffaccesssystems.Admin.Dashboard;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class FireBaseTokenManipulator {
    public void getInstance(final Context mContext) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Instance", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        //mActivity1=mActivity;

                        saveTokenOnline(mContext,token);
                        // Log and toast
                        String msg = mContext.getString(R.string.msg_token_fmt, token);
                        Log.d("FirebaseToken", msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    Activity mActivity1;
    public int saveTokenOnline(Context mContext, String token){

        new saveTokentodb(mContext).execute(token);
        return 0;
    }

    class saveTokentodb extends AsyncTask<String, Void,String>{
        Context mContext;
        saveTokentodb(Context mContext){
            this.mContext=mContext;
        }
        String apiPath = ApiClass.getBaseUrl()+"savetokenadmin/";

        String response;
        int success=0;
        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");
            postDataParams.put("token",strings[0]);

            HttpConnectionService service = new HttpConnectionService(mContext);

//            Log.d("Response",apiPath+" "+postDataParams.toString());
            response = service.sendRequest(apiPath, postDataParams);
            Log.d("Response",response);
            success=1;
            return  null;
        }
        ProgressDialog processDialog;


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if (processDialog!=null && processDialog.isShowing()) {
                processDialog.dismiss();
            }

            if (response.contains("success")) {
                Log.d("response success",response);
                //mActivity1.finish();
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
