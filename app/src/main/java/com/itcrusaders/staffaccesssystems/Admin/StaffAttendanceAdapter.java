package com.itcrusaders.staffaccesssystems.Admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.StaffMembersDAO;

import java.util.ArrayList;


public class StaffAttendanceAdapter extends RecyclerView.Adapter<StaffAttendanceAdapter.StaffAttendanceViewHolder> {

    private ArrayList<StaffMembersDAO> staffMembers;
    final private StaffAttendanceAdapter.StaffAttendanceAdapterOnClickHandler handler;


    public interface StaffAttendanceAdapterOnClickHandler{
        void clickHandler(String Params);
    }

    public StaffAttendanceAdapter(StaffAttendanceAdapter.StaffAttendanceAdapterOnClickHandler clickHandler){
        handler=clickHandler;
    }
    @Override
    public StaffAttendanceAdapter.StaffAttendanceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();


        int layoutIdforItem = R.layout.staff_attendance_loader;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        //View view1 =  inflater.inflate(layoutIdforItem,viewGroup,shouldAttachToParentImmediately);

        return new StaffAttendanceAdapter.StaffAttendanceViewHolder(inflater.inflate(layoutIdforItem,viewGroup,shouldAttachToParentImmediately));
    }

    @Override
    public void onBindViewHolder(StaffAttendanceAdapter.StaffAttendanceViewHolder staffMembersViewHolder, int position) {

        StaffMembersDAO staff = staffMembers.get(position);

        staffMembersViewHolder.mStaffNAme.setText(staff.staff_name);
        staffMembersViewHolder.staffLayout.setTag(staff.staffId);

        staffMembersViewHolder.mStaffId.setText(staff.staffId);
        staffMembersViewHolder.mStaffNAme.setText(staff.staff_name);
        staffMembersViewHolder.mStaffMobile.setText(staff.staff_phone);
        staffMembersViewHolder.mStaffPresent.setText(staff.present+"");
        staffMembersViewHolder.mStaffAbsent.setText(staff.absent+"");

        //staffMembersViewHolder.mStaffLoginStatus.setText(staff.login_status + " by "+staff.logintime);
        //unsentMessagesViewHolder.mSenderId.setText(msg.getMsg_id());
    }

    @Override
    public int getItemCount() {
        if(null == staffMembers)
            return 0;
        return staffMembers.size();
    }

    public void setMessageData(ArrayList<StaffMembersDAO> staffMember){
        staffMembers = staffMember;
        notifyDataSetChanged();
    }

    public class StaffAttendanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //public final TextView mSenderId;

        public final TextView mStaffId;

        public final TextView mStaffNAme;

        public final TextView mStaffMobile;

        public final TextView mStaffPresent;

        public final TextView mStaffAbsent;

        public final LinearLayout staffLayout;

        public StaffAttendanceViewHolder(View itemView) {
            super(itemView);

            //mSenderId = (TextView) itemView.findViewById(R.id.rytxt_sendId);
            mStaffId = (TextView) itemView.findViewById(R.id.txt_att_staff_id);
            mStaffNAme = (TextView) itemView.findViewById(R.id.txt_att_staff_name);
            mStaffMobile = itemView.findViewById(R.id.txt_att_staff_phone);
            mStaffPresent = (TextView) itemView.findViewById(R.id.txt_att_staff_present);
            mStaffAbsent = itemView.findViewById(R.id.txt_att_staff_absent);
            staffLayout = itemView.findViewById(R.id.staff_div);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            handler.clickHandler(staffLayout.getTag().toString());
        }
    }

}


