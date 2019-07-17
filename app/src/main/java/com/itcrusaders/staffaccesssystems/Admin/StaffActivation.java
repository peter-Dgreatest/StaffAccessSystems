package com.itcrusaders.staffaccesssystems.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;

import java.util.HashMap;

public class StaffActivation extends AsyncTask<String,Void,String> {

    private Context mContext;
    private Activity mActivity;
    private String updateType,staff_name,staff_mobile,staff_mail,staffId,results;
    private ProgressDialog processDialog;

    private String apiPath = ApiClass.getBaseUrl()+"stafflisteditactivate/";


    StaffActivation(Context mContext, Activity mActivity, String updateType,
                    String staff_name, String staff_mobile, String staff_mail, String staffId){
        this.mContext = mContext;
        this.mActivity = mActivity;

        this.updateType = updateType;
        this.staff_name = staff_name;
        this.staff_mobile = staff_mobile;
        this.staff_mail = staff_mail;
        this.staffId = staffId;
    }
    int success =0;
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

    @Override
    protected String doInBackground(String... voids) {
        postDataParams = new HashMap<String, String>();
        postDataParams.put("HTTP_ACCEPT", "application/json");
        postDataParams.put("staffname",staff_name);
        postDataParams.put("staffmobile",staff_mobile);
        postDataParams.put("staffmail",staff_mail);
        postDataParams.put("staffId",staffId);
        postDataParams.put("todo",updateType);

        HttpConnectionService service = new HttpConnectionService(mContext);
        response = service.sendRequest(apiPath, postDataParams);
        try {
            success = 1;
            results = response;
        } catch (Exception e) {
            success = 0;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (processDialog.isShowing()) {
            processDialog.dismiss();
        }

        if (success == 1) {
            if (null != results) {
                if(results.contains("successfully")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Success");
                    builder.setMessage("Staff Account "+updateType+"d successfully!");
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
                    builder.setMessage("Error Occurred "+updateType+"ing Staff Account!"+"");
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
