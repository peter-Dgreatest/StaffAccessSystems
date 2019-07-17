package com.itcrusaders.staffaccesssystems;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class VisitorsDAO {
    String fname;
    String lname;
    String group;
    String church;
    String organ;
    String vemail;
    String mobile;
    String purpose;
    String Idno;
    String Idtype;
    String appoinId;
    String appoin,photo,results;
    Bitmap bitmap;

    private String apiPath = ApiClass.getBaseUrl()+"visitors/bookApp/";
    private ProgressDialog processDialog;
    private JSONArray restulJsonArray;
    private int success = 0;
    private Context mContext;
    private Activity mActivity;

    //fname,lname,Idtype,Idno,mobile,vemail,purpose,
    //                                            organ,church,group
    VisitorsDAO(String fname, String lname, String Idtype, String Idno,String mobile,String vemail,
                String purpose, String organ,String church, String group, String appoin,String appoinId,
                String photo,Bitmap bitmap, Context mContext, Activity mActivity){

        this.fname = fname;
        this.lname = lname;

        this.Idtype = Idtype;
        this.Idno = Idno;

        this.organ = organ;
        this.group = group;
        this.church = church;
        this.appoin = appoin;
        this.appoinId=appoinId;
        this.vemail = vemail;
        this.photo = photo;
        this.mobile = mobile;
        this.purpose = purpose;
        if(appoin!=null)
        this.appoin=appoin;

        this.bitmap = bitmap;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    VisitorsDAO(String fname, String lname, String Idtype, String Idno,String mobile,String vemail,
                String purpose, String organ,String church, String group,String appoinId,
                String photo){

        this.fname = fname;
        this.lname = lname;
        this.Idtype = Idtype;
        this.Idno = Idno;
        this.mobile = mobile;
        this.vemail = vemail;
        this.purpose = purpose;
        this.organ = organ;
        this.church = church;
        this.group = group;
        this.appoinId = appoinId;

        this.photo = photo;
    }

    public void bookAppointment(){
        new bookAppointmentTask().execute();
    }

    class bookAppointmentTask extends AsyncTask<String,Void,String>{


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
        protected String doInBackground(String... arg0) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);

            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");
            postDataParams.put("fname",fname);
            postDataParams.put("lname",lname);

            postDataParams.put("mobile",mobile);
            postDataParams.put("vemail",vemail);


            postDataParams.put("Idtype",Idtype);
            postDataParams.put("Idno",Idno);
            postDataParams.put("purpose",purpose);

            postDataParams.put("organization",organ);

            postDataParams.put("church",church);
            postDataParams.put("group",group);

            postDataParams.put("appoin",appoin);
            postDataParams.put("appoinId",appoinId);
            postDataParams.put("pic",encodedImage);


            HttpConnectionService service = new HttpConnectionService(mContext);
            response = service.sendRequest(apiPath, postDataParams);
            try {
                success = 1;
                Log.d("llll",response+"lll");
                JSONObject resultJsonObject = new JSONObject(response);
                results = resultJsonObject.get("output").toString();
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
                        builder.setMessage("Your Appointment had been booked successful! \r!" +
                                " Have a seat, you will be contacted shortly!");

                        //NotificationManager
                        //sendSMS
                        SMSManipulation smsManipulation = new SMSManipulation(mContext,fname,mobile);
                        smsManipulation.sendVisitorsMsg();
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent splashIntent = new Intent(mActivity,SplashScreen.class);
                                mActivity.startActivity(splashIntent);
                                mActivity.finish();
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
}
