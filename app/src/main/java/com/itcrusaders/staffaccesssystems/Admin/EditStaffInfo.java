package com.itcrusaders.staffaccesssystems.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.HttpConnectionService;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.StaffMembersDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.N)
public class EditStaffInfo extends AppCompatActivity {
    private ProgressDialog processDialog;
    private String apiPath = ApiClass.getBaseUrl()+"stafflist/get1/";

    public TextView mStaffName,mStaffMobile,mStaffMail,mStaffBday;
    MenuItem btn_activate,btn_deactivate;
    public Button mStaffUpdate;
    de.hdodenhof.circleimageview.CircleImageView imageView;

    private int success = 0;

    private JSONArray restulJsonArray;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_staff_info);

        if(!(getIntent() == null)) {
            Intent thisIntent = getIntent();
            id = thisIntent.getStringExtra("id");
        }

        retrieveStaffInfo(id);

    }

    String active="1";
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

            HttpConnectionService service = new HttpConnectionService(mContext);
            response = service.sendRequest(apiPath+idd, postDataParams);
            try {
                success = 1;
                //Log.d("kllllllll",response);
                //Log.d("kllllllll",apiPath+idd);
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
                    ArrayList<StaffMembersDAO> staffMembers = new ArrayList<>();
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            //Log.d("here1","sjhss");

                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            //Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            StaffMembersDAO messagesDAO =
                                    new StaffMembersDAO(
                                            jsonObject.get("staff_xxname").toString(),
                                            jsonObject.get("staff_xxphone").toString(),
                                            jsonObject.get("staff_xxmail").toString(),
                                            jsonObject.get("staff_xxid").toString(),
                                            jsonObject.get("staff_xxbday").toString(),
                                            jsonObject.get("staff_xxpic").toString(),0);

                            active = jsonObject.get("active").toString();
                            staffMembers.add(messagesDAO);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    displayStaffInfo(staffMembers.get(0));
                }
            }
        }
    }


    public void retrieveStaffInfo(String id){
        new getStaffInfoFromInternet(this,this).execute(id);
    }

    public void displayStaffInfo(StaffMembersDAO staff){


        bindButtons(staff.staffId);
        staffId = staff.staffId;

        //Log.d("StaffName",staff.staff_name);
        mStaffName.setText(staff.staff_name);
        mStaffMobile.setText(staff.staff_phone);
        mStaffMail.setText(staff.staff_mail);
        mStaffBday.setText(staff.staffBday);
        imageView = findViewById(R.id.staff_img_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePic();
            }
        });

        staff_img_url = staff.staff_pic;

        staff_mail = mStaffMail.getText().toString();
        staff_mobile =  mStaffMobile.getText().toString();
        staff_name = mStaffName.getText().toString();
        try {
            menu.clear();
            if (active.equalsIgnoreCase("1")) {
                getMenuInflater().inflate(R.menu.staffdeactivate, this.menu);
            } else {
                getMenuInflater().inflate(R.menu.staffreactivate, this.menu);
            }
        }catch (Exception ex){

        }
        new downloadImageFromNet().execute(ApiClass.getImageBaseUrl()+staff.staff_pic);
    }

    public static Bitmap getBitmapFromUrl(String filePath)
    {
        //Bitmap d= null;

        Bitmap img = null;
        URL uu;


        InputStream inputStream = null;
        try {
            Log.d("filepath",filePath);
            inputStream = new URL(filePath).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //uu = new URL(filePath);
            img = BitmapFactory.decodeStream(inputStream);

        return img;
    }

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
            imageView.setImageBitmap(bitmap);
        }
    }


    String staff_name, staff_mobile, staff_mail, staffId,staff_img_url,staffbday;
    Bitmap staffImg;
    Button btnChangePhoto;

    public void bindButtons(final String staffId){


        btnChangePhoto = findViewById(R.id.btn_change_staff_pic);
        mStaffMobile = findViewById(R.id.txt_staff_mobile);
        mStaffName = findViewById(R.id.txt_staff_name);
        mStaffMail = findViewById(R.id.txt_staff_mail);
        mStaffBday = findViewById(R.id.txt_staff_bday);

        mStaffUpdate = findViewById(R.id.btn_update_staff_info);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        mStaffBday.setOnFocusChangeListener(new View.OnFocusChangeListener() {


            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus == true) {
                    new DatePickerDialog(EditStaffInfo.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        mStaffBday.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditStaffInfo.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePic();
            }
        });

        mStaffUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    staff_mail = mStaffMail.getText().toString();
                    staff_mobile =  mStaffMobile.getText().toString();
                    staff_name = mStaffName.getText().toString();
                    //staffId = mStaffUpdate.getTag().toString();

                    staffbday = mStaffBday.getText().toString();
                    staffImg = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                    mStaffBday = findViewById(R.id.txt_staff_bday);

                    AlertDialog.Builder builder = new AlertDialog.Builder(EditStaffInfo.this);

                    builder.setTitle("Update Staff Info");
                    builder.setMessage("Are you sure ?");
                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    final ArrayList list = new ArrayList();
                    list.add(EditStaffInfo.this);
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new StaffInfoManipulation(list,"Update",
                                    staff_name,staff_mobile,staff_mail,staffbday,staffId,staff_img_url,staffImg).execute();

                        }
                    });


                    builder.show();
                }
        });
    }

    final Calendar myCalendar = Calendar.getInstance();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(active.equalsIgnoreCase("1")){
            getMenuInflater().inflate(R.menu.staffdeactivate, menu);
        }else{
            getMenuInflater().inflate(R.menu.staffreactivate, menu);
        }
        this.menu=menu;
        return super.onCreateOptionsMenu(menu);
    }

    private Menu menu;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_deactivate_staff){
            new StaffActivation(this,this,
                    "deactivate",staff_name,staff_mobile,staff_mail,staffId).execute();
        }else if(item.getItemId() == R.id.action_reactivate_staff){
            new StaffActivation(this,this,
                    "reactivate",staff_name,staff_mobile,staff_mail,staffId).execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLabel() {
        String myFormat = "dd MMM"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mStaffBday.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        processDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        processDialog.dismiss();
    }

    private final int requestCode = 10000;
    private void changePic() {
        Intent photoCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(photoCapture,requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode== requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }
}