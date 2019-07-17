package com.itcrusaders.staffaccesssystems.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itcrusaders.staffaccesssystems.AdminLogin;
import com.itcrusaders.staffaccesssystems.HomeActivity;
import com.itcrusaders.staffaccesssystems.MainTabbedActivity;
import com.itcrusaders.staffaccesssystems.MaterialDesignCalender.SchVisitorCalendarActivity;
import com.itcrusaders.staffaccesssystems.MaterialDesignCalender.VisitorCalendarActivity;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.SplashScreen;
import com.itcrusaders.staffaccesssystems.StaffDashboard;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


public class Dashboard extends AppCompatActivity {

    CardView mLaunchStaffPortal,mLaunchVisitorsPortal,mLaunchStaffAttendanceRecords,mLaunchVisitorsSchPortal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("com.itcrusaders.staffaccesssystems.Admin",
                MODE_PRIVATE);

        String adminLog = sharedPreferences.getString("adminlogin1","");

        String clearanceLevel = sharedPreferences.getString("clearanceLevel","");

        if(!(adminLog.equalsIgnoreCase("success")) && !(clearanceLevel.equalsIgnoreCase("pastor")) ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You do not have access to this page!");
            builder.setTitle("Error");
            builder.setCancelable(false);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dashboard.this.finish();
                    System.exit(1);
                }
            });
            builder.show();

        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;



        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((40*width/100),((66*height/100)));

        layoutParams.setMargins((5*width/100),(7*height/100),(5*height/100),(5*width/100));

        //btn_launch_visitors_sch_records
        mLaunchVisitorsSchPortal = findViewById(R.id.btn_launch_visitors_sch_records);
        mLaunchVisitorsSchPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Dashboard.this,SchVisitorCalendarActivity.class);
                startActivity(intent);

                //Toast.makeText(Dashboard.this,"This part is pending!please check later",Toast.LENGTH_SHORT).show();
            }
        });


        mLaunchVisitorsPortal = findViewById(R.id.btn_launch_visitors_records);
        mLaunchVisitorsPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Dashboard.this,VisitorCalendarActivity.class);
                startActivity(intent);

                //Toast.makeText(Dashboard.this,"This part is pending!please check later",Toast.LENGTH_SHORT).show();
            }
        });


        mLaunchStaffPortal = findViewById(R.id.btn_launch_staff_portal);
        mLaunchStaffPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,StaffInfoEdit.class);
                startActivity(intent);
            }
        });

        mLaunchStaffAttendanceRecords = findViewById(R.id.btn_launch_staff_records);

        mLaunchStaffAttendanceRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,MainTabbedActivity.class);
                startActivity(intent);
            }
        });

        if(clearanceLevel.equalsIgnoreCase("secretary")){
            findViewById(R.id.staff_portal_div).setVisibility(View.GONE);
        }else if(clearanceLevel.equalsIgnoreCase("humanrescource")){
            findViewById(R.id.visitor_portal_div).setVisibility(View.GONE);
        }

        mLaunchVisitorsSchPortal.setLayoutParams(layoutParams);
        mLaunchStaffPortal.setLayoutParams(layoutParams);
        mLaunchStaffAttendanceRecords.setLayoutParams(layoutParams);
        mLaunchVisitorsPortal.setLayoutParams(layoutParams);

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Dashboard.this, SplashScreen.class));
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_login, menu);
        if(sharedPreferences.getString("clearanceLevel","")!=null){
            menu.removeItem(R.id.action_admin_login);
        }else{
            menu.removeItem(R.id.action_admin_logout);
        }
        return true;
    }
    SharedPreferences sharedPreferences ;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_admin_login) {
            Intent intent = new Intent(this,AdminLogin.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.action_admin_logout){
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(this,SplashScreen.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
