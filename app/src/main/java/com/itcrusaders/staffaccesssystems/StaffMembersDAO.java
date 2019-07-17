package com.itcrusaders.staffaccesssystems;

import android.os.AsyncTask;

public class StaffMembersDAO {

    public String staffId,staff_name,login_status, staff_phone,staff_pic,logintime,staff_mail,staffBday,active;
    public int present,absent;

    public String getStaffId() {
        return staffId;
    }



    public StaffMembersDAO(String staffId,String staff_name,String staff_phone,
                           String login_status,String staff_pic,String logintime){

        this.staffId=staffId;
        this.staff_name=staff_name;
        this.staff_phone=staff_phone;
        this.login_status=login_status;
        this.staff_pic=staff_pic;
        this.logintime=logintime;
    }



    public StaffMembersDAO(String staff_name, String staff_phone, String staff_mail, String staffId,
                           String staffBday, String staff_pic,int i) {
        this.staff_name = staff_name;
        this.staff_phone = staff_phone;
        this.staff_mail = staff_mail;
        this.staffId = staffId;
        this.staff_pic=staff_pic;
        this.staffBday=staffBday;
    }

    public StaffMembersDAO(String staff_name, String staff_phone, int present,
                                int absent, String staffId) {
        this.staff_name = staff_name;
        this.staff_phone = staff_phone;
        this.present = present;
        this.absent=0;
        this.staffId = staffId;
    }

    public class updateStaffInfo extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}
