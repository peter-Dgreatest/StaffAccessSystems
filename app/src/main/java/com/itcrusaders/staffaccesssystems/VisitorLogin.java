package com.itcrusaders.staffaccesssystems;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

/**
 * A login screen that offers login via email/password.
 */
public class VisitorLogin extends AppCompatActivity {

    // UI references.
    ImageView imageView;
    private int requestCode =20;
    private Button bookAppointment;
    //visitor_ID_no

    String appointmentId="";

    String fname;
    String lname;
    String group;
    String church;
    String organ;
    String vemail;
    String mobile;
    String purpose;
    String purposeO;
    String Idno;
    String Idtype;
    String appoin="";
    String appoinId;

    TextFieldBoxes pOthersCont,vidothersCont;
    private EditText Fname,Lname,gGroup,Church,VEmail,Mobile,pOthers,edt_Idno,Organization,Idothers;
    MaterialBetterSpinner autoCompleteTextView,visitorIdtype;
    ScrollView scrollView ;
    LinearLayout linearLayout;

    Context mContext;
    ArrayList<VisitorsDAO> visitorsDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_login);


        imageView = findViewById(R.id.visitors_face);
        bookAppointment = findViewById(R.id.btn_visitor_book);

        Fname = findViewById(R.id.fname);
        Lname = findViewById(R.id.lname);

        visitorIdtype = findViewById(R.id.visitorIdentificationType);
        Idothers = findViewById(R.id.vidothers);
        vidothersCont = findViewById(R.id.vIdtypeCont);
        edt_Idno = findViewById(R.id.visitor_ID_no);

        Mobile = findViewById(R.id.phone);
        VEmail = findViewById(R.id.vemail);

        autoCompleteTextView =  findViewById(R.id.purposeOfVisit);
        pOthers = findViewById(R.id.purposeothers);
        pOthersCont = findViewById(R.id.vpOthersCont);

        Organization = findViewById(R.id.organization);
        gGroup = findViewById(R.id.group);
        Church = findViewById(R.id.church);



        //create a list of items for the spinner.
        String[] items = new String[]{"Financial","Marital","Family", "Educational","Others"};
        String[] idtpes = new String[]{"National Id","Drivers Licence","Others"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, items);

        ArrayAdapter idAdapter = new ArrayAdapter<>(this,android.R.layout.simple_expandable_list_item_1,idtpes);

        autoCompleteTextView.setThreshold(1);

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equalsIgnoreCase("others")){
                    pOthersCont.setVisibility(View.VISIBLE);
                }
                else{
                    pOthersCont.setVisibility(View.GONE);
                }
            }
        });

        visitorIdtype.setThreshold(1);
        visitorIdtype.setAdapter(idAdapter);

        visitorIdtype.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equalsIgnoreCase("others")){
                    vidothersCont.setVisibility(View.VISIBLE);
                }
                else{
                    vidothersCont.setVisibility(View.GONE);
                }
            }
        });

        String Id = null;
        if(!(getIntent() == null)){
            Id = getIntent().getStringExtra("appointmentId");
        }

        mContext = this;
        if(Id==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you have an appointment?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(mContext, Get_Visitors_Activity_Appoitment_Id.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        else{
            getAppointmentById appointmentById = new getAppointmentById(mContext,this,Id);
            visitorsDAO = appointmentById.getVisitorInfo();
        }

        //View view = findViewById(R.layout.activity_visitor_login);


        linearLayout = (LinearLayout) findViewById(R.id.email_login_form);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        int margin = width * 20/100;

        ScrollView.LayoutParams params =
                new ScrollView.LayoutParams((width*60/100), ScrollView.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin,10,margin,10);


        linearLayout.setLayoutParams(params);


        //linearLayout.setBackgroundColor(Color.WHITE);


        //autoCompleteTextView.onItemClick(adapter,view,);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoCapture.putExtra("android.intent.extras.CAMERA_FACING",1);

                startActivityForResult(photoCapture,requestCode);
            }
        });

        //autoCompleteTextView.on
        bookAppointment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = Fname.getText().toString();
                lname = Lname.getText().toString();

                Idtype = visitorIdtype.getText().toString();
                if(Idtype.equalsIgnoreCase("others"))
                    Idtype = Idothers.getText().toString();
                Idno = edt_Idno.getText().toString();

                mobile = Mobile.getText().toString();
                vemail = VEmail.getText().toString();

                purpose = autoCompleteTextView.getText().toString();
                if(purpose.equalsIgnoreCase("others"))
                    purpose = purpose +" "+ pOthers.getText().toString();

                organ = Organization.getText().toString();
                church = Church.getText().toString();
                group = gGroup.getText().toString();


                //if(appoin.)
                boolean _2proceed =
                        checkforEmptyString();

                if(_2proceed) {
                    Bitmap bm;

                    bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    VisitorsDAO visitorsDAO = new VisitorsDAO(fname,lname,Idtype,Idno,mobile,vemail,purpose,
                            organ,church,group,appoin,appointmentId,"", bm, VisitorLogin.this, VisitorLogin.this);
                    visitorsDAO.bookAppointment();
                }
            }
        });
    }

    //fname,lname,Idtype,Idno,mobile,vemail,purpose,
    //                                            organ,church,group
    private boolean checkforEmptyString() {
        String msg="";
        if(fname.trim().length()<3){
            msg =  "Your First Name Cannot Be Blank!!!";
            Fname.setActivated(true);
        } else if(lname.trim().length()<3){
            msg =  "Your Last Name Cannot Be Blank!!!";
            Lname.requestFocus();
        }else if(Idtype.trim().length()<3){
            msg =  "Enter A Valid Id Card type!!!";
            visitorIdtype.requestFocus();
        } else if(Idno.trim().length()<3){
            msg =  "Enter A Valid Id no!!!";
            edt_Idno.requestFocus();
        }else if(mobile.trim().length()<3){
            msg =  "Enter A Valid Phone Number!!!";
            Mobile.requestFocus();
        }else if(vemail.trim().length()<3){
            msg =  "Enter A Valid Email!!!";
            VEmail.requestFocus();
        }else if((purpose.length()<6 )|| (purpose.contains("Others") && purpose.length() < 9) ){
            msg =  purpose+" What Is The Purpose Of Your Visit?";
            autoCompleteTextView.requestFocus();
        }else if(organ.trim().length()<3){
            msg =  "To what oraganization do you belong?";
            Organization.requestFocus();
        }else if(church.trim().length()<3){
            msg =  "Enter A Value For Church!!!";
            Church.requestFocus();
        } else if(group.trim().length()<3){
            msg =  "What Group Do You Belong To?";
            gGroup.requestFocus();
        }

        if(msg.length()>10) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage(msg);
            builder.setPositiveButton("Okay",null);
            builder.show();
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode== requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }



    class getAppointmentById extends AsyncTask<String, Void, Void> {
        private Context mContext;
        private Activity mActivity;

        public getAppointmentById(Context mContext, Activity mActivity,String id) {
            this.mContext = mContext;
            this.mActivity = mActivity;
            this.id=id;
        }

        String response = "";
        String id= "";
        HashMap<String, String> postDataParams;
        ProgressDialog processDialog;
        int success=0;
        JSONArray restulJsonArray;
        String apiPath = ApiClass.getBaseUrl()+"visitorlist/get1/";

        ArrayList<VisitorsDAO> visitorsDAOS;

        public ArrayList<VisitorsDAO> getVisitorInfo(){
            this.execute(id);

            return visitorsDAOS;
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
        protected Void doInBackground(String... arg0) {

            appointmentId= arg0[0].toString();

            postDataParams = new HashMap<String, String>();
            postDataParams.put("HTTP_ACCEPT", "application/json");

            HttpConnectionService service = new HttpConnectionService(mContext);

            postDataParams.put("type", "transId");
            response = service.sendRequest(apiPath+appointmentId, postDataParams);
            try {
                success = 1;
                //Log.d("kllllllll",response);
                JSONObject resultJsonObject = new JSONObject(response);
                restulJsonArray = resultJsonObject.getJSONArray("output");
            } catch (JSONException e) {
                success = 0;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);


            if (processDialog.isShowing()) {
                processDialog.dismiss();
            }

            if (success == 1) {
                if (null != restulJsonArray) {
                    //ArrayAdapter listViewAdapter = new ArrayAdapter<String>(mContext, R.layout.mobile_name_listview);


                    visitorsDAOS = new ArrayList<>();
                    for (int i = 0; i < restulJsonArray.length(); i++) {
                        try {
                            //Log.d("here1","sjhss");

                            JSONObject jsonObject = restulJsonArray.getJSONObject(i);
                            //Log.d("JSONINFO",jsonObject.toString()+"dddd");
                            VisitorsDAO visitorsDAO =
                                    new VisitorsDAO(jsonObject.get("name").toString(),"",

                                            jsonObject.get("Idtype").toString(),
                                            jsonObject.get("Idno").toString(),

                                            jsonObject.get("mobile").toString(),
                                            jsonObject.get("vemail").toString(),

                                            jsonObject.get("purpose").toString(),

                                            jsonObject.get("organization").toString(),
                                            jsonObject.get("church").toString(),
                                            jsonObject.get("gGroup").toString(),

                                            jsonObject.get("transId").toString(),

                                            jsonObject.get("pic").toString());

                            visitorsDAOS.add(visitorsDAO);
                            //Log.d("lalalal",jsonObject.get("staff_xxname").toString());

                            //listViewAdapter.add(jsonObject.get("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    returnVisitorsInfo(visitorsDAOS.get(0));
                }
            }
        }

        private VisitorsDAO returnVisitorsInfo(VisitorsDAO visitorsDAO) {
            new downloadImageFromNet().execute(ApiClass.getImageBaseUrl()+"rest/"+visitorsDAO.photo);
            fname = visitorsDAO.fname.substring(0,visitorsDAO.fname.indexOf(" "));
            lname = visitorsDAO.fname.substring(visitorsDAO.fname.indexOf(" ")+1);

            Fname.setText(fname);
            Lname.setText(lname);
            visitorIdtype.setText(visitorsDAO.Idtype);
            edt_Idno.setText(visitorsDAO.Idno);

            Mobile.setText(visitorsDAO.mobile);
            VEmail.setText(visitorsDAO.vemail);


            autoCompleteTextView.setText(visitorsDAO.purpose);
            pOthers.setVisibility(View.GONE);

            Organization.setText(visitorsDAO.organ);
            Church.setText(visitorsDAO.church);
            gGroup.setText(visitorsDAO.group);


            bookAppointment.setText("Re-Open Appointment");
            bookAppointment.setTag(appointmentId);
            appoin="yes";
            return visitorsDAO;
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

        public Bitmap getBitmapFromUrl(String filePath)
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
    }

}