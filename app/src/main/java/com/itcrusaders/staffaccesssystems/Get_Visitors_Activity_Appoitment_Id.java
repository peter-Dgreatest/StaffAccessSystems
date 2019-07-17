package com.itcrusaders.staffaccesssystems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class Get_Visitors_Activity_Appoitment_Id extends AppCompatActivity {

    Button mProceed;
    ExtendedEditText appointmentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get__visitors___appoitment__id);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*0.7),(int) (height*0.4));

        appointmentId= findViewById(R.id.appointmentid);
        mProceed = findViewById(R.id.btn_proceed);
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent visitorLoginIntent = new Intent(Get_Visitors_Activity_Appoitment_Id.this,VisitorLogin.class);
                visitorLoginIntent.putExtra("appointmentId",appointmentId.getText().toString());
                startActivity(visitorLoginIntent);
                Get_Visitors_Activity_Appoitment_Id.this.finish();
            }
        });
    }
}
