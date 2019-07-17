package com.itcrusaders.staffaccesssystems;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import java.util.HashMap;

public class SMSManipulation {

    String staffId,logtype;
    String msg;


    private String apiPath = ApiClass.getBaseUrl()+"stafflist/sendsms/";

    Context mContext;
    String staffName;
    String staffMobile;
    String staffMail;

    SMSManipulation(Context context,String staffId,String logtype,String staffName,String staffMobile,String staffMail){
        this.mContext=context;
        this.logtype=logtype;
        this.staffId=staffId;
        this.staffName=staffName;
        this.staffMobile = staffMobile;
        this.staffMail = staffMail;
        switch (logtype){
            case "in":
                logs = " Login key ";
                break;
            case "out" :
                    logs = " Log out key ";
                break;
            case "sp":
                logs = " key to lodge a special assignment ";
                break;
        }
        msg = "Dear "+staffName+", proceed with this code "+logs+"  ";


    }
    String logs="";

    private String visitorMobile,visitorName;

    SMSManipulation(Context context, String visitorMobile, String visitorName){
        this.mContext=context;
        this.visitorMobile = visitorMobile;
        this.visitorName = visitorName;

        msg = "Dear "+visitorName+", your Appointment had been booked successful! /r/n/!" +
                " Have a seat, you will be contacted shortly!";


    }

    public void sendVisitorsMsg(){
        apiPath = ApiClass.getBaseUrl()+"visitors/sendsms/";
        new sendSMSFromNet("visitors").execute();
    }

    public void sendMsg() {
        new sendSMSFromNet("staff").execute(staffId);
    }




    class sendSMSFromNet extends AsyncTask<String,Void,String> {


        String response = "";
        HashMap<String, String> postDataParams;
        String messageType;

        sendSMSFromNet(String type){
            this.messageType = type;
        }
        @Override
        protected String doInBackground(String... strings) {
            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");
            postDataParams.put("message",msg);

            if(messageType.equalsIgnoreCase("visitors")){
                postDataParams.put("v_name",visitorName);
                postDataParams.put("v_mobile",visitorMobile);
            }else {
                postDataParams.put("id", staffId);
                postDataParams.put("staffMobile", staffMobile);
                postDataParams.put("staffName", staffName);
                postDataParams.put("staffMail", staffMail);
                postDataParams.put("todo", logs);
            }

            HttpConnectionService service = new HttpConnectionService(mContext);
            response = service.sendRequest(apiPath, postDataParams);
            //Log.d("making",apiPath);
            //Toast.makeText(mContext,response,Toast.LENGTH_LONG).show();
            Log.d("making",response+"jhhjj");

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
