package com.itcrusaders.staffaccesssystems.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.util.Base64;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class StaffInfoManipulation extends AsyncTask<String,Void,String> {

    private Context mContext;
    private Activity mActivity;
    private String updateType,staff_name,staff_mobile,staff_mail,staffId,results,staff_img_URL,staff_bday;
    private ProgressDialog processDialog;
    private Bitmap staff_img;

    private String apiPath = ApiClass.getBaseUrl()+"stafflistedit/";

    StaffInfoManipulation(ArrayList context_activity, String updateType,
                          String staff_name, String staff_mobile, String staff_mail,String staff_bday,
                          String staffId, String staff_img_url,  Bitmap staff_img){
        this.mContext = (Context) context_activity.get(0);
        this.mActivity = (Activity) context_activity.get(0);

        this.updateType = updateType;
        this.staff_name = staff_name;
        this.staff_mobile = staff_mobile;
        this.staff_mail = staff_mail;
        this.staffId = staffId;
        this.staff_img_URL = staff_img_url;
        this.staff_bday = staff_bday;
        this.staff_img = staff_img;
    }

    String response = "";
    HashMap<String, String> postDataParams;


    int success;

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

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        staff_img.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);

        postDataParams = new HashMap<String, String>();
        postDataParams.put("HTTP_ACCEPT", "application/json");
        postDataParams.put("staffname",staff_name);
        postDataParams.put("staffmobile",staff_mobile);
        postDataParams.put("staffmail",staff_mail);
        postDataParams.put("staffbday",staff_bday);
        postDataParams.put("updatetype",updateType);
        postDataParams.put("staffimg_src",staff_img_URL);
        postDataParams.put("staffimg",encodedImage);
        postDataParams.put("staffId",staffId);

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
                    builder.setMessage("Staff Info "+updateType+" successful!");
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
                    builder.setMessage("Error Occurred "+updateType+"ing Staff Info!"+"");
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
