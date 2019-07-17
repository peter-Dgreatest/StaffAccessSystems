package com.itcrusaders.staffaccesssystems.Admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itcrusaders.staffaccesssystems.ApiClass;
import com.itcrusaders.staffaccesssystems.R;
import com.itcrusaders.staffaccesssystems.StaffMembersDAO;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffMembersAdapter extends RecyclerView.Adapter<StaffMembersAdapter.StaffMembersViewHolder> implements Filterable{


    private ArrayList<StaffMembersDAO> staffMembers,staffMembersListFiltered;
    final private StaffMembersAdapterOnClickHandler handler;


    public interface StaffMembersAdapterOnClickHandler{
        void clickHandler(String Params);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    staffMembersListFiltered=staffMembers;
                }else {
                    ArrayList<StaffMembersDAO> filteredlist = new ArrayList<>();
                    for(StaffMembersDAO row : staffMembers){
                        if(row.staff_name.toLowerCase().contains(charString.toLowerCase())){
                            filteredlist.add(row);
                        }
                    }
                    staffMembersListFiltered = filteredlist;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values=staffMembersListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                staffMembersListFiltered = (ArrayList<StaffMembersDAO>) results.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public StaffMembersAdapter(StaffMembersAdapterOnClickHandler clickHandler){
        handler=clickHandler;
    }
    @Override
    public StaffMembersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();


        int layoutIdforItem = R.layout.staff_names_loader_admin;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        //View view1 =  inflater.inflate(layoutIdforItem,viewGroup,shouldAttachToParentImmediately);

        return new StaffMembersViewHolder(inflater.inflate(layoutIdforItem,viewGroup,shouldAttachToParentImmediately));
    }

    public static Bitmap getBitmapFromUrl(String filePath)
    {
        //Bitmap d= null;

        Bitmap img = null;
        URL uu;

        try{
            InputStream inputStream = new URL(filePath).openStream();
            //uu = new URL(filePath);
            img = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.d("dddfff",e.toString());
        }

        return img;
    }

    public class downloadImageFromNet extends AsyncTask<String,Void, Bitmap>{

        StaffMembersViewHolder viewHolder;
        public downloadImageFromNet(StaffMembersViewHolder viewHolder){
            this.viewHolder=viewHolder;
        }
        @Override
        protected Bitmap doInBackground(String... lists) {
            String imageURL = lists[0];

            Bitmap bimage = null;
            ArrayList arrayList = new ArrayList();
            return getBitmapFromUrl(imageURL);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            viewHolder.mImageView.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onBindViewHolder(StaffMembersViewHolder staffMembersViewHolder, int position) {

        StaffMembersDAO staff = staffMembersListFiltered.get(position);

        if(staff.active.equalsIgnoreCase("1")) {

        }else{
            staffMembersViewHolder.staffLayout.setBackgroundResource(R.color.grey);
        }
        String apipath = ApiClass.getImageBaseUrl();
        staffMembersViewHolder.mStaffNAme.setText(staff.staff_name);
        staffMembersViewHolder.staffLayout.setTag(staff.staffId);

        new downloadImageFromNet(staffMembersViewHolder).execute(apipath + staff.staff_pic);


        //staffMembersViewHolder.mImageView.setImageBitmap(getBitmapFromUrl(apipath+staff.staff_pic));


        staffMembersViewHolder.mStaffPhone.setText(staff.staff_phone);
        //unsentMessagesViewHolder.mSenderId.setText(msg.getMsg_id());
    }

    @Override
    public int getItemCount() {
        if(null == staffMembersListFiltered)
            return 0;
        return staffMembersListFiltered.size();
    }

    public void setMessageData(ArrayList<StaffMembersDAO> staffMember){
        staffMembers= staffMembersListFiltered = staffMember;
        notifyDataSetChanged();
    }

    public class StaffMembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //public final TextView mSenderId;

        public final TextView mStaffPhone;

        public final TextView mStaffNAme;

        public final LinearLayout staffLayout;

        public final CircleImageView  mImageView;



        public StaffMembersViewHolder(View itemView) {
            super(itemView);

            //mSenderId = (TextView) itemView.findViewById(R.id.rytxt_sendId);
            mStaffPhone= (TextView) itemView.findViewById(R.id.txtv_staff_phone);
            mStaffNAme = (TextView) itemView.findViewById(R.id.staff_name);
            mImageView = itemView.findViewById(R.id.image_view);
            staffLayout = itemView.findViewById(R.id.staff_div);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            handler.clickHandler(staffLayout.getTag().toString());
        }
    }

}


