package com.itcrusaders.staffaccesssystems.Admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisitorsAdapter extends RecyclerView.Adapter<VisitorsAdapter.VisitorMembersViewHolder> implements Filterable {


    private ArrayList<VisitorLoginRecords> visitorMembers,visitorMembersFiltered;
    final private VisitorMembersAdapterOnClickHandler handler;


    public interface VisitorMembersAdapterOnClickHandler{
        void clickHandler(String Params);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    visitorMembersFiltered=visitorMembers;
                }else {
                    ArrayList<VisitorLoginRecords> filteredlist = new ArrayList<>();
                    for(VisitorLoginRecords row : visitorMembers){
                        if(row.vname.toLowerCase().contains(charString.toLowerCase()) ||
                                row.vmobile.toLowerCase().contains(charString.toLowerCase()) ||
                                row.vpurpose.toLowerCase().contains(charString.toLowerCase()) ||
                                row.vChurch.toLowerCase().contains(charString.toLowerCase())){
                            filteredlist.add(row);
                        }
                    }
                    visitorMembersFiltered = filteredlist;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values=visitorMembersFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                visitorMembersFiltered = (ArrayList<VisitorLoginRecords>) results.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }


    public VisitorsAdapter(VisitorMembersAdapterOnClickHandler clickHandler){
        handler=clickHandler;
    }

    @Override
    public VisitorMembersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();


        int layoutIdforItem = R.layout.visitor_loader;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        //View view1 =  inflater.inflate(layoutIdforItem,viewGroup,shouldAttachToParentImmediately);

        return new VisitorMembersViewHolder(inflater.inflate(layoutIdforItem,viewGroup,shouldAttachToParentImmediately));
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

        VisitorMembersViewHolder viewHolder;
        public downloadImageFromNet(VisitorMembersViewHolder viewHolder){
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
    public void onBindViewHolder(VisitorMembersViewHolder visitorMembersViewHolder, int position) {

        VisitorLoginRecords visitor = visitorMembersFiltered.get(position);

        String apipath = ApiClass.getImageBaseUrl()+"rest/";

        visitorMembersViewHolder.mVisitorNAme.setText(visitor.vname);
        visitorMembersViewHolder.visitorLayout.setTag(visitor.vid);

        if(visitor.appoindate.equalsIgnoreCase("denied")){
            visitorMembersViewHolder.visitorLayout.setBackgroundColor(Color.GRAY);
        }else if(visitor.appoindate.equalsIgnoreCase("approved")){
            visitorMembersViewHolder.visitorLayout.setBackgroundResource(R.color.successcolor);
        }else{

        }
        visitorMembersViewHolder.mVisitorPhone.setText(visitor.vmobile);
        visitorMembersViewHolder.mVisitorPurpose.setText("Purpose Of Visit : "+visitor.vpurpose);
        visitorMembersViewHolder.mVisitorChurch.setText("From "+visitor.vChurch+"\n"+visitor.vgroup+" group");
        //visitorMembersViewHolder.mVisitorwho2see.setText("To see : "+ visitor.vwho2see);



        new downloadImageFromNet(visitorMembersViewHolder).execute(apipath+visitor.vimgsrc);


        //visitorMembersViewHolder.mImageView.setImageBitmap(getBitmapFromUrl(apipath+visitor.visitor_pic));

        //unsentMessagesViewHolder.mSenderId.setText(msg.getMsg_id());
    }

    @Override
    public int getItemCount() {
        if(null == visitorMembersFiltered)
            return 0;
        return visitorMembersFiltered.size();
    }

    public void setMessageData(ArrayList<VisitorLoginRecords> visitorMember){
        visitorMembersFiltered = visitorMembers= visitorMember;
        notifyDataSetChanged();
    }

    public class VisitorMembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //public final TextView mSenderId;

        public final TextView mVisitorPhone;

        public final TextView mVisitorNAme;
        public final TextView mVisitorChurch;
        public final TextView mVisitorPurpose;
        public final TextView mVisitorwho2see;

        public final LinearLayout visitorLayout;

        public final CircleImageView  mImageView;



        public VisitorMembersViewHolder(View itemView) {
            super(itemView);

            //mSenderId = (TextView) itemView.findViewById(R.id.rytxt_sendId);
            mVisitorPhone= (TextView) itemView.findViewById(R.id.visitor_mobile);
            mVisitorNAme = (TextView) itemView.findViewById(R.id.visitor_name);

            mVisitorPurpose= (TextView) itemView.findViewById(R.id.visitor_purpose_);
            mVisitorwho2see = (TextView) itemView.findViewById(R.id.visitor_2see);
            mVisitorChurch= (TextView) itemView.findViewById(R.id.visitor_church_gGroup);

            ///mVisitorNAme = (TextView) itemView.findViewById(R.id.visitor_name);
            mImageView = itemView.findViewById(R.id.image_view);
            visitorLayout = itemView.findViewById(R.id.visitor_div);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            handler.clickHandler(visitorLayout.getTag().toString());
        }
    }

}


