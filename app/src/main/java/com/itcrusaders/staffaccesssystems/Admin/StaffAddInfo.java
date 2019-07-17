package com.itcrusaders.staffaccesssystems.Admin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.R;

import java.util.ArrayList;
import java.util.Locale;



@RequiresApi(api = Build.VERSION_CODES.N)
public class StaffAddInfo extends AppCompatActivity {

    private ProgressDialog processDialog;
    private String apiPath = ApiClass.getBaseUrl()+"stafflist/get1/";

    public TextView mStaffName,mStaffMobile,mStaffMail,mStaffBday;
    public Button mStaffUpdate;
    de.hdodenhof.circleimageview.CircleImageView imageView;


    private int success = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_add_info);
        bindButtons();
    }

    String staff_name, staff_mobile, staff_mail,staffbday;
    Bitmap staffImg;
    Button btnChangePhoto;

    final Calendar myCalendar = Calendar.getInstance();

    private void updateLabel() {
        String myFormat = "dd MMM"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mStaffBday.setText(sdf.format(myCalendar.getTime()));
    }


    public void bindButtons(){


        btnChangePhoto = findViewById(R.id.btn_change_staff_pic);
        mStaffMobile = findViewById(R.id.txt_staff_mobile);
        mStaffName = findViewById(R.id.txt_staff_name);
        mStaffMail = findViewById(R.id.txt_staff_mail);
        mStaffBday = findViewById(R.id.txt_staff_bday);

        imageView = findViewById(R.id.staff_img_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePic();
            }
        });

        mStaffUpdate = findViewById(R.id.btn_update_staff_info);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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


            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus == true) {
                    new DatePickerDialog(StaffAddInfo.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });
        mStaffBday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(StaffAddInfo.this, date, myCalendar
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

                AlertDialog.Builder builder = new AlertDialog.Builder(StaffAddInfo.this);

                builder.setTitle("Add Staff Info");
                builder.setMessage("Are you sure ?");
                builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                final ArrayList list = new ArrayList();
                list.add(StaffAddInfo.this);
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new StaffInfoManipulation(list,"insert",
                                staff_name,staff_mobile,staff_mail,staffbday,"","",staffImg).execute();

                    }
                });


                builder.show();
            }
        });
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
