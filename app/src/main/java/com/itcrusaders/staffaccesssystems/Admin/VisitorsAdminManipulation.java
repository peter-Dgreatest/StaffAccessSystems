package com.itcrusaders.staffaccesssystems.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;
import com.itcrusaders.staffaccesssystems.SMSManipulation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VisitorsAdminManipulation extends AsyncTask<String,String,Void> {

    public String updateApp(){
        this.execute("");
        return "";
    }
    Context mContext;
    String Apipath = ApiClass.getBaseUrl();
    private ProgressDialog processDialog;
    private JSONArray restulJsonArray;
    private int success = 0;
    String todoResponse="";

    private Activity mActivity;
    String response = "",results="",id,appId,vname,vphone,newDate,newTime,vmail,todo="";
    HashMap<String, String> postDataParams;


    VisitorsAdminManipulation(Context context, Activity activity, ArrayList<String> uploadData) throws JSONException {
        this.mContext=context;
        this.mActivity=activity;

        id = uploadData.get(0);
        appId = uploadData.get(1);
        vname = uploadData.get(2);
        vphone = uploadData.get(3);
        vmail = uploadData.get(4);
        newDate = uploadData.get(5);
        newTime = uploadData.get(6);
    }

    VisitorsAdminManipulation(Context context, Activity activity, ArrayList<String> uploadData,String todo) throws JSONException {
        this.mContext=context;
        this.mActivity=activity;

        id = uploadData.get(0);
        appId = uploadData.get(1);
        vname = uploadData.get(2);
        vphone = uploadData.get(3);
        vmail = uploadData.get(4);
        this.todo=todo;
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
    protected Void doInBackground(String... voids) {

        HttpConnectionService service = new HttpConnectionService(mContext);
        postDataParams = new HashMap<String, String>();
        if(!(todo.length()>2)){
            postDataParams.put("id",id);
            postDataParams.put("appId",appId);
            postDataParams.put("vname",vname);
            postDataParams.put("vphone",vphone);
            postDataParams.put("vmail",vmail);
            postDataParams.put("newDate",newDate);
            postDataParams.put("newTime",newTime);
            todoResponse = "rescheduled";
            response = service.sendRequest(Apipath+"visitorlist/rebookAppointment/", postDataParams);
        }else if(todo.equalsIgnoreCase("approve")){
            postDataParams.put("id",id);
            postDataParams.put("appId",appId);
            postDataParams.put("vname",vname);
            postDataParams.put("vphone",vphone);
            postDataParams.put("vmail",vmail);
            todoResponse = "approved";
            response = service.sendRequest(Apipath+"visitorlist/approveAppointment/", postDataParams);
        }else if(todo.equalsIgnoreCase("deny")){
            postDataParams.put("id",id);
            postDataParams.put("appId",appId);
            postDataParams.put("vname",vname);
            postDataParams.put("vphone",vphone);
            postDataParams.put("vmail",vmail);

            todoResponse = "denied";
            response = service.sendRequest(Apipath+"visitorlist/denyAppointment/", postDataParams);
        }

        try {
            success = 1;
            //Log.d("lllllll",response.toString());
            JSONObject resultJsonObject = new JSONObject(response);
            results = resultJsonObject.get("output").toString();
        } catch (Exception e) {
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
            if (null != results) {
                if(results.contains("successfully")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Success");
                    builder.setMessage("The Appointment was "+todoResponse+" successfully!");

                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NavUtils.navigateUpFromSameTask(mActivity);
                        }
                    });
                    builder.show();

                }else if(results.equalsIgnoreCase("empty")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Oops");
                    builder.setMessage("Error Please contact Admin : "+"");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //sNavUtils.navigateUpFromSameTask(SMSVerificationActivity.this);
                        }
                    });
                    builder.show();
                }
            }
        }
    }

}
